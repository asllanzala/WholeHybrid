package com.honeywell.wholesale.framework.utils;


import java.util.Calendar;
import java.util.TimeZone;

/**
 * Created by H154326 on 16/11/24.
 * Email: yang.liu6@honeywell.com
 */

public class CalendarString {
    private static String mYear;
    private static String mMonth;
    private static String mDay;
    private static String mWeek;

    public static String StringData(){
        final Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));

        mYear = String.valueOf(calendar.get(Calendar.YEAR));
        mMonth = String.valueOf(calendar.get(Calendar.MONTH)+1);
        mDay = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
        mWeek = String.valueOf(calendar.get(Calendar.DAY_OF_WEEK));

        if("1".equals(mWeek)){
            mWeek ="天";
        }else if("2".equals(mWeek)){
            mWeek ="一";
        }else if("3".equals(mWeek)){
            mWeek ="二";
        }else if("4".equals(mWeek)){
            mWeek ="三";
        }else if("5".equals(mWeek)){
            mWeek ="四";
        }else if("6".equals(mWeek)){
            mWeek ="五";
        }else if("7".equals(mWeek)){
            mWeek ="六";
        }
        return mMonth + "月" + mDay+"日"+" 星期"+mWeek;
    }
}
