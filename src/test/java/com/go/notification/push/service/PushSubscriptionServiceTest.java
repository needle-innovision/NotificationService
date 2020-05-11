package com.go.notification.push.service;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.sql.SQLException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.go.notification.dao.PushServiceDAO;
import com.go.notification.push.models.PushSubscription;
import com.go.notification.push.models.PushSubscriptionKey;

@RunWith(SpringJUnit4ClassRunner.class)
public class PushSubscriptionServiceTest {
	
	@MockBean
	PushServiceDAO pushServiceDAO;
	
	@InjectMocks
	PushSubscriptionServiceImpl pushSubscriptionServiceImpl;
	
	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
	}
	
	@Test
	public void testSaveOrUpdatePushSubscription() throws SQLException {
		PushSubscription sub = getSubscriptionInfo();
		when(pushServiceDAO.saveOrUpdateSubscription(sub, 2343,"MOBILE")).thenReturn(true);
		boolean actual = pushSubscriptionServiceImpl.saveOrUpdatePushSubscription(sub, 2343,"MOBILE");
		assertTrue(actual);
	}
	
	@Test
	public void testRemoveSubscriptionRecord() throws SQLException {
		when(pushServiceDAO.removeSubscriptionRecord(2343, "74084c385bbf8e03b964bd7af8a908ec"))
			.thenReturn(true);
		boolean actual = pushSubscriptionServiceImpl.removeSubscription(2343, "74084c385bbf8e03b964bd7af8a908ec");
		assertTrue(actual);
	}
	
	@Test
	public void testGetSubscriptionStatus() throws SQLException {
		when(pushServiceDAO.getSubscriptionStatus(2343, "74084c385bbf8e03b964bd7af8a908ec"))
		.thenReturn(true);
		boolean actual = pushSubscriptionServiceImpl.getSubscriptionStatus(2343, "74084c385bbf8e03b964bd7af8a908ec");
		assertTrue(actual);
	}
	
	private PushSubscription getSubscriptionInfo() {
		PushSubscription sub = new PushSubscription();
		PushSubscriptionKey subKeys = new PushSubscriptionKey();
		subKeys.setP256dh("BPrnyaMKG7bqvwj6r74MrxwcTR1sfK_TGNqB3rWXDuGENs_k27mQXFLyaQCN3iDsGRtHDIDSJ_FOVsiiLtesD0s");
		subKeys.setAuth("uUoLLctb9cM_ZzVXYhlGrA");
		sub.setEndpoint("https://updates.push.services.mozilla.com/wpush/v2/gAAAAABbs5NJxQ6cLR68PuD3Ny7D90fszty4e4am_WRTUlJbStj4AVtdxV7FJaWhUZquS9crRsF452KVR5TQ9-7_0DKyVb6KOEHyUAw3vJYh9KFKXyhjY7Bhw3pkshkUUDEd9lub14dED1O4izf52feiYVrv6TXTI_8Sjh63Xb4iiqglVJz63kk");
		sub.setUniqueId("74084c385bbf8e03b964bd7af8a908ec");
		return sub;
	}

}
