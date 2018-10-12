package com.tlf.cloud.storage.core.config;

import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@Configuration(RemoteConfig.SERVICE_NAME)
@EnableFeignClients(basePackages = { "com.tlf.cloud.storage.client" })
public class RemoteConfig {

	public final static String SERVICE_NAME = "cloud-storage-server";
}
