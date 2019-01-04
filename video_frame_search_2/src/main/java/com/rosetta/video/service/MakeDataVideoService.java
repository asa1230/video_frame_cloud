package com.rosetta.video.service;

import com.rosetta.video.detector.DhashDetector;
import com.rosetta.video.entity.Hash;
import com.rosetta.video.entity.HashCacheMap;
import com.rosetta.video.entity.Video;
import com.rosetta.video.entity.VideoCacheMap;
import com.rosetta.video.util.MD5Utils;
import com.rosetta.video.util.VideoCaptureUtils;
import org.apache.commons.lang3.StringUtils;
import org.opencv.core.Mat;
import org.opencv.highgui.VideoCapture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class MakeDataVideoService {
    private static final Logger log = LoggerFactory.getLogger(MakeDataVideoService.class);

    @Autowired
    private DhashDetector dhashDetector;

//    @Scheduled(initialDelay=1000, fixedRate=720000000)
    public void cacheFolder(){
        File file = new File("/data/rosetta/video");
        File[] tempList = file.listFiles();
        List<String> params = new ArrayList<>();
        for (File aTempList : tempList) {
            if (aTempList.isFile()) {
                params.add(aTempList.getPath());
            }
            if (!aTempList.isDirectory()) {
            }

        }
        try {
            ExecutorService exec = Executors.newFixedThreadPool(13);
            List<Callable<Integer>> tasks = new ArrayList<>();
            Callable<Integer> task;

            for (String path : params) {
                task = () -> {
                    File file1 = new File(path);
                    if (!file1.isFile()) {
                        log.info("no path with : " + path);
                        return 0;
                    }
                    cacheVideos(path);
                    return 1;
                };
                tasks.add(task);
            }
            exec.invokeAll(tasks);
            exec.shutdown();
        } catch (Exception e) {
            log.error("detect error : " , e);
        }
    }

    public void cacheVideos(String url) {
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
            VideoCacheMap.put(videoKey,video);

            synchronized (HashCacheMap.dataMap) {
                Hash hash = (Hash) HashCacheMap.get(dhash28.toString());
                if (hash == null) {
                    hash = new Hash();
                    hash.setHashKey(dhash28.toString());
                }
                String videos = hash.getVideos();
                StringBuilder sb = new StringBuilder();
                if(StringUtils.isNotBlank(videos)) {
                    sb.append(videos);
                    sb.append(",").append(videoKey);
                    hash.setVideos(sb.toString());
                } else {
                    hash.setVideos(videoKey);
                }
                HashCacheMap.put(dhash28.toString(),hash);
            }
            log.info("video cache map size : " + VideoCacheMap.size());
            log.info("hash cache map size : " + HashCacheMap.size());
        } catch (Exception e) {
            log.error("import to hbase error : ",e);
        } finally {
            if (mat != null) {
                mat.release();
            }
        }
    }

}
