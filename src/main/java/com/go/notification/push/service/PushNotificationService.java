package com.go.notification.push.service;

import java.sql.SQLException;

import com.ne.commons.notification.vo.MblPushResponseVO;
import com.ne.commons.notification.vo.PushNotificationVO;
import com.ne.commons.notification.vo.PushResponseVO;

public interface PushNotificationService {
	void pushNotification(PushNotificationVO pushNotification, PushResponseVO pushResponse) throws SQLException, Exception;

	PushResponseVO constructPushResponse(PushNotificationVO pushNotification);

	MblPushResponseVO constructMblPushResponse(PushNotificationVO pushNotification);

	void mblPushNotification(PushNotificationVO pushNotification, String mblResponse) throws SQLException;

	PushResponseVO constructPushResponseWithTitle(PushNotificationVO pushNotification);
}
