package com.fms.notification.service;

import com.fms.notification.model.Notification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class NotificationSenderFactory {
    @Autowired
    @Qualifier("PushNotificationSenderService")
    private NotificationSenderService notificationSenderService;

    public NotificationSenderService getSevice(Notification notification) {
        switch (notification.getDestinationType()) {
            case "PUSH":
                return notificationSenderService;
            default:
                throw new IllegalArgumentException("Unable to find correct NotificationService");
        }
    }
}
