package com.tlf.cloud.storage.server.service;

import com.tlf.cloud.storage.server.bo.MqSliceInfo;
import com.tlf.cloud.storage.server.bo.UploadInfo;
import com.tlf.cloud.storage.server.config.IconConfig;
import com.tlf.cloud.storage.server.config.PathConfig;
import com.tlf.cloud.storage.server.config.PreviewConfig;
import com.tlf.cloud.storage.server.entity.TFileInfo;
import com.tlf.cloud.storage.server.entity.TPdfInfo;
import com.tlf.cloud.storage.server.file.FileUtil;
import com.tlf.cloud.storage.server.file.TempFileUtil;
import com.tlf.cloud.storage.server.mq.MqFileService;
import com.tlf.cloud.storage.server.repository.FileRepo;
import com.tlf.cloud.storage.server.repository.IndexRepo;
import com.tlf.cloud.storage.server.repository.PdfRepo;
import com.tlf.cloud.storage.core.bo.req.FileInfoReq;
import com.tlf.cloud.storage.core.bo.resp.FileInfoResp;
import com.tlf.cloud.storage.core.bo.resp.PreviewResp;
import com.tlf.cloud.storage.core.enums.FileState;
import com.tlf.cloud.storage.server.service.cache.FileCacheService;
import com.tlf.common.lang.exception.MyException;
import com.tlf.common.lang.util.MyBeanUtils;
import com.tlf.common.lang.util.StringEncoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class FileService {
    @Autowired
    PathConfig pathConfig;

    @Autowired
    IconConfig iconConfig;

    @Autowired
    PreviewConfig previewConfig;

    @Autowired
    FileRepo fileRepo;

    @Autowired
    IndexRepo indexRepo;

    @Autowired
    MqFileService mqFileService;

    @Autowired
    PdfRepo pdfRepo;

    @Autowired
    FileCacheService fileCacheService;

    @Transactional
    public List<FileInfoResp> findFile(Long account, String path) {
        if (account == null) {
            throw new MyException("用户未登录");
        }
//        if (page == null || limit == null) {
//            return null;
//        }
//        Pageable pageable = new PageRequest(page, limit);
        String relativePath = pathConfig.getRelativePath(path);
        List<FileInfoResp> list = new ArrayList<>();
        List<TFileInfo> tList = fileRepo.findAllByUpdateByAndPath(account, relativePath);
        for (TFileInfo tFileInfo : tList) {
            FileInfoResp fileInfoResp = MyBeanUtils.copyProperties2(tFileInfo, FileInfoResp.class);
            fileInfoResp.setId(tFileInfo.getId().toString());
            list.add(fileInfoResp);
        }
        return list;
    }

    public String handShake(FileInfoReq fileInfoReq) {
        String fileName = fileInfoReq.getFileName();
        Long account = fileInfoReq.getUpdateBy();
        String path = fileInfoReq.getPath();
        String tempPath = pathConfig.getTempPath(account, path, fileName);

        String type = fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length());
        fileInfoReq.setType(type);
        String icon = iconConfig.getIcon(type);
        fileInfoReq.setIcon(icon);
        try {
            log.info("create temp file : {}, size : {}", tempPath, fileInfoReq.getFileSize());
            TempFileUtil.createTempFile(tempPath, fileInfoReq.getFileSize());
        } catch (Exception e) {
            throw new MyException("temp文件创建失败:" + tempPath);
        }
        return saveFileInfoAndUpdateInfo(fileInfoReq);
    }

    private String veriFileName(String fileName, Long account, String path) {
        int index = 0;
        String testFileName = fileName;
        while (true) {
            if (!exist(testFileName, account, path)) {
                return index == 0 ? fileName : testFileName;
            }
            index++;
            int s = fileName.lastIndexOf('.');
            String type = fileName.substring(s, fileName.length());
            testFileName = fileName.substring(0, s);
            testFileName = testFileName + "(" + index + ")" + type;
        }
    }

    private boolean exist(String fileName, Long account, String path) {
        List<FileInfoResp> fileList = findFile(account, path);
        for(FileInfoResp fileInfoResp : fileList) {
            if (fileInfoResp.getFileName().equals(fileName)) {
                return true;
            }
        }
        return false;
    }

    @Transactional
    public String saveFileInfoAndUpdateInfo(FileInfoReq fileInfo) {
        Long account = fileInfo.getUpdateBy();
        String path = fileInfo.getPath();
        String fileName = fileInfo.getFileName();
        fileName = veriFileName(fileName, account, path);
        fileInfo.setFileName(fileName);
        String relativePath = pathConfig.getRelativePath(path);
        TFileInfo tFileInfo = MyBeanUtils.copyProperties2(fileInfo, TFileInfo.class);
        tFileInfo.setIndexInfo(indexRepo.findFirstByUpdateByAndPath(account, relativePath));

        try {
            tFileInfo = fileRepo.save(tFileInfo);
        } catch (Exception e) {
            throw new MyException("save tFileInfo时出错: " + tFileInfo + "\n" + e.getMessage());
        }

        UploadInfo uploadInfo = new UploadInfo();
        uploadInfo.setFileId(tFileInfo.getId());
        uploadInfo.setFileState(FileState.HANDSHAKE.getCode());
        uploadInfo.setMergeCode(StringEncoder.encodeByMD5(account + ":" + System.currentTimeMillis()));
        uploadInfo.setUploadCount(0);
        uploadInfo.setSliceSize(fileInfo.getSliceSize());
        uploadInfo.setSliceCount(fileInfo.getSliceCount());
        try {
            fileCacheService.putUploadInfo(uploadInfo.getMergeCode(), uploadInfo);
        } catch (Exception e) {
            throw new MyException("save UploadInfo时出错: "  + e.getMessage());
        }

        return uploadInfo.getMergeCode();
    }

    @Transactional
    public void mqMerge(MultipartFile file, Integer index, String mergeCode) {
        UploadInfo uploadInfo = fileCacheService.getUploadInfo(mergeCode);

        if (null == uploadInfo) {
            throw new MyException("文件识别码不存在: " + mergeCode);
        }

        MqSliceInfo sliceInfo = new MqSliceInfo();
        sliceInfo.setIndex(index);
        sliceInfo.setMergeCode(mergeCode);
        try {
            byte[] bytes = FileUtil.fileToBytes(file);
            sliceInfo.setFile(bytes);
        } catch (Exception e) {
            e.printStackTrace();
        }
        log.info("send mq merge file : {}, index : {}", mergeCode, index);
        mqFileService.fileMergeOutput(sliceInfo);
    }

    @Transactional
    public void merge(MqSliceInfo sliceInfo) {
        byte[] file = sliceInfo.getFile();
        int index = sliceInfo.getIndex();
        String mergeCode = sliceInfo.getMergeCode();
        UploadInfo uploadInfo = fileCacheService.getUploadInfo(mergeCode);
        TFileInfo tFileInfo = fileRepo.findOne(uploadInfo.getFileId());
        String path = tFileInfo.getIndexInfo().getPath();
        Long account = tFileInfo.getUpdateBy();
        String fileName = tFileInfo.getFileName();

        String tempPath = pathConfig.getTempPath(account, path, fileName);
        Long sliceSize = uploadInfo.getSliceSize();

        Long pos = index * sliceSize;
        log.info("start merge file : {}, index : {}", tempPath, index);
        FileUtil.merge(file, pos, tempPath);

        if (!isFinish(mergeCode)) {
            return;
        }
        String filePath = pathConfig.getAbsoluteFilePath(account, path, fileName);
        FileUtil.changeName(tempPath, filePath);
        log.info("finish merge file : {}", filePath);
    }

    private synchronized boolean isFinish(String mergeCode) {
        UploadInfo uploadInfo = fileCacheService.getUploadInfo(mergeCode);
        Integer uploadCount = uploadInfo.getUploadCount();
        Integer sliceCount = uploadInfo.getSliceCount();
        uploadCount++;
        if (uploadCount.equals(sliceCount)) {
            fileCacheService.deleteUploadInfo(mergeCode);
            return true;
        }
        uploadInfo.setUploadCount(uploadCount);
        uploadInfo.setFileState(FileState.UPLOADING.getCode());
        fileCacheService.putUploadInfo(uploadInfo.getMergeCode(), uploadInfo);
        return false;
    }

//    @Scheduled(cron = "0/2 * * * * *")
    @Transactional
    public void deleteFile(String id) {
        TFileInfo tFileInfo = fileRepo.findOne(Long.parseLong(id));
        if (tFileInfo == null) {
            throw new MyException("id不存在");
        }
        fileRepo.delete(Long.parseLong(id));
        String absoluteFilePath = pathConfig.getAbsoluteFilePath(tFileInfo);
        FileUtil.deleteFile(absoluteFilePath);
    }

    @Transactional
    public List<PreviewResp> preview(String id) {
        TFileInfo tFileInfo = fileRepo.findOne(Long.parseLong(id));
        if (tFileInfo == null) {
            throw new MyException("id不存在");
        }
        String absoluteFilePath = pathConfig.getAbsoluteFilePath(tFileInfo);
        if (!tFileInfo.getType().equals("pdf")) {
            return null;
        }
        String fileMd5 = FileUtil.getFileMd5(absoluteFilePath);
        if (null == fileMd5) {
            throw new MyException("创建文件md5失败");
        }
        List<TPdfInfo> list = pdfRepo.findAllByPdfMd5OrderByPdfIndex(fileMd5);
        if (list.isEmpty()) {
            list = previewConfig.pdfPreview(absoluteFilePath, fileMd5);
            pdfRepo.save(list);
        }
        return MyBeanUtils.copyCollectionProperties(list, PreviewResp.class);
    }

    public String getDownloadPath(String id) {
        TFileInfo tFileInfo = fileRepo.findOne(Long.parseLong(id));
        if (tFileInfo == null) {
            throw new MyException("id不存在");
        }
        Long account = tFileInfo.getUpdateBy();
        String fileName = tFileInfo.getFileName();
        String path = tFileInfo.getIndexInfo().getPath();
        String relativePath = pathConfig.getRelativePath(path);
//        TFileInfo tFileInfo = fileRepo.findFirstByUpdateByAndPathAndFileName(account, relativePath, fileName);
        return "/cloud_storage/user_file/" + account + relativePath + fileName;
    }

    @Transactional
    public void rename(String id, String newName) {
        TFileInfo tFileInfo = fileRepo.findOne(Long.parseLong(id));
        newName = newName + "." + tFileInfo.getType();
        if (existFileName(tFileInfo.getIndexInfo().getId(), newName)) {
            throw new MyException("新文件名已存在");
        }
        String oldFilePath = pathConfig.getAbsoluteFilePath(tFileInfo.getUpdateBy(), tFileInfo.getIndexInfo().getPath(), tFileInfo.getFileName());
        String newFilePath = pathConfig.getAbsoluteFilePath(tFileInfo.getUpdateBy(), tFileInfo.getIndexInfo().getPath(), newName);
        FileUtil.changeName(oldFilePath, newFilePath);
        tFileInfo.setFileName(newName);
        fileRepo.save(tFileInfo);
    }

    private boolean existFileName(Long indexId, String fileName) {
        List<TFileInfo> list = fileRepo.findAllByIndexInfoId(indexId);
        for (TFileInfo tFileInfo : list) {
            if (tFileInfo.getFileName().equals(fileName)) {
                return true;
            }
        }
        return false;
    }
}
