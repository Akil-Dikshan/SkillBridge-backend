package com.skillbridge.review.client;

import com.skillbridge.review.client.dto.BookingResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Feign client for calling booking-service.
 *
 * Used by ReviewService to verify that a booking is in COMPLETED status
 * before accepting a review submission.
 *
 * The internal endpoint (/api/bookings/internal/**) is whitelisted in
 * booking-service's SecurityConfig — no JWT is required for that path.
 * However, FeignAuthInterceptor still forwards the Authorization header
 * if one is present in the current request context.
 */
@FeignClient(name = "booking-service", url = "${booking.service.url:http://localhost:8083}")
public interface BookingServiceClient {

    /**
     * Retrieves a booking by its ID.
     * Used to verify status == COMPLETED and studentId ownership.
     *
     * @param bookingId the booking to look up
     * @return BookingResponse with id, studentId, mentorId, status
     */
    @GetMapping("/api/bookings/internal/{bookingId}")
    BookingResponse getBooking(@PathVariable Long bookingId);
}
