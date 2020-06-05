package com.fms.notification.service;

import com.fms.notification.exception.EntityNotFoundException;
import com.fms.notification.exception.NotificationSubscriptionException;
import com.fms.notification.firebase.FCMService;
import com.fms.notification.model.NotificationSubscription;
import com.fms.notification.model.NotificationSubscriptionRequest;
import com.fms.notification.model.PushNotificationSubscriptionRequest;
import com.fms.notification.repository.NotificationSubscriptionRepository;
import com.google.firebase.messaging.FirebaseMessagingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.Valid;
import java.util.Objects;
import java.util.Optional;

import static com.fms.notification.service.NotificationSubscriptionService.create;

@Slf4j
@Service
public class PushNotificationSubscriptionService implements NotificationSubscriptionService {

    @Autowired
    FCMService fcmService;

    @Autowired
    NotificationSubscriptionRepository notificationSubscriptionRepository;

    @Autowired
    DateService dateService;

    @Override
    public NotificationSubscription subscribe(@Valid NotificationSubscriptionRequest request) {
        validate(request);
        PushNotificationSubscriptionRequest pushRequest = (PushNotificationSubscriptionRequest) request;
        try {
            fcmService.subscribeToTopic(pushRequest);
            NotificationSubscription notificationSubscription = create(request, true, dateService.current());
            return notificationSubscriptionRepository.save(notificationSubscription);
        } catch (FirebaseMessagingException e) {
            throw new NotificationSubscriptionException(e.getMessage());
        }
    }

    @Override
    public NotificationSubscription unsubscribe(@Valid NotificationSubscriptionRequest request) {
        validate(request);
        PushNotificationSubscriptionRequest pushRequest = (PushNotificationSubscriptionRequest) request;
        try {
            fcmService.unsubscribeFromTopic(pushRequest);
            Optional<NotificationSubscription> optionalNotificationSubscription =
                    notificationSubscriptionRepository.findOneByUserIdAndTypeAndContactPointAndSubscribedEntity(request.getUserId(),
                            request.getType().toString(), request.contactPoint(), request.subscribedEntity());
            if(optionalNotificationSubscription.isPresent()) {
                throw new EntityNotFoundException(NotificationSubscription.class, "userId", request.getUserId(),
                        "type", request.getType().toString(), "contactPoint", request.contactPoint(), "subscribedEntity",
                        request.subscribedEntity());
            }
            NotificationSubscription notificationSubscription = optionalNotificationSubscription.get();
            notificationSubscription.setSubscribed(false);
            notificationSubscription.setUpdatedAt(dateService.current());
            return notificationSubscriptionRepository.save(notificationSubscription);
        } catch (FirebaseMessagingException e) {
            throw new NotificationSubscriptionException(e.getMessage());
        }
    }

    private void validate(@Valid NotificationSubscriptionRequest request) {
        if(Objects.isNull(request) || !(request instanceof PushNotificationSubscriptionRequest))
            throw new IllegalArgumentException("Invalid request is provided");
    }
}
