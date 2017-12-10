package com.honeywell.wholesale.ui.login.module;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by xiaofei on 7/15/16.
 *
 *  {
 *       "employee_id": "18688888888"
 *  }
 */

public class UpdateCategoryCloudApiRequest {

    @SerializedName("employee_id")
    private String mEmplyeeId;

    @SerializedName("shop_id")
    private int mShopId;

    @SerializedName("category_stock_status")
    private String categoryStockStatus;

    @SerializedName("warehouse_id")
    private int warehouseId = -1;


    public UpdateCategoryCloudApiRequest(String mEmplyeeId, int mShopId) {
        this.mEmplyeeId = mEmplyeeId;
        this.mShopId = mShopId;
    }

    public UpdateCategoryCloudApiRequest(String mEmplyeeId, String categoryStockStatus) {
        this.mEmplyeeId = mEmplyeeId;
        this.categoryStockStatus = categoryStockStatus;
    }

    public UpdateCategoryCloudApiRequest(String mEmplyeeId, int mShopId, String categoryStockStatus) {
        this.mEmplyeeId = mEmplyeeId;
        this.mShopId = mShopId;
        this.categoryStockStatus = categoryStockStatus;
    }

    public UpdateCategoryCloudApiRequest(String mEmplyeeId, int mShopId, String categoryStockStatus, int warehouseId) {
        this.mEmplyeeId = mEmplyeeId;
        this.mShopId = mShopId;
        this.categoryStockStatus = categoryStockStatus;
        this.warehouseId = warehouseId;
    }

    public String getCategoryStockStatus() {
        return categoryStockStatus;
    }

    public void setCategoryStockStatus(String categoryStockStatus) {
        this.categoryStockStatus = categoryStockStatus;
    }

    public String getmEmplyeeId() {
        return mEmplyeeId;
    }

    public void setmEmplyeeId(String mEmplyeeId) {
        this.mEmplyeeId = mEmplyeeId;
    }

    public int getmShopId() {
        return mShopId;
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
