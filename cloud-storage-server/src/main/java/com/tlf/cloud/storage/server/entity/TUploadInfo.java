package com.tlf.cloud.storage.server.entity;

import com.tlf.commondata.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Entity;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = TUploadInfo.TABLE_NAME)
@Data
@EqualsAndHashCode(callSuper = false)
public class TUploadInfo extends BaseEntity {
    protected static final String TABLE_NAME = "t_upload_info";

    @OneToOne(targetEntity = TFileInfo.class)
    private TFileInfo tFileInfo;

    private String mergeCode;

    private Integer fileState;

    private Long sliceSize;

    private Integer sliceCount;

    private Integer uploadCount;
}
