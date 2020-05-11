package com.go.notification.service.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class NotificationServiceJDBCDAOImpl implements NotificationServiceJDBCDAO {

	private static Logger log = LoggerFactory.getLogger(NotificationServiceJDBCDAOImpl.class);

	@Autowired
	private DataSource dataSource;

	@Autowired
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	@Override
	public boolean updateBellNotificationStatus(int userId, String[] ids, String option) throws SQLException {
		boolean flag = false;
		Connection conn = null;
		PreparedStatement ps = null;
		int count = 0;
		if(dataSource != null) {
			conn = dataSource.getConnection();
			if (conn == null) {
				throw new SQLException("Unable to get the Connection!");
			}
		} else {
			throw new SQLException("Unable to get the Connection!");
		}

		String notificationStatusClearAllQuery="UPDATE ne_notification_users set status=1 where user_id=? and notification_type='BELL'";
		String notificationUpdateQuery = "UPDATE ne_notification_users set status=1 where notification_id=? and user_id=? and notification_type='BELL'";
		try {
			if(ids.length!=0){
				ps = conn.prepareStatement(notificationUpdateQuery);
				for (String id : ids) {
					ps.setString(1, id);
					ps.setInt(2, userId);
					ps.addBatch();
				}
				ps.executeBatch();

				count = ps.executeUpdate();
				if (count == ids.length) {
					flag = true;
				}
			}
			else{
				ps = conn.prepareStatement(notificationStatusClearAllQuery);
				ps.setInt(1, userId);

				count = ps.executeUpdate();
				if (count > 0) {
					flag = true;
				}
			}

		} catch (Exception e) {
			log.error("Exception in updating bell notification status in DB:", e);
		} finally {

			if (ps != null) {
				ps.close();
				ps = null;
			}
			if (conn != null) {
				conn.close();
				conn = null;
			}
		}
		return flag;
	}

	@Override
	public long getUserConvUnRead(long userId) throws SQLException {
		long convCount = 0L;
		Connection conn = null;
		conn = dataSource.getConnection();
		if (conn == null) {
			throw new SQLException("Unable to get the Connection!");
		}
		String unReadCountQuery = "SELECT COUNT(conversationId) AS convCount,userId FROM ne_ms_conversation_users WHERE unreadCount>0 AND userId=?";
		PreparedStatement stm = conn.prepareStatement(unReadCountQuery);
		stm.setLong(1, userId);
		ResultSet rs = null;
		try {
			rs = stm.executeQuery();
			while (rs.next()) {
				convCount = rs.getLong("convCount");

			}

		} catch (Exception e) {
			log.error("Exception in getting user unread messages count from DB:", e);
		} finally {

			if (stm != null) {
				stm.close();
				stm = null;
			}
			if (conn != null) {
				conn.close();
				conn = null;
			}
		}
		return convCount;
	}

	@Override
	public boolean updateBellNotificationStatus(String entityId, long userId)throws SQLException {
		boolean flag = false;
		Connection conn = null;
		PreparedStatement ps = null;
		int count = 0;
		conn = dataSource.getConnection();
		if (conn == null) {
			throw new SQLException("Unable to get the Connection!");
		}

		String notificationUpdateQuery = "UPDATE ne_notification_users set status=1 where entity_id=? and user_id=? and notification_type='BELL'";
		try {

			ps = conn.prepareStatement(notificationUpdateQuery);
			ps.setString(1, entityId);
			ps.setLong(2, userId);

			count = ps.executeUpdate();
			if (count > 0) {
				flag = true;
			}
		} catch (Exception e) {
			log.error("Exception in updating bell notification status in DB:", e);
		} finally {

			if (ps != null) {
				ps.close();
				ps = null;
			}
			if (conn != null) {
				conn.close();
				conn = null;
			}
		}
		return flag;
	}

}
