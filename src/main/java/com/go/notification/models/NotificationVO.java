package com.go.notification.models;

import java.util.List;

import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;



public class NotificationVO {

	@Field(type = FieldType.Keyword, store = true)
	private String notificationId;
	@Field(type = FieldType.Keyword, store = true)
	private String notificationType;
	@Field(type = FieldType.Nested, store = true)
	private List<UserVO> users;
	private int eventId;
	private String event;
	private String entityId;
	private String entityType;
	@Field(type = FieldType.Keyword, store = true)
	private String action;
	private long eventTime;
	private String notificationLevel;

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

	public List<UserVO> getUsers() {
		return users;
	}

	public void setUser(List<UserVO> users) {
		this.users = users;
	}

	public int getEventId() {
		return eventId;
	}

	public void setEventId(int eventId) {
		this.eventId = eventId;
	}

	public String getEvent() {
		return event;
	}

	public void setEvent(String event) {
		this.event = event;
	}

	public String getEntityId() {
		return entityId;
	}

	public void setEntityId(String entityId) {
		this.entityId = entityId;
	}

	public String getEntityType() {
		return entityType;
	}

	public void setEntityType(String entityType) {
		this.entityType = entityType;
	}

	public void setUsers(List<UserVO> users) {
		this.users = users;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public long getEventTime() {
		return eventTime;
	}

	public void setEventTime(long eventTime) {
		this.eventTime = eventTime;
	}

	public String getNotificationLevel() {
		return notificationLevel;
	}

	public void setNotificationLevel(String notificationLevel) {
		this.notificationLevel = notificationLevel;
	}

}
