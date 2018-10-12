package com.tlf.cloud.storage.server.entity;

import com.tlf.commondata.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = TPdfInfo.TABLE_NAME)
@Data
@EqualsAndHashCode(callSuper = false)
public class TPdfInfo extends BaseEntity {
    protected static final String TABLE_NAME = "t_pdf_info";
    private Integer pdfIndex;
    private String pdfPath;
    private Integer pdfWidth;
    private Integer pdfHeight;
    private String pdfMd5;
}
