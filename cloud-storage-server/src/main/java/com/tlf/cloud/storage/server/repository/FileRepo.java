package com.tlf.cloud.storage.server.repository;

import com.tlf.cloud.storage.server.entity.TFileInfo;
import com.tlf.commondata.repository.MyRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface FileRepo extends MyRepository<TFileInfo, Long> {
    @Query(nativeQuery = true, value = "SELECT * FROM t_file_info t1 " +
            "JOIN t_index_info t2 ON t1.index_info_id = t2.id " +
            "WHERE t2.path = ?2 AND t1.update_by = ?1")
    List<TFileInfo> findAllByUpdateByAndPath(Long account, String path);

    @Query(nativeQuery = true, value = "SELECT * FROM t_file_info t1 where index_info_id = ?1")
    List<TFileInfo> findAllByIndexInfoId(Long indexId);

    @Modifying
    @Transactional
    @Query(nativeQuery = true, value = "DELETE FROM t_file_info t1 where index_info_id = ?1")
    void deleteAllByIndexInfoId(Long indexId);


}
