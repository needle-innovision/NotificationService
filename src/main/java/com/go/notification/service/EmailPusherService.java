package com.go.notification.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ne.commons.constants.EventConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import com.go.notification.models.EmailServiceRequest;
import com.go.notification.proxyConfiguration.EmailServiceProxy;
import com.ne.commons.notification.vo.EmailNotificationVO;
import com.ne.commons.notification.vo.User;

@Component
public class EmailPusherService {

	@Autowired
	EmailServiceProxy emailService;
	
	@Value("${fromEmail}")
	String fromEmail;

	private static final Logger log = LoggerFactory.getLogger(EmailPusherService.class);

	public void pushEmailNotification(EmailNotificationVO notificationObj) {
		try {
			EmailServiceRequest emailRequest = new EmailServiceRequest();
			emailRequest.setEmailTemplateName(notificationObj.getTemplatePath());

			List<String> toList = new ArrayList<String>();
			for (User user : notificationObj.getUsers()) {
				toList.add(user.getEmail());
			}
			emailRequest.setToMails(toList);

//			if((notificationObj.getEventId()== EventConstants.MAIL_TO_KEEPIT.getValue())||(notificationObj.getEventId()== EventConstants.MAIL_TO_OM_TEAM.getValue())){
//				Map<String,Object> attributes= new HashMap<>(notificationObj.getTemplateAttributes());
//				List<String> ccList = new ArrayList<String>();
//				List<String> bccList = new ArrayList<>();
//				List<String> tolist = new ArrayList<>();
//
//				if (!((List) attributes.get("emailCc")).isEmpty()) {
//					for ( Object user : ((List)attributes.get("emailCc"))) {
//						ccList.add((String) user);
//					}
//					emailRequest.setCcMails(ccList);
//				}
//				if (!((List) attributes.get("emailBcc")).isEmpty()) {
//					for ( Object user : ((List)attributes.get("emailBcc"))) {
//						bccList.add((String) user);
//					}
//					emailRequest.setBccMails(bccList);
//				}
//				if (!((List) attributes.get("emailTo")).isEmpty()) {
//					for ( Object user : ((List)attributes.get("emailTo"))) {
//						tolist.add((String) user);
//					}
//					emailRequest.setToMails(tolist);
//				}
//				emailRequest.setEmailTemplateName((String)attributes.get("emailTemplate"));
//
//			}
			emailRequest.setEmailSubject(notificationObj.getEmailSubject());
			emailRequest.setFromEmail(fromEmail);
			emailRequest.setTemplateAttributes(notificationObj.getTemplateAttributes());
			emailRequest.setMicroServiceCode("NotificationService");
			log.info("***********************EMAIL*******************"+notificationObj.getAction()+"****:"+notificationObj.getEmailSubject());

			emailService.sendEmail(emailRequest);
			log.info("Notification with Id " + notificationObj.getNotificationId()
					+ " sent successfull to the Email Service");

		} catch (Exception e) {
			log.error("Exception in pushing email notification to Email Service:", e);
		}

	}

}
