package com.tlf.cloud.storage.server.service.cache;

import com.tlf.cloud.storage.server.bo.UploadInfo;
import com.tlf.common.data.cache.CacheableService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class FileCacheService implements CacheableService {

    @Override
    public Map<String, Long> initCacheExpireTime() {
        return null;
    }

    @Override
    public int cacheDbIndex() {
        return 2;
    }

    @CachePut(value = "FileUploadInfo", key = "#key.toString()", condition = "#key != null")
    public UploadInfo putUploadInfo(String key, UploadInfo uploadInfo) {
        return uploadInfo;
    }

    @Cacheable(value = "FileUploadInfo", key = "#key.toString()", condition = "#key != null")
    public UploadInfo getUploadInfo(String key) {
        return null;
    }

    @CacheEvict(value = "FileUploadInfo", key = "#key.toString()", condition = "#key != null")
    public void deleteUploadInfo(String key){}
}
