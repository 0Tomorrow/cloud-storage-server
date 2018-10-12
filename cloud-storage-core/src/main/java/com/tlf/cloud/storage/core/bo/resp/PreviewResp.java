package com.tlf.cloud.storage.core.bo.resp;

import lombok.Data;

@Data
public class PreviewResp {
    private String pdfPath;
    private Integer pdfIndex;
    private Integer pdfWidth;
    private Integer pdfHeight;
}
