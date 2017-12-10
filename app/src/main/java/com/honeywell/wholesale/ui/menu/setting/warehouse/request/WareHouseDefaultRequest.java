package com.honeywell.wholesale.ui.menu.setting.warehouse.request;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by H154326 on 17/4/27.
 * Email: yang.liu6@honeywell.com
 */

public class WareHouseDefaultRequest {
    @SerializedName("warehouse_id")
    private int wareHouseId;
    @SerializedName("shop_id")
    private int shopId;

    public WareHouseDefaultRequest() {
    }

    public WareHouseDefaultRequest(int wareHouseId, int shopId) {
        this.wareHouseId = wareHouseId;
        this.shopId = shopId;
    }

    public int getWareHouseId() {
        return wareHouseId;
    }

    public void setWareHouseId(int wareHouseId) {
        this.wareHouseId = wareHouseId;
    }

    public int getShopId() {
        return shopId;
    }

    public void setShopId(int shopId) {
        this.shopId = shopId;
    }

    public String getJsonString(){
        return new Gson().toJson(this);
    }
    public JSONObject getJsonObject(){
        try {
            return new JSONObject(getJsonString());
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
}
