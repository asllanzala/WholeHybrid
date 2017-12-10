package com.honeywell.wholesale.ui.report.customer;

import com.google.gson.annotations.SerializedName;

/**
 * Created by xiaofei on 9/22/16.
 *
 */
public class CustomerChartEntity {

    @SerializedName("customer_id")
    private String customerId;

    @SerializedName("customer_name")
    private String customerName;

    @SerializedName("contact_name")
    private String contactName;

    @SerializedName("income_amount")
    private String incomeAmount;

    @SerializedName("income_ratio")
    private String incomeRatio;

    @SerializedName("profit_amount")
    private String profitAmount;

    @SerializedName("profit_ratio")
    private String profitRatio;

    @SerializedName("receivable")
    private String receivable;

    public String getCustomerId() {
        return customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getContactName() {
        return contactName;
    }

    public String getIncomeAmount() {
        return incomeAmount;
    }

    public String getIncomeRatio() {
        return incomeRatio;
    }

    public String getProfitAmount() {
        return profitAmount;
    }

    public String getProfitRatio() {
        return profitRatio;
    }

    public String getReceivable() {
        return receivable;
    }
}
