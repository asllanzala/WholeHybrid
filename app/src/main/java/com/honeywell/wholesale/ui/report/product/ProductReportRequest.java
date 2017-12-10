package com.honeywell.wholesale.ui.report.product;

import com.google.gson.annotations.SerializedName;
import com.honeywell.wholesale.framework.utils.WSTimeStamp;

/**
 * Created by xiaofei on 9/20/16.
 *
 */
public class ProductReportRequest {
    @SerializedName("shop_id")
    private String shopId;

    @SerializedName("start_time")
    private String startTime;

    //WSTimeStamp.getSpecialDayEndTime(-1);
    @SerializedName("end_time")
    private String endTime = "";

    //enum:  "income", "profit", "sale_quantity", 分别对应销售额、利润、销量
    @SerializedName("top_condition")
    private TopCondition topCondition = TopCondition.INCOME;

    @SerializedName("top_number")
    private String topNumber = "5";

    public ProductReportRequest(String shopId, String startTime) {
        this.shopId = shopId;
        this.startTime = startTime;
    }

    public ProductReportRequest(String shopId, String startTime, TopCondition topCondition) {
        this.shopId = shopId;
        this.startTime = startTime;
        this.topCondition = topCondition;
    }

    public enum TopCondition {
        INCOME("income"),
        PROFIT("profit"),
        SALES("sale_quantity")
        ;

        private final String text;

        /**
         * @param text
         */
        private TopCondition(final String text) {
            this.text = text;
        }
        @Override
        public String toString() {
            return text;
        }
    }

    public String getShopId() {
        return shopId;
    }

    public void setShopId(String shopId) {
        this.shopId = shopId;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public TopCondition getTopCondition() {
        return topCondition;
    }

    public void setTopCondition(TopCondition topCondition) {
        this.topCondition = topCondition;
    }

    public String getTopNumber() {
        return topNumber;
    }

    public void setTopNumber(String topNumber) {
        this.topNumber = topNumber;
    }
}
