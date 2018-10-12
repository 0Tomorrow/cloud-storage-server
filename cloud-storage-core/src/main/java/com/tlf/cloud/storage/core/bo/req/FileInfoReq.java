package com.tlf.cloud.storage.core.bo.req;

import lombok.Data;

@Data
public class FileInfoReq {
    private String fileName;

    private String path;

    private String type;

    private Long fileSize;

    private Integer fileState;

    private Long sliceSize;

    private String mergeCode;

    private Integer sliceCount;

    private Integer uploadCount;

    private Long updateBy;

    private Long updateTime;

    private String icon;
}
