package com.honeywell.wholesale.framework.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.honeywell.wholesale.framework.application.WholesaleApplication;

public class SharePreferenceUtil {

    /**
     * get instance of SharedPreference by file name
     *
     * @param name  name of the SharedPreference storage file. (i.e group control, user login etc)
     * @return  instance of the particular SharedPreference
     */
    public static SharedPreferences getSharedPreferenceInstanceByName(String name) {
        return WholesaleApplication.getInstance().getApplicationContext().
                    getSharedPreferences(name, Context.MODE_PRIVATE);
    }

    /**
     * get/set methods for particular SharePreference
     *
     * @param storageName name of the SharedPreference storage file. (i.e group control, user login etc)
     * @param key
     * @param defaultValue
     * @return
     */
    public static void setPrefInt(final String storageName, final String key, final int defaultValue) {
        getSharedPreferenceInstanceByName(storageName).edit().putInt(key, defaultValue).commit();
    }

    public static int getPrefInt(final String storageName, final String key, final int defaultValue) {
        return getSharedPreferenceInstanceByName(storageName).getInt(key, defaultValue);
    }

    public static long getPrefLong(final String storageName, final String key, final long defaultValue) {
        return getSharedPreferenceInstanceByName(storageName).getLong(key, defaultValue);
    }

    public static void setPrefBoolean(final String storageName, final String key, final boolean defaultValue) {
        getSharedPreferenceInstanceByName(storageName).edit().putBoolean(key, defaultValue).commit();
    }

    public static boolean getPrefBoolean(final String storageName, final String key, final boolean defaultValue) {
        return getSharedPreferenceInstanceByName(storageName).getBoolean(key, defaultValue);
    }

    public static String getPrefString(final String storageName, String key, final String defaultValue) {
        return getSharedPreferenceInstanceByName(storageName).getString(key, defaultValue);
    }

    public static void setPrefString(final String storageName, final String key, final String defaultValue) {
        getSharedPreferenceInstanceByName(storageName).edit().putString(key, defaultValue).commit();
    }

    public static void setPrefLong(final String storageName, final String key, final long defaultValue) {
        getSharedPreferenceInstanceByName(storageName).edit().putLong(key, defaultValue).commit();
    }

    public static void clearPreference(Context context, final SharedPreferences p) {
        final SharedPreferences.Editor editor = p.edit();
        editor.clear();
        editor.commit();
    }


}
