# Multi-stage build for Spring Boot application
FROM gradle:8.5-jdk21 AS build

# Set working directory
WORKDIR /app

# Copy gradle files first for better caching
COPY build.gradle settings.gradle ./
COPY gradle/ gradle/

# Download dependencies (this layer will be cached if dependencies don't change)
RUN gradle dependencies --no-daemon

# Copy source code
COPY src/ src/

# Build the application
RUN gradle bootJar --no-daemon

# Runtime stage
FROM openjdk:21-jdk-slim

# Set working directory
WORKDIR /app

# Create directory for media files
RUN mkdir -p /app/media && chmod 775 /app/media

# Create non-root user for security
RUN groupadd -r appuser && useradd -r -g appuser appuser

# Add appuser to docker group (GID 999 matches host docker group)
RUN groupadd -g 999 docker && usermod -aG docker appuser

# Install curl for health checks (optional)
RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

# Copy the built jar from build stage
COPY --from=build /app/build/libs/*.jar app.jar

# Change ownership of the app directory to appuser
RUN chown -R appuser:appuser /app

# Switch to non-root user
USER appuser

# Expose port
EXPOSE 8080

# Set active Spring profile
ENV SPRING_PROFILES_ACTIVE=prod

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]