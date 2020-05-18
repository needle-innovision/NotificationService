package com.fms.notification.service;

import com.fms.notification.model.Notification;

public interface NotificationHandlerService {

    void handleAsync(Notification notification);
}
