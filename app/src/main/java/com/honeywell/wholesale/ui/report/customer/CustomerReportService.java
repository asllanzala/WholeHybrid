package com.honeywell.wholesale.ui.report.customer;

import com.honeywell.wholesale.framework.http.HttpResult;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

/**
 * Created by xiaofei on 9/20/16.
 *
 */
public interface CustomerReportService {
    @POST("statistic/customer")
    Call<HttpResult<CustomerReportResponse<CustomerChartEntity>>> getDailyStatistic(@Header("Cookie") String token,
                                                                                    @Body CustomerReportRequest reportRequest);
}
