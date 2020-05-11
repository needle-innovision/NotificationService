package com.go.notification.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.go.notification.push.models.PushSubscriptionKey;
import com.go.notification.push.models.PushSubscription;

/**
 * DAO handle the saving of push subscription and updating it
 * Also handles reading push subscription data
 * @author santhosh.gudla
 *
 */
@Service
public class PushServiceJDBCDAO implements PushServiceDAO {

	private static final Logger log = LoggerFactory.getLogger(PushServiceJDBCDAO.class);

	@Autowired
	private DataSource dataSource;

	@Autowired
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	/**
	 * Saves the push subscription information
	 * @param push 
	 */
	@Override
	public boolean saveOrUpdateSubscription(PushSubscription pushModel, long userId,String pushType) throws SQLException {
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
		String pushSubscriptionUpdateQuery = "UPDATE ne_push_notification set endpoint=?, pdh_key=?, auth_key=?,status=?, token=? where unique_id=? and user_id=?";
		String pushSubscriptionInsertQuery = "INSERT INTO ne_push_notification (user_id, endpoint, pdh_key, auth_key, status, unique_id,token,push_type) values (?,?,?,?,?,?,?,?)";
		try {
			ps = conn.prepareStatement(pushSubscriptionUpdateQuery);
			if(org.springframework.util.StringUtils.isEmpty(pushModel.getEndpoint())) {
				ps.setString(1, null);
			}else {
				ps.setString(1, pushModel.getEndpoint());

			}
			if(org.springframework.util.StringUtils.isEmpty(pushModel.getKeys())) {
				ps.setString(2, null);
				ps.setString(3, null);
			}else {
				ps.setString(2, pushModel.getKeys().getP256dh());
				ps.setString(3, pushModel.getKeys().getAuth());
			}
			ps.setInt(4, 1);
			if(org.springframework.util.StringUtils.isEmpty(pushModel.getToken())) {
				ps.setString(5, null);
			}else {
				ps.setString(5, pushModel.getToken());
			}
			
			ps.setString(6, pushModel.getUniqueId());
			ps.setLong(7, userId);
			count = ps.executeUpdate();
			if (count <= 0) {
				try {
					ps = conn.prepareStatement(pushSubscriptionInsertQuery);
					ps.setLong(1, userId);

					if(org.springframework.util.StringUtils.isEmpty(pushModel.getEndpoint())) {
						ps.setString(2, null);
					}else {
						ps.setString(2, pushModel.getEndpoint());
					}
					if(org.springframework.util.StringUtils.isEmpty(pushModel.getKeys())) {
						ps.setString(3, null);
						ps.setString(4, null);
					}else {
						ps.setString(3, pushModel.getKeys().getP256dh());
						ps.setString(4, pushModel.getKeys().getAuth());
					}
					ps.setInt(5, 1);
					ps.setString(6, pushModel.getUniqueId());

					if(org.springframework.util.StringUtils.isEmpty(pushModel.getToken())) {
						ps.setString(7, null);
					}else {
						ps.setString(7, pushModel.getToken());
					}
					ps.setString(8,pushType);
					count = ps.executeUpdate();
					if(count > 0) {
						flag = true;
						log.info("Subscription saved for userId:"+userId+" uniqueId"+pushModel.getUniqueId());
					}
				} catch (Exception e) {
					log.error("Exception while inserting push subscription : ", e);
				}
			} else {
				flag = true;
				log.info("Subscription updated for userId:"+userId+" uniqueId"+pushModel.getUniqueId());
			}
		} catch (Exception e) {
			log.error("Exception while updating push subscription : ", e);
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

	/**
	 * Get the push subscriptions list from the db based on userids
	 * Then returns it for push notifications
	 */
	@Override
	public List<PushSubscription> getWebPushModel(List<Long> userIds) throws SQLException {
		List<PushSubscription> pushList = new ArrayList<>();
		Connection conn = null;
		PreparedStatement ps = null;
		String userId = StringUtils.join(userIds, ",");
		conn = dataSource.getConnection();
		if (conn == null) {
			throw new SQLException("Unable to get the Connection!");
		}

		String pushSubscriptionUpdateQuery = "select * from ne_push_notification where user_id IN (" + userId + ") and status=1 and push_type='BROWSER'";
		try {
			ps = conn.prepareStatement(pushSubscriptionUpdateQuery);
			ResultSet rs = null;
			rs = ps.executeQuery();

			while (rs.next()) {
				PushSubscription pushSubscription = new PushSubscription();
				PushSubscriptionKey keys = new PushSubscriptionKey();
				pushSubscription.setUniqueId(rs.getString("unique_id"));
				pushSubscription.setEndpoint(rs.getString("endpoint"));
				keys.setP256dh(rs.getString("pdh_key"));
				keys.setAuth(rs.getString("auth_key"));
				pushSubscription.setKeys(keys);
				pushList.add(pushSubscription);
			}
		} catch (Exception e) {
			log.error("Exception while getting push subscription for user : ", e);
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
		return pushList;
	}

	/**
	 * Unsubscribe for given user and unique browser
	 * @param userId 
	 * @param uniqueId browser unique id
	 */
	@Override
	public boolean removeSubscriptionRecord(long userId, String uniqueId) throws SQLException {
		boolean status = false;
		int count;
		Connection conn = null;
		PreparedStatement ps = null;
		conn = dataSource.getConnection();
		if (conn == null) {
			throw new SQLException("Unable to get the Connection!");
		}

//		String deleteQuery = "delete from ne_push_notification where user_id=? and unique_id=?";
		String unSubscribe = "UPDATE ne_push_notification set status=0 where unique_id=? and user_id=?";
		try {
			ps = conn.prepareStatement(unSubscribe);
			ps.setString(1, uniqueId);
			ps.setLong(2, userId);
			count = ps.executeUpdate();
			if(count > 0) {
				status = true;
				log.info("Unsubscribe push notification success for User Id:"+userId+" uniqueId:"+uniqueId);
			}
		} catch (Exception e) {
			log.error("Exception while deleting push subscription :", e);
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
		return status;
	}

	/**
	 * Get the status of subscription for given user id and browser unique id
	 * 
	 * @param userId
	 * @param uniqueId
	 */
	@Override
	public boolean getSubscriptionStatus(long userId, String uniqueId) throws SQLException {
		boolean status = false;
		Connection conn = null;
		PreparedStatement ps = null;
		conn = dataSource.getConnection();
		if (conn == null) {
			throw new SQLException("Unable to get the Connection!");
		}

		String deleteQuery = "select status from ne_push_notification where user_id=? and unique_id=?";
		try {
			ps = conn.prepareStatement(deleteQuery);
			ps.setLong(1, userId);
			ps.setString(2, uniqueId);
			ResultSet rs = null;
			rs = ps.executeQuery();
			while(rs.next()) {
				status = true;
			}
		} catch (Exception e) {
			log.error("Exception in getting subscription status from db :", e);
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
		return status;
	}

	@Override
	public List<PushSubscription> getMobileAppPushModel(List<Long> userIds) throws SQLException {
		List<PushSubscription> pushList = new ArrayList<>();
		Connection conn = null;
		PreparedStatement ps = null;
		String userId = StringUtils.join(userIds, ",");
		conn = dataSource.getConnection();
		if (conn == null) {
			throw new SQLException("Unable to get the Connection!");
		}

		String pushSubscriptionUpdateQuery = "select * from ne_push_notification where user_id IN (" + userId + ") and status=1 and push_type='MOBILE'";
		try {
			ps = conn.prepareStatement(pushSubscriptionUpdateQuery);
			ResultSet rs = null;
			rs = ps.executeQuery();

			while (rs.next()) {
				PushSubscription pushSubscription = new PushSubscription();
				pushSubscription.setUniqueId(rs.getString("unique_id"));
				pushSubscription.setToken(rs.getString("token"));
				pushSubscription.setUserId(rs.getInt("user_id"));
				pushList.add(pushSubscription);
				log.info("inside the dao layer" + pushSubscription);

			}
		} catch (Exception e) {
			log.error("Exception while getting push subscription for user : ", e);
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
		return pushList;
	}

}
