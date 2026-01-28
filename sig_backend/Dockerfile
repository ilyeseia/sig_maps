# Build stage
FROM eclipse-temurin:17-jdk as builder
WORKDIR /app

# Install dos2unix for converting line endings
RUN apt-get update && apt-get install -y dos2unix && rm -rf /var/lib/apt/lists/*

# Copy gradle wrapper files first
COPY gradlew .
COPY gradlew.bat .
COPY gradle gradle

# Fix line endings and permissions
RUN dos2unix gradlew && chmod +x gradlew

# Copy build files
COPY build.gradle .

# Download dependencies to cache them
RUN ./gradlew dependencies --no-daemon || true

COPY src src

# Build the application (skip tests for faster builds)
RUN ./gradlew build -x test --no-daemon

# Runtime stage
FROM eclipse-temurin:17-jre
WORKDIR /app

# Create a non-root user
RUN groupadd -r appuser && useradd -r -g appuser appuser

# Install curl for healthcheck
RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

# Copy the built JAR from builder stage
COPY --from=builder /app/build/libs/*.jar app.jar

# Set permissions
RUN chown -R appuser:appuser /app

# Switch to non-root user
USER appuser

ENV SPRING_PROFILES_ACTIVE=docker

# Expose port
EXPOSE 8080

# Add healthcheck
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:8080/actuator/health || exit 1

# Use environment variable for JVM settings with defaults
# Java 17 optimized settings
ENV JAVA_OPTS="-Xms2048m -Xmx4096m -XX:+UseG1GC -XX:MaxGCPauseMillis=200"

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]
