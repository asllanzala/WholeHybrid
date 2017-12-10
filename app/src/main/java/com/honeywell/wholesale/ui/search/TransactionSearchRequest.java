package com.honeywell.wholesale.ui.search;

import com.google.gson.annotations.SerializedName;

/**
 * Created by xiaofei on 9/12/16.
 *
 */
public class TransactionSearchRequest {

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

    public TransactionSearchRequest(String shopId, String startTime, String pageLength,
                                    String searchString, String orderBy, String descOrder) {
        this.shopId = shopId;
        this.startTime = startTime;
        this.pageLength = pageLength;
        this.searchString = searchString;
        this.orderBy = orderBy;
        this.descOrder = descOrder;
    }

    public TransactionSearchRequest(String shopId, String startTime, String searchString, String orderBy, int pageNumber) {
        this.shopId = shopId;
        this.startTime = startTime;
        this.searchString = searchString;
        this.orderBy = orderBy;
        this.pageNumber = pageNumber;
    }
}
