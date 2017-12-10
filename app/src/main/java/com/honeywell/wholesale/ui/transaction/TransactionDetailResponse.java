package com.honeywell.wholesale.ui.transaction;

import com.google.gson.annotations.SerializedName;
import com.honeywell.wholesale.framework.model.Order;

import java.util.ArrayList;

/**
 * Created by xiaofei on 10/14/16.
 *
 * 单个订单详情查询
 * Response http://159.99.93.113:8099/transaction/sale/detail
 */

public class TransactionDetailResponse {

    @SerializedName("total_order_list")
    private ArrayList<Order> orderList;

    public TransactionDetailResponse(ArrayList<Order> orderList) {
        this.orderList = orderList;
    }

    public ArrayList<Order> getOrderList() {
        return orderList;
    }

    public void setOrderList(ArrayList<Order> orderList) {
        this.orderList = orderList;
    }
}
