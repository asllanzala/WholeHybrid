package com.honeywell.wholesale.ui.purchase.request;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by liuyang on 2017/7/6.
 */

public class ProductDetailBean {
    @SerializedName("product_id")
    private int productId;
    @SerializedName("product_code")
    private String productCode;  //条形码
    @SerializedName("shop_id")
    private int shopId;

    public ProductDetailBean(int productId) {
        this.productId = productId;
    }

    public ProductDetailBean(int productId, int shopId) {
        this.productId = productId;
        this.shopId = shopId;
    }

    public ProductDetailBean(String productCode, int shopId) {
        this.productCode = productCode;
        this.shopId = shopId;
    }

    public ProductDetailBean(int productId, String productCode, int shopId) {
        this.productId = productId;
        this.productCode = productCode;
        this.shopId = shopId;
    }

    public ProductDetailBean() {
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

    public int getShopId() {
        return shopId;
    }

    public void setShopId(int shopId) {
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
