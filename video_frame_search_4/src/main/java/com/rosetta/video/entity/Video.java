package com.rosetta.video.entity;

/**
 * 视频类
 */
public class Video {

    private String key;     // rowKey
    private String src;     // url
    private Integer dhash28;// 28维dhash
    private String dhash128; // 128维dhash
    private Integer distance; // 距离

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getSrc() {
        return src;
    }

    public void setSrc(String src) {
        this.src = src;
    }

    public Integer getDhash28() {
        return dhash28;
    }

    public void setDhash28(Integer dhash28) {
        this.dhash28 = dhash28;
    }

    public String getDhash128() {
        return dhash128;
    }

    public void setDhash128(String dhash128) {
        this.dhash128 = dhash128;
    }

    public Integer getDistance() {
        return distance;
    }

    public void setDistance(Integer distance) {
        this.distance = distance;
    }
}
