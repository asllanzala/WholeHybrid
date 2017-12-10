package com.honeywell.wholesale.ui.report.supplier;

import com.honeywell.wholesale.framework.http.HttpResult;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

/**
 * Created by xiaofei on 9/22/16.
 *
 */
public interface SupplierReportService {
    @POST("statistic/supplier")
    Call<HttpResult<SupplierReportResponse<SupplierChartEntity>>> getSupplierStatistic(@Header("Cookie") String token,
                                                                                       @Body SupplierReportRequest reportRequest);
}
