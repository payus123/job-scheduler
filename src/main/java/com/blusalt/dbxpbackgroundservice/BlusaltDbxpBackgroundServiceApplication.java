package com.blusalt.dbxpbackgroundservice;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication(scanBasePackages = "com.blusalt")
@OpenAPIDefinition
@ConditionalOnProperty(value = "spring.profiles.active", havingValue = "prod")
public class BlusaltDbxpBackgroundServiceApplication {
	public static void main(String[] args) {
		SpringApplication.run(BlusaltDbxpBackgroundServiceApplication.class, args);
	}
}
