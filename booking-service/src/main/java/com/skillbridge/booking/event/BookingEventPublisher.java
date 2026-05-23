package com.skillbridge.booking.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class BookingEventPublisher {

    // Must match RabbitMQConfig.EXCHANGE in notification-service
    private static final String EXCHANGE = "skillbridge.events";

    private static final String KEY_BOOKING_CONFIRMED = "booking.confirmed";
    private static final String KEY_BOOKING_CANCELLED = "booking.cancelled";

    private final RabbitTemplate rabbitTemplate;

    public void publishBookingConfirmed(BookingEventDto event) {
        log.info("Publishing booking.confirmed event for bookingId={}", event.getBookingId());
        rabbitTemplate.convertAndSend(EXCHANGE, KEY_BOOKING_CONFIRMED, event);
    }

    public void publishBookingCancelled(BookingEventDto event) {
        log.info("Publishing booking.cancelled event for bookingId={}", event.getBookingId());
        rabbitTemplate.convertAndSend(EXCHANGE, KEY_BOOKING_CANCELLED, event);
    }
}
