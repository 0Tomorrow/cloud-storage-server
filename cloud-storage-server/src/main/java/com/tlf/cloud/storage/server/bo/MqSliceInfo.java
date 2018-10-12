package com.tlf.cloud.storage.server.bo;

import lombok.Data;

@Data
public class MqSliceInfo {
    private String mergeCode;
    private byte[] file;
    private Integer index;
}
