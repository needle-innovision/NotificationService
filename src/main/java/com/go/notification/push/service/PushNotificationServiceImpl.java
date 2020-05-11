package com.go.notification.push.service;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.ne.commons.events.BaseEvent;
import org.apache.commons.lang.StringUtils;
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
import com.ne.commons.events.TicketEvent;
import com.ne.commons.notification.vo.MblPushResponseVO;
import com.ne.commons.notification.vo.PushNotificationVO;
import com.ne.commons.notification.vo.PushResponseVO;
import com.ne.commons.notification.vo.User;

/**
 * Push Notification service has implementations related to push notification
 * business logic
 * 
 * @author santhosh.gudla
 *
 */
@Service
public class PushNotificationServiceImpl implements PushNotificationService {

	private static Logger log = LoggerFactory.getLogger(PushNotificationServiceImpl.class);

	@Autowired
	PushServiceDAO pushServiceDAO;

	@Autowired
	PushSubscriptionService pushSubscriptionService;

	@Value("${push.notification.image-url}")
	String imageUrl;

	Map<String, String> DEFAULT_PUSH_TITLES = new HashMap<String, String>() {
		private static final long serialVersionUID = 1L;

		{

			put("CONVERSATION_CREATED", "Created conversation");
			put("CONVERSATION_CHANGED_OWNER", "has made you the new owner of conversation");
			put("CONVERSATION_ASSIGNED_OWNER", "has made you the new owner of conversation");
			put("CONVERSATION_INVITED_PARTICIPANT", "has invited you to conversation");
			put("NEW_CONVERSATION_MESSAGE", "has added a new message to conversation");
			put("NEW_DM_MESSAGE", "has sent you a new message");
			put("CONVERSATION_FILE_ATTACHED", "has added a new file to conversation");
			put("DM_FILE_ATTACHED", "has sent you a file");
			put("CONVERSATION_CHANGED_STATUS", "has PAUSED the conversation");
			put("CONVERSATION_CLOSED", "has ENDED the conversation");
			put("CONVERSATION_WARROOM_STARTED", "has STARTED a WAR Room in conversation");
			put("CONVERSATION_WARROOM_ENDED", "has ENDED a WAR Room in conversation");
			put("CONVERSATION_AUTOROUTING_QUEUE", "Conversation is auto-routed to queue");
			put("QUEUE_ROUTING", "has moved the conversation to queue");
			put("CONVERSATION_TICKET_SLA_BREACHED", "Ticket SLA has been breached");
			put("CONVERSATION_TASK_CHANGED_STATUS", "has changed the status for task to ");
			put("CONVERSATION_TASK_OVERDUE", "Task is overdue");
			put("CONVERSATION_TICKET_CHANGED", "Ticket status has been changed");

		}
	};

	/**
	 * Handles the business logic for get userids from user Get push subscription
	 * list based on user object then send to push subscription service
	 * 
	 * @param pushNotification will contains the user info to whom notification
	 *                         should be sent
	 * @param pushResponse     contains push notification data.
	 * @throws Exception
	 */
	@Override
	public void pushNotification(PushNotificationVO pushNotification, PushResponseVO pushResponse) throws Exception {
		List<Long> userIds = pushNotification.getUsers().stream().map(User::getUserId).collect(Collectors.toList());
		if (userIds != null && !userIds.isEmpty()) {
			log.info("Sending push notifications to : " + userIds);
			List<PushSubscription> webBrowserPushSubscriptionList = pushServiceDAO.getWebPushModel(userIds);
			String pushResponseString = JsonConverterUtility.mapToJson(pushResponse);
			if (webBrowserPushSubscriptionList != null && !webBrowserPushSubscriptionList.isEmpty()) {
				pushSubscriptionService.sendPushNotification(webBrowserPushSubscriptionList, pushResponseString);

			} else {
				log.info("In given users list nobody subscribed " + userIds);
			}

		} else {
			log.info("User list is empty can't process push notification: " + pushNotification.getAction());
		}
//		List<PushSubscription> pushList = pushServiceDAO.getPushModel(userId);

	}

	/**
	 * Constructs the Push Response vo Which will be sent to service worker for show
	 * in notification
	 * 
	 * @param pushNotification
	 * @return PushResponseVO
	 */
	@Override
	public PushResponseVO constructPushResponse(PushNotificationVO pushNotification) {
		PushResponseVO pushResponse = new PushResponseVO();
		if (pushNotification.getBaseEvent().getUser() != null) {
			String userFullName = pushNotification.getBaseEvent().getUser().getFirstName() + " "
					+ pushNotification.getBaseEvent().getUser().getLastName();
			pushResponse.setActionUserFullName(userFullName);
		}
		pushResponse.setTitle(pushNotification.getAction());
		if (pushNotification.getAction().equalsIgnoreCase(EventConstants.CONVERSATION_AUTOROUTING_QUEUE.name())
				|| pushNotification.getAction().equalsIgnoreCase(EventConstants.QUEUE_ROUTING.name())) {
			String title = pushNotification.getConvTitle();
			if (title.length() > 60) {
				pushResponse.setBodyText("~" + pushNotification.getBaseEvent().getEventName() + ":"
						+ StringUtils.substring(title, 0, 60) + "...");
			} else {
				pushResponse.setBodyText(
						"~" + pushNotification.getBaseEvent().getEventName() + ":" + pushNotification.getConvTitle());
			}
		} else if (pushNotification.getAction().equalsIgnoreCase(EventConstants.CONVERSATION_TICKET_POST.name())) {

			pushResponse.setActionUserFullName(
					pushNotification.getBaseEvent().getChannelName() == null ? "third party system"
							: pushNotification.getBaseEvent().getChannelName());

			String title = pushNotification.getConvTitle();
			if (title.length() > 60) {
				pushResponse.setBodyText(StringUtils.substring(title, 0, 60) + "...");
			} else {
				pushResponse.setBodyText(title);
			}
		} else {
			if (pushNotification.getAction().equalsIgnoreCase(EventConstants.NEW_DM_MESSAGE.name())
					|| pushNotification.getAction().equalsIgnoreCase(EventConstants.DM_FILE_ATTACHED.name())) {
				pushResponse.setBodyText("@" + pushNotification.getConvTitle());
			} else if (pushNotification.getAction()
					.equalsIgnoreCase(EventConstants.CONVERSATION_TASK_CHANGED_STATUS.name())
					|| pushNotification.getAction().equalsIgnoreCase(EventConstants.CONVERSATION_TICKET_CHANGED.name())
					|| pushNotification.getAction().equalsIgnoreCase(EventConstants.CONVERSATION_TICKET_SLA_BREACHED.name())
					||pushNotification.getAction().equalsIgnoreCase(EventConstants.CONVERSATION_TASK_CHANGED_OWNER.name())
					||pushNotification.getAction().equalsIgnoreCase(EventConstants.CONVERSATION_TASK_ASSIGNED_OWNER.name())
					) {
				pushResponse.setBodyText(pushNotification.getBaseEvent().getEventName());
				ChangeEvent changeEvent = (ChangeEvent) pushNotification.getBaseEvent();
				pushResponse.setNewValue(changeEvent.getNewValue());
			} else if(pushNotification.getAction().equalsIgnoreCase(EventConstants.SCHEDULED_TICKET_IN_MINS.name())){

				String displayText = "";
				if("SCHEDULED_30".equalsIgnoreCase(pushNotification.getBaseEvent().getEntityType())){
					displayText = "There is Scheduled activity in 30 Mins";
				} else if("SCHEDULED_15".equalsIgnoreCase(pushNotification.getBaseEvent().getEntityType())){
					displayText = "There is Scheduled activity in 15 Mins";
				}

				String bodyText="";
				String title = pushNotification.getConvTitle();
				log.info("title " + title);
				if (!StringUtils.isEmpty(title)) {
					if (title.length() > 60) {
						bodyText ="#" + pushNotification.getBaseEvent().getEntityName() + ":"
								+ StringUtils.substring(title, 0, 60) + "...";
					} else {
						bodyText ="#" + pushNotification.getBaseEvent().getEntityName() + ":" + title;
					}
				}

				pushResponse.setActionUserFullName(displayText);
				log.info("bodyText>>>>>>>>>>>>>>>>>>>>>>"+bodyText);
				pushResponse.setBodyText(bodyText);
				pushResponse.setTitle("");

			} else if(pushNotification.getAction().equalsIgnoreCase(EventConstants.SCHEDULED_TICKET_STARTED.name())){
				String displayText = "The Scheduled activity has started";

				String bodyText="";
				String title = pushNotification.getConvTitle();
				log.info("title " + title);
				if (!StringUtils.isEmpty(title)) {
					if (title.length() > 60) {
						bodyText ="#" + pushNotification.getBaseEvent().getEntityName() + ":"
								+ StringUtils.substring(title, 0, 60) + "...";
					} else {
						bodyText ="#" + pushNotification.getBaseEvent().getEntityName() + ":" + title;
					}
				}

				pushResponse.setActionUserFullName(displayText);
				pushResponse.setBodyText(bodyText);
				pushResponse.setTitle("");
			}else if(pushNotification.getAction().equalsIgnoreCase(EventConstants.CONVERSATION_TASK_INVITE_GROUP.name())){
				pushResponse.setBodyText(pushNotification.getBaseEvent().getEventName());
			}else {
				String title = pushNotification.getConvTitle();
				log.info("title " + title);
				if (!StringUtils.isEmpty(title)) {
					if (title.length() > 60) {
						pushResponse.setBodyText("#" + pushNotification.getBaseEvent().getChannelName() + ":"
								+ StringUtils.substring(title, 0, 60) + "...");
					} else {
						pushResponse.setBodyText("#" + pushNotification.getBaseEvent().getChannelName() + ":" + title);
					}
				}
			}
		}
		pushResponse.setActionType(pushNotification.getBaseEvent().getActionType());
		pushResponse.setImageUrl(imageUrl);
		pushResponse.setConversationUrl(pushNotification.getConvUrl());
		return pushResponse;
	}

	@Override
	public MblPushResponseVO constructMblPushResponse(PushNotificationVO pushNotification) {
		log.info("inside the mobilepushResponse obj");
		MblPushResponseVO pushResponse = new MblPushResponseVO();
		if (pushNotification.getBaseEvent().getUser() != null) {
			pushResponse.setUser(pushNotification.getBaseEvent().getUser());
		}
		pushResponse.setChannelId(pushNotification.getBaseEvent().getChannelId());
		pushResponse.setChannelName(pushNotification.getBaseEvent().getChannelName());
		pushResponse.setOrgUniqueId(pushNotification.getBaseEvent().getOrgUniqueId());
		pushResponse.setOrgTypeId(Long.toString(pushNotification.getBaseEvent().getOrgTypeId()));
		pushResponse.setConversationId(pushNotification.getEntityId());
		pushResponse.setAction(pushNotification.getAction());
		pushResponse.setActionType(pushNotification.getBaseEvent().getActionType());
		pushResponse.setEventType(pushNotification.getBaseEvent().getEventType());
		if (pushNotification.getAction().equalsIgnoreCase(EventConstants.CONVERSATION_CHANGED_STATUS.name())) {
			ChangeEvent changeEvent = (ChangeEvent) pushNotification.getBaseEvent();
			pushResponse.setConversationTitle(pushNotification.getBaseEvent().getEventName());
			pushResponse.setNewValue(changeEvent.getNewValue());
			pushResponse.setOldValue(changeEvent.getOldValue());
		} else if (pushNotification.getAction().equalsIgnoreCase(EventConstants.NEW_CONVERSATION_MESSAGE.name())) {
			pushResponse.setConversationTitle(pushNotification.getBaseEvent().getEntityName());
			pushResponse.setMessage(pushNotification.getBaseEvent().getEventName());
			pushResponse.setMessageId(pushNotification.getBaseEvent().getEventId());
		} else {
			log.info("No other cases are handling in mbl app case ");

		}

		log.info("push Repsonse Obj" + pushResponse);
		return pushResponse;
	}

	@Override
	public void mblPushNotification(PushNotificationVO pushNotification, String mblPushResponse) throws SQLException {
		List<Long> userIds = pushNotification.getUsers().stream().map(User::getUserId).collect(Collectors.toList());
		if (userIds != null && !userIds.isEmpty()) {
			log.info("Sending push notifications to : " + userIds);

			List<PushSubscription> MobileAppPushSubscriptionList = pushServiceDAO.getMobileAppPushModel(userIds);
			if (MobileAppPushSubscriptionList != null && !MobileAppPushSubscriptionList.isEmpty()) {
//				pushSubscriptionService.sendMobileAppPushNotification(MobileAppPushSubscriptionList, mblPushResponse);
			} else {
				log.info("In given users list nobody subscribed " + userIds);
			}

		} else {
			log.info("User list is empty can't process push notification: " + pushNotification.getAction());
		}
//		List<PushSubscription> pushList = pushServiceDAO.getPushModel(userId);

	}

	@Override
	public PushResponseVO constructPushResponseWithTitle(PushNotificationVO pushNotification) {
		PushResponseVO pushResponse = this.constructPushResponse(pushNotification);

		String notificationTitle;
		if (pushResponse.getTitle().equals("CONVERSATION_AUTOROUTING_QUEUE") ||
			pushResponse.getTitle().equals("CONVERSATION_TICKET_SLA_BREACHED") ||
			pushResponse.getTitle().equals("CONVERSATION_CREATED") ||
			pushResponse.getTitle().equals("CONVERSATION_TICKET_CHANGED") ||
			pushResponse.getTitle().equals("CONVERSATION_TICKET_SLA_BREACHED")) {
			notificationTitle = DEFAULT_PUSH_TITLES.get(pushResponse.getTitle());
		} else if (pushResponse.getTitle().equals("NEW_DM_MESSAGE")) {
			if (pushResponse.getActionType().equals("direct")) {
				notificationTitle = pushResponse.getActionUserFullName() + " " + DEFAULT_PUSH_TITLES.get(pushResponse.getTitle());
			} else {
				notificationTitle = pushResponse.getActionUserFullName() + " has added a new message to DMG";
			}
		} else if (pushResponse.getTitle().equals("DM_FILE_ATTACHED")) {
			if (pushResponse.getActionType().equals("dm")) {
				notificationTitle = pushResponse.getActionUserFullName() + " " + DEFAULT_PUSH_TITLES.get(pushResponse.getTitle());
			} else {
				notificationTitle = pushResponse.getActionUserFullName() + " has added a new file to DMG";
			}
		} else if (pushResponse.getTitle().equals("CONVERSATION_TASK_CHANGED_STATUS")) {
			notificationTitle = pushResponse.getActionUserFullName() + " " + DEFAULT_PUSH_TITLES.get(pushResponse.getTitle()) + " " + pushResponse.getNewValue().toUpperCase();
		} else if (pushResponse.getTitle().equals("CONVERSATION_TICKET_POST")) {
			notificationTitle = "Ticket update from " + pushResponse.getActionUserFullName();
		} else if (pushResponse.getTitle().equals("CONVERSATION_ASSIGNED_OWNER")) {
			if(pushResponse.getActionType().equals("bot")){
				notificationTitle= "You have been assigned as an owner of this conversation";
			}else{
				notificationTitle = pushResponse.getActionUserFullName() + " " + DEFAULT_PUSH_TITLES.get(pushResponse.getTitle());
			}
		} else {
			notificationTitle = pushResponse.getActionUserFullName() + " " + DEFAULT_PUSH_TITLES.get(pushResponse.getTitle());
		}
		
		
		pushResponse.setTitle(notificationTitle);
		return pushResponse;
	}

}
