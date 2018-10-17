package com.tlf.cloud.storage.server.entity;

import com.tlf.common.data.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = TIndexInfo.TABLE_NAME)
@Data
@EqualsAndHashCode(callSuper = false)
@ToString(exclude = "prevIndex")
public class TIndexInfo extends BaseEntity {
    protected static final String TABLE_NAME = "t_index_info";

    private String indexName;

    @ManyToOne(targetEntity = TIndexInfo.class)
    private TIndexInfo prevIndex;

    private String path;
}
