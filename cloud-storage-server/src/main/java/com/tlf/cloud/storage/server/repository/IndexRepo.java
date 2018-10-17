package com.tlf.cloud.storage.server.repository;

import com.tlf.cloud.storage.server.entity.TIndexInfo;
import com.tlf.common.data.repository.MyRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface IndexRepo extends MyRepository<TIndexInfo, Long> {

    TIndexInfo findFirstByUpdateByAndPath(Long account, String path);

    List<TIndexInfo> findAllByPrevIndexId(Long id);

    @Query(nativeQuery = true, value = "SELECT * FROM t_index_info t1 " +
            "JOIN t_index_info t2 " +
            "on t1.prev_index_id = t2.id " +
            "WHERE t2.path = ?2 AND t1.update_by = ?1")
    List<TIndexInfo> findAllByUpdateByAndPrevPath(Long account, String prevPath);

    TIndexInfo findFirstById(Long id);

}
