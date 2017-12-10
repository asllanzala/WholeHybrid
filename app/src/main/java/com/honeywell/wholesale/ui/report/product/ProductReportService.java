package com.honeywell.wholesale.ui.report.product;

import com.honeywell.wholesale.framework.http.HttpResult;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

/**
 * Created by xiaofei on 9/20/16.
 *
 */
public interface ProductReportService {
    @POST("statistic/product")
    Call<HttpResult<ProductReportResponse<ProductChartEntity>>> getProductStatistic(@Header("Cookie") String token,
                                                                                    @Body ProductReportRequest body);
}
