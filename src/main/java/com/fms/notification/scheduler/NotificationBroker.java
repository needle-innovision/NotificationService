package com.fms.notification.scheduler;

import com.fms.notification.model.Notification;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Deque;
import java.util.concurrent.ConcurrentLinkedDeque;

@Slf4j
@Component
public class NotificationBroker {

    private Deque<Notification> notificationsQueue;

    @PostConstruct
    public void init() {
        notificationsQueue = new ConcurrentLinkedDeque<Notification>();
    }

    public void put(Notification notification) {
        try {
            log.debug("Adding a notification: {} into queue", notification);
            notificationsQueue.offer(notification);
        } catch (Exception e) {
            log.error("Unable to add notification: {} into queue", notification, e);
        }
    }

    public Notification take() {
        Notification notification = notificationsQueue.poll();
        log.debug("Popped off a notification: {} from queue", notification);
        return notification;
    }
}
