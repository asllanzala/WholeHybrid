package com.honeywell.wholesale.ui.dashboard.Interface;

import com.honeywell.wholesale.framework.model.Order;

import java.util.ArrayList;

/**
 * Created by xiaofei on 10/24/16.
 */

public interface TransactionInterface {
    void setTransactionSearchStatus (String payMent, String orderStatus, final String pageNumber);
}
