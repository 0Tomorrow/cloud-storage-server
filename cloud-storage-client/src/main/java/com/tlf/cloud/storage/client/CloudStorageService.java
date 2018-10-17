package com.tlf.cloud.storage.client;

import com.tlf.cloud.storage.client.impl.CloudStorageClient;
import com.tlf.cloud.storage.core.bo.req.IndexInfoReq;
import com.tlf.common.lang.bo.RespBase;
import com.tlf.common.lang.exception.MyException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CloudStorageService {
    @Autowired
    CloudStorageClient cloudStorageClient;

    public Boolean createRootIndex(Long account) {
        IndexInfoReq indexInfoReq = new IndexInfoReq();
        indexInfoReq.setUpdateBy(account);
        RespBase<Boolean> response = cloudStorageClient.createRootIndex(indexInfoReq);
        if (response.getCode() != 0 && response.getMsg() != null) {
            throw new MyException(response.getMsg());
        }
        return response.getData();
    }
}
