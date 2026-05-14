# SkillBridge Backend

A production-grade **Student Mentor Booking Platform** built with microservices architecture.

![CI](https://github.com/Akil-Dikshan/skillbridge-backend/actions/workflows/ci.yml/badge.svg)

---

## Architecture

SkillBridge follows a microservices architecture where each service is independently deployable,
owns its own PostgreSQL database, and communicates via HTTP (OpenFeign) or async messaging (RabbitMQ).

## Services

| Service              | Port  | Description                        | Status     |
|----------------------|-------|------------------------------------|------------|
| api-gateway          | :8080 | Routes all client traffic          | 🔄 Phase 1 |
| auth-service         | :8081 | JWT + Firebase authentication      | 🔄 Phase 1 |
| user-service         | :8082 | Profiles, skills, availability     | 🔄 Phase 1 |
| booking-service      | :8083 | Session scheduling — core domain   | 🔄 Phase 1 |
| notification-service | :8084 | Async email notifications          | 🔜 Phase 2 |
| review-service       | :8085 | Ratings and feedback               | 🔜 Phase 2 |

## Tech Stack

`Java 17` · `Spring Boot 3` · `PostgreSQL 15` · `Docker` · `RabbitMQ` · `Firebase` · `JWT`

## Running Locally

```bash
git clone https://github.com/Akil-Dikshan/skillbridge-backend
cd skillbridge-backend
docker-compose up
```

## Live Demo

> Deployment in progress — available after Phase 1 (Week 7)

---

*Built as a flagship portfolio project targeting WSO2 / IFS internships.*
