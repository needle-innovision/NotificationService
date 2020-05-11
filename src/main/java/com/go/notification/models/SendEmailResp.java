package com.go.notification.models;

public class SendEmailResp {
	private String microServiceCode;
	private String status;
	private String transactionNo;

	public String getMicroServiceCode() {
		return microServiceCode;
	}

	public void setMicroServiceCode(String microServiceCode) {
		this.microServiceCode = microServiceCode;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getTransactionNo() {
		return transactionNo;
	}

	public void setTransactionNo(String transactionNo) {
		this.transactionNo = transactionNo;
	}

	@Override
	public String toString() {
		return "SendEmailResp [microServiceCode=" + microServiceCode + ", status=" + status + ", transactionNo="
				+ transactionNo + "]";
	}

}
