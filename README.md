# Banking Microservices

A personal banking simulation application built using **Java 21, Spring
Boot, and a microservices architecture**. The project focuses on backend
engineering concepts such as secure authentication, account management,
transaction processing, service communication, and data consistency.

The application is being developed with an emphasis on clean code,
maintainability, security, and production-oriented practices.

## Overview

The application simulates common banking operations through
independently developed microservices. It supports different user roles
and provides workflows for account registration, approval, activation,
money transfers, and administrative operations.

The main goal of this project is to apply backend development concepts
in a practical application, particularly around microservices, security,
database transactions, and reliable communication between services.

## Features

### Authentication and User Management

-   User registration with email OTP verification.
-   JWT-based authentication and authorization.
-   Role-based access control for User, Admin, and Super Admin.
-   Account registration and admin approval workflow.
-   Password setup and management.
-   Refresh token handling using HttpOnly cookies.
-   User profile management.

### Banking Operations

-   Savings account creation and activation.
-   PIN setup and validation.
-   Money transfers between accounts.
-   Balance enquiry and transaction history.
-   Transaction amount and frequency limits.
-   Account status validation and blocking after repeated incorrect PIN
    attempts.
-   Admin-initiated deposits.

### Administrative Operations

-   Admin approval or rejection of account registration requests.
-   User management and filtering.
-   Viewing user and transaction information.
-   Branch management.
-   Super Admin operations for creating admins and managing branches.

## Architecture

The application is divided into the following microservices:

| Service | Responsibility |
|---|---|
| *API Gateway* | Entry point for client requests, JWT validation, and request routing. |
| *Eureka Server* | Service registration and discovery. |
| *Authentication Service* | Login, registration workflows, authentication, and token management. |
| *OTP Service* | OTP generation, verification, storage, and rate limiting. |
| *User Profile Service* | User profile and user-related operations. |
| *Branch Service* | Branch information and management. |
| *Banking Service* | Bank accounts, deposits, money transfers, and transaction history. |
| *Notification Service* | Handling application notifications. |
| *Common Library* | Shared components, DTOs, and reusable application code. |
  -----------------------------------------------------------------------

### Request Flow

Client requests enter through the API Gateway, which validates JWTs and
routes requests to the appropriate service. Eureka provides service
discovery, while OpenFeign is used for inter-service communication.

The Banking Service handles account and transaction operations together
to maintain consistency when performing financial operations.

## Technical Stack

| Category | Technologies |
|---|---|
| **Language** | Java 21 |
| **Framework** | Spring Boot 3.5.x |
| **Microservices** | Spring Cloud |
| **API Gateway** | Spring Cloud Gateway |
| **Service Discovery** | Netflix Eureka |
| **Security** | Spring Security, JWT |
| **ORM** | Spring Data JPA, Hibernate |
| **Database** | PostgreSQL |
| **Caching and Rate Limiting** | Redis |
| **Messaging** | Apache Kafka |
| **Service Communication** | OpenFeign |
| **Object Mapping** | MapStruct |
| **Build Tool** | Maven |
| **Testing** | JUnit 5, Mockito |
| **Containerization** | Docker Compose (local infrastructure) |

## Security and Transaction Management

Security and data consistency are important parts of this project.

-   JWT validation is handled at the API Gateway.
-   The Gateway propagates authenticated user information to downstream
    services through trusted request headers.
-   Role-based authorization is implemented using Spring Security.
-   Refresh tokens are stored in HttpOnly cookies.
-   OTP operations include Redis-based rate limiting.
-   Passwords and account PINs are stored using password hashing.
-   Money transfers are handled within database transactions.
-   Pessimistic locking is used when accessing accounts during
    transfers.
-   Accounts are locked in a consistent order to reduce the possibility
    of deadlocks.
-   Sender and receiver account status, available balance, and
    transaction limits are validated before completing transfers.

The Banking Service keeps account and transaction operations together so
that related balance updates and transaction records can be handled
within the same database transaction.

## Project Structure

Each service is maintained as a separate Maven project.

``` text
bank-app-microservices/
│
├── authentication/
├── banking/
├── branch/
├── common-library/
├── eureka-server/
├── gateway/
├── notification/
├── otp/
└── user-profile/
```

The `common-library` module contains reusable components shared across
services.

## Getting Started

### Prerequisites

Make sure the following are installed:

-   Java 21
-   Maven
-   PostgreSQL
-   Redis
-   Apache Kafka
-   Docker and Docker Compose (for running local infrastructure)

### 1. Clone the repository

``` bash
git clone https://github.com/adnankppro/bank-app-microservices.git

cd bank-app-microservices
```

### 2. Start infrastructure services

Start PostgreSQL, Redis, and Kafka using the provided Docker Compose
configuration.

Run the command from the directory containing your Compose file:

``` bash
docker compose up -d
```

### 3. Configure the services

Before starting the application, configure the required properties for
each service.

This includes:

-   PostgreSQL database connection details.
-   Redis connection details.
-   Kafka bootstrap server configuration.
-   JWT and other security-related configuration.
-   Inter-service communication settings.

Use your local environment or application configuration files for these
values. Do not commit credentials, secrets, or production configuration
to the repository.

### 4. Build and install the Common Library

The common library must be installed in your local Maven repository
before building or running services that depend on it.

``` bash
cd common-library

mvn clean install
```

### 5. Start the application

Start the services in the following order:

1.  Eureka Server
2.  API Gateway
3.  Authentication Service
4.  OTP Service
5.  User Profile Service
6.  Branch Service
7.  Banking Service
8.  Notification Service

Each service can be started independently from its own module directory:

``` bash
mvn spring-boot:run
```

Alternatively, import the individual Maven projects into your IDE and
run their Spring Boot application classes.

**Note:** Ensure that the required infrastructure services are running
and that each service's configuration is correct before starting the
application.

## Testing

Testing is currently in progress.

The project uses JUnit 5 and Mockito for unit testing. Integration
testing with PostgreSQL and concurrency testing are also being explored
to verify database interactions, transaction behaviour, and simultaneous
account operations.

The testing scope includes:

-   Business logic and validation.
-   Transaction limits and PIN validation.
-   Account balance updates.
-   Transaction consistency and rollback behaviour.
-   Database interactions and concurrency scenarios.

## Current Development Status

The core authentication, user management, account management, and
banking workflows have been implemented.

The remaining work focuses on:

-   Ticket raising and resolution.
-   Expanding unit test coverage.
-   Integration and concurrency testing.
-   API documentation using Swagger/OpenAPI.
-   Improving project documentation and local setup.

This project is actively maintained, and its implementation may evolve
as development and testing continue.

## Future Improvements

- Design and implement ticket management functionality.
- Expand unit, integration, and concurrency test coverage.
- Review and refactor existing code to improve readability and maintainability.
- Add API documentation using Swagger/OpenAPI.
- Improve logging and observability.
- Enhance the local development and deployment setup.

## License

No open-source license has been added to this repository at present.
