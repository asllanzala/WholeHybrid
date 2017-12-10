package com.honeywell.wholesale.ui.menu.setting.warehouse.request;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by H154326 on 17/4/27.
 * Email: yang.liu6@honeywell.com
 */

public class WareHouseBlockRequest {
    @SerializedName("warehouse_id")
    private int wareHouseId;
    @SerializedName("enabled")
    private boolean enabled;

    public WareHouseBlockRequest() {
    }

    public WareHouseBlockRequest(int wareHouseId) {
        this.wareHouseId = wareHouseId;
    }

    public WareHouseBlockRequest(int wareHouseId, boolean enabled) {
        this.wareHouseId = wareHouseId;
        this.enabled = enabled;
    }

    public int getWareHouseId() {
        return wareHouseId;
    }

    public void setWareHouseId(int wareHouseId) {
        this.wareHouseId = wareHouseId;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
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
