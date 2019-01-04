package com.rosetta.video.entity;

import org.opencv.core.Mat;

public class KeyFrame {

    private Mat mat;

    private Double timeStart;

    private Double timeSpan;

    private float[] histHSV;

    // 128bit dhash feature
    public static final String DHASH128 = "dhash128";
    private int[] dhash128;

    // 28bit dhash feature
    public static final String DHASH28 = "dhash28";
    private Integer dhash28;

    public Mat getMat() {
        return mat;
    }

    public void setMat(Mat mat) {
        this.mat = mat;
    }

    public Double getTimeStart() {
        return timeStart;
    }

    public void setTimeStart(Double timeStart) {
        this.timeStart = timeStart;
    }

    public Double getTimeSpan() {
        return timeSpan;
    }

    public void setTimeSpan(Double timeSpan) {
        this.timeSpan = timeSpan;
    }

    public float[] getHistHSV() {
        return histHSV;
    }

    public void setHistHSV(float[] histHSV) {
        this.histHSV = histHSV;
    }

    public int[] getDhash128() {
        return dhash128;
    }

    public void setDhash128(int[] dhash128) {
        this.dhash128 = dhash128;
    }

    public Integer getDhash28() {
        return dhash28;
    }

    public void setDhash28(Integer dhash28) {
        this.dhash28 = dhash28;
    }
}
