package com.fms.notification.exception;

public class NotificationSubscriptionException extends ApplicationError {
    public NotificationSubscriptionException(Exception e) {
        super(e);
    }

    public NotificationSubscriptionException(String message) {
        super(message);
    }
}
