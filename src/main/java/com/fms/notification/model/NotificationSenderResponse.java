package com.fms.notification.model;


import lombok.*;

import java.sql.Timestamp;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class NotificationSenderResponse {
    private Notification notification;
    private String status;
    private String response;
    private Timestamp messageSentAt;

    public static NotificationSenderResponse of(Notification notification, String status, String response, Timestamp messageSentAt) {
        return new NotificationSenderResponse(notification, status, response, messageSentAt);
    }

    public Notification getNotificationModel() {
        notification.setStatus(status);
        notification.setMessageSentAt(messageSentAt);
        notification.setNumTrials(notification.getNumTrials() + 1);
        return notification;
    }
}
