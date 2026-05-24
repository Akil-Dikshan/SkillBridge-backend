package com.skillbridge.review.controller;

import com.skillbridge.review.dto.ReviewRequest;
import com.skillbridge.review.dto.ReviewResponse;
import com.skillbridge.review.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    /**
     * Submits a new review.
     * Accessible only by users with ROLE_STUDENT.
     * The studentId is extracted from the JWT token.
     */
    @PostMapping
    public ResponseEntity<ReviewResponse> submitReview(
            @Valid @RequestBody ReviewRequest request,
            @RequestHeader("Authorization") String authHeader) {

        String token = authHeader.substring(7);
        ReviewResponse response = reviewService.submitReview(request, token);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Gets all reviews for a specific mentor.
     * Accessible to any authenticated user.
     */
    @GetMapping("/mentor/{mentorId}")
    public ResponseEntity<List<ReviewResponse>> getReviewsByMentor(
            @PathVariable Long mentorId) {
        return ResponseEntity.ok(reviewService.getReviewsByMentor(mentorId));
    }
}
