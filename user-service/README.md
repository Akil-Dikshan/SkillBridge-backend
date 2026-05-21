# User Service

Handles user profiles, mentor profiles, mentor availability, and mentor search for the SkillBridge platform.

## Tech Stack

- Java 17
- Spring Boot 3.5.14
- Spring Data JPA
- PostgreSQL
- JWT Authentication

## Running the Service

Run from the root of the project using Docker Compose:

```bash
docker-compose up --build user-service
```

## Environment Variables

| Variable | Description |
|----------|-------------|
| DB_USERNAME | PostgreSQL username |
| DB_PASSWORD | PostgreSQL password |
| JWT_SECRET | Secret key for JWT validation |

## API Endpoints

### User Profile

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | /api/users/profile | Create a user profile |
| GET | /api/users/profile | Get your profile |
| PUT | /api/users/profile | Update your profile |

### Mentor Profile

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | /api/mentors/profile | Create mentor profile |
| GET | /api/mentors/profile | Get your mentor profile |
| PUT | /api/mentors/profile | Update mentor profile |

### Mentor Availability

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | /api/mentors/availability | Add availability slot |
| GET | /api/mentors/availability | Get your availability |
| DELETE | /api/mentors/availability/{id} | Remove availability slot |

### Mentor Search

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | /api/mentors/search?skill= | Search mentors by skill |
| GET | /api/mentors/available?day= | Search mentors by available day |