package com.go.notification.push.service;

import java.sql.SQLException;

import com.ne.commons.notification.vo.MblPushResponseVO;
import com.ne.commons.notification.vo.PushNotificationVO;

public interface MblPushNotificationService {

	public MblPushResponseVO constructMblPushResponse(PushNotificationVO pushNotification);
	public void mblPushNotification(PushNotificationVO pushNotification, MblPushResponseVO mblPushResponse) throws SQLException, Exception;
}
