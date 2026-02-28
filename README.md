# Salon Reservation Platform

> **멀티 매장 미용실 예약에서 발생하는 취소·노쇼 수수료를 정책 기반으로 자동 산정하고,  
이벤트 기반 아키텍처로 정합성과 운영 조회를 분리한 예약 운영 플랫폼**

---

## Why

미용실 예약 운영에서는 다음과 같은 문제가 반복적으로 발생합니다.

- 노쇼 및 당일 취소로 인한 매장 손실
- 취소 시점에 따라 달라지는 수수료 정책으로 인한 분쟁
- 정책 변경 이후 과거 예약에 대한 계산 근거 추적의 어려움
- 예약/취소 데이터 증가에 따른 운영자 조회 성능 저하

본 프로젝트는 위 문제를 **시스템적으로 해결하는 것**에 초점을 둡니다.

---

## How

이 프로젝트는 다음과 같은 전략으로 문제를 해결합니다.

- **정책 버전 관리 기반 수수료 엔진**
  - 정책 변경 시에도 과거 예약은 당시 정책으로 재현 가능
  - “왜 이 금액이 부과되었는지” 근거를 데이터로 보존
- **Redis 기반 정합성 보장**
  - 슬롯 중복 예약 방지
  - 상태 변경 및 요청 멱등성 처리
- **Kafka 기반 이벤트 분리**
  - 예약 상태 변경을 이벤트로 발행
  - 검색/알림/감사 로그 처리 로직 분리
- **OpenSearch 기반 Read Model**
  - 운영자(어드민) 조회를 위한 고속 검색
  - 기간/상태/매장/키워드 필터링 지원

---

## What

### Core Features
- 멀티 매장 입점형 예약 플랫폼
- 디자이너 타임슬롯 기반 예약
- 취소 / 노쇼 수수료 자동 계산
- 정책 엔진 + 정책 버전 고정
- 예약 상태 변경 감사 로그(Audit)
- 운영자용 예약/수수료 고속 검색

### Architecture Highlights
- Event-driven architecture
- Read / Write 모델 분리
- Consistency & traceability 중심 설계
- CI/CD 기반 자동 배포 환경
- Secret Manager를 통한 민감 정보 관리

---

## API Docs (Swagger UI)

| 모듈 | 환경 | URL |
|---|---|---|
| API (고객용) | 로컬 | http://localhost:8080/swagger-ui/index.html |
| API (고객용) | 개발계 | http://{DEV_API_HOST}/swagger-ui/index.html |
| Admin (관리자용) | 로컬 | http://localhost:8081/swagger-ui/index.html |
| Admin (관리자용) | 개발계 | http://{DEV_ADMIN_HOST}/swagger-ui/index.html |

> `{DEV_API_HOST}` / `{DEV_ADMIN_HOST}` 는 개발계 배포 후 실제 도메인으로 교체

---

## Tech Stack

- **Backend**: Spring Boot, JPA
- **Messaging**: Kafka
- **Cache / Lock**: Redis
- **Search**: OpenSearch
- **Infra**: Docker, Docker Compose
- **CI/CD**: GitHub Actions
- **Secrets**: Secret Manager (env / managed secrets)

---

> **This project focuses on consistency, traceability, and operational scalability rather than feature richness.**
