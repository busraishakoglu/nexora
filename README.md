# NEXORA

**NEXORA** is an enterprise-oriented data management platform built with
Java and Spring Boot. The project is designed to demonstrate scalable
REST API development, layered backend architecture, data validation,
database management, and modern enterprise software development
practices.

> **Project Status:** Active development

## Tech Stack

-   Java 21
-   Spring Boot
-   Spring Data JPA
-   PostgreSQL
-   Flyway
-   Maven
-   Docker / Docker Compose
-   Lombok
-   Jakarta Bean Validation
-   Postman
-   Git / GitHub

## Architecture

NEXORA currently follows a layered architecture with clear separation of
responsibilities:

``` text
Controller
    ↓
Service
    ↓
Repository
    ↓
PostgreSQL
```

Supporting components include:

-   DTOs for API request/response models
-   Mapper layer for Entity/DTO conversion
-   Global exception handling
-   Custom exceptions
-   Specification-based dynamic filtering

## Implemented Features

### Customer Management

-   Create customer
-   Retrieve all customers
-   Retrieve customer by ID
-   Update customer
-   Delete customer
-   Duplicate email prevention

### API Validation & Error Handling

-   Bean Validation for request payloads
-   Global exception handling
-   Custom `CustomerNotFoundException`
-   Duplicate email exception handling
-   Standardized validation error responses
-   Appropriate HTTP status codes such as `400`, `404`, `409`, and `204`

### Query Capabilities

-   Pagination
-   Sorting
-   General customer search
-   Dynamic filtering
-   Combined search, pagination, and sorting

### Database & Development

-   PostgreSQL integration
-   Flyway database migrations
-   Docker-based PostgreSQL environment
-   Environment-variable-based database credentials
-   Postman API testing
-   IntelliJ IDEA debug configuration

## Customer API

Method     Endpoint                Description
  ---------- ----------------------- ---------------------------
`POST`     `/api/customers`        Create a new customer
`GET`      `/api/customers`        Retrieve customers
`GET`      `/api/customers/{id}`   Retrieve a customer by ID
`PUT`      `/api/customers/{id}`   Update a customer
`DELETE`   `/api/customers/{id}`   Delete a customer

### Pagination

``` http
GET /api/customers?page=0&size=10
```

### Sorting

``` http
GET /api/customers?page=0&size=10&sort=lastName,asc
```

### Search

``` http
GET /api/customers?search=test
```

Search can be combined with pagination and sorting:

``` http
GET /api/customers?search=test&page=0&size=10&sort=lastName,asc
```

### Dynamic Filtering

The customer query infrastructure supports dynamic filtering using
Spring Data JPA Specifications.

Examples:

``` http
GET /api/customers?firstName=test
```

``` http
GET /api/customers?email=example.com
```

Filters can be combined with pagination and sorting.

## Error Handling

Example response when a customer cannot be found:

``` json
{
  "timestamp": "2026-08-15T12:00:00",
  "status": 404,
  "error": "Not Found",
  "message": "Customer not found with id: 999",
  "path": "/api/customers/999"
}
```

Example validation response:

``` json
{
  "timestamp": "2026-08-15T12:00:00",
  "status": 400,
  "error": "Validation Failed",
  "path": "/api/customers",
  "errors": {
    "firstName": "First name cannot be empty",
    "email": "Email format is invalid"
  }
}
```

## Environment Variables

Database passwords are not stored directly in the source code.

`application.properties` uses:

``` properties
spring.datasource.password=${DB_PASSWORD}
```

Docker Compose also reads the database password from the local
environment.

Create a local `.env` file:

``` env
DB_PASSWORD=your_local_database_password
```

The `.env` file is excluded from Git tracking and must not be committed.

## Running the Project

### Prerequisites

Make sure the following are installed:

-   JDK 21
-   Docker
-   Git

### Start PostgreSQL

From the project root:

``` bash
docker compose up -d
```

### Run the Application

Using the Maven Wrapper:

``` bash
./mvnw spring-boot:run
```

The API is available at:

``` text
http://localhost:8080
```

## Roadmap

The following capabilities are planned as the project evolves:

-   Swagger / OpenAPI documentation
-   Auditing and structured logging
-   Spring Security
-   JWT authentication and authorization
-   Data Quality Engine
-   Data quality rules and scoring
-   Kafka-based event-driven architecture
-   Multi-threaded processing
-   Unit tests with JUnit 5 and Mockito
-   Integration testing with Testcontainers
-   CI/CD pipeline
-   React frontend
-   Python-based AI service integration

## Development Approach

NEXORA is being developed incrementally with an emphasis on:

-   Clean and maintainable code
-   RESTful API design
-   Explicit validation and error handling
-   Secure configuration management
-   Testable backend architecture
-   Scalable enterprise development practices

## License

This project is currently developed as a portfolio project.
