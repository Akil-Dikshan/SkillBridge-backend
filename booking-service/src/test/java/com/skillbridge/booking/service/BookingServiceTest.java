package com.skillbridge.booking.service;

import com.skillbridge.booking.client.AuthServiceClient;
import com.skillbridge.booking.client.UserServiceClient;
import com.skillbridge.booking.client.dto.MentorProfileResponse;
import com.skillbridge.booking.client.dto.UserEmailResponse;
import com.skillbridge.booking.client.dto.UserProfileResponse;
import com.skillbridge.booking.dto.BookingRequest;
import com.skillbridge.booking.dto.BookingResponse;
import com.skillbridge.booking.event.BookingEventPublisher;
import com.skillbridge.booking.model.Booking;
import com.skillbridge.booking.model.BookingStatus;
import com.skillbridge.booking.repository.BookingRepository;
import com.skillbridge.booking.security.JwtService;
import feign.FeignException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private JwtService jwtService;

    @Mock
    private UserServiceClient userServiceClient;

    @Mock
    private AuthServiceClient authServiceClient;

    @Mock
    private BookingEventPublisher bookingEventPublisher;

    @InjectMocks
    private BookingService bookingService;

    private BookingRequest buildRequest() {
        BookingRequest request = new BookingRequest();
        request.setMentorId(2L);
        request.setBookingDate(LocalDate.of(2026, 6, 15));
        request.setStartTime(LocalTime.of(10, 0));
        request.setDurationMinutes(60);
        request.setNotes("Looking forward to it");
        return request;
    }

    private Booking buildBooking() {
        return Booking.builder()
                .id(1L)
                .studentId(1L)
                .mentorId(2L)
                .bookingDate(LocalDate.of(2026, 6, 15))
                .startTime(LocalTime.of(10, 0))
                .durationMinutes(60)
                .status(BookingStatus.PENDING)
                .notes("Looking forward to it")
                .build();
    }

    @Test
    void createBooking_success() {
        when(jwtService.extractUserId("token")).thenReturn(1L);
        when(userServiceClient.getUserProfile(1L)).thenReturn(new UserProfileResponse());
        when(userServiceClient.getMentorProfile(2L)).thenReturn(new MentorProfileResponse());
        when(bookingRepository.existsByMentorIdAndBookingDateAndStartTimeAndStatusIn(
                anyLong(), any(), any(), anyList())).thenReturn(false);
        when(bookingRepository.save(any(Booking.class))).thenReturn(buildBooking());
        when(authServiceClient.getUserById(anyLong())).thenReturn(new UserEmailResponse());

        BookingResponse response = bookingService.createBooking(buildRequest(), "token");

        assertThat(response).isNotNull();
        assertThat(response.getStudentId()).isEqualTo(1L);
        assertThat(response.getMentorId()).isEqualTo(2L);
        assertThat(response.getStatus()).isEqualTo(BookingStatus.PENDING);
    }

    @Test
    void createBooking_throwsWhenStudentNotFound() {
        when(jwtService.extractUserId("token")).thenReturn(1L);
        when(userServiceClient.getUserProfile(1L)).thenThrow(FeignException.NotFound.class);

        assertThatThrownBy(() -> bookingService.createBooking(buildRequest(), "token"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Student not found");
    }

    @Test
    void createBooking_throwsWhenMentorNotFound() {
        when(jwtService.extractUserId("token")).thenReturn(1L);
        when(userServiceClient.getUserProfile(1L)).thenReturn(new UserProfileResponse());
        when(userServiceClient.getMentorProfile(2L)).thenThrow(FeignException.NotFound.class);

        assertThatThrownBy(() -> bookingService.createBooking(buildRequest(), "token"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Mentor not found");
    }

    @Test
    void createBooking_throwsWhenConflictDetected() {
        when(jwtService.extractUserId("token")).thenReturn(1L);
        when(userServiceClient.getUserProfile(1L)).thenReturn(new UserProfileResponse());
        when(userServiceClient.getMentorProfile(2L)).thenReturn(new MentorProfileResponse());
        when(bookingRepository.existsByMentorIdAndBookingDateAndStartTimeAndStatusIn(
                anyLong(), any(), any(), anyList())).thenReturn(true);

        assertThatThrownBy(() -> bookingService.createBooking(buildRequest(), "token"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("already booked");
    }

    @Test
    void getStudentBookings_returnsBookings() {
        when(jwtService.extractUserId("token")).thenReturn(1L);
        when(bookingRepository.findByStudentId(1L)).thenReturn(List.of(buildBooking()));

        List<BookingResponse> result = bookingService.getStudentBookings("token");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getStudentId()).isEqualTo(1L);
    }

    @Test
    void getMentorBookings_returnsBookings() {
        when(jwtService.extractUserId("token")).thenReturn(2L);
        when(bookingRepository.findByMentorId(2L)).thenReturn(List.of(buildBooking()));

        List<BookingResponse> result = bookingService.getMentorBookings("token");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getMentorId()).isEqualTo(2L);
    }

    @Test
    void updateStatus_pendingToConfirmed_success() {
        Booking booking = buildBooking();
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        BookingResponse response = bookingService.updateStatus(1L, BookingStatus.CONFIRMED, "token");

        assertThat(response.getStatus()).isEqualTo(BookingStatus.CONFIRMED);
    }

    @Test
    void updateStatus_throwsWhenBookingNotFound() {
        when(bookingRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookingService.updateStatus(99L, BookingStatus.CONFIRMED, "token"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Booking not found");
    }

    @Test
    void updateStatus_throwsWhenAlreadyCancelled() {
        Booking booking = buildBooking();
        booking.setStatus(BookingStatus.CANCELLED);
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        assertThatThrownBy(() -> bookingService.updateStatus(1L, BookingStatus.CONFIRMED, "token"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Cannot update a CANCELLED booking");
    }

    @Test
    void updateStatus_throwsWhenCompletingUnconfirmedBooking() {
        Booking booking = buildBooking();
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        assertThatThrownBy(() -> bookingService.updateStatus(1L, BookingStatus.COMPLETED, "token"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("must be CONFIRMED");
    }
}
