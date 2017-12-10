package com.honeywell.wholesale.ui.report.customer;

import com.google.gson.annotations.SerializedName;

/**
 * Created by xiaofei on 9/26/16.
 */
public class CustomerSummary {

    @SerializedName("income_amount")
    private String incomeAmount;

    @SerializedName("income_ratio")
    private Float incomeRatio;

    @SerializedName("profit_amount")
    private String profitAmount;

    @SerializedName("profit_ratio")
    private Float profitRatio;

    public String getIncomeAmount() {
        return incomeAmount;
    }

    public Float getIncomeRatio() {
        return incomeRatio;
    }

    public String getProfitAmount() {
        return profitAmount;
    }

    public Float getProfitRatio() {
        return profitRatio;
    }
}
