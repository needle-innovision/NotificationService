package com.fms.notification.model;

import lombok.*;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Entity
@ToString
@Table(name = "notification_subscription")
public class NotificationSubscription extends BaseModel {
    @javax.persistence.Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private int id;
    @Column(name = "userId")
    private String userId;
    @Column(name = "type")
    private String type;
    @Column(name = "contactPoint")
    private String contactPoint;
    @Column(name = "subscribedEntity")
    private String subscribedEntity;
    @Column(name = "subscribed")
    private boolean subscribed;
}
