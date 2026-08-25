# NEXORA

**NEXORA** is an enterprise-oriented **Data Intelligence Platform** designed to demonstrate modern full-stack enterprise software architecture and engineering practices.

The platform is currently being developed from a strong backend foundation using **Java 21, Spring Boot, PostgreSQL, and Spring Data JPA**, with a target architecture based on **microservices, microfrontends, event-driven communication, data quality processing, analytics, and AI-assisted data intelligence**.

> **Project Status:** Active Development  
> **Current Phase:** Customer Domain Foundation & Automated Testing

---

## Project Vision

NEXORA is designed to evolve beyond a traditional CRUD application into a modular enterprise platform capable of supporting:

- Customer and enterprise data management
- Data quality analysis and scoring
- Dynamic search and filtering
- Analytics and reporting
- Event-driven processing
- AI-assisted data quality and anomaly analysis
- Microservice-based backend architecture
- Microfrontend-based frontend architecture

The project is developed incrementally so that architectural decisions and engineering practices can evolve together with the platform.

---

## Current Tech Stack

### Backend

- Java 21
- Spring Boot 3
- Spring Web
- Spring Data JPA
- Hibernate
- Spring Data JPA Specifications
- Jakarta Bean Validation
- Lombok
- Maven / Maven Wrapper

### Database

- PostgreSQL
- Flyway
- Docker / Docker Compose

### Testing

- JUnit 5
- Mockito
- Maven Surefire

### API & Development Tools

- Postman
- Swagger / OpenAPI
- IntelliJ IDEA
- DBeaver
- Git / GitHub

---

## Target Platform Architecture

NEXORA is being designed toward a distributed full-stack architecture.

```text
                         NEXORA PLATFORM
                                │
              ┌─────────────────┴─────────────────┐
              │                                   │
       MICROFRONTENDS                        MICROSERVICES
     React + TypeScript                    Spring Boot / Java
              │                                   │
       ┌──────┼───────┐                 ┌─────────┼──────────┐
       │      │       │                 │         │          │
   Customer Analytics Data Quality   Customer  Data Quality Analytics
      MFE      MFE       MFE          Service    Service     Service
                                                │
                                                │
                                              Kafka
                                                │
                                                ▼
                                        AI / ML Services
                                             Python
```

The exact service and frontend boundaries will evolve as new domains are implemented.

---

## Current Backend Architecture

The currently implemented Customer domain follows a layered architecture:

```text
HTTP Request
     │
     ▼
Controller
     │
     ▼
Service
     │
     ▼
Repository
     │
     ▼
Spring Data JPA / Hibernate
     │
     ▼
PostgreSQL
```

Supporting components include:

- DTOs for request and response contracts
- Mapper layer for Entity/DTO conversion
- Global exception handling
- Domain-specific exceptions
- Bean Validation
- Spring Data JPA Specifications
- Pagination and sorting
- Flyway migrations
- Automated unit testing

---

# Implemented Features

## Customer Management

- Create customer
- Retrieve customers
- Retrieve customer by ID
- Update customer
- Delete customer
- Duplicate email prevention

---

## Validation & Error Handling

- Jakarta Bean Validation
- Global exception handling
- `CustomerNotFoundException`
- `DuplicateEmailException`
- Standardized API error responses
- Validation error responses
- HTTP status handling including:
    - `400 Bad Request`
    - `404 Not Found`
    - `409 Conflict`
    - `204 No Content`

---

## Query Capabilities

The Customer API supports:

- Pagination
- Sorting
- General search
- Dynamic filtering
- Case-insensitive filtering
- Multiple filter combinations
- Search + pagination + sorting

Dynamic query construction is implemented using:

```text
Spring Data JPA Specification<Customer>
```

This avoids creating separate repository methods for every possible filter combination.

---

# Database & Seed Data

NEXORA uses **PostgreSQL** as its primary relational database.

Database changes are version-controlled using **Flyway**.

Current migrations include:

```text
V1__create_customers_table.sql
V2__insert_customer_seed_data.sql
```

## Realistic Seed Dataset

The development database contains **1,000+ deterministic customer records** designed to support future:

- Reporting
- Data quality analysis
- Pagination testing
- Filtering
- Analytics
- AI/ML experiments

The seed dataset intentionally contains controlled data variations such as:

- Multiple email domains
- Missing phone numbers
- Different phone formats
- Case variations
- Leading/trailing whitespace
- Distributed creation/update dates

This provides a more realistic dataset for future Data Quality and AI modules.

---

# Customer API

| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/api/customers` | Create a customer |
| `GET` | `/api/customers` | Retrieve customers |
| `GET` | `/api/customers/{id}` | Retrieve customer by ID |
| `PUT` | `/api/customers/{id}` | Update customer |
| `DELETE` | `/api/customers/{id}` | Delete customer |

---

## Pagination

```http
GET /api/customers?page=0&size=10
```

## Sorting

```http
GET /api/customers?page=0&size=10&sort=lastName,asc
```

## General Search

```http
GET /api/customers?search=test
```

Search can be combined with pagination and sorting:

```http
GET /api/customers?search=test&page=0&size=10&sort=lastName,asc
```

## Dynamic Filtering

```http
GET /api/customers?firstName=busra
```

```http
GET /api/customers?email=gmail.com
```

Multiple filters can be combined:

```http
GET /api/customers?firstName=busra&email=gmail.com&page=0&size=10
```

Internally, these parameters are converted into dynamic JPA Specifications.

---

# Automated Testing

Automated unit testing is implemented using:

- **JUnit 5**
- **Mockito**

Current test suite status:

```text
Tests run: 18
Failures: 0
Errors: 0
Skipped: 0
```

## Customer Service Tests

The service test suite currently covers:

- Successful customer creation
- Duplicate email handling
- Successful customer retrieval
- Missing customer retrieval
- Successful customer update
- Missing customer update
- Successful customer deletion
- Missing customer deletion
- Pagination
- Empty page handling
- Dynamic filtering

Repository and mapper dependencies are mocked to keep service tests isolated from the database.

---

## Customer Specification Tests

Dynamic filtering logic is tested independently.

Current coverage includes:

### Case-insensitive field filtering

```text
firstName = "Büşra"

→

LOWER(firstName) LIKE '%büşra%'
```

### General search

General search generates an OR expression across:

```text
firstName
OR lastName
OR email
OR phone
```

### Multiple filters

Individual field filters are combined using AND:

```text
firstName LIKE '%büşra%'
AND
email LIKE '%gmail%'
```

### Empty filters

An empty filter request does not introduce unnecessary field predicates.

---

## Running Tests

Run the complete test suite:

```bash
./mvnw test
```

Run the Customer service and specification unit tests:

```bash
./mvnw -Dtest=CustomerServiceImplTest,CustomerSpecificationTest test
```

---

# Error Handling

Example customer-not-found response:

```json
{
  "timestamp": "2026-08-15T12:00:00",
  "status": 404,
  "error": "Not Found",
  "message": "Customer not found with id: 999",
  "path": "/api/customers/999"
}
```

Example validation response:

```json
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

---

# Environment Variables

Database credentials are not stored directly in source code.

`application.properties` uses:

```properties
spring.datasource.password=${DB_PASSWORD}
```

Create a local `.env` file when required:

```env
DB_PASSWORD=your_local_database_password
```

The `.env` file must remain excluded from Git.

---

# Running NEXORA

## Prerequisites

- JDK 21
- Docker
- Git

## Start PostgreSQL

```bash
docker compose up -d
```

## Run the Application

```bash
./mvnw spring-boot:run
```

The backend API runs at:

```text
http://localhost:8080
```

---

# Development Roadmap

## Current / Next

- [x] Customer CRUD
- [x] Request validation
- [x] Global exception handling
- [x] Pagination
- [x] Sorting
- [x] Dynamic filtering with Specification
- [x] PostgreSQL integration
- [x] Flyway migrations
- [x] 1,000+ realistic seed dataset
- [x] Customer Service unit tests
- [x] Customer Specification unit tests
- [x] 18 passing unit tests
- [ ] MockMvc Controller tests
- [ ] Testcontainers integration tests
- [ ] Auditing
- [ ] Soft delete
- [ ] Structured logging

## Platform Evolution

- [ ] Spring Security
- [ ] JWT authentication and authorization
- [ ] Microservice decomposition
- [ ] API Gateway
- [ ] Kafka event-driven communication
- [ ] Data Quality Service
- [ ] Analytics / Reporting Service
- [ ] Data quality rules and scoring
- [ ] Multi-threaded / batch processing
- [ ] React + TypeScript frontend
- [ ] Microfrontend architecture
- [ ] Python AI/ML service
- [ ] AI-assisted data quality analysis
- [ ] Anomaly detection
- [ ] Dockerized platform environment
- [ ] CI/CD with GitHub Actions
- [ ] Observability and monitoring

---

# Development Approach

NEXORA is developed incrementally with an emphasis on:

- Clean and maintainable code
- Domain-oriented architecture
- RESTful API design
- Testability
- Explicit validation and error handling
- Secure configuration
- Database version control
- Reproducible development environments
- Scalable full-stack architecture
- Microservice readiness
- Microfrontend readiness
- Data-driven and AI-assisted capabilities

The current Customer domain serves as the first foundation of the larger NEXORA platform.

---

## License

NEXORA is currently developed as a personal portfolio and software engineering showcase project.