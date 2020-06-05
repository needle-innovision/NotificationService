package com.fms.notification.model;

import lombok.*;

import javax.validation.constraints.NotNull;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class PushNotificationSubscriptionRequest extends NotificationSubscriptionRequest {
    @NotNull(message = "device token must not be null")
    private String deviceToken;
    @NotNull(message = "topic name must not be null")
    private String topic;

    @Override
    public String contactPoint() {
        return topic;
    }

    @Override
    public String subscribedEntity() {
        return deviceToken;
    }
}
