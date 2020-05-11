package com.go.notification.service.dao;

import java.util.HashMap;
import java.util.List;


public interface NotificationESDAO {
	
	public HashMap<String, Object> getBellNotifications(int userId, String filter,long offset, int limit);
	public boolean updateBellNotificationStatus(int userId, String[] ids, int option);
	public boolean updateBellNotificationStatus(String entityId,long userId);

}
