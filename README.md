# MySQLCommunicationRepo

A lightweight library/demo that showcases how to build repository and service layers for MySQL communication using Spring (without Spring Boot), JDBI 3, and transactionality. The project emphasizes clear separation of service logic from data access and offers integration testing with Testcontainers.

## Key Features

- Spring Core/Context for component management (no Spring Boot).
- JDBI 3 for data access (a lightweight alternative to JPA/Hibernate).
- MySQL Connector/J as the database driver.
- Logging via SLF4J + Log4j2.
- Lombok to reduce boilerplate.
- Integration tests powered by Testcontainers (MySQL) and jdbi3-testing.
- Unit tests with JUnit 5 + Mockito + AssertJ.

## Tech Stack

- Java 21, Maven
- Spring Core 6.x, Spring Context 6.x
- JDBI 3.49.x
- MySQL Connector/J 8.0.x
- SLF4J 2.x + Log4j2
- Lombok
- Guava, Evo-Inflector (utility helpers)
- JUnit 5, Mockito, Testcontainers (test profile)

## Prerequisites

- Java 21 (JDK)
- Maven 3.9+
- Docker (if you want to run integration tests with Testcontainers – recommended)

## Build and Test

- Build the project:
  ```bash
  mvn -q -U -e clean package
  ```
- Run tests:
  ```bash
  mvn -q -e test
  ```
  Testcontainers will automatically start a MySQL container — no local MySQL installation required.

Note: The project builds a JAR but is not a standalone runnable application (no main). Treat it as a module/library to be included in a larger application or run logic through tests.

## Database Configuration (Development)

Access to the database is provided via JDBI. How you supply connection parameters depends on your Spring configuration (e.g., JavaConfig or properties). Typical parameters:

- URL: `jdbc:mysql://host:port/database_name?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC`
- User/password: `db_user` / `db_password`

Example environment variables you can wire into your configuration:

- `DB_URL=jdbc:mysql://localhost:3306/app_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC`
- `DB_USER=app_user`
- `DB_PASS=secret`

If you prefer not to configure a local database, rely on the integration tests (Testcontainers) to run “live” scenarios against MySQL in Docker.

## Architecture and Patterns

- Service layer (Spring Service) encapsulates use-case logic.
- Repository layer built on JDBI performs SQL queries and result mapping.
- Transactionality “all-or-nothing” for operations spanning multiple tables/repository methods.
- Centralized logging via SLF4J/Log4j2.

An example use case includes inserting related entities (e.g., player + team) both in a “plain” two-step approach and in a transactional variant (recommended).

## Testing

- Unit: Mockito + JUnit 5 + AssertJ.
- Integration: Testcontainers (MySQL) + jdbi3-testing. Tests spin up a MySQL container, apply migrations/seed data if configured in tests, and verify repository/service behavior against a real database.

Commands:
- Run all tests:
  ```bash
  mvn test
  ```
- Increase log verbosity (e.g., to debug JDBI/SQL): configure Log4j2 levels in your logging config files (in test or main resources).

## Quality, Style, and Logging

- Lombok is used (e.g., builders, RequiredArgsConstructor).
- Logging centralized with SLF4J implemented by Log4j2.
- Recommended dev log level: INFO; for SQL diagnostics, set DEBUG on JDBI/SQL packages.

## How to Use This Library in Your App

- Add this module as a Maven dependency (local or from your internal repository).
- Provide Spring configuration (JavaConfig) to create:
  - A DataSource pointing to your MySQL instance.
  - A Jdbi factory and component scanning for repositories/services.
- Inject services into your application layer and call use-case methods.

## Troubleshooting

- Integration tests do not start:
  - Ensure Docker is running and you have permissions to start containers.
- Lombok types missing during compilation:
  - Ensure your IDE has Lombok plugin installed and annotation processing enabled.


Have questions or want this README tailored to your environment? Open an issue or start a discussion.
```

