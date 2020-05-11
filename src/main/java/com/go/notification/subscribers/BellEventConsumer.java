package com.go.notification.subscribers;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.go.notification.constants.NotificationTypes;
import com.go.notification.models.BellNotificationVO;
import com.go.notification.service.BellPusherService;

@Component
public class BellEventConsumer {

	private static final Logger log = LoggerFactory.getLogger(BellEventConsumer.class);

	@Autowired
	BellPusherService bellPusherService;

	@KafkaListener(topics = "${kafka.topic.notification-bell}")
	public void processNotification(List<String> content) {
		for (String string : content) {
			log.info("subscribed content:" + content);
			boolean eventSuccess = false;
			BellNotificationVO bellObj = null;
			try {
				ObjectMapper mapper = new ObjectMapper();
				mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
				bellObj = mapper.readValue(string, BellNotificationVO.class);
				if (bellObj.getNotificationType().equalsIgnoreCase(NotificationTypes.BELL.toString())) {
					eventSuccess = bellPusherService.pushBellNotification(bellObj);
					if (eventSuccess) {
						log.info("Bell Event subscribed successfully");
					} else {
						log.info("Bell Event subsciption failed");
					}
				}
			} catch (Exception e) {
				log.error("Error in subscribing bell notification Information for NotificationId:"
						+ bellObj.getNotificationId(), e);
			}
		}
	}

}
