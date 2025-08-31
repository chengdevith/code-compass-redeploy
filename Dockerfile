# --------- Build Stage ---------
FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /app

# Install required tools for Gradle
RUN apk add --no-cache bash unzip findutils curl git

# Copy Gradle wrapper & make it executable
COPY gradlew .
RUN chmod +x ./gradlew

# Copy Gradle config
COPY gradle gradle
COPY build.gradle settings.gradle ./

# Copy source code before building
COPY src src

# Download dependencies and build jar (skip tests)
RUN ./gradlew clean bootJar -x test --no-daemon

# Install required tools for Gradle
RUN apk add --no-cache bash unzip findutils curl git

# Copy Gradle wrapper & make it executable
COPY gradlew .
RUN chmod +x ./gradlew

# Copy Gradle config
COPY gradle gradle
COPY build.gradle settings.gradle ./

# Download dependencies
RUN ./gradlew build -x test --no-daemon

# Copy source code
COPY src src

# Build the jar
RUN ./gradlew clean bootJar --no-daemon

# --------- Runtime Stage ---------
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Copy built jar
COPY --from=builder /app/build/libs/*.jar app.jar

# Expose port 8080
EXPOSE 8080

# Run Spring Boot
ENTRYPOINT ["java","-jar","app.jar"]
