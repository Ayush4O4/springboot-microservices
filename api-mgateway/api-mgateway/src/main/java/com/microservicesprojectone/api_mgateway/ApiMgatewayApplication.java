package com.microservicesprojectone.api_mgateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class ApiMgatewayApplication {

	public static void main(String[] args) {
		SpringApplication.run(ApiMgatewayApplication.class, args);
	}

}
