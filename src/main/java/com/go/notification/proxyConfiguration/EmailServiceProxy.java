package com.go.notification.proxyConfiguration;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import com.go.notification.models.EmailServiceRequest;
import com.go.notification.models.SendEmailResp;

@FeignClient(name = "EmailService")
public interface EmailServiceProxy {

	@RequestMapping("/sendemails")
	public SendEmailResp sendEmail(@RequestBody EmailServiceRequest emailRequest);
}
