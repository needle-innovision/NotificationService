package com.go.notification.push.service;

import java.sql.SQLException;
import java.util.List;

import com.go.notification.push.models.PushSubscription;
import com.ne.commons.notification.vo.MblPushResponseVO;

public interface PushSubscriptionService {
	boolean saveOrUpdatePushSubscription(PushSubscription pushModel, long userId, String pushType) throws SQLException;
	
	void sendPushNotification(List<PushSubscription> pushSubList, String pushResponse);
	
	boolean removeSubscription(long userId, String uniqueId) throws SQLException;
	
	boolean getSubscriptionStatus(long userId, String uniqueId) throws SQLException;

	void sendMobileAppPushNotification(List<PushSubscription> mobileAppPushSubscriptionList, String mblPushResponse, String title, String body);

}
