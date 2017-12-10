package com.honeywell.wholesale.ui.priceDiff.request;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.honeywell.wholesale.framework.model.PayAccount;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by liuyang on 2017/6/7.
 */

public class PriceDiffPayRequest {

    @SerializedName("sale_id")
    private String mSaleId;

    @SerializedName("shop_id")
    private String shopId;

    @SerializedName("account_flow_list")
    private ArrayList<PayAccount> accountFlowList;

    public PriceDiffPayRequest() {
    }

    public PriceDiffPayRequest(String mSaleId, String shopId, ArrayList<PayAccount> accountFlowList) {
        this.mSaleId = mSaleId;
        this.shopId = shopId;
        this.accountFlowList = accountFlowList;
    }

    public PriceDiffPayRequest(String mSaleId, ArrayList<PayAccount> accountFlowList) {
        this.mSaleId = mSaleId;
        this.accountFlowList = accountFlowList;
    }

    public String getmSaleId() {
        return mSaleId;
    }

    public void setmSaleId(String mSaleId) {
        this.mSaleId = mSaleId;
    }

    public ArrayList<PayAccount> getAccountFlowList() {
        return accountFlowList;
    }

    public void setAccountFlowList(ArrayList<PayAccount> accountFlowList) {
        this.accountFlowList = accountFlowList;
    }

    public String getShopId() {
        return shopId;
    }

    public void setShopId(String shopId) {
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
}
