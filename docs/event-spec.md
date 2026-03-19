# event-spec.md (Deposit 기반 수정 버전)

Salon Reservation Platform - Event Specification

---

# 1. Event Architecture

예약 시스템은 **Event Driven Architecture** 기반으로 동작한다.

목표

* 결제 시스템 분리
* 알림 시스템 분리
* 검색 시스템 분리
* 확장성 확보

구조

```text
Reservation Service
        │
        │ publish
        ▼
       Kafka
        │
        ├── Payment Service
        ├── Notification Service
        ├── Search Sync Service
        └── Analytics Service
```

---

# 2. Kafka Topics

| Topic               | 설명         |
| ------------------- | ---------- |
| reservation-events  | 예약 관련 이벤트  |
| deposit-events      | 예약금 이벤트    |
| notification-events | 알림 이벤트     |
| search-sync-events  | 검색 인덱스 동기화 |

---

# 3. reservation-events

예약 상태 변경 이벤트

### 이벤트 타입

```text
RESERVATION_CREATED
RESERVATION_CONFIRMED
RESERVATION_CANCELED
RESERVATION_COMPLETED
RESERVATION_NO_SHOW
```

---

# 4. Reservation Event Schema

공통 이벤트 구조

```json
{
  "eventId": "uuid",
  "eventType": "RESERVATION_CREATED",
  "occurredAt": "2026-02-10T12:00:00Z",
  "storeId": 1,
  "reservationId": 9001,
  "payload": {}
}
```

---

## Example — Reservation Created

```json
{
  "eventId": "uuid",
  "eventType": "RESERVATION_CREATED",
  "occurredAt": "2026-02-10T12:00:00Z",
  "storeId": 1,
  "reservationId": 9001,
  "payload": {
    "staffId": 55,
    "menuId": 10,
    "startAt": "2026-02-15T10:00:00",
    "endAt": "2026-02-15T11:00:00",
    "status": "REQUESTED"
  }
}
```

---

# 5. deposit-events

예약금 관련 이벤트

### 이벤트 타입

```text
DEPOSIT_CREATED
DEPOSIT_PAID
DEPOSIT_REFUNDED
DEPOSIT_FORFEITED
```

설명

| 이벤트               | 의미             |
| ----------------- | -------------- |
| DEPOSIT_CREATED   | 예약 생성 시 예약금 생성 |
| DEPOSIT_PAID      | 예약금 결제 완료      |
| DEPOSIT_REFUNDED  | 취소로 환불         |
| DEPOSIT_FORFEITED | 노쇼로 몰수         |

---

# 6. Deposit Event Schema

```json
{
  "eventId": "uuid",
  "eventType": "DEPOSIT_PAID",
  "occurredAt": "2026-02-10T12:01:00Z",
  "storeId": 1,
  "reservationId": 9001,
  "depositId": 5001,
  "payload": {
    "amount": 12000,
    "currency": "KRW",
    "status": "PAID"
  }
}
```

---

# 7. Reservation Confirmation Flow

예약금 결제 완료 시 예약 확정

이벤트 흐름

```text
Customer
   │
Create Reservation
   │
ReservationCreatedEvent
   │
DepositCreatedEvent
   │
Customer Payment
   │
DepositPaidEvent
   │
ReservationConfirmedEvent
```

즉

```text
결제 완료 = 예약 확정
```

---

# 8. Cancellation Flow

예약 취소 이벤트 흐름

```text
Cancel Reservation
      │
      ▼
ReservationCanceledEvent
      │
      ▼
DepositRefundedEvent
      │
      ▼
Search Sync Event
      │
      ▼
Notification Event
```

---

# 9. No Show Flow

노쇼 이벤트 흐름

```text
Scheduler
   │
   ▼
Detect No Show
   │
   ▼
ReservationNoShowEvent
   │
   ▼
DepositForfeitedEvent
   │
   ▼
NotificationEvent
```

즉

```text
노쇼 = 예약금 몰수
```

---

# 10. Search Sync Event

검색 인덱스 동기화 이벤트

```json
{
  "eventId": "uuid",
  "eventType": "RESERVATION_INDEX_SYNC",
  "occurredAt": "2026-02-10T12:00:00Z",
  "storeId": 1,
  "reservationId": 9001,
  "payload": {
    "status": "CONFIRMED",
    "startAt": "2026-02-15T10:00:00",
    "staffName": "Designer B",
    "menuName": "Cut"
  }
}
```

---

# 11. Partition Strategy

Kafka Partition Key

```text
reservationId
```

이유

```text
같은 예약 이벤트 순서 보장
```

예

```text
RESERVATION_CREATED
→ DEPOSIT_PAID
→ RESERVATION_CONFIRMED
→ RESERVATION_COMPLETED
```

---

# 12. Event Idempotency

이벤트 중복 방지

Consumer는

```text
eventId
```

기준으로 중복 체크

테이블

```sql
processed_event
----------------
event_id
processed_at
```

이미 처리된 이벤트면

```text
skip
```

---

# 13. Producer Rule

이벤트 발행 규칙

```text
DB commit 이후 publish
```

권장

```text
Outbox Pattern
```

---

# 14. Consumer Responsibilities

### Payment Consumer

* 예약금 결제 처리

---

### Notification Consumer

* 예약 생성 알림
* 예약 확정 알림
* 취소 알림
* 노쇼 알림

---

### Search Sync Consumer

* OpenSearch index 업데이트

---

# 15. Event Retention

권장

```text
reservation-events → 7일
deposit-events → 30일
notification-events → 3일
```

---

# 16. Dead Letter Queue

실패 이벤트

```text
reservation-events-dlq
deposit-events-dlq
```

사용 목적

* 장애 분석
* 수동 재처리

---

# 17. Monitoring

모니터링

```text
consumer lag
event throughput
error rate
DLQ size
```

도구

```text
Prometheus
Grafana
Kafka exporter
```

---

# 핵심 이벤트 흐름 (가장 중요)

```text
예약 생성
↓
RESERVATION_CREATED

예약금 생성
↓
DEPOSIT_CREATED

예약금 결제
↓
DEPOSIT_PAID

예약 확정
↓
RESERVATION_CONFIRMED
```

---

# 이 구조의 장점

이 구조는 실제 서비스 구조와 동일하다.

```text
예약 시스템
+
결제 시스템
+
이벤트 시스템
```

특히

```text
미용실
병원
호텔
레스토랑
```

예약 플랫폼은 대부분

```text
예약금 방식
```

쓴다.

---

# 지금 문서 기준 다음 작업

지금 문서 기준 **개발 순서**

```text
1 Deposit entity
2 ReservationService
3 DepositService
4 Payment integration
5 Reservation cancel
6 No show scheduler
7 Kafka event publish
8 Search sync
```

---

## 진짜 중요한 이야기 하나

지금 네 프로젝트는 사실

```text
예약 시스템
```

이 아니라

```text
예약 + 결제 플랫폼
```

이다.

그래서 다음으로 만들면 **설계 완성도가 확 올라가는 문서**가 있다.

```text
payment-flow.md
```

내용

```text
예약 생성
→ 예약금 생성
→ 결제 요청
→ 결제 성공
→ 예약 확정
```

