package com.honeywell.wholesale.framework.utils;

import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by xiaofei on 7/21/16.
 *
 */
public class RichText {

    public static final String RICHTEXT_STRING = "STRING";
    public static final String RICHTEXT_COLOR = "COLOR";
    public static final String RICHTEXT_SIZE = "SIZE";

    private static RichText richText;
    private static SpannableStringBuilder ssb =  new SpannableStringBuilder();

    private RichText() {
    }

    public static synchronized RichText getInstance() {
        if(richText == null) {
            richText = new RichText();
        }
        ssb.clear();
        return richText;
    }

    public static SpannableStringBuilder getSpannableStringFromList(List<HashMap<String,Object>> list){

        ssb.clear();

        int position = 0;
        for (int i=0;i<list.size();i++){
            HashMap<String,Object> map = list.get(i);
            try{
                String st = (String)map.get(RICHTEXT_STRING);
                ssb.append(st);
                int len = st.length();

                if (map.containsKey(RICHTEXT_COLOR)){
                    int color = ((Integer)map.get(RICHTEXT_COLOR)).intValue();
                    ssb.setSpan(new ForegroundColorSpan(color), position, position+len, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                }

                if (map.containsKey(RICHTEXT_SIZE)){
                    int size = ((Integer)map.get(RICHTEXT_SIZE)).intValue();
                    ssb.setSpan(new AbsoluteSizeSpan(size), position, position+len, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }

                position = position+len;

            } catch(Exception e){
                return null;
            }
        }
        return ssb;
    }

    public SpannableStringBuilder append(String str, HashMap<RICHTEXT_TYPE, String> type){
        int position = ssb.length();
        int length = str.length();

        ssb.append(str);
        Set s = type.entrySet();
        Iterator i = s.iterator();
        while (i.hasNext()){
            Map.Entry entry = (Map.Entry)i.next();

            if (RICHTEXT_TYPE.RICHTEXT_COLOR == entry.getKey()){
                int color = Integer.valueOf((String) entry.getValue());
                ssb.setSpan(new ForegroundColorSpan(color), position, position + length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }

            if (RICHTEXT_TYPE.RICHTEXT_SIZE == entry.getKey()){
                int size = Integer.valueOf((String) entry.getValue());
                ssb.setSpan(new AbsoluteSizeSpan(size, true), position, position + length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
        return ssb;
    }

    public enum RICHTEXT_TYPE {
        RICHTEXT_COLOR,
        RICHTEXT_SIZE,
    }
}
