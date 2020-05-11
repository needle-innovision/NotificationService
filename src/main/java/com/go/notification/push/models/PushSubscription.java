package com.go.notification.push.models;

public class PushSubscription {
	
	private String endpoint;
	private String expirationTime;
	private PushSubscriptionKey keys;
	private String uniqueId;
	private String token;
	private int userId;
	
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public String getEndpoint() {
		return endpoint;
	}
	public void setEndpoint(String endpoint) {
		this.endpoint = endpoint;
	}
	public String getExpirationTime() {
		return expirationTime;
	}
	public void setExpirationTime(String expirationTime) {
		this.expirationTime = expirationTime;
	}
	public PushSubscriptionKey getKeys() {
		return keys;
	}
	public void setKeys(PushSubscriptionKey keys) {
		this.keys = keys;
	}
	public String getUniqueId() {
		return uniqueId;
	}
	public void setUniqueId(String uniqueId) {
		this.uniqueId = uniqueId;
	}
	@Override
	public String toString() {
		return "PushSubscription [endpoint=" + endpoint + ", expirationTime=" + expirationTime + ", keys=" + keys
				+ ", uniqueId=" + uniqueId + ", token=" + token + ", userId=" + userId + "]";
	}
	
	
	
}
