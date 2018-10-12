package com.tlf.cloud.storage.server.config;

import com.tlf.cloud.storage.server.entity.TPdfInfo;
import com.tlf.cloud.storage.server.util.PdfUtil;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Data
@Component
@ConfigurationProperties(prefix = "cloud-storage.preview")
public class PreviewConfig {
    private String pdfImgRelativePath;
    private String pdfImgAbsolutePath;

    public List<TPdfInfo> pdfPreview(String path, String pdfMd5) {
        String imgPath = pdfImgAbsolutePath + pdfMd5 + "/";
        String relativePath = pdfImgRelativePath + pdfMd5 + "/";
        List<TPdfInfo> list = PdfUtil.pdfToImageList(path, imgPath, relativePath);
        if (list == null) {
            return null;
        }
        for (TPdfInfo tPdfInfo : list) {
            tPdfInfo.setPdfMd5(pdfMd5);
        }
        return list;
    }
}
