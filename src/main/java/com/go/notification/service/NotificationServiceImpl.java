package com.go.notification.service;

import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.go.notification.service.dao.NotificationServiceJDBCDAO;

@Component
public class NotificationServiceImpl implements NotificationService {

	@Autowired
	NotificationServiceJDBCDAO notificationDAO;

	private static final Logger log = LoggerFactory.getLogger(NotificationServiceImpl.class);

	@Override
	public long getUserConversationUnRead(long userId) {
		long count = 0L;
		try {
			count = notificationDAO.getUserConvUnRead(userId);
		} catch (SQLException e) {
			log.error("Exception in getting user conversation unread count:", e);
		}
		return count;
	}

}
