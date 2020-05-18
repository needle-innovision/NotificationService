package com.fms.notification.repository;


import com.fms.notification.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Integer> {

    @Query("select n from Notification n where messageSentAt is null")
    List<Notification> findNewNotifications();

}
