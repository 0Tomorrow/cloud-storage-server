package com.tlf.cloud.storage.server.file;

import com.tlf.commonlang.exception.MyException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.ByteBuffer;

@Slf4j
public class FileUtil {
    public static byte[] fileToBytes(MultipartFile file) throws Exception {
        Long byteSize = file.getSize();
        InputStream fip = file.getInputStream();
        byte[] data = new byte[1024];
        ByteBuffer buffer = ByteBuffer.allocate(Integer.parseInt(byteSize + ""));
        int fileSize = 0;
        int len;
        while ((len = fip.read(data)) != -1) {
            fileSize += len;
            buffer.put(data,0,len);
        }
        fip.close();
        ByteBuffer fileBuffer = ByteBuffer.allocate(fileSize);
        data = fileBuffer.put(buffer.array(), 0, fileSize).array();
        return data;
    }

    public static void merge(byte[] file, Long pos, String path) {
        File tempFile = new File(path);
        try (RandomAccessFile raf = new RandomAccessFile(tempFile, "rw")) {
            raf.seek(pos);
            raf.write(file);
            raf.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void changeName(String tempPath, String filePath) {
        File tempFile = new File(tempPath);
        File file = new File(filePath);
        if (!tempFile.renameTo(file)) {
            throw new MyException("文件修改名称时异常: old : " + tempPath + ", new : " + filePath);
        }
    }

    public static void deleteFile(String filePath) {
        File file = new File(filePath);
        if (!file.exists() || !file.delete()) {
            throw new MyException("删除文件时异常: " + filePath);
        }
    }

    public static void createFile(byte[] file, String path) {
        File newFile = new File(path);
        if (newFile.exists()) {
            throw new MyException("文件已存在");
        }
        try {
            newFile.createNewFile();
            FileOutputStream fos = new FileOutputStream(newFile);
            fos.write(file);
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getFileMd5(String path) {
        try {
            FileInputStream fis = new FileInputStream(path);
            String md5 = DigestUtils.md5Hex(fis);
            fis.close();
            return md5;
        } catch (Exception e) {
            return null;
        }
    }
}
