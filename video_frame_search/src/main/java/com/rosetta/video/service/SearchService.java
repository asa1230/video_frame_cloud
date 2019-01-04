package com.rosetta.video.service;

import com.alibaba.fastjson.JSONArray;
import com.rosetta.video.constants.VideoSearchConstant;
import com.rosetta.video.dao.HashTemplate;
import com.rosetta.video.dao.VideoTemplate;
import com.rosetta.video.detector.DhashDetector;
import com.rosetta.video.detector.DistanceOps;
import com.rosetta.video.detector.HammingSprout;
import com.rosetta.video.entity.Hash;
import com.rosetta.video.entity.HashCacheMap;
import com.rosetta.video.entity.Video;
import com.rosetta.video.entity.VideoCacheMap;
import com.rosetta.video.util.CommonUtils;
import com.rosetta.video.util.MD5Utils;
import com.rosetta.video.util.VideoCaptureUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.opencv.core.Mat;
import org.opencv.highgui.VideoCapture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * 搜索
 */
@Service
public class SearchService {

    private static final Logger log = LoggerFactory.getLogger(SearchService.class);

    @Autowired
    private DhashDetector dhashDetector;
    @Autowired
    private HammingSprout hammingSprout;
    @Autowired
    private DistanceOps distanceOps;
    
    @Autowired
    private VideoTemplate videoTemplate;
    @Autowired
    private HashTemplate hashTemplate;

    /**
     * Hbase 中搜索
     * @param url
     * @return
     */
    public String searchByHbase(String url) {
        String result = "";
        Mat mat = null;
        List<SortVideo> resultList = null;
        try {
            resultList = new LinkedList<>();
            VideoCapture capture = VideoCaptureUtils.getVideoCapture(url);
            String videoKey = MD5Utils.md5(url);
            Video video = videoTemplate.get(videoKey);
            String src = video.getSrc();
            if (StringUtils.isNotBlank(src)) {
                SortVideo sortVideo = new SortVideo();
                sortVideo.setSrc(src);
                sortVideo.setDistance(0);
                resultList.add(sortVideo);
            }
            mat = VideoCaptureUtils.getKeyMat(capture);
            if (mat == null) {
                log.warn("video get key mat error : " + url);
                return result;
            }

            Integer dhash28 = dhashDetector.getDhashGRAY28(mat);
            int[] dhash128Int = dhashDetector.getDhashGRAY128(mat);

            // 获取距离3以内的所有索引
            List<Integer> threeLayers = hammingSprout.getThreeLayers(dhash28, 28);
            List<String> rowKeyVideos = videosByIndexList(threeLayers);
            rowKeyVideos = rowKeyVideos.stream().distinct().collect(Collectors.toList());
            log.info("videos key by index list size : " + rowKeyVideos.size());
            List<Video> videoList = videoListByRowkeys(rowKeyVideos);
            List<SortVideo> sortVideos = dhash128Filter(videoList, dhash128Int);
            log.info("dhash filtered video count : " + sortVideos.size());
            if (CollectionUtils.isNotEmpty(sortVideos)) {
                resultList.addAll(sortVideos);
            }
        } catch (Exception e) {
            log.error("search by hbase error : " , e);
        } finally {
            mat.release();
        }
        if (resultList != null) {
            List<SortVideo> collect = resultList.stream().filter(distinctByKey(SortVideo::getSrc)).sorted(Comparator.comparing(SortVideo::getDistance)).limit(5).collect(Collectors.toList());
            result = JSONArray.toJSONString(collect);
        }
        return result;
    }

    /**
     * 从缓存中搜索
     * @param url
     * @return
     */
    public String searchByCache(String url) {
        String result = "";
        Mat mat = null;
        List<SortVideo> resultList = null;
        try {
            resultList = new LinkedList<>();
            VideoCapture capture = VideoCaptureUtils.getVideoCapture(url);

            String videoKey = MD5Utils.md5(url);

            mat = VideoCaptureUtils.getKeyMat(capture);
            if (mat == null) {
                log.warn("video get key mat error : " + url);
                return result;
            }
            Integer dhash28 = dhashDetector.getDhashGRAY28(mat);
            int[] dhash128Int = dhashDetector.getDhashGRAY128(mat);

            // 获取距离3以内的所有索引
            List<Integer> threeLayers = hammingSprout.getThreeLayers(dhash28, 28);

            List<String> firstResults = new LinkedList<>();
            for (Integer index :
                    threeLayers) {
                if (HashCacheMap.constainsKey(index.toString())) {
                    Hash hash = (Hash) HashCacheMap.get(index.toString());
                    if (hash != null) {
                        String videos = hash.getVideos();
                        if (StringUtils.isNotBlank(videos)){
                            String[] split = videos.split(",");
                            for (String key :
                                    split) {
                                firstResults.add(key);
                            }
                        }
                    }
                }
            }
            log.info("first videos size : " + firstResults.size());

            for (String key :
                    firstResults) {
                Video video = (Video) VideoCacheMap.get(key);

                String dhash128 = video.getDhash128();
                String[] split = dhash128.split(",");
                int[] nowDhash = Arrays.stream(split).map(item -> Integer.parseInt(item)).mapToInt(Integer::intValue).toArray();
                int distance = distanceOps.calcHammingDistance(dhash128Int, nowDhash);
                if (distance <= VideoSearchConstant.SecondCheckHammingDistanceRange) {
                    SortVideo sortVideo = new SortVideo();
                    sortVideo.setDistance(distance);
                    sortVideo.setSrc(video.getSrc());
                    resultList.add(sortVideo);
                }
            }

        } catch (Exception e) {
            log.error("search error : " , e);
        } finally {
            mat.release();
        }
        if (resultList!= null) {
            List<SortVideo> collect = resultList.stream().sorted(Comparator.comparing(SortVideo::getDistance)).limit(5).collect(Collectors.toList());
            result = JSONArray.toJSONString(collect);
        }
        return result;
    }

    /**
     * dhash128 过滤视频
     * @param videos
     * @param dhash128Int
     * @return
     */
    public List<SortVideo> dhash128Filter(List<Video> videos,int[] dhash128Int) {
        List<SortVideo> res = new LinkedList<>();
        long a = System.currentTimeMillis();
        try {
            ExecutorService exec = Executors.newFixedThreadPool(13);
            List<Callable<Integer>> tasks = new ArrayList<>();
            Callable<Integer> task;
            for (Video video :
                    videos) {
                task = () -> {
                    String dhash128 = video.getDhash128();
                    String[] split = dhash128.split(",");
                    int[] nowDhash = Arrays.stream(split).map(item -> Integer.parseInt(item)).mapToInt(Integer::intValue).toArray();
                    int distance = distanceOps.calcHammingDistance(dhash128Int, nowDhash);
                    if (distance <= VideoSearchConstant.SecondCheckHammingDistanceRange) {
                        SortVideo sortVideo = new SortVideo();
                        sortVideo.setDistance(distance);
                        sortVideo.setSrc(video.getSrc());
                        res.add(sortVideo);
                    }
                    return 1;
                };
                tasks.add(task);
            }
            exec.invokeAll(tasks);
            exec.shutdown();
        } catch (Exception e) {
            log.error("dhash128Filter error : " , e);
        }
        long b = System.currentTimeMillis();
        log.info("video dhash128 filter cost : " + (b - a) + " ms");
        return res;
    }

    /**
     * rowkeys 对应 video集合
     * @param rowKeys
     * @return
     */
    public List<Video> videoListByRowkeys(List<String> rowKeys) {
        List<Video> res = new LinkedList<>();
        long a = System.currentTimeMillis();
        try {
            List<List<String>> split = CommonUtils.split(rowKeys, 200);
            for (List<String> each :
                    split) {
                List<Video> videos = videoTemplate.get(each);
                res.addAll(videos);
            }
        } catch (Exception e) {
            log.error("videoListByRowkeys error : " , e);
        }
        long b = System.currentTimeMillis();
        log.info("rowKeys to video list cost : " + (b - a) + " ms");
        return res;
    }

    /**
     * 根据索引集合，获取初始video结果集
     * 
     * @param indexs
     * @return
     */
    public List<String> videosByIndexList(List<Integer> indexs) {
        List<String> res = new LinkedList<>();
        long a = System.currentTimeMillis();
        try {
            ExecutorService exec = Executors.newFixedThreadPool(13);
            List<Callable<Integer>> tasks = new ArrayList<>();
            Callable<Integer> task;

            List<String> collect = indexs.stream().map(item -> item.toString()).collect(Collectors.toList());
            List<List<String>> split = CommonUtils.split(collect, 200);
            for (List<String> each :
                    split) {
                task = () -> {
                    List<Hash> hashes = hashTemplate.get(each);
                    for (Hash hash :
                            hashes) {
                        String videos = hash.getVideos();
                        if (StringUtils.isNotBlank(videos)) {
                            String[] strSplit = videos.split(",");
                            for (String str :
                                    strSplit) {
                                res.add(str);
                            }
                        }
                    }
                    return 1;
                };
                tasks.add(task);
            }
            exec.invokeAll(tasks);
            exec.shutdown();
        } catch (Exception e) {
            log.error("index list to first video list error : " , e);
        }
        long b = System.currentTimeMillis();
        log.info("indexs to videos costs : " + (b - a ) + " ms");
        return res;
    }

    /**
     * 过滤器 配合Stream filter 去除重复属性
     * @param keyExtractor
     * @param <T>
     * @return
     */
    public static <T> Predicate<T> distinctByKey(Function<? super T, Object> keyExtractor) {
        Map<Object, Boolean> seen = new ConcurrentHashMap<>();
        return object -> seen.putIfAbsent(keyExtractor.apply(object), Boolean.TRUE) == null;
    }

    class SortVideo {
        private String src;
        private Integer distance;

        public String getSrc() {
            return src;
        }

        public void setSrc(String src) {
            this.src = src;
        }

        public Integer getDistance() {
            return distance;
        }

        public void setDistance(Integer distance) {
            this.distance = distance;
        }
    }

}
