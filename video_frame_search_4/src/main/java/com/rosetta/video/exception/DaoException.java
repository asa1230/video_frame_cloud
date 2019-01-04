package com.rosetta.video.exception;

/**
 * @Author: czj
 * @Date: 18-6-11 下午5:44
 */
public class DaoException extends Exception {

    /**
     * dao exception code
     */
    public static final Integer TABLE_VIDEO_OPS_FAIL = 200;
    public static final Integer TABLE_SHOT_OPS_FAIL = 201;
    public static final Integer TABLE_HASH_OPS_FAIL = 202;

    private Integer errorCode;

    /**
    * 构造一个基本异常.
    * @param message 信息描述
    */
    public DaoException(String message) {
        super(message);
    }

    /**
    * 构造一个基本异常.
    * @param errorCode 错误编码
    * @param message 信息描述
    */
    public DaoException(Integer errorCode,
                        String message) {
        super(message);
        this.errorCode = errorCode;
    }

    /**
     * 构造一个基本异常.
     * @param message 信息描述
     * @param cause 根异常类（可以存入任何异常）
     */
    public DaoException(String message,
                        Throwable cause) {
        super(message, cause);
    }

    /**
    * 构造一个基本异常.
    * @param errorCode 错误编码
    * @param message 信息描述
    * @param cause 根异常类（可以存入任何异常）
    */
    public DaoException(Integer errorCode,
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
