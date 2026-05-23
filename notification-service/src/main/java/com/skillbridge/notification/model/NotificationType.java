package com.skillbridge.notification.model;

/**
 * The type of notification event that triggered an email.
 * Stored as a STRING in the notification_log table.
 */
public enum NotificationType {
    BOOKING_CONFIRMED,
    BOOKING_CANCELLED,
    SESSION_REMINDER
}
