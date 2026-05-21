# API Gateway

Spring Cloud Gateway service for the SkillBridge platform. Single entry point for all client traffic — handles JWT validation, request routing, CORS, and rate limiting.

## Overview

| Property | Value |
|----------|-------|
| Port | `8080` |
| Framework | Spring Cloud Gateway |
| Java | 17 |
| Epic | SB-5 |

## Responsibilities

- Routes `/api/auth/**` → Auth Service `:8081`
- Routes `/api/users/**` → User Service `:8082`
- Routes `/api/bookings/**` → Booking Service `:8083`
- Validates JWT tokens on every request before forwarding downstream
- Enforces CORS policy for the React frontend origin
- Rate limits clients to 20 requests per second per IP

## Environment Variables

| Variable | Description | Default |
|----------|-------------|---------|
| `JWT_SECRET` | HS256 signing key shared across all services | — |
| `AUTH_SERVICE_URL` | Internal URL of the Auth Service | `http://localhost:8081` |
| `USER_SERVICE_URL` | Internal URL of the User Service | `http://localhost:8082` |
| `BOOKING_SERVICE_URL` | Internal URL of the Booking Service | `http://localhost:8083` |

Create a `.env` file in the project root (never commit it):

```env
JWT_SECRET=your_secret_here
AUTH_SERVICE_URL=http://auth-service:8081
USER_SERVICE_URL=http://user-service:8082
BOOKING_SERVICE_URL=http://booking-service:8083
```

## Running Locally

### With Docker Compose (recommended)

```bash
docker-compose up --build
```

Starts the full stack — gateway, all services, and all databases.

### Standalone

```bash
./mvnw spring-boot:run
```

Requires Auth Service, User Service, and Booking Service to be running on their default ports.

## Routing Rules

| Path Pattern | Downstream Service | Port |
|---|---|---|
| `/api/auth/**` | auth-service | 8081 |
| `/api/users/**` | user-service | 8082 |
| `/api/bookings/**` | booking-service | 8083 |

## Security

JWT validation runs as a `GlobalFilter` at `HIGHEST_PRECEDENCE`. Every request must include a valid Bearer token in the `Authorization` header, except for:

- `POST /api/auth/register`
- `POST /api/auth/login`

Requests with a missing or invalid token receive `401 Unauthorized` before they reach any downstream service.

## Rate Limiting

In-memory rate limiting is applied per client IP address. Clients exceeding 20 requests per second receive `429 Too Many Requests`. The window resets every second.

## CORS

Configured to allow requests from `http://localhost:3000` (React dev server) with credentials. Update `allowedOrigins` in `application.yaml` for production.

## Jira

Epic: [SB-5 — API Gateway](https://akildikshan02ujhgikjh.atlassian.net)

| Task | Description |
|------|-------------|
| SB-65 | Initialise api-gateway Spring Cloud Gateway project |
| SB-66 | Configure routing rules for auth-service |
| SB-67 | Configure routing rules for user-service |
| SB-68 | Configure routing rules for booking-service |
| SB-69 | Implement JWT validation filter at gateway level |
| SB-70 | Configure CORS policy for frontend origin |
| SB-71 | Add rate limiting configuration |
| SB-72 | Create api-gateway Dockerfile |
| SB-73 | Add api-gateway to docker-compose.yml |
| SB-74 | Test full request flow through gateway to all services |
| SB-75 | Write api-gateway README.md |
