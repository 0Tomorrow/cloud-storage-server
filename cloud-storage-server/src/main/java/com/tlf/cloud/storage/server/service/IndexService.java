package com.tlf.cloud.storage.server.service;

import com.tlf.cloud.storage.server.config.IconConfig;
import com.tlf.cloud.storage.server.config.PathConfig;
import com.tlf.cloud.storage.server.entity.TFileInfo;
import com.tlf.cloud.storage.server.entity.TIndexInfo;
import com.tlf.cloud.storage.server.file.FileUtil;
import com.tlf.cloud.storage.server.file.IndexUtil;
import com.tlf.cloud.storage.server.repository.FileRepo;
import com.tlf.cloud.storage.server.repository.IndexRepo;
import com.tlf.cloud.storage.core.bo.req.IndexInfoReq;
import com.tlf.cloud.storage.core.bo.resp.IndexInfoResp;
import com.tlf.common.lang.exception.MyException;
import com.tlf.common.lang.util.MyBeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class IndexService {

    @Autowired
    PathConfig pathConfig;

    @Autowired
    IconConfig iconConfig;

    @Autowired
    IndexRepo indexRepo;

    @Autowired
    FileRepo fileRepo;

    public List<IndexInfoResp> findIndex(Long updateBy, String path) {

        String relativePath = pathConfig.getRelativePath(path);
        List<IndexInfoResp> list = new ArrayList<>();
        List<TIndexInfo> tList = indexRepo.findAllByUpdateByAndPrevPath(updateBy, relativePath);
        for (TIndexInfo tIndexInfo : tList) {
            IndexInfoResp indexInfoResp = MyBeanUtils.copyProperties2(tIndexInfo, IndexInfoResp.class);
            indexInfoResp.setIcon(iconConfig.getIcon("folder"));
            indexInfoResp.setId(tIndexInfo.getId().toString());
            list.add(indexInfoResp);
        }
        return list;
    }

    public void createIndex(IndexInfoReq indexInfo) {
        String path = indexInfo.getPath();
        String indexName = indexInfo.getIndexName();
        Long account = indexInfo.getUpdateBy();

        String relativePath = pathConfig.getRelativePath(path);
        String relativeIndexPath = pathConfig.getRelativeIndexPath(path, indexName);
        String absolutePath = pathConfig.getAbsoluteIndexPath(account, path, indexName);

        TIndexInfo index = indexRepo.findFirstByUpdateByAndPath(account, relativeIndexPath);
        if (index != null) {
            throw new MyException("该文件夹已存在: " + relativeIndexPath);
        }

        IndexUtil.createIndex(absolutePath);

        TIndexInfo tIndexInfo = new TIndexInfo();
        tIndexInfo.setUpdateBy(account);
        tIndexInfo.setIndexName(indexName);
        tIndexInfo.setPath(relativeIndexPath);
        tIndexInfo.setPrevIndex(indexRepo.findFirstByUpdateByAndPath(account, relativePath));

        indexRepo.save(tIndexInfo);
    }

    public void deleteIndex(String id, boolean sure) {
        if (haveFile(id) && !sure) {
            throw new MyException("该文件夹下还有文件，请确认是否全部删除");
        }
        deleteAllIndex(id);
    }

    private boolean haveFile(String indexId) {
        List<TFileInfo> fileList = fileRepo.findAllByIndexInfoId(Long.parseLong(indexId));
        if (fileList != null && fileList.size() != 0) {
            return true;
        }
        List<TIndexInfo> indexList = indexRepo.findAllByPrevIndexId(Long.parseLong(indexId));
        for (TIndexInfo tIndexInfo : indexList) {
            if (haveFile(tIndexInfo.getId().toString())) {
                return true;
            }
        }
        return false;
    }

    @Transactional
    public void deleteAllIndex(String indexId) {
        deleteAllFile(indexId);
        TIndexInfo tIndexInfo = indexRepo.findOne(Long.parseLong(indexId));
        if (tIndexInfo == null) {
            throw new MyException("文件夹不存在");
        }
        String indexAbsolutePath = pathConfig.getAbsolutePath(tIndexInfo);

        List<TIndexInfo> indexList = indexRepo.findAllByPrevIndexId(Long.parseLong(indexId));
        if (indexList != null && indexList.size() != 0) {
            for (TIndexInfo indexInfo : indexList) {
                deleteAllIndex(indexInfo.getId().toString());
            }
        }
        indexRepo.delete(Long.parseLong(indexId));
        IndexUtil.deleteIndex(indexAbsolutePath);
    }

    private void deleteAllFile(String indexId) {
        List<TFileInfo> fileList = fileRepo.findAllByIndexInfoId(Long.parseLong(indexId));

        if (fileList == null || fileList.size() == 0) {
            return;
        }
        fileRepo.deleteAllByIndexInfoId(Long.parseLong(indexId));
        for (TFileInfo tFileInfo : fileList) {
            String absoluteFilePath = pathConfig.getAbsoluteFilePath(tFileInfo);
            FileUtil.deleteFile(absoluteFilePath);
        }
    }

    @Transactional
    public void createRootIndex(IndexInfoReq indexInfoReq) {
        Long account = indexInfoReq.getUpdateBy();
        String userPath = pathConfig.getUserPath(account);
        IndexUtil.createIndex(userPath);

        TIndexInfo tIndexInfo1 = indexRepo.findFirstById(0L);
        if (tIndexInfo1 == null) {
            tIndexInfo1 = new TIndexInfo();
            tIndexInfo1.setId(0L);
            tIndexInfo1.setPrevIndex(tIndexInfo1);
            indexRepo.save(tIndexInfo1);
        }

        TIndexInfo tIndexInfo = new TIndexInfo();
        tIndexInfo.setUpdateBy(account);
        tIndexInfo.setPath("/");
        tIndexInfo.setPrevIndex(tIndexInfo1);

        indexRepo.save(tIndexInfo);
    }
}
