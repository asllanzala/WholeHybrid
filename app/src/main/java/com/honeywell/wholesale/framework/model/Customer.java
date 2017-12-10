package com.honeywell.wholesale.framework.model;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

/**
 * Created by xiaofei on 8/25/16.
 *
 */
public class Customer implements BaseModel{

    public Customer() {
    }

    public Customer(String customeId, String customerName, String shopId) {
        this.customeId = customeId;
        this.customerName = customerName;
        this.shopId = shopId;
    }

    public Customer(String customeId, String contactName, String shopId,
                    String contactPhone, String address, String customerName,
                    String invoiceTitle, String memo, String pinyin,
                    String initials) {
        this.customeId = customeId;
        this.contactName = contactName;
        this.shopId = shopId;
        this.contactPhone = contactPhone;
        this.address = address;
        this.customerName = customerName;
        this.invoiceTitle = invoiceTitle;
        this.memo = memo;
        this.pinyin = pinyin;
        this.initials = initials;
    }

    @SerializedName("customer_id")
    private String customeId = "";

    @SerializedName("contact_name")
    private String contactName = "";

    @SerializedName("shop_id")
    private String shopId = "";

    @SerializedName("contact_phone")
    private String contactPhone = "";

    @SerializedName("address")
    private String address = "";

    @SerializedName("customer_name")
    private String customerName = "";

    @SerializedName("invoice_title")
    private String invoiceTitle = "";

    //备注
    @SerializedName("memo")
    private String memo = "";

    @SerializedName("customer_name_pinyin")
    private String pinyin = "";
    @SerializedName("customer_name_py")
    private String initials = "";

    private String group = "";


    public String getCustomeId() {
        return customeId;
    }

    public String getContactName() {
        return contactName;
    }

    public String getShopId() {
        return shopId;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public String getAddress() {
        return address;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getInvoiceTitle() {
        return invoiceTitle;
    }

    public String getMemo() {
        return memo;
    }

    public String getPinyin() {
        return pinyin;
    }

    public String getInitials() {
        return initials;
    }

    public void setShopId(String shopId) {
        this.shopId = shopId;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getJsonString() {
        return new Gson().toJson(this);
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

}
