package com.fms.notification.service;


import com.fms.notification.model.Notification;
import com.fms.notification.model.NotificationSenderResponse;
import com.fms.notification.util.NotificationSendingException;

public interface NotificationSenderService {
    NotificationSenderResponse send(Notification notification) throws NotificationSendingException;
}
