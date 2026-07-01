# Vizorix API Application (`apps/api`)

## Purpose
This application serves as the backend orchestrator and API gateway for the Vizorix platform.

---

## Current Capabilities
*   ✔ Application starts
*   ✔ Profiles (`local`, `dev`, `prod`)
*   ✔ Logging (Structured logback rotation)
*   ✔ Actuator health checks
*   ✔ Swagger UI (Enabled in `local` and `dev`, Disabled in `prod`)
*   ✔ Flyway configured
*   ✔ PostgreSQL configured
*   ✘ Authentication
*   ✘ Business APIs
*   ✘ Database schema
*   ✘ Execution Engine

---

## Milestone 2 Capabilities Details
*   **Spring Boot Framework**: Running on Spring Boot 3.3.1 with Java 21 compile configuration.
*   **Profiles Orchestration**: Separate profile properties configured for:
    *   `local`: Fallback properties for local development database targets.
    *   `dev`: Injected configurations for shared staging sandboxes.
    *   `prod`: Hardened configurations for production servers.
*   **OpenAPI Documentation**: Automatic Swagger schema index served via http://localhost:8080/swagger-ui/index.html (disabled in `prod`).
*   **Database Migrations**: Flyway migration schemas configuration with PostgreSQL driver.
*   **Quality Controls**: Spotless formatting and Checkstyle validation rules integrated directly into the Maven build.
*   **Structured Telemetry**: Exposes Actuator health and telemetry data at `http://localhost:8080/actuator/health`.

---

## Directory Structure

```text
apps/api/
├── .mvn/wrapper/               # Maven wrapper bin files
├── src/
│   ├── main/
│   │   ├── java/com/vizorix/api/
│   │   │   ├── config/         # OpenApi and ApiProperties classes
│   │   │   └── ApiApplication.java
│   │   └── resources/
│   │       ├── db/migration/   # Future database SQL migrations location
│   │       ├── application.yml
│   │       ├── application-local.yml
│   │       ├── application-dev.yml
│   │       ├── application-prod.yml
│   │       ├── logback-spring.xml
│   │       └── banner.txt
│   └── test/
│       └── java/com/vizorix/api/ApiApplicationTests.java
├── checkstyle.xml              # Coding syntax rules
├── mvnw                        # Linux Maven wrapper launch script
├── mvnw.cmd                    # Windows Maven wrapper launch script
└── pom.xml                     # Maven project builder configuration
```

---

## Running the Application Locally

1.  **Boot DB dependencies**: Make sure your local PostgreSQL database is running on port `5432` with a database named `vizorix`, username `postgres`, and password `postgrespassword` (or pass custom values via `DB_URL`, `DB_USERNAME`, `DB_PASSWORD` variables).
2.  **Launch Server**:
    ```bash
    # Windows
    .\mvnw.cmd spring-boot:run
    
    # Unix
    ./mvnw spring-boot:run
    ```

---

## Code Style & Format Verification
The Maven compiler is configured to run spotless formatting checks and checkstyle code validations on verification phases:
```bash
# Run validation checks and compiles tests
./mvnw clean verify

# Auto-apply spotless formatting rules to source files
./mvnw spotless:apply
```
