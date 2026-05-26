package com.skillbridge.booking.service;

import com.skillbridge.booking.client.AuthServiceClient;
import com.skillbridge.booking.client.UserServiceClient;
import com.skillbridge.booking.client.dto.UserEmailResponse;
import com.skillbridge.booking.dto.BookingRequest;
import com.skillbridge.booking.dto.BookingResponse;
import com.skillbridge.booking.event.BookingEventDto;
import com.skillbridge.booking.event.BookingEventPublisher;
import com.skillbridge.booking.model.Booking;
import com.skillbridge.booking.model.BookingStatus;
import com.skillbridge.booking.repository.BookingRepository;
import com.skillbridge.booking.security.JwtService;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final JwtService jwtService;
    private final UserServiceClient userServiceClient;
    private final AuthServiceClient authServiceClient;
    private final BookingEventPublisher eventPublisher;

    public BookingResponse createBooking(BookingRequest request, String token) {
        Long studentId = jwtService.extractUserId(token);

        // Verify student profile exists in user-service (optional)
        try {
            userServiceClient.getUserProfile(studentId);
        } catch (FeignException e) {
            log.warn("Student profile not found or error: " + studentId);
        }

        // Verify mentor profile exists in user-service
        try {
            userServiceClient.getMentorProfile(request.getMentorId());
        } catch (FeignException e) {
            throw new RuntimeException("Mentor not found with id: " + request.getMentorId());
        }

        // Check no conflicting booking exists for this mentor at this time
        boolean conflict = bookingRepository
                .existsByMentorIdAndBookingDateAndStartTimeAndStatusIn(
                        request.getMentorId(),
                        request.getBookingDate(),
                        request.getStartTime(),
                        List.of(BookingStatus.PENDING, BookingStatus.CONFIRMED)
                );

        if (conflict) {
            throw new RuntimeException("This time slot is already booked");
        }

        Booking booking = Booking.builder()
                .studentId(studentId)
                .mentorId(request.getMentorId())
                .bookingDate(request.getBookingDate())
                .startTime(request.getStartTime())
                .durationMinutes(request.getDurationMinutes())
                .notes(request.getNotes())
                .status(BookingStatus.PENDING)
                .build();

        Booking saved = bookingRepository.save(booking);

        // Publish booking.confirmed event to RabbitMQ asynchronously
        publishConfirmedEvent(saved);

        return toResponse(saved);
    }

    public List<BookingResponse> getStudentBookings(String token) {
        Long studentId = jwtService.extractUserId(token);
        return bookingRepository.findByStudentId(studentId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public List<BookingResponse> getMentorBookings(String token) {
        Long mentorId = jwtService.extractUserId(token);
        return bookingRepository.findByMentorId(mentorId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public BookingResponse updateStatus(Long bookingId, BookingStatus newStatus, String token) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        validateStatusTransition(booking.getStatus(), newStatus);

        booking.setStatus(newStatus);
        Booking updated = bookingRepository.save(booking);

        // Publish cancellation event when booking is cancelled
        if (newStatus == BookingStatus.CANCELLED) {
            publishCancelledEvent(updated);
        }

        return toResponse(updated);
    }

    /**
     * Returns a booking by ID for internal Feign calls from review-service.
     * Used to verify booking status is COMPLETED before accepting a review.
     */
    public BookingResponse getBookingById(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found with id: " + bookingId));
        return toResponse(booking);
    }

    // ─── Private Helpers ──────────────────────────────────────────────────────

    private void publishConfirmedEvent(Booking booking) {
        try {
            UserEmailResponse student = authServiceClient.getUserById(booking.getStudentId());
            UserEmailResponse mentor  = authServiceClient.getUserById(booking.getMentorId());

            BookingEventDto event = BookingEventDto.builder()
                    .bookingId(booking.getId())
                    .studentId(booking.getStudentId())
                    .studentEmail(student.getEmail())
                    .mentorId(booking.getMentorId())
                    .mentorEmail(mentor.getEmail())
                    .bookingDate(booking.getBookingDate())
                    .startTime(booking.getStartTime())
                    .durationMinutes(booking.getDurationMinutes())
                    .eventType("CONFIRMED")
                    .build();

            eventPublisher.publishBookingConfirmed(event);
        } catch (Exception e) {
            // Event publishing failure must NOT fail the booking itself
            log.error("Failed to publish booking.confirmed event for bookingId={}: {}",
                    booking.getId(), e.getMessage());
        }
    }

    private void publishCancelledEvent(Booking booking) {
        try {
            UserEmailResponse student = authServiceClient.getUserById(booking.getStudentId());
            UserEmailResponse mentor  = authServiceClient.getUserById(booking.getMentorId());

            BookingEventDto event = BookingEventDto.builder()
                    .bookingId(booking.getId())
                    .studentId(booking.getStudentId())
                    .studentEmail(student.getEmail())
                    .mentorId(booking.getMentorId())
                    .mentorEmail(mentor.getEmail())
                    .bookingDate(booking.getBookingDate())
                    .startTime(booking.getStartTime())
                    .durationMinutes(booking.getDurationMinutes())
                    .eventType("CANCELLED")
                    .build();

            eventPublisher.publishBookingCancelled(event);
        } catch (Exception e) {
            log.error("Failed to publish booking.cancelled event for bookingId={}: {}",
                    booking.getId(), e.getMessage());
        }
    }

    private void validateStatusTransition(BookingStatus current, BookingStatus next) {
        if (current == BookingStatus.CANCELLED || current == BookingStatus.COMPLETED) {
            throw new RuntimeException("Cannot update a " + current + " booking");
        }
        if (next == BookingStatus.COMPLETED && current != BookingStatus.CONFIRMED) {
            throw new RuntimeException("Booking must be CONFIRMED before it can be COMPLETED");
        }
    }

    private BookingResponse toResponse(Booking booking) {
        return BookingResponse.builder()
                .id(booking.getId())
                .studentId(booking.getStudentId())
                .mentorId(booking.getMentorId())
                .bookingDate(booking.getBookingDate())
                .startTime(booking.getStartTime())
                .durationMinutes(booking.getDurationMinutes())
                .status(booking.getStatus())
                .notes(booking.getNotes())
                .createdAt(booking.getCreatedAt())
                .build();
    }
}
