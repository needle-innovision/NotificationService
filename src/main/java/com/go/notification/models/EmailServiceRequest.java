package com.go.notification.models;

import java.util.List;
import java.util.Map;

public class EmailServiceRequest {
	private String fromEmail;
	private String fromName;
	private List<String> toMails;
	private List<String> ccMails;
	private List<String> bccMails;
	private String emailSubject;
	private String microServiceCode;
	private String emailTemplateName;
	private Map<String, String> templateAttributes;
	private String fileServiceId;

	public String getFromEmail() {
		return fromEmail;
	}

	public void setFromEmail(String fromEmail) {
		this.fromEmail = fromEmail;
	}

	public String getFromName() {
		return fromName;
	}

	public void setFromName(String fromName) {
		this.fromName = fromName;
	}

	public List<String> getToMails() {
		return toMails;
	}

	public void setToMails(List<String> toMails) {
		this.toMails = toMails;
	}

	public List<String> getCcMails() {
		return ccMails;
	}

	public void setCcMails(List<String> ccMails) {
		this.ccMails = ccMails;
	}

	public List<String> getBccMails() {
		return bccMails;
	}

	public void setBccMails(List<String> bccMails) {
		this.bccMails = bccMails;
	}

	public String getEmailSubject() {
		return emailSubject;
	}

	public void setEmailSubject(String emailSubject) {
		this.emailSubject = emailSubject;
	}

	public String getMicroServiceCode() {
		return microServiceCode;
	}

	public void setMicroServiceCode(String microServiceCode) {
		this.microServiceCode = microServiceCode;
	}

	public String getEmailTemplateName() {
		return emailTemplateName;
	}

	public void setEmailTemplateName(String emailTemplateName) {
		this.emailTemplateName = emailTemplateName;
	}

	public Map<String, String> getTemplateAttributes() {
		return templateAttributes;
	}

	public void setTemplateAttributes(Map<String, String> templateAttributes) {
		this.templateAttributes = templateAttributes;
	}

	public String getFileServiceId() {
		return fileServiceId;
	}

	public void setFileServiceId(String fileServiceId) {
		this.fileServiceId = fileServiceId;
	}

	@Override
	public String toString() {
		return "EmailServiceRequest [fromEmail=" + fromEmail + ", fromName=" + fromName + ", toMails=" + toMails
				+ ", ccMails=" + ccMails + ", bccMails=" + bccMails + ", emailSubject=" + emailSubject
				+ ", microServiceCode=" + microServiceCode + ", emailTemplateName=" + emailTemplateName
				+ ", templateAttributes=" + templateAttributes + ", fileServiceId=" + fileServiceId + "]";
	}

}
