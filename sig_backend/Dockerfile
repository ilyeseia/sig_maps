# Build stage
FROM eclipse-temurin:17-jdk-alpine as builder
WORKDIR /app

# Install required tools
RUN apk add --no-cache bash dos2unix

# Copy gradle wrapper files first
COPY gradlew .
COPY gradlew.bat .
COPY gradle gradle

# Fix line endings and permissions
RUN dos2unix gradlew && chmod +x gradlew

# Copy build files
COPY build.gradle .
COPY settings.gradle ./

# Download dependencies to cache them
RUN ./gradlew dependencies --no-daemon --info || true

# Copy source code
COPY src src

# Build the application (skip tests for faster builds)
RUN ./gradlew build -x test --no-daemon --info

# Runtime stage
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Create a non-root user
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

# Install curl for healthcheck
RUN apk add --no-cache curl

# Copy the built JAR from builder stage
COPY --from=builder /app/build/libs/*.jar app.jar

# Set permissions
RUN chown -R appuser:appgroup /app

# Switch to non-root user
USER appuser

# Environment defaults
ENV SPRING_PROFILES_ACTIVE=docker
ENV JAVA_OPTS="-Xms512m -Xmx1024m -XX:+UseG1GC -XX:MaxGCPauseMillis=200"

# Expose port
EXPOSE 8080

# Add healthcheck
HEALTHCHECK --interval=30s --timeout=10s --start-period=90s --retries=5 \
    CMD curl -f http://localhost:8080/actuator/health || exit 1

# Entry point
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]
