package com.go.notification.controller;

import java.sql.SQLException;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.go.notification.dao.PushServiceDAO;
import com.go.notification.models.ResponseVO;
import com.go.notification.push.models.PushSubscription;
import com.go.notification.push.service.PushNotificationService;
import com.go.notification.push.service.PushSubscriptionService;
import com.go.notification.service.JWTService;
import com.go.notification.utils.ClientDetails;
import com.go.notification.utils.UrlValidator;

@RestController
public class PushNotificationController {

	private static final Logger log = LoggerFactory.getLogger(PushNotificationController.class);

	@Autowired
	JWTService jwtService;
	
	@Autowired
	PushSubscriptionService pushSubscriptionService;
	
	@Autowired
	PushNotificationService pushNotificationService;

	@RequestMapping(value = "/push/subscribe/unique/{uniqueId}", method = RequestMethod.POST)
	public ResponseEntity<ResponseVO> subscribePushNotification(HttpServletRequest request, 
			@RequestBody PushSubscription pushModel, 
			@PathVariable(value = "uniqueId") String uniqueId) throws SQLException {
		ResponseVO response = new ResponseVO();
		if(pushModel != null && UrlValidator.isValidUrl(pushModel.getEndpoint())) {
			boolean status = false;
			int userId = 0;
			String pushType="BROWSER";			
			String jwtToken = request.getHeader("jwt-token").toString();
			userId = jwtService.getUserId(jwtToken);
			log.info("Push subscription request for userId:"+userId+" uniqueId:"+uniqueId);
			if(userId <= 0 || StringUtils.isEmpty(uniqueId)) {
				response.setStatus("failed");
				response.setResponse("Failed");
				return new ResponseEntity<ResponseVO>(response, HttpStatus.BAD_REQUEST);
			}
			pushModel.setUniqueId(uniqueId);
			status  = pushSubscriptionService.saveOrUpdatePushSubscription(pushModel, userId,pushType);
			if(status) {
				response.setStatus("success");
				response.setResponse("Subscription successful");
				return new ResponseEntity<ResponseVO>(response, HttpStatus.OK);
			}
		} else {
			response.setStatus("failed");
			response.setResponse("Failed");
			return new ResponseEntity<ResponseVO>(response, HttpStatus.BAD_REQUEST);
		}
		response.setStatus("failed");
		response.setResponse("Failed");
		return new ResponseEntity<ResponseVO>(response, HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	@RequestMapping(value = "/push/subscribe/unique/{uniqueId}", method = RequestMethod.DELETE)
	public ResponseEntity<ResponseVO> unUubscribePushNotification(HttpServletRequest request, 
			@PathVariable(value = "uniqueId") String uniqueId, @RequestParam(value = "id") long userId) throws SQLException {
		ResponseVO response = new ResponseVO();
		boolean status = false;
		if(userId <= 0 || StringUtils.isEmpty(uniqueId)) {
			response.setStatus("failed");
			response.setResponse("Failed");
			return new ResponseEntity<ResponseVO>(response, HttpStatus.BAD_REQUEST);
		}
		log.info("Unsubscribe push notificaation called for User Id:"+userId+" uniqueId:"+uniqueId);
		status = pushSubscriptionService.removeSubscription(userId, uniqueId);
		if(status) {
			response.setStatus("success");
			response.setResponse("Unsubscribed successfully");
			return new ResponseEntity<ResponseVO>(response, HttpStatus.OK);
		} else {
			log.info("User doesn't exist or something went wrong for User Id:"+userId+" uniqueId:"+uniqueId);
		}
		response.setStatus("failed");
		response.setResponse("Failed");
		return new ResponseEntity<ResponseVO>(response, HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	@RequestMapping(value = "/push/subscribe/unique/{uniqueId}", method = RequestMethod.GET)
	public ResponseEntity<ResponseVO> getSubscription(HttpServletRequest request, 
			@PathVariable(value = "uniqueId") String uniqueId) throws SQLException {
		ResponseVO response = new ResponseVO();
		boolean status = false;
		int userId = 0;
		String jwtToken = request.getHeader("jwt-token").toString();
		userId = jwtService.getUserId(jwtToken);
		if(userId <= 0 || StringUtils.isEmpty(uniqueId)) {
			response.setStatus("failed");
			response.setResponse("Failed");
			return new ResponseEntity<ResponseVO>(response, HttpStatus.BAD_REQUEST);
		}
		status = pushSubscriptionService.getSubscriptionStatus(userId, uniqueId);

		response.setStatus("success");
		response.setResponse(status);
		return new ResponseEntity<ResponseVO>(response, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/mbl/push/subscribe/unique/{uniqueId}", method = RequestMethod.POST)
	public ResponseEntity<ResponseVO> subscribePushNotification(@RequestBody PushSubscription pushModel, 
			@PathVariable(value = "uniqueId") String uniqueId) throws SQLException {
		ResponseVO response = new ResponseVO();
		if(pushModel != null) {
			boolean status = false;
			int userId = 0;
			String pushType="MOBILE";
			userId=pushModel.getUserId();
			log.info("Push subscription request for userId:"+userId+" uniqueId:"+uniqueId);
			if(userId <= 0 || StringUtils.isEmpty(uniqueId)) {
				response.setStatus("failed");
				response.setResponse("Failed");
				return new ResponseEntity<ResponseVO>(response, HttpStatus.BAD_REQUEST);
			}
			pushModel.setUniqueId(uniqueId);
			status  = pushSubscriptionService.saveOrUpdatePushSubscription(pushModel, userId,pushType);
			if(status) {
				response.setStatus("success");
				response.setResponse("Subscription successful");
				return new ResponseEntity<ResponseVO>(response, HttpStatus.OK);
			}
		} else {
			response.setStatus("failed");
			response.setResponse("Failed");
			return new ResponseEntity<ResponseVO>(response, HttpStatus.BAD_REQUEST);
		}
		response.setStatus("failed");
		response.setResponse("Failed");
		return new ResponseEntity<ResponseVO>(response, HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	@RequestMapping(value = "/mbl/push/subscribe/unique/{uniqueId}", method = RequestMethod.GET)
	public ResponseEntity<ResponseVO> getMblUserSubscription(@PathVariable(value = "uniqueId") String uniqueId,@RequestParam(value = "id") long userId) throws SQLException {
		ResponseVO response = new ResponseVO();
		boolean status = false;
		if(userId <= 0 || StringUtils.isEmpty(uniqueId)) {
			response.setStatus("failed");
			response.setResponse("Failed");
			return new ResponseEntity<ResponseVO>(response, HttpStatus.BAD_REQUEST);
		}
		status = pushSubscriptionService.getSubscriptionStatus(userId, uniqueId);

		response.setStatus("success");
		response.setResponse(status);
		return new ResponseEntity<ResponseVO>(response, HttpStatus.OK);
	}
	
}
