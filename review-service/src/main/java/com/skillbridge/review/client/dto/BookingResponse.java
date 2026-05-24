package com.skillbridge.review.client.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Minimal DTO for the booking-service response.
 *
 * Only the fields review-service needs are declared here.
 * Jackson will ignore any extra fields returned by booking-service.
 *
 * Used by BookingServiceClient to verify:
 *   1. The booking exists
 *   2. booking.status == "COMPLETED" before accepting a review
 *   3. booking.studentId == the JWT userId (ownership check)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingResponse {

    private Long id;

    /** The student who made the booking. */
    private Long studentId;

    /** The mentor who was booked. */
    private Long mentorId;

    /**
     * Booking lifecycle status.
     * Expected value for review eligibility: "COMPLETED"
     */
    private String status;
}
