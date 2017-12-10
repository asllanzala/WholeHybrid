package com.honeywell.wholesale.framework.http;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by H154326 on 16/11/8.
 * Email: yang.liu6@honeywell.com
 */

public class ShopQueryRequest {

    @SerializedName("company_account")
    private String companyAccount;

    public ShopQueryRequest() {
    }

    public ShopQueryRequest(String companyAccount) {
        this.companyAccount = companyAccount;
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

    public String getCompanyAccount() {
        return companyAccount;
    }

    public void setCompanyAccount(String companyAccount) {
        this.companyAccount = companyAccount;
    }
}
