package com.go.notification.dao;

import java.sql.SQLException;
import java.util.List;

import com.go.notification.push.models.PushSubscription;

public interface PushServiceDAO {
	boolean saveOrUpdateSubscription(PushSubscription pushModel, long userId, String pushType) throws SQLException;
	
	List<PushSubscription> getWebPushModel(List<Long> userIds) throws SQLException;
	
	boolean removeSubscriptionRecord(long userId, String uniqueId) throws SQLException;
	
	boolean getSubscriptionStatus(long userId, String uniqueId) throws SQLException;

	List<PushSubscription> getMobileAppPushModel(List<Long> userIds) throws SQLException;
}
