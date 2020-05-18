package com.fms.notification.model;

import lombok.*;
import org.springframework.util.StringUtils;

import javax.persistence.*;
import java.sql.Timestamp;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Entity
@ToString
@Table(name = "notification")
public class Notification extends BaseModel {
    @javax.persistence.Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private int id;
    @Column(name = "userId")
    private String userId;
    @Column(name = "notificationMessageId")
    private int notificationMessageId;
    @Column(name = "notificationPayload")
    private String notificationPayload;
    @Column(name = "destinationType")
    private String destinationType;
    @Column(name = "userDeviceId")
    private int userDeviceId;
    @Column(name = "alertDestination")
    private String alertDestination;
    @Column(name = "deviceType")
    private String deviceType;
    private String status;
    private int numTrials;
    @Column(name = "messageSentAt")
    private Timestamp messageSentAt;
    @Column(name = "hasUserDeletedNotification")
    private boolean hasUserDeletedNotification;

    public boolean shouldPushToToken() {
        if(!StringUtils.isEmpty(userDeviceId) && !StringUtils.isEmpty(alertDestination))
            return true;
        return false;
    }
}
