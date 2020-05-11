package com.go.notification.push.service;

import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.go.notification.dao.PushServiceDAO;
import com.go.notification.push.models.PushSubscription;
import com.go.notification.utils.JsonConverterUtility;
import com.ne.commons.constants.EventConstants;
import com.ne.commons.events.ChangeEvent;
import com.ne.commons.events.InviteParticipantEvent;
import com.ne.commons.events.ZoomEvent;
import com.ne.commons.notification.vo.MblPushResponseVO;
import com.ne.commons.notification.vo.PushNotificationVO;
import com.ne.commons.notification.vo.User;

@Service
public class MblPushNotificationServiceImpl implements MblPushNotificationService {

	private static Logger log = LoggerFactory.getLogger(MblPushNotificationServiceImpl.class);

	@Autowired
	PushServiceDAO pushServiceDAO;

	@Autowired
	PushSubscriptionService pushSubscriptionService;

	@Value("${push.notification.image-url}")
	String imageUrl;

	@Override
	public MblPushResponseVO constructMblPushResponse(PushNotificationVO pushNotification) {
		log.info("inside the mobilepushResponse obj");
		MblPushResponseVO pushResponse = new MblPushResponseVO();
		if (pushNotification.getBaseEvent().getUser() != null) {
			pushResponse.setUser(pushNotification.getBaseEvent().getUser());
		}

		log.info("step1--->");
		pushResponse.setChannelId(pushNotification.getBaseEvent().getChannelId());
		pushResponse.setChannelName(pushNotification.getBaseEvent().getChannelName());
		pushResponse.setOrgUniqueId(pushNotification.getBaseEvent().getOrgUniqueId());
		pushResponse.setOrgTypeId(Long.toString(pushNotification.getBaseEvent().getOrgTypeId()));
		pushResponse.setConversationId(pushNotification.getEntityId());
		pushResponse.setAction(pushNotification.getAction());
		pushResponse.setActionType(pushNotification.getBaseEvent().getActionType());
		pushResponse.setEventType(pushNotification.getBaseEvent().getEventType());
		pushResponse.setConversationTitle(pushNotification.getBaseEvent().getEventName());
		pushResponse.setEventName(pushNotification.getBaseEvent().getEventName());
		pushResponse.setEventId(pushNotification.getBaseEvent().getEntityId());
		pushResponse.setEventType(pushNotification.getBaseEvent().getEventType());
		log.info("step---2");
		if (pushNotification.getAction().equalsIgnoreCase(EventConstants.CONVERSATION_CHANGED_STATUS.name())
				|| pushNotification.getAction().equalsIgnoreCase(EventConstants.CONVERSATION_ASSIGNED_OWNER.name())
				|| pushNotification.getAction().equalsIgnoreCase(EventConstants.QUEUE_ROUTING.name())
				|| pushNotification.getAction().equalsIgnoreCase(EventConstants.CONVERSATION_AUTOROUTING_QUEUE.name())
				|| pushNotification.getAction().equalsIgnoreCase(EventConstants.CONVERSATION_CLOSED.name())
				|| pushNotification.getAction().equalsIgnoreCase(EventConstants.CONVERSATION_CHANGED_STATUS.name())
				|| pushNotification.getAction().equalsIgnoreCase(EventConstants.CONVERSATION_CHANGED_OWNER.name())
				|| pushNotification.getAction().equalsIgnoreCase(EventConstants.CONVERSATION_ASSIGNED_OWNER.name())
				|| pushNotification.getAction().equalsIgnoreCase(EventConstants.CONVERSATION_TICKET_CHANGED.name())
				|| pushNotification.getAction()
						.equalsIgnoreCase(EventConstants.CONVERSATION_TASK_CHANGED_STATUS.name())) {
			ChangeEvent changeEvent = (ChangeEvent) pushNotification.getBaseEvent();
			pushResponse.setConversationTitle(pushNotification.getBaseEvent().getEntityName());
			pushResponse.setNewValue(changeEvent.getNewValue());
			pushResponse.setOldValue(changeEvent.getOldValue());
		} else if (pushNotification.getAction().equalsIgnoreCase(EventConstants.NEW_CONVERSATION_MESSAGE.name())
				|| pushNotification.getAction().equalsIgnoreCase(EventConstants.CONVERSATION_FILE_ATTACHED.name())) {
			pushResponse.setConversationTitle(pushNotification.getBaseEvent().getEntityName());
			pushResponse.setMessage(pushNotification.getBaseEvent().getEventName());
			pushResponse.setMessageId(pushNotification.getBaseEvent().getEventId());
		} else if (pushNotification.getAction().equalsIgnoreCase(EventConstants.CONVERSATION_WARROOM_ENDED.name())
				|| pushNotification.getAction().equalsIgnoreCase(EventConstants.CONVERSATION_WARROOM_STARTED.name())) {
			ZoomEvent zoomEvent = (ZoomEvent) pushNotification.getBaseEvent();
			pushResponse.setUserObjects(zoomEvent.getParticipants());
		} else if(pushNotification.getAction()
				.equalsIgnoreCase(EventConstants.CONVERSATION_INVITED_PARTICIPANT.name())){
			InviteParticipantEvent inviteEvent = (InviteParticipantEvent) pushNotification.getBaseEvent();
			pushResponse.setUserObjects(inviteEvent.getUserObjects());
		} else {
			log.info("No other cases are handling in mbl app case ");
		}
		log.info("push Repsonse Obj" + pushResponse.toString());
		return pushResponse;
	}

	@Override
	public void mblPushNotification(PushNotificationVO pushNotification, MblPushResponseVO mblPushResponse) throws Exception {
		List<Long> userIds = pushNotification.getUsers().stream().map(User::getUserId).collect(Collectors.toList());
		if (userIds != null && !userIds.isEmpty()) {
			log.info("Sending push notifications to : " + userIds);

			List<PushSubscription> MobileAppPushSubscriptionList = pushServiceDAO.getMobileAppPushModel(userIds);
			if (MobileAppPushSubscriptionList != null && !MobileAppPushSubscriptionList.isEmpty()) {
				pushSubscriptionService.sendMobileAppPushNotification(MobileAppPushSubscriptionList, JsonConverterUtility.mapToJson(mblPushResponse), mblPushResponse.getTitle(), mblPushResponse.getBodyText());
			} else {
				log.info("In given users list nobody subscribed " + userIds);
			}

		} else {
			log.info("User list is empty can't process push notification: " + pushNotification.getAction());
		}
		// List<PushSubscription> pushList = pushServiceDAO.getPushModel(userId);

	}
}
