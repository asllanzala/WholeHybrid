package com.honeywell.wholesale.ui.login.module;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by xiaofei on 7/18/16.
 *
 */
public class UpdateInventoryCloudApiRequest {

    @SerializedName("shop_id")
    private String mShopId;

    @SerializedName("last_request_time")
    private String mLastRequestTime;

    @SerializedName("page_length")
    private String pageLength = "100";

    public UpdateInventoryCloudApiRequest(String shopId, String lastRequestTime){
        mShopId = shopId;
        mLastRequestTime = lastRequestTime;
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

    public String getmShopId() {
        return mShopId;
    }

    public void setmShopId(String mShopId) {
        this.mShopId = mShopId;
    }

    public String getmLastRequestTime() {
        return mLastRequestTime;
    }

    public void setmLastRequestTime(String mLastRequestTime) {
        this.mLastRequestTime = mLastRequestTime;
    }

}
