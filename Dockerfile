# --------- Build Stage ---------
FROM ghcr.io/graalvm/jdk-community:21-debian AS builder
WORKDIR /app

# Install required Linux tools for Gradle
RUN apt-get update && apt-get install -y bash unzip xargs procps && rm -rf /var/lib/apt/lists/*

# Copy Gradle wrapper & build files
COPY gradlew .
COPY gradle gradle
COPY build.gradle settings.gradle ./

# Make wrapper executable
RUN chmod +x ./gradlew

# Download dependencies (cache layer)
RUN ./gradlew build -x test --no-daemon

# Copy source code
COPY src src

# Build the jar
RUN ./gradlew clean bootJar -x test --no-daemon

# --------- Runtime Stage ---------
FROM eclipse-temurin:21-jre
WORKDIR /app

# Copy built jar from builder stage
COPY --from=builder /app/build/libs/*.jar app.jar

# Expose port 8080
EXPOSE 8080

# Run Spring Boot
ENTRYPOINT ["java","-jar","app.jar"]
