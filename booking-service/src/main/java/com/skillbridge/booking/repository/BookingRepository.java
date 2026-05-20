package com.skillbridge.booking.repository;

import com.skillbridge.booking.model.Booking;
import com.skillbridge.booking.model.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByMentorId(Long mentorId);

    List<Booking> findByStudentId(Long studentId);

    boolean existsByMentorIdAndBookingDateAndStartTimeAndStatusIn(
            Long mentorId,
            LocalDate bookingDate,
            LocalTime startTime,
            List<BookingStatus> statuses
    );
}