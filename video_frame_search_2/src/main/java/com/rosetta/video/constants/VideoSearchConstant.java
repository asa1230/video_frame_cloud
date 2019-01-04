package com.rosetta.video.constants;

/**
 * @Author: nya
 * @Date: 18-8-9 上午10:33
 * @Desc: 视频搜索模块相关常量配置
 */
public class VideoSearchConstant {

    // 获取关键Mat 特征集合 fps
    public static final Double KeyMatFrameFps = 10.0;
    // 获取关键Mat 特征集合 timeSpan
    public static final Double KeyMatFrameTimeSpan = 0.5;

    // 帧获取特征 采取的默认位数
    public static final Integer FeatureDhashSize = 28 ;
    // hbase 查库获取 Hash列表,传参集合长度
    public static final Integer HashQueryListSize = 200 ;

    // 设置CPU最大线程数
    public static final Integer ServerCPUMaxThreadSize = 12 ;

    // 二次搜索汉明距离长度
    public static final Integer SecondCheckHammingDistanceRange = 10 ;

    // 第一次筛选阈值
    public static final Double FirstVerifyScoreSize = 0.75;

    // 第二次筛选碰撞阈值
    public static final Double SecondVerifyStrikePercent = 0.35;

    // redis 存入队列
    public static final String redisMqForSearchKey = "videoinfo_appadd";

    // 视频获取关键帧,熵值最小度量
    public static final Double MinEntropyForKeyFrame = 1.0;

    // 无用校验帧数,占总帧数个数
    public static final Double UnUsefulFramePercent = 0.65;

    // add by nya
    public static final Double FpsCapture = 100.0; // 取100帧
    public static final Double TimeStartCapture = 1.0; // 起始时间
    public static final Double TimeSpanCaputure = 1.0; // 时间间隔

}