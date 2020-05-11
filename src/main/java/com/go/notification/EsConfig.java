package com.go.notification;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EsConfig {

	@Value("${elasticsearch.clustername}")
	String clusterName;

	@Value("${elasticsearch.host}")
	String hostName;
	
	@Value("${elasticsearch.transport.port}")
	int transportPort;

	@Bean
	public TransportClient client() throws Exception {

		Settings settings = Settings.builder().put("cluster.name", clusterName).build();
		TransportClient client = new PreBuiltTransportClient(settings);
		client.addTransportAddress(
				new InetSocketTransportAddress(new InetSocketAddress(InetAddress.getByName(hostName), transportPort)));
		return client;

	}

}
