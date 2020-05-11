package com.go.notification.push.service;

import static org.junit.Assert.assertNotNull;

import org.json.JSONException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.go.notification.utils.TestUtils;
import com.ne.commons.events.BaseEvent;
import com.ne.commons.notification.vo.PushNotificationVO;
import com.ne.commons.notification.vo.PushResponseVO;
import com.ne.commons.user.User;

@RunWith(SpringJUnit4ClassRunner.class)
public class PushNotificationServiceTest {
	
	@InjectMocks
	PushNotificationServiceImpl pushNotificationServiceImpl;
	
	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
	}
	
	@Test
	public void testConstructPushResponse() throws JSONException, Exception {
		PushNotificationVO pushNotification = new PushNotificationVO();
		BaseEvent baseEvent = new BaseEvent();
		User user = new User();
		user.setFirstName("NetEnrich");
		user.setLastName("GO");
		baseEvent.setUser(user);
		baseEvent.setChannelName("NetEnrich Go");
		pushNotification.setBaseEvent(baseEvent);
		pushNotification.setAction("CONVERSATION_WARROOM_STARTED");
		pushNotification.setConvTitle("Test Conversation");
		pushNotification.setConvUrl("http://localhost:4000/conversation");
		
		PushResponseVO actualResp = pushNotificationServiceImpl.constructPushResponse(pushNotification);
		
		PushResponseVO dummyResp = new PushResponseVO();
		dummyResp.setActionUserFullName("NetEnrich GO");
		dummyResp.setBodyText("#"+pushNotification.getBaseEvent().getChannelName()+":"+pushNotification.getConvTitle());
		dummyResp.setConversationUrl(pushNotification.getConvUrl());
		dummyResp.setTitle(pushNotification.getAction());
		dummyResp.setImageUrl("../static/images/favicon.ico");
		
		assertNotNull(actualResp);
		JSONAssert.assertEquals(TestUtils.mapToJson(dummyResp), TestUtils.mapToJson(actualResp), false);
	}
	
}
