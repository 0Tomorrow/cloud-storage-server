package com.tlf.cloud.storage.server.util;

import com.tlf.cloud.storage.server.entity.TPdfInfo;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class PdfUtil {

    private static final float DEFAULT_DPI=105;

    public static List<TPdfInfo> pdfToImageList(String pdfPath, String imgPath, String relativePath) {
        try{
            File fileFolder = new File(imgPath);
            if (fileFolder.exists()) {
                return null;
            } else {
                fileFolder.mkdir();
            }

            if(pdfPath==null||"".equals(pdfPath)||!pdfPath.endsWith(".pdf"))
                return null;
            //图像合并使用参数
            int[] singleImgRGB; // 保存一张图片中的RGB数据
            BufferedImage imageResult = null;//保存每张图片的像素值
            //利用PdfBox生成图像
            PDDocument pdDocument = PDDocument.load(new File(pdfPath));
            PDFRenderer renderer = new PDFRenderer(pdDocument);
            List<TPdfInfo> imgList = new ArrayList<>();
            //循环每个页码
            for(int i=0,len=pdDocument.getNumberOfPages(); i<len; i++){
                File outFile = new File(imgPath + i + ".jpg");
                BufferedImage image = renderer.renderImageWithDPI(i, DEFAULT_DPI, ImageType.RGB);
                int imageHeight=image.getHeight();
                int imageWidth=image.getWidth();
                imageResult= new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_RGB);
                singleImgRGB= image.getRGB(0, 0, imageWidth, imageHeight, null, 0, imageWidth);
                imageResult.setRGB(0, 0, imageWidth, imageHeight, singleImgRGB, 0, imageWidth); // 写入流中
                ImageIO.write(imageResult, "jpg", outFile);// 写图片

                TPdfInfo pdfInfo = new TPdfInfo();
                pdfInfo.setPdfIndex(i);
                pdfInfo.setPdfWidth(imageWidth);
                pdfInfo.setPdfHeight(imageHeight);
                pdfInfo.setPdfPath(relativePath + i + ".jpg");
                imgList.add(pdfInfo);
            }
            pdDocument.close();
            return imgList;

        }catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getFileMd5(byte[] bytes) {
        return DigestUtils.md5Hex(bytes);
    }
}
