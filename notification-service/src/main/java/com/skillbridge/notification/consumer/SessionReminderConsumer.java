package com.skillbridge.notification.consumer;

import com.skillbridge.notification.config.RabbitMQConfig;
import com.skillbridge.notification.event.BookingEventDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * Listens to the notification.session.reminder queue.
 *
 * Triggered by a scheduled reminder job (future ticket) that publishes
 * a session.reminder event approximately 24 hours before the booking
 * start time.
 *
 * On success  → message acknowledged and removed from queue.
 * On exception → message requeued (Spring AMQP default behaviour).
 */
@Slf4j
@Component
public class SessionReminderConsumer {

    @RabbitListener(queues = RabbitMQConfig.QUEUE_SESSION_REMINDER)
    public void handleSessionReminder(BookingEventDto event) {
        log.info("📬 Received session.reminder event — bookingId={} student={} mentor={} date={} time={}",
                event.getBookingId(), event.getStudentEmail(), event.getMentorEmail(),
                event.getBookingDate(), event.getStartTime());

        // TODO SB-93: send reminder email to student and mentor
        // emailService.sendSessionReminder(event);
    }
}
