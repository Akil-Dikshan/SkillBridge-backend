package com.skillbridge.notification.service;

import com.skillbridge.notification.event.BookingEventDto;
import com.skillbridge.notification.model.NotificationLog;
import com.skillbridge.notification.model.NotificationStatus;
import com.skillbridge.notification.model.NotificationType;
import com.skillbridge.notification.repository.NotificationLogRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

/**
 * Sends transactional HTML emails for all booking-related events
 * and persists a NotificationLog record for every delivery attempt.
 *
 * Called by:
 *   - BookingConfirmedConsumer  → sendBookingConfirmation()
 *   - BookingCancelledConsumer  → sendBookingCancellation()
 *   - SessionReminderConsumer   → sendSessionReminder()
 *
 * Uses Spring's JavaMailSender which is auto-configured from
 * spring.mail.* properties in application.yaml.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;
    private final NotificationLogRepository notificationLogRepository;

    @Value("${notification.mail.from:noreply@skillbridge.com}")
    private String fromAddress;

    // ─── Public API ──────────────────────────────────────────────────────────

    /**
     * Sends a booking confirmation email to both the student and the mentor.
     */
    public void sendBookingConfirmation(BookingEventDto event) {
        sendEmail(event.getStudentEmail(), "SkillBridge — Your Booking is Confirmed!",
                buildConfirmationBody("student", event), event.getBookingId(), NotificationType.BOOKING_CONFIRMED);
        sendEmail(event.getMentorEmail(), "SkillBridge — You Have a New Booking",
                buildConfirmationBody("mentor", event), event.getBookingId(), NotificationType.BOOKING_CONFIRMED);
    }

    /**
     * Sends a cancellation email to both the student and the mentor.
     */
    public void sendBookingCancellation(BookingEventDto event) {
        sendEmail(event.getStudentEmail(), "SkillBridge — Booking Cancelled",
                buildCancellationBody("student", event), event.getBookingId(), NotificationType.BOOKING_CANCELLED);
        sendEmail(event.getMentorEmail(), "SkillBridge — Booking Cancelled",
                buildCancellationBody("mentor", event), event.getBookingId(), NotificationType.BOOKING_CANCELLED);
    }

    /**
     * Sends a session reminder email to both the student and the mentor.
     */
    public void sendSessionReminder(BookingEventDto event) {
        sendEmail(event.getStudentEmail(), "SkillBridge — Reminder: Your Session is Tomorrow",
                buildReminderBody("student", event), event.getBookingId(), NotificationType.SESSION_REMINDER);
        sendEmail(event.getMentorEmail(), "SkillBridge — Reminder: You Have a Session Tomorrow",
                buildReminderBody("mentor", event), event.getBookingId(), NotificationType.SESSION_REMINDER);
    }

    // ─── Private Helpers ─────────────────────────────────────────────────────

    /**
     * Core send method. All public methods delegate here.
     *
     * Always saves a NotificationLog record — SENT on success, FAILED on error.
     * MessagingException is caught and logged but NOT re-thrown so a failed
     * email never crashes the consumer or causes the RabbitMQ message to requeue.
     */
    private void sendEmail(String to, String subject, String htmlBody,
                           Long bookingId, NotificationType type) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            // true  = multipart (required for HTML bodies)
            // UTF-8 = prevents character encoding issues in names / subjects
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(fromAddress);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlBody, true); // true = treat body as HTML
            mailSender.send(message);

            log.info("✉️  Email sent     → to={} subject={}", to, subject);

            notificationLogRepository.save(NotificationLog.builder()
                    .bookingId(bookingId)
                    .recipientEmail(to)
                    .notificationType(type)
                    .status(NotificationStatus.SENT)
                    .build());

        } catch (Exception e) {
            log.error("❌ Email failed    → to={} subject={} error={}", to, subject, e.getMessage());

            notificationLogRepository.save(NotificationLog.builder()
                    .bookingId(bookingId)
                    .recipientEmail(to)
                    .notificationType(type)
                    .status(NotificationStatus.FAILED)
                    .errorMessage(e.getMessage())
                    .build());
        }
    }

    // ─── HTML Body Builders ──────────────────────────────────────────────────

    private String buildConfirmationBody(String role, BookingEventDto event) {
        String greeting = "student".equals(role)
                ? "Your mentoring session has been confirmed."
                : "You have a new mentoring session booked.";

        return """
                <html><body style="font-family: Arial, sans-serif; color: #333;">
                  <h2 style="color: #4A90E2;">SkillBridge — Booking Confirmed ✅</h2>
                  <p>%s</p>
                  <table style="border-collapse: collapse; width: 100%%; max-width: 480px;">
                    <tr><td style="padding: 8px; font-weight: bold;">Booking ID</td>
                        <td style="padding: 8px;">#%d</td></tr>
                    <tr style="background:#f9f9f9;">
                        <td style="padding: 8px; font-weight: bold;">Date</td>
                        <td style="padding: 8px;">%s</td></tr>
                    <tr><td style="padding: 8px; font-weight: bold;">Time</td>
                        <td style="padding: 8px;">%s</td></tr>
                    <tr style="background:#f9f9f9;">
                        <td style="padding: 8px; font-weight: bold;">Duration</td>
                        <td style="padding: 8px;">%d minutes</td></tr>
                  </table>
                  <p style="margin-top: 24px; color: #888; font-size: 12px;">
                    This is an automated message from SkillBridge. Please do not reply.
                  </p>
                </body></html>
                """.formatted(
                greeting,
                event.getBookingId(),
                event.getBookingDate(),
                event.getStartTime(),
                event.getDurationMinutes()
        );
    }

    private String buildCancellationBody(String role, BookingEventDto event) {
        String greeting = "student".equals(role)
                ? "Unfortunately, your mentoring session has been cancelled."
                : "A mentoring session has been cancelled.";

        return """
                <html><body style="font-family: Arial, sans-serif; color: #333;">
                  <h2 style="color: #E24A4A;">SkillBridge — Booking Cancelled ❌</h2>
                  <p>%s</p>
                  <table style="border-collapse: collapse; width: 100%%; max-width: 480px;">
                    <tr><td style="padding: 8px; font-weight: bold;">Booking ID</td>
                        <td style="padding: 8px;">#%d</td></tr>
                    <tr style="background:#f9f9f9;">
                        <td style="padding: 8px; font-weight: bold;">Date</td>
                        <td style="padding: 8px;">%s</td></tr>
                    <tr><td style="padding: 8px; font-weight: bold;">Time</td>
                        <td style="padding: 8px;">%s</td></tr>
                  </table>
                  <p style="margin-top: 16px;">
                    If you have any questions, please contact support.
                  </p>
                  <p style="margin-top: 24px; color: #888; font-size: 12px;">
                    This is an automated message from SkillBridge. Please do not reply.
                  </p>
                </body></html>
                """.formatted(
                greeting,
                event.getBookingId(),
                event.getBookingDate(),
                event.getStartTime()
        );
    }

    private String buildReminderBody(String role, BookingEventDto event) {
        String greeting = "student".equals(role)
                ? "This is a reminder that your mentoring session is coming up."
                : "This is a reminder that you have a mentoring session coming up.";

        return """
                <html><body style="font-family: Arial, sans-serif; color: #333;">
                  <h2 style="color: #E2A24A;">SkillBridge — Session Reminder ⏰</h2>
                  <p>%s</p>
                  <table style="border-collapse: collapse; width: 100%%; max-width: 480px;">
                    <tr><td style="padding: 8px; font-weight: bold;">Booking ID</td>
                        <td style="padding: 8px;">#%d</td></tr>
                    <tr style="background:#f9f9f9;">
                        <td style="padding: 8px; font-weight: bold;">Date</td>
                        <td style="padding: 8px;">%s</td></tr>
                    <tr><td style="padding: 8px; font-weight: bold;">Time</td>
                        <td style="padding: 8px;">%s</td></tr>
                    <tr style="background:#f9f9f9;">
                        <td style="padding: 8px; font-weight: bold;">Duration</td>
                        <td style="padding: 8px;">%d minutes</td></tr>
                  </table>
                  <p style="margin-top: 16px;">
                    Please be ready a few minutes before your session starts.
                  </p>
                  <p style="margin-top: 24px; color: #888; font-size: 12px;">
                    This is an automated message from SkillBridge. Please do not reply.
                  </p>
                </body></html>
                """.formatted(
                greeting,
                event.getBookingId(),
                event.getBookingDate(),
                event.getStartTime(),
                event.getDurationMinutes()
        );
    }
}
