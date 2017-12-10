package com.honeywell.wholesale.ui.report.supplier;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by xiaofei on 9/22/16.
 *
 */

public class SupplierReportResponse<T> {

    @SerializedName("top_suppliers")
    private ArrayList<T> supplierList;

    public ArrayList<T> getSupplierList() {
        return supplierList;
    }

    public SupplierSummary getSupplierSummary() {
        return supplierSummary;
    }

    @SerializedName("summary")
    private SupplierSummary supplierSummary;
}
