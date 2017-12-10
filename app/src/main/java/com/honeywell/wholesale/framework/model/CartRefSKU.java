package com.honeywell.wholesale.framework.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by xiaofei on 3/21/17.
 *
 */

public class CartRefSKU {

    @SerializedName("uuid")
    private String cartUuid;

    @SerializedName("product_id")
    private String productId;

    @SerializedName("product_code")
    private String productCode;

    @SerializedName("product_number")
    private String productNumber;

    @SerializedName("product_name")
    private String productName;

    @SerializedName("product_unit_price")
    private String productUnitPrice;

    @SerializedName("total_number")
    private String totalNumber;

    @SerializedName("image_url")
    private String imageUrl;

    @SerializedName("unit_name")
    private String unitName;

    @SerializedName("quantity")
    private String quantity;

    @SerializedName("sku_json")
    private String skuJson;

    public CartRefSKU() {
    }

    public CartRefSKU(String cartUuid, String productId, String productCode, String productNumber,
                      String productName, String productUnitPrice, String totalNumber,
                      String imageUrl, String unitName, String quantity, String skuJson) {
        this.cartUuid = cartUuid;
        this.productId = productId;
        this.productCode = productCode;
        this.productNumber = productNumber;
        this.productName = productName;
        this.productUnitPrice = productUnitPrice;
        this.totalNumber = totalNumber;
        this.imageUrl = imageUrl;
        this.unitName = unitName;
        this.quantity = quantity;
        this.skuJson = skuJson;
    }

    public String getCartUuid() {
        return cartUuid;
    }

    public String getProductId() {
        return productId;
    }

    public String getProductCode() {
        return productCode;
    }

    public String getProductNumber() {
        return productNumber;
    }

    public String getProductName() {
        return productName;
    }

    public String getProductUnitPrice() {
        return productUnitPrice;
    }

    public String getTotalNumber() {
        return totalNumber;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getUnitName() {
        return unitName;
    }

    public String getQuantity() {
        return quantity;
    }

    public String getSkuJson() {
        return skuJson;
    }
}
