package com.honeywell.wholesale.ui.transaction;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by xiaofei on 10/13/16.
 *
 * 销售查询：/transaction/sale/search, 增加过滤条件分别搜索：
 * 已完成(order_status==1)
 * 已取消(order_status==100)
 * 未支付（payment=0 and order_status=0）
 * 赊账中(payment=2 and order_status=0)
 */

public class TransactionQueryRequest {

    @SerializedName("shop_id")
    private String shopId;

    @SerializedName("start_time")
    private String startTime = "";

    @SerializedName("end_time")
    private String endTime = "";

    @SerializedName("page_length")
    private String pageLength = "50";

    @SerializedName("search_string")
    private String searchString = "";

    @SerializedName("page_number")
    private String pageNumber;

    @SerializedName("order_by")
    private String orderBy;

    @SerializedName("desc_order")
    private String descOrder = "false";

    @SerializedName("payment")
    private String payment;

    @SerializedName("order_status")
    private String orderStatus;


    public TransactionQueryRequest() {
    }

    public TransactionQueryRequest(String shopId, String startTime, String endTime, String pageLength,
                                   String searchString, String pageNumber, String orderBy, String descOrder,
                                   String payment, String orderStatus) {
        this.shopId = shopId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.pageLength = pageLength;
        this.searchString = searchString;
        this.pageNumber = pageNumber;
        this.orderBy = orderBy;
        this.descOrder = descOrder;
        this.payment = payment;
        this.orderStatus = orderStatus;
    }

    public TransactionQueryRequest(String shopId, String pageNumber, String orderStatus,String descOrder) {
        this.shopId = shopId;
        this.pageNumber = pageNumber;
        this.orderStatus = orderStatus;
        this.descOrder = descOrder;
    }

    public TransactionQueryRequest(String shopId, String pageNumber, String orderBy,
                                    String payment, String orderStatus) {
        this.shopId = shopId;
        this.pageNumber = pageNumber;
        this.orderBy = orderBy;
        this.payment = payment;
        this.orderStatus = orderStatus;
    }

    public TransactionQueryRequest(String searchString, String shopId, int pageNumber, String orderBy,
                                   String payment, String orderStatus) {
        this.shopId = shopId;
        this.pageNumber = String.valueOf(pageNumber);
        this.orderBy = orderBy;
        this.payment = payment;
        this.orderStatus = orderStatus;
        this.searchString = searchString;
    }



    public TransactionQueryRequest(String orderStatus, String shopId,
                                   String pageNumber, String orderBy, String startTime, String descOrder) {
        this.orderStatus = orderStatus;
        this.shopId = shopId;
        this.pageNumber = pageNumber;
        this.orderBy = orderBy;
        this.startTime = startTime;
        this.descOrder = descOrder;
    }

    public String getJsonString() {
        return new Gson().toJson(this);
    }

    public JSONObject getJsonObject() {
        try {
            return new JSONObject(getJsonString());
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
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

    public String getPageLength() {
        return pageLength;
    }

    public void setPageLength(String pageLength) {
        this.pageLength = pageLength;
    }

    public String getSearchString() {
        return searchString;
    }

    public void setSearchString(String searchString) {
        this.searchString = searchString;
    }

    public String getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(String pageNumber) {
        this.pageNumber = pageNumber;
    }

    public String getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
    }

    public String getDescOrder() {
        return descOrder;
    }

    public void setDescOrder(String descOrder) {
        this.descOrder = descOrder;
    }

    public String getPayment() {
        return payment;
    }

    public void setPayment(String payment) {
        this.payment = payment;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }
}
