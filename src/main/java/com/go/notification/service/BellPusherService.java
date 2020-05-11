package com.go.notification.service;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.go.notification.es.repository.BellNotificationRepository;
import com.go.notification.models.BellNotificationVO;

@Service
public class BellPusherService {

	private static final Logger log = LoggerFactory.getLogger(BellPusherService.class);

	@Autowired
	BellNotificationRepository bellRepo;

	public boolean pushBellNotification(BellNotificationVO bellObj) {
		boolean flag = false;
		try {
			// saving bell notification object to ES
			try {
				if (bellObj.getNotificationId() == null)
					bellObj.setNotificationId(UUID.randomUUID().toString());
			} catch (Exception e) {
				log.error("Exception in generating uuid for notification event:");
			}
			log.info(bellObj.getNotificationId()+"notificationID************************************");
			bellRepo.save(bellObj);
			flag = true;

		} catch (Exception e) {
			log.error("Exception in pushing bell Notification Event for Id :" + bellObj.getNotificationId(), e);
		}
		return flag;
	}

}
