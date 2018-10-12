package com.tlf.cloud.storage.client;

import com.tlf.cloud.storage.client.impl.CloudStorageClient;
import com.tlf.cloud.storage.core.bo.req.IndexInfoReq;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CloudStorageService {
    @Autowired
    CloudStorageClient cloudStorageClient;

    public Boolean createRootIndex(Long account) {
        IndexInfoReq indexInfoReq = new IndexInfoReq();
        indexInfoReq.setUpdateBy(account);
        return cloudStorageClient.createRootIndex(indexInfoReq);
    }
}
