package com.honeywell.wholesale.ui.login.module;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by xiaofei on 7/26/16.
 *
 */
public class TransactionOrderRequest {

    @SerializedName("shop_id")
    private String mShopId;

    @SerializedName("last_request_time")
    private String lastUpdateTime;

    public TransactionOrderRequest(String shopId, String lastUpdateTime){
        mShopId = shopId;
        this.lastUpdateTime = lastUpdateTime;
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

}
