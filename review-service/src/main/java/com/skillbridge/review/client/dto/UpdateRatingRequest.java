package com.skillbridge.review.client.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Request body sent to user-service when pushing an updated mentor rating.
 *
 * Sent by ReviewService after every new review submission via
 * UserServiceClient.updateMentorRating().
 *
 * user-service uses these values to update the mentor_profiles table:
 *   - average_rating: the recalculated average across all reviews
 *   - total_reviews: the new total count of reviews
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateRatingRequest {

    /** New average rating, rounded to 2 decimal places. e.g. 4.33 */
    private BigDecimal averageRating;

    /** Total number of reviews for this mentor (including the new one). */
    private Integer totalReviews;
}
