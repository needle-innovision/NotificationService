package com.go.notification.push.models;

public class PushSubscriptionKey {
	private String p256dh;
	private String auth;
	public String getP256dh() {
		return p256dh;
	}
	public void setP256dh(String p256dh) {
		this.p256dh = p256dh;
	}
	public String getAuth() {
		return auth;
	}
	public void setAuth(String auth) {
		this.auth = auth;
	}
	@Override
	public String toString() {
		return "PushKeyModel [p256dh=" + p256dh + ", auth=" + auth + "]";
	}
}
