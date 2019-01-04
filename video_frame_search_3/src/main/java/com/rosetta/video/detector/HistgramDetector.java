package com.rosetta.video.detector;

import com.rosetta.video.exception.DetectException;
import com.rosetta.video.util.ImgUtils;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfFloat;
import org.opencv.core.MatOfInt;
import org.opencv.imgproc.Imgproc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

@Service
public class HistgramDetector {

    /*******************************************************************************************************************
     * for opencv
     */

    private static final Logger log = LoggerFactory.getLogger(HistgramDetector.class);

    /**
     *  extract Histgram of HSV from Mat
     * @param frame
     * @return
     */
    public float[] calcHistHSV(Mat frame) throws DetectException {

        Mat frameHSV = ImgUtils.bgr2hsv(frame);
        if (frameHSV.channels() != 3) {
            try {
                frameHSV.release();
            } catch (Exception e) {
                e.printStackTrace();
            }
            throw new DetectException(DetectException.DETECT_HIST_FAIL,
                    "channels.size() != 3");
        }
        List<Mat> channels = new ArrayList<>();
        Core.split(frameHSV, channels);

        MatOfInt channelIndex = new MatOfInt(0);
        MatOfInt histSize = new MatOfInt(64);
        MatOfFloat histRangeH = new MatOfFloat(0, 180);
        MatOfFloat histRangeSV = new MatOfFloat(0, 256);

        Mat histH = new Mat();
        Mat histS = new Mat();
        Mat histV = new Mat();

        // calc histogram
        Imgproc.calcHist(channels.subList(0, 1),
                channelIndex,
                new Mat(),
                histH,
                histSize,
                histRangeH,
                false);
        Imgproc.calcHist(channels.subList(1, 2),
                channelIndex,
                new Mat(),
                histS,
                histSize,
                histRangeSV,
                false);
        Imgproc.calcHist(channels.subList(2, 3),
                channelIndex,
                new Mat(),
                histV,
                histSize,
                histRangeSV,
                false);

        // normalize
        Core.normalize(histH, histH, 1, 0, Core.NORM_L1, -1, new Mat());
        Core.normalize(histS, histS, 1, 0, Core.NORM_L1, -1, new Mat());
        Core.normalize(histV, histV, 1, 0, Core.NORM_L1, -1, new Mat());

        // normalize
//          int pixAll = hist.rows() * hist.cols();
//          Mat histNormed = new Mat(hist.rows(), hist.cols(), hist.type());
//          Core.divide(hist, new Scalar(pixAll), histNormed);

        // merge
        List<Mat> channelsHist = new ArrayList<>();
        channelsHist.add(histH);
        channelsHist.add(histS);
        channelsHist.add(histV);
        Mat hist = new Mat();
        Core.merge(channelsHist, hist);

        // Mat to Float[]
        int c = hist.cols();
        int r = hist.rows();
        int ch = hist.channels();
        float[] histArr = new float[hist.cols() * hist.rows() * hist.channels()];
        hist.get(0, 0, histArr);

        // release Mat
        try {
            frameHSV.release();
            for (Mat channel : channels) channel.release();
            channelIndex.release();
            histSize.release();
            histRangeH.release();
            histRangeSV.release();
            for (Mat channel : channelsHist) channel.release();
            hist.release();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return histArr;


    }

    /*******************************************************************************************************************
     * for java
     */

    /**
     * extract histogram features from the buffered image.
     * the type of the features is Float[].
     * the size of the features is 3*64.
     *
     * @param srcImage, buffered image to extract features
     * @return the histogram features; if the size of image is uncommon,return empty array.
     */
    public Float[] getHistRGB(BufferedImage srcImage) throws DetectException {
        try {
            if (srcImage.getWidth() < 3 || srcImage.getHeight() < 3) return new Float[0];
            BufferedImage image = ImgUtils.deTransparency(srcImage);
            int width = image.getWidth();
            int height = image.getHeight();
            Float[] features = new Float[3 * 64];
            for (int i = 0; i < features.length; i++) {
                features[i] = 0f;
            }

            BufferedImage newImage = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
            newImage.getGraphics().drawImage(image, 0, 0, width, height, null);
            int[] andOperand = {0xff0000, 0xff00, 0xff};
            int[] shiftOperand = {16, 8, 0};
            Float factor = 1.0f / (width * height);
            for (int i = 0; i < width; i++) {
                for (int j = 0; j < height; j++) {
                    int pixel = newImage.getRGB(i, j);
                    for (int k = 0; k < 3; k++) {
                        int value = ((pixel & andOperand[k]) >> shiftOperand[k]) / 4;
                        features[k * 64 + value] += factor;
                    }
                }
            }
            return features;
        } catch (Exception e) {
            throw new DetectException(
                    DetectException.DETECT_HIST_FAIL,
                    "fail to detect histgram",
                    e);
        }
    }


}
