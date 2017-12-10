package com.honeywell.wholesale.ui.transaction.preorders.network;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by H154326 on 17/5/2.
 * Email: yang.liu6@honeywell.com
 */

public class PreOrderSearchRequest {
    @SerializedName("warehouse_id")
    private int warehouseId;
    @SerializedName("last_request_time")
    private String lastRequestTime;
    @SerializedName("until_time")
    private String untilTime;
    @SerializedName("page_length")
    private String pageLength = "50";
    @SerializedName("search_string")
    private String searchString;
    @SerializedName("page_number")
    private String pageNumber;
    @SerializedName("stock_status")
    private String stockStatus;
    @SerializedName("category_id")
    private int categoryId = -1;

    public PreOrderSearchRequest() {
    }

    public PreOrderSearchRequest(int warehouseId, String pageNumber) {
        this.warehouseId = warehouseId;
        this.pageNumber = pageNumber;
    }

    public PreOrderSearchRequest(int warehouseId, String pageNumber, String stockStatus, int categoryId) {
        this.warehouseId = warehouseId;
        this.pageNumber = pageNumber;
        this.stockStatus = stockStatus;
        this.categoryId = categoryId;
    }

    public PreOrderSearchRequest(int warehouseId, String searchString, String pageNumber) {
        this.warehouseId = warehouseId;
        this.searchString = searchString;
        this.pageNumber = pageNumber;
    }

    public PreOrderSearchRequest(int warehouseId, String searchString, String pageNumber, String stockStatus) {
        this.warehouseId = warehouseId;
        this.searchString = searchString;
        this.pageNumber = pageNumber;
        this.stockStatus = stockStatus;
    }

    public PreOrderSearchRequest(String searchString, String pageNumber, String stockStatus) {
        this.searchString = searchString;
        this.pageNumber = pageNumber;
        this.stockStatus = stockStatus;
    }

    public int getWarehouseId() {
        return warehouseId;
    }

    public void setWarehouseId(int warehouseId) {
        this.warehouseId = warehouseId;
    }

    public String getLastRequestTime() {
        return lastRequestTime;
    }

    public void setLastRequestTime(String lastRequestTime) {
        this.lastRequestTime = lastRequestTime;
    }

    public String getUntilTime() {
        return untilTime;
    }

    public void setUntilTime(String untilTime) {
        this.untilTime = untilTime;
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
}
