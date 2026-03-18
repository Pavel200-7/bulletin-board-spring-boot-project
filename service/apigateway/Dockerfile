# Этап сборки
FROM eclipse-temurin:21-jdk AS builder
WORKDIR /workspace

COPY gradlew .
COPY gradle/wrapper/gradle-wrapper.jar gradle/wrapper/
COPY gradle/wrapper/gradle-wrapper.properties gradle/wrapper/
COPY build.gradle .
COPY settings.gradle .

COPY gradle gradle/
RUN ./gradlew dependencies --no-daemon || true
COPY src src
RUN ./gradlew clean bootJar -x test --no-daemon

# Финальный этап 
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app
COPY --from=builder /workspace/build/libs/*.jar app.jar
CMD ["java", "-jar", "app.jar"]