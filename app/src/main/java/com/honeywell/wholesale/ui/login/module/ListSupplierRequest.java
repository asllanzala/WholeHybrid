package com.honeywell.wholesale.ui.login.module;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by xiaofei on 8/29/16.
 *
 */
public class ListSupplierRequest {

    public ListSupplierRequest(String mShopId, String lastUpdateTime) {
        this.mShopId = mShopId;
        this.lastUpdateTime = lastUpdateTime;
    }

    public ListSupplierRequest(String mShopId, String lastUpdateTime, String mUntilTime) {
        this.mShopId = mShopId;
        this.lastUpdateTime = lastUpdateTime;
        this.mUntilTime = mUntilTime;
    }

    public ListSupplierRequest(String mShopId, String lastUpdateTime, String mUntilTime, String mSearchKey) {
        this.mShopId = mShopId;
        this.lastUpdateTime = lastUpdateTime;
        this.mUntilTime = mUntilTime;
        this.mSearchKey = mSearchKey;
    }

    @SerializedName("shop_id")
    private String mShopId;

    @SerializedName("last_request_time")
    private String lastUpdateTime;

    @SerializedName("until_time")
    private String mUntilTime;

    @SerializedName("search_string")
    private String mSearchKey;

    @SerializedName("page_length")
    private String pageSize;

    public String getmShopId() {
        return mShopId;
    }

    public String getJsonString() {
        return new Gson().toJson(this);
    }

    public JSONObject getJsonObject() {
        try {
            return new JSONObject(getJsonString());
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
}
