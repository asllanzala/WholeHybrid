package com.honeywell.wholesale.ui.purchase.request;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.honeywell.wholesale.framework.model.PayAccount;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by liuyang on 2017/6/7.
 */

public class PurchasePayRequest {
    @SerializedName("purchase_summary_id")
    private String mPurchaseSummaryId;

    @SerializedName("shop_id")
    private String mShopId;

    @SerializedName("account_flow_list")
    private ArrayList<PayAccount> accountFlowList;

    public PurchasePayRequest() {
    }

    public PurchasePayRequest(String mPurchaseSummaryId, String mShopId, ArrayList<PayAccount> accountFlowList) {
        this.mPurchaseSummaryId = mPurchaseSummaryId;
        this.mShopId = mShopId;
        this.accountFlowList = accountFlowList;
    }

    public PurchasePayRequest(String mPurchaseSummaryId, ArrayList<PayAccount> accountFlowList) {
        this.mPurchaseSummaryId = mPurchaseSummaryId;
        this.accountFlowList = accountFlowList;
    }

    public String getOrderNumber() {
        return mPurchaseSummaryId;
    }

    public void setOrderNumber(String mSaleId) {
        this.mPurchaseSummaryId = mSaleId;
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
