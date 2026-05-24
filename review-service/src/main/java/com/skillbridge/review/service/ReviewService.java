package com.skillbridge.review.service;

import com.skillbridge.review.client.BookingServiceClient;
import com.skillbridge.review.client.UserServiceClient;
import com.skillbridge.review.client.dto.BookingResponse;
import com.skillbridge.review.client.dto.UpdateRatingRequest;
import com.skillbridge.review.dto.ReviewRequest;
import com.skillbridge.review.dto.ReviewResponse;
import com.skillbridge.review.model.Review;
import com.skillbridge.review.repository.ReviewRepository;
import com.skillbridge.review.security.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final BookingServiceClient bookingServiceClient;
    private final UserServiceClient userServiceClient;
    private final JwtService jwtService;

    /**
     * Submits a new post-session review.
     * Enforces the following business rules:
     * 1. Booking must exist and be COMPLETED.
     * 2. The student submitting the review must be the student on the booking.
     * 3. Only one review is allowed per booking.
     *
     * @param request The review details
     * @param token The JWT token containing the studentId
     * @return ReviewResponse representing the saved review
     */
    @Transactional
    public ReviewResponse submitReview(ReviewRequest request, String token) {

        Long studentId = jwtService.extractUserId(token);

        // 1. Verify the booking via Feign
        BookingResponse booking;
        try {
            booking = bookingServiceClient.getBooking(request.getBookingId());
        } catch (Exception e) {
            log.error("Failed to fetch booking ID {}", request.getBookingId(), e);
            throw new IllegalArgumentException("Booking not found or could not be verified");
        }

        // 2. Validate booking status is COMPLETED
        if (!"COMPLETED".equalsIgnoreCase(booking.getStatus())) {
            throw new IllegalArgumentException("Reviews can only be submitted for COMPLETED sessions");
        }

        // 3. Verify ownership (prevent reviewing someone else's session)
        if (!booking.getStudentId().equals(studentId)) {
            throw new SecurityException("You can only review your own sessions");
        }

        // 4. Verify mentor ID matches the booking
        if (!booking.getMentorId().equals(request.getMentorId())) {
            throw new IllegalArgumentException("Mentor ID does not match the booking");
        }

        // 5. Prevent duplicate reviews
        if (reviewRepository.existsByBookingId(request.getBookingId())) {
            throw new IllegalStateException("A review has already been submitted for this booking");
        }

        // 6. Save the review
        Review review = Review.builder()
                .bookingId(request.getBookingId())
                .studentId(studentId)
                .mentorId(request.getMentorId())
                .rating(request.getRating())
                .feedback(request.getFeedback())
                .build();

        Review savedReview = reviewRepository.save(review);

        // 7. Recalculate average rating and push to user-service
        recalculateMentorRating(request.getMentorId());

        return toResponse(savedReview);
    }

    /**
     * Retrieves all reviews submitted for a specific mentor.
     */
    public List<ReviewResponse> getReviewsByMentor(Long mentorId) {
        return reviewRepository.findByMentorId(mentorId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Calculates the new average rating for a mentor and pushes the
     * updated value to the user-service via Feign.
     *
     * We catch and log Feign exceptions here so that a failure in user-service
     * does not rollback the successful save of the review. Eventual consistency
     * could be handled with async queues, but for now we try sync.
     */
    private void recalculateMentorRating(Long mentorId) {
        try {
            List<Review> reviews = reviewRepository.findByMentorId(mentorId);
            if (reviews.isEmpty()) {
                return;
            }

            double avg = reviews.stream()
                    .mapToInt(Review::getRating)
                    .average()
                    .orElse(0.0);

            BigDecimal averageRating = BigDecimal.valueOf(avg)
                    .setScale(2, RoundingMode.HALF_UP);
            
            int totalReviews = reviews.size();

            log.info("Pushing updated rating to user-service for mentorId={}: avg={}, total={}",
                    mentorId, averageRating, totalReviews);

            userServiceClient.updateMentorRating(
                    mentorId,
                    new UpdateRatingRequest(averageRating, totalReviews)
            );

        } catch (Exception e) {
            // Log but don't rethrow. We don't want to fail the review save
            // just because user-service is momentarily down.
            log.error("Failed to update mentor rating in user-service for mentorId={}", mentorId, e);
        }
    }

    private ReviewResponse toResponse(Review review) {
        return ReviewResponse.builder()
                .id(review.getId())
                .bookingId(review.getBookingId())
                .studentId(review.getStudentId())
                .mentorId(review.getMentorId())
                .rating(review.getRating())
                .feedback(review.getFeedback())
                .createdAt(review.getCreatedAt())
                .build();
    }
}
