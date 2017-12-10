package com.honeywell.wholesale.ui.scan.network;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by H154326 on 17/3/2.
 * Email: yang.liu6@honeywell.com
 */

public class ProductDetailRequest {
    @SerializedName("product_id")
    private int productId;
    @SerializedName("product_code")
    private String productCode;  //条形码
    @SerializedName("warehouse_id")
    private int wareHouseId;

    public ProductDetailRequest(int productId) {
        this.productId = productId;
    }

    public ProductDetailRequest(int productId, int wareHouseId) {
        this.productId = productId;
        this.wareHouseId = wareHouseId;
    }

    public ProductDetailRequest(String productCode, int wareHouseId) {
        this.productCode = productCode;
        this.wareHouseId = wareHouseId;
    }

    public ProductDetailRequest(int productId, String productCode, int wareHouseId) {
        this.productId = productId;
        this.productCode = productCode;
        this.wareHouseId = wareHouseId;
    }

    public ProductDetailRequest() {
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