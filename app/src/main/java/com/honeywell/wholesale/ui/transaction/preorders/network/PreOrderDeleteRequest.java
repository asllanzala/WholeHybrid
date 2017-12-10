package com.honeywell.wholesale.ui.transaction.preorders.network;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by H154326 on 17/5/2.
 * Email: yang.liu6@honeywell.com
 */

public class PreOrderDeleteRequest {
    @SerializedName("preorder_id")
    private int preorderId;

    public PreOrderDeleteRequest() {
    }

    public int getPreorderId() {
        return preorderId;
    }

    public void setPreorderId(int preorderId) {
        this.preorderId = preorderId;
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
