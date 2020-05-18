package com.fms.notification.service;


import com.fms.notification.firebase.FCMService;
import com.fms.notification.model.Notification;
import com.fms.notification.model.NotificationSenderResponse;
import com.fms.notification.model.PushNotificationRequest;
import com.fms.notification.util.NotificationSendingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import static com.fms.notification.util.JsonMapper.getObject;

@Slf4j
@Service
@Qualifier("PushNotificationSenderService")
public class PushNotificationSenderService implements NotificationSenderService {

    @Autowired
    FCMService fcmService;

    @Autowired
    DateService dateService;

    @Override
    public NotificationSenderResponse send(Notification notification) {
        log.debug("Processing push notification request: {}", notification);
        try {
            String response = fcmService.sendMessage(composeRequest(notification));
            return NotificationSenderResponse.of(notification, "success", response, dateService.current());
        } catch (InterruptedException | ExecutionException | IOException e) {
            log.error("Exception occurred while sending message to firebase", e);
            return NotificationSenderResponse.of(notification, "success", e.getMessage(), dateService.current());
        }
    }

    private PushNotificationRequest composeRequest(Notification notification) throws IOException {
        PushNotificationRequest request = getObject(notification.getNotificationPayload(), PushNotificationRequest.class);
        if (notification.shouldPushToToken()) {
            request.setToken(notification.getAlertDestination());
        } else {
            request.setTopic(notification.getAlertDestination());
        }
        request.setDeviceType(notification.getDeviceType());
        return request;
    }
}
