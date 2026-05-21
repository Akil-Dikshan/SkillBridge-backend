package com.skillbridge.booking.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
public class BookingRequest {

    @NotNull(message = "Mentor ID is required")
    private Long mentorId;

    @NotNull(message = "Booking date is required")
    private LocalDate bookingDate;

    @NotNull(message = "Start time is required")
    private LocalTime startTime;

    @NotNull(message = "Duration is required")
    @Min(value = 30, message = "Duration must be at least 30 minutes")
    private Integer durationMinutes;

    private String notes;
}