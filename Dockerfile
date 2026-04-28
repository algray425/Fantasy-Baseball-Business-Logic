# ── Stage 1: Build ──────────────────────────────────────────────
FROM gradle:8.7-jdk21 AS builder
WORKDIR /app

COPY build.gradle.kts settings.gradle.kts ./
COPY gradle/ ./gradle/
RUN gradle dependencies --no-daemon --configuration runtimeClasspath || true

COPY src/ ./src/
RUN gradle shadowJar --no-daemon --stacktrace

# ── Stage 2: Run ────────────────────────────────────────────────
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

RUN addgroup -S appgroup && adduser -S appuser -G appgroup
RUN mkdir -p /app/data && chown appuser:appgroup /app/data

COPY --from=builder /app/build/libs/*-all.jar app.jar

# Copy your existing database into the image
COPY ./db/baseball_info.db /app/data/baseball_info.db
RUN chown appuser:appgroup /app/data/baseball_info.db

COPY ./db/fantasy_baseball_users.db /app/data/fantasy_baseball_users.db
RUN chown appuser:appgroup /app/data/fantasy_baseball_users.db

USER appuser

EXPOSE 9292
ENTRYPOINT ["java", "-XX:+UseContainerSupport", "-XX:MaxRAMPercentage=75.0", "-jar", "app.jar"]