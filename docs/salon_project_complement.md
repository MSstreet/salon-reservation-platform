# API Gap Analysis

## 1. 현재 구현 상태 평가

### 👍 잘 되어있는 부분

#### 매장 / 디자이너 / 메뉴 관리

* 매장 CRUD 존재
* 디자이너 등록 및 상태 변경 가능
* 메뉴 등록 및 활성/비활성 처리 가능

#### 예약 기본 기능

* 예약 생성
* 예약 취소
* 예약 조회 (필터 포함)

#### 타임슬롯 구조

* 타임슬롯 자동 생성 API 존재

#### API 구조

* Admin / API 서버 분리 (8081 / 8080)

---

## 2. 빠진 핵심 기능

### 🚨 1. 예약 상태 변경 API 없음

#### 필요 API

```
POST /admin/reservations/{reservationId}/approve
POST /admin/reservations/{reservationId}/reject
POST /admin/reservations/{reservationId}/complete
POST /admin/reservations/{reservationId}/no-show
```

#### 문제점

* 예약 상태 흐름 관리 불가
* 결제 / 리뷰 / 정산 불가능

---

### 🚨 2. 결제 상태 관리 없음

#### 필요 API

```
POST /api/payments/deposits/confirm
POST /api/payments/deposits/fail
POST /api/payments/deposits/{depositId}/refund
```

#### 문제점

* 결제 성공/실패 구분 불가
* 환불 처리 불가

---

### 🚨 3. 타임슬롯 조회 설계 부족

#### 개선 필요

```
GET /api/stores/{storeId}/time-slots
?date=YYYY-MM-DD
&designerId=1
&menuId=1
```

#### 문제점

* 시술 소요시간 반영 불가
* 예약 가능 여부 정확도 낮음

---

### 🚨 4. 디자이너별 메뉴 없음

#### 필요 API

```
GET /api/designers/{designerId}/menus
```

#### 문제점

* 디자이너별 시술 제한 표현 불가

---

### 🚨 5. 사용자 기준 예약 조회 없음

#### 필요 API

```
GET /api/users/me/reservations
```

#### 문제점

* 사용자 UX 불가능

---

### 🚨 6. 리뷰 시스템 없음

#### 필요 API

```
POST /api/reservations/{reservationId}/reviews
GET /api/designers/{designerId}/reviews
```

#### 문제점

* 서비스 신뢰도 부족

---

### 🚨 7. 스케줄 조회 없음

#### 필요 API

```
GET /admin/stores/{storeId}/staff-schedules
GET /api/designers/{designerId}/schedules
```

#### 문제점

* 타임슬롯 계산 불완전

---

### 🚨 8. 예약 상세 조회 없음

#### 필요 API

```
GET /api/reservations/{reservationId}
```

#### 문제점

* 상세 화면 구성 불가

---

### 🚨 9. 인증 시스템 부족

#### 현재

* Mock 로그인만 존재

#### 필요 API

```
POST /auth/login
POST /auth/refresh
```

#### 문제점

* 권한 분리 불가

---

### 🚨 10. 예약 충돌 방지 설계 없음

#### 필요 요소

* 타임슬롯 중복 체크
* Lock (Redis 또는 DB)

#### 문제점

* 이중 예약 발생 가능

---

## 3. 우선순위 정리

### 🔴 1순위 (필수)

* 예약 상태 변경 API
* 결제 상태 처리 API
* 타임슬롯 조회 개선
* 사용자 예약 조회
* 예약 상세 조회

### 🟠 2순위

* 디자이너 메뉴 매핑
* 스케줄 조회
* 리뷰 시스템

### 🟡 3순위

* 알림
* 통계
* 인증 고도화

---

## 4. 최종 평가

현재 상태:

> CRUD 기반 예약 시스템 (약 60점)

보완 후:

> 실제 서비스 가능 수준 (85~90점)

---

## 5. 다음 단계

* 예약 API 상세 설계
* 트랜잭션 처리
* 동시성 제어 (Redis Lock)
* 결제 연동 구조 설계
