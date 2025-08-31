# --------- Build Stage ---------
FROM ghcr.io/graalvm/jdk-community:21 AS builder
WORKDIR /app

# Copy Gradle wrapper & build files
COPY gradlew .
COPY gradle gradle
COPY build.gradle settings.gradle ./

# Download dependencies (cache layer)
RUN ./gradlew dependencies --no-daemon || return 0

# Copy the rest of the source code
COPY src src

# Build the jar
RUN ./gradlew clean bootJar --no-daemon

# --------- Runtime Stage ---------
FROM eclipse-temurin:21-jre
WORKDIR /app

# Copy built jar from builder stage
COPY --from=builder /app/build/libs/*.jar app.jar

# Expose port 8080
EXPOSE 8080

# Run Spring Boot
ENTRYPOINT ["java","-jar","app.jar"]
