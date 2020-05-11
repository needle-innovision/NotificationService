package com.go.notification.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.go.notification.constants.NotificationTypes;
import com.go.notification.push.service.MblPushNotificationService;
import com.go.notification.push.service.PushNotificationService;
import com.go.notification.utils.JsonConverterUtility;
import com.google.common.eventbus.Subscribe;
import com.ne.commons.notification.vo.MblPushResponseVO;
import com.ne.commons.notification.vo.PushNotificationVO;
import com.ne.commons.notification.vo.PushResponseVO;

/**
 * Receives push notification from kafka subscriber Then process the
 * notification
 * 
 * @author santhosh.gudla
 *
 */
@Component
public class PushNotificationListener {

	private static final Logger log = LoggerFactory.getLogger(PushNotificationListener.class);

	@Autowired
	PushNotificationService pushNotificationService;

	@Autowired
	MblPushNotificationService mblPushNotificationService;

	@Subscribe
	public void processPushNotification(PushNotificationVO pushNotification) {
		if (pushNotification.getNotificationType().equalsIgnoreCase((NotificationTypes.MBPUSH.toString()))) {
			PushResponseVO pushResponse = pushNotificationService.constructPushResponseWithTitle(pushNotification);
			MblPushResponseVO mblPushResponse = mblPushNotificationService.constructMblPushResponse(pushNotification);
			mblPushResponse.setTitle(pushResponse.getTitle());
			mblPushResponse.setBodyText(pushResponse.getBodyText());
			try {
				log.info("inside mbl pushNotification subscriber" + mblPushResponse);
//				String mblResponse = JsonConverterUtility.mapToJson(mblPushResponse);
				mblPushNotificationService.mblPushNotification(pushNotification, mblPushResponse);
			} catch (Exception e) {
				log.error("Error while converting mbl push response to json string:\n", e);
			}

		} 
		if(pushNotification.getNotificationType().equalsIgnoreCase((NotificationTypes.PUSH.toString())))  {
			PushResponseVO pushResponse = pushNotificationService.constructPushResponse(pushNotification);
			try {
				log.info("inside pushNotification subscriber" + pushResponse);
//				String response = JsonConverterUtility.mapToJson(pushResponse);
				pushNotificationService.pushNotification(pushNotification, pushResponse);
			} catch (Exception e) {
				log.error("Error while converting push response to json string:\n", e);
			}
		}

	}

	/*
	 * @Subscribe public void processMblPushNotification(PushNotificationVO
	 * pushNotification) { MblPushResponseVO
	 * mblPushResponse=pushNotificationService.constructMblPushResponse(
	 * pushNotification); try {
	 * log.info("inside pushNotification subscriber"+mblPushResponse); String
	 * mblResponse = JsonConverterUtility.mapToJson(mblPushResponse);
	 * pushNotificationService.mblPushNotification(pushNotification, mblResponse); }
	 * catch (Exception e) {
	 * log.error("Error while converting push response to json string"); } }
	 */
}
