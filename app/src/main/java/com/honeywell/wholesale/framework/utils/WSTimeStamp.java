package com.honeywell.wholesale.framework.utils;

import android.util.Log;

import com.honeywell.hybridapp.framework.log.LogHelper;
import com.honeywell.wholesale.R;
import com.honeywell.wholesale.framework.application.WholesaleApplication;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by xiaofei on 9/12/16.
 *
 */
public class WSTimeStamp {
    private static final String TAG = "WSTimeStamp";
    private static final Long MILLIS_IN_DAY = 86400000L;

    public String getCurrentTimeStamp(){
        Long tsLong = System.currentTimeMillis();
        return tsLong.toString();
    }

    public static String getLastMonth(){
        Calendar cal = Calendar.getInstance();
        System.out.println("Before "+cal.getTime().toString());
        cal.add(Calendar.MONTH, -1);
        System.out.println("After "+cal.getTime().toString());

        LogHelper.getInstance().e(TAG, cal.getTime().toString());
        Long tsLong = cal.getTimeInMillis();
        return tsLong.toString();
    }

    public static String getLast3Month(){
        Calendar c = Calendar.getInstance();
        c.add(Calendar.MONTH, -3);

        LogHelper.getInstance().e(TAG, c.getTime().toString());
        Long tsLong = c.getTimeInMillis();
        return tsLong.toString();
    }

    public static String getLastWeek(){
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DAY_OF_YEAR, -7);

        LogHelper.getInstance().e(TAG, c.getTime().toString());
        return String.valueOf(c.getTimeInMillis());
    }

    public static long getSpecialDayEndTime(int days){
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR, 24);
        calendar.add(Calendar.DAY_OF_YEAR, days);
        calendar.set(Calendar.HOUR_OF_DAY, 24);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.add(Calendar.MILLISECOND, -1);
        System.out.println("getSpecialDayEndTime " + calendar.getTime().toString());
        LogHelper.getInstance().e(TAG, calendar.getTime().toString());
        return calendar.getTimeInMillis();
    }

    public static long getSpecialDayStartTime(int days){
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR, 24);
        calendar.add(Calendar.DAY_OF_YEAR, days);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        System.out.println("getSpecialDayStartTime "+calendar.getTime().toString());
        return calendar.getTimeInMillis();
    }

    public static String getLastMonthStartTime(){
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, -1);  // calendar的1月是0
        calendar.set(Calendar.DAY_OF_MONTH, 0);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        System.out.println("getLastMonthStartTime "+calendar.getTime().toString());
        Long tsLong = calendar.getTimeInMillis();
        return tsLong.toString();
    }

    public static String getSpecialMonthStartTime(int months){
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, months);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        Long tsLong = calendar.getTimeInMillis();
        return tsLong.toString();
    }

    public static String getSpecialMonthEndTime(int months){
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, months);
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        calendar.set(Calendar.HOUR_OF_DAY, 24);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.add(Calendar.MILLISECOND, -1);
        Long tsLong = calendar.getTimeInMillis();
        return tsLong.toString();
    }

    public static String getSpecialYearsStartTime(int years){
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR, 24);
        calendar.add(Calendar.YEAR, years);
        calendar.set(Calendar.MONTH, 0);  // calendar的1月是0
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        Long tsLong = calendar.getTimeInMillis();
        return tsLong.toString();
    }

    public static String getSpecialYearsEndTime(int years){
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR, 24);
        calendar.add(Calendar.YEAR, years);
        calendar.set(Calendar.MONTH, calendar.getActualMaximum(Calendar.MONTH));  // calendar的1月是0
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        calendar.set(Calendar.HOUR_OF_DAY, 24);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.add(Calendar.MILLISECOND, -1);
        Long tsLong = calendar.getTimeInMillis();
        return tsLong.toString();
    }

    public static String getFullTimeString(long timeInMillis) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date(timeInMillis);
        String dateStr = format.format(date);
//        LogHelper.d(TAG, "mTimeStamp=" + timeInMillis + ", format2=" + format.format(date));
        return dateStr;
    }

    /**
     *
     * @param timeInMillis
     * @return "MM-dd"
     */
    public static String getMonthDateString(long timeInMillis) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timeInMillis);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH) + 1;  // calendar的1月是0

        return month + "-" + day;
    }

    /**
     *
     * @param timeInMillis
     * @return yy年
     */
    public static String getYearString(long timeInMillis) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timeInMillis);
        int year = calendar.get(Calendar.YEAR);
        String yearStr = String.valueOf(year);
        yearStr = yearStr.substring(yearStr.length()-2, yearStr.length());
        return yearStr+ "年";
    }

    /**
     *
     * @param timeInMillis
     * @return yy年MM月
     */
    public static String getYearMonthString(long timeInMillis) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timeInMillis);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;  // calendar的1月是0

        String yearStr = String.valueOf(year);
        yearStr = yearStr.substring(yearStr.length()-2, yearStr.length());
        return yearStr+ "年" + month + "月";
    }

    /**
     *
     * @param timeInMillis
     * @return yy年MM月
     */
    public static String getChartYearMonthString(long timeInMillis) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timeInMillis);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;  // calendar的1月是0
        return year+ "年" + month + "月";
    }

    /**
     *
     * @param timeInMillis
     * @return MM月
     */
    public static String getMonthString(long timeInMillis) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timeInMillis);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;  // calendar的1月是0
        return year + "-" + month;
    }

    /**
     *
     * @param timeInMillis
     * @return MM日
     */
    public static String getChartDayString(long timeInMillis) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timeInMillis);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        return day + "日";
    }
    /**
     *
     * @param timeInMillis
     * @return MM月
     */
    public static String getChartMonthString(long timeInMillis) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timeInMillis);
        int month = calendar.get(Calendar.MONTH) + 1;  // calendar的1月是0
        return month + "月";
    }

    /**
     *
     * @param timeInMillis
     * @return MMMM年
     */
    public static String getChartYearString(long timeInMillis) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timeInMillis);
        int year = calendar.get(Calendar.YEAR);
        return year + "年";
    }

    /**
     * @return 2016年2月3日 星期五
     */
    public static String getChartYearMonthDayString(long timeInMillis) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timeInMillis);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;  // calendar的1月是0
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        String mWay = String.valueOf(calendar.get(Calendar.DAY_OF_WEEK));
        if("1".equals(mWay)){
            mWay ="日";
        }else if("2".equals(mWay)){
            mWay ="一";
        }else if("3".equals(mWay)){
            mWay ="二";
        }else if("4".equals(mWay)){
            mWay ="三";
        }else if("5".equals(mWay)){
            mWay ="四";
        }else if("6".equals(mWay)){
            mWay ="五";
        }else if("7".equals(mWay)){
            mWay ="六";
        }
        return year + "年" + month + "月" + day+"日"+" 星期"+mWay;
    }

    /**
     * @return 16年2月3日
     */
    public static String getYearMonthDayString(long timeInMillis) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timeInMillis);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;  // calendar的1月是0
        int day = calendar.get(Calendar.DAY_OF_MONTH);


        Calendar nowTime = Calendar.getInstance();
        int nowYear = nowTime.get(Calendar.YEAR);
        int nowMonth = nowTime.get(Calendar.MONTH) + 1;  // calendar的1月是0
        int nowDay = nowTime.get(Calendar.DAY_OF_MONTH);

        if (year == nowYear && month == nowMonth && day == nowDay) {
            LogHelper.getInstance().d(TAG, "year=" + year
                    + ", month=" + month
                    + ", day=" + day
                    + ", nowYear=" + nowYear
                    + ", nowMonth=" + nowMonth
                    + ", nowDay=" + nowDay);
            return WholesaleApplication.getAppContext().getResources().getString(R.string.today);
        } else {
            String yearStr = String.valueOf(year);
            yearStr = yearStr.substring(yearStr.length()-2, yearStr.length());
            return yearStr+ "年" + month + "月" + day + "日";
        }
    }

    public static boolean isSameDay(long timeInMillis1, long timeInMillis2) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timeInMillis1);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;  // calendar的1月是0
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        Calendar nowTime = Calendar.getInstance();
        calendar.setTimeInMillis(timeInMillis2);
        int nowYear = nowTime.get(Calendar.YEAR);
        int nowMonth = nowTime.get(Calendar.MONTH) + 1;  // calendar的1月是0
        int nowDay = nowTime.get(Calendar.DAY_OF_MONTH);

        return  (year == nowYear && month == nowMonth && day == nowDay);
    }

    // 根据毫秒时间戳获取当前时间格式字符串
    public static String getDate(long milliSeconds, String dateFormat) {
        // Create a DateFormatter object for displaying date in specified format.
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }

}
