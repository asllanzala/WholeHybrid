package com.honeywell.wholesale.ui.search;


import com.honeywell.wholesale.framework.http.HttpResult;
import com.honeywell.wholesale.framework.model.Order;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

/**
 * Created by xiaofei on 9/12/16.
 *
 */

public interface TransactionService {
    @POST("transaction/sale/search")
    Call<HttpResult<TransactionResult<Order>>> getTransactionList(@Header("Cookie") String token,
                                                                  @Body TransactionSearchRequest body);
}
