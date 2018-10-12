package com.tlf.cloud.storage.core.bo.req;

import lombok.Data;

@Data
public class IndexInfoReq {
    private String indexName;

    private String path;

    private Long updateBy;
}
