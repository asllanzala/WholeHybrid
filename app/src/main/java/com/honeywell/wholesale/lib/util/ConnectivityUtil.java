package com.honeywell.wholesale.lib.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;

import com.honeywell.wholesale.framework.application.WholesaleApplication;

/**
 * Created by xiaofei on 7/7/16.
 *
 */
public class ConnectivityUtil {

    private static NetworkInfo getNetworkInfo(){
        ConnectivityManager cm = (ConnectivityManager)
                WholesaleApplication.getAppContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo();
    }

    public static boolean isConnected(){
        NetworkInfo info = ConnectivityUtil.getNetworkInfo();
        return (info != null && info.isConnected());
    }

    public static boolean isConnectedMobile(){
        NetworkInfo info = ConnectivityUtil.getNetworkInfo();
        return (info != null && info.isConnected() && info.getType() == ConnectivityManager.TYPE_MOBILE);
    }

    public static boolean isConnectedWiFi(){
        NetworkInfo info = ConnectivityUtil.getNetworkInfo();
        return (info != null && info.isConnected() && info.getType() == ConnectivityManager.TYPE_WIFI);
    }

    public static NET_TYPE connectionType(){
        NetworkInfo info = ConnectivityUtil.getNetworkInfo();
        if (info == null){
            return NET_TYPE.NET_NONE;
        }

        int type = info.getType();

        if (type == ConnectivityManager.TYPE_WIFI){
            return NET_TYPE.NET_WIFI;
        }

        int subType = info.getSubtype();
        switch (subType) {
            case TelephonyManager.NETWORK_TYPE_GPRS:
            case TelephonyManager.NETWORK_TYPE_EDGE:
            case TelephonyManager.NETWORK_TYPE_CDMA:
            case TelephonyManager.NETWORK_TYPE_1xRTT:
            case TelephonyManager.NETWORK_TYPE_IDEN:
                return NET_TYPE.NET_2G;
            case TelephonyManager.NETWORK_TYPE_UMTS:
            case TelephonyManager.NETWORK_TYPE_EVDO_0:
            case TelephonyManager.NETWORK_TYPE_EVDO_A:
            case TelephonyManager.NETWORK_TYPE_HSDPA:
            case TelephonyManager.NETWORK_TYPE_HSUPA:
            case TelephonyManager.NETWORK_TYPE_HSPA:
            case TelephonyManager.NETWORK_TYPE_EVDO_B:
            case TelephonyManager.NETWORK_TYPE_EHRPD:
            case TelephonyManager.NETWORK_TYPE_HSPAP:
                return NET_TYPE.NET_3G;
            case TelephonyManager.NETWORK_TYPE_LTE:
                return NET_TYPE.NET_4G;
            default:
                return NET_TYPE.NET_UNKNOWN;
        }

    }

    public enum NET_TYPE{
        NET_NONE,
        NET_WIFI,
        NET_4G,
        NET_3G,
        NET_2G,
        NET_UNKNOWN,
    }
}
