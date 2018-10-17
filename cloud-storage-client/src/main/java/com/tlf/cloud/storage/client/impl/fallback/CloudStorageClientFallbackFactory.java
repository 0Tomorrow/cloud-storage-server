package com.tlf.cloud.storage.client.impl.fallback;

import com.tlf.cloud.storage.client.impl.CloudStorageClient;
import com.tlf.common.lang.fallback.MyHystrixClientFallbackFactory;
import org.springframework.stereotype.Component;

@Component
public class CloudStorageClientFallbackFactory extends MyHystrixClientFallbackFactory<CloudStorageClient> {

}
