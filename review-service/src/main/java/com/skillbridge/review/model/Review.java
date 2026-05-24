package com.skillbridge.review.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * Represents a student's post-session review for a mentor.
 *
 * Business rules enforced here:
 *   - booking_id is UNIQUE → one review per booking, no duplicates
 *   - rating is 1–5, validated at the DTO level before reaching here
 *   - feedback is optional (nullable TEXT column)
 *   - created_at is set automatically on first insert
 *
 * Maps to the "reviews" table in review_db.
 * Does not hold foreign keys to other service databases —
 * cross-service data access happens through APIs only.
 */
@Entity
@Table(name = "reviews")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Review {

    /** Auto-incremented primary key. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The booking this review is for.
     * UNIQUE constraint enforces one review per booking.
     * Not a FK — booking-service owns that table.
     */
    @Column(name = "booking_id", nullable = false, unique = true)
    private Long bookingId;

    /**
     * The student who submitted this review.
     * Extracted from JWT at submission time.
     */
    @Column(name = "student_id", nullable = false)
    private Long studentId;

    /**
     * The mentor being reviewed.
     * Taken from the request body (cross-validated against the booking).
     */
    @Column(name = "mentor_id", nullable = false)
    private Long mentorId;

    /**
     * Star rating from 1 (worst) to 5 (best).
     * Validated by @Min(1) @Max(5) in ReviewRequest before reaching here.
     */
    @Column(nullable = false)
    private Integer rating;

    /**
     * Optional written feedback from the student.
     * Stored as TEXT to allow longer responses.
     */
    @Column(columnDefinition = "TEXT")
    private String feedback;

    /**
     * Set automatically by Hibernate to the current UTC timestamp
     * when the row is first inserted. Never updated after that.
     */
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
