package com.honeywell.wholesale.ui.transaction;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by xiaofei on 10/14/16.
 *
 *  Request http://159.99.93.113:8099/transaction/sale/detail
 */

public class TransactionDetailRequest {

    @SerializedName("shop_id")
    private String shopId;

    @SerializedName("sale_id")
    private String saleId;

    public TransactionDetailRequest() {
    }

    public TransactionDetailRequest(String saleId, String shopId) {
        this.saleId = saleId;
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

    public String getSaleId() {
        return saleId;
    }

    public void setShopId(String shopId) {
        this.shopId = shopId;
    }

    public void setSaleId(String saleId) {
        this.saleId = saleId;
    }
}
