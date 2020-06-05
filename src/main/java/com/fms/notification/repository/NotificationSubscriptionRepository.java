package com.fms.notification.repository;

import com.fms.notification.model.NotificationSubscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface NotificationSubscriptionRepository extends JpaRepository<NotificationSubscription, Integer> {
    Optional<NotificationSubscription> findOneByUserIdAndTypeAndContactPointAndSubscribedEntity(String userId,
                                                                                                String type,
                                                                                                String contactPoint,
                                                                                                String subscribedEntity);

}
