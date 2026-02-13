# Build stage
FROM eclipse-temurin:17-jdk-alpine as builder
WORKDIR /app

# Install required tools including wget for downloading JAI
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

# Download JAI libraries manually and install to local Maven repository
# This is needed because javax.media:jai_core is not available in Maven Central
RUN mkdir -p /root/.m2/repository/javax/media/jai_core/1.1.3 && \
    mkdir -p /root/.m2/repository/javax/media/jai_codec/1.1.3 && \
    echo "Downloading JAI core..." && \
    (wget -q -O /root/.m2/repository/javax/media/jai_core/1.1.3/jai_core-1.1.3.jar \
        "https://repo.osgeo.org/repository/release/javax/media/jai_core/1.1.3/jai_core-1.1.3.jar" || \
     wget -q -O /root/.m2/repository/javax/media/jai_core/1.1.3/jai_core-1.1.3.jar \
        "https://build.geoserver.org/geoserver/main/jai_core-1.1.3.jar" || \
     echo "JAI core download failed, continuing with exclusion strategy") && \
    echo '<?xml version="1.0" encoding="UTF-8"?><project xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd" xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"><modelVersion>4.0.0</modelVersion><groupId>javax.media</groupId><artifactId>jai_core</artifactId><version>1.1.3</version></project>' > /root/.m2/repository/javax/media/jai_core/1.1.3/jai_core-1.1.3.pom

# Download dependencies to cache them (allow failure as we have exclusions)
RUN ./gradlew dependencies --no-daemon || true

# Copy source code
COPY src src

# Build the application (skip tests for faster builds)
RUN ./gradlew build -x test --no-daemon || \
    ./gradlew bootJar -x test --no-daemon

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
