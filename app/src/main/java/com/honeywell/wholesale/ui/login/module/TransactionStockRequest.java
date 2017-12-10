package com.honeywell.wholesale.ui.login.module;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by xiaofei on 7/26/16.
 *
 */
public class TransactionStockRequest {
    @SerializedName("shop_id")
    private String mShopId;

    @SerializedName("last_request_time")
    private String lastRequestTime;

    public TransactionStockRequest(String shopId, String lastRequestTime){
        mShopId = shopId;
        this.lastRequestTime = lastRequestTime;
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

    public void setShopId(String shopId){
        this.mShopId = shopId;
    }

    public String getShopId(){
        return mShopId;
    }


    public String getLastRequestTime() {
        return lastRequestTime;
    }

    public void setLastRequestTime(String lastRequestTime) {
        this.lastRequestTime = lastRequestTime;
    }
}
