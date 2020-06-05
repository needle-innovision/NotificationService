package com.fms.notification.firebase;

import com.fms.notification.exception.ApplicationError;
import com.fms.notification.model.PushNotificationRequest;
import com.fms.notification.model.PushNotificationSubscriptionRequest;
import com.google.firebase.messaging.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.Duration;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

@Service
public class FCMService {

    private Logger logger = LoggerFactory.getLogger(FCMService.class);

    public void sendMessage(Map<String, String> data, PushNotificationRequest request)
            throws InterruptedException, ExecutionException {
        Message message = getPreconfiguredMessageWithData(data, request);
        String response = sendAndGetResponse(message);
        logger.info("Sent message with data. Topic: " + request.getTopic() + ", " + response);
    }

    public void sendMessageWithoutData(PushNotificationRequest request)
            throws InterruptedException, ExecutionException {
        Message message = getPreconfiguredMessageWithoutData(request);
        String response = sendAndGetResponse(message);
        logger.info("Sent message without data. Topic: " + request.getTopic() + ", " + response);
    }

    private String sendAndGetResponse(Message message) throws InterruptedException, ExecutionException {

        return FirebaseMessaging.getInstance().sendAsync(message).get();
    }

    public void sendMessageToToken(PushNotificationRequest request)
            throws InterruptedException, ExecutionException {
        Message message = getPreconfiguredMessageToToken(request);
        String response = sendAndGetResponse(message);
        logger.info("Sent message to token. Device token: " + request.getToken() + ", " + response);
    }

    private AndroidConfig getAndroidConfig(String topic) {
        return AndroidConfig.builder()
                .setTtl(Duration.ofMinutes(2).toMillis()).setCollapseKey(topic)
                .setPriority(AndroidConfig.Priority.HIGH)
                .setNotification(AndroidNotification.builder().setSound(NotificationParameter.SOUND.getValue())
                        .setColor(NotificationParameter.COLOR.getValue()).setTag(topic).build()).build();
    }

    private ApnsConfig getApnsConfig(String topic) {
        return ApnsConfig.builder()
                .setAps(Aps.builder().setCategory(topic).setThreadId(topic).build()).build();
    }

    private Message getPreconfiguredMessageToToken(PushNotificationRequest request) {
        return getPreconfiguredMessageBuilder(request).setToken(request.getToken())
                .build();
    }

    private Message getPreconfiguredMessageWithoutData(PushNotificationRequest request) {
        return getPreconfiguredMessageBuilder(request).setTopic(request.getTopic())
                .build();
    }

    private Message getPreconfiguredMessageWithData(Map<String, String> data, PushNotificationRequest request) {
        return getPreconfiguredMessageBuilder(request).putAllData(data).setTopic(request.getTopic())
                .build();
    }

    private Message.Builder getPreconfiguredMessageBuilder(PushNotificationRequest request) {
        AndroidConfig androidConfig = getAndroidConfig(request.getTopic());
//        ApnsConfig apnsConfig = getApnsConfig(request.getTopic());
        /*return Message.builder()
                .setApnsConfig(apnsConfig).setAndroidConfig(androidConfig).setNotification(
                        new Notification(request.getTitle(), request.getMessage()));*/
//        return Message.builder()
//                .setAndroidConfig(androidConfig).setNotification(
//                        new Notification(request.getTitle(), request.getMessage()));
        return Message.builder().setNotification(
                        new Notification(request.getTitle(), request.getMessage()));
    }

    public String sendMessage(PushNotificationRequest request) throws ExecutionException, InterruptedException {
        Message message = configureMessageBuilder(request);
        logger.info("Configure message: {} for given request: {}", message, request);
        try {
            return sendAndGetResponse(message);
        } catch (InterruptedException | ExecutionException e) {
           throw e;
        }
    }

    private Message configureMessageBuilder(PushNotificationRequest request) {
        Message.Builder builder = Message.builder()
                .setNotification(new Notification(request.getTitle(), request.getMessage()));
        setConfig(request, builder);
        setData(request, builder);
        setTopic(request, builder);
        setToken(request, builder);
        return builder.build();
    }

    private void setToken(PushNotificationRequest request, Message.Builder builder) {
        if(StringUtils.isEmpty(request.getToken())) return;
        builder.setToken(request.getToken());
    }

    private void setTopic(PushNotificationRequest request, Message.Builder builder) {
        if(StringUtils.isEmpty(request.getTopic())) return;
        builder.setTopic(request.getTopic());
    }

    private void setData(PushNotificationRequest request, Message.Builder builder) {
        if(Objects.isNull(request.getData())) return;
        builder.putAllData(request.getData());
    }

    private void setConfig(PushNotificationRequest request, Message.Builder builder) {
        if(StringUtils.isEmpty(request.getTopic())) return;
        if (request.getDeviceType().equalsIgnoreCase("andriod")) {
            builder.setAndroidConfig(getAndroidConfig(request.getTopic()));
        } else {
            builder.setApnsConfig(getApnsConfig(request.getTopic()));
        }
    }


    public void subscribeToTopic(PushNotificationSubscriptionRequest pushRequest) throws FirebaseMessagingException {
        TopicManagementResponse topicManagementResponse =
                FirebaseMessaging.getInstance().subscribeToTopic(Arrays.asList(pushRequest.getDeviceToken()), pushRequest.getTopic());
        if(Objects.isNull(topicManagementResponse) || topicManagementResponse.getSuccessCount() <= 0) {
            throw new ApplicationError(String.format("Unable to subscribe to topic = %s", pushRequest.getTopic()));
        }
    }

    public void unsubscribeFromTopic(PushNotificationSubscriptionRequest pushRequest) throws FirebaseMessagingException {
        TopicManagementResponse topicManagementResponse =
                FirebaseMessaging.getInstance().unsubscribeFromTopic(Arrays.asList(pushRequest.getDeviceToken()), pushRequest.getTopic());
        if(Objects.isNull(topicManagementResponse) || topicManagementResponse.getSuccessCount() <= 0) {
            throw new ApplicationError(String.format("Unable to unsubscribe from topic = %s", pushRequest.getTopic()));
        }
    }
}
