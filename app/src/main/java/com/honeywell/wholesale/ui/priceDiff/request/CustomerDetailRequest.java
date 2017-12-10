package com.honeywell.wholesale.ui.priceDiff.request;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by liuyang on 2017/6/16.
 */

public class CustomerDetailRequest {
    @SerializedName("shop_id")
    private int shopId;

    @SerializedName("customer_id")
    private int customerId;

    public CustomerDetailRequest() {
    }

    public CustomerDetailRequest(int shopId, int customerId) {
        this.shopId = shopId;
        this.customerId = customerId;
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
