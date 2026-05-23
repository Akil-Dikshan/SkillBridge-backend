package com.skillbridge.notification.consumer;

import com.skillbridge.notification.config.RabbitMQConfig;
import com.skillbridge.notification.event.BookingEventDto;
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
public class BookingCancelledConsumer {

    @RabbitListener(queues = RabbitMQConfig.QUEUE_BOOKING_CANCELLED)
    public void handleBookingCancelled(BookingEventDto event) {
        log.info("📬 Received booking.cancelled event — bookingId={} student={} mentor={}",
                event.getBookingId(), event.getStudentEmail(), event.getMentorEmail());

        // TODO SB-93: send cancellation email to student and mentor
        // emailService.sendBookingCancellation(event);
    }
}
