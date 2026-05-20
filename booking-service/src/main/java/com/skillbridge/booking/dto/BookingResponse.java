package com.skillbridge.booking.dto;

import com.skillbridge.booking.model.BookingStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@Setter
@Builder
public class BookingResponse {

    private Long id;
    private Long studentId;
    private Long mentorId;
    private LocalDate bookingDate;
    private LocalTime startTime;
    private Integer durationMinutes;
    private BookingStatus status;
    private String notes;
    private LocalDateTime createdAt;
}