package com.honeywell.wholesale.ui.newreport.network;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.honeywell.wholesale.framework.utils.WSTimeStamp;

/**
 * Created by xiaofei on 9/20/16.
 *
 */
public class IncomeReportRequest {

    @SerializedName("shop_id")
    private String shopId;

    @SerializedName("start_time")
    private String startTime;

    @SerializedName("end_time")
    private String endTime = null;

    /**
     * Endtime不传的时候,默认到当前时间
     * @param shopId
     * @param startTime
     */
    public IncomeReportRequest(String shopId, String startTime) {
        this.shopId = shopId;
        this.startTime = startTime;
    }

    public IncomeReportRequest(String shopId, String startTime, String endTime) {
        this.shopId = shopId;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public String getJsonString() {
        return new Gson().toJson(this)
                + ", startTime=" + WSTimeStamp.getFullTimeString(Long.valueOf(startTime))
                + ", endTime=" + WSTimeStamp.getFullTimeString(Long.valueOf(endTime));
    }


}
