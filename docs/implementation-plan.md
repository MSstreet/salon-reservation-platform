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


  ---
문서 vs 현재 구현 대조

✅ 이미 해결된 항목 (문서가 outdated)

┌─────┬──────────────────────────┬─────────────────────────────────────────────────────────────────────────────┐
│  #  │        문서 지적         │                                  실제 현황                                  │
├─────┼──────────────────────────┼─────────────────────────────────────────────────────────────────────────────┤
│ 10  │ 예약 충돌 방지 설계 없음 │ Redis Distributed Lock + 슬롯 상태 검증 구현 완료                           │
├─────┼──────────────────────────┼─────────────────────────────────────────────────────────────────────────────┤
│ 2   │ 결제 상태 관리 없음      │ POST /api/payments/deposits/{id} 존재, 취소 시 환불 내부 처리됨 (부분 완료) │
└─────┴──────────────────────────┴─────────────────────────────────────────────────────────────────────────────┘

  ---
❌ 여전히 유효한 Gap

🔴 1순위 — 핵심 플로우

┌─────┬────────────────────────┬─────────────────────────────────────────────────────────────────┐
│  #  │          내용          │                              비고                               │
├─────┼────────────────────────┼─────────────────────────────────────────────────────────────────┤
│ 1   │ 예약 완료 API 없음     │ PATCH /admin/stores/{id}/reservations/{id}/complete 미구현      │
├─────┼────────────────────────┼─────────────────────────────────────────────────────────────────┤
│ 1   │ No-show 수동 처리 없음 │ 자동 스케줄러는 있지만 어드민 수동 처리 API 없음                │
├─────┼────────────────────────┼─────────────────────────────────────────────────────────────────┤
│ 8   │ 예약 상세 조회 없음    │ GET /api/stores/{storeId}/reservations/{reservationId} 미구현   │
├─────┼────────────────────────┼─────────────────────────────────────────────────────────────────┤
│ 5   │ 사용자 예약 조회 없음  │ GET /api/stores/{storeId}/reservations?customerPhone=... 미구현 │
└─────┴────────────────────────┴─────────────────────────────────────────────────────────────────┘

▎ 참고: 문서의 approve/reject는 현재 설계와 맞지 않음. 현재는 예약금 결제 = 자동 CONFIRMED 구조라 approve 단계가 없음.

🟠 2순위 — UX 완성도

┌─────┬───────────────────────────────┬───────────────────────────────────────────────────────────────┐
│  #  │             내용              │                             비고                              │
├─────┼───────────────────────────────┼───────────────────────────────────────────────────────────────┤
│ 3   │ 타임슬롯 menuId 파라미터 없음 │ 시술 소요시간 기반 슬롯 필터링 불가                           │
├─────┼───────────────────────────────┼───────────────────────────────────────────────────────────────┤
│ 7   │ 스케줄 조회 없음              │ POST만 있고 GET 없음 (GET /admin/stores/{id}/staff-schedules) │
├─────┼───────────────────────────────┼───────────────────────────────────────────────────────────────┤
│ 2   │ 결제 타임아웃 스케줄러 없음   │ PENDING 10분 초과 → 자동 CANCELED 미구현                      │
└─────┴───────────────────────────────┴───────────────────────────────────────────────────────────────┘

🟡 3순위 — 추가 기능

┌─────┬──────────────────────┬─────────────────────────────────────────┐
│  #  │         내용         │                  비고                   │
├─────┼──────────────────────┼─────────────────────────────────────────┤
│ 4   │ 디자이너별 메뉴 없음 │ StaffMenu 엔티티 자체가 없음, 설계 필요 │
├─────┼──────────────────────┼─────────────────────────────────────────┤
│ 6   │ 리뷰 시스템 없음     │ Phase 미착수                            │
├─────┼──────────────────────┼─────────────────────────────────────────┤
│ 9   │ 인증 고도화          │ refresh token 없음, Mock 로그인만       │
└─────┴──────────────────────┴─────────────────────────────────────────┘

  ---
권장 진행 순서

1. 예약 완료 API
   PATCH /admin/stores/{storeId}/reservations/{reservationId}/complete
   → Reservation CONFIRMED → COMPLETED + ReservationHistory 기록

2. No-show 수동 API
   PATCH /admin/stores/{storeId}/reservations/{reservationId}/no-show
   → Reservation → NO_SHOW + Deposit → FORFEITED

3. 예약 상세 조회
   GET /api/stores/{storeId}/reservations/{reservationId}
   → 상세 화면 구성 가능

4. 결제 타임아웃 스케줄러
   → PENDING 10분 초과 → Reservation CANCELED + Slot OPEN

5. 스케줄 조회 API
   GET /admin/stores/{storeId}/staff-schedules?date=&staffId=

6. 타임슬롯 menuId 파라미터 추가
   → menuId → duration_min 조회 → 연속 슬롯 필터링

7. 디자이너별 메뉴 매핑 (설계부터 필요)
8. Kafka 이벤트 발행 (Phase 9)

결론: 문서의 내용은 대체로 유효합니다. 단, #10 충돌 방지는 이미 해결됐고, #1의 approve/reject는 현재 Deposit 기반 설계와 맞지 않아 complete + no-show로 대체하는 게 맞습니다.
1~4번부터 순서대로 진행하면 핵심 예약 플로우가 완성됩니다.

✻ Sautéed for 1m 12s

❯ 진행 전에 진행 순서에 대한 문서를 만들어줘

⏺ Write(.prompts/next-steps.md)
⎿  Wrote 180 lines to .prompts/next-steps.md
# 다음 구현 순서

     salon_project_complement.md 분석 기반 진행 계획

     ---

     ## 현황 요약

     - 구현 완료: 26개 엔드포인트 (API 12 / Admin 14)
     - Redis Lock + 슬롯 상태 검증으로 동시성 제어 완료
     … +170 lines (ctrl+o to expand)

⏺ .prompts/next-steps.md 생성 완료.

구성 요약:
- 1단계: 예약 완료 + No-show 수동 + 예약 상세 조회 (핵심 플로우 마무리)
- 2단계: 결제 타임아웃 스케줄러
- 3단계: 스케줄 조회 GET + 타임슬롯 menuId 파라미터
- 4단계: 디자이너별 메뉴 매핑 (엔티티 설계 포함)
- 5단계: Kafka 이벤트 발행
- 6단계: 인증 고도화
- 보류: 리뷰 시스템, OpenSearch, 사용자 예약 조회 (인증 연동 후)





