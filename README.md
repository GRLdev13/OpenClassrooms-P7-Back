# Back

Minimal REST application built with Spring Boot 4.1.0 and Java 21.

Hibernate is configured through Spring Data JPA. The application uses
PostgreSQL at runtime, Flyway for schema migrations, and H2 only in tests.

## Requirements

- Java 21 or newer
- Maven 3.6.3 or newer, or the included Maven wrapper
- PostgreSQL 18, or Docker with Compose

## Run

The default local connection is:

- URL: `jdbc:postgresql://localhost:5432/your_car_your_way`
- Username: `postgres`
- Password: `postgres`

Start the provided PostgreSQL container, then launch the application:

```powershell
docker compose up -d
.\mvnw.cmd spring-boot:run
```

If PostgreSQL is already installed locally, create the `your_car_your_way`
database and provide your actual credentials through environment variables:

```powershell
$env:DB_URL = "jdbc:postgresql://localhost:5432/your_car_your_way"
$env:DB_USERNAME = "postgres"
$env:DB_PASSWORD = "your-password"
.\mvnw.cmd spring-boot:run
```

On macOS or Linux, use the equivalent environment variables and run:

```shell
./mvnw spring-boot:run
```

Then open `http://localhost:8080/`. The application redirects to Swagger UI,
where the API can be explored and called interactively.

- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`

## Persistence

JPA entities under `com.example.back` are detected automatically. The `domain`
package contains the entities and relationships from the project data model:
clients, admins, roles, agencies, vehicles, agency vehicle assignments, rentals,
bills, conversations, and messages. Database connection and Hibernate schema
settings are in `src/main/resources/application.properties`.

Flyway automatically applies versioned SQL files from
`src/main/resources/db/migration`. Hibernate uses `ddl-auto=validate`, so it
checks entity-to-schema compatibility without modifying the schema. The first
migration creates all tables, keys, relationships, and indexes from the data
model.

The PostgreSQL JDBC driver and connection pool are managed by Spring Boot. In a
deployed environment, set `DB_URL`, `DB_USERNAME`, and `DB_PASSWORD` outside the
repository rather than storing credentials in source control.

## User CRUD API

The client/user API is available under `/api/users`:

- `GET /api/users` lists active users.
- `GET /api/users/{id}` returns one active user.
- `POST /api/users` creates a user.
- `PUT /api/users/{id}` updates a user.
- `DELETE /api/users/{id}` soft-deletes a user.
- `POST /api/users/login` validates an active user's email and password. Its
  token is empty until token generation is implemented.

Create and update payloads are validated. Passwords are BCrypt-hashed before
storage and are never included in API responses.

## Conversation API

- `POST /api/conversations` creates an `OPEN` conversation between an active
  client and an active admin whose IDs are different.
- `GET /api/conversations/{id}` returns a conversation and its linked messages,
  ordered chronologically by creation date.
- `GET /api/conversations/client/{clientId}` returns every conversation belonging
  to a client, with the linked messages for each conversation.
- `POST /api/conversations/{conversationId}/messages` creates a message in the
  selected conversation from a `MessageDto` request body.

## Test

```shell
./mvnw test
```
