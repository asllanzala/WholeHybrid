package com.honeywell.wholesale.framework.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by H154326 on 17/5/3.
 * Email: yang.liu6@honeywell.com
 */

public class InventorySku {

    @SerializedName("warehouse_id")
    private int wareHouseId;

    @SerializedName("warehouse_name")
    private String wareHouseName;

    @SerializedName("unit")
    private String unit;

    @SerializedName("categorys")
    private ArrayList<Category> category;

    @SerializedName("suppliers")
    private ArrayList<Supplier> supplier;

    @SerializedName("product_name")
    private String productName;

    @SerializedName("describe")
    private String describe;

    @SerializedName("product_code")
    private String productCode;

    @SerializedName("product_number")
    private String productNumber;

    @SerializedName("product_name_pinyin")
    private String pinyin;

    @SerializedName("product_name_py")
    private String pyInitials;

    @SerializedName("product_id")
    private int productId;

    @SerializedName("sku_list")
    private ArrayList<SkuInner> skuList;

    @SerializedName("total_quantity")
    private String totalQuantity;

    @SerializedName("price_range")
    private String priceRange;

    @SerializedName("inventory_list")
    private ArrayList<SKU> inventoryList;

    private class SkuInner{
        @SerializedName("sku_key_id")
        private int skuKeyId;

        @SerializedName("sku_key_name")
        private int skuKeyName;

        @SerializedName("sku_value_list")
        private SkuValue skuValueList;

        private class SkuValue{
            @SerializedName("sku_value_id")
            private int skuValueId;

            @SerializedName("sku_value_name")
            private int skuValueName;
        }

    }
}
