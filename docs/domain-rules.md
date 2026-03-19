정책이 이렇게 바뀐다.

기존

```text
취소 시 패널티 계산
(20%, 30%, 50%)
```

변경

```text
예약 시 예약금 결제
노쇼 → 예약금 환불 불가
취소 → 정책에 따라 환불 가능
```

그래서 **Penalty / PolicyVersion 중심 설계 → Deposit 중심 설계**로 바꾸면 된다.

아래는 **수정된 domain-rules.md 핵심 부분**이다.

---

# domain-rules.md (Deposit 기반 정책)

Salon Reservation Platform - Domain Rules

---

# 1. Reservation Lifecycle

예약 상태

```text
REQUESTED
CONFIRMED
COMPLETED
CANCELED
NO_SHOW
```

설명

| 상태        | 의미        |
| --------- | --------- |
| REQUESTED | 고객 예약 생성  |
| CONFIRMED | 예약금 결제 완료 |
| COMPLETED | 시술 완료     |
| CANCELED  | 예약 취소     |
| NO_SHOW   | 고객 미방문    |

핵심 변화

```text
예약금 결제 완료 → CONFIRMED
```

---

# 2. Reservation State Transition

허용 상태 전이

```text
REQUESTED → CONFIRMED
REQUESTED → CANCELED

CONFIRMED → COMPLETED
CONFIRMED → CANCELED
CONFIRMED → NO_SHOW
```

종료 상태

```text
COMPLETED
CANCELED
NO_SHOW
```

---

# 3. Reservation Deposit Rule

예약 생성 시 고객은 **예약금(Deposit)**을 결제해야 한다.

예약금 목적

```text
노쇼 방지
예약 확정
```

예약금 기준

```text
depositAmount = menu.price * depositRate
```

예

```text
menu price = 60000
depositRate = 20%

depositAmount = 12000
```

예약금은 다음 상태를 가진다.

```text
PENDING
PAID
REFUNDED
FORFEITED
```

설명

| 상태        | 의미     |
| --------- | ------ |
| PENDING   | 결제 대기  |
| PAID      | 결제 완료  |
| REFUNDED  | 환불 완료  |
| FORFEITED | 노쇼로 몰수 |

---

# 4. Deposit Entity

예약금 정보는 `Deposit` 엔티티로 관리한다.

```text
Deposit
```

필드

```text
deposit_id
reservation_id
amount
currency
status
paid_at
refunded_at
forfeited_at
```

Reservation과 관계

```text
Reservation 1 : 1 Deposit
```

---

# 5. Reservation Creation Rule

예약 생성 절차

```text
1 슬롯 검증
2 Reservation 생성 (status = REQUESTED)
3 Deposit 생성 (status = PENDING)
4 결제 요청 생성
```

예

```text
Reservation = REQUESTED
Deposit = PENDING
```

---

# 6. Reservation Confirmation Rule

예약금 결제가 완료되면 예약은 확정된다.

절차

```text
Deposit status = PAID
Reservation status = CONFIRMED
```

즉

```text
결제 완료 = 예약 확정
```

---

# 7. Cancellation Rule

취소 시 예약금 처리 규칙

취소 시점 기준

```text
cancelTime
startAt
```

환불 정책 예

```text
24시간 이전 취소 → 전액 환불
24시간 미만 취소 → 환불 불가
```

환불 가능

```text
Deposit status → REFUNDED
```

환불 불가

```text
Deposit status → FORFEITED
```

---

# 8. No Show Rule

노쇼 기준

```text
현재 시간 > reservation.startAt + gracePeriod
```

예

```text
gracePeriod = 10분
```

조건

```text
status = CONFIRMED
AND customer 미방문
```

노쇼 처리

```text
Reservation status = NO_SHOW
Deposit status = FORFEITED
```

즉

```text
예약금 환불 불가
```

---

# 9. Reservation Completion Rule

시술 완료 시

```text
Reservation status = COMPLETED
```

예약금 처리

예약금은 **서비스 비용 일부로 차감**

예

```text
menu price = 60000
deposit = 12000

결제 잔액
= 48000
```

---

# 10. Slot Release Rule

취소 시 슬롯 복구

```text
BOOKED → OPEN
```

적용 대상

```text
Reservation status = CANCELED
```

---

# 11. Audit Event

기록 이벤트

```text
RESERVATION_CREATED
DEPOSIT_PAID
RESERVATION_CONFIRMED
RESERVATION_CANCELED
DEPOSIT_REFUNDED
RESERVATION_NO_SHOW
DEPOSIT_FORFEITED
RESERVATION_COMPLETED
```

---

# 12. Concurrency Rule

예약 생성 시

```text
Redis Lock
```

사용

락 키

```text
reservation:staff:{staffId}:start:{startAt}
```

목적

```text
동시 예약 방지
```

---

# 13. Idempotency Rule

예약 생성 / 취소 / 결제

모두

```text
Idempotency-Key
```

지원

기준

```text
(userId, endpoint, idempotencyKey)
```

---

# 14. Error Codes

| 상황       | 에러                              |
| -------- | ------------------------------- |
| slot 없음  | SLOT_NOT_FOUND                  |
| slot 사용중 | RESERVATION_SLOT_ALREADY_BOOKED |
| 예약 없음    | RESERVATION_NOT_FOUND           |
| 취소 불가    | RESERVATION_CANNOT_CANCEL       |
| 상태 전이 오류 | RESERVATION_INVALID_STATUS      |

---

# 변경된 핵심 구조

기존

```text
Reservation
Penalty
PolicyVersion
```

변경

```text
Reservation
Deposit
```

즉

```text
패널티 계산 시스템 ❌
예약금 시스템 ⭕
```

---

# ERD 변경 포인트 (중요)

ERD도 이렇게 바꾸는 게 좋다.

추가

```text
Deposit
```

삭제

```text
Penalty
PolicyVersion
```

---

# 추천 Deposit 테이블

```sql
deposit
--------
deposit_id
reservation_id
amount
status
paid_at
refunded_at
forfeited_at
created_at
```

---

# 현실 서비스 장점

이 방식이 좋은 이유

```text
노쇼 방지
정책 단순
분쟁 감소
결제 시스템 연결 쉬움
```

그래서

```text
미용실
병원
레스토랑
```

예약 시스템은 거의 이 구조다.

---

# 다음으로 해야 할 것

이 정책을 쓰면 **한 가지 큰 장점**이 생긴다.

이제 시스템이 이렇게 바뀐다.

```text
예약 시스템
+
결제 시스템
```

그래서 다음 문서 하나만 더 만들면 된다.

```text
payment-flow.md
```

내용

```text
예약 → 결제 → 예약 확정
```

---
