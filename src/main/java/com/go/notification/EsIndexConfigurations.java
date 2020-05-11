package com.go.notification;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EsIndexConfigurations {
	
	@Value("${elasticsearch.indexName}")
	String indexName;
	
	@Bean
	public String indexName(){
	    return indexName;
	}
	

}
