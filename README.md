# multitenancy-microservices

> Spring Boot backend architecture demo for comparing multi-tenancy strategies across microservices.

![Java](https://img.shields.io/badge/runtime-java%2021-111827?style=for-the-badge&labelColor=111827&color=5b5ef4)
![Spring Boot](https://img.shields.io/badge/spring%20boot-3.3.1-111827?style=for-the-badge&labelColor=111827&color=5b5ef4)
![Status](https://img.shields.io/badge/status-sandbox%20demo-111827?style=for-the-badge&labelColor=111827&color=5b5ef4)

[Русская версия](README.ru.md)

| Field | Value |
|---|---|
| Status | Sandbox backend architecture demo |
| Type | Spring Boot microservices / multi-tenancy demo |
| Primary stack | Java 21, Spring Boot 3.3.1, Spring Cloud 2023.0.2, PostgreSQL 15, Consul, Docker Compose |
| Services | `employee-service`, `organization-service`, `device-service`, `tenant-service` |
| API style | REST with tenant-aware headers on domain services |
| Maintenance command | `./gradlew build` |

## Purpose

- Demonstrate several tenant data-isolation approaches in a Spring Boot microservice system.
- Keep service boundaries explicit while using local service discovery and PostgreSQL-backed demo data.
- Preserve a sandbox learning project, not a hardened production deployment.

## Runtime

| Component | Value | Source |
|---|---|---|
| Java | 21 via Gradle toolchain | `*/build.gradle` |
| Gradle wrapper | 8.6 at repository root | `gradle/wrapper/gradle-wrapper.properties` |
| Spring Boot | 3.3.1 | `*/build.gradle` |
| Spring Cloud | 2023.0.2 | `*/build.gradle` |
| Database | PostgreSQL 15 Alpine containers | `docker-compose.yml` |
| Service discovery | HashiCorp Consul 1.10.0 | `docker-compose.yml` |
| Observability | Spring Boot Actuator, Micrometer Prometheus registry | `*/build.gradle`, `*/application.yml` |

## Architecture

The repository contains four Spring Boot services and local infrastructure for tenant-aware backend design:

| Module | Responsibility | Tenant model | Default port |
|---|---|---|---|
| `employee-service` | Employee API and employee data | Database per tenant | `8081` |
| `organization-service` | Organization API and organization data | Schema per tenant | `8082` |
| `device-service` | Device API and demo device data | Tenant column / Hibernate tenant id | `8083` |
| `tenant-service` | Tenant metadata registry | Shared tenant catalog | `8084` |

![Architecture](assets/multitenancy.png)

## Repository layout

- `employee-service/` - employee API, Feign integration, and per-tenant datasource configuration.
- `organization-service/` - organization API, schema-based tenant configuration, and tenant-aware cache coverage.
- `device-service/` - device API, persistence layer, and tenant-column isolation.
- `tenant-service/` - tenant registry API and Flyway-backed tenant metadata storage.
- `consul/` - Consul server and client configuration.
- `init/` - PostgreSQL initialization scripts for demo databases.
- `assets/` - architecture image and supporting repository assets.
- `docker-compose.yml` - local PostgreSQL and Consul stack.

## API surface

| Service | Routes | Tenant requirement |
|---|---|---|
| `employee-service` | `/api/v1/employee`, `/api/v1/employee/{id}` | Requires `X-TenantID` |
| `organization-service` | `/api/v1/organization`, `/api/v1/organization/{id}` | Requires `X-TenantID` |
| `device-service` | `/api/v1/device`, `/api/v1/device/{id}` | Requires `X-TenantID` |
| `tenant-service` | `/api/v1/tenant`, `/api/v1/tenant/{id}` | Tenant registry; no tenant header requirement documented in code |
| All services | `/actuator/health`, `/actuator/info`, `/actuator/metrics`, `/actuator/prometheus` | Actuator routes are exempt from tenant headers |

Missing or blank `X-TenantID` headers on tenant-aware domain routes are rejected with `400 Bad Request`.

## Run locally

1. Copy the environment template and replace demo passwords before sharing or deploying anything derived from it:

```sh
cp .env.example .env
```

2. Start local dependencies:

```sh
docker compose up -d
```

3. Build and test all services with JDK 21:

```sh
export JAVA_HOME=$(/usr/libexec/java_home -v 21) # macOS example
./gradlew build
# If the executable bit is unavailable: sh ./gradlew build
```

The application runtime uses Consul and PostgreSQL from Docker Compose. Test profiles disable Consul/discovery, and tenant isolation tests use Testcontainers where configured.

4. Run a single service against the local stack:

```sh
SPRING_PROFILES_ACTIVE=local ./gradlew :employee-service:bootRun
```

5. Verify individual modules:

```sh
./gradlew :employee-service:test
./gradlew :organization-service:test
./gradlew :device-service:test
./gradlew :tenant-service:test
```

## Local ports

| Component | Port(s) | Notes |
|---|---:|---|
| `employee-service` | `8081` | Spring Boot service |
| `organization-service` | `8082` | Spring Boot service |
| `device-service` | `8083` | Spring Boot service |
| `tenant-service` | `8084` | Spring Boot service |
| Consul UI/API | `8500` | Docker Compose |
| Consul DNS | `8600/tcp`, `8600/udp` | Docker Compose |
| employee tenant 1 DB | `65431` | PostgreSQL container |
| employee tenant 2 DB | `65432` | PostgreSQL container |
| organization DB | `65433` | PostgreSQL container |
| device DB | `65434` | PostgreSQL container |
| tenant DB | `65435` | PostgreSQL container |

## Configuration profiles

- Default profile: shared service defaults such as service name, port, JPA dialect, actuator exposure, and demo-safe environment placeholders.
- `local` profile: local Docker Compose endpoints and overrideable datasource/Consul variables.
- `test` profile: disables Consul/discovery and database health checks for lightweight context and actuator smoke tests. `tenant-service` also disables Flyway in this profile unless a specific integration test overrides it.

Common local variables:

- `POSTGRES_PASSWORD` - Docker Compose PostgreSQL password.
- `DB_PASSWORD` - shared demo database password used by services when service-specific overrides are absent.
- `CONSUL_HOST`, `CONSUL_PORT` - local Consul endpoint overrides.
- Service-specific overrides such as `EMPLOYEE_TENANT_1_DB_URL`, `ORGANIZATION_DB_URL`, `DEVICE_DB_URL`, and `TENANT_DB_URL` are available in each `application-local.yml`.

## Tenant routing and isolation tests

Tenant isolation is covered by integration and controller tests:

- `employee-service` verifies database-per-tenant routing with Testcontainers and fails closed for unknown tenants instead of falling back to the default datasource.
- `organization-service` verifies schema-per-tenant isolation with Testcontainers and includes a tenant-aware cache regression test.
- `device-service` verifies tenant-column isolation.
- `employee-service`, `organization-service`, and `device-service` controller tests verify tenant header validation on API routes.
## Observability

Each service exposes a minimal Spring Boot Actuator baseline:

- `/actuator/health` and probe groups for health checks.
- `/actuator/info` for service metadata.
- `/actuator/metrics` for Micrometer metrics.
- `/actuator/prometheus` for Prometheus scraping.

The exposed actuator set is controlled by `ACTUATOR_ENDPOINTS` and defaults to `health,info,metrics,prometheus`.
Each service also logs one structured `http_request` line per request with method, URI, status, and duration.

## Limitations / security

- Sandbox project only; no authentication/authorization layer is documented for public production use.
- Demo passwords are placeholders and must be replaced for any real environment.
- Tenant isolation patterns are intentionally mixed for comparison, not a single recommended production blueprint.
- Review database migrations, tenant provisioning, observability, and auth before adapting this code outside a local demo.

## Status

Backend architecture demo. The repository is public as a reference implementation for service boundaries, runtime wiring, and configuration hygiene; it is not maintained as a production-ready product.
