package com.honeywell.wholesale.ui.menu.shop;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by H154326 on 17/1/18.
 * Email: yang.liu6@honeywell.com
 */

public class GetCustomerDetailApiRequest {
    @SerializedName("employee_id")
    private String mEmplyeeId;

    @SerializedName("company_account")
    private String mCompanyAccount;


    public GetCustomerDetailApiRequest() {
    }

    public GetCustomerDetailApiRequest(String mEmplyeeId, String mCompanyAccount) {
        this.mEmplyeeId = mEmplyeeId;
        this.mCompanyAccount = mCompanyAccount;
    }

    public String getmEmplyeeId() {
        return mEmplyeeId;
    }

    public void setmEmplyeeId(String mEmplyeeId) {
        this.mEmplyeeId = mEmplyeeId;
    }

    public String getmCompanyAccount() {
        return mCompanyAccount;
    }

    public void setmCompanyAccount(String mCompanyAccount) {
        this.mCompanyAccount = mCompanyAccount;
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
