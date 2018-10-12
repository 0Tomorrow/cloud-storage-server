package com.tlf.cloud.storage.server.entity;

import com.tlf.commondata.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = TFileInfo.TABLE_NAME)
@Data
@EqualsAndHashCode(callSuper = false)
public class TFileInfo extends BaseEntity {
    protected static final String TABLE_NAME = "t_file_info";

    private String fileName;

    @ManyToOne(targetEntity = TIndexInfo.class)
    private TIndexInfo indexInfo;

    private String type;

    private Long fileSize;

    private String icon;
}
