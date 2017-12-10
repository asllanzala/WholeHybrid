package com.honeywell.wholesale.framework.model;

import com.google.gson.Gson;

import java.util.ArrayList;

/**
 * Created by H154326 on 17/3/30.
 * Email: yang.liu6@honeywell.com
 */

public class PreCartOrder {
    private final static String TAG = "CartItem";

    private String employeeId;
    private String userName;
    private String shopId;
    private String shopName;

    private String customerId;
    private String customerName;
    private String contactName;
    private String contactPhone;
    private String customerNotes;
    private String invoiceTitle;
    private String address;

    private ArrayList<CartRefSKU> cartRefSKUArrayList = new ArrayList<CartRefSKU>();
    private String saleListString;

    public PreCartOrder() {
    }

    public PreCartOrder(CartRefCustomer cartRefCustomer, ArrayList<CartRefSKU> cartRefSKUArrayList) {
        this.employeeId = cartRefCustomer.getEmployeeId();
        this.userName = cartRefCustomer.getUserName();
        this.shopId = cartRefCustomer.getShopId();
        this.shopName = cartRefCustomer.getShopName();
        this.customerId = cartRefCustomer.getCustomerId();
        this.customerName = cartRefCustomer.getCustomerName();
        this.contactName = cartRefCustomer.getContactName();
        this.contactPhone = cartRefCustomer.getContactPhone();
        this.customerNotes = cartRefCustomer.getCustomerNotes();
        this.invoiceTitle = "";
        this.address = cartRefCustomer.getAddress();
        this.cartRefSKUArrayList = cartRefSKUArrayList;
    }

    public PreCartOrder(String employeeId, String userName, String shopId, String shopName, String customerId,
                        String customerName, String contactName, String contactPhone, String customerNotes,
                        String invoiceTitle, String address,
                        String saleListString) {
        this.employeeId = employeeId;
        this.userName = userName;
        this.shopId = shopId;
        this.shopName = shopName;
        this.customerId = customerId;
        this.customerName = customerName;
        this.contactName = contactName;
        this.contactPhone = contactPhone;
        this.customerNotes = customerNotes;
        this.invoiceTitle = invoiceTitle;
        this.address = address;
        this.saleListString = saleListString;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
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

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
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

    public String getCustomerNotes() {
        return customerNotes;
    }

    public void setCustomerNotes(String customerNotes) {
        this.customerNotes = customerNotes;
    }

    public String getInvoiceTitle() {
        return invoiceTitle;
    }

    public void setInvoiceTitle(String invoiceTitle) {
        this.invoiceTitle = invoiceTitle;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }


    public String getSkuString() {
        return saleListString;
    }

    public void setSkuString(String skuString) {
        this.saleListString = skuString;
    }
}
