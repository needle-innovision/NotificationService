package com.fms.notification.model;


import com.fasterxml.jackson.annotation.JsonProperty;

public enum NotificationType {
    @JsonProperty("push")
    PUSH,
    @JsonProperty("email")
    EMAIL,
    @JsonProperty("sms")
    SMS
}
