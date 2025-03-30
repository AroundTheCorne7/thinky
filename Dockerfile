FROM eclipse-temurin:21-jdk AS builder

WORKDIR /app

# Copy Gradle files and build everything
COPY . .
RUN ./gradlew build

# ---

FROM eclipse-temurin:21-jdk-alpine

WORKDIR /app

COPY --from=builder /app/build/libs/thinky.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]
