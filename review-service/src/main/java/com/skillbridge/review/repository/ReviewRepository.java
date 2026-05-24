package com.skillbridge.review.repository;

import com.skillbridge.review.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Spring Data JPA repository for the Review entity.
 *
 * All methods here are used by ReviewService:
 *   - existsByBookingId      → duplicate-review guard before saving
 *   - findByMentorId         → fetch all reviews for a mentor (GET endpoint)
 *   - findByMentorId (same)  → also used for average rating recalculation
 */
public interface ReviewRepository extends JpaRepository<Review, Long> {

    /**
     * Returns true if a review already exists for this booking.
     * Used to enforce the one-review-per-booking business rule.
     */
    boolean existsByBookingId(Long bookingId);

    /**
     * Returns all reviews submitted for a given mentor.
     * Used both for the GET endpoint and for average rating calculation.
     */
    List<Review> findByMentorId(Long mentorId);

    /**
     * Returns the average rating for a mentor across all their reviews.
     * Returns null if the mentor has no reviews yet.
     */
    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.mentorId = :mentorId")
    Double findAverageRatingByMentorId(@Param("mentorId") Long mentorId);

    /**
     * Returns the total number of reviews for a mentor.
     */
    long countByMentorId(Long mentorId);
}
