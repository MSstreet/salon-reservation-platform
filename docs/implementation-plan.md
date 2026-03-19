좋다.
지금까지 만든 문서들을 기준으로 **현실적인 개발 순서(implementation roadmap)**를 **실제로 개발하는 순서 기준으로 다시 짜줄게.**

핵심 기준은 이것이다.

```text
1️⃣ 먼저 예약 핵심 로직
2️⃣ 그 다음 결제
3️⃣ 그 다음 이벤트
4️⃣ 그 다음 검색/부가기능
```

즉 **핵심 → 확장** 순서다.

지금 문서들 기준으로 **가장 효율적인 개발 순서**는 아래다.

---

# Salon Reservation Platform

# Implementation Roadmap

---

# Phase 1 — 프로젝트 기반 구축

목표
개발 가능한 기본 환경 만들기

### 작업

1️⃣ Spring Boot 프로젝트 생성

```text
Java 17
Spring Boot 3
Gradle
```

2️⃣ 기본 의존성

```text
Spring Web
Spring Data JPA
Validation
Redis
Kafka
OpenSearch
```

3️⃣ 공통 모듈

```text
GlobalExceptionHandler
ErrorResponse
BaseEntity
```

4️⃣ Health API

```http
GET /api/health
```

---

# Phase 2 — Core Domain Entity

목표
ERD 기반 Entity 먼저 작성

### 구현

Entity

```text
Store
Staff
ServiceMenu
ReservationCustomer
StaffSchedule
TimeSlot
Reservation
Deposit
ReservationEvent
```

Repository

```text
JpaRepository
```

---

# Phase 3 — Slot System

목표
예약 가능한 시간 계산

### 구현 서비스

```text
SlotService
```

기능

```text
슬롯 생성
슬롯 조회
연속 슬롯 계산
슬롯 상태 검증
```

API

```http
GET /stores/{storeId}/slots
```

예

```text
date
staffId
menuId
```

---

# Phase 4 — Reservation Core (가장 중요)

여기가 **이 프로젝트의 핵심이다.**

목표

```text
예약 생성
슬롯 점유
동시성 제어
```

구현

### Redis Lock

```text
reservation:staff:{staffId}:start:{startAt}
```

TTL

```text
3~5 seconds
```

---

### ReservationService

핵심 로직

```text
createReservation()
```

순서

```text
1 idempotency check
2 slot 조회
3 requiredSlots 계산
4 redis lock
5 slot 검증
6 reservation 생성
7 deposit 생성
8 slot BOOKED
9 commit
10 이벤트 발행
```

상태

```text
Reservation = REQUESTED
Deposit = PENDING
```

---

# Phase 5 — Payment System

목표

예약금 결제 기반 예약 확정

구현 서비스

```text
PaymentService
DepositService
```

---

### Payment API

```http
POST /payments/deposits/{depositId}
```

---

### Payment Webhook

결제 성공 시

```text
Deposit = PAID
Reservation = CONFIRMED
```

---

# Phase 6 — Cancellation System

목표

예약 취소 + 환불

서비스

```text
ReservationCancelService
```

로직

```text
Reservation = CANCELED
Deposit = REFUNDED or FORFEITED
Slot = OPEN
```

---

# Phase 7 — No Show 처리

목표

노쇼 자동 판정

방법

```text
Scheduler
```

조건

```text
currentTime > startAt + gracePeriod
```

처리

```text
Reservation = NO_SHOW
Deposit = FORFEITED
```

---

# Phase 8 — Reservation Query

목표

예약 조회 기능

API

```http
GET /customers/{id}/reservations
GET /stores/{id}/reservations
```

필터

```text
status
date
staff
```

---

# Phase 9 — Event System

목표

이벤트 기반 확장

Kafka Producer

```text
ReservationCreatedEvent
DepositCreatedEvent
DepositPaidEvent
ReservationConfirmedEvent
ReservationCanceledEvent
ReservationNoShowEvent
```

---

Kafka Consumer

```text
NotificationConsumer
SearchSyncConsumer
AnalyticsConsumer
```

---

# Phase 10 — Search System

목표

OpenSearch 기반 예약 검색

Index

```text
reservation-index
```

필드

```text
store
staff
menu
status
startAt
customerName
```

---

# Phase 11 — Test

### 동시성 테스트

예

```text
100 concurrent reservation requests
```

결과

```text
1 success
99 fail
```

---

### 취소 테스트

```text
cancel before 24h
cancel after 24h
```

---

### 노쇼 테스트

```text
scheduler run
```

---

# Phase 12 — Deploy

배포

```text
Docker
EC2
RDS
Redis
Kafka
OpenSearch
```

---

# 현실적인 개발 기간

혼자 기준

```text
Core Reservation
1 ~ 2 weeks
```

```text
Payment + Cancel
1 week
```

```text
Event + Search
1 week
```

총

```text
3 ~ 4 weeks
```

---

# 가장 중요한 기능 TOP 5

이 프로젝트 핵심

```text
1 ReservationService
2 SlotService
3 Redis Lock
4 Payment Webhook
5 Cancellation Logic
```

---

# 절대 먼저 만들지 말 것

이거 먼저 만들면 **시간 낭비**다.

```text
Kafka
OpenSearch
Notification
Analytics
```

이건 **나중**이다.

---

# 진짜 실무 기준 개발 순서

```text
1 Entity
2 SlotService
3 ReservationService
4 DepositService
5 PaymentService
6 CancelReservation
7 NoShow Scheduler
8 Event publish
9 Search
```

---

# 마지막 중요한 조언

지금 네 프로젝트에서 **제일 중요한 코드 하나**는 이것이다.

```text
ReservationService.createReservation()
```

여기서

```text
Redis Lock
Slot validation
Transaction
```

이거 잘 짜면

이 프로젝트는

```text
CRUD 프로젝트 ❌
실무형 백엔드 시스템 ⭕
```

된다.

---
