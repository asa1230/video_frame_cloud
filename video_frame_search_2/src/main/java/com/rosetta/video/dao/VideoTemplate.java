package com.rosetta.video.dao;

import com.rosetta.video.entity.Video;
import com.rosetta.video.exception.DaoException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class VideoTemplate {

    public static final String TABLE_NAME = "video_frame_video";
    public static final String COLUMN_FAMILY = "d";

    @Autowired
    private HbaseTableOps hbaseTableOps;

    public void put(Video video) throws DaoException {
        try {
            String rowKey = video.getKey();
            if (rowKey == null || rowKey.length() == 0) {
                throw new DaoException(
                        DaoException.TABLE_VIDEO_OPS_FAIL,
                        "fail to operate video table: " +
                                "rowkey == null || rowkey.length() == 0");
            }

            Map<String, String> columnMap = new HashMap<>();
            if (video.getSrc() != null) {
                columnMap.put("src", video.getSrc());
            }
            if (video.getDhash28() != null) {
                columnMap.put("dhash28", video.getDhash28().toString());
            }
            if (video.getDhash128() != null) {
                columnMap.put("dhash128", video.getDhash128());
            }
            hbaseTableOps.put(TABLE_NAME, rowKey, COLUMN_FAMILY, columnMap);

        } catch (Exception e) {
            throw new DaoException(
                    DaoException.TABLE_VIDEO_OPS_FAIL,
                    "fail to operate video table",
                    e);
        }
    }


    public Video get(String rowKey) throws DaoException {
        try {
            if (rowKey == null || rowKey.length() == 0) {
                throw new DaoException(
                        DaoException.TABLE_VIDEO_OPS_FAIL,
                        "fail to operate video table: " +
                                "rowkey == null || rowkey.length() == 0");
            }
            Map<String, String> res = hbaseTableOps.get(TABLE_NAME,
                    rowKey, COLUMN_FAMILY);
            Video video = new Video();
            video.setKey(rowKey);
            String src = res.get("src");
            String dhash28 = res.get("dhash28");
            String dhash128 = res.get("dhash128");
            if (StringUtils.isNotBlank(src)) {
                video.setSrc(src);
            }
            if (StringUtils.isNotBlank(dhash28)) {
                video.setDhash28(Integer.parseInt(dhash28));
            }
            if (StringUtils.isNotBlank(dhash128)) {
                video.setDhash128(dhash128);
            }

            return video;

        } catch (Exception e) {
            throw new DaoException(
                    DaoException.TABLE_VIDEO_OPS_FAIL,
                    "fail to operate video table",
                    e);
        }
    }

    /**
     *
     * @param keyList
     * @return
     * @throws DaoException
     */
    public List<Video> get(List<String> keyList) throws DaoException {
        try {
            if (keyList == null || keyList.size() == 0) {
                throw new DaoException(
                        DaoException.TABLE_HASH_OPS_FAIL,
                        "fail to operate video table: " +
                                "keyList == null || keyList.size() == 0");
            }

            List<Map<String, String>> resList = hbaseTableOps.get(TABLE_NAME,
                    keyList, COLUMN_FAMILY);
            List<Video> videoList = new ArrayList<>();
            for (Map<String, String> res : resList) {
                Video video = new Video();
                String src = res.get("src");
                String dhash28 = res.get("dhash28");
                String dhash128 = res.get("dhash128");
                if (StringUtils.isNotBlank(src)) {
                    video.setSrc(src);
                }
                if (StringUtils.isNotBlank(dhash28)) {
                    video.setDhash28(Integer.parseInt(dhash28));
                }
                if (StringUtils.isNotBlank(dhash128)) {
                    video.setDhash128(dhash128);
                }
                videoList.add(video);
            }
            return videoList;

        } catch (Exception e) {
            throw new DaoException(
                    DaoException.TABLE_HASH_OPS_FAIL,
                    "fail to operate video table",
                    e);
        }
    }


}
