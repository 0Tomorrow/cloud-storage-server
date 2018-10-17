package com.tlf.cloud.storage.server.bo;

import com.tlf.cloud.storage.server.entity.TFileInfo;
import lombok.Data;

@Data
public class UploadInfo {
    private Long fileId;

    private String mergeCode;

    private Integer fileState;

    private Long sliceSize;

    private Integer sliceCount;

    private Integer uploadCount;
}
