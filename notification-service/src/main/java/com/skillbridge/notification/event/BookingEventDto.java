package com.skillbridge.notification.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Mirrors the BookingEventDto published by booking-service.
 * Each microservice owns its own copy of shared data shapes —
 * no cross-service class sharing.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BookingEventDto {

    private Long   bookingId;
    private Long   studentId;
    private String studentEmail;
    private Long   mentorId;
    private String mentorEmail;
    private LocalDate bookingDate;
    private LocalTime startTime;
    private Integer durationMinutes;

    /** "CONFIRMED" or "CANCELLED" */
    private String eventType;
}
