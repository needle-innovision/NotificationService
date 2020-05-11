package com.go.notification.subscribers;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.eventbus.EventBus;
import com.ne.commons.notification.vo.PushNotificationVO;

@Component
public class PushEventConsumer {
	
	@Autowired
	EventBus eventBus;

	private static final Logger log = LoggerFactory.getLogger(PushEventConsumer.class);

	@KafkaListener(topics = "${kafka.topic.notification-push}")
	public void processNotification(List<String> content) {
		for (String string : content) {
			log.info("subscribed content:" + content);
			PushNotificationVO pushNotification = null;
			try {
				ObjectMapper mapper = new ObjectMapper();
				mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
				pushNotification = mapper.readValue(string, PushNotificationVO.class);
				eventBus.post(pushNotification);
			} catch (Exception e) {
				log.error("Error in subscribing Push notification Information for NotificationId:"
						+ pushNotification.getNotificationId(), e);
			}
		}
	}

}
