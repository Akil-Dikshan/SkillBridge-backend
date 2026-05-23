package com.skillbridge.booking.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BookingEventDto {

    private Long bookingId;

    private Long studentId;
    private String studentEmail;

    private Long mentorId;
    private String mentorEmail;

    private LocalDate bookingDate;
    private LocalTime startTime;
    private Integer durationMinutes;

    private String eventType;   // "CONFIRMED" or "CANCELLED"
}
