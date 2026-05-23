package com.skillbridge.notification.service;

import com.skillbridge.notification.event.BookingEventDto;
import com.skillbridge.notification.model.NotificationLog;
import com.skillbridge.notification.model.NotificationStatus;
import com.skillbridge.notification.model.NotificationType;
import com.skillbridge.notification.repository.NotificationLogRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private NotificationLogRepository notificationLogRepository;

    @InjectMocks
    private EmailService emailService;

    @BeforeEach
    void setUp() {
        // Inject the @Value field that Spring would normally populate
        ReflectionTestUtils.setField(emailService, "fromAddress", "noreply@skillbridge.com");

        // mailSender.createMimeMessage() must return a real MimeMessage
        // because MimeMessageHelper inspects it internally
        when(mailSender.createMimeMessage()).thenReturn(mock(MimeMessage.class));
    }

    private BookingEventDto buildEvent() {
        return BookingEventDto.builder()
                .bookingId(1L)
                .studentId(10L)
                .studentEmail("student@test.com")
                .mentorId(20L)
                .mentorEmail("mentor@test.com")
                .bookingDate(LocalDate.of(2026, 6, 15))
                .startTime(LocalTime.of(10, 0))
                .durationMinutes(60)
                .eventType("CONFIRMED")
                .build();
    }

    @Test
    void sendBookingConfirmation_sendsEmailsToStudentAndMentor() {
        emailService.sendBookingConfirmation(buildEvent());

        // Two emails sent — one to student, one to mentor
        verify(mailSender, times(2)).send(any(MimeMessage.class));
    }

    @Test
    void sendBookingConfirmation_savesTwoSentLogs() {
        emailService.sendBookingConfirmation(buildEvent());

        ArgumentCaptor<NotificationLog> captor = ArgumentCaptor.forClass(NotificationLog.class);
        verify(notificationLogRepository, times(2)).save(captor.capture());

        List<NotificationLog> saved = captor.getAllValues();

        assertThat(saved).allMatch(log -> log.getStatus() == NotificationStatus.SENT);
        assertThat(saved).allMatch(log -> log.getNotificationType() == NotificationType.BOOKING_CONFIRMED);
        assertThat(saved).allMatch(log -> log.getBookingId().equals(1L));
        assertThat(saved).extracting(NotificationLog::getRecipientEmail)
                .containsExactlyInAnyOrder("student@test.com", "mentor@test.com");
    }

    @Test
    void sendBookingCancellation_sendsEmailsToStudentAndMentor() {
        emailService.sendBookingCancellation(buildEvent());

        verify(mailSender, times(2)).send(any(MimeMessage.class));
    }

    @Test
    void sendBookingCancellation_savesTwoSentLogs() {
        emailService.sendBookingCancellation(buildEvent());

        ArgumentCaptor<NotificationLog> captor = ArgumentCaptor.forClass(NotificationLog.class);
        verify(notificationLogRepository, times(2)).save(captor.capture());

        List<NotificationLog> saved = captor.getAllValues();
        assertThat(saved).allMatch(log -> log.getStatus() == NotificationStatus.SENT);
        assertThat(saved).allMatch(log -> log.getNotificationType() == NotificationType.BOOKING_CANCELLED);
    }

    @Test
    void sendSessionReminder_sendsEmailsToStudentAndMentor() {
        emailService.sendSessionReminder(buildEvent());

        verify(mailSender, times(2)).send(any(MimeMessage.class));
    }

    @Test
    void sendSessionReminder_savesTwoSentLogs() {
        emailService.sendSessionReminder(buildEvent());

        ArgumentCaptor<NotificationLog> captor = ArgumentCaptor.forClass(NotificationLog.class);
        verify(notificationLogRepository, times(2)).save(captor.capture());

        List<NotificationLog> saved = captor.getAllValues();
        assertThat(saved).allMatch(log -> log.getStatus() == NotificationStatus.SENT);
        assertThat(saved).allMatch(log -> log.getNotificationType() == NotificationType.SESSION_REMINDER);
    }

    @Test
    void sendBookingConfirmation_whenMailFails_savesFailedLog() {
        doThrow(new RuntimeException("SMTP unavailable"))
                .when(mailSender).send(any(MimeMessage.class));

        // Should not throw — failed emails must never crash the consumer
        emailService.sendBookingConfirmation(buildEvent());

        ArgumentCaptor<NotificationLog> captor = ArgumentCaptor.forClass(NotificationLog.class);
        verify(notificationLogRepository, atLeastOnce()).save(captor.capture());

        assertThat(captor.getAllValues())
                .allMatch(log -> log.getStatus() == NotificationStatus.FAILED);
    }
}
