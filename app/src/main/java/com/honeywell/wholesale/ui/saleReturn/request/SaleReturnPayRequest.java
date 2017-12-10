package com.honeywell.wholesale.ui.saleReturn.request;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.honeywell.wholesale.framework.model.PayAccount;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by liuyang on 2017/6/7.
 */

public class SaleReturnPayRequest {
    @SerializedName("sale_id")
    private String mSaleId;

    @SerializedName("account_flow_list")
    private ArrayList<PayAccount> accountFlowList;

    public SaleReturnPayRequest() {
    }

    public SaleReturnPayRequest(String mSaleId, ArrayList<PayAccount> accountFlowList) {
        this.mSaleId = mSaleId;
        this.accountFlowList = accountFlowList;
    }

    public String getOrderNumber() {
        return mSaleId;
    }

    public void setOrderNumber(String mSaleId) {
        this.mSaleId = mSaleId;
    }

    public ArrayList<PayAccount> getAccountFlowList() {
        return accountFlowList;
    }

    public void setAccountFlowList(ArrayList<PayAccount> accountFlowList) {
        this.accountFlowList = accountFlowList;
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
