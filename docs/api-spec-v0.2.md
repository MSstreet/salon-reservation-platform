바뀐 핵심 3가지:

```text
1 예약금(Deposit) 개념 추가
2 결제 API 추가
3 예약 확정 조건 변경 (결제 완료 → CONFIRMED)
```

API 구조가 이렇게 바뀐다.

기존

```text
예약 생성
예약 취소
예약 조회
```

지금

```text
예약 생성
예약금 결제
예약 확정
예약 취소
노쇼
예약 조회
```

그래서 **새로운 api-spec.md 전체 구조**를 다시 만들어준다.

---

# api-spec.md

Salon Reservation Platform - API Specification

---

# 1. API Base

```text
Base URL
/api/v1
```

Content Type

```text
application/json
```

Authentication

```text
JWT (Bearer Token)
```

---

# 2. Health API

서비스 상태 확인

### Request

```http
GET /api/v1/health
```

### Response

```json
{
  "status": "UP",
  "service": "salon-reservation-platform"
}
```

---

# 3. Store API

매장 조회

### Request

```http
GET /api/v1/stores
```

Response

```json
[
  {
    "storeId": 1,
    "name": "Gangnam Salon"
  }
]
```

---

# 4. Staff API

디자이너 조회

### Request

```http
GET /api/v1/stores/{storeId}/staff
```

Response

```json
[
  {
    "staffId": 10,
    "name": "Designer A"
  }
]
```

---

# 5. Menu API

시술 메뉴 조회

### Request

```http
GET /api/v1/stores/{storeId}/menus
```

Response

```json
[
  {
    "menuId": 1,
    "name": "Hair Cut",
    "durationMin": 30,
    "price": 60000
  }
]
```

---

# 6. Slot API

예약 가능 시간 조회

### Request

```http
GET /api/v1/stores/{storeId}/slots
```

Query

```text
date
staffId
menuId
```

Example

```http
GET /api/v1/stores/1/slots?date=2026-02-15&staffId=5&menuId=2
```

Response

```json
[
  {
    "slotId": 100,
    "startAt": "2026-02-15T10:00:00",
    "endAt": "2026-02-15T10:30:00",
    "status": "OPEN"
  }
]
```

---

# 7. Reservation Create API

예약 생성

### Request

```http
POST /api/v1/reservations
```

Headers

```text
Idempotency-Key: uuid
```

Body

```json
{
  "storeId": 1,
  "staffId": 5,
  "menuId": 2,
  "slotId": 100,
  "customerName": "홍길동",
  "customerPhone": "01012345678"
}
```

### Response

```json
{
  "reservationId": 9001,
  "status": "REQUESTED",
  "depositId": 5001,
  "depositAmount": 12000
}
```

설명

```text
예약 생성 후 예약금 결제 필요
```

---

# 8. Deposit 조회 API

예약금 정보 조회

### Request

```http
GET /api/v1/deposits/{depositId}
```

Response

```json
{
  "depositId": 5001,
  "reservationId": 9001,
  "amount": 12000,
  "status": "PENDING"
}
```

---

# 9. Deposit Payment API

예약금 결제 요청

### Request

```http
POST /api/v1/payments/deposits/{depositId}
```

Body

```json
{
  "paymentMethod": "CARD"
}
```

Response

```json
{
  "paymentId": "pay_1234",
  "paymentUrl": "https://payment-gateway"
}
```

---

# 10. Payment Webhook API

결제 완료 콜백

### Request

```http
POST /api/v1/payments/webhook
```

Example

```json
{
  "transactionId": "tx123",
  "depositId": 5001,
  "status": "SUCCESS",
  "amount": 12000
}
```

처리

```text
Deposit = PAID
Reservation = CONFIRMED
```

---

# 11. Reservation Cancel API

예약 취소

### Request

```http
POST /api/v1/reservations/{reservationId}/cancel
```

Response

```json
{
  "reservationId": 9001,
  "status": "CANCELED"
}
```

예약금 처리

```text
환불 가능 → REFUNDED
환불 불가 → FORFEITED
```

---

# 12. Reservation 조회

고객 예약 조회

### Request

```http
GET /api/v1/customers/{customerId}/reservations
```

Response

```json
[
  {
    "reservationId": 9001,
    "status": "CONFIRMED",
    "startAt": "2026-02-15T10:00:00",
    "staffName": "Designer A"
  }
]
```

---

# 13. Store Reservation 조회

매장 예약 조회

### Request

```http
GET /api/v1/stores/{storeId}/reservations
```

Query

```text
date
status
staffId
```

---

# 14. Reservation Detail API

예약 상세 조회

### Request

```http
GET /api/v1/reservations/{reservationId}
```

Response

```json
{
  "reservationId": 9001,
  "status": "CONFIRMED",
  "customerName": "홍길동",
  "menuName": "Hair Cut",
  "startAt": "2026-02-15T10:00:00"
}
```

---

# 15. Error Codes

| Code                            | Description |
| ------------------------------- | ----------- |
| SLOT_NOT_FOUND                  | 슬롯 없음       |
| RESERVATION_SLOT_ALREADY_BOOKED | 슬롯 이미 예약    |
| RESERVATION_NOT_FOUND           | 예약 없음       |
| DEPOSIT_NOT_FOUND               | 예약금 없음      |
| PAYMENT_FAILED                  | 결제 실패       |
| RESERVATION_CANNOT_CANCEL       | 취소 불가       |

---

# 16. Reservation State

```text
REQUESTED
CONFIRMED
COMPLETED
CANCELED
NO_SHOW
```

---

# 17. Deposit State

```text
PENDING
PAID
REFUNDED
FORFEITED
```

---

# 18. Idempotency

예약 생성 API는 멱등성을 지원한다.

Header

```text
Idempotency-Key
```

중복 요청 시

```text
기존 응답 반환
```

---

# 19. Rate Limit

추천

```text
예약 생성
5 requests / minute
```

---

# 20. API Summary

핵심 API

```text
GET /health
GET /stores
GET /stores/{storeId}/staff
GET /stores/{storeId}/menus
GET /stores/{storeId}/slots

POST /reservations
POST /payments/deposits/{depositId}
POST /payments/webhook
POST /reservations/{reservationId}/cancel

GET /customers/{customerId}/reservations
GET /stores/{storeId}/reservations
GET /reservations/{reservationId}
```