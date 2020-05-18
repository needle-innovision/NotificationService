package com.fms.notification.dao;


import com.fms.notification.model.Notification;
import com.fms.notification.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class NotificationDao {

    @Autowired
    private NotificationRepository notificationRepository;

    public Notification save(Notification notificationModel) {
        return notificationRepository.save(notificationModel);
    }

    public List<Notification> getNewNotifications() {
        return notificationRepository.findNewNotifications();
    }
}
