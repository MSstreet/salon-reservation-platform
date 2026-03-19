

---

# reservation-flow.md

Salon Reservation Platform - Reservation Flow

---

# 1. 목적

예약 생성부터 결제, 취소, 노쇼까지의 **전체 예약 흐름과 정합성 규칙**을 정의한다.

이 문서는 다음을 보장하기 위한 기준이다.

* 중복 예약 방지
* 슬롯 점유 보장
* 예약금 결제 기반 예약 확정
* 취소 시 슬롯 복구
* 노쇼 시 예약금 몰수
* 이벤트 기반 확장

---

# 2. 핵심 개념

예약 시스템은 **예약금 기반 예약 확정 모델**을 사용한다.

흐름

```text
예약 생성
↓
예약금 생성
↓
예약금 결제
↓
예약 확정
```

즉

```text
Deposit Paid = Reservation Confirmed
```

---

# 3. 예약 생성 전체 흐름

예약 생성은 다음 단계로 진행된다.

```text
1 Idempotency Key 검증
2 시작 슬롯 조회
3 메뉴 duration 조회
4 필요한 연속 슬롯 계산
5 Redis Lock 획득
6 슬롯 상태 검증
7 Reservation 생성
8 Deposit 생성
9 트랜잭션 commit
10 이벤트 발행
```

이 단계에서는 아직 예약이 **확정되지 않는다**.

상태

```text
Reservation = REQUESTED
Deposit = PENDING
```

---

# 4. 예약 생성 Sequence

```text
Client
   │
Create Reservation API
   │
   ▼
ReservationService
   │
   ├─ SlotService (슬롯 검증)
   ├─ CustomerService (고객 조회/생성)
   ├─ DepositService (예약금 생성)
   │
   ▼
DB Transaction
   │
   ├─ Reservation 저장
   ├─ Deposit 생성
   │
   ▼
Commit
   │
   ▼
RESERVATION_CREATED Event
DEPOSIT_CREATED Event
```

---

# 5. 슬롯 점유 규칙

슬롯은 30분 단위로 생성된다.

예

```text
10:00 - 10:30
10:30 - 11:00
11:00 - 11:30
```

메뉴 duration 기준으로 필요한 슬롯 계산

```text
requiredSlots = ceil(menu.durationMin / 30)
```

예

```text
Cut 30m → 1 slot
Perm 60m → 2 slots
Color 90m → 3 slots
```

예약 생성 시 조건

* 슬롯 존재
* 슬롯 상태 OPEN
* 연속 슬롯 존재
* 동일 staff

실패 시

```text
409 RESERVATION_SLOT_ALREADY_BOOKED
```

---

# 6. Redis Lock 전략

예약 생성 시 **동시 요청 충돌 방지**를 위해 Redis Lock 사용

Lock Key

```text
reservation:staff:{staffId}:start:{startAt}
```

Lock TTL

```text
3 ~ 5 seconds
```

Lock 획득 실패 시

```text
409 RESERVATION_SLOT_ALREADY_BOOKED
```

---

# 7. Reservation 생성

Reservation 저장

```text
storeId
staffId
menuId
customerId
startAt
endAt
status = REQUESTED
```

start/end 계산

```text
startAt = slot.startAt
endAt = startAt + menu.duration
```

---

# 8. Deposit 생성

Reservation 생성 후 Deposit 생성

```text
depositAmount = menu.price * depositRate
```

예

```text
menu price = 60000
depositRate = 20%

deposit = 12000
```

Deposit 상태

```text
PENDING
```

---

# 9. 결제 흐름

고객이 예약금을 결제하면 예약이 확정된다.

결제 흐름

```text
Client
   │
Deposit Payment API
   │
   ▼
Payment Gateway
   │
   ▼
DepositService
   │
   ├─ deposit status = PAID
   ├─ reservation status = CONFIRMED
   │
   ▼
Event publish
```

---

# 10. 결제 성공 이벤트

이벤트 흐름

```text
DEPOSIT_PAID
↓
RESERVATION_CONFIRMED
```

예약 상태 변경

```text
REQUESTED → CONFIRMED
```

---

# 11. 예약 취소 흐름

예약 취소 절차

```text
1 Reservation 조회
2 취소 가능 상태 검증
3 Reservation status = CANCELED
4 Deposit 환불 처리
5 Slot OPEN 복구
6 이벤트 발행
```

---

# 12. 취소 시 예약금 처리

취소 시점 기준 환불 여부 결정

예 정책

```text
24시간 이전 취소 → 환불
24시간 미만 취소 → 환불 불가
```

환불 가능

```text
Deposit → REFUNDED
```

환불 불가

```text
Deposit → FORFEITED
```

---

# 13. 슬롯 복구

예약 취소 시 슬롯 복구

```text
BOOKED → OPEN
```

적용 대상

```text
Reservation = CANCELED
```

---

# 14. 노쇼 처리

노쇼 판정 기준

```text
현재 시간 > startAt + gracePeriod
```

예

```text
gracePeriod = 10분
```

조건

```text
reservation.status = CONFIRMED
```

노쇼 처리

```text
reservation.status = NO_SHOW
deposit.status = FORFEITED
```

즉

```text
예약금 환불 불가
```

---

# 15. 시술 완료

시술 완료 시

```text
reservation.status = COMPLETED
```

예약금 처리

예약금은 **최종 결제 금액에서 차감**

예

```text
menu price = 60000
deposit = 12000

remaining payment = 48000
```

---

# 16. 이벤트 흐름

예약 생성

```text
RESERVATION_CREATED
DEPOSIT_CREATED
```

결제 완료

```text
DEPOSIT_PAID
RESERVATION_CONFIRMED
```

취소

```text
RESERVATION_CANCELED
DEPOSIT_REFUNDED
```

노쇼

```text
RESERVATION_NO_SHOW
DEPOSIT_FORFEITED
```

---

# 17. Idempotency

예약 생성 / 취소 / 결제

모두 멱등 처리

Header

```text
Idempotency-Key
```

기준

```text
(userId, endpoint, idempotencyKey)
```

---

# 18. DB 정합성 보호

Redis Lock 외에도 DB 보호 필요

방법

```text
조건부 Slot Update
```

예

```sql
UPDATE time_slot
SET status = 'BOOKED'
WHERE slot_id = ?
AND status = 'OPEN'
```

---

# 19. 실패 시나리오

### 슬롯 충돌

```text
409 RESERVATION_SLOT_ALREADY_BOOKED
```

### 결제 실패

```text
Deposit = PENDING
Reservation = REQUESTED
```

### 결제 지연

예약 확정 전까지 슬롯은 유지

---

# 20. 서비스 구조

추천 서비스

```text
ReservationService
SlotService
DepositService
PaymentService
CustomerService
EventService
LockService
IdempotencyService
```

---

# 21. 핵심 원칙

이 시스템의 핵심 규칙

1️⃣ **예약 생성과 슬롯 점유는 같은 트랜잭션**

2️⃣ **결제 완료가 예약 확정 조건**

3️⃣ **취소 시 슬롯 복구**

4️⃣ **노쇼 시 예약금 몰수**

5️⃣ **Redis Lock으로 동시 예약 방지**

---

# 최종 예약 흐름

```text
예약 생성
↓
Reservation REQUESTED
Deposit PENDING
↓
예약금 결제
↓
Deposit PAID
Reservation CONFIRMED
↓
시술 완료
↓
Reservation COMPLETED
```

---

# 취소 흐름

```text
예약 취소
↓
Reservation CANCELED
↓
Deposit REFUNDED
↓
Slot OPEN
```

---

# 노쇼 흐름

```text
노쇼 발생
↓
Reservation NO_SHOW
↓
Deposit FORFEITED
```

---
