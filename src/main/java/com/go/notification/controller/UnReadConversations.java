package com.go.notification.controller;

import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.go.notification.models.ResponseVO;
import com.go.notification.service.JWTService;
import com.go.notification.service.NotificationService;

@RestController
public class UnReadConversations {
	

	@Autowired
	JWTService jwtService;
	
	@Autowired
	NotificationService service;
	
	private static final Logger log = LoggerFactory.getLogger(UnReadConversations.class);
	
	@RequestMapping(value = "/getUserConvUnread", method = RequestMethod.GET)
	public ResponseEntity<ResponseVO> getUserBellNotifications(HttpServletRequest request) {

		ResponseVO response = new ResponseVO();
		long userId = 0;
		String jwtToken = request.getHeader("jwt-token").toString();
		long count=0L;
		userId = jwtService.getUserId(jwtToken);
		if (userId == 0) {
			log.error("Failed to get userInfo from jwt Token:");
			response.setStatus("failed");
			response.setResponse("Invalid Request");
			return new ResponseEntity<ResponseVO>(response, HttpStatus.BAD_REQUEST);
		}
		count = service.getUserConversationUnRead(userId);
		response.setStatus("success");
		response.setResponse(count);
		return new ResponseEntity<ResponseVO>(response, HttpStatus.OK);
	}
	
	

}
