package com.honeywell.hybridapp.framework.log;

import android.os.Build;
import android.util.Log;

/**
 * Created by allanhwmac on 16/5/20.
 */
public class LogHelper {

    public static final int VERBOSE = 1;
    public static final int DEBUG = 2;
    public static final int INFO = 3;
    public static final int WARN = 4;
    public static final int ERROR = 5;
    public static final int NOTHING = 6;
    public static final int LEVEL = VERBOSE;


    public static LogHelper mLogHelper = null;

    public static synchronized LogHelper getInstance() {
        if(mLogHelper == null) {
            mLogHelper = new LogHelper();
        }

        return mLogHelper;
    }

    private LogHelper(){

    }

    public static void v(String tag, String logData){
        if (LEVEL <= VERBOSE) {
            Log.v(tag, logData);
        }
    }

    public static void i(String tag, String logData){
        if (LEVEL <= INFO) {
            Log.i(tag, logData);
        }
    }

    public static void d(String tag, String logData){
        if (LEVEL <= DEBUG) {
            Log.d(tag, logData);
        }
    }

    public static void w(String tag, String logData){
        if (LEVEL <= WARN) {
            Log.w(tag, logData);
        }
    }

    public static void e(String tag, String logData){
        if (LEVEL <= ERROR) {
            Log.e(tag, logData);
        }
    }
}
