# multitenancy-microservices

[English version](README.md)

Демонстрационный backend-проект для изучения multi-tenancy patterns на Spring Boot microservices. Sandbox-проект, не production deployment.

## Назначение проекта

- Backend architecture demo для сравнения подходов к multi-tenancy в микросервисной системе.
- Фокус на границах Spring Boot сервисов, service discovery и изоляции tenant-данных в PostgreSQL.
- Sandbox-проект, не поддерживаемый продукт и не hardened production deployment.

## Технологический стек

- Java 21 (Gradle toolchain)
- Spring Boot
- Spring Cloud
- Consul для service discovery
- PostgreSQL
- Docker Compose

## Архитектура

Репозиторий содержит четыре Spring Boot microservices и локальную инфраструктуру для экспериментов с tenant-aware backend design:

- `employee-service` - хранит employee data в отдельных PostgreSQL databases для каждого tenant.
- `organization-service` - управляет organization data с tenant-specific schemas.
- `device-service` - управляет device data для demo domain.
- `tenant-service` - хранит tenant metadata, используемые системой.
- Consul обеспечивает local service discovery для microservices.
- Docker Compose запускает Consul и PostgreSQL containers для локального sandbox environment.

![Architecture](assets/multitenancy.png)

## Структура репозитория

- `employee-service/` - employee API и per-tenant datasource configuration.
- `organization-service/` - organization API и schema-based tenant configuration.
- `device-service/` - device API и persistence layer.
- `tenant-service/` - tenant registry API и persistence layer.
- `consul/` - конфигурация Consul server и client.
- `init/` - PostgreSQL initialization scripts для demo databases.
- `assets/` - architecture image и supporting repository assets.
- `docker-compose.yml` - локальный PostgreSQL и Consul stack.

## Быстрый старт

1. Скопируйте шаблон переменных окружения:

```shell
cp .env.example .env
```

2. Установите `POSTGRES_PASSWORD` и `DB_PASSWORD` в `.env`. Полный список переменных -- в `.env.example`.

3. Запустите локальные зависимости:

```shell
docker compose up -d
```

4. Соберите все сервисы. Запускайте Gradle под JDK 21; Gradle 8.x не запускается на Java 25:

```shell
export JAVA_HOME=$(/usr/libexec/java_home -v 21) # пример для macOS
./gradlew build
# Если executable bit недоступен: sh ./gradlew build
```

Для сборки требуется Java 21 и запущенный Consul для integration tests.

5. Проверьте отдельный модуль:

```shell
export JAVA_HOME=$(/usr/libexec/java_home -v 21) # пример для macOS
./gradlew :employee-service:test
./gradlew :organization-service:test
./gradlew :device-service:test
./gradlew :tenant-service:test
```

## Профили конфигурации

- Default profile: общие настройки сервисов -- service name, port, JPA dialect, actuator exposure и demo-safe environment placeholders.
- `local` profile: endpoints локального Docker Compose и переопределяемые datasource/Consul variables. Используйте его при запуске сервисов против локального stack:

```shell
SPRING_PROFILES_ACTIVE=local ./gradlew :employee-service:bootRun
```

- `test` profile: отключает Consul/discovery и database health checks для lightweight context и actuator smoke tests. В `tenant-service` этот профиль также отключает Flyway, если конкретный integration test не переопределяет это явно.

Основные локальные переменные:

- `POSTGRES_PASSWORD` -- пароль PostgreSQL для Docker Compose.
- `DB_PASSWORD` -- общий demo database password для сервисов, если service-specific overrides не заданы.
- `CONSUL_HOST`, `CONSUL_PORT` -- переопределения local Consul endpoint.
- Service-specific overrides вроде `EMPLOYEE_TENANT_1_DB_URL`, `ORGANIZATION_DB_URL`, `DEVICE_DB_URL` и `TENANT_DB_URL` доступны в соответствующих `application-local.yml`.

## Tenant routing и isolation tests

Tenant-aware API routes требуют header `X-TenantID`. Missing или blank tenant headers отклоняются с `400 Bad Request`. Actuator endpoints под `/actuator/**` исключены, чтобы health checks и metrics не требовали tenant context.

Tenant isolation покрыта integration и controller tests:

- `employee-service` проверяет database-per-tenant routing через Testcontainers и fail-closed поведение для unknown tenants без fallback на default datasource.
- `organization-service` проверяет schema-per-tenant isolation через Testcontainers и содержит regression test для tenant-aware cache.
- `employee-service`, `organization-service` и `device-service` controller tests проверяют tenant header validation на API routes.

## Observability

Каждый сервис включает минимальный Spring Boot Actuator baseline:

- `/actuator/health` и probe groups для health checks.
- `/actuator/info` для service metadata.
- `/actuator/metrics` для Micrometer metrics.
- `/actuator/prometheus` для Prometheus scraping.

Набор exposed actuator endpoints управляется через `ACTUATOR_ENDPOINTS` и по умолчанию равен `health,info,metrics,prometheus`.
Также каждый сервис пишет одну structured log line `http_request` на request: method, URI, status и duration.

## Статус

Только backend architecture demo. Репозиторий является sandbox для изучения multi-tenancy patterns и не позиционируется как поддерживаемый продукт.
