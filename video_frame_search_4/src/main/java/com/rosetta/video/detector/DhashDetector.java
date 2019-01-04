package com.rosetta.video.detector;

import com.rosetta.video.exception.DetectException;
import com.rosetta.video.exception.VideoException;
import com.rosetta.video.util.ImgUtils;
import org.bytedeco.javacpp.opencv_highgui;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.highgui.VideoCapture;
import org.opencv.imgproc.Imgproc;
import org.springframework.stereotype.Service;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author: czj
 * @Date: 18-6-11 下午3:31
 */
@Service
public class DhashDetector {

    /*******************************************************************************************************************
     * for opencv
     */

    /**
     *
     * @param img
     * @return
     * @throws DetectException
     */
    public Integer getDhashGRAY32(Mat img) throws DetectException {
        if (img.cols()<3 || img.rows()<3) {
            throw new DetectException(
                    DetectException.DETECT_DHASH_FAIL,
                    "fail to detect dhash: img.cols()<3 || img.rows()<3");
        }
        Mat grayImg = ImgUtils.bgr2gray(img);
        Mat resizedImg = new Mat();
        Size size = new Size(5, 5);
        Imgproc.resize(grayImg, resizedImg, size);
        byte[] bytePixels = new byte[5 * 5];
        resizedImg.get(0, 0, bytePixels);
        int[] pixels = new int[bytePixels.length];
        for (int i=0; i<pixels.length; i++) {
            pixels[i] = bytePixels[i] & 0xff;
        }
        int feature = 0;
        for (int j=0; j<4; j++) {
            for (int i=0; i<4; i++) {
                int colBit = pixels[i*5+j] > pixels[(i+1)*5+j] ? 1 : 0;
                feature = (feature << 1) + colBit;
                int rowBit = pixels[i*5+j] > pixels[i*5+j+1] ? 1 : 0 ;
                feature = (feature << 1) + rowBit;
            }
        }
        // release Mat
        try {
            grayImg.release();
            resizedImg.release();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return feature;
    }

    /**
     *
     * @param img
     * @return
     * @throws DetectException
     */
    public Integer getDhashGRAY28(Mat img) throws DetectException {
        if (img.cols()<3 || img.rows()<3) {
            throw new DetectException(
                    DetectException.DETECT_DHASH_FAIL,
                    "fail to detect dhash: img.cols()<3 || img.rows()<3");
        }
        Mat grayImg = ImgUtils.bgr2gray(img);
        Mat resizedImg = new Mat();
        Size size = new Size(5, 5);
        Imgproc.resize(grayImg, resizedImg, size);
        byte[] bytePixels = new byte[5 * 5];
        resizedImg.get(0, 0, bytePixels);
        int[] pixels = new int[bytePixels.length];
        for (int i=0; i<pixels.length; i++) {
            pixels[i] = bytePixels[i] & 0xff;
        }
        int feature = 0;
        for (int j=0; j<4; j++) {
            for (int i=0; i<4; i++) {
                if (i==0 && j==0) continue;
                if (i==3 && j==0) continue;
                // left to right
                int colBit = pixels[i*5+j] > pixels[(i+1)*5+j] ? 1 : 0;
                feature = (feature << 1) + colBit;
            }
        }
        for (int j=0; j<4; j++) {
            for (int i=0; i<4; i++) {
                if (i==0 && j==0) continue;
                if (i==0 && j==3) continue;
                // top to bottom
                int rowBit = pixels[i*5+j] > pixels[i*5+j+1] ? 1 : 0 ;
                feature = (feature << 1) + rowBit;
            }
        }
        // release Mat
        try {
            grayImg.release();
            resizedImg.release();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return feature;
    }

    /**
     *
     * @param img
     * @return
     * @throws DetectException
     */
    public Integer getDhashGRAY16(Mat img) throws DetectException {
        if (img.cols()<3 || img.rows()<3) {
            throw new DetectException(
                    DetectException.DETECT_DHASH_FAIL,
                    "fail to detect dhash: img.cols()<3 || img.rows()<3");
        }
        Mat grayImg = ImgUtils.bgr2gray(img);
        Mat resizedImg = new Mat();
        Size size = new Size(5, 4);
        Imgproc.resize(grayImg, resizedImg, size);
        byte[] bytePixels = new byte[5 * 4];
        resizedImg.get(0, 0, bytePixels);
        int[] pixels = new int[bytePixels.length];
        for (int i=0; i<pixels.length; i++) {
            pixels[i] = bytePixels[i] & 0xff;
        }
        int feature = 0;
        for (int j=0; j<4; j++) {
            for (int i=0; i<4; i++) {
                int colBit = pixels[i*4+j] > pixels[(i+1)*4+j] ? 1 : 0;
                feature = (feature << 1) + colBit;
            }
        }
        // release Mat
        try {
            grayImg.release();
            resizedImg.release();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return feature;
    }

    /**
     *
     * @param img
     * @return
     * @throws DetectException
     */
    public Integer getDhashGRAY16_s(Mat img) throws DetectException {
        if (img.cols()<3 || img.rows()<3) {
            throw new DetectException(
                    DetectException.DETECT_DHASH_FAIL,
                    "fail to detect dhash: img.cols()<3 || img.rows()<3");
        }
        Mat grayImg = ImgUtils.bgr2gray(img);
        Mat resizedImg = new Mat();
        Size size = new Size(4, 4);
        Imgproc.resize(grayImg, resizedImg, size);
        // pixels
        byte[] bytePixels = new byte[4 * 4];
        resizedImg.get(0, 0, bytePixels);
        int[] pixels = new int[4 * 4];
        for (int i=0; i<pixels.length; i++) {
            pixels[i] = bytePixels[i] & 0xff;
        }
        // bits
        int[] bits = new int[4 * 4];
        bits[0] = pixels[1] > pixels[0] ? 1 : 0;
        bits[1] = pixels[2] > pixels[1] ? 1 : 0;
        bits[2] = pixels[3] > pixels[2] ? 1 : 0;
        bits[3] = pixels[7] > pixels[3] ? 1 : 0;
        bits[4] = pixels[11] > pixels[7] ? 1 : 0;
        bits[5] = pixels[15] > pixels[11] ? 1 : 0;
        bits[6] = pixels[14] > pixels[15] ? 1 : 0;
        bits[7] = pixels[13] > pixels[14] ? 1 : 0;
        bits[8] = pixels[12] > pixels[13] ? 1 : 0;
        bits[9] = pixels[8] > pixels[12] ? 1 : 0;
        bits[10] = pixels[4] > pixels[8] ? 1 : 0;
        bits[11] = pixels[0] > pixels[4] ? 1 : 0;
        bits[12] = pixels[9] > pixels[5] ? 1 : 0;
        bits[13] = pixels[10] > pixels[9] ? 1 : 0;
        bits[14] = pixels[6] > pixels[10] ? 1 : 0;
        bits[15] = pixels[5] > pixels[6] ? 1 : 0;
        // featrue
        int feature = 0;
        for (int bit : bits) {
            feature = (feature << 1) + bit;
        }
        // release Mat
        try {
            grayImg.release();
            resizedImg.release();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return feature;
    }

    /**
     *
     * @param img
     * @return
     * @throws DetectException
     */
    public int[] getDhashGRAY128(Mat img) throws DetectException {
        if (img.cols()<3 || img.rows()<3) {
            throw new DetectException(
                    DetectException.DETECT_DHASH_FAIL,
                    "fail to detect dhash: img.cols()<3 || img.rows()<3");
        }
        Mat grayImg = ImgUtils.bgr2gray(img);
        Mat resizedImg = new Mat();
        Size size = new Size(9, 9);
        Imgproc.resize(grayImg, resizedImg, size);
        byte[] bytePixels = new byte[9 * 9];
        resizedImg.get(0, 0, bytePixels);
        int[] pixels = new int[bytePixels.length];
        for (int i=0; i<pixels.length; i++) {
            pixels[i] = bytePixels[i] & 0xff;
        }
        int[] features = new int[8 * 8 * 2 / 32];
        for (int i=0; i<features.length; i++) {
            features[i] = 0;
        }
        int idx = 0;
        for (int j=0; j<8; j++) {
            for (int i=0; i<8; i++) {
                int colBit = pixels[i*9+j] > pixels[(i+1)*9+j] ? 1 : 0;
                features[idx/32] = (features[idx/32] << 1) + colBit;
                idx ++;
                int rowBit = pixels[i*9+j] > pixels[i*9+j+1] ? 1 : 0 ;
                features[idx/32] = (features[idx/32] << 1) + rowBit;
                idx ++;
            }
        }
        // release Mat
        try {
            grayImg.release();
            resizedImg.release();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return features;
    }

    /*******************************************************************************************************************
     * for java
     */

    /**
     * 输入路径获取Dhash集合
     * @param path
     * @return
     */
    public List<Integer> videoToDhashs(String path) throws VideoException {
        VideoCapture capture = new VideoCapture(path);// 读取视频文件
        List<Integer> matList = new ArrayList<>();
        if (capture.isOpened()){// 判断视频是否打开
            double frameCount = capture.get(opencv_highgui.CV_CAP_PROP_FRAME_COUNT);//总帧数
            double fps = capture.get(opencv_highgui.CV_CAP_PROP_FPS);// 帧率
            double len = frameCount / fps ;// 时间长度
            Double d_s = new Double(len);

            for (int i = 0; i < d_s.intValue(); i++) {
                System.out.println(i+1);
                Mat frame = new Mat();
                capture.set(opencv_highgui.CV_CAP_PROP_POS_MSEC, i * 1000);//设置视频的位置(单位:毫秒)
                capture.read(frame);//读取下一帧画面

                matList.add(getDhashGRAY32(ImgUtils.matToBufferedImage(frame)));// BufferedImage --> 32Dhash 并存入 集合
                frame.release();
            }
            capture.release();// 关闭视频文件
        } else {
            throw new VideoException(
                    VideoException.TIME_OUT_OF_BOUNCE,
                    "time out of bounce");
        }
        return matList;
    }

    /**
     * extract brief dhash features by one channel from the buffered image.
     * the type of the features is Integer.
     * the size of the features is size*size*2*3/32.
     *
     * @param srcImage, buffered image to extract features
     * @return the dhash feature; Integer; the dhash feature is 16 bit.
     */
    public Integer getDhashGRAY16(BufferedImage srcImage) {

        if (srcImage.getWidth()<3 || srcImage.getHeight()<3) return -1;
        BufferedImage image = ImgUtils.deTransparency(srcImage);
        BufferedImage newImage = new BufferedImage(5, 4, BufferedImage.TYPE_BYTE_GRAY);
        newImage.getGraphics().drawImage(image, 0, 0, 5, 4, null);
        int[][] pixels = new int[5][4];
        for (int i=0; i<5; i++) {
            for (int j=0; j<4; j++) {
                pixels[i][j] = newImage.getRGB(i, j);
            }
        }
        Integer feature = 0;
        for (int i=0; i<4; i++) {
            for (int j=0; j<4; j++) {
                int colBit = pixels[i][j]>pixels[i][j+1] ? 1 : 0;
                feature = (feature << 1) + colBit;
            }
        }
        return feature;
    }

    /**
     * 获取32位Dhash
     * @param srcImage
     * @return
     */
    public Integer getDhashGRAY32(BufferedImage srcImage) {
        if (srcImage.getWidth()<3 || srcImage.getHeight()<3) return -1;
        BufferedImage image = ImgUtils.deTransparency(srcImage);
        BufferedImage newImage = new BufferedImage(5, 5, BufferedImage.TYPE_BYTE_GRAY);
        newImage.getGraphics().drawImage(image, 0, 0, 5, 5, null);
        int[][] pixels = new int[5][5];
        for (int i=0; i<5; i++) {
            for (int j=0; j<5; j++) {
                pixels[i][j] = newImage.getRGB(i, j);
            }
        }
//        for (int i=0; i<4; i++) {
//            for (int j=0; j<4; j++) {
//                System.out.print(Integer.toBinaryString(pixels[i][j]) + ", ");
//            }
//        }
//        System.out.println();
        Integer feature = 0;
        for (int i=0; i<4; i++) {
            for (int j=0; j<4; j++) {
                int colBit = pixels[i][j] > pixels[i][j+1] ? 1 : 0;
                feature = (feature << 1) + colBit;
                int rowBit = pixels[i][j] > pixels[i+1][j] ? 1 : 0 ;
                feature = (feature << 1) + rowBit;
            }
        }
        return feature;
    }

    /**
     * extract dhash features by one channel from the buffered image.
     * the type of the features is Integer[].
     * the size of the features is size*size*2*3/32.
     *
     * @param srcImage, buffered image to extract features
     * @param size, dhash size to calc
     * @return the dhash features; if the size of image is uncommon or the param size is not multiples of 8,return empty array.
     */
    public Integer[] getDhaIntegershGRAY(BufferedImage srcImage, int size) {

        if (srcImage.getWidth()<3 || srcImage.getHeight()<3) return new Integer[0];
        if (size<=0 || size%4!=0) return new Integer[0];
        BufferedImage image = ImgUtils.deTransparency(srcImage);

        int width = size + 1;
        BufferedImage newImage = new BufferedImage(width, width, BufferedImage.TYPE_BYTE_GRAY);
        newImage.getGraphics().drawImage(image, 0, 0, width, width, null);
        int[][] pixels = new int[width][width];
        for (int i=0; i<width; i++) {
            for (int j=0; j<width; j++) {
                pixels[i][j] = newImage.getRGB(i, j);
            }
        }
//        for (int i=0; i<width; i++) {
//            for (int j=0; j<width; j++) {
//                System.out.print(Integer.toBinaryString(pixels[i][j]) + ", ");
//            }
//        }
//        System.out.println();
        Integer[] features = new Integer[size*size*2/32];
        for (int i=0; i<features.length; i++) {
            features[i] = 0;
        }
        int idx = 0;
        for (int i=0; i<size; i++) {
            for (int j=0; j<size; j++) {
                int rowBit = pixels[i][j] > pixels[i][j+1] ? 1 : 0;
                features[idx/32] = (features[idx/32] << 1) + rowBit;
                idx ++;
                int colBit = pixels[i][j] > pixels[i+1][j] ? 1 : 0;
                features[idx/32] = (features[idx/32] << 1) + colBit;
                idx ++;
            }
        }
        return features;
    }

    /**
     * extract dhash features by three channels from the buffered image.
     * the type of the features is Integer[].
     * the size of the features is size*size*2*3/32.
     *
     * @param srcImage, buffered image to extract features
     * @param size, dhash size to calc
     * @return the dhash features; if the size of image is uncommon or the param size is not multiples of 8,return empty array.
     */
    public Integer[] getDhashRGB(BufferedImage srcImage, int size) {

        if (srcImage.getWidth()<3 || srcImage.getHeight()<3) return new Integer[0];
        if (size<=0 || size%8!=0) return new Integer[0];
        BufferedImage image = ImgUtils.deTransparency(srcImage);

        int width = size + 1;
        BufferedImage newImage = new BufferedImage(width, width,
                BufferedImage.TYPE_3BYTE_BGR);
        newImage.getGraphics().drawImage(image, 0, 0, width, width, null);
        int[] andOperand = {0xff0000, 0xff00, 0xff};
        int[] shiftOperand = {16, 8, 0};
        int[][][] pixels = new int[width][width][3];
        for (int i=0; i<width; i++) {
            for (int j=0; j<width; j++) {
                int pixel = newImage.getRGB(i, j);
                for (int k=0; k<3; k++) {
                    // TODO
                    pixels[i][j][k] = (pixel & andOperand[k]) >> shiftOperand[k];
                }
            }
        }
        Integer[] features = new Integer[size*size*2*3/32];
        for (int i=0; i<features.length; i++) {
            features[i] = 0;
        }
        int idx = 0;
        for (int i=0; i<size; i++) {
            for (int j=0; j<size; j++) {
                for (int k=0; k<3; k++) {
                    int rowBit = pixels[i][j][k]>pixels[i][j+1][k] ? 1 : 0;
                    features[idx/32] = (features[idx/32] << 1) + rowBit;
                    idx ++;
                    int colBit = pixels[i][j][k]>pixels[i+1][j][k] ? 1 : 0;
                    features[idx/32] = (features[idx/32] << 1) + colBit;
                    idx ++;
                }
            }
        }
        return features;
    }





}
