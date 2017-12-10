package com.honeywell.wholesale.ui.report.customer;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by xiaofei on 9/22/16.
 *
 */
public class CustomerReportResponse<T> {

    public ArrayList<T> getCustomerList() {
        return customerList;
    }

    @SerializedName("top_customers")
    private ArrayList<T> customerList;

    @SerializedName("summary")
    private CustomerSummary customerSummary;

    public CustomerSummary getCustomerSummary() {
        return customerSummary;
    }
}
