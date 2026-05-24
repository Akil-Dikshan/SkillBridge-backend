package com.skillbridge.review.client;

import com.skillbridge.review.client.dto.UpdateRatingRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * Feign client for calling user-service.
 *
 * Used by ReviewService to push an updated average rating to the
 * mentor's profile after every new review submission.
 *
 * The internal endpoint (/api/users/internal/**) is whitelisted in
 * user-service's SecurityConfig — no JWT required on that path.
 */
@FeignClient(name = "user-service", url = "${user.service.url:http://localhost:8082}")
public interface UserServiceClient {

    /**
     * Pushes the recalculated average rating to the mentor's profile.
     *
     * @param mentorId the user ID of the mentor in user-service
     * @param request  the new averageRating and totalReviews values
     */
    @PutMapping("/api/users/internal/{mentorId}/mentor-rating")
    void updateMentorRating(@PathVariable Long mentorId,
                            @RequestBody UpdateRatingRequest request);
}
