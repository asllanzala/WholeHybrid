package com.honeywell.wholesale.ui.search;

import com.honeywell.wholesale.framework.http.HttpResult;
import com.honeywell.wholesale.framework.model.Order;
import com.honeywell.wholesale.ui.transaction.TransactionQueryRequest;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

/**
 * Created by xiaofei on 10/24/16.
 */

public interface TransantionRefactorService {
    @POST("transaction/sale/search")
    Call<HttpResult<TransactionResult<Order>>> getTransactionList(@Header("Cookie") String token,
                                                                   @Body TransactionSearchRefactorRequest body);
}
