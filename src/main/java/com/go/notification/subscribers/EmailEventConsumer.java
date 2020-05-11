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
import com.go.notification.service.EmailPusherService;
import com.ne.commons.notification.vo.EmailNotificationVO;


@Component
public class EmailEventConsumer {
	
	@Autowired
	EmailPusherService emailPusherService;
	
	private static final Logger log = LoggerFactory.getLogger( EmailEventConsumer.class);
	
	@KafkaListener(topics = "${kafka.topic.notification-email}")
	public void processNotification(List<String> content) {
		for (String string : content) {
			log.info("subscribed content:" + content);
			try {
				ObjectMapper mapper = new ObjectMapper();
				mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
				EmailNotificationVO notificationObj = mapper.readValue(string, EmailNotificationVO.class);
				if (notificationObj.getNotificationType().equalsIgnoreCase(NotificationTypes.EMAIL.toString())) {
					emailPusherService.pushEmailNotification(notificationObj);
					log.info(notificationObj.getAction(),"*************************EMAIL ACTION ******************");
					log.info("Email send sucessfully");
				}
			} catch (Exception e) {
				log.error("Error in subscribing email notification Information:", e);
			}
		}
	}

}
