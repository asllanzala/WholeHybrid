package com.honeywell.wholesale.ui.menu.setting.warehouse.request;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by H154326 on 17/4/27.
 * Email: yang.liu6@honeywell.com
 */

public class WareHouseDetailRequest {
    @SerializedName("warehouse_id")
    private int wareHouseId;

    public int getWareHouseId() {
        return wareHouseId;
    }

    public WareHouseDetailRequest() {
    }

    public WareHouseDetailRequest(int wareHouseId) {
        this.wareHouseId = wareHouseId;
    }

    public void setWareHouseId(int wareHouseId) {
        this.wareHouseId = wareHouseId;
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
