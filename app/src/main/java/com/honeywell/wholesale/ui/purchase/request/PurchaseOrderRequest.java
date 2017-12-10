package com.honeywell.wholesale.ui.purchase.request;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.honeywell.wholesale.framework.model.ExtraCost;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by liuyang on 2017/6/7.
 */

public class PurchaseOrderRequest {
    @SerializedName("shop_id")
    private int shopId;
    @SerializedName("total_price")
    private String totalPrice;
    @SerializedName("discount")
    private String discount;
    @SerializedName("supplier_company_name")
    private String supplierCompanyName;
    @SerializedName("supplier_id")
    private int supplierId;
    @SerializedName("supplier_name")
    private String supplierName;
    @SerializedName("address")
    private String address;
    @SerializedName("contact_name")
    private String contactName;
    @SerializedName("contact_phone")
    private String contactPhone;
    @SerializedName("invoice_title")
    private String invoiceTitla;
    @SerializedName("remark")
    private String remark;
    @SerializedName("buy_list")
    private ArrayList<Buy> buyList;

    public PurchaseOrderRequest() {
    }

    public PurchaseOrderRequest(int shopId, String totalPrice, String supplierName, int supplierId, String remark) {
        this.shopId = shopId;
        this.totalPrice = totalPrice;
        this.supplierName = supplierName;
        this.supplierId = supplierId;
        this.remark = remark;
    }

    public PurchaseOrderRequest(int shopId, String totalPrice, String supplierName, int supplierId) {
        this.shopId = shopId;
        this.totalPrice = totalPrice;
        this.supplierName = supplierName;
        this.supplierId = supplierId;
    }

    public PurchaseOrderRequest(int shopId, String totalPrice, String supplierName, String address,
                                String contactName, String contactPhone, int supplierId) {
        this.shopId = shopId;
        this.totalPrice = totalPrice;
        this.supplierName = supplierName;
        this.address = address;
        this.contactName = contactName;
        this.contactPhone = contactPhone;
        this.supplierId = supplierId;
    }

    public PurchaseOrderRequest(int shopId, String totalPrice, String supplierName, String address,
                                String contactName, String contactPhone, int supplierId, ArrayList<Buy> buyList) {
        this.shopId = shopId;
        this.totalPrice = totalPrice;
        this.supplierName = supplierName;
        this.address = address;
        this.contactName = contactName;
        this.contactPhone = contactPhone;
        this.supplierId = supplierId;
        this.buyList = buyList;
    }

    public static class Buy{
        @SerializedName("product_id")
        private int productId;
        @SerializedName("product_name")
        private String productName;
        @SerializedName("buy_sku_list")
        private ArrayList<BuySku> buySkuList;

        public Buy() {
        }

        public Buy(int productId, String productName) {
            this.productId = productId;
            this.productName = productName;
        }

        public Buy(int productId, String productName, ArrayList<BuySku> buySkuList) {
            this.productId = productId;
            this.productName = productName;
            this.buySkuList = buySkuList;
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

        public ArrayList<BuySku> getBuySkuList() {
            return buySkuList;
        }

        public void setBuySkuList(ArrayList<BuySku> buySkuList) {
            this.buySkuList = buySkuList;
        }
    }

    public static class BuySku{
        @SerializedName("inventory_id")
        private int inventoryId;
        @SerializedName("price")
        private String price;
        @SerializedName("quantity")
        private String quantity;
        @SerializedName("warehouse_id")
        private int warehouseId;
        @SerializedName("unit")
        private String unit;
        @SerializedName("batch_id")
        private String batchId;
        @SerializedName("batch_name")
        private String batchName;
        @SerializedName("toatlpremium")
        private String toatlpremium;
        @SerializedName("premium_list")
        private ArrayList<ExtraCost> premiumList;

        public BuySku() {
        }

        public BuySku(int inventoryId, String price, String quantity) {
            this.inventoryId = inventoryId;
            this.price = price;
            this.quantity = quantity;
        }

        public BuySku(int inventoryId, String price, String quantity, ArrayList<ExtraCost> premiumList) {
            this.inventoryId = inventoryId;
            this.price = price;
            this.quantity = quantity;
            this.premiumList = premiumList;
        }

        public BuySku(int inventoryId, String price, String quantity, int warehouseId, String unit, String batchId, String batchName, String toatlpremium, ArrayList<ExtraCost> premiumList) {
            this.inventoryId = inventoryId;
            this.price = price;
            this.quantity = quantity;
            this.warehouseId = warehouseId;
            this.unit = unit;
            this.batchId = batchId;
            this.batchName = batchName;
            this.toatlpremium = toatlpremium;
            this.premiumList = premiumList;
        }

        public ArrayList<ExtraCost> getPremiumList() {
            return premiumList;
        }

        public void setPremiumList(ArrayList<ExtraCost> premiumList) {
            this.premiumList = premiumList;
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

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
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

    public int getSupplierIdId() {
        return supplierId;
    }

    public void setSupplierIdId(int supplierId) {
        this.supplierId = supplierId;
    }

    public ArrayList<Buy> getSaleList() {
        return buyList;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public void setSaleList(ArrayList<Buy> buyList) {
        this.buyList = buyList;
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
