package com.fms.notification.model;

import lombok.*;

import javax.annotation.Nullable;
import javax.validation.constraints.Null;
import java.util.Map;

@Builder
@Getter
@Setter
@ToString
@AllArgsConstructor
public class PushNotificationRequest {

    private String title;
    private String message;
    @Nullable
    private String deviceType;
    @Nullable
    private String topic;
    @Nullable
    private String token;
    @Nullable
    private Map<String, String> data;

    public PushNotificationRequest() {
    }

    public PushNotificationRequest(String title, String messageBody, String topicName) {
        this.title = title;
        this.message = messageBody;
        this.topic = topicName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
