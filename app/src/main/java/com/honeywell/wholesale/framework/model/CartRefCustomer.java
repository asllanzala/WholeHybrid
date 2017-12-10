package com.honeywell.wholesale.framework.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by xiaofei on 3/20/17.
 *
 */

public class CartRefCustomer {

    @SerializedName("employee_id")
    private String employeeId;

    @SerializedName("user_name")
    private String userName;

    @SerializedName("shop_id")
    private String shopId;

    @SerializedName("shop_name")
    private String shopName;

    @SerializedName("customer_id")
    private String customerId;

    @SerializedName("customer_name")
    private String customerName;

    @SerializedName("contact_name")
    private String contactName;

    @SerializedName("contact_phone")
    private String contactPhone;

    @SerializedName("warehouse_id")
    private String warehouseId;

    @SerializedName("warehouse_name")
    private String warehouseName;

    @SerializedName("customer_notes")
    private String customerNotes;

    @SerializedName("invoice_name")
    private String invoiceName;

    @SerializedName("address")
    private String address;

    private String uuid;

    public CartRefCustomer() {
    }

    public CartRefCustomer(String employeeId, String userName, String shopId, String shopName,
                           String customerId, String customerName, String contactName, String contactPhone,
                           String address, String warehouseId, String warehouseName) {
        this.employeeId = employeeId;
        this.userName = userName;
        this.shopId = shopId;
        this.shopName = shopName;
        this.customerId = customerId;
        this.customerName = customerName;
        this.contactName = contactName;
        this.contactPhone = contactPhone;
        this.address = address;
        this.warehouseId = warehouseId;
        this.warehouseName = warehouseName;
    }

    public CartRefCustomer(String employeeId, String userName, String shopId, String shopName,
                           String customerId, String customerName, String contactName, String contactPhone,
                           String customerNotes, String invoiceName, String address, String warehouseId, String warehouseName) {
        this.employeeId = employeeId;
        this.userName = userName;
        this.shopId = shopId;
        this.shopName = shopName;
        this.customerId = customerId;
        this.customerName = customerName;
        this.contactName = contactName;
        this.contactPhone = contactPhone;
        this.customerNotes = customerNotes;
        this.invoiceName = invoiceName;
        this.address = address;
        this.warehouseId = warehouseId;
        this.warehouseName = warehouseName;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public String getUserName() {
        return userName;
    }

    public String getShopId() {
        return shopId;
    }

    public String getShopName() {
        return shopName;
    }

    public String getCustomerId() {
        return customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getContactName() {
        return contactName;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public String getCustomerNotes() {
        return customerNotes;
    }

    public String getInvoiceName() {
        return invoiceName;
    }

    public String getAddress() {
        return address;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getWarehouseId() {
        return warehouseId;
    }

    public void setWarehouseId(String warehouseId) {
        this.warehouseId = warehouseId;
    }

    public String getWarehouseName() {
        return warehouseName;
    }

    public void setWarehouseName(String warehouseName) {
        this.warehouseName = warehouseName;
    }
}
