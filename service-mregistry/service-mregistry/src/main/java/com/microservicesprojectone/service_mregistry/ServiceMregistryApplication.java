package com.microservicesprojectone.service_mregistry;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class ServiceMregistryApplication {

	public static void main(String[] args) {
		SpringApplication.run(ServiceMregistryApplication.class, args);
	}

}
