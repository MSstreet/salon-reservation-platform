````md
# API Spec (MVP) - Salon Reservation Platform

본 문서는 멀티 매장(입점형) 미용실 예약 플랫폼의 MVP API 명세입니다.  
핵심 목표는 **정합성(중복 예약 방지/멱등)**, **정책 기반 패널티**, **운영 조회 성능**을 만족하는 API 설계입니다.

---

## 0. Base Info

- Base URL: `/api/v1`
- Auth: JWT Bearer Token
- Roles:
  - `CUSTOMER`: 예약 생성/취소/내 예약 조회
  - `STORE_ADMIN`: 매장 운영(슬롯 생성, 예약 상태 변경)
  - `ADMIN`: 플랫폼 운영(정책 등록, 전체 검색)

---

## 1. Authentication (MVP)

### 1.1 JWT 사용
- Header:
  - `Authorization: Bearer <JWT>`
- JWT Claims (예시)
  - `sub`: userId
  - `role`: CUSTOMER | STORE_ADMIN | ADMIN
  - `storeId`: (STORE_ADMIN인 경우 필수)
  - `exp`, `iat`

> MVP에서는 실제 회원/로그인은 단순화할 수 있습니다.  
> 예: seed 계정 + `/auth/mock-login` 제공(개발용)

---

## 2. Multi-tenant (Store Scoping) Rule

- 모든 Write/Read는 `storeId` 기준 스코프를 반드시 적용합니다.
- `STORE_ADMIN` 요청은 토큰의 `storeId`와 path의 `{storeId}`가 다르면 `403` 처리합니다.
- `ADMIN`은 전체 조회 가능(필요 시 storeId로 필터링).

## 3. Common Response Format

### 3.1 Success
```json
{
  "success": true,
  "data": {},
  "error": null,
  "meta": {
    "requestId": "req-123",
    "timestamp": "2026-02-15T12:34:56Z"
  }
}
````

### 3.2 Error

```json
{
  "success": false,
  "data": null,
  "error": {
    "code": "RESERVATION_SLOT_ALREADY_BOOKED",
    "message": "Selected time slot is no longer available.",
    "details": {
      "slotId": 101
    }
  },
  "meta": {
    "requestId": "req-123",
    "timestamp": "2026-02-15T12:34:56Z"
  }
}
```

---

## 4. Error Code Standard

### 4.1 HTTP Status Rules

* `400` INVALID_REQUEST: 필수 파라미터 누락/형식 오류
* `401` UNAUTHORIZED: 토큰 없음/만료/위조
* `403` FORBIDDEN: 권한 부족(역할 불일치, store 스코프 위반)
* `404` NOT_FOUND: 리소스 없음
* `409` CONFLICT: 정합성 충돌(중복 예약/중복 상태 변경 등)
* `422` UNPROCESSABLE: 도메인 규칙 위반(취소 불가 시점 등)
* `500` INTERNAL_ERROR

### 4.2 Domain Error Codes (MVP)

* `AUTH_UNAUTHORIZED`
* `AUTH_FORBIDDEN`
* `STORE_NOT_FOUND`
* `STAFF_NOT_FOUND`
* `MENU_NOT_FOUND`
* `SLOT_NOT_FOUND`
* `SLOT_NOT_OPEN`
* `RESERVATION_NOT_FOUND`
* `RESERVATION_INVALID_STATUS`
* `RESERVATION_SLOT_ALREADY_BOOKED` (409)
* `RESERVATION_TIME_OVERLAP` (409)
* `RESERVATION_CANNOT_CANCEL` (422)
* `POLICY_NOT_FOUND`
* `POLICY_VERSION_CONFLICT` (409)
* `IDEMPOTENCY_CONFLICT` (409)

---

## 5 Idempotency (중요)

예약 생성/취소/상태변경 같은 “명령형 API”는 클라이언트 재시도에 대비해 멱등키를 권장합니다.

* Header:

    * `Idempotency-Key: <uuid>`
* 서버는 `(userId, endpoint, idempotencyKey)` 기준으로 일정 시간(TTL) 동일 응답을 반환할 수 있습니다.
* 충돌 시:

    * `409 IDEMPOTENCY_CONFLICT`

---

# 6. APIs

## 6.1 매장 목록 조회 (고객)

### GET `/stores`

고객이 예약할 매장을 선택하기 위한 매장 목록 조회

**Auth**: Optional (로그인 없어도 조회 가능)
**Query**

* `status` (optional) `ACTIVE|INACTIVE` — 미지정 시 ACTIVE만 반환

**Response 200**

```json
{
  "success": true,
  "data": [
    {
      "storeId": 1,
      "name": "Salon A",
      "status": "ACTIVE",
      "timezone": "Asia/Seoul"
    }
  ],
  "error": null,
  "meta": { "requestId": "req-1", "timestamp": "..." }
}
```

**Errors**

* 없음

---

## 6.2 디자이너 목록 조회 (고객)

### GET `/stores/{storeId}/designers`

고객이 매장을 선택한 뒤 해당 매장의 디자이너 목록을 조회

**Auth**: Optional (로그인 없어도 조회 가능)

**Response 200**

```json
{
  "success": true,
  "data": [
    {
      "staffId": 55,
      "name": "Designer B",
      "role": "DESIGNER",
      "status": "ACTIVE"
    }
  ],
  "error": null,
  "meta": { "requestId": "req-1", "timestamp": "..." }
}
```

> ACTIVE 상태의 DESIGNER 역할만 반환합니다.

**Errors**

* `404 STORE_NOT_FOUND`

---

## 6.3 Slot 조회 (고객)

### GET `/stores/{storeId}/slots`

특정 매장/디자이너/날짜/메뉴에 대해 예약 가능한 슬롯 조회

**Auth**: Optional (로그인 없어도 조회 가능)
**Query**

* `date` (required) `YYYY-MM-DD`
* `staffId` (required)
* `menuId` (required)  // duration 기반으로 예약 가능 범위를 판단
* `from` (optional) `HH:mm`
* `to` (optional) `HH:mm`

**Response 200**

```json
{
  "success": true,
  "data": {
    "storeId": 1,
    "date": "2026-02-15",
    "menuId": 10,
    "durationMin": 60,
    "slots": [
      {
        "slotId": 101,
        "staffId": 55,
        "startAt": "2026-02-15T10:00:00",
        "endAt": "2026-02-15T10:30:00",
        "status": "OPEN",
        "isAvailableForMenu": true
      }
    ]
  },
  "error": null,
  "meta": { "requestId": "req-1", "timestamp": "..." }
}
```

**Errors**

* `404 STORE_NOT_FOUND`
* `404 MENU_NOT_FOUND`

---

## 6.4 예약 생성 (고객)

### POST `/stores/{storeId}/reservations`

고객이 디자이너/메뉴/시간을 선택해 예약 생성

**Auth**: `CUSTOMER`
**Headers**

* `Idempotency-Key: <uuid>` (권장)

**Request**

```json
{
  "staffId": 55,
  "menuId": 10,
  "slotId": 101,
  "customerName": "Kim",
  "customerPhone": "01012341234",
  "memo": "염색 알러지 있습니다"
}
```

> `customerPhone`은 서버에서 해시 처리하여 저장 권장

**Response 201**

```json
{
  "success": true,
  "data": {
    "reservationId": 9001,
    "storeId": 1,
    "staffId": 55,
    "menuId": 10,
    "startAt": "2026-02-15T10:00:00",
    "endAt": "2026-02-15T11:00:00",
    "status": "REQUESTED",
    "policyVersionId": 3,
    "createdAt": "2026-02-10T12:00:00"
  },
  "error": null,
  "meta": { "requestId": "req-2", "timestamp": "..." }
}
```

**Errors**

* `404 STORE_NOT_FOUND`
* `404 STAFF_NOT_FOUND`
* `404 MENU_NOT_FOUND`
* `404 SLOT_NOT_FOUND`
* `409 RESERVATION_SLOT_ALREADY_BOOKED`
* `409 RESERVATION_TIME_OVERLAP`
* `422 SLOT_NOT_OPEN`
* `409 IDEMPOTENCY_CONFLICT`

---

## 6.5 예약 취소 (고객)

### POST `/stores/{storeId}/reservations/{reservationId}/cancel`

예약 취소 + (필요 시) 패널티 계산 트리거

**Auth**: `CUSTOMER`
**Headers**

* `Idempotency-Key: <uuid>` (권장)

**Request**

```json
{
  "reason": "개인 사정"
}
```

**Response 200**

```json
{
  "success": true,
  "data": {
    "reservationId": 9001,
    "status": "CANCELED",
    "canceledAt": "2026-02-14T09:00:00",
    "penalty": {
      "exists": true,
      "type": "CANCELLATION",
      "ratePercent": 20,
      "amount": 12000,
      "policyVersionId": 3
    }
  },
  "error": null,
  "meta": { "requestId": "req-3", "timestamp": "..." }
}
```

**Errors**

* `404 RESERVATION_NOT_FOUND`
* `403 AUTH_FORBIDDEN` (store 스코프 위반 등)
* `422 RESERVATION_CANNOT_CANCEL`
* `409 RESERVATION_INVALID_STATUS`
* `409 IDEMPOTENCY_CONFLICT`

---

## 6.6 예약 상태 변경 (매장 운영/어드민)

### POST `/stores/{storeId}/reservations/{reservationId}/status`

예약 상태 변경 (확정/완료/노쇼 등)

**Auth**: `STORE_ADMIN` or `ADMIN`
**Headers**

* `Idempotency-Key: <uuid>` (권장)

**Request**

```json
{
  "status": "CONFIRMED",
  "reason": "매장 승인"
}
```

**Response 200**

```json
{
  "success": true,
  "data": {
    "reservationId": 9001,
    "prevStatus": "REQUESTED",
    "newStatus": "CONFIRMED",
    "changedAt": "2026-02-10T12:05:00"
  },
  "error": null,
  "meta": { "requestId": "req-4", "timestamp": "..." }
}
```

**Status values**

* `REQUESTED`, `CONFIRMED`, `COMPLETED`, `CANCELED`, `NO_SHOW`

**Errors**

* `404 RESERVATION_NOT_FOUND`
* `403 AUTH_FORBIDDEN` (다른 storeId 접근)
* `409 RESERVATION_INVALID_STATUS`
* `409 IDEMPOTENCY_CONFLICT`

---

## 6.7 정책 등록(버전 생성) (플랫폼/어드민)

### POST `/stores/{storeId}/policies/cancellation-no-show/versions`

매장별 취소/노쇼 정책 버전 생성

**Auth**: `ADMIN`

**Request**

```json
{
  "effectiveFrom": "2026-02-01T00:00:00",
  "rules": [
    { "type": "CANCELLATION", "minHoursBefore": 24, "ratePercent": 0 },
    { "type": "CANCELLATION", "minHoursBefore": 3, "ratePercent": 20 },
    { "type": "CANCELLATION", "minHoursBefore": 0, "ratePercent": 30 },
    { "type": "NO_SHOW", "minHoursBefore": 0, "ratePercent": 50 }
  ],
  "note": "2026-02 정책 업데이트"
}
```

**Response 201**

```json
{
  "success": true,
  "data": {
    "policyVersionId": 3,
    "storeId": 1,
    "version": 3,
    "effectiveFrom": "2026-02-01T00:00:00",
    "createdAt": "2026-02-10T12:10:00"
  },
  "error": null,
  "meta": { "requestId": "req-5", "timestamp": "..." }
}
```

**Errors**

* `404 STORE_NOT_FOUND`
* `409 POLICY_VERSION_CONFLICT` (동일 버전/기간 충돌)
* `400 INVALID_REQUEST`

---

## 6.8 어드민 예약 검색 (운영 조회)

### GET `/admin/reservations`

운영자용 예약/취소/노쇼/수수료 대상 케이스 조회 (OpenSearch 기반 Read Model)

**Auth**: `ADMIN`
**Query**

* `storeId` (optional)  // 전체 또는 특정 매장
* `status` (optional) `REQUESTED|CONFIRMED|COMPLETED|CANCELED|NO_SHOW`
* `from` (required) `YYYY-MM-DD`
* `to` (required) `YYYY-MM-DD`
* `q` (optional) 키워드(고객명/전화 일부/메모/디자이너명)
* `hasPenalty` (optional) `true|false`
* `page` (default: 0)
* `size` (default: 20)
* `sort` (default: `startAt,desc`)  // 예: `penaltyAmount,desc`

**Response 200**

```json
{
  "success": true,
  "data": {
    "page": 0,
    "size": 20,
    "totalElements": 120,
    "items": [
      {
        "reservationId": 9001,
        "storeId": 1,
        "storeName": "Salon A",
        "staffName": "Designer B",
        "menuName": "Cut",
        "startAt": "2026-02-15T10:00:00",
        "status": "CANCELED",
        "customerName": "Kim",
        "hasPenalty": true,
        "penaltyAmount": 12000
      }
    ]
  },
  "error": null,
  "meta": { "requestId": "req-6", "timestamp": "..." }
}
```

**Errors**

* `403 AUTH_FORBIDDEN`
* `400 INVALID_REQUEST`

---

## 6.9 예약 고객 상세 조회

### GET `/reservation-customers/{customerId}`

예약 고객 정보 및 예약 이력 확인용

**Auth**: `CUSTOMER` (본인) or `STORE_ADMIN` or `ADMIN`

**Response 200**

```json
{
  "success": true,
  "data": {
    "customerId": 1,
    "name": "Kim",
    "phone": "01012341234",
    "email": "kim@example.com",
    "createdAt": "2026-02-21T10:00:00"
  },
  "error": null,
  "meta": { "requestId": "req-8", "timestamp": "..." }
}
```

**Errors**

* `404 RESERVATION_CUSTOMER_NOT_FOUND`
* `403 AUTH_FORBIDDEN` — 본인이 아닌 고객 정보 접근

---

## 6.10 예약 고객 예약 내역 조회

### GET `/reservation-customers/{customerId}/reservations`

특정 예약 고객의 예약 내역 목록 조회

**Auth**: `CUSTOMER` (본인) or `STORE_ADMIN` or `ADMIN`
**Query**

* `status` (optional) `REQUESTED|CONFIRMED|COMPLETED|CANCELED|NO_SHOW`
* `from` (optional) `YYYY-MM-DD`
* `to` (optional) `YYYY-MM-DD`
* `page` (default: 0)
* `size` (default: 20)

**Response 200**

```json
{
  "success": true,
  "data": {
    "page": 0,
    "size": 20,
    "totalElements": 5,
    "items": [
      {
        "reservationId": 9001,
        "storeId": 1,
        "storeName": "Salon A",
        "staffName": "Designer B",
        "menuName": "Cut",
        "startAt": "2026-02-15T10:00:00",
        "endAt": "2026-02-15T11:00:00",
        "status": "CONFIRMED",
        "createdAt": "2026-02-10T12:00:00"
      }
    ]
  },
  "error": null,
  "meta": { "requestId": "req-9", "timestamp": "..." }
}
```

**Errors**

* `404 RESERVATION_CUSTOMER_NOT_FOUND`
* `403 AUTH_FORBIDDEN`

---

## 6.11 매장별 예약 고객 목록 조회 (매장 운영)

### GET `/stores/{storeId}/reservation-customers`

해당 매장에 예약한 적 있는 예약 고객 목록 조회 (STORE_ADMIN용)

**Auth**: `STORE_ADMIN` or `ADMIN`
**Query**

* `q` (optional) 고객명/전화번호 검색
* `page` (default: 0)
* `size` (default: 20)

**Response 200**

```json
{
  "success": true,
  "data": {
    "page": 0,
    "size": 20,
    "totalElements": 50,
    "items": [
      {
        "customerId": 1,
        "name": "Kim",
        "phone": "01012341234",
        "totalReservations": 5,
        "lastVisitAt": "2026-02-15T10:00:00"
      }
    ]
  },
  "error": null,
  "meta": { "requestId": "req-10", "timestamp": "..." }
}
```

**Errors**

* `404 STORE_NOT_FOUND`
* `403 AUTH_FORBIDDEN`

---

## 7. Notes (MVP Scope)

* 결제/예약금은 MVP 범위에서 제외(추후 HOLD/TTL 모델로 확장 가능)
* `TimeSlot`은 MVP에서 30분 단위로 생성/관리
* 메뉴 소요시간(60/120분)은 예약 생성 시 연속 슬롯 가능 여부로 검증(구현 단계에서 확정)

---

```

---

## 다음 커밋/PR 추천 (바로 쓰기)
- 브랜치: `feature/docs-api-spec`
- 커밋 메시지:
```

docs: add API specification for MVP

```
- PR 대상: `develop`

---

원하면 다음 단계로 바로 이어서:
- `docs/domain-rules.md` (슬롯 점유, 취소 가능 기준, 노쇼 판정, 상태 전이 규칙)
- 또는 `docs/event-spec.md` (Kafka 토픽/스키마/파티션키/멱등 전략)

어느 걸 먼저 갈까?
```
