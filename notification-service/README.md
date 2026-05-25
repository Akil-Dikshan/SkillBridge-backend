# Notification Service

Handles all transactional email notifications for the SkillBridge platform. Consumes booking events from RabbitMQ and sends HTML emails to students and mentors. Persists a log record for every delivery attempt.

## Tech Stack

- Java 17
- Spring Boot 3.5.14
- Spring AMQP (RabbitMQ)
- Spring Mail (JavaMailSender)
- Spring Data JPA
- PostgreSQL

## Running the Service

Run from the root of the project using Docker Compose:

```bash
docker-compose up --build notification-service
```

Emails are caught locally by Mailhog. Open the Mailhog UI at:

```
http://localhost:8025
```

## Environment Variables

| Variable | Description |
|----------|-------------|
| DB_USERNAME | PostgreSQL username |
| DB_PASSWORD | PostgreSQL password |
| SPRING_DATASOURCE_URL | Full JDBC URL for notification_db |
| RABBITMQ_HOST | RabbitMQ hostname (default: localhost) |
| RABBITMQ_PORT | RabbitMQ AMQP port (default: 5672) |
| RABBITMQ_USERNAME | RabbitMQ username (default: guest) |
| RABBITMQ_PASSWORD | RabbitMQ password (default: guest) |
| MAIL_HOST | SMTP server hostname (default: mailhog in docker-compose) |
| MAIL_PORT | SMTP server port (default: 1025) |
| MAIL_USERNAME | SMTP username (not required for Mailhog) |
| MAIL_PASSWORD | SMTP password (not required for Mailhog) |
| MAIL_FROM | Sender address (default: noreply@skillbridge.com) |

## Event Consumers

| Queue | Routing Key | Triggered By | Action |
|-------|-------------|--------------|--------|
| notification.booking.confirmed | booking.confirmed | Booking status → CONFIRMED | Sends confirmation email to student and mentor |
| notification.booking.cancelled | booking.cancelled | Booking status → CANCELLED | Sends cancellation email to student and mentor |
| notification.session.reminder | session.reminder | Scheduled reminder job (future) | Sends reminder email to student and mentor |

All queues are bound to the `skillbridge.events` topic exchange.

## Notification Log

Every email delivery attempt is persisted to the `notification_log` table in PostgreSQL.

| Column | Description |
|--------|-------------|
| id | Auto-generated primary key |
| booking_id | The booking that triggered the notification |
| recipient_email | Address the email was sent to |
| notification_type | BOOKING_CONFIRMED, BOOKING_CANCELLED, or SESSION_REMINDER |
| status | SENT or FAILED |
| error_message | Populated on FAILED — stores the SMTP error message |
| sent_at | Timestamp set automatically on insert |

## RabbitMQ Management UI

When running via Docker Compose, the RabbitMQ management dashboard is available at:

```
http://localhost:15672
```

Login with `guest` / `guest`. Use it to inspect queues, view message rates, and verify event delivery.
