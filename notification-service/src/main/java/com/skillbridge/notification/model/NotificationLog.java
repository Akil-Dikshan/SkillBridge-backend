package com.skillbridge.notification.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * Persists a record for every email delivery attempt made by EmailService.
 *
 * One row = one email to one recipient.
 * If both student and mentor are emailed for the same booking event,
 * two rows are inserted — one per recipient.
 *
 * Maps to the "notification_log" table in notification_db.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "notification_log")
public class NotificationLog {

    /** Auto-incremented primary key assigned by the database. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The booking that triggered this notification.
     * Not a foreign key — notification-service does not own the booking table.
     */
    @Column(name = "booking_id", nullable = false)
    private Long bookingId;

    /** Email address the notification was sent to. */
    @Column(name = "recipient_email", nullable = false, length = 255)
    private String recipientEmail;

    /**
     * The type of event that triggered this email.
     * EnumType.STRING stores the name (e.g. "BOOKING_CONFIRMED")
     * rather than the ordinal position — safe against enum reordering.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "notification_type", nullable = false, length = 50)
    private NotificationType notificationType;

    /**
     * Whether the email was delivered successfully or failed.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 10)
    private NotificationStatus status;

    /**
     * Populated only when status = FAILED.
     * Stores the MessagingException message for debugging.
     */
    @Column(name = "error_message", length = 500)
    private String errorMessage;

    /**
     * Set automatically by Hibernate to the current UTC timestamp
     * when the row is first inserted. Never updated after that.
     */
    @CreationTimestamp
    @Column(name = "sent_at", nullable = false, updatable = false)
    private LocalDateTime sentAt;
}
