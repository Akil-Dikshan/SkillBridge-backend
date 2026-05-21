# auth-service

Handles authentication for the SkillBridge platform.

## Endpoints

| Method | Path | Access | Description |
|--------|------|--------|-------------|
| POST | `/api/auth/register` | Public | Register a new user |
| POST | `/api/auth/login` | Public | Login and get tokens |
| POST | `/api/auth/refresh` | Public | Refresh access token |
| GET | `/api/admin/ping` | ADMIN only | Verify admin access |

## Tech

- Spring Boot 3 · Spring Security · JWT (jjwt 0.12.3) · PostgreSQL

## Running

```bash
docker-compose up --build
```

## Environment Variables

| Variable | Description |
|----------|-------------|
| `DB_USERNAME` | PostgreSQL username |
| `DB_PASSWORD` | PostgreSQL password |
| `JWT_SECRET` | JWT signing secret (hex) |