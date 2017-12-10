package com.honeywell.wholesale.ui.priceDiff.request;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by liuyang on 2017/6/7.
 */

public class PriceDiffOrderRequest {
    @SerializedName("shop_id")
    private int shopId;
    @SerializedName("total_price")
    private String totalPrice;
    @SerializedName("customer_name")
    private String customerName;
    @SerializedName("address")
    private String address;
    @SerializedName("contact_name")
    private String contactName;
    @SerializedName("contact_phone")
    private String contactPhone;
    @SerializedName("customer_id")
    private int customerId;
    @SerializedName("remark")
    private String remark;
    @SerializedName("sale_list")
    private ArrayList<Sale> saleList;

    public PriceDiffOrderRequest() {
    }

    public PriceDiffOrderRequest(int shopId, String totalPrice, String customerName, int customerId, String remark) {
        this.shopId = shopId;
        this.totalPrice = totalPrice;
        this.customerName = customerName;
        this.customerId = customerId;
        this.remark = remark;
    }

    public PriceDiffOrderRequest(int shopId, String totalPrice, String customerName, int customerId) {
        this.shopId = shopId;
        this.totalPrice = totalPrice;
        this.customerName = customerName;
        this.customerId = customerId;
    }

    public PriceDiffOrderRequest(int shopId, String totalPrice, String customerName, String address,
                                 String contactName, String contactPhone, int customerId) {
        this.shopId = shopId;
        this.totalPrice = totalPrice;
        this.customerName = customerName;
        this.address = address;
        this.contactName = contactName;
        this.contactPhone = contactPhone;
        this.customerId = customerId;
    }

    public PriceDiffOrderRequest(int shopId, String totalPrice, String customerName, String address,
                                 String contactName, String contactPhone, int customerId, ArrayList<Sale> saleList) {
        this.shopId = shopId;
        this.totalPrice = totalPrice;
        this.customerName = customerName;
        this.address = address;
        this.contactName = contactName;
        this.contactPhone = contactPhone;
        this.customerId = customerId;
        this.saleList = saleList;
    }

    public static class Sale{
        @SerializedName("product_id")
        private int productId;
        @SerializedName("product_name")
        private String productName;
        @SerializedName("sale_sku_list")
        private ArrayList<SaleSku> saleSkuList;

        public Sale() {
        }

        public Sale(int productId, String productName) {
            this.productId = productId;
            this.productName = productName;
        }

        public Sale(int productId, String productName, ArrayList<SaleSku> saleSkuList) {
            this.productId = productId;
            this.productName = productName;
            this.saleSkuList = saleSkuList;
        }

        public int getProductId() {
            return productId;
        }

        public void setProductId(int productId) {
            this.productId = productId;
        }

        public String getProductName() {
            return productName;
        }

        public void setProductName(String productName) {
            this.productName = productName;
        }

        public ArrayList<SaleSku> getSaleSkuList() {
            return saleSkuList;
        }

        public void setSaleSkuList(ArrayList<SaleSku> saleSkuList) {
            this.saleSkuList = saleSkuList;
        }
    }

    public static class SaleSku{
        @SerializedName("inventory_id")
        private int inventoryId;
        @SerializedName("fill_price")
        private String fillPrice;
        @SerializedName("quantity")
        private String quantity;
        @SerializedName("warehouse_sku_list")
        private ArrayList<WarehouseSku> warehouseSkuList;

        public SaleSku() {
        }

        public SaleSku(int inventoryId, String fillPrice, String quantity) {
            this.inventoryId = inventoryId;
            this.fillPrice = fillPrice;
            this.quantity = quantity;
        }

        public SaleSku(int inventoryId, String fillPrice, String quantity, ArrayList<WarehouseSku> warehouseSkuList) {
            this.inventoryId = inventoryId;
            this.fillPrice = fillPrice;
            this.quantity = quantity;
            this.warehouseSkuList = warehouseSkuList;
        }
    }

    public static class WarehouseSku{
        @SerializedName("warehouse_id")
        private int warehouseId;
        @SerializedName("warehouse_quantity")
        private String warehouseQuantity;

        public WarehouseSku() {
        }

        public WarehouseSku(int warehouseId, String warehouseQuantity) {
            this.warehouseId = warehouseId;
            this.warehouseQuantity = warehouseQuantity;
        }
    }


    public int getShopId() {
        return shopId;
    }

    public void setShopId(int shopId) {
        this.shopId = shopId;
    }

    public String getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(String totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public ArrayList<Sale> getSaleList() {
        return saleList;
    }

    public void setSaleList(ArrayList<Sale> saleList) {
        this.saleList = saleList;
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
