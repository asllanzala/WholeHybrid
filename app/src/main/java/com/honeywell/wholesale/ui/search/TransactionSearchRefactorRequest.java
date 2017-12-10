package com.honeywell.wholesale.ui.search;

import com.google.gson.annotations.SerializedName;

/**
 * Created by xiaofei on 10/24/16.
 */

public class TransactionSearchRefactorRequest {
    @SerializedName("shop_id")
    private String shopId;

    @SerializedName("start_time")
    private String startTime;

    @SerializedName("page_length")
    private String pageLength = "50";

    @SerializedName("search_string")
    private String searchString;

    @SerializedName("order_by")
    private String orderBy;

    @SerializedName("desc_order")
    private String descOrder = "false";

    @SerializedName("page_number")
    private int pageNumber;

    @SerializedName("payment")
    private String payMent;

    @SerializedName("order_status")
    private String orderStatus;

    @SerializedName("end_time")
    private String endTime="";

    public TransactionSearchRefactorRequest(String shopId, String startTime, String pageLength,
                                            String searchString, String orderBy, String descOrder,
                                            int pageNumber, String payMent, String orderStatus) {
        this.shopId = shopId;
        this.startTime = startTime;
        this.pageLength = pageLength;
        this.searchString = searchString;
        this.orderBy = orderBy;
        this.descOrder = descOrder;
        this.pageNumber = pageNumber;
        this.payMent = payMent;
        this.orderStatus = orderStatus;
    }

    public TransactionSearchRefactorRequest(String shopId, String startTime, String searchString,
                                            String orderBy, int pageNumber,
                                            String payMent, String orderStatus) {
        this.shopId = shopId;
        this.startTime = startTime;
        this.searchString = searchString;
        this.orderBy = orderBy;
        this.pageNumber = pageNumber;
        this.payMent = payMent;
        this.orderStatus = orderStatus;
    }


    public TransactionSearchRefactorRequest(String shopId, String searchString,
                                            String orderBy, int pageNumber,
                                            String payMent, String orderStatus) {
        this.shopId = shopId;
        this.searchString = searchString;
        this.orderBy = orderBy;
        this.pageNumber = pageNumber;
        this.payMent = payMent;
        this.orderStatus = orderStatus;
    }

}
