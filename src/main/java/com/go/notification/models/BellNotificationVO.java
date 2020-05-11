package com.go.notification.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import com.ne.commons.events.BaseEvent;

@Document(indexName = "#{@indexName}", type = "notification", shards = 1, replicas = 0, refreshInterval = "-1")
public class BellNotificationVO extends NotificationVO {

	@Id
	@Field(type = FieldType.Keyword, store = true)
	private String id;
	private int status;
	@Field(type = FieldType.Object, store = false)
	private BaseEvent commonEventContent;

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
		this.id = super.getNotificationId();
	}

	public BaseEvent getCommonEventContent() {
		return commonEventContent;
	}

	public void setCommonEventContent(BaseEvent commonEventContent) {
		this.commonEventContent = commonEventContent;
	}

}
