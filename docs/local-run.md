# 로컬 실행 가이드

## 필수 설치 사항 (Mac 기준)

- **JDK 17** (Spring Boot 3 권장)
- **Docker Desktop**
- **Git**
- **Gradle** (프로젝트 내 Gradle Wrapper 포함)

확인:
```bash
java -version
docker --version
docker compose version
```

---

## 1. 환경 변수 설정

`.env.example`을 복사하여 `.env` 파일 생성:

```bash
cp .env.example .env
```

필요 시 `.env` 파일의 값들을 수정하세요.

---

## 2. 로컬 인프라 실행

Docker Compose로 MySQL, Redis, Kafka, OpenSearch를 한 번에 실행:

```bash
docker compose up -d
```

실행 확인:
```bash
docker compose ps
```

로그 확인:
```bash
docker compose logs -f kafka
```

---

## 3. 인프라 동작 확인

### MySQL
```bash
docker exec -it salon-mysql mysql -u salon -psalon -D salon -e "SELECT 1;"
```

### Redis
```bash
docker exec -it salon-redis redis-cli ping
```

### Kafka (토픽 리스트)
```bash
docker exec -it salon-kafka kafka-topics --bootstrap-server localhost:9092 --list
```

### OpenSearch
```bash
curl -s http://localhost:9200 | head
```

---

## 4. Gradle 빌드 확인

프로젝트 루트에서:

```bash
./gradlew clean build
```

테스트 제외 빌드:
```bash
./gradlew clean build -x test
```

---

## 5. Spring Boot 애플리케이션 실행

### IntelliJ IDEA에서:
1. `SalonReservationApplication.java` 우클릭
2. `Run 'SalonReservationApplication'` 선택
3. Active Profile: `local` 설정 (VM options: `-Dspring.profiles.active=local`)

### 터미널에서:
```bash
./gradlew bootRun --args='--spring.profiles.active=local'
```

### Health Check 확인
애플리케이션 실행 후:
```bash
curl http://localhost:8080/api/health
```

응답 예시:
```json
{
  "status": "UP",
  "timestamp": "2024-02-09T10:30:00",
  "service": "salon-reservation-platform"
}
```

---

## 6. 종료

### 애플리케이션 종료
- IntelliJ: Stop 버튼
- 터미널: `Ctrl + C`

### 인프라 종료
```bash
docker compose down
```

### 데이터까지 초기화
```bash
docker compose down -v
```

---

## 트러블슈팅

### MySQL 연결 안 될 때
- 포트 충돌 확인: `lsof -i :3306`
- 기존 MySQL 서비스가 실행 중이면 중지
- 컨테이너 재시작: `docker compose restart mysql`

### Kafka 연결 안 될 때
- Kafka는 내부/외부 리스너 설정이 복잡합니다
- `docker compose logs kafka`로 에러 확인
- 필요 시 컨테이너 재시작: `docker compose restart kafka`

### OpenSearch 메모리 부족
- Docker Desktop 메모리 설정 확인 (최소 4GB 권장)
- `docker compose logs opensearch`로 확인

### Gradle 빌드 실패
```bash
# Gradle Wrapper 재생성
gradle wrapper --gradle-version 8.5

# 권한 설정
chmod +x gradlew
```

### JPA ddl-auto=create 주의
- 로컬 환경에서는 `create`로 설정되어 있어 애플리케이션 시작 시 테이블이 재생성됩니다
- 데이터를 유지하려면 `application-local.yml`에서 `validate` 또는 `update`로 변경하세요
