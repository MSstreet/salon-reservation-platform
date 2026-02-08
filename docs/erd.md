# ERD (Multi-Salon Reservation Platform)

본 문서는 멀티 매장(입점형) 미용실 예약 플랫폼의 핵심 도메인 모델과 관계, PK/FK, 인덱스 설계를 정의합니다.

---

## 1. 멀티 매장(테넌시) 규칙

- 모든 도메인 데이터는 반드시 `store_id`를 기준으로 스코프가 제한됩니다.
- 조회/변경 API는 항상 `store_id` 조건이 포함되어야 하며, 인덱스도 이를 고려합니다.
- (권장) 애플리케이션 레벨에서 `store_id` 필터를 공통 적용하거나, QueryDSL/Repository 계층에서 기본 조건으로 강제합니다.

---

## 2. 엔티티 목록

- `Store` : 입점 매장
- `Staff` : 디자이너/직원
- `ServiceMenu` : 시술 메뉴(커트/염색 등)
- `StaffSchedule` : 디자이너 근무/휴무 스케줄
- `TimeSlot` : 예약 가능한 시간 슬롯(예: 30분 단위)
- `Reservation` : 예약 본체
- `PolicyVersion` : 취소/노쇼 패널티 정책 버전(매장별)
- `Penalty` : 수수료(패널티) 산정 결과
- `ReservationEvent` : 감사 로그(상태 변경/패널티 계산 등)

---

## 3. 테이블 설계 (PK/FK/인덱스)

> 타입은 DB에 맞춰 조정하세요. (권장: `BIGINT` PK + `UUID`(eventId) 보조키)

### 3.1 Store
- **PK**: `store_id`
- 컬럼(예시):
  - `store_id` (BIGINT)
  - `name` (VARCHAR)
  - `status` (ENUM: ACTIVE/INACTIVE)
  - `timezone` (VARCHAR)  // 예: Asia/Seoul
  - `created_at`, `updated_at`
- **인덱스**
  - `idx_store_status (status)` (선택)

---

### 3.2 Staff
- **PK**: `staff_id`
- **FK**: `store_id -> Store.store_id`
- 컬럼(예시):
  - `staff_id` (BIGINT)
  - `store_id` (BIGINT)
  - `name` (VARCHAR)
  - `role` (ENUM: DESIGNER/STAFF/STORE_ADMIN)
  - `status` (ENUM: ACTIVE/INACTIVE)
  - `created_at`, `updated_at`
- **인덱스**
  - `idx_staff_store (store_id)`
  - `uq_staff_store_name (store_id, name)` (선택: 동일 매장 내 중복 이름 제한 시)

---

### 3.3 ServiceMenu
- **PK**: `menu_id`
- **FK**: `store_id -> Store.store_id`
- 컬럼(예시):
  - `menu_id` (BIGINT)
  - `store_id` (BIGINT)
  - `name` (VARCHAR)               // 커트/염색 등
  - `duration_min` (INT)           // 30/60/120...
  - `price` (INT)
  - `status` (ENUM: ACTIVE/INACTIVE)
  - `created_at`, `updated_at`
- **인덱스**
  - `idx_menu_store (store_id)`
  - `uq_menu_store_name (store_id, name)` (선택)

---

### 3.4 StaffSchedule
- **PK**: `schedule_id`
- **FK**: `store_id -> Store.store_id`
- **FK**: `staff_id -> Staff.staff_id`
- 컬럼(예시):
  - `schedule_id` (BIGINT)
  - `store_id` (BIGINT)
  - `staff_id` (BIGINT)
  - `date` (DATE)                  // 특정 일자
  - `start_time` (TIME)
  - `end_time` (TIME)
  - `type` (ENUM: WORK/OFF)         // 근무/휴무
  - `created_at`, `updated_at`
- **인덱스**
  - `idx_schedule_store_date (store_id, date)`
  - `idx_schedule_staff_date (staff_id, date)`
  - `uq_schedule_staff_date (staff_id, date)` (선택: 하루 1개 룰이면)

---

### 3.5 TimeSlot
- **PK**: `slot_id`
- **FK**: `store_id -> Store.store_id`
- **FK**: `staff_id -> Staff.staff_id`
- 컬럼(예시):
  - `slot_id` (BIGINT)
  - `store_id` (BIGINT)
  - `staff_id` (BIGINT)
  - `date` (DATE)
  - `start_at` (DATETIME)          // 슬롯 시작 시각(타임존은 store.timezone 기준)
  - `end_at` (DATETIME)            // 슬롯 종료(보통 start_at + 30m)
  - `status` (ENUM: OPEN/HELD/BOOKED/BLOCKED)
  - `held_until` (DATETIME, nullable) // HOLD 만료(선택)
  - `created_at`, `updated_at`
- **인덱스**
  - `idx_slot_store_date (store_id, date)`
  - `idx_slot_staff_start (staff_id, start_at)`
  - `idx_slot_store_staff_start (store_id, staff_id, start_at)`
- **유니크 제약(중요)**
  - `uq_slot_staff_start (staff_id, start_at)`  
    → 동일 디자이너 같은 시간에 슬롯 중복 생성 방지

> 구현 팁: 초기 MVP는 `OPEN/BOOKED/BLOCKED`만 써도 충분합니다. (`HELD`는 결제 홀드가 필요할 때)

---

### 3.6 Reservation
- **PK**: `reservation_id`
- **FK**: `store_id -> Store.store_id`
- **FK**: `staff_id -> Staff.staff_id`
- **FK**: `menu_id -> ServiceMenu.menu_id`
- **FK(권장)**: `slot_id -> TimeSlot.slot_id` (선택: 대표 슬롯만 연결)
- **FK(핵심)**: `policy_version_id -> PolicyVersion.policy_version_id`  // 예약 확정 시점 정책 고정
- 컬럼(예시):
  - `reservation_id` (BIGINT)
  - `store_id` (BIGINT)
  - `staff_id` (BIGINT)
  - `menu_id` (BIGINT)
  - `slot_id` (BIGINT, nullable)
  - `customer_id` (BIGINT 또는 VARCHAR)  // MVP는 간단히
  - `customer_name` (VARCHAR)
  - `customer_phone_hash` (VARCHAR)      // 개인정보 보호(해시)
  - `start_at` (DATETIME)
  - `end_at` (DATETIME)
  - `status` (ENUM: REQUESTED/CONFIRMED/COMPLETED/CANCELED/NO_SHOW)
  - `policy_version_id` (BIGINT)
  - `cancel_reason` (VARCHAR, nullable)
  - `created_at`, `updated_at`
- **인덱스**
  - `idx_res_store_start (store_id, start_at)`
  - `idx_res_store_status_start (store_id, status, start_at)`
  - `idx_res_staff_start (staff_id, start_at)`
  - `idx_res_customer_phone (customer_phone_hash)` (선택)

> 메뉴 소요시간 반영 방식: `start_at/end_at`은 Reservation에 확정 저장(재현/증빙용)

---

### 3.7 PolicyVersion
- **PK**: `policy_version_id`
- **FK**: `store_id -> Store.store_id`
- 컬럼(예시):
  - `policy_version_id` (BIGINT)
  - `store_id` (BIGINT)
  - `version` (INT)                  // 1,2,3...
  - `effective_from` (DATETIME)      // 적용 시작(선택)
  - `rules_json` (JSON/TEXT)         // 취소/노쇼 룰 정의
  - `created_by` (VARCHAR)
  - `created_at`
- **인덱스 / 유니크**
  - `uq_policy_store_version (store_id, version)`
  - `idx_policy_store_effective (store_id, effective_from)` (선택)

> 핵심: Reservation은 반드시 `policy_version_id`를 저장하여 “당시 정책”을 재현 가능하게 합니다.

---

### 3.8 Penalty
- **PK**: `penalty_id`
- **FK**: `store_id -> Store.store_id`
- **FK**: `reservation_id -> Reservation.reservation_id`
- **FK**: `policy_version_id -> PolicyVersion.policy_version_id`
- 컬럼(예시):
  - `penalty_id` (BIGINT)
  - `store_id` (BIGINT)
  - `reservation_id` (BIGINT)
  - `policy_version_id` (BIGINT)
  - `type` (ENUM: CANCELLATION/NO_SHOW)
  - `rate_percent` (INT)             // 20, 30, 50...
  - `amount` (INT)
  - `currency` (VARCHAR, default: KRW)
  - `calculated_at` (DATETIME)
  - `basis_json` (JSON/TEXT)         // 계산 근거(취소시점, 룰ID 등)
- **인덱스 / 유니크**
  - `uq_penalty_reservation (reservation_id)`  // 예약당 1개 패널티로 고정하면
  - `idx_penalty_store_type_date (store_id, type, calculated_at)`

---

### 3.9 ReservationEvent (Audit Log)
- **PK**: `event_id` (UUID 권장) 또는 BIGINT + UUID 보조키
- **FK**: `store_id -> Store.store_id`
- **FK**: `reservation_id -> Reservation.reservation_id`
- 컬럼(예시):
  - `event_id` (UUID)
  - `store_id` (BIGINT)
  - `reservation_id` (BIGINT)
  - `event_type` (ENUM: RESERVATION_CREATED/STATUS_CHANGED/PENALTY_CALCULATED/NO_SHOW_MARKED/...)
  - `occurred_at` (DATETIME)
  - `actor_type` (ENUM: CUSTOMER/STORE_ADMIN/SYSTEM)
  - `actor_id` (VARCHAR)
  - `payload_json` (JSON/TEXT)       // 변경 전/후, 근거 등
- **인덱스**
  - `idx_event_store_time (store_id, occurred_at)`
  - `idx_event_res_time (reservation_id, occurred_at)`
  - `idx_event_type_time (event_type, occurred_at)` (선택)

---

## 4. Mermaid ERD

```mermaid
erDiagram
    STORE ||--o{ STAFF : has
    STORE ||--o{ SERVICE_MENU : offers
    STORE ||--o{ STAFF_SCHEDULE : schedules
    STORE ||--o{ TIME_SLOT : provides
    STORE ||--o{ RESERVATION : owns
    STORE ||--o{ POLICY_VERSION : defines
    STORE ||--o{ PENALTY : generates
    STORE ||--o{ RESERVATION_EVENT : logs

    STAFF ||--o{ STAFF_SCHEDULE : works
    STAFF ||--o{ TIME_SLOT : opens
    STAFF ||--o{ RESERVATION : serves

    SERVICE_MENU ||--o{ RESERVATION : booked_with

    TIME_SLOT ||--o{ RESERVATION : binds

    POLICY_VERSION ||--o{ RESERVATION : applied_to
    POLICY_VERSION ||--o{ PENALTY : used_for

    RESERVATION ||--o| PENALTY : may_have
    RESERVATION ||--o{ RESERVATION_EVENT : emits

    STORE {
        BIGINT store_id PK
        VARCHAR name
        VARCHAR timezone
        ENUM status
        DATETIME created_at
        DATETIME updated_at
    }

    STAFF {
        BIGINT staff_id PK
        BIGINT store_id FK
        VARCHAR name
        ENUM role
        ENUM status
        DATETIME created_at
        DATETIME updated_at
    }

    SERVICE_MENU {
        BIGINT menu_id PK
        BIGINT store_id FK
        VARCHAR name
        INT duration_min
        INT price
        ENUM status
        DATETIME created_at
        DATETIME updated_at
    }

    STAFF_SCHEDULE {
        BIGINT schedule_id PK
        BIGINT store_id FK
        BIGINT staff_id FK
        DATE date
        TIME start_time
        TIME end_time
        ENUM type
        DATETIME created_at
        DATETIME updated_at
    }

    TIME_SLOT {
        BIGINT slot_id PK
        BIGINT store_id FK
        BIGINT staff_id FK
        DATE date
        DATETIME start_at
        DATETIME end_at
        ENUM status
        DATETIME held_until
        DATETIME created_at
        DATETIME updated_at
    }

    POLICY_VERSION {
        BIGINT policy_version_id PK
        BIGINT store_id FK
        INT version
        DATETIME effective_from
        TEXT rules_json
        VARCHAR created_by
        DATETIME created_at
    }

    RESERVATION {
        BIGINT reservation_id PK
        BIGINT store_id FK
        BIGINT staff_id FK
        BIGINT menu_id FK
        BIGINT slot_id FK
        BIGINT policy_version_id FK
        VARCHAR customer_name
        VARCHAR customer_phone_hash
        DATETIME start_at
        DATETIME end_at
        ENUM status
        VARCHAR cancel_reason
        DATETIME created_at
        DATETIME updated_at
    }

    PENALTY {
        BIGINT penalty_id PK
        BIGINT store_id FK
        BIGINT reservation_id FK
        BIGINT policy_version_id FK
        ENUM type
        INT rate_percent
        INT amount
        VARCHAR currency
        DATETIME calculated_at
        TEXT basis_json
    }

    RESERVATION_EVENT {
        UUID event_id PK
        BIGINT store_id FK
        BIGINT reservation_id FK
        ENUM event_type
        DATETIME occurred_at
        ENUM actor_type
        VARCHAR actor_id
        TEXT payload_json
    }
