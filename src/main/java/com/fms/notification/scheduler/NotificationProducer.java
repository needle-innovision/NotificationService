package com.fms.notification.scheduler;

import com.fms.notification.dao.NotificationDao;
import com.fms.notification.model.Notification;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class NotificationProducer {

    @Autowired
    private NotificationDao notificationDao;

    @Autowired
    private NotificationBroker notificationBroker;

    @Scheduled(initialDelay = 10000, fixedDelay = 600000)
    public void reload() {
        log.debug("Started loading notifications");
        try {
            List<Notification> notificationList = notificationDao.getNewNotifications();
            log.debug("Total numer of new notifications is: {}", notificationList.size());
            notificationList.stream().forEach(n -> notificationBroker.put(n));
        } catch (Exception e) {
            log.error("Exception occurred while reloading notifications", e);
        }
    }
}
