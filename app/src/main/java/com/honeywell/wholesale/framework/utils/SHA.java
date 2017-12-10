package com.honeywell.wholesale.framework.utils;

import android.text.SpannableStringBuilder;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by liuyang on 2017/6/5.
 */

public class SHA {

    private static SHA sha;
    private static String strType = "SHA-256";

    private SHA() {
    }

    public static synchronized SHA getInstance() {
        if(sha == null) {
            sha = new SHA();
        }
        return sha;
    }

    private static String DoSha(final String strText, final String strType) {
        // 返回值
        String strResult = null;

        // 是否是有效字符串
        if (strText != null && strText.length() > 0) {
            try {
                // SHA 加密开始
                // 创建加密对象 并傳入加密類型
                MessageDigest messageDigest = MessageDigest.getInstance(strType);
                // 传入要加密的字符串
                messageDigest.update(strText.getBytes());
                // 得到 byte 類型结果
                byte byteBuffer[] = messageDigest.digest();

                // 將 byte 轉換爲 string
                StringBuffer strHexString = new StringBuffer();
                // 遍歷 byte buffer
                for (int i = 0; i < byteBuffer.length; i++) {
                    String hex = Integer.toHexString(0xff & byteBuffer[i]);
                    if (hex.length() == 1) {
                        strHexString.append('0');
                    }
                    strHexString.append(hex);
                }
                // 得到返回結果
                strResult = strHexString.toString();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
        }
        return strResult;
    }
}
