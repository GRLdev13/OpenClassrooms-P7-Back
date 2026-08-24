# Back

Minimal REST application built with Spring Boot 4.1.0 and Java 21.

Hibernate is configured through Spring Data JPA. Local development uses an
in-memory H2 database, so its data is discarded when the application stops.

## Requirements

- Java 21 or newer
- Maven 3.6.3 or newer, or the included Maven wrapper

## Run

```shell
./mvnw spring-boot:run
```

On Windows PowerShell:

```powershell
.\mvnw.cmd spring-boot:run
```

Then open `http://localhost:8080/`. The endpoint returns:

```json
{"message":"Spring Boot is running"}
```

## Persistence

JPA entities under `com.example.back` are detected automatically. `Note` and
`NoteRepository` provide a minimal persistence example. Database connection and
Hibernate schema settings are in `src/main/resources/application.properties`.

For production, replace H2 with your database driver and connection settings,
set `spring.jpa.hibernate.ddl-auto=validate`, and manage schema changes with a
migration tool such as Flyway or Liquibase.

## Test

```shell
./mvnw test
```
