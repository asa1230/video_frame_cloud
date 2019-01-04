package com.rosetta.video.exception;

/**
 * @Author: czj
 * @Date: 18-6-11 下午5:44
 */
public class VideoException extends Exception {

    /**
     * video exception code
     */
    public static final Integer VIDEO_OPEN_FAIL = 0;
    public static final Integer READ_FRAME_FAIL = 1;
    public static final Integer FPS_OUT_OF_BOUNCE = 2;
    public static final Integer TIME_OUT_OF_BOUNCE = 3;
    public static final Integer FRAME_OUT_OF_BOUNCE = 4;
    public static final Integer VIDEO_ADDRESS_ILLEGAL = 5;
    public static final Integer TIME_FRAME_DIFFER = 6;
    public static final Integer VIDEO_IMPORT_FAIL = 7;
    public static final Integer EXTRACT_KEY_FRAME_FAIL = 8;

    private Integer errorCode;

    /**
    * 构造一个基本异常.
    * @param message 信息描述
    */
    public VideoException(String message) {
        super(message);
    }

    /**
    * 构造一个基本异常.
    * @param errorCode 错误编码
    * @param message 信息描述
    */
    public VideoException(Integer errorCode,
                          String message) {
        super(message);
        this.errorCode = errorCode;
    }

    /**
     * 构造一个基本异常.
     * @param message 信息描述
     * @param cause 根异常类（可以存入任何异常）
     */
    public VideoException(String message,
                          Throwable cause) {
        super(message, cause);
    }

    /**
    * 构造一个基本异常.
    * @param errorCode 错误编码
    * @param message 信息描述
    * @param cause 根异常类（可以存入任何异常）
    */
    public VideoException(Integer errorCode,
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
