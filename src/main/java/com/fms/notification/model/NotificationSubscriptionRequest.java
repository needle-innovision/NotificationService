package com.fms.notification.model;

import lombok.*;

import javax.validation.constraints.NotNull;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public abstract class NotificationSubscriptionRequest {
    @NotNull(message = "type must be either push, email, sms")
    private NotificationType type;
    @NotNull(message = "userId must be provided")
    private String userId;

    public abstract String contactPoint();

    public abstract String subscribedEntity();
}
