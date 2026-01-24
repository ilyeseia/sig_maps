# Sig-Backend Project Audit Report

**Date:** January 21, 2026
**Auditor:** Antigravity (AI Assistant)
**Scope:** Dependencies (`build.gradle`), Configuration, and Docker

---

## 1. Executive Summary
The "sig-backend" is a **Spring Boot 2.7.18** application using Java 8. While robust, it contained several outdated dependencies that posed security or compatibility risks. The audit focused on upgrading these specific libraries and ensuring the Docker runtime environment is explicitly configured for the deployment pipeline.

**Key Actions Taken:**
1.  **Security Update:** Upgraded `commons-io` from 1.3.2 to 2.11.0 to address multiple historical vulnerabilities.
2.  **Compatibility Tuning:** Updated `hibernate-spatial` to 5.6.15.Final to better align with Spring Boot 2.7 and modern PostGIS dialects.
3.  **Docker Config:** Added default `SPRING_PROFILES_ACTIVE=docker` to the Dockerfile to ensure correct property loading in containers.

---

## 2. Dependency Audit & Updates

| Package | Old Version | New Version | Reason / Notes |
| :--- | :--- | :--- | :--- |
| **commons-io** | `1.3.2` | `2.11.0` | **Critical Security**. Old version had directory traversal vulnerabilities. |
| **hibernate-spatial** | `5.4.8.Final` | `5.6.15.Final` | Improved compatibility with Spring Boot 2.7 JPA/Hibernate managed versions. |
| **Spring Boot** | `2.7.18` | `2.7.18` | Maintained at last stable 2.x release (Upgrade to 3.x requires Java 17). |
| **Java** | `1.8` | `1.8` | Maintained legacy Java 8 support (as per Dockerfile base). |

> **Note on Java Version:** The project is locked to Java 8. Migrating to Spring Boot 3.0+ will require a mandatory upgrade to **Java 17**.

---

## 3. Configuration Improvements

### Dockerfile
-   **Active Profile:** Added `ENV SPRING_PROFILES_ACTIVE=docker`. This ensures that even if `docker-compose` doesn't pass the variable (or during standalone runs), the application correctly loads `application-docker.properties` instead of trying to connect to `localhost` databases.

### build.gradle
-   **Structure:** The file structure is standard. Repositories include OSGeo, which is necessary for GeoTools.

---

## 4. Production Readiness & Maintenance

### Immediate Next Steps
1.  **Verify Backend Start:**
    ```bash
    docker-compose up -d --build backend
    ```
2.  **Check Logs:** Ensure no `ClassNotFoundException` errors related to Hibernate or GeoTools occur during startup.

### Long-Term Strategy
**Migration to Java 17 & Spring Boot 3**
-   **Why:** Spring Boot 2.x is EOL (OSS support ended Nov 2023).
-   **Path:**
    1.  Upgrade JDK to 17 in `Dockerfile` and local env.
    2.  Update `build.gradle` to Spring Boot 3.x.
    3.  Upgrade `javax.*` imports to `jakarta.*` (automated tools like OpenRewrite can help).
    4.  Update GeoTools to version 28+ for Java 17 support.

---

## 5. Security Recommendations
-   Monitor the `geotools` and `jts` libraries, as geospatial libraries often have infrequent but critical CVE updates.
-   Ensure the `application-docker.properties` passwords are injected via environment variables in production (which is already correctly set up with `${POSTGRES_PASSWORD:...}` fallbacks).
