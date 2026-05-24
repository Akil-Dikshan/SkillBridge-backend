package com.skillbridge.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Request body received from review-service via Feign
 * to update a mentor's average rating on their profile.
 *
 * Called by the internal PUT /api/users/internal/{userId}/mentor-rating endpoint.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateRatingRequest {

    /** Recalculated average rating from review-service. */
    private BigDecimal averageRating;

    /** Total number of reviews for this mentor. */
    private Integer totalReviews;
}
