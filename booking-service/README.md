# Booking Service

Handles the full booking lifecycle for the SkillBridge platform — from session requests through confirmation, completion, and cancellation.

## Tech Stack

- Java 17
- Spring Boot 3.5.14
- Spring Data JPA
- PostgreSQL
- Spring Cloud OpenFeign
- JWT Authentication

## Running the Service

Run from the root of the project using Docker Compose:

```bash
docker-compose up --build booking-service
```

## Environment Variables

| Variable | Description |
|----------|-------------|
| DB_USERNAME | PostgreSQL username |
| DB_PASSWORD | PostgreSQL password |
| JWT_SECRET | Secret key for JWT validation |
| USER_SERVICE_URL | Base URL of the user-service (default: http://localhost:8082) |

## API Endpoints

### Bookings

| Method | Endpoint | Role | Description |
|--------|----------|------|-------------|
| POST | /api/bookings | STUDENT | Create a new booking request |
| GET | /api/bookings/student | STUDENT | Get all bookings for the logged-in student |
| GET | /api/bookings/mentor | MENTOR | Get all bookings for the logged-in mentor |
| PATCH | /api/bookings/{id}/status | ANY | Update booking status |

## Booking Lifecycle

```
PENDING → CONFIRMED → COMPLETED
PENDING → CANCELLED
CONFIRMED → CANCELLED
```

A booking cannot be updated once it is COMPLETED or CANCELLED.
