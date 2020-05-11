package com.go.notification;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

import com.go.notification.event.PushNotificationListener;
import com.google.common.eventbus.EventBus;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
@ComponentScan({ "com.go.notification","com.ne.commons" })
public class NeNotificationServiceApplication {

	@Autowired
	PushNotificationListener pushNotificationListener;
	

	public static void main(String[] args) {
		SpringApplication.run(NeNotificationServiceApplication.class, args);
	}

	@Bean
	public RestTemplate restTemplate(RestTemplateBuilder builder) {
		return builder.build();
	}

	/*@Bean
	@Primary
	public DataSource dataSource(@Value("${spring.datasource.driver-class-name}") String DB_DRIVER,
			@Value("${spring.datasource.password}") String DB_PASSWORD,
			@Value("${spring.datasource.url}") String DB_URL,
			@Value("${spring.datasource.username}") String DB_USERNAME) {

		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		dataSource.setDriverClassName(DB_DRIVER);
		dataSource.setUrl(DB_URL);
		dataSource.setUsername(DB_USERNAME);
		dataSource.setPassword(DB_PASSWORD);
		return dataSource;

	}*/

	@Bean
	@ConditionalOnProperty(name = "spring.profiles.active", havingValue = "local")
	public WebMvcConfigurer corsConfigurer() {
		return new WebMvcConfigurerAdapter() {
			@Override
			public void addCorsMappings(CorsRegistry registry) {
				registry.addMapping("/**").allowedOrigins("*").allowedMethods("GET", "POST", "DELETE", "PUT",
						"OPTIONS");
			}
		};
	}

	@Bean
	public EventBus eventBus() {
		EventBus eventBus = new EventBus();
		eventBus.register(pushNotificationListener);
		return eventBus;
	}

	@Configuration
	public static class SecurityPermitAllConfig extends WebSecurityConfigurerAdapter {
		@Override
		protected void configure(HttpSecurity http) throws Exception {
			http.authorizeRequests().anyRequest().permitAll().and().csrf().disable();
		}
	}
}
