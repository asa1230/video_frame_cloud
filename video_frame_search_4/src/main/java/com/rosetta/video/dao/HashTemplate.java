package com.rosetta.video.dao;

import com.rosetta.video.entity.Hash;
import com.rosetta.video.exception.DaoException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class HashTemplate {

    public static final String TABLE_NAME = "video_frame_hash";
    public static final String COLUMN_FAMILY = "d";

    @Autowired
    private HbaseTableOps hbaseTableOps;

    /**
     *
     * @param hash
     * @throws DaoException
     */
    public void put(Hash hash) throws DaoException {
        try {
            String rowKey = hash.getHashKey();
            if (rowKey == null || rowKey.length() == 0) {
                throw new DaoException(
                        DaoException.TABLE_HASH_OPS_FAIL,
                        "fail to operate hash table:" +
                                " rowkey == null || rowkey.length() == 0");
            }

            Map<String, String> columnMap = new HashMap<>();

            if (hash.getVideos() != null) {
                columnMap.put("videos",hash.getVideos());
            }
            hbaseTableOps.put(TABLE_NAME, rowKey, COLUMN_FAMILY, columnMap);

        } catch (Exception e) {
            throw new DaoException(
                    DaoException.TABLE_HASH_OPS_FAIL,
                    "fail to operate hash table",
                    e);
        }
    }

    /**
     *
     * @param rowKey
     * @return
     * @throws DaoException
     */
    public Hash get(String rowKey) throws DaoException {
        try {
            if (rowKey == null || rowKey.length() == 0) {
                throw new DaoException(
                        DaoException.TABLE_HASH_OPS_FAIL,
                        "fail to operate hash table: " +
                                "rowkey == null || rowkey.length() == 0");
            }
            Map<String, String> res = hbaseTableOps.get(TABLE_NAME,
                    rowKey, COLUMN_FAMILY);
            Hash hash = new Hash();
            hash.setHashKey(rowKey);
            String videos = res.get("videos");
            if (StringUtils.isNotBlank(videos)) {
                hash.setVideos(videos);
            }
            return hash;

        } catch (Exception e) {
            throw new DaoException(
                    DaoException.TABLE_HASH_OPS_FAIL,
                    "fail to operate hash table",
                    e);
        }
    }

    /**
     *
     * @param keyList
     * @return
     * @throws DaoException
     */
    public List<Hash> get(List<String> keyList) throws DaoException {
        try {
            if (keyList == null || keyList.size() == 0) {
                throw new DaoException(
                        DaoException.TABLE_HASH_OPS_FAIL,
                        "fail to operate hash table: " +
                                "keyList == null || keyList.size() == 0");
            }

            List<Map<String, String>> resList = hbaseTableOps.get(TABLE_NAME,
                    keyList, COLUMN_FAMILY);
            List<Hash> hashList = new ArrayList<>();
            for (Map<String, String> res : resList) {
                Hash hash = new Hash();
                if (res.get("ROWKEY") != null) {
                    hash.setHashKey(res.get("ROWKEY"));
                }
                if (res.get("videos") != null) {
                    hash.setVideos(res.get("videos"));
                }
                hashList.add(hash);
            }
            return hashList;

        } catch (Exception e) {
            throw new DaoException(
                    DaoException.TABLE_HASH_OPS_FAIL,
                    "fail to operate hash table",
                    e);
        }
    }


}
