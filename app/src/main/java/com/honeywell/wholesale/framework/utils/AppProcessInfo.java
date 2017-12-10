package com.honeywell.wholesale.framework.utils;

import android.app.ActivityManager;
import android.content.Context;

import java.util.List;

/**
 * Created by H155935 on 16/5/18.
 * Email: xiaofei.he@honeywell.com
 */
public class AppProcessInfo {

    public enum AppInfo {
        APP_DEAD,
        APP_BACKGROUND,
        APP_FOREGROUND,
    }

    public static AppInfo isAppOnForeground(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        if (appProcesses == null) {
            return AppInfo.APP_DEAD;
        }
        final String packageName = context.getPackageName();
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {

            if (appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND && appProcess.processName.equals(packageName)) {
                return AppInfo.APP_FOREGROUND;
            }

            if (appProcess.processName.equals(packageName)){
                return AppInfo.APP_BACKGROUND;
            }
        }
        return AppInfo.APP_DEAD;
    }
}
