# Hotels API

A RESTful API for managing hotels: listing/detailed info, search by parameters,
creating hotels, managing amenities, and retrieving statistical histograms.

## Tech Stack

- Java 21
- Spring Boot 4.1.1 (Spring Web MVC, Spring Data JPA)
- Liquibase — database schema management
- H2 — default in-memory database
- MapStruct — Entity ↔ DTO mapping
- springdoc-openapi — Swagger UI / OpenAPI 3
- JUnit 5, Mockito, MockMvc — tests
- Maven

## Requirements

- JDK 21+
- Maven 3.9+ (or use the bundled `./mvnw`)

## Running the application

```bash
mvn spring-boot:run
```

The application starts on port **8092**. All endpoints are available under the
`/property-view` prefix, e.g. `http://localhost:8092/property-view/hotels`.

## Swagger / OpenAPI

Once the app is running, documentation is available at:

- Swagger UI: http://localhost:8092/swagger-ui.html
- OpenAPI JSON: http://localhost:8092/v3/api-docs

## H2 Console

- URL: http://localhost:8092/h2-console
- JDBC URL: `jdbc:h2:mem:testdb`
- User: `sa`
- Password: (empty)

## Tests

```bash
mvn test
```

Coverage includes unit tests for the service layer (`HotelServiceImplTest`) and
integration tests for the controller via `MockMvc` (`HotelControllerTest`), running
against a real H2 database with Liquibase migrations applied.

## API

Base prefix for all endpoints: `/property-view`

### `GET /hotels`

Returns a list of all hotels with brief information. Supports pagination and sorting
via standard Spring Data parameters (`page`, `size`, `sort`); defaults to `size=30`,
sorted by `name`.

```json
[
  {
    "id": 1,
    "name": "DoubleTree by Hilton Minsk",
    "description": "The DoubleTree by Hilton Hotel Minsk offers 193 luxurious rooms ...",
    "address": "9 Pobediteley Avenue, Minsk, 220004, Belarus",
    "phone": "+375 17 309-80-00"
  }
]
```

### `GET /hotels/{id}`

Returns detailed information about a specific hotel.

```json
{
  "id": 1,
  "name": "DoubleTree by Hilton Minsk",
  "description": "The DoubleTree by Hilton Hotel Minsk offers 193 luxurious rooms ...",
  "brand": "Hilton",
  "address": {
    "houseNumber": "9",
    "street": "Pobediteley Avenue",
    "city": "Minsk",
    "country": "Belarus",
    "postcode": "220004"
  },
  "contacts": {
    "phone": "+375 17 309-80-00",
    "email": "doubletreeminsk.info@hilton.com"
  },
  "arrivalTime": {
    "checkIn": "14:00",
    "checkOut": "12:00"
  },
  "amenities": ["Free parking", "Free WiFi", "Non-smoking rooms"]
}
```

Returns `404 Not Found` if a hotel with the given `id` doesn't exist.

### `GET /search`

Searches hotels by parameters: `name`, `brand`, `city`, `country`, `amenities`.
All parameters are optional and combined with `AND`. The `amenities` filter can be
passed multiple times — a hotel must have **all** of the listed amenities to match.

```
GET /property-view/search?city=minsk
GET /property-view/search?amenities=Free WiFi&amenities=Pool
```

Response format is the same as `GET /hotels`.

### `POST /hotels`

Creates a new hotel.

```json
{
  "name": "DoubleTree by Hilton Minsk",
  "description": "The DoubleTree by Hilton Hotel Minsk offers 193 luxurious rooms ...",
  "brand": "Hilton",
  "address": {
    "houseNumber": "9",
    "street": "Pobediteley Avenue",
    "city": "Minsk",
    "country": "Belarus",
    "postcode": "220004"
  },
  "contacts": {
    "phone": "+375 17 309-80-00",
    "email": "doubletreeminsk.info@hilton.com"
  },
  "arrivalTime": {
    "checkIn": "14:00",
    "checkOut": "12:00"
  }
}
```

`description` and `arrivalTime.checkOut` are optional. The response (`201 Created`)
is the brief hotel info in the same format as `GET /hotels`. Validation errors return
`400 Bad Request` with per-field details.

### `POST /hotels/{id}/amenities`

Adds a list of amenities to a hotel (existing ones are kept, new ones are created).

```json
["Free parking", "Free WiFi", "Non-smoking rooms"]
```

Returns `200 OK` on success, `404 Not Found` if the hotel doesn't exist.

### `GET /histogram/{param}`

Returns the number of hotels grouped by the given parameter's value. Allowed values
for `param`: `brand`, `city`, `country`, `amenities`.

```
GET /property-view/histogram/city
```

```json
{
  "Minsk": 1,
  "Moscow": 2,
  "Mogilev": 1
}
```

Returns `400 Bad Request` for an unknown parameter.

## Architecture

The project is organized into layers:

- `controller` — REST controllers
- `service` / `service.impl` — business logic
- `repository` — Spring Data JPA repositories + `Specification` for dynamic search
- `model` — JPA entities
- `dto` (`request` / `response`) — request/response DTOs
- `mapper` — MapStruct mappers between entities and DTOs
- `exception` / `exception.handler` — domain exceptions and a global error handler (`@RestControllerAdvice`, `ProblemDetail`)
- `specification` — dynamic JPA `Specification` builders for `/search`

The database schema is version-controlled via Liquibase (`src/main/resources/db/changelog`).
