package com.go.notification.controller;

import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.go.notification.models.BellNotificationVO;
import com.go.notification.service.BellPusherService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.go.notification.models.ResponseVO;
import com.go.notification.models.updateVO;
import com.go.notification.service.BellNotificationService;
import com.go.notification.service.JWTService;

@RestController
public class BellNotificationController {

	private static final Logger log = LoggerFactory.getLogger(BellNotificationController.class);

	@Autowired
	JWTService jwtService;

	@Autowired
	BellNotificationService bellService;

	@Autowired
	BellPusherService bellPusherService;

	@RequestMapping(value = "/getBellNotification", method = RequestMethod.GET)
	public ResponseEntity<ResponseVO> getUserBellNotifications(HttpServletRequest request,
			@RequestParam(value = "limit", defaultValue = "3", required = false) int limit,
			@RequestParam(value = "offset", defaultValue = "0", required = false) long offset,
			@RequestParam(value = "filter", defaultValue = "eventTime", required = false) String filter) {

		ResponseVO response = new ResponseVO();
		int userId = 0;
		String jwtToken = request.getHeader("jwt-token").toString();

		userId = jwtService.getUserId(jwtToken);
		if (userId == 0) {
			log.error("Failed to get userInfo from jwt Token:");
			response.setStatus("failed");
			response.setResponse("Invalid Request");
			return new ResponseEntity<ResponseVO>(response, HttpStatus.BAD_REQUEST);
		}
		HashMap<String, Object> notificationList = bellService.getBellNotifications(userId, filter, offset, limit);
		response.setStatus("success");
		response.setResponse(notificationList);
		return new ResponseEntity<ResponseVO>(response, HttpStatus.OK);
	}

	@RequestMapping(value = "/updateNotificationStatus", method = RequestMethod.POST)
	public ResponseEntity<ResponseVO> updateNotificationStatus(HttpServletRequest request,
			@RequestParam(value = "notificationId", required = false) String notificationId,
			@RequestParam(value = "option", required = false) String option,
		    @RequestParam(value = "mark", required = false) int mark,
			@RequestBody updateVO updateObj) {
		ResponseVO response = new ResponseVO();
		int userId = 0;
		String jwtToken = request.getHeader("jwt-token").toString();
		boolean flag = false;
		userId = jwtService.getUserId(jwtToken);
		if (userId == 0) {
			log.error("Failed to get userInfo from jwt Token:");
			response.setStatus("failed");
			response.setResponse("Invalid Request");
			return new ResponseEntity<ResponseVO>(response, HttpStatus.BAD_REQUEST);
		}
		flag = bellService.updateBellNotificationStatus(userId, updateObj.getIds(),mark);
		if (flag) {
			response.setStatus("success");
			response.setResponse("Bell Notification Status Updated Sucessfully");
		} else {
			response.setStatus("failed");
			response.setResponse("Failed to update Bell Notificaiton Status");
			return new ResponseEntity<ResponseVO>(response, HttpStatus.BAD_REQUEST);
		}

		return new ResponseEntity<ResponseVO>(response, HttpStatus.OK);
	}


	@RequestMapping(value = "/bellpost", method = RequestMethod.POST)
	public void bellpost(@RequestBody BellNotificationVO bellObj) {

		bellPusherService.pushBellNotification(bellObj);


	}

}
