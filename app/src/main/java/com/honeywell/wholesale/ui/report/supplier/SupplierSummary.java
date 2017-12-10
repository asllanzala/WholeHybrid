package com.honeywell.wholesale.ui.report.supplier;

import com.google.gson.annotations.SerializedName;

/**
 * Created by xiaofei on 9/26/16.
 *
 */
public class SupplierSummary {

    @SerializedName("buy_amount")
    private String buyAmount;

    @SerializedName("buy_ratio")
    private Float buyRatio;

    public String getBuyAmount() {
        return buyAmount;
    }

    public Float getBuyRatio() {
        return buyRatio;
    }
}
