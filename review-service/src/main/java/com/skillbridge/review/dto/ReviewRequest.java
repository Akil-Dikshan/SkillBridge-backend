package com.skillbridge.review.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

/**
 * Request body for POST /api/reviews.
 *
 * Validation rules:
 *   - bookingId: required — identifies which session is being reviewed
 *   - mentorId: required — used to cross-validate against the booking
 *   - rating: required, 1–5 stars
 *   - feedback: optional written comment (can be null or empty)
 */
@Getter
@Setter
public class ReviewRequest {

    @NotNull(message = "Booking ID is required")
    private Long bookingId;

    @NotNull(message = "Mentor ID is required")
    private Long mentorId;

    @NotNull(message = "Rating is required")
    @Min(value = 1, message = "Rating must be at least 1")
    @Max(value = 5, message = "Rating must be at most 5")
    private Integer rating;

    /** Optional written feedback. May be null or blank. */
    private String feedback;
}
