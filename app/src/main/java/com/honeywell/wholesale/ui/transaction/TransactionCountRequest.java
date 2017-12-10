package com.honeywell.wholesale.ui.transaction;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by xiaofei on 10/14/16.
 *
 * Request http://159.99.93.113:8099/transaction/sale/get_numbers
 */

public class TransactionCountRequest {

    @SerializedName("shop_id")
    private String shopId;

    public TransactionCountRequest() {
    }

    public TransactionCountRequest(String shopId) {
        this.shopId = shopId;
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

    public String getShopId() {
        return shopId;
    }

    public void setShopId(String shopId) {
        this.shopId = shopId;
    }
}
