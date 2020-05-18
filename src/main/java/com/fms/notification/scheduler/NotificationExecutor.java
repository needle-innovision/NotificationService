package com.fms.notification.scheduler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

@Slf4j
@Component
public class NotificationExecutor {

//    @Value("notification.consumer.execution.num.threads")
//    private int nThreads;

    @Autowired
    private NotificationConsumer notificationConsumer;

    private ExecutorService executorService;

    @PostConstruct
    public void init() {
        executorService = Executors.newFixedThreadPool(1);
        IntStream.range(0, 1).forEach(i -> executorService.submit(notificationConsumer));
    }
}
