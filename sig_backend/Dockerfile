# Build stage
FROM eclipse-temurin:17-jdk-alpine as builder
WORKDIR /app

# Install required tools
RUN apk add --no-cache bash dos2unix curl wget

# Copy gradle wrapper files first
COPY gradlew .
COPY gradlew.bat .
COPY gradle gradle

# Fix line endings and permissions
RUN dos2unix gradlew && chmod +x gradlew

# Copy build files
COPY build.gradle .
COPY settings.gradle ./

# Download JAI libraries manually
RUN mkdir -p /root/.m2/repository/javax/media/jai_core/1.1.3 && \
    wget -q -O /root/.m2/repository/javax/media/jai_core/1.1.3/jai_core-1.1.3.jar \
        "https://repo.osgeo.org/repository/release/javax/media/jai_core/1.1.3/jai_core-1.1.3.jar" || \
    echo "JAI not downloaded, continuing..." && \
    echo '<?xml version="1.0"?><project><modelVersion>4.0.0</modelVersion><groupId>javax.media</groupId><artifactId>jai_core</artifactId><version>1.1.3</version></project>' > /root/.m2/repository/javax/media/jai_core/1.1.3/jai_core-1.1.3.pom

# Download dependencies
RUN ./gradlew dependencies --no-daemon || true

# Copy source code
COPY src src

# Build the application
RUN ./gradlew build -x test --no-daemon || ./gradlew bootJar -x test --no-daemon

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
ENV JAVA_OPTS="-Xms512m -Xmx1024m -XX:+UseG1GC"

# Expose port
EXPOSE 8080

# Add healthcheck
HEALTHCHECK --interval=30s --timeout=10s --start-period=90s --retries=5 \
    CMD curl -f http://localhost:8080/actuator/health || exit 1

# Entry point
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]
