package com.honeywell.wholesale.framework.model;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

/**
 * Created by xiaofei on 8/25/16.
 *
 */
public class Supplier implements BaseModel{

    public Supplier() {
    }

    public Supplier(String supplierId, String contactName, String shopId) {
        this.supplierId = supplierId;
        this.contactName = contactName;
        this.shopId = shopId;
    }

    public Supplier(String supplierId, String contactName, String shopId,
                    String contactPhone, String supplierName, String descMemo,
                    String bankInfo, String address, String pinyin,
                    String initials) {
        this.supplierId = supplierId;
        this.contactName = contactName;
        this.shopId = shopId;
        this.contactPhone = contactPhone;
        this.supplierName = supplierName;
        this.descMemo = descMemo;
        this.bankInfo = bankInfo;
        this.address = address;
        this.pinyin = pinyin;
        this.initials = initials;
    }

    @SerializedName("supplier_id")
    private String supplierId;

    @SerializedName("contact_phone")
    private String contactPhone;

    @SerializedName("supplier_name")
    private String supplierName;

    @SerializedName("desc_memo")
    private String descMemo;

    @SerializedName("bank_info")
    private String bankInfo;

    @SerializedName("address")
    private String address;

    @SerializedName("contact_name")
    private String contactName;

    @SerializedName("supplier_name_pinyin")
    private String pinyin;
    @SerializedName("supplier_name_py")
    private String initials;

    private String shopId;

    public String getContactName() {
        return contactName;
    }

    public String getShopId() {
        return shopId;
    }

    public void setShopId(String shopId) {
        this.shopId = shopId;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public String getDescMemo() {
        return descMemo;
    }

    public String getBankInfo() {
        return bankInfo;
    }

    public String getAddress() {
        return address;
    }

    public String getPinyin() {
        return pinyin;
    }

    public String getInitials() {
        return initials;
    }

    public String getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(String supplierId) {
        this.supplierId = supplierId;
    }

    public String getJsonString() {
        return new Gson().toJson(this);
    }

}
