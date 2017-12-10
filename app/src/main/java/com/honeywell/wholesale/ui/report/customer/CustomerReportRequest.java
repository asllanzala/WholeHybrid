package com.honeywell.wholesale.ui.report.customer;

import com.google.gson.annotations.SerializedName;
import com.honeywell.wholesale.framework.utils.WSTimeStamp;

/**
 * Created by xiaofei on 9/20/16.
 *
 */
public class CustomerReportRequest {
    @SerializedName("shop_id")
    private String shopId;

    @SerializedName("start_time")
    private String startTime;

    //WSTimeStamp.getSpecialDayEndTime(-1);
    @SerializedName("end_time")
    private String endTime = "";

    //  enum:  "income", "profit", "receivable", 分别对应销售额、利润、销量
    @SerializedName("top_condition")
    private TopCondition topCondition = TopCondition.INCOME;

    @SerializedName("top_number")
    private String topNumber = "5";

    public CustomerReportRequest(String shopId, String startTime, String endTime,
                                 TopCondition topCondition, String topNumber) {
        this.shopId = shopId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.topCondition = topCondition;
        this.topNumber = topNumber;
    }

    public CustomerReportRequest(String shopId, String startTime) {
        this.shopId = shopId;
        this.startTime = startTime;
    }

    public CustomerReportRequest(String shopId, String startTime, String endTime, TopCondition topCondition) {
        this.shopId = shopId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.topCondition = topCondition;
    }

    public enum TopCondition {
        INCOME("income"),
        PROFIT("profit"),
        RECEIVE("receivable");

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
