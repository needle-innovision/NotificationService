package com.go.notification.models;

import com.ne.commons.events.BaseEvent;

public class BellResponseVO {

	private String notificationId;
	private String notificationType;
	private String action;
	private Object baseEvent;
	private String notificationLevel;
	private long eventTime;

	public String getNotificationId() {
		return notificationId;
	}

	public void setNotificationId(String notificationId) {
		this.notificationId = notificationId;
	}

	public String getNotificationType() {
		return notificationType;
	}

	public void setNotificationType(String notificationType) {
		this.notificationType = notificationType;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public Object getBaseEvent() {
		return baseEvent;
	}

	public void setBaseEvent(Object baseEvent) {
		this.baseEvent = baseEvent;
	}

	public String getNotificationLevel() {
		return notificationLevel;
	}

	public void setNotificationLevel(String notificationLevel) {
		this.notificationLevel = notificationLevel;
	}

	public long getEventTime() {
		return eventTime;
	}

	public void setEventTime(long eventTime) {
		this.eventTime = eventTime;
	}

}
