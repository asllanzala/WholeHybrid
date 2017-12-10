package com.honeywell.wholesale.framework.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by H155935 on 16/5/11.
 * Email: xiaofei.he@honeywell.com
 */
public class Inventory implements BaseModel{

    @SerializedName("product_number")
    private String productNumber;

    @SerializedName("product_code")
    private String productCode;

    @SerializedName("category_id")
    private String category;

    @SerializedName("category_ids")
    private int[] categoryies;

    @SerializedName("sale_number")
    private String saleNumber;

    @SerializedName("name")
    private String productName;

    @SerializedName("total")
    private String total;

    @SerializedName("standard_price")
    private String standardPrice = "0";

    @SerializedName("stock_price")
    private String stockPrice;

    @SerializedName("vip_price")
    private String vipPrice;

    @SerializedName("avg_price")
    private String avgPrice;

    @SerializedName("last_sale_time")
    private String lastSaleTime;

    @SerializedName("pic_src")
    private String picSrc;

    @SerializedName("quantity")
    private String quantity;

    @SerializedName("product_id")
    private int productId;

    @SerializedName("unit_name")
    private String unit = "";

    @SerializedName("product_name_py")
    private String pyInitials;

    @SerializedName("product_name_pinyin")
    private String pinyin;

    public Inventory(int productId) {
        this.productId = productId;
    }

    public Inventory(String productCode, String category, String saleNumber,
                     String productName, String standardPrice, String stockPrice,
                     String vipPrice, String avgPrice, String lastSaleTime,
                     String picSrc, String quantity, int productId, String productNumber, String unit) {
        this.productCode = productCode;
        this.category = category;
        this.saleNumber = saleNumber;
        this.productName = productName;
        this.standardPrice = standardPrice;
        this.stockPrice = stockPrice;
        this.vipPrice = vipPrice;
        this.avgPrice = avgPrice;
        this.lastSaleTime = lastSaleTime;
        this.picSrc = picSrc;
        this.quantity = quantity;
        this.productId = productId;
        this.productNumber = productNumber;
        this.unit = unit;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getCategory() {
        if (category != null){
            return category;
        }

        if (categoryies == null){
            return category;
        }

        if (categoryies.length != 0){
            category = String.valueOf(categoryies[0]);
        }

        return category;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getStandardCategory() {
        if (category != null){
            return category;
        }

        if ((categoryies == null) && (category == null)){
            return "";
        }

        if (categoryies.length != 0){
            category = String.valueOf(categoryies[0]);
        }

        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getSaleNumber() {
        return saleNumber;
    }

    public void setSaleNumber(String saleNumber) {
        this.saleNumber = saleNumber;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getStandardPrice() {
        return standardPrice;
    }

    public void setStandardPrice(String standardPrice) {
        this.standardPrice = standardPrice;
    }

    public String getStockPrice() {
        return stockPrice;
    }

    public void setStockPrice(String stockPrice) {
        this.stockPrice = stockPrice;
    }

    public String getVipPrice() {
        return vipPrice;
    }

    public void setVipPrice(String vipPrice) {
        this.vipPrice = vipPrice;
    }

    public String getAvgPrice() {
        return avgPrice;
    }

    public void setAvgPrice(String avgPrice) {
        this.avgPrice = avgPrice;
    }

    public String getLastSaleTime() {
        return lastSaleTime;
    }

    public void setLastSaleTime(String lastSaleTime) {
        this.lastSaleTime = lastSaleTime;
    }

    public String getPicSrc() {
        return picSrc;
    }

    public void setPicSrc(String picSrc) {
        this.picSrc = picSrc;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public String getPinyin() {
        return pinyin;
    }

    public String getPyInitials() {
        return pyInitials;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public int[] getCategoryies() {
        return categoryies;
    }

    public String getProductNumber() {
        return productNumber;
    }

    public void setProductNumber(String productNumber) {
        this.productNumber = productNumber;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }


}
