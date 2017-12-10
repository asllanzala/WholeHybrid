package com.honeywell.wholesale.ui.priceDiff.request;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by liuyang on 2017/6/17.
 */

public class ProductDetailPriceDiffRequest {

    @SerializedName("product_id")
    private int productId;
    @SerializedName("product_code")
    private String productCode;  //条形码
    @SerializedName("warehouse_id")
    private int wareHouseId;
    @SerializedName("shop_id")
    private int shopId;

    public ProductDetailPriceDiffRequest() {
    }

    public ProductDetailPriceDiffRequest(int productId, String shopId) {
        this.productId = productId;
        this.shopId = Integer.valueOf(shopId);
    }

    public ProductDetailPriceDiffRequest(int productId, int wareHouseId) {
        this.productId = productId;
        this.wareHouseId = wareHouseId;
    }

    public ProductDetailPriceDiffRequest(String productCode, String shopId) {
        this.productCode = productCode;
        this.shopId = Integer.valueOf(shopId);
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public int getWareHouseId() {
        return wareHouseId;
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
