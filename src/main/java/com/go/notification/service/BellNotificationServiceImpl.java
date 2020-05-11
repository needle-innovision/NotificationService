package com.go.notification.service;

import java.util.HashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.go.notification.service.dao.NotificationESDAO;
import com.go.notification.service.dao.NotificationServiceJDBCDAO;

@Component
public class BellNotificationServiceImpl implements BellNotificationService {

	@Autowired
	NotificationServiceJDBCDAO notificationDAO;

	@Autowired
	NotificationESDAO notificationESDAO;

	private static final Logger log = LoggerFactory.getLogger(BellNotificationServiceImpl.class);

	@Override
	public HashMap<String, Object> getBellNotifications(int userId, String filter, long offset, int limit) {
		HashMap<String, Object> notificationResults = notificationESDAO.getBellNotifications(userId, filter, offset,
				limit);
		return notificationResults;
	}

	@Override
	public boolean updateBellNotificationStatus(int userId, String[] ids, int option) {
		boolean status = false;
		try {
			// updating bell status in db and elastic search

			status = notificationDAO.updateBellNotificationStatus(userId, ids, "");
			status = notificationESDAO.updateBellNotificationStatus(userId, ids, option);
		} catch (Exception e) {
			log.error("Failed to update bell notification status:", e);
		}
		return status;
	}

	@Override
	public void removeUserNotifications(String entityId, long userId) {
		boolean status = false;
		try {
			// updating bell status in db and elastic search
			status = notificationDAO.updateBellNotificationStatus(entityId,userId);
			status = notificationESDAO.updateBellNotificationStatus(entityId,userId);
		} catch (Exception e) {
			log.error("Exception in removing user notification:", e);
		}

	}

}
