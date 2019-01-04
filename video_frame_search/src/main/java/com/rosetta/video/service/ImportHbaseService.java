package com.rosetta.video.service;

import com.rosetta.video.dao.HashTemplate;
import com.rosetta.video.dao.VideoTemplate;
import com.rosetta.video.detector.DhashDetector;
import com.rosetta.video.entity.Hash;
import com.rosetta.video.entity.Video;
import com.rosetta.video.util.MD5Utils;
import com.rosetta.video.util.VideoCaptureUtils;
import org.apache.commons.lang3.StringUtils;
import org.opencv.core.Mat;
import org.opencv.highgui.VideoCapture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
public class ImportHbaseService {

    private static final Logger log = LoggerFactory.getLogger(ImportHbaseService.class);

    @Autowired
    private DhashDetector dhashDetector;
    @Autowired
    private VideoTemplate videoTemplate;
    @Autowired
    private HashTemplate hashTemplate;

    /**
     * 视频入库，成功 1 ，失败 0
     * @param url
     * @return
     */
    public String insertToHbase(String url) {

        String result = "0";
        Mat mat = null;
        try {
            VideoCapture capture = VideoCaptureUtils.getVideoCapture(url);

            String videoKey = MD5Utils.md5(url);

            mat = VideoCaptureUtils.getKeyMat(capture);
            Integer dhash28 = dhashDetector.getDhashGRAY28(mat);
            int[] dhash128Int = dhashDetector.getDhashGRAY128(mat);

            Integer[] dhash128Boxed = Arrays.stream(dhash128Int).boxed().toArray(Integer[]::new);
            String dhash128 = StringUtils.join(dhash128Boxed, ",");

            Video video = new Video();
            video.setKey(videoKey);
            video.setSrc(url);
            video.setDhash28(dhash28);
            video.setDhash128(dhash128);
            videoTemplate.put(video);

            Hash hash = hashTemplate.get(dhash28.toString());
            String videos = hash.getVideos();
            StringBuilder sb = new StringBuilder();
            if(StringUtils.isNotBlank(videos)) {
                sb.append(videos);
                sb.append(",").append(videoKey);
                hash.setVideos(sb.toString());
            } else {
                hash.setVideos(videoKey);
            }
            hashTemplate.put(hash);
            result = "1";
        } catch (Exception e) {
            log.error("import to hbase error : ",e);
        } finally {
            if (mat != null) {
                mat.release();
            }
        }
        return result;
    }

}
