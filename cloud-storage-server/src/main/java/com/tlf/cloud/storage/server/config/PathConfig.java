package com.tlf.cloud.storage.server.config;

import com.tlf.cloud.storage.server.entity.TFileInfo;
import com.tlf.cloud.storage.server.entity.TIndexInfo;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "cloud-storage.file")
public class PathConfig {
    private String rootPath;

    public String getRelativePath(String path) {
        if (path == null || path.equals("")) {
            return "/";
        }
        if (path.charAt(0) != '/') {
            path = "/" + path;
        }
        if (path.charAt(path.length() - 1) != '/') {
            path = path + "/";
        }
        return path;
    }

    public String getAbsolutePath(TIndexInfo tIndexInfo) {
        Long account = tIndexInfo.getUpdateBy();
        String relativePath = tIndexInfo.getPath();
        return getAbsolutePath(account, relativePath);
    }

    public String getAbsolutePath(Long account, String path) {
        String relativePath = getRelativePath(path);
        return rootPath + account + relativePath;
    }

    public String getTempPath(Long account, String path, String fileName) {
        String absolutePath = getAbsolutePath(account, path);
        return absolutePath + fileName + ".temp";
    }

    public String getAbsoluteFilePath(TFileInfo tFileInfo) {
        Long account = tFileInfo.getUpdateBy();
        String path = tFileInfo.getIndexInfo().getPath();
        String fileName = tFileInfo.getFileName();
        return getAbsoluteFilePath(account, path, fileName);
    }

    public String getAbsoluteFilePath(Long account, String path, String fileName) {
        String absolutePath = getAbsolutePath(account, path);
        return absolutePath + fileName;
    }

    public String getAbsoluteIndexPath(Long account, String path, String indexName) {
        String absolutePath = getAbsolutePath(account, path);
        return absolutePath + indexName + "/";
    }

    public String getRelativeIndexPath(String path, String indexName) {
        String relativePath = getRelativePath(path);
        return relativePath + indexName + "/";
    }

    public String getUserPath(Long account) {
        return getAbsolutePath(account, "");
    }
}
