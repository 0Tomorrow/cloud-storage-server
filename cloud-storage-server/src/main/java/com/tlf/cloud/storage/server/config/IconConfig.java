package com.tlf.cloud.storage.server.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "cloud-storage.icon")
public class IconConfig {
    private String folderIcon;
    private String txtIcon;
    private String pdfIcon;
    private String exeIcon;
    private String rarIcon;
    private String unKnowIcon;

    public String getIcon(String type) {
        switch (type) {
            case "folder" : return folderIcon;
            case "txt" : return txtIcon;
            case "pdf" : return pdfIcon;
            case "exe" : return exeIcon;
            case "rar" : return rarIcon;
            default: return unKnowIcon;
        }
    }
}
