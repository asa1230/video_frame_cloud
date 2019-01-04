package com.rosetta.video.exception;

/**
 * @Author: czj
 * @Date: 18-6-11 下午5:44
 */
public class DetectException extends Exception {

    /**
     * detect exception code
     */
    public static final Integer DETECT_SIFT_FAIL = 100;
    public static final Integer DETECT_DHASH_FAIL = 101;
    public static final Integer DETECT_HIST_FAIL = 102;
    public static final Integer CALC_EUCLIDEAN_DISTANCE_FAIL = 103;
    public static final Integer CALC_ENTROPY_FAIL = 104;

    private Integer errorCode;

    /**
    * 构造一个基本异常.
    * @param message 信息描述
    */
    public DetectException(String message) {
        super(message);
    }

    /**
    * 构造一个基本异常.
    * @param errorCode 错误编码
    * @param message 信息描述
    */
    public DetectException(Integer errorCode,
                           String message) {
        super(message);
        this.errorCode = errorCode;
    }

    /**
     * 构造一个基本异常.
     * @param message 信息描述
     * @param cause 根异常类（可以存入任何异常）
     */
    public DetectException(String message,
                           Throwable cause) {
        super(message, cause);
    }

    /**
    * 构造一个基本异常.
    * @param errorCode 错误编码
    * @param message 信息描述
    * @param cause 根异常类（可以存入任何异常）
    */
    public DetectException(Integer errorCode,
                           String message,
                           Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public Integer getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(Integer errorCode) {
        this.errorCode = errorCode;
    }

}
