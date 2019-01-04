package com.rosetta.video.util;

import org.apache.commons.codec.digest.DigestUtils;

public class MD5Utils {

    // key 密钥
    private static final String KEY = "video-retrieval";

    /**
     * MD5方法
     * @param text 明文
     * @return 密文
     */
    public static String md5(String text) {
        return DigestUtils.md5Hex(text + KEY);
    }

    /**
     * MD5验证方法
     * @param text 明文
     * @param md5 密文
     * @return true/false
     * @throws Exception
     */
    public static boolean verify(String text, String md5) {
        //根据传入的密钥进行验证
        String md5Text = md5(text);
        if (md5Text.equalsIgnoreCase(md5)) {
            return true;
        } else {
            return false;
        }
    }
}
