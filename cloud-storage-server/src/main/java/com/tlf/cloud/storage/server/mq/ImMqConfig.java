package com.tlf.cloud.storage.server.mq;

import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableBinding({MqProcessor.class})
public class ImMqConfig {

}
