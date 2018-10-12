package com.tlf.cloud.storage.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@EnableEurekaClient
@SpringBootApplication
public class CloudStorageServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(CloudStorageServerApplication.class, args);
	}
}
