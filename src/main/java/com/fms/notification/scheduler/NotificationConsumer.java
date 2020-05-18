package com.fms.notification.scheduler;

import com.fms.notification.model.Notification;
import com.fms.notification.service.NotificationHandlerService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class NotificationConsumer implements Runnable {

    @Autowired
    private NotificationBroker notificationRequestBroker;

    @Autowired
    private NotificationHandlerService notificationHandlerService;

    @Override
    public void run() {
        while (true) {
            try {
                Notification notification = notificationRequestBroker.take();
                if (Objects.isNull(notification)) {
                    sleepForSecs(2);
                    continue;
                }
                notificationHandlerService.handleAsync(notification);

            } catch (Exception e) {
                log.error("Exception occurred in NotificationConsumer",e);
            }
        }
    }

    @SneakyThrows
    private void sleepForSecs(int sleepTimeout) {
        TimeUnit.SECONDS.sleep(sleepTimeout);
    }
}
