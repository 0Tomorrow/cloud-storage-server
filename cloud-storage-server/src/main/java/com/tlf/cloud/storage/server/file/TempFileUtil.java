package com.tlf.cloud.storage.server.file;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class TempFileUtil {
//    public static void createTempFile(String tempFilePath) {
//        File file = new File(tempFilePath);
//        try {
//            if (!file.exists()) {
//                file.createNewFile();
//            }
//        } catch (Exception e) {
//            throw new MyException("创建temp文件失败：" + e.getMessage());
//        }
//    }
    public static void createTempFile(String tempPath, long size) throws IOException {
        File file = new File(tempPath);
        FileOutputStream fos = null;
        FileChannel output = null;
        try {
            fos = new FileOutputStream(file);
            output = fos.getChannel();
            output.write(ByteBuffer.allocate(1), size-1);
        } finally {
            try {
                if (output != null) {
                    output.close();
                }
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
