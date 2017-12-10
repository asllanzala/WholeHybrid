package com.honeywell.wholesale.ui.report.product;

import com.google.gson.annotations.SerializedName;

/**
 * Created by xiaofei on 9/20/16.
 *
 */
public class ProductChartEntity {

    @SerializedName("product_id")
    private String productId;

    @SerializedName("product_name")
    private String productName;

    @SerializedName("income_amount")
    private String income;

    @SerializedName("income_ratio")
    private String incomeRatio;

    @SerializedName("profit_amount")
    private String profit;

    @SerializedName("profit_ratio")
    private String profitRatio;

    @SerializedName("sale_quantity")
    private String saleQuantity;

    @SerializedName("inventory_quantity")
    private String inventoryQuantity;


    @SerializedName("pic_src")
    private String picSrc;

    public String getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }

    public String getIncome() {
        return income;
    }

    public String getIncomeRatio() {
        return incomeRatio;
    }

    public String getProfit() {
        return profit;
    }

    public String getProfitRatio() {
        return profitRatio;
    }

    public String getSaleQuantity() {
        return saleQuantity;
    }

    public String getInventoryQuantity() {
        return inventoryQuantity;
    }

    public String getPicSrc() {
        return picSrc;
    }
}
