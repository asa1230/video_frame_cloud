package com.rosetta.video.util;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Class ImgUtils is a class for extracting feature from buffered image, and evaluate the features.
 *
 * @author  chenzhaojin
 * @version 1.0.4, 12/06/17
 */
public class ImgUtils {

    /*******************************************************************************************************************
     * for opencv
     */

    /**
     *
     * @param src
     * @return
     */
    public static Mat bgr2gray(Mat src) {
        try {
            Mat dst = new Mat(src.rows(), src.cols(), CvType.CV_8UC1);
            Imgproc.cvtColor(src, dst, Imgproc.COLOR_BGR2GRAY);
//            src.release();
            return dst;
        } catch (Exception e) {
            e.printStackTrace();
            return new Mat();
        }
    }

    /**
     *
     * @param src
     * @return
     */
    public static Mat bgr2hsv(Mat src) {
        try {
            Mat dst = new Mat(src.rows(), src.cols(), CvType.CV_8UC3);
            Imgproc.cvtColor(src, dst, Imgproc.COLOR_BGR2HSV);
//            src.release();
            return dst;
        } catch (Exception e) {
            e.printStackTrace();
            return new Mat();
        }
    }

    /**
     * Mat 转 BufferedImage
     * @param mat
     * @return
     */
    public static BufferedImage matToBufferedImage(Mat mat) {
        byte[] data1 = new byte[mat.rows() * mat.cols() * (int)(mat.elemSize())];
        mat.get(0,0,data1);
        BufferedImage image = new BufferedImage(mat.cols(),mat.rows(),
                BufferedImage.TYPE_BYTE_GRAY);
        image.getRaster().setDataElements(0,0,mat.cols(),mat.rows(),data1);
//        mat.release();
        return image;
    }

    /*******************************************************************************************************************
     * for java
     */

    /**
     * getVideoCapture the image file and bgr2gray it to buffered image
     * this function is just for test
     *
     * @param imgPath, path to the image file
     * @return the buffered image of the image file; if there is Exception, return null.
     */
    @Deprecated
    public static BufferedImage getBufferedImage(String imgPath){

        FileInputStream fileInputStream = null;
        BufferedImage bufferedImage = null;
        try {
            fileInputStream = new FileInputStream(imgPath);
            bufferedImage = ImageIO.read(fileInputStream);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (null != fileInputStream) {
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return bufferedImage;
    }

    /**
     * to cast the buffered image to 3 channels
     * by adding a white background and making it opaque
     *
     * @param srcImage, buffered image to make opaque
     * @return the buffered image which is opaque
     */
    public static BufferedImage deTransparency(BufferedImage srcImage) {

        if (Transparency.OPAQUE == srcImage.getTransparency()) {
            return srcImage;
        } else {
            BufferedImage dstImage = new BufferedImage(
                    srcImage.getWidth(),
                    srcImage.getHeight(),
                    BufferedImage.TYPE_3BYTE_BGR);
            dstImage.getGraphics().drawImage(
                    srcImage,
                    0,
                    0,
                    srcImage.getWidth(),
                    srcImage.getHeight(),
                    Color.white,
                    null);
            return dstImage;
        }
    }

}
