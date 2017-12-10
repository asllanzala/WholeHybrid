package com.honeywell.wholesale.ui.transaction;

import com.google.gson.annotations.SerializedName;
import com.honeywell.wholesale.framework.model.Order;

import java.util.ArrayList;

/**
 * Created by xiaofei on 10/13/16.
 *
 * response  http://159.99.93.113:8099/transaction/sale/search
 */

public class TransactionQueryReponse {

    @SerializedName("total_order_list")
    private ArrayList<Order> orderList;

    @SerializedName("count")
    private String count;

    @SerializedName("next_page")
    private String isNextPage;

    public ArrayList<Order> getOrderList() {
        return orderList;
    }

    public void setOrderList(ArrayList<Order> orderList) {
        this.orderList = orderList;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public String getIsNextPage() {
        return isNextPage;
    }

    public void setIsNextPage(String isNextPage) {
        this.isNextPage = isNextPage;
    }
}
