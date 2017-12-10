package com.honeywell.wholesale.ui.report.product;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by xiaofei on 9/20/16.
 *
 */
public class ProductReportResponse<T> {
    public ArrayList<T> getProductsList() {
        return productsList;
    }

    @SerializedName("top_products")
    private ArrayList<T> productsList;

    public ProductSummary getProductSummary() {
        return productSummary;
    }

    @SerializedName("summary")
    private ProductSummary productSummary;
}
