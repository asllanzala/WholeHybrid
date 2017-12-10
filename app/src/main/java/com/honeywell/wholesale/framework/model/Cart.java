package com.honeywell.wholesale.framework.model;

import com.google.gson.Gson;
import com.honeywell.wholesale.framework.database.CartRefSKUDao;

/**
 * Created by H154326 on 16/11/29.
 * Email: yang.liu6@honeywell.com
 */

public class Cart {
    private final static String TAG = "Cart";

    private String cartUuid;

    private String employeeId;
    private String shopId;

    private String customerId;
    private String customerName;

    private int productId;
    private String productName;

    private String unitPrice;

    private String cartNumber;  //购物篮中每个customer的预售单数目

    private String contactPhone;
    private String contactName;


    public Cart() {
    }

    public Cart(CartRefCustomer cartRefCustomer, String amount){
        this.employeeId = cartRefCustomer.getEmployeeId();
        this.shopId = cartRefCustomer.getShopId();
        this.customerId = cartRefCustomer.getCustomerId();
        this.customerName = cartRefCustomer.getCustomerName();
        this.cartNumber = amount;
        this.contactPhone = cartRefCustomer.getContactPhone();
        this.contactName = cartRefCustomer.getContactName();
        this.cartUuid = cartRefCustomer.getUuid();
    }

    public Cart(String employeeId, String shopId, String customerId,
                String customerName, int productId, String productName,
                String unitPrice, String cartNumber, String contactPhone, String contactName, String cartUuid) {
        this.employeeId = employeeId;
        this.shopId = shopId;
        this.customerId = customerId;
        this.customerName = customerName;
        this.productId = productId;
        this.productName = productName;
        this.unitPrice = unitPrice;
        this.cartNumber = cartNumber;
        this.contactPhone = contactPhone;
        this.contactName = contactName;
        this.cartUuid = cartUuid;
    }

    public Cart(String employeeId, String shopId,
                String customerId, String customerName, int productId,
                String productName, String cartNumber, String contactPhone, String contactName, String cartUuid) {
        this.employeeId = employeeId;
        this.shopId = shopId;
        this.customerId = customerId;
        this.customerName = customerName;
        this.productId = productId;
        this.productName = productName;
        this.cartNumber = cartNumber;
        this.contactPhone = contactPhone;
        this.contactName = contactName;
        this.cartUuid = cartUuid;
    }

    public Cart(String employeeId, String shopId, String customerId, String customerName, String cartNumber,
                String contactPhone, String contactName,String cartUuid) {
        this.employeeId = employeeId;
        this.shopId = shopId;
        this.customerId = customerId;
        this.customerName = customerName;
        this.cartNumber = cartNumber;
        this.contactPhone = contactPhone;
        this.contactName = contactName;
        this.cartUuid = cartUuid;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public String getShopId() {
        return shopId;
    }

    public void setShopId(String shopId) {
        this.shopId = shopId;
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


    public String getCartNumber() {
        return cartNumber;
    }

    public void setCartNumber(String cartNumber) {
        this.cartNumber = cartNumber;
    }

    public String getJsonString() {
        return (new Gson()).toJson(this);
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public String getCartUuid() {
        return cartUuid;
    }

    public void setCartUuid(String cartUuid) {
        this.cartUuid = cartUuid;
    }
}
