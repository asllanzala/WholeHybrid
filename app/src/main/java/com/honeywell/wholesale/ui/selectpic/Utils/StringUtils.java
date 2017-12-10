package com.honeywell.wholesale.ui.selectpic.Utils;

/**
 * Created by shushunsakai on 16/6/15.
 */
public class StringUtils {

    public static String getLastPathSegment(String content) {

        if (content == null || content.length() == 0) {
            return "";

        }
        String[] segments = content.split("/");
        if (segments.length > 0) {
            return segments[segments.length - 1];
        }
        return "";
    }
}
