package com.skillbridge.booking.controller;

import com.skillbridge.booking.dto.BookingRequest;
import com.skillbridge.booking.dto.BookingResponse;
import com.skillbridge.booking.model.BookingStatus;
import com.skillbridge.booking.service.BookingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public ResponseEntity<BookingResponse> createBooking(
            @Valid @RequestBody BookingRequest request,
            @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.substring(7);
        return ResponseEntity.ok(bookingService.createBooking(request, token));
    }

    @GetMapping("/student")
    public ResponseEntity<List<BookingResponse>> getStudentBookings(
            @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.substring(7);
        return ResponseEntity.ok(bookingService.getStudentBookings(token));
    }

    @GetMapping("/mentor")
    public ResponseEntity<List<BookingResponse>> getMentorBookings(
            @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.substring(7);
        return ResponseEntity.ok(bookingService.getMentorBookings(token));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<BookingResponse> updateStatus(
            @PathVariable Long id,
            @RequestParam BookingStatus status,
            @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.substring(7);
        return ResponseEntity.ok(bookingService.updateStatus(id, status, token));
    }

    /**
     * Internal endpoint for review-service Feign calls.
     * Returns booking details (id, studentId, mentorId, status) so review-service
     * can verify a booking is COMPLETED before accepting a review.
     *
     * This path is whitelisted in SecurityConfig — no JWT required.
     * It is only reachable from within the Docker network (not via API Gateway).
     */
    @GetMapping("/internal/{bookingId}")
    public ResponseEntity<BookingResponse> getBookingInternal(
            @PathVariable Long bookingId) {
        return ResponseEntity.ok(bookingService.getBookingById(bookingId));
    }
}