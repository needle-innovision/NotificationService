package com.go.notification.service;

import java.util.HashMap;
import java.util.List;



public interface BellNotificationService {

	public HashMap<String, Object> getBellNotifications(int userId, String filter,long offset, int limit);

	public boolean updateBellNotificationStatus(int userId, String[] ids,int option);
	
	public void removeUserNotifications(String entityId,long userId);

}
