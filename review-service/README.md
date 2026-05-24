# SkillBridge Review Service

The **Review Service** manages post-session feedback and mentor ratings.
It is part of the SkillBridge microservices architecture and runs on **port 8085**.

## Responsibilities

1. **Accept Reviews**: Allows students to submit a 1-5 star rating and optional feedback for a completed mentoring session.
2. **Verify Bookings**: Calls the Booking Service to ensure the session is `COMPLETED` and the student is authorized to review it.
3. **Calculate Ratings**: Automatically recalculates the mentor's overall average rating.
4. **Push Ratings**: Updates the Mentor's profile in the User Service with the new average rating and total review count.

## Endpoints

All endpoints are routed through the API Gateway at `/api/reviews/**`.

| Method | Endpoint | Auth Required | Description |
|---|---|---|---|
| POST | `/api/reviews` | `ROLE_STUDENT` | Submit a new review for a session. |
| GET | `/api/reviews/mentor/{id}` | Any Authenticated | Get all reviews for a specific mentor. |
| GET | `/actuator/health` | None | Service health check. |

### Submit Review Payload Example

```json
{
  "bookingId": 10,
  "mentorId": 2,
  "rating": 5,
  "feedback": "Great session, very helpful!"
}
```

## Internal Dependencies (OpenFeign)

This service depends on two other services to function:

1. **Booking Service (`booking-service:8083`)**
   - **Why**: To verify the booking status is `COMPLETED` before accepting a review.
   - **Endpoint Used**: `GET /api/bookings/internal/{bookingId}`

2. **User Service (`user-service:8082`)**
   - **Why**: To push the newly calculated average rating to the mentor's profile.
   - **Endpoint Used**: `PUT /api/users/internal/{mentorId}/mentor-rating`

*Note: These internal endpoints do not require JWT authentication, but the `FeignAuthInterceptor` propagates the incoming user's JWT token anyway for logging and tracing.*

## Environment Variables

| Variable | Default Value | Description |
|---|---|---|
| `SPRING_DATASOURCE_URL` | `jdbc:postgresql://localhost:5432/review_db` | PostgreSQL connection URL |
| `DB_USERNAME` | `postgres` | Database username |
| `DB_PASSWORD` | `postgres` | Database password |
| `JWT_SECRET` | (base64 string) | Secret key for parsing JWT tokens |
| `BOOKING_SERVICE_URL` | `http://localhost:8083` | Booking service URL |
| `USER_SERVICE_URL` | `http://localhost:8082` | User service URL |

## Running Locally

1. Start the database and dependencies from the root directory:
   ```bash
   docker-compose up -d review-db booking-service user-service
   ```
2. Run the application:
   ```bash
   mvn spring-boot:run
   ```

## Postman Testing (SB-110)

To fully verify this service locally using Postman:

1. Ensure the system is running (`docker-compose up`).
2. Login as a Student to get a JWT token.
3. **Submit a Review**: Send a `POST /api/reviews` with the payload above. Verify you get a `201 Created`.
4. **Duplicate Check**: Send the exact same request again. Verify you get a `409 Conflict`.
5. **Pending Check**: Try to review a booking that is `PENDING` or `CONFIRMED`. Verify you get a `400 Bad Request`.
6. **Role Check**: Login as a Mentor and try to `POST /api/reviews`. Verify you get a `403 Forbidden`.
7. **Verify Rating Push**: Call `GET /api/users/{mentorId}/mentor-profile`. Verify the `averageRating` and `totalReviews` have been updated.
