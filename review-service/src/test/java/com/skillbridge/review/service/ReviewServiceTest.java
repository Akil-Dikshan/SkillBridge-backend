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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private BookingServiceClient bookingServiceClient;

    @Mock
    private UserServiceClient userServiceClient;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private ReviewService reviewService;

    private ReviewRequest buildRequest() {
        ReviewRequest request = new ReviewRequest();
        request.setBookingId(10L);
        request.setMentorId(2L);
        request.setRating(5);
        request.setFeedback("Great session!");
        return request;
    }

    private BookingResponse buildBooking(String status, Long studentId) {
        BookingResponse booking = new BookingResponse();
        booking.setId(10L);
        booking.setStudentId(studentId);
        booking.setMentorId(2L);
        booking.setStatus(status);
        return booking;
    }

    private Review buildReview(int rating) {
        return Review.builder()
                .id(1L)
                .bookingId(10L)
                .studentId(1L)
                .mentorId(2L)
                .rating(rating)
                .feedback("Feedback")
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    void submitReview_success() {
        when(jwtService.extractUserId("token")).thenReturn(1L);
        when(bookingServiceClient.getBooking(10L)).thenReturn(buildBooking("COMPLETED", 1L));
        when(reviewRepository.existsByBookingId(10L)).thenReturn(false);
        when(reviewRepository.save(any(Review.class))).thenReturn(buildReview(5));
        when(reviewRepository.findByMentorId(2L)).thenReturn(List.of(buildReview(5)));

        ReviewResponse response = reviewService.submitReview(buildRequest(), "token");

        assertThat(response.getRating()).isEqualTo(5);
        verify(userServiceClient).updateMentorRating(eq(2L), any(UpdateRatingRequest.class));
    }

    @Test
    void submitReview_throwsWhenBookingNotCompleted() {
        when(jwtService.extractUserId("token")).thenReturn(1L);
        when(bookingServiceClient.getBooking(10L)).thenReturn(buildBooking("CONFIRMED", 1L));

        assertThatThrownBy(() -> reviewService.submitReview(buildRequest(), "token"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("COMPLETED");
    }

    @Test
    void submitReview_throwsWhenStudentMismatch() {
        when(jwtService.extractUserId("token")).thenReturn(99L);
        when(bookingServiceClient.getBooking(10L)).thenReturn(buildBooking("COMPLETED", 1L));

        assertThatThrownBy(() -> reviewService.submitReview(buildRequest(), "token"))
                .isInstanceOf(SecurityException.class)
                .hasMessageContaining("only review your own");
    }

    @Test
    void submitReview_throwsWhenDuplicateReview() {
        when(jwtService.extractUserId("token")).thenReturn(1L);
        when(bookingServiceClient.getBooking(10L)).thenReturn(buildBooking("COMPLETED", 1L));
        when(reviewRepository.existsByBookingId(10L)).thenReturn(true);

        assertThatThrownBy(() -> reviewService.submitReview(buildRequest(), "token"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("already been submitted");
    }

    @Test
    void getReviewsByMentor_returnsReviews() {
        when(reviewRepository.findByMentorId(2L)).thenReturn(List.of(buildReview(4), buildReview(5)));

        List<ReviewResponse> result = reviewService.getReviewsByMentor(2L);

        assertThat(result).hasSize(2);
    }

    @Test
    void recalculateMentorRating_calculatesCorrectAverage() {
        when(jwtService.extractUserId("token")).thenReturn(1L);
        when(bookingServiceClient.getBooking(10L)).thenReturn(buildBooking("COMPLETED", 1L));
        when(reviewRepository.existsByBookingId(10L)).thenReturn(false);
        when(reviewRepository.save(any(Review.class))).thenReturn(buildReview(4));
        
        // 5, 3, 4 -> average 4.00
        when(reviewRepository.findByMentorId(2L)).thenReturn(List.of(
                buildReview(5),
                buildReview(3),
                buildReview(4)
        ));

        reviewService.submitReview(buildRequest(), "token");

        verify(userServiceClient).updateMentorRating(
                eq(2L),
                argThat(req -> req.getAverageRating().doubleValue() == 4.0 && req.getTotalReviews() == 3)
        );
    }

    @Test
    void recalculateMentorRating_feignFailure_doesNotCrash() {
        when(jwtService.extractUserId("token")).thenReturn(1L);
        when(bookingServiceClient.getBooking(10L)).thenReturn(buildBooking("COMPLETED", 1L));
        when(reviewRepository.existsByBookingId(10L)).thenReturn(false);
        when(reviewRepository.save(any(Review.class))).thenReturn(buildReview(5));
        when(reviewRepository.findByMentorId(2L)).thenReturn(List.of(buildReview(5)));

        doThrow(new RuntimeException("Feign error")).when(userServiceClient)
                .updateMentorRating(anyLong(), any(UpdateRatingRequest.class));

        // Should not throw
        ReviewResponse response = reviewService.submitReview(buildRequest(), "token");

        assertThat(response).isNotNull();
    }
}
