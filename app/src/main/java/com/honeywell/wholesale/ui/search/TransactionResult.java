package com.honeywell.wholesale.ui.search;

import android.text.format.DateUtils;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xiaofei on 9/12/16.
 *
 */
public class TransactionResult<T> {

    @SerializedName("count")
    private String searchCount;

    @SerializedName("total_order_list")
    private ArrayList<T> orderList;

    @SerializedName("next_page")
    private String nextPage;

    @SerializedName("last_update_time")
    private String lastUpdateTime;

    public String getNextPage() {
        return nextPage;
    }

    public ArrayList<T> getOrderList() {
        return orderList;
    }

    public String getLastUpdateTime() {
        return lastUpdateTime;
    }

    public String getSearchCount() {
        return searchCount;
    }

    @Override
    public String toString() {
        return "TransactionResult{" +
                "searchCount='" + searchCount + '\'' +
                ", orderList=" + orderList +
                ", nextPage='" + nextPage + '\'' +
                ", lastUpdateTime='" + lastUpdateTime + '\'' +
                '}';
    }
}
