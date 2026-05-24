package com.skillbridge.review.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Response payload returned when fetching reviews or
 * successfully submitting a new review.
 */
@Getter
@Setter
@Builder
public class ReviewResponse {

    private Long id;
    private Long bookingId;
    private Long studentId;
    private Long mentorId;
    private Integer rating;
    private String feedback;
    private LocalDateTime createdAt;
}
