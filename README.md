# Back

Minimal REST application built with Spring Boot 4.1.0 and Java 21.

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

## Test

```shell
./mvnw test
```
