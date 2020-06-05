package com.fms.notification.controller;

import com.fms.notification.model.NotificationSubscription;
import com.fms.notification.model.PushNotificationSubscriptionRequest;
import com.fms.notification.service.NotificationSubscriptionService;
import com.fms.notification.vo.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static com.fms.notification.util.Constants.buildResponseEntity;

@Slf4j
@RestController
@RequestMapping("/notifications")
public class PushNotificationSubscriptionController {

    @Autowired
    NotificationSubscriptionService notificationSubscriptionService;

    @PostMapping("/topic/subscribe")
    public ResponseEntity<Response> subscribeToTopic(@RequestBody @Valid PushNotificationSubscriptionRequest request) {
        log.info("Call for subscribing request = {}", request);
        NotificationSubscription notificationSubscription =
                notificationSubscriptionService.subscribe(request);
        return buildResponseEntity(notificationSubscription, HttpStatus.CREATED);
    }

    @PutMapping("/topic/unsubscribe")
    public ResponseEntity<Response> unsubscribeFromTopic(@RequestBody @Valid PushNotificationSubscriptionRequest request) {
        log.info("Call for unsubscribing request = {}", request);
        NotificationSubscription notificationSubscription =
                notificationSubscriptionService.unsubscribe(request);
        return buildResponseEntity(notificationSubscription, HttpStatus.CREATED);
    }
}
