package com.blusalt.dbxpbackgroundservice;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

@SpringBootApplication
@EnableFeignClients
@ConditionalOnProperty(value = "spring.profiles.active", havingValue = "local")
public class BlusaltDbxpBackgroundServiceApplicationTests {

	public static void main(String[] args) {
		SpringApplication.run(BlusaltDbxpBackgroundServiceApplicationTests.class, args);
	}

	@Bean
	public ModelMapper modelMapper(){ return new ModelMapper();}

}