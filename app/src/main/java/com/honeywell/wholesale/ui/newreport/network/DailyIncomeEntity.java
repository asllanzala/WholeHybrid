package com.honeywell.wholesale.ui.newreport.network;

import com.google.gson.annotations.SerializedName;

/**
 * Created by xiaofei on 9/20/16.
 *
 */
public class DailyIncomeEntity {

    @SerializedName("dt")
    private long timeStamp;

    @SerializedName("income")
    private float income;

    @SerializedName("profit")
    private float profit;

    @SerializedName("deals")
    private int deals;

    @SerializedName("receivable_create_amount")
    private float newOutstandings;

    @SerializedName("receivable_create_deal")
    private int newOutstandingBills;

    @SerializedName("receivable_confirm_amount")
    private float settleOutstandings;

    @SerializedName("receivable_confirm_deal")
    private int settleOutstandingBills;

    @SerializedName("customer_new")
    private int newCustomers;

    public float getProfit() {
        return profit;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public float getIncome() {
        return income;
    }

    public int getDeals() {
        return deals;
    }

    public float getNewOutstandings() {
        return newOutstandings;
    }

    public int getNewOutstandingBills() {
        return newOutstandingBills;
    }

    public float getSettleOutstandings() {
        return settleOutstandings;
    }

    public int getSettleOutstandingBills() {
        return settleOutstandingBills;
    }

    public int getNewCustomers() {
        return newCustomers;
    }

}
