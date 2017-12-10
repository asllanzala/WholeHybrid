package com.honeywell.wholesale.framework.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by H154326 on 16/11/25.
 * Email: yang.liu6@honeywell.com
 */

public class Shop {

    @SerializedName("shopId")
    private String shopId;

    @SerializedName("shopName")
    private String shopName;

    @SerializedName("ShopAddress")
    private String shopAddress;

    public Shop() {
    }

    public Shop(String shopName, String shopId) {
        this.shopName = shopName;
        this.shopId = shopId;
    }

    public Shop(String shopId, String shopName, String shopAddress) {
        this.shopId = shopId;
        this.shopName = shopName;
        this.shopAddress = shopAddress;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null){
            return false;
        }

        if (o == this){
            return true;
        }

        if (!(o instanceof Category)){
            return false;
        }

        Category other = (Category)o;

        if (other.getCategoryId().equals(this.shopId) && other.getName().equals(this.shopName)){
            return true;
        }

        return false;
    }

    public String getShopId() {
        return shopId;
    }

    public void setShopId(String shopId) {
        this.shopId = shopId;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }
}
