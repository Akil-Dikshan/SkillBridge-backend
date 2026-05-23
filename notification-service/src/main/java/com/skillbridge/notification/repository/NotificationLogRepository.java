package com.skillbridge.notification.repository;

import com.skillbridge.notification.model.NotificationLog;
import com.skillbridge.notification.model.NotificationStatus;
import com.skillbridge.notification.model.NotificationType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Spring Data repository for NotificationLog.
 *
 * Spring generates all implementations at startup — no SQL or
 * EntityManager code needed. Method names are parsed by Spring
 * to build the correct JPQL queries automatically.
 */
public interface NotificationLogRepository extends JpaRepository<NotificationLog, Long> {

    /** All log entries for a given booking — useful for support queries. */
    List<NotificationLog> findByBookingId(Long bookingId);

    /** All log entries for a given recipient email. */
    List<NotificationLog> findByRecipientEmail(String recipientEmail);

    /** All failed delivery attempts — useful for monitoring / alerting. */
    List<NotificationLog> findByStatus(NotificationStatus status);

    /** All notifications of a specific type for a booking. */
    List<NotificationLog> findByBookingIdAndNotificationType(Long bookingId, NotificationType type);
}
