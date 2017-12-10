package com.honeywell.wholesale.ui.report.supplier;

import com.google.gson.annotations.SerializedName;

/**
 * Created by xiaofei on 9/22/16.
 *
 */

public class SupplierChartEntity {

    @SerializedName("supplier_id")
    private String supplierId;

    @SerializedName("supplier_name")
    private String supplierName;

    @SerializedName("contact_name")
    private String contactName;

    @SerializedName("buy_amount")
    private String buyAmount;

    @SerializedName("buy_ratio")
    private String buyRatio;


    public String getSupplierId() {
        return supplierId;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public String getContactName() {
        return contactName;
    }

    public String getBuyAmount() {
        return buyAmount;
    }

    public String getBuyRatio() {
        return buyRatio;
    }
}
