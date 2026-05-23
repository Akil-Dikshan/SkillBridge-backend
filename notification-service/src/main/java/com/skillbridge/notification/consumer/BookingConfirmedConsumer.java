package com.skillbridge.notification.consumer;

import com.skillbridge.notification.config.RabbitMQConfig;
import com.skillbridge.notification.event.BookingEventDto;
import com.skillbridge.notification.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * Listens to the notification.booking.confirmed queue.
 *
 * Spring AMQP automatically deserializes the JSON payload into
 * a BookingEventDto using the Jackson2JsonMessageConverter
 * registered in RabbitMQConfig.
 *
 * On success  → message is acknowledged and removed from the queue.
 * On exception → message is requeued (Spring AMQP default behaviour).
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class BookingConfirmedConsumer {

    private final EmailService emailService;

    /**
     * Called once per booking.confirmed message.
     *
     * @param event the deserialized event from booking-service
     */
    @RabbitListener(queues = RabbitMQConfig.QUEUE_BOOKING_CONFIRMED)
    public void handleBookingConfirmed(BookingEventDto event) {
        log.info("📬 Received booking.confirmed event — bookingId={} student={} mentor={}",
                event.getBookingId(), event.getStudentEmail(), event.getMentorEmail());

        emailService.sendBookingConfirmation(event);
    }
}
