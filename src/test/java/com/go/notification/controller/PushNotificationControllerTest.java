package com.go.notification.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.sql.SQLException;
import java.util.Random;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.RandomStringUtils;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.go.notification.push.models.PushSubscription;
import com.go.notification.push.models.PushSubscriptionKey;
import com.go.notification.push.service.PushNotificationService;
import com.go.notification.push.service.PushSubscriptionService;
import com.go.notification.service.JWTService;
import com.go.notification.utils.TestUtils;

@RunWith(SpringRunner.class)
public class PushNotificationControllerTest {

	@MockBean
	JWTService jwtService;
	
	private MockMvc mockMvc;
	
	@MockBean
	PushSubscriptionService pushSubscriptionService;
	
	@MockBean
	PushNotificationService pushNotificationService;
	
	@MockBean
	HttpServletRequest request;
	
	@InjectMocks
	PushNotificationController pushNotificationController;
	
	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		mockMvc = MockMvcBuilders.standaloneSetup(pushNotificationController).build();
	}
	
	
	@Test
	public void testSubscribePushNotificationSuccess() throws Exception {
		PushSubscription pushSubscription = getPushSubscription(true);
		 String generatedString = RandomStringUtils.randomAlphanumeric(10);
		 String token = RandomStringUtils.randomAlphanumeric(10);
		 pushSubscription.setUniqueId(generatedString);
		 Integer userId = 1234;
		 String pushType="MOBILE";
		 when(request.getHeader(Mockito.anyString())).thenReturn(token);
		when(jwtService.getUserId(Mockito.anyString())).thenReturn(userId);
		when(pushSubscriptionService.saveOrUpdatePushSubscription(pushSubscription, userId,pushType)).thenReturn(true);
		
		String requestBody = TestUtils.mapToJson(pushSubscription);
		ResultActions result = mockMvc
				.perform(post("/push/subscribe/unique/{uniqueId}", generatedString)
						.contentType(MediaType.APPLICATION_JSON).content(requestBody)
						.header("jwt-token", token))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.status", Matchers.is("success")))
				.andExpect(jsonPath("$.*", Matchers.hasSize(2))).andDo(print());
	}
	
	@Test
	public void testGetSubscription() throws Exception {
		PushSubscription pushSubscription = getPushSubscription(true);
		 String generatedString = RandomStringUtils.randomAlphanumeric(10);
		 String token = RandomStringUtils.randomAlphanumeric(10);
		 pushSubscription.setUniqueId(generatedString);
		 Integer userId = 1234;
		 when(request.getHeader(Mockito.anyString())).thenReturn(token);
		when(jwtService.getUserId(Mockito.anyString())).thenReturn(userId);
		when(pushSubscriptionService.getSubscriptionStatus(userId, generatedString)).thenReturn(true);
		
		ResultActions result = mockMvc
				.perform(get("/push/subscribe/unique/{uniqueId}", generatedString)
						.contentType(MediaType.APPLICATION_JSON)
						.header("jwt-token", token))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.status", Matchers.is("success")))
				.andExpect(jsonPath("$.*", Matchers.hasSize(2))).andDo(print());
	}
	
	private PushSubscription getPushSubscription(boolean valid) {
		PushSubscription push = new PushSubscription();
		PushSubscriptionKey keys = new PushSubscriptionKey();
		String validUrl = "https://fcm.googleapis.com/fcm/send/fa8BzZK6_Dk:APA91bHcBrHc7mUYFq6BP9fSsvhJylXQnoT_CG19XiA4ZjkEfhMWfifJBBxd_8XUQZCEy5Rzk_hOoGF5pwQ9ni8f9YKFVvlwG3uFZQvsJRv_J0CNbK_mzzW4_oGfabD_puQ2wC_3Cfev";
		String inValidUrl = "https://fcm.googleapis.com/fcm/send/fa8BzZK6_Dk:APA91bHcBrHc7mUYFq6BP9fSsvhJylXQnoT_CG19XiA4ZjkEfhMWfifJBBxd_8XUQZ%CEy5Rzk_hOoGF5>pwQ9ni8f9YKFVvlwG3uFZ<QvsJRv_J0CNbK_mzzW4_oGfabD_puQ2wC_3Cfev";
		if(valid)
			push.setEndpoint(validUrl);
		else
			push.setEndpoint(inValidUrl);
		keys.setP256dh("BAhDN_1O0GfnVxY7sIDIXqq7Xw00ANYbGI2C1k-fWlPqrZca3hpc40LPmPaPNDsTnP_sgoYfuc8uiMxKeKhOCwE");
		keys.setAuth("SGgKQHxASpRHWXLgXfHwIQ");
		push.setKeys(keys);
		return push;
	}
}
