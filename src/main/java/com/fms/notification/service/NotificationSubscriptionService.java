package com.fms.notification.service;

import com.fms.notification.model.NotificationSubscription;
import com.fms.notification.model.NotificationSubscriptionRequest;

import javax.validation.Valid;
import java.sql.Timestamp;

public interface NotificationSubscriptionService {

    NotificationSubscription subscribe(@Valid NotificationSubscriptionRequest request);

    NotificationSubscription unsubscribe(@Valid NotificationSubscriptionRequest request);

    static NotificationSubscription create(NotificationSubscriptionRequest request,
                                           boolean subscribed,
                                           Timestamp currentTimestamp) {
        NotificationSubscription notificationSubscription = NotificationSubscription
                .builder()
                .userId(request.getUserId())
                .type(request.getType().toString())
                .contactPoint(request.contactPoint())
                .subscribedEntity(request.subscribedEntity())
                .subscribed(subscribed)
                .build();
        notificationSubscription.setCreatedAt(currentTimestamp);
        return notificationSubscription;
    }
}
