package com.go.notification.push.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Security;
import java.security.spec.InvalidKeySpecException;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.jose4j.lang.JoseException;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.go.notification.dao.PushServiceDAO;
import com.go.notification.push.models.PushSubscription;
import nl.martijndwars.webpush.Notification;
import nl.martijndwars.webpush.PushService;
import nl.martijndwars.webpush.Subscription;

/**
 * Push Subscription service will handle business logic related to Register push
 * subscription, Send push notifications
 * 
 * @author santhosh.gudla
 *
 */
@Service
public class PushSubscriptionServiceImpl implements PushSubscriptionService {

	private static final Logger log = LoggerFactory.getLogger(PushSubscriptionServiceImpl.class);

	@Autowired
	PushServiceDAO pushServiceDAO;

	@Value("${mbl.push.notification.fcm.auth-key}")
	String authKeyFcm;

	@Value("${mbl.push.notification.fcm.url}")
	String fcmUrl;

	@Value("${push.notification.public-key}")
	String publicKey;

	@Value("${push.notification.private-key}")
	String privateKey;

	/**
	 * Method which handles the push subscription saving
	 * 
	 * @param pushModel
	 * @param userId
	 */
	@Override
	public boolean saveOrUpdatePushSubscription(PushSubscription pushModel, long userId, String pushType)
			throws SQLException {
		return pushServiceDAO.saveOrUpdateSubscription(pushModel, userId, pushType);
	}

	/**
	 * Method which will prepares the Push notification required information Then
	 * send to the subscribed users
	 * 
	 * @param pushSubList
	 *            contains the subscriptions list
	 * @param pushResponse
	 *            contains data which will be shown in notification
	 */
	@Override
	public void sendPushNotification(List<PushSubscription> pushSubList, String pushResponse) {
		Security.addProvider(new BouncyCastleProvider());
		for (PushSubscription pushSubscription : pushSubList) {
			PushService pushService = new PushService();
			Subscription subscription = new Subscription();
			subscription.endpoint = pushSubscription.getEndpoint();
			Subscription.Keys key = subscription.new Keys();
			key.p256dh = pushSubscription.getKeys().getP256dh();
			key.auth = pushSubscription.getKeys().getAuth();
			subscription.keys = key;
			try {
				pushService.setPublicKey(publicKey);
				pushService.setPrivateKey(privateKey);
				Notification notification = new Notification(subscription, pushResponse);
				pushService.send(notification);
				log.info("Push notification sent successful");
			} catch (NoSuchAlgorithmException e) {
				log.error("Error while sending push notification: ", e);
			} catch (NoSuchProviderException e) {
				log.error("Error while sending push notification: ", e);
			} catch (InvalidKeySpecException e) {
				log.error("Error while sending push notification: ", e);
			} catch (GeneralSecurityException e) {
				log.error("Error while sending push notification: ", e);
			} catch (IOException e) {
				log.error("Error while sending push notification: ", e);
			} catch (JoseException e) {
				log.error("Error while sending push notification: ", e);
			} catch (ExecutionException e) {
				log.error("Error while sending push notification: ", e);
			} catch (InterruptedException e) {
				log.error("Error while sending push notification: ", e);
			}
		}
	}

	/**
	 * Handles business logic while unsubscribe for given user and requested browser
	 * 
	 * @param userId
	 * @param uniqueId
	 */
	@Override
	public boolean removeSubscription(long userId, String uniqueId) throws SQLException {
		boolean status = false;
		status = pushServiceDAO.removeSubscriptionRecord(userId, uniqueId);
		return status;
	}

	/**
	 * Handles business logic while geting subscription status for requested user
	 * 
	 * @param userId
	 * @param uniqueId
	 */
	@Override
	public boolean getSubscriptionStatus(long userId, String uniqueId) throws SQLException {
		boolean status = false;
		status = pushServiceDAO.getSubscriptionStatus(userId, uniqueId);
		if (status) {
			log.info("User subscribed for push notification for userId: " + userId + " uniqueId:" + uniqueId);
		} else {
			log.info("User not subscribed for push notification for userId: " + userId + " uniqueId:" + uniqueId);
		}
		return status;
	}

	@Override
	public void sendMobileAppPushNotification(List<PushSubscription> mobileAppPushSubscriptionList,
			String pushResponse, String title, String body) {
		for (PushSubscription pushNotify : mobileAppPushSubscriptionList) {
			log.info("inside the mobile PushNotification event"+ pushNotify);
			try {
				URL url = new URL(fcmUrl);
				HttpURLConnection conn;
				conn = (HttpURLConnection) url.openConnection();
				conn.setUseCaches(false);
				conn.setDoInput(true);
				conn.setDoOutput(true);
				conn.setRequestMethod("POST");
				conn.setRequestProperty("Authorization", "key=" + authKeyFcm);
				conn.setRequestProperty("Content-Type", "application/json");
				
				JSONObject notification = new JSONObject();
				notification.put("title", title);
				notification.put("body", body); // Notification body
				notification.put("click_action", "FCM_PLUGIN_ACTIVITY");
				notification.put("badge", 1);
				log.info("push response vo data"+pushResponse);
				
				JSONObject data = new JSONObject();
				data.put("notification", pushResponse);
				

				JSONObject pushNotification = new JSONObject();
				pushNotification.put("to", pushNotify.getToken().trim());
				pushNotification.put("notification", notification);
				pushNotification.put("data", data);

				OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
				wr.write(pushNotification.toString());
				wr.flush();
				wr.close();

				int responseCode = conn.getResponseCode();
				log.info("Response Code : " + responseCode);

				BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
				String inputLine;
				StringBuffer response = new StringBuffer();

				while ((inputLine = in.readLine()) != null) {
					response.append(inputLine);
				}
				in.close();
			} catch (MalformedURLException e) {
				log.error("Mbl PushNotification MalformedURLException case", e);
			} catch (ProtocolException e) {
				log.error("Mbl PushNotification ProtocolException case", e);
			} catch (IOException e) {
				log.error("Mbl PushNotification IOException case", e);
			} catch (NullPointerException e) {
				log.warn("Mbl PushNotification IOException case", e);
			} catch (Exception e) {
				log.error("Unhandled exception >>==========>\n", e);
			}

		}
	}

}
