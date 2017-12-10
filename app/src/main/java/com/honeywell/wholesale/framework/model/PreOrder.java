package com.honeywell.wholesale.framework.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by xiaofei on 4/24/17.
 * 预售单
 */

public class PreOrder {

    @SerializedName("preorder_id")
    private int preorderId;

    @SerializedName("shop_id")
    private int shopId;

    @SerializedName("preorder_customer")
    private PreOrderCustomer preCustomer;

    @SerializedName("preorder_employee")
    private PreOrderEmployee preEmployee;

    @SerializedName("total_price")
    private String totalPrice;

    @SerializedName("preorder_remarks")
    private String remarks;

    @SerializedName("created_time")
    private String createdTime;

    @SerializedName("preorder_product")
    private ArrayList<PreOrderProduct> productArrayList;

    public String getCustomerName(){
        return preCustomer.getCustomerName();
    }

    public int getCustomerId(){
        return preCustomer.getCustomerId();
    }

    public String getCartNumber(){
        return preCustomer.getCustomerName();
    }

    public int getPreorderId() {
        return preorderId;
    }

    public int getShopId() {
        return shopId;
    }

    public PreOrderCustomer getPreCustomer() {
        return preCustomer;
    }

    public PreOrderEmployee getPreEmployee() {
        return preEmployee;
    }

    public String getTotalPrice() {
        return totalPrice;
    }

    public String getRemarks() {
        return remarks;
    }

    public String getCreatedTime() {
        return createdTime;
    }

    public ArrayList<PreOrderProduct> getProductArrayList() {
        return productArrayList;
    }

    public void setProductArrayList(ArrayList<PreOrderProduct> productArrayList) {
        this.productArrayList = productArrayList;
    }

    public PreOrder() {
    }

    public PreOrder(int preorderId, int shopId, PreOrderCustomer preCustomer,
                    PreOrderEmployee preEmployee, String totalPrice, String remarks,
                    String createdTime, ArrayList<PreOrderProduct> productArrayList) {
        this.preorderId = preorderId;
        this.shopId = shopId;
        this.preCustomer = preCustomer;
        this.preEmployee = preEmployee;
        this.totalPrice = totalPrice;
        this.remarks = remarks;
        this.createdTime = createdTime;
        this.productArrayList = productArrayList;
    }


    public class PreOrderCustomer {
        @SerializedName("customer_id")
        private int customerId;

        @SerializedName("customer_name")
        private String customerName;

        public PreOrderCustomer(int customerId, String customerName) {
            this.customerId = customerId;
            this.customerName = customerName;
        }

        public int getCustomerId() {
            return customerId;
        }

        public String getCustomerName() {
            return customerName;
        }
    }

    public class PreOrderEmployee {
        @SerializedName("employee_id")
        private int employeeId;

        @SerializedName("employee_name")
        private int employeeName;

        public PreOrderEmployee(int employeeId, int employeeName) {
            this.employeeId = employeeId;
            this.employeeName = employeeName;
        }

        public int getEmployeeId() {
            return employeeId;
        }

        public int getEmployeeName() {
            return employeeName;
        }
    }

    public class PreOrderProduct {

        @SerializedName("product_id")
        private int productId;

        @SerializedName("product_code")
        private String productCode;

        @SerializedName("product_number")
        private String productNumber;

        @SerializedName("total_quantity")
        private String totalQuantity;

        @SerializedName("product_image_url")
        private String imageUrl;

        @SerializedName("product_name")
        private String productName;

        @SerializedName("product_sku")
        private ArrayList<PreOrderSKUProduct> skuProducts;

        public PreOrderProduct(int productId, String productCode, String imageUrl, String productName, ArrayList<PreOrderSKUProduct> skuProducts) {
            this.productId = productId;
            this.productCode = productCode;
            this.imageUrl = imageUrl;
            this.productName = productName;
            this.skuProducts = skuProducts;
        }

        public int getProductId() {
            return productId;
        }

        public void setProductId(int productId) {
            this.productId = productId;
        }

        public String getProductNumber() {
            return productNumber;
        }

        public void setProductNumber(String productNumber) {
            this.productNumber = productNumber;
        }

        public String getTotalQuantity() {
            return totalQuantity;
        }

        public void setTotalQuantity(String totalQuantity) {
            this.totalQuantity = totalQuantity;
        }

        public String getProductCode() {
            return productCode;
        }

        public void setProductCode(String productCode) {
            this.productCode = productCode;
        }

        public void setImageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
        }

        public void setProductName(String productName) {
            this.productName = productName;
        }

        public String getImageUrl() {
            return imageUrl;
        }

        public String getProductName() {
            return productName;
        }

        public ArrayList<PreOrderSKUProduct> getSkuProducts() {
            return skuProducts;
        }

        public void setSkuProducts(ArrayList<PreOrderSKUProduct> skuProducts) {
            this.skuProducts = skuProducts;
        }

    }

    public class PreOrderSKUProduct {
        @SerializedName("inventory_id")
        private int inventoryId;

        @SerializedName("sale_price")
        private int salePrice;

        @SerializedName("warehouse_sku_list")
        private ArrayList<SKUWareHouse> skuWareHouses;

        public int getInventoryId() {
            return inventoryId;
        }

        public int getSalePrice() {
            return salePrice;
        }

        public ArrayList<SKUWareHouse> getSkuWareHouses() {
            return skuWareHouses;
        }

        public void setSkuWareHouses(ArrayList<SKUWareHouse> skuWareHouses) {
            this.skuWareHouses = skuWareHouses;
        }
    }

    public class SKUWareHouse {
        @SerializedName("warehouse_inventory_id")
        private int wareHouseInventoryId;

        @SerializedName("quantity")
        private int quantity;

        public int getWareHouseInventoryId() {
            return wareHouseInventoryId;
        }

        public int getQuantity() {
            return quantity;
        }

        public void setQuantity(int quantity) {
            this.quantity = quantity;
        }
    }
}
