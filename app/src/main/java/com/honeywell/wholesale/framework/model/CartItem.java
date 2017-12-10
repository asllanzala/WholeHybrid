package com.honeywell.wholesale.framework.model;

import android.content.Intent;
import android.support.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by H155935 on 16/6/21.
 * Email: xiaofei.he@honeywell.com
 */
public class CartItem {
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

    private int productId;
    private String productCode;
    private String productNumber;
    private String productName;
    private String unitPrice;
    private String stockQuantity;

    private String totalNumber;
    private String imageUrl;
    private String unit;

    public CartItem(JSONObject jsonObject) {
        fromJson(jsonObject);
    }

    public CartItem(String employeeId, String userName, @NonNull String shopId, String shopName, @NonNull String customerId,
                    String customerName,
                    String contactName, String contactPhone, String customerNotes, String invoiceTitle, String address, String stockQuantity){

        this.employeeId = employeeId;
        this.userName = userName;
        this.shopId = shopId;
        this.shopName = shopName;

        this.contactName = contactName;
        this.contactPhone = contactPhone;
        this.customerId = customerId;
        this.customerName = customerName;
        this.customerNotes = customerNotes;
        this.invoiceTitle = invoiceTitle;
        this.address = address;
        this.stockQuantity = stockQuantity;
    }

    public CartItem(String employeeId, String userName, @NonNull String shopId, String shopName, @NonNull String customerId, String customerName,
                    String contactName, String contactPhone, String customerNotes, String invoiceTitle, String address,
                    int productId, String productName, String unitPrice,
                    String totalNumber, String imageUrl, String unit, String productCode, String productNumber, String stockQuantity){
        this.employeeId = employeeId;
        this.userName = userName;
        this.shopId = shopId;
        this.shopName = shopName;

        this.contactName = contactName;
        this.contactPhone = contactPhone;
        this.customerId = customerId;
        this.customerName = customerName;
        this.customerNotes = customerNotes;
        this.invoiceTitle = invoiceTitle;
        this.address = address;

        this.imageUrl = imageUrl;
        this.productId = productId;
        this.productName = productName;
        this.totalNumber = totalNumber;
        this.unit = unit;
        this.unitPrice = unitPrice;
        this.productCode = productCode;
        this.productNumber = productNumber;
        this.stockQuantity = stockQuantity;
    }

    public CartItem(String employeeId, String userName, String shopId, String shopName, String customerId, String customerName, String contactName,
                    String contactPhone, String invoiceTitle, String address, int productId,
                    String productName, String unitPrice, String imageUrl, String unit, String productCode, String productNumber, String stockQuantity) {
        this.employeeId = employeeId;
        this.userName = userName;
        this.shopId = shopId;
        this.shopName = shopName;
        this.customerId = customerId;
        this.customerName = customerName;
        this.contactName = contactName;
        this.contactPhone = contactPhone;
        this.invoiceTitle = invoiceTitle;
        this.address = address;
        this.productId = productId;
        this.productName = productName;
        this.unitPrice = unitPrice;
        this.imageUrl = imageUrl;
        this.unit = unit;
        this.productCode = productCode;
        this.productNumber = productNumber;
        this.stockQuantity = stockQuantity;
    }

    public String getShopId() {
        return shopId;
    }

    public String getShopName() {
        return shopName;
    }

    public String getEmployeeId() {
        return employeeId;
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

    public String getInvoiceTitle() {
        return invoiceTitle;
    }

    public int getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }

    public String getUnitPrice() {
        return unitPrice;
    }

    public String getTotalNumber() {
        return totalNumber;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getFirstImageUrl() {
        String[] ImageUrl = getImageUrl().split(",");
        String firstImageUrl1 = ImageUrl[0].replaceAll("\\[","");
        String firstImageUrl2 = firstImageUrl1.replaceAll("\\]","");
        String firstImageUrl3 = firstImageUrl2.replaceAll("\"","");
        String firstImageUrl = firstImageUrl3.replaceAll("\\\\","");
        return firstImageUrl;
    }

    public String getUnit() {
        return unit;
    }

    public String getCustomerNotes() {
        return customerNotes;
    }

    public String getUserName() {
        return userName;
    }

    public String getAddress() {
        return address;
    }

    public JSONObject toJsonObject() {
        JSONObject cartJsonObject = new JSONObject();
        try {
            cartJsonObject.put("employeeId", employeeId);
            cartJsonObject.put("userName", userName);
            cartJsonObject.put("shopId", shopId);
            cartJsonObject.put("shopName", shopName);

            cartJsonObject.put("customerId", customerId);
            cartJsonObject.put("customerName", customerName);
            cartJsonObject.put("contactName", contactName);
            cartJsonObject.put("contactPhone", contactPhone);
            cartJsonObject.put("customerNotes", customerNotes);
            cartJsonObject.put("invoiceTitle", invoiceTitle);
            cartJsonObject.put("address", address);

            cartJsonObject.put("productId", productId);
            cartJsonObject.put("productName", productName);
            cartJsonObject.put("unitPrice", unitPrice);
            cartJsonObject.put("totalNumber", totalNumber);
            cartJsonObject.put("imageUrl", imageUrl);
            cartJsonObject.put("unit", unit);
            cartJsonObject.put("stockQuantity", stockQuantity);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return cartJsonObject;
    }

    public String toJsonString() {
        return toJsonObject().toString();
    }

    public void fromJson(JSONObject requestBodyJsonObject) {
        employeeId = requestBodyJsonObject.optString("employeeId");
        userName = requestBodyJsonObject.optString("userName");
        shopId = requestBodyJsonObject.optString("shopId");
        shopName = requestBodyJsonObject.optString("shopName");

        customerId = requestBodyJsonObject.optString("customerId");
        customerName = requestBodyJsonObject.optString("customerName");
        contactName = requestBodyJsonObject.optString("contactName");
        contactPhone = requestBodyJsonObject.optString("contactPhone");
        customerNotes = requestBodyJsonObject.optString("customerNotes");
        invoiceTitle = requestBodyJsonObject.optString("invoiceTitle");
        address = requestBodyJsonObject.optString("address");

        productId = requestBodyJsonObject.optInt("productId");
        productCode =requestBodyJsonObject.optString("productCode");
        productNumber = requestBodyJsonObject.optString("productNumber");
        productName = requestBodyJsonObject.optString("productName");
        unitPrice = requestBodyJsonObject.optString("unitPrice");
        totalNumber = requestBodyJsonObject.optString("totalNumber");
        imageUrl = requestBodyJsonObject.optString("imageUrl");
        unit = requestBodyJsonObject.optString("unit");
        stockQuantity = requestBodyJsonObject.optString("stockQuantity");
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public void setTotalNumber(String totalNumber) {
        this.totalNumber = totalNumber;
    }

    public String getProductNumber() {
        return productNumber;
    }

    public void setProductNumber(String productNumber) {
        this.productNumber = productNumber;
    }

    public String getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(String stockQuantity) {
        this.stockQuantity = stockQuantity;
    }
}

