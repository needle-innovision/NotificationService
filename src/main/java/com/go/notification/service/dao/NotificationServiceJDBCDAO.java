package com.go.notification.service.dao;

import java.sql.SQLException;


public interface NotificationServiceJDBCDAO {
	
	public boolean updateBellNotificationStatus(int userId, String[] ids, String option)throws SQLException;
	public long getUserConvUnRead(long userId)throws SQLException;
	public boolean updateBellNotificationStatus(String entityId,long userId)throws SQLException;

}
