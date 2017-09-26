package com.connection.application;

import static springfox.documentation.builders.PathSelectors.regex;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@EnableSwagger2
@ComponentScan("com.connection.*")
public class CompanyExampleApplication {

	public static void main(String[] args) {
		SpringApplication.run(CompanyExampleApplication.class, args);
	}

	@Bean
	public Docket productApi() {
		return new Docket(DocumentationType.SWAGGER_2)
				.groupName("Swagger2")
				.apiInfo(apiInfo())
				.select()
				.apis(RequestHandlerSelectors.basePackage("com.connection.controller"))
				.paths(regex("/company.*"))
				.build();
	}

	private ApiInfo apiInfo() {
		return new ApiInfo("Spring Boot REST API", "Spring Boot REST API for a Company", "1.0",
				"Terms of service",
				new Contact("Vasileios", "Oikonomou", "Application"),
				"Apache License Version 2.0", "https://www.apache.org/licenses/LICENSE-2.0");

	}

}
