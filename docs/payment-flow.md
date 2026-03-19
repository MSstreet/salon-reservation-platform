---

# payment-flow.md

Salon Reservation Platform - Payment Flow

---

# 1. 목적

예약금(Deposit) 결제 과정을 정의한다.

이 문서는 다음을 보장한다.

* 예약금 결제 기반 예약 확정
* 결제 실패 시 예약 상태 유지
* 취소 시 환불 처리
* 노쇼 시 예약금 몰수
* 결제 이벤트 기반 확장

---

# 2. 결제 시스템 구성

결제 시스템은 다음 구성 요소로 이루어진다.

```text
Client
  │
  ▼
Payment API
  │
  ▼
Payment Gateway
(토스 / Stripe / 카카오페이)
  │
  ▼
PaymentService
  │
  ▼
DepositService
```

---

# 3. 결제 대상

결제 대상은 **예약금(Deposit)** 이다.

Deposit 계산

```text
depositAmount = menu.price * depositRate
```

예

```text
menu price = 60000
depositRate = 20%

depositAmount = 12000
```

Deposit 상태

```text
PENDING
PAID
REFUNDED
FORFEITED
```

---

# 4. 예약 생성 시 결제 흐름

예약 생성 후 예약금 결제가 필요하다.

전체 흐름

```text
Client
  │
Create Reservation
  │
  ▼
ReservationService
  │
  ▼
Reservation = REQUESTED
Deposit = PENDING
  │
  ▼
Return Payment Request
```

---

# 5. 결제 요청 API

```http
POST /payments/deposits/{depositId}
```

Request

```json
{
  "paymentMethod": "CARD"
}
```

Response

```json
{
  "paymentId": "pay_1234",
  "amount": 12000,
  "currency": "KRW",
  "paymentUrl": "https://payment-gateway-url"
}
```

---

# 6. 결제 승인 흐름

고객 결제 승인 후 Payment Gateway가 서버에 콜백을 보낸다.

```text
Client Payment
   │
   ▼
Payment Gateway
   │
Webhook
   │
   ▼
PaymentService
   │
   ▼
DepositService
```

---

# 7. 결제 성공 처리

결제 성공 시 다음 처리를 수행한다.

```text
1 Deposit status = PAID
2 Reservation status = CONFIRMED
3 ReservationEvent 저장
4 이벤트 발행
```

이벤트

```text
DEPOSIT_PAID
RESERVATION_CONFIRMED
```

---

# 8. 결제 실패 처리

결제 실패 시

```text
Deposit = PENDING
Reservation = REQUESTED
```

고객은 다시 결제 시도 가능

예

```text
Retry Payment
```

예약은 아직 확정되지 않는다.

---

# 9. 결제 타임아웃

결제 제한 시간 예

```text
10분
```

시간 초과 시

```text
Reservation = CANCELED
Deposit = PENDING (expired)
Slot = OPEN
```

이벤트

```text
RESERVATION_PAYMENT_EXPIRED
```

---

# 10. 취소 시 환불

예약 취소 시 환불 여부 결정

기준

```text
cancelTime
startAt
```

예

```text
24시간 이전 취소 → 환불
24시간 미만 취소 → 환불 불가
```

환불 가능

```text
Deposit = REFUNDED
```

환불 불가

```text
Deposit = FORFEITED
```

---

# 11. 환불 처리 흐름

```text
Cancel Reservation
      │
      ▼
ReservationService
      │
      ▼
DepositService
      │
      ▼
Payment Gateway Refund
      │
      ▼
Deposit status = REFUNDED
```

이벤트

```text
DEPOSIT_REFUNDED
```

---

# 12. 노쇼 처리

노쇼 판정 기준

```text
currentTime > reservation.startAt + gracePeriod
```

예

```text
gracePeriod = 10분
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

이벤트

```text
RESERVATION_NO_SHOW
DEPOSIT_FORFEITED
```

---

# 13. Payment Entity (Optional)

결제 로그 테이블

```sql
payment
-------
payment_id PK
deposit_id FK
gateway
transaction_id
amount
currency
status
paid_at
created_at
```

status

```text
INIT
SUCCESS
FAILED
REFUNDED
```

---

# 14. Idempotency 처리

결제 API는 멱등성을 지원해야 한다.

Header

```text
Idempotency-Key
```

기준

```text
(userId, endpoint, idempotencyKey)
```

동일 요청 재시도 시

```text
기존 결제 결과 반환
```

---

# 15. 이벤트 발행

결제 관련 이벤트

```text
DEPOSIT_CREATED
DEPOSIT_PAID
DEPOSIT_REFUNDED
DEPOSIT_FORFEITED
```

Reservation 이벤트

```text
RESERVATION_CONFIRMED
RESERVATION_CANCELED
RESERVATION_NO_SHOW
```

---

# 16. 이벤트 흐름

결제 성공

```text
DepositPaidEvent
        │
        ▼
ReservationConfirmedEvent
        │
        ▼
Notification
Search Sync
Analytics
```

---

# 17. 실패 시나리오

### 결제 성공 but DB 실패

해결

```text
Outbox Pattern
```

---

### Webhook 중복 호출

해결

```text
payment.transaction_id unique
```

---

### 결제 중복 요청

해결

```text
Idempotency-Key
```

---

# 18. 모니터링

모니터링 항목

```text
결제 성공률
결제 실패율
환불 요청
노쇼 발생률
```

---

# 19. 보안

보안 규칙

```text
Webhook signature 검증
Payment gateway token validation
```

---

# 20. 전체 흐름 요약

예약 생성

```text
Reservation REQUESTED
Deposit PENDING
```

결제 완료

```text
Deposit PAID
Reservation CONFIRMED
```

취소

```text
Reservation CANCELED
Deposit REFUNDED
```

노쇼

```text
Reservation NO_SHOW
Deposit FORFEITED
```

---

# 최종 시스템 흐름

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

# 이 구조의 장점

이 구조는 실제 서비스에서 많이 사용하는 모델이다.

예

```text
미용실 예약
병원 예약
레스토랑 예약
호텔 예약
```

대부분

```text
예약금 기반 예약 시스템
```

사용한다.

---
