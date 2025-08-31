# --------- Build Stage ---------
FROM ghcr.io/graalvm/graalvm-community:23 AS builder
WORKDIR /app

# Copy Gradle wrapper & make it executable
COPY gradlew .
RUN chmod +x ./gradlew

# Copy Gradle configuration
COPY gradle gradle
COPY build.gradle settings.gradle ./

# Download dependencies
RUN ./gradlew build -x test --no-daemon

# Copy source code
COPY src src

# Build the jar
RUN ./gradlew clean bootJar --no-daemon

# --------- Runtime Stage ---------
FROM eclipse-temurin:21-jre
WORKDIR /app

# Copy built jar
COPY --from=builder /app/build/libs/*.jar app.jar

# Expose port 8080
EXPOSE 8080

# Run Spring Boot
ENTRYPOINT ["java","-jar","app.jar"]
