package com.skillbridge.notification.consumer;

import com.skillbridge.notification.config.RabbitMQConfig;
import com.skillbridge.notification.event.BookingEventDto;
import com.skillbridge.notification.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * Listens to the notification.booking.cancelled queue.
 *
 * Triggered when a booking status transitions to CANCELLED
 * (published by BookingService.updateStatus in booking-service).
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class BookingCancelledConsumer {

    private final EmailService emailService;

    @RabbitListener(queues = RabbitMQConfig.QUEUE_BOOKING_CANCELLED)
    public void handleBookingCancelled(BookingEventDto event) {
        log.info("📬 Received booking.cancelled event — bookingId={} student={} mentor={}",
                event.getBookingId(), event.getStudentEmail(), event.getMentorEmail());

        emailService.sendBookingCancellation(event);
    }
}
