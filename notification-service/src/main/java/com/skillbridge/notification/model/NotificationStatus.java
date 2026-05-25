package com.skillbridge.notification.model;

/**
 * Whether the email was delivered successfully or failed.
 * Stored as a STRING in the notification_log table.
 */
public enum NotificationStatus {
    SENT,
    FAILED
}
