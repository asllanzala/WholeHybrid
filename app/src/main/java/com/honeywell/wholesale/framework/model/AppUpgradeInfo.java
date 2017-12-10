package com.honeywell.wholesale.framework.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by xiaofei on 7/20/16.
 *
 */
public class AppUpgradeInfo {

    @SerializedName("url")
    private String url;

    @SerializedName("version")
    private String versionCode; // version code

    @SerializedName("versionDetail")
    private String versionDetail;

    @SerializedName("versionName")
    private String versionName;

    public AppUpgradeInfo(String url, String version, String versionDetail, String versionName) {
        this.url = url;
        this.versionCode = version;
        this.versionDetail = versionDetail;
        this.versionName = versionName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getVersionCode() {
        return versionCode;
    }

    public int getVersionCodeInt() {
        return Integer.valueOf(versionCode);
    }

    public void setVersionCode(String version) {
        this.versionCode = version;
    }

    public String getVersionDetail() {
        return versionDetail;
    }

    public void setVersionDetail(String versionDetail) {
        this.versionDetail = versionDetail;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }
}
