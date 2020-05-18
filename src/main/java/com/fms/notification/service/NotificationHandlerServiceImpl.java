package com.fms.notification.service;

import com.fms.notification.dao.NotificationDao;
import com.fms.notification.model.Notification;
import com.fms.notification.model.NotificationSenderResponse;
import com.fms.notification.util.NotificationSendingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
public class NotificationHandlerServiceImpl implements NotificationHandlerService {

    @Autowired
    private NotificationSenderFactory notificationSenderFactory;

    @Autowired
    private NotificationDao notificationDao;

    @Override
    public void handleAsync(Notification notification) {
        CompletableFuture.runAsync(() -> handle(notification));
    }

    private void handle(Notification notification) {
        log.info("Processing notification: {}", notification);
        try {
            NotificationSenderResponse response = send(notification);
            log.debug("Got response: {} for the notification: {}", response, notification);
            Notification savedNotification = notificationDao.save(response.getNotificationModel());
            log.debug("Saved notification is: {}", savedNotification);
        } catch (Exception e) {
            log.error("Exception occurred while handling the notification: {}", notification, e);
        }
    }

    private NotificationSenderResponse send(Notification notification) {
        NotificationSenderResponse response = null;
        try {
            NotificationSenderService notificationSenderService =
                    notificationSenderFactory.getSevice(notification);
            response =
                    notificationSenderService.send(notification);
            log.debug("Got response: {} for the notification: {}", response, notification);
        } catch (Exception e) {
            log.error("Unable to send the given notification: {}", notification, e);
        }
        return response;
    }
}
