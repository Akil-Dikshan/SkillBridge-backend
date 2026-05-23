package com.skillbridge.notification.service;

import com.skillbridge.notification.event.BookingEventDto;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

/**
 * Sends transactional HTML emails for all booking-related events.
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

    @Value("${notification.mail.from:noreply@skillbridge.com}")
    private String fromAddress;

    // ─── Public API ──────────────────────────────────────────────────────────

    /**
     * Sends a booking confirmation email to both the student and the mentor.
     */
    public void sendBookingConfirmation(BookingEventDto event) {
        sendEmail(
                event.getStudentEmail(),
                "SkillBridge — Your Booking is Confirmed!",
                buildConfirmationBody("student", event)
        );
        sendEmail(
                event.getMentorEmail(),
                "SkillBridge — You Have a New Booking",
                buildConfirmationBody("mentor", event)
        );
    }

    /**
     * Sends a cancellation email to both the student and the mentor.
     */
    public void sendBookingCancellation(BookingEventDto event) {
        sendEmail(
                event.getStudentEmail(),
                "SkillBridge — Booking Cancelled",
                buildCancellationBody("student", event)
        );
        sendEmail(
                event.getMentorEmail(),
                "SkillBridge — Booking Cancelled",
                buildCancellationBody("mentor", event)
        );
    }

    /**
     * Sends a session reminder email to both the student and the mentor.
     */
    public void sendSessionReminder(BookingEventDto event) {
        sendEmail(
                event.getStudentEmail(),
                "SkillBridge — Reminder: Your Session is Tomorrow",
                buildReminderBody("student", event)
        );
        sendEmail(
                event.getMentorEmail(),
                "SkillBridge — Reminder: You Have a Session Tomorrow",
                buildReminderBody("mentor", event)
        );
    }

    // ─── Private Helpers ─────────────────────────────────────────────────────

    /**
     * Core send method. All public methods delegate here.
     * On MessagingException the error is logged but NOT re-thrown —
     * a failed email must never crash the consumer or requeue the message.
     */
    private void sendEmail(String to, String subject, String htmlBody) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            // true  = multipart (required for HTML)
            // UTF-8 = prevents character encoding issues in names / subjects
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(fromAddress);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlBody, true); // true = treat body as HTML
            mailSender.send(message);
            log.info("✉️  Email sent     → to={} subject={}", to, subject);
        } catch (MessagingException e) {
            log.error("❌ Email failed    → to={} subject={} error={}", to, subject, e.getMessage());
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
