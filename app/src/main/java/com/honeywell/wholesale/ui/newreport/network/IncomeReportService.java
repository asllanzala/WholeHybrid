package com.honeywell.wholesale.ui.newreport.network;

import com.honeywell.wholesale.framework.http.HttpResult;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

/**
 * Created by xiaofei on 9/20/16.
 *
 */
public interface IncomeReportService {
    @POST("statistic/income/day")
    Call<HttpResult<DailyIncomeResponse<DailyIncomeEntity>>> getDailyStatistic(@Header("Cookie") String token,
                                                                               @Body IncomeReportRequest body);


    @POST("statistic/income/month")
    Call<HttpResult<MonthlyIncomeResponse<MonthlyIncomeEntity>>> getMonthStatistic(@Header("Cookie") String token,
                                                                                   @Body IncomeReportRequest body);

    @POST("statistic/income/year")
    Call<HttpResult<AnnualReportResponse<AnnualIncomeEntity>>> getAnnualStatistic(@Header("Cookie") String token,
                                                                                  @Body IncomeReportRequest body);
}
