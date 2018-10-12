package com.tlf.cloud.storage.client.impl;

import com.tlf.cloud.storage.client.impl.fallback.CloudStorageClientFallbackFactory;
import com.tlf.cloud.storage.core.bo.req.IndexInfoReq;
import com.tlf.cloud.storage.core.config.RemoteConfig;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient(name = RemoteConfig.SERVICE_NAME, fallbackFactory = CloudStorageClientFallbackFactory.class)
public interface CloudStorageClient {

    @RequestMapping(value = "/index/root/create")
    Boolean createRootIndex(@RequestBody IndexInfoReq indexInfoReq);
}
