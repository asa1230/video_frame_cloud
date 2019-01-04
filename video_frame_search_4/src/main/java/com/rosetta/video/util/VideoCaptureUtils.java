package com.rosetta.video.util;

import com.rosetta.video.constants.VideoSearchConstant;
import com.rosetta.video.entity.KeyFrame;
import com.rosetta.video.exception.DetectException;
import com.rosetta.video.exception.VideoException;
import org.bytedeco.javacpp.opencv_highgui;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfFloat;
import org.opencv.core.MatOfInt;
import org.opencv.highgui.Highgui;
import org.opencv.highgui.VideoCapture;
import org.opencv.imgproc.Imgproc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class VideoCaptureUtils {

    private static final Logger log = LoggerFactory.getLogger(VideoCaptureUtils.class);
    private static final Double EPSILON = 0.00001; //Double运算误差的修正值

    /**
     * calc entropy of float[]
     * @param histHSV
     * @return
     */
    private static Double calcImageEntropy(float[] histHSV) throws DetectException {
        Double entropy = 0.0;
        for (float h : histHSV) {
            if (h < 0) throw new DetectException(DetectException.CALC_ENTROPY_FAIL,
                    "the f in float[] can not less than 0");
            h = (float) Math.max(h, EPSILON);
            entropy += (-1.0) * h * Math.log10(h);
        }
        return entropy;
    }

    /**
     * get video capture
     * @param videoAddress
     * @return
     * @throws VideoException
     */
    public static VideoCapture getVideoCapture(String videoAddress) throws VideoException {

        if (videoAddress == null || videoAddress.length() == 0) {
            throw new VideoException(
                    VideoException.VIDEO_ADDRESS_ILLEGAL,
                    "video address is not legal");
        }
        VideoCapture capture = new VideoCapture(videoAddress);
        checkVideoCapture(capture);
        return capture;
    }

    /**
     * check video capture is valid
     * @param capture
     * @throws VideoException
     */
    private static void checkVideoCapture(VideoCapture capture) throws VideoException {
        if ( ! capture.isOpened()){ //判断视频对象是否合法
            throw new VideoException(
                    VideoException.VIDEO_ADDRESS_ILLEGAL,
                    "video address is not legal");
        }
    }

    /**
     * get time count of video
     * @param capture
     * @return
     */
    public static Double getTimeCount(VideoCapture capture) {
        Double frameCount = capture.get(opencv_highgui.CV_CAP_PROP_FRAME_COUNT); //总帧数
        Double fps = capture.get(opencv_highgui.CV_CAP_PROP_FPS); //帧率
        return frameCount / fps; //时间长度
    }

//    public static Double getTimeCount(VideoCapture capture) {
//        Double frameCount = capture.get(opencv_highgui.CV_CAP); //总帧数
//        Double fps = capture.get(opencv_highgui.CV_CAP_PROP_FPS); //帧率
//        return frameCount / fps; //时间长度
//    }

    /**
     * get frame count of video
     * @param capture
     * @return
     */
    public static Integer getFrameCount(VideoCapture capture) {
        Double frameCount = capture.get(opencv_highgui.CV_CAP_PROP_FRAME_COUNT); //总帧数
        return frameCount.intValue();
    }

    /**
     * get fps of video
     * @param capture
     * @return
     */
    public static Double getFps(VideoCapture capture) {
        return capture.get(opencv_highgui.CV_CAP_PROP_FPS); //帧率
    }

    /**
     * set capture to current time
     * @param capture
     * @param currentTime, unit s
     */
    public static void setCurrentTime(VideoCapture capture,
                                      Double currentTime) {
        //设置视频的位置(单位:毫秒)
        capture.set(opencv_highgui.CV_CAP_PROP_POS_MSEC, currentTime * 1000);
    }

    /**
     *
     * @param capture 视频对象
     * @param fps 取值区间为(0.0, 150.0]
     * @param timeStart 单位秒
     * @param timeSpan 单位秒
     * @return
     */
    public static List<Mat> extractFrameByFpsAndTimeSpan(VideoCapture capture,
                                                         Double fps,
                                                         Double timeStart,
                                                         Double timeSpan) throws VideoException{

        if (fps <= 0.0 || fps > 150.0) {
            throw new VideoException(
                    VideoException.FPS_OUT_OF_BOUNCE,
                    "fps out of bounce: fps <= 0.0 || fps > 150.0");
        }
        Double rawFps = VideoCaptureUtils.getFps(capture);
        Double validFps = Math.min(fps, rawFps);
        Double validTimeGap = 1.0 / validFps;

        Double validTimeStart = Math.max(0.0, timeStart);
        Double timeEnd = timeStart + timeSpan;
        Double timeCount = VideoCaptureUtils.getTimeCount(capture);
        Double validTimeEnd = Math.min(timeEnd, timeCount);
        Double validTimeSpan = validTimeEnd - validTimeStart;
        if (validTimeSpan <= 0.0) {
            throw new VideoException(
                    VideoException.TIME_OUT_OF_BOUNCE,
                    "time span out of bounce: validTimeSpan <= 0.0");
        }
        try {
            List<Mat> frameList = new ArrayList<>();
            Double currentTime = validTimeStart;
            while(currentTime + EPSILON < validTimeEnd) {
                VideoCaptureUtils.setCurrentTime(capture, currentTime);
                Mat frame = new Mat();
                capture.read(frame);
                frameList.add(frame);
                currentTime += validTimeGap;
            }
            return frameList;
        } catch (Exception e) {
            e.printStackTrace();
            throw new VideoException(
                    VideoException.READ_FRAME_FAIL,
                    "capture fail to read frame",
                    e);
        } finally {
            capture.release();
        }

    }

    public static void main(String[] args) {
        try {
            System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
            long a = System.currentTimeMillis();
            VideoCapture capture = VideoCaptureUtils.getVideoCapture("/home/lab/tt.mp4");
            VideoCaptureUtils util = new VideoCaptureUtils();
            Mat keyMat = util.getKeyMat(capture);
            long b = System.currentTimeMillis();
            System.out.println("cost:" + (b-a));
            Highgui.imwrite("/home/lab/test1.jpg",keyMat);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Mat getKeyMat(VideoCapture capture) {

        Mat res = null;
        try {
            List<Mat> mats = extractFrameByFpsAndTimeSpan(capture, VideoSearchConstant.FpsCapture, VideoSearchConstant.TimeStartCapture, VideoSearchConstant.TimeSpanCaputure);
            res = findKeyFrame(mats);
        } catch (Exception e) {
            log.error("get key mat error : ",e);
        }
        return res;
    }

    /**
     * 取出色彩丰富度最大的帧
     * @param matList
     * @return
     */
    private static Mat findKeyFrame(List<Mat> matList) throws DetectException {
        List<KeyFrame> keyFrameList = new ArrayList<>();
        for(Mat mat : matList) {
            KeyFrame keyFrame = new KeyFrame();
            float[] histHSV;
            try {
                histHSV = calcHistHSV(mat);
            } catch (DetectException e) {
                log.error("findKeyFrame error for calcHistHSV : " , e);
                continue;
            }
            keyFrame.setMat(mat);
            keyFrame.setHistHSV(histHSV);
            keyFrameList.add(keyFrame);
        }
        int maxIdx = 0;
        Double maxEntropy = 0.0;
        for (int i=0; i<keyFrameList.size(); i++) {
            Double entropy = calcImageEntropy(keyFrameList.get(i).getHistHSV());
            if (entropy > maxEntropy) {
                maxIdx = i;
                maxEntropy = entropy;
            }
        }
        for (int i = 0 ; i < keyFrameList.size() ; i++) {
            if (i != maxIdx) {
                if (keyFrameList.get(i).getMat() != null) {
                    keyFrameList.get(i).getMat().release();
                }
            }
        }
        if (maxEntropy < VideoSearchConstant.MinEntropyForKeyFrame) {
            return null;
        } else {
            return keyFrameList.get(maxIdx).getMat();
        }

    }

    /**
     *  extract Histgram of HSV from Mat
     * @param frame
     * @return
     */
    private static float[] calcHistHSV(Mat frame) throws DetectException {

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

}
