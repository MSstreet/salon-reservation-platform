# ── Build Stage ──────────────────────────────────────────────────────────────
FROM eclipse-temurin:17-jdk-alpine AS builder
WORKDIR /workspace

COPY gradlew .
COPY gradle/ gradle/
COPY settings.gradle .
COPY build.gradle .
COPY core/ core/
COPY api/ api/
COPY admin/ admin/

RUN chmod +x gradlew && ./gradlew :admin:bootJar -x test --no-daemon

# ── Run Stage ─────────────────────────────────────────────────────────────────
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

ENV SPRING_PROFILES_ACTIVE=dev

COPY --from=builder /workspace/admin/build/libs/salon-admin.jar app.jar

EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]