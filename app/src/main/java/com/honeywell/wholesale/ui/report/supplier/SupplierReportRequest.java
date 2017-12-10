package com.honeywell.wholesale.ui.report.supplier;

import com.google.gson.annotations.SerializedName;
import com.honeywell.wholesale.framework.utils.WSTimeStamp;

/**
 * Created by xiaofei on 9/22/16.
 *
 */
public class SupplierReportRequest {

    @SerializedName("shop_id")
    private String shopId;

    @SerializedName("start_time")
    private String startTime;

    //WSTimeStamp.getSpecialDayEndTime(-1);
    @SerializedName("end_time")
    private String endTime = "";

    @SerializedName("top_condition")
    private TopCondition topCondition = TopCondition.AMOUNT;

    @SerializedName("top_number")
    private String topNumber = "5";

    public SupplierReportRequest(String shopId, String startTime) {
        this.shopId = shopId;
        this.startTime = startTime;
    }

    public enum TopCondition {
        AMOUNT("buy_amount");

        private final String text;

        /**
         * @param text
         */
        TopCondition(final String text) {
            this.text = text;
        }
        @Override
        public String toString() {
            return text;
        }
    }

    public String getTopNumber() {
        return topNumber;
    }

    public void setTopNumber(String topNumber) {
        this.topNumber = topNumber;
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
}
