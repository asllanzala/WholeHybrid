package com.honeywell.wholesale.ui.dashboard.fragment;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.honeywell.hybridapp.framework.log.LogHelper;
import com.honeywell.wholesale.R;
import com.honeywell.wholesale.framework.event.SwitchFragmentEvent;
import com.honeywell.wholesale.framework.http.HttpResult;
import com.honeywell.wholesale.framework.http.WebServerConfigManager;
import com.honeywell.wholesale.framework.model.Account;
import com.honeywell.wholesale.framework.model.AccountManager;
import com.honeywell.wholesale.framework.network.NetworkManager;
import com.honeywell.wholesale.framework.utils.WSTimeStamp;
import com.honeywell.wholesale.lib.util.StringUtil;
import com.honeywell.wholesale.ui.base.BaseRootFragment;
import com.honeywell.wholesale.ui.newreport.AxisView;
import com.honeywell.wholesale.ui.newreport.ChartAssistDataModel;
import com.honeywell.wholesale.ui.newreport.ChartDataAdapter;
import com.honeywell.wholesale.ui.newreport.ChartDataModel;
import com.honeywell.wholesale.ui.newreport.network.AnnualIncomeEntity;
import com.honeywell.wholesale.ui.newreport.network.AnnualReportResponse;
import com.honeywell.wholesale.ui.newreport.network.DailyIncomeEntity;
import com.honeywell.wholesale.ui.newreport.network.DailyIncomeResponse;
import com.honeywell.wholesale.ui.newreport.network.IncomeReportRequest;
import com.honeywell.wholesale.ui.newreport.network.IncomeReportService;
import com.honeywell.wholesale.ui.newreport.network.MonthlyIncomeEntity;
import com.honeywell.wholesale.ui.newreport.network.MonthlyIncomeResponse;

import org.w3c.dom.Text;

import java.util.ArrayList;

import de.greenrobot.event.EventBus;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;


/**
 * Created by H154326 on 17/1/6.
 * Email: yang.liu6@honeywell.com
 */

public class NewReportNativeFragment extends BaseRootFragment {

    private static final String TAG = NewReportNativeFragment.class.getSimpleName();

    //报表类型：销售额-利润；销售额；利润
    private static final String BOTH = "BOTH";
    private static final String SALE = "SALE";
    private static final String PROFIT = "PROFIT";

    //报表数据源的位置，最左，中间，或者最右→_→
    private static final String LEFT_POSITION = "LEFT";
    private static final String MIDDLE_POSITION = "MIDDLE";
    private static final String RIGHT_POSITION = "RIGHT";

    public static final int INCOME_REPORT_DAILY = 1;
    public static final int INCOME_REPORT_MONTHLY = 2;
    public static final int INCOME_REPORT_YEARLY = 3;

    //数据源的缺省avg值
    public static final float DEFAULT_AVG = 0f;

    //向服务器端请求日报表数据个数，返回30个
    private final int REPORT_DAILY_DATA_WINDOW_SIZE = 30;
    //向服务器端请求月报表数据个数，返回13个
    private final int REPORT_MONTHLY_DATA_WINDOW_SIZE = 12;
    //向服务器端请求年报表数据个数，返回11个
    private final int REPORT_ANNUAL_DATA_WINDOW_SIZE = 10;

    //向服务器端请求日报表的最后日期
    private int mLastDailyEndDayDiff = 0;
    //向服务器端请求月报表的最后日期
    private int mLastMonthlyEndDayDiff = 0;
    //向服务器端请求年报表的最后日期
    private int mLastAnnualEndDayDiff = 0;

    //当前报表类型
    private static int mCurrentReportType = INCOME_REPORT_DAILY;

    private Retrofit retrofit;
    private IncomeReportService incomeReportService;

    //日报表，月报表，年报表，用来接受服务器传来的数据
    private ArrayList<DailyIncomeEntity> dailyList;
    private ArrayList<MonthlyIncomeEntity> monthList;
    private ArrayList<AnnualIncomeEntity> annualList;

    //到目前为止，所欠的所有金额的总和，并且跟报表的其他数据不一样，并不是跟着每一个item联动，特此记录，以儆效尤
    private String mReceivable = "0";

    //初始化报表时默认item的位置信息
    private int mDefaultPosition = 0;

    private int mCurrentPosition = 0;

    private RecyclerView mRecyclerView;

    private ChartDataAdapter mChartDataAdapter;
    //填充报表的数据list
    private ArrayList<ChartDataModel> mChartDataList = new ArrayList<>(REPORT_DAILY_DATA_WINDOW_SIZE);
    //填充报表的辅助数据list，也是填充坐标轴的数据list
    private ChartAssistDataModel mChartAssistDataModel = new ChartAssistDataModel();

    private TextView mChartDayTextView;
    private TextView mChartMonthTextView;
    private TextView mChartYearTextView;
    private TextView mChartCalenderTextView;
    private TextView mChartKindTextView;
    private TextView mChartDaySaleMoneyTextView;
    private TextView mChartDayprofitMoneyTextView;
    private TextView mChartDayCreditTextView;
    private TextView mChartSaleMoneyTitleTextView;
    private TextView mChartProfitMoneyTitleTextView;
    private TextView mChartSaleTextView;
    private TextView mChartProfitTextView;
    private TextView mChartMainRoleTextView;

    private View mChartBefSaleView;
    private View mChartBefProfitView;
    private View mChartDayView;
    private View mChartMonthView;
    private View mChartYearView;

    private AxisView mLeftAxisView;
    private AxisView mRightAxisView;

    private PopupWindow mChartKindPopupWindow;

    private LinearLayoutManager layoutManager;

    private ScrollView mChartMainScrollView;


    @Override
    public int getLayout() {
        return R.layout.fragment_chart_main;
    }

    @Override
    public int getIndex() {
        return 4;
    }

    @Override
    public void initView(View view) {
        super.initView(view);
        initViews(view);
        initAdapter();
        initReportData(INCOME_REPORT_DAILY);
        switchToDay();
    }

    private void initViews(View view) {
        //得到控件
        mRecyclerView = (RecyclerView) view.findViewById(R.id.id_recyclerview_horizontal);
        //设置布局管理器
        layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        layoutManager.scrollToPosition(mDefaultPosition + 2);
        mRecyclerView.setLayoutManager(layoutManager);

        //点击切换日报表
        mChartDayTextView = (TextView) view.findViewById(R.id.chart_day_text_view);
        mChartDayTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mChartDayView.getVisibility() != View.VISIBLE){
                    switchToDay();
                    initReportData(INCOME_REPORT_DAILY);
                }
            }
        });

        //点击切换月报表
        mChartMonthTextView = (TextView) view.findViewById(R.id.chart_month_text_view);
        mChartMonthTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mChartMonthView.getVisibility() != View.VISIBLE) {
                    switchToMonth();
                    initReportData(INCOME_REPORT_MONTHLY);
                }
            }
        });

        //点击切换年报表
        mChartYearTextView = (TextView) view.findViewById(R.id.chart_year_text_view);
        mChartYearTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mChartYearView.getVisibility() != View.VISIBLE) {
                    switchToYear();
                    initReportData(INCOME_REPORT_YEARLY);
                }
            }
        });
        mChartCalenderTextView = (TextView) view.findViewById(R.id.chart_calender_text_view);
        mChartKindTextView = (TextView) view.findViewById(R.id.chart_kind_text_view);
        mChartKindTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initChartKindPopupWindow();
                int left = Math.round(v.getX()) + v.getWidth() / 2 - 160 - 115;
                int top = v.getTop();
                mChartKindPopupWindow.showAsDropDown(v, left, top);
            }
        });
        mChartDaySaleMoneyTextView = (TextView) view.findViewById(R.id.chart_day_sale_money_text_view);
        mChartDayprofitMoneyTextView = (TextView) view.findViewById(R.id.chart_day_profit_money_text_view);
        mChartDayCreditTextView = (TextView) view.findViewById(R.id.chart_day_credit_text_view);

        mChartSaleMoneyTitleTextView = (TextView) view.findViewById(R.id.chart_day_sale_money_title_text_view);
        mChartProfitMoneyTitleTextView = (TextView) view.findViewById(R.id.chart_day_profit_money_title_text_view);

        mChartDayView = view.findViewById(R.id.chart_day_view);
        mChartMonthView = view.findViewById(R.id.chart_month_view);
        mChartYearView = view.findViewById(R.id.chart_year_view);

        mLeftAxisView = (AxisView) view.findViewById(R.id.left_axis);
        mLeftAxisView.setIsLeftAxis(true);
        mLeftAxisView.setmChartAssistDataModel(mChartAssistDataModel);
        mRightAxisView = (AxisView) view.findViewById(R.id.right_axis);
        mRightAxisView.setIsLeftAxis(false);
        mRightAxisView.setmChartAssistDataModel(mChartAssistDataModel);

        mChartSaleTextView = (TextView) view.findViewById(R.id.chart_sale_text_view);
        mChartProfitTextView = (TextView) view.findViewById(R.id.chart_profit_text_view);

        mChartBefSaleView = view.findViewById(R.id.chart_bef_sale_view);
        mChartBefProfitView = view.findViewById(R.id.chart_bef_profit_view);
        mChartMainScrollView = (ScrollView) view.findViewById(R.id.fragment_chart_main_scroll_view);
        mChartMainRoleTextView = (TextView) view.findViewById(R.id.fragment_chart_main_role_text_view);

        judgeRole();
    }

    private void judgeRole(){
        Account currentAccount = AccountManager.getInstance().getCurrentAccount();
        String mCurrentCompanyAccount = currentAccount.getCompanyAccount();
        if (mCurrentCompanyAccount.equals("develop") || mCurrentCompanyAccount.equals("testcompany")){
            mChartMainRoleTextView.setText("你暂时没有权限，努力工作，早日成为店长！");
        } else {
            mChartMainRoleTextView.setText("你暂时没有权限，努力工作，早日成为店长！");
        }

        if (currentAccount.getRole().equals("0")){
            mChartMainScrollView.setVisibility(View.VISIBLE);
            mChartMainRoleTextView.setVisibility(View.GONE);
        } else {
            mChartMainScrollView.setVisibility(View.GONE);
            mChartMainRoleTextView.setVisibility(View.VISIBLE);
        }

    }

    /**
     * 初始化网络请求
     */
    private void initialWebClient() throws Exception {
        String baseUrl = WebServerConfigManager.getWebServiceUrl();
        NetworkManager networkManager = NetworkManager.getInstance();

        retrofit = new Retrofit.Builder()
                .client(networkManager.setSSL().build())
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();
    }

    /**
     * 初始化adapter
     */
    private void initAdapter() {
        mChartDataAdapter = new ChartDataAdapter(getActivity(), mChartDataList, mChartAssistDataModel, BOTH);
        mRecyclerView.setAdapter(mChartDataAdapter);
        setHeaderView(mRecyclerView);
        setFooterView(mRecyclerView);
        mChartDataAdapter.notifyDataSetChanged();
        mChartDataAdapter.setOnItemClickListener(new ChartDataAdapter.MyItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Log.e(TAG, "position = " + (position - 1));
                setChartClickShow(position - 1);
                mCurrentPosition = position - 1;
            }
        });
    }

    /**
     * 为recyclerview添加headerview
     *
     * @param view
     */
    private void setHeaderView(RecyclerView view) {
        View header = LayoutInflater.from(getContext()).inflate(R.layout.item_chart_header_view, view, false);
        mChartDataAdapter.addHeaderView(header);
    }

    /**
     * 为recyclerview添加footerview
     *
     * @param view
     */
    private void setFooterView(RecyclerView view) {
        View footer = LayoutInflater.from(getContext()).inflate(R.layout.item_chart_footer_view, view, false);
        mChartDataAdapter.addFooterView(footer);
    }

    /**
     * 从服务器端获取数据.
     *
     * @param reportType INCOME_REPORT_DAILY, INCOME_REPORT_MONTHLY, INCOME_REPORT_YEARLY
     */
    public void initReportData(int reportType) {
        mCurrentReportType = reportType;

        try {
            initialWebClient();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // initial first data info
        String shopId = AccountManager.getInstance().getCurrentAccount().getCurrentShopId();
        IncomeReportRequest incomeReportRequest = null;
        switch (reportType) {
            case INCOME_REPORT_DAILY:
                long startTime = WSTimeStamp.getSpecialDayStartTime(mLastDailyEndDayDiff - REPORT_DAILY_DATA_WINDOW_SIZE);
                long endTime = WSTimeStamp.getSpecialDayEndTime(mLastDailyEndDayDiff);
                incomeReportRequest = new IncomeReportRequest(shopId, String.valueOf(startTime), String.valueOf(endTime));
                getChartDaysDataFromWeb(incomeReportRequest);
                break;

            case INCOME_REPORT_MONTHLY:
                String startMonth = WSTimeStamp.getSpecialMonthStartTime(mLastMonthlyEndDayDiff - REPORT_MONTHLY_DATA_WINDOW_SIZE);
                String endMonth = WSTimeStamp.getSpecialMonthEndTime(mLastMonthlyEndDayDiff);
                incomeReportRequest = new IncomeReportRequest(shopId, startMonth, endMonth);
                getChartMonthDataFromWeb(incomeReportRequest);
                break;

            case INCOME_REPORT_YEARLY:
                String startYear = WSTimeStamp.getSpecialYearsStartTime(mLastAnnualEndDayDiff - REPORT_ANNUAL_DATA_WINDOW_SIZE);
                String endYear = WSTimeStamp.getSpecialYearsEndTime(mLastAnnualEndDayDiff);
                incomeReportRequest = new IncomeReportRequest(shopId, startYear, endYear);
                getChartAnnualDataFromWeb(incomeReportRequest);
                break;
            default:
                break;
        }

    }

    /**
     * 从服务器端获取日报表的数据
     *
     * @param request
     */
    private void getChartDaysDataFromWeb(IncomeReportRequest request) {
        if (incomeReportService == null) {
            incomeReportService = retrofit.create(IncomeReportService.class);
        }

        LogHelper.getInstance().d(TAG, "获取日报:" + request.getJsonString());

        Call<HttpResult<DailyIncomeResponse<DailyIncomeEntity>>> call =
                incomeReportService.getDailyStatistic(getToken(), request);

        call.enqueue(new Callback<HttpResult<DailyIncomeResponse<DailyIncomeEntity>>>() {
            @Override
            public void onResponse(Call<HttpResult<DailyIncomeResponse<DailyIncomeEntity>>> call, Response<HttpResult<DailyIncomeResponse<DailyIncomeEntity>>> response) {
                if (response == null) {
                    Log.e(TAG, "Cloud response is null!!!");
                    return;
                }

                if (!response.isSuccessful()){
                    Toast.makeText(getContext(), "获取数据失败", Toast.LENGTH_SHORT).show();
                    return;
                }
//                Log.e("data1", response.body().toString());
//                Log.e("data", new Gson().toJson(response.body()));
                HttpResult httpResult = response.body();
                DailyIncomeResponse report = (DailyIncomeResponse) httpResult.getData();
                dailyList = report.getIncomeList();
                if (report.getReceivable() != null) {
                    mReceivable = report.getReceivable();
                } else {
                    mReceivable = "0";
                }

                prepareReportData(INCOME_REPORT_DAILY);
                reloadChartData();

//                Log.e("data", new Gson().toJson(response.body()));
            }

            @Override
            public void onFailure(Call<HttpResult<DailyIncomeResponse<DailyIncomeEntity>>> call, Throwable t) {
                Log.e(TAG, "失败");
                if (getContext() != null) {
                    Toast.makeText(getContext(), "获取数据失败", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * 从服务器端获取月报表的数据
     *
     * @param request
     */
    private void getChartMonthDataFromWeb(IncomeReportRequest request) {
        if (incomeReportService == null) {
            incomeReportService = retrofit.create(IncomeReportService.class);
        }
        LogHelper.getInstance().d(TAG, "获取月报:" + request.getJsonString());

        Call<HttpResult<MonthlyIncomeResponse<MonthlyIncomeEntity>>> call =
                incomeReportService.getMonthStatistic(getToken(), request);

        call.enqueue(new Callback<HttpResult<MonthlyIncomeResponse<MonthlyIncomeEntity>>>() {
            @Override
            public void onResponse(Call<HttpResult<MonthlyIncomeResponse<MonthlyIncomeEntity>>> call, Response<HttpResult<MonthlyIncomeResponse<MonthlyIncomeEntity>>> response) {
                if (response == null) {
                    Log.e(TAG, "Cloud response is null!!!");
                    return;
                }

                if (!response.isSuccessful()){
                    Toast.makeText(getContext(), "获取数据失败", Toast.LENGTH_SHORT).show();
                    return;
                }
//                Log.e("data", new Gson().toJson(response.body()));
                HttpResult httpResult = response.body();
                MonthlyIncomeResponse report = (MonthlyIncomeResponse) httpResult.getData();
                monthList = report.getIncomeList();
                if (report.getReceivable() != null) {
                    mReceivable = report.getReceivable();
                } else {
                    mReceivable = "0";
                }

                prepareReportData(INCOME_REPORT_MONTHLY);
                reloadChartData();
//                Log.e("data111", new Gson().toJson(response.body()));
            }

            @Override
            public void onFailure(Call<HttpResult<MonthlyIncomeResponse<MonthlyIncomeEntity>>> call, Throwable t) {

                Toast.makeText(getContext(), "获取数据失败", Toast.LENGTH_SHORT).show();
            }
        });

    }

    /**
     * 从服务器端获取年报表的数据
     *
     * @param request
     */
    private void getChartAnnualDataFromWeb(IncomeReportRequest request) {
        if (incomeReportService == null) {
            incomeReportService = retrofit.create(IncomeReportService.class);
        }
        LogHelper.getInstance().d(TAG, "获取年报:" + request.getJsonString());

        Call<HttpResult<AnnualReportResponse<AnnualIncomeEntity>>> call =
                incomeReportService.getAnnualStatistic(getToken(), request);

        call.enqueue(new Callback<HttpResult<AnnualReportResponse<AnnualIncomeEntity>>>() {
            @Override
            public void onResponse(Call<HttpResult<AnnualReportResponse<AnnualIncomeEntity>>> call, Response<HttpResult<AnnualReportResponse<AnnualIncomeEntity>>> response) {
                if (response == null) {
                    Log.e(TAG, "Cloud response is null!!!");
                    return;
                }

                if (!response.isSuccessful()){
                    Toast.makeText(getContext(), "获取数据失败", Toast.LENGTH_SHORT).show();
                    return;
                }

//                Log.e("data", new Gson().toJson(response.body()));
                HttpResult httpResult = response.body();
                AnnualReportResponse report = (AnnualReportResponse) httpResult.getData();
                annualList = report.getIncomeList();
                if (report.getReceivable() != null) {
                    mReceivable = report.getReceivable();
                } else {
                    mReceivable = "0";
                }

                prepareReportData(INCOME_REPORT_YEARLY);
                reloadChartData();

//                Log.e("data", new Gson().toJson(response.body()));
            }

            @Override
            public void onFailure(Call<HttpResult<AnnualReportResponse<AnnualIncomeEntity>>> call, Throwable t) {
                Toast.makeText(getContext(), "获取数据失败", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Prepare report data.
     *
     * @param reportType INCOME_REPORT_DAILY, INCOME_REPORT_MONTHLY, INCOME_REPORT_YEARLY
     */
    private void prepareReportData(int reportType) {
        switch (reportType) {
            case INCOME_REPORT_DAILY:
                if (dailyList == null) {
                    Toast.makeText(getContext(), "没有数据", Toast.LENGTH_SHORT).show();
                    return;
                }
                mChartDataList.clear();

                initChartDayDatas(dailyList);

                break;

            case INCOME_REPORT_MONTHLY:
                if (monthList == null) {
                    Toast.makeText(getContext(), "没有数据", Toast.LENGTH_SHORT).show();
                    return;
                }
                mChartDataList.clear();

                initChartMonthDatas(monthList);
                break;

            case INCOME_REPORT_YEARLY:
                if (annualList == null) {
                    Toast.makeText(getContext(), "没有数据", Toast.LENGTH_SHORT).show();
                    return;
                }
                mChartDataList.clear();

                initChartAnnualDatas(annualList);
                break;
            default:
                break;
        }
    }

    private String getToken() {
        Account currentUser = AccountManager.getInstance().getCurrentAccount();
        String s = currentUser.getToken();
        return "token=" + s;
    }

    /**
     * 将数据重新填充到adapter中
     */
    private void reloadChartData() {
        mLeftAxisView.setmChartAssistDataModel(mChartAssistDataModel);
        mRightAxisView.setmChartAssistDataModel(mChartAssistDataModel);
        mChartDataAdapter.setData(mChartDataList, mChartAssistDataModel, BOTH);
        mChartDataAdapter.notifyDataSetChanged();
        setChartClickShow(mDefaultPosition);
        layoutManager.scrollToPosition(mDefaultPosition + 2);
        mRecyclerView.setLayoutManager(layoutManager);
    }

    /**
     * 切换到日报表
     */
    private void switchToDay() {

        mChartKindTextView.setText("销售额-利润");
        mChartDayView.setVisibility(View.VISIBLE);
        mChartMonthView.setVisibility(View.GONE);
        mChartYearView.setVisibility(View.GONE);
        mLeftAxisView.setVisibility(View.VISIBLE);
        mRightAxisView.setVisibility(View.VISIBLE);
        mChartSaleMoneyTitleTextView.setText("当日销售额");
        mChartProfitMoneyTitleTextView.setText("当日利润额");
        mChartSaleTextView.setVisibility(View.VISIBLE);
        mChartProfitTextView.setVisibility(View.VISIBLE);
        mChartBefSaleView.setVisibility(View.VISIBLE);
        mChartBefProfitView.setVisibility(View.VISIBLE);
    }

    /**
     * 切换到月报表
     */
    private void switchToMonth() {

        mChartKindTextView.setText("销售额-利润");
        mChartDayView.setVisibility(View.GONE);
        mChartMonthView.setVisibility(View.VISIBLE);
        mChartYearView.setVisibility(View.GONE);
        mLeftAxisView.setVisibility(View.VISIBLE);
        mRightAxisView.setVisibility(View.VISIBLE);
        mChartSaleMoneyTitleTextView.setText("当月销售额");
        mChartProfitMoneyTitleTextView.setText("当月利润额");
        mChartSaleTextView.setVisibility(View.VISIBLE);
        mChartProfitTextView.setVisibility(View.VISIBLE);
        mChartBefSaleView.setVisibility(View.VISIBLE);
        mChartBefProfitView.setVisibility(View.VISIBLE);
    }

    /**
     * 切换到年报表
     */
    private void switchToYear() {

        mChartKindTextView.setText("销售额-利润");
        mChartDayView.setVisibility(View.GONE);
        mChartMonthView.setVisibility(View.GONE);
        mChartYearView.setVisibility(View.VISIBLE);
        mLeftAxisView.setVisibility(View.VISIBLE);
        mRightAxisView.setVisibility(View.VISIBLE);
        mChartSaleMoneyTitleTextView.setText("当年销售额");
        mChartProfitMoneyTitleTextView.setText("当年利润额");
        mChartSaleTextView.setVisibility(View.VISIBLE);
        mChartProfitTextView.setVisibility(View.VISIBLE);
        mChartBefSaleView.setVisibility(View.VISIBLE);
        mChartBefProfitView.setVisibility(View.VISIBLE);
    }

    /**
     * 获取一组数据的最大值
     */
    public static float getMax(ArrayList<Float> arrayList) {
        float max = arrayList.get(0).floatValue();
        for (int i = 1; i < arrayList.size(); i++) {
            if (Float.compare(arrayList.get(i).floatValue(), max) > 0)
                max = arrayList.get(i).floatValue();
        }
        return max;
    }

    /**
     * 获取一组数据的最小值
     */
    public static float getMin(ArrayList<Float> arrayList) {
        float min = arrayList.get(0).floatValue();
        for (int i = 1; i < arrayList.size(); i++) {
            if (Float.compare(arrayList.get(i).floatValue(), min) < 0)
                min = arrayList.get(i).floatValue();
        }
        return min;
    }

    /**
     * 获取两个数的平均值
     */
    public static float getAvg(float m1, float m2) {
        return (m1 + m2) / 2f;
    }

    /**
     * 点击报表时，显示的内容
     *
     * @param position
     */
    private void setChartClickShow(int position) {

        if ( (mChartDataList == null) ||(mChartDataList.size() == 0) ) {
            return;
        } else {
            mChartDataAdapter.setRectSelected(position);
            mChartDataAdapter.notifyDataSetChanged();
            mChartCalenderTextView.setText(mChartDataList.get(position).getmCurrentDate());
            mChartDaySaleMoneyTextView.setText(String.valueOf(mChartDataList.get(position).getmIncome()));
            mChartDayprofitMoneyTextView.setText(String.valueOf(mChartDataList.get(position).getmProfit()));
            mChartDayCreditTextView.setText(String.valueOf(mChartDataList.get(position).getmCreditCreateAmount()));
        }
    }

    /**
     * 重新载入报表和坐标轴的数据
     */
    private void reloadAdapter(String mChartType){
        mChartDataAdapter.setData(mChartDataList, mChartAssistDataModel, mChartType);
        mRecyclerView.setAdapter(mChartDataAdapter);
        mChartDataAdapter.notifyDataSetChanged();
        mLeftAxisView.setmChartAssistDataModel(mChartAssistDataModel);
        mRightAxisView.setmChartAssistDataModel(mChartAssistDataModel);
    }

    /**
     * 切换至 销售额-利润
     */
    private void switchToBoth(){
        //仅切换报表类型，销售额-利润，销售额，利润，互转时，记忆选中的状态，若是两头的数据，保持位置不变，否则，就将上次选中的日期滚动到中间。
        if (mCurrentPosition != mDefaultPosition) {
            setChartClickShow(mCurrentPosition);
            layoutManager.scrollToPosition(mCurrentPosition - 3);
        } else {
            setChartClickShow(mDefaultPosition);
            layoutManager.scrollToPosition(mDefaultPosition + 2);
        }
        mRecyclerView.setLayoutManager(layoutManager);
        mLeftAxisView.setVisibility(View.VISIBLE);
        mRightAxisView.setVisibility(View.VISIBLE);
        mChartKindTextView.setText("销售额-利润");
        mChartSaleTextView.setVisibility(View.VISIBLE);
        mChartProfitTextView.setVisibility(View.VISIBLE);
        mChartBefSaleView.setVisibility(View.VISIBLE);
        mChartBefProfitView.setVisibility(View.VISIBLE);
        if (mChartKindPopupWindow != null){
            mChartKindPopupWindow.dismiss();
        }
    }

    /**
     * 切换至 销售额
     */
    private void switchToSale(){
        //仅切换报表类型，销售额-利润，销售额，利润，互转时，记忆选中的状态，若是两头的数据，保持位置不变，否则，就将上次选中的日期滚动到中间。
        if (mCurrentPosition != mDefaultPosition) {
            setChartClickShow(mCurrentPosition);
            layoutManager.scrollToPosition(mCurrentPosition - 3);
        } else {
            setChartClickShow(mDefaultPosition);
            layoutManager.scrollToPosition(mDefaultPosition + 2);
        }
        mRecyclerView.setLayoutManager(layoutManager);
        mLeftAxisView.setVisibility(View.VISIBLE);
        mRightAxisView.setVisibility(View.GONE);
        mChartKindTextView.setText("销售额");
        mChartSaleTextView.setVisibility(View.VISIBLE);
        mChartProfitTextView.setVisibility(View.GONE);
        mChartBefSaleView.setVisibility(View.VISIBLE);
        mChartBefProfitView.setVisibility(View.GONE);
        if (mChartKindPopupWindow != null){
            mChartKindPopupWindow.dismiss();
        }
    }

    /**
     * 切换至 利润
     */
    private void switchToProfit(){
        //仅切换报表类型，销售额-利润，销售额，利润，互转时，记忆选中的状态，若是两头的数据，保持位置不变，否则，就将上次选中的日期滚动到中间。
        if (mCurrentPosition != mDefaultPosition) {
            setChartClickShow(mCurrentPosition);
            layoutManager.scrollToPosition(mCurrentPosition - 3);
        } else {
            setChartClickShow(mDefaultPosition);
            layoutManager.scrollToPosition(mDefaultPosition + 2);
        }
        mRecyclerView.setLayoutManager(layoutManager);
        mLeftAxisView.setVisibility(View.GONE);
        mRightAxisView.setVisibility(View.VISIBLE);
        mChartKindTextView.setText("利润");
        mChartSaleTextView.setVisibility(View.GONE);
        mChartProfitTextView.setVisibility(View.VISIBLE);
        mChartBefSaleView.setVisibility(View.GONE);
        mChartBefProfitView.setVisibility(View.VISIBLE);
        if (mChartKindPopupWindow != null){
            mChartKindPopupWindow.dismiss();
        }
    }

    /**
     * 初始化 更多 popupWindow
     */
    private void initChartKindPopupWindow() {
        final View chartKindPopupModelWindowView = LayoutInflater.from(getActivity()).inflate(R.layout.popup_chart_kind_select, null, false);
        final TextView mBothChartTextView = (TextView) chartKindPopupModelWindowView.findViewById(R.id.popup_chart_kind_both_text_view);
        mBothChartTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reloadAdapter(BOTH);
                switchToBoth();
                //mChartKindPopupWindow.dismiss();
            }
        });
        final TextView mSaleChartTextView = (TextView) chartKindPopupModelWindowView.findViewById(R.id.popup_chart_kind_sale_text_view);
        mSaleChartTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reloadAdapter(SALE);
                switchToSale();
                //mChartKindPopupWindow.dismiss();
            }
        });
        final TextView mProfitChartTextView = (TextView) chartKindPopupModelWindowView.findViewById(R.id.popup_chart_kind_profit_text_view);
        mProfitChartTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reloadAdapter(PROFIT);
                switchToProfit();
                //mChartKindPopupWindow.dismiss();
            }
        });

        mChartKindPopupWindow = new PopupWindow(chartKindPopupModelWindowView, ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, true);

        mChartKindPopupWindow.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#f1f1f1")));
        chartKindPopupModelWindowView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (chartKindPopupModelWindowView != null && chartKindPopupModelWindowView.isShown()) {
                    mChartKindPopupWindow.dismiss();
                    mChartKindPopupWindow = null;
                }
                return false;
            }
        });
    }

    /**
     * 日报表数据
     *
     * @param dailyList
     */
    private void initChartDayDatas(ArrayList<DailyIncomeEntity> dailyList) {

        mChartDataList = new ArrayList<>(REPORT_DAILY_DATA_WINDOW_SIZE);
        ArrayList<Float> mIncome = new ArrayList<Float>(REPORT_DAILY_DATA_WINDOW_SIZE);
        ArrayList<Float> mProfit = new ArrayList<Float>(REPORT_DAILY_DATA_WINDOW_SIZE);
        ArrayList<Float> mCredit = new ArrayList<>(REPORT_DAILY_DATA_WINDOW_SIZE);
        ArrayList<Long> mTime = new ArrayList<>(REPORT_DAILY_DATA_WINDOW_SIZE);
        for (int index = 0; index < dailyList.size(); index++) {
            mIncome.add(dailyList.get(index).getIncome());
            mProfit.add(dailyList.get(index).getProfit());
            mCredit.add(Float.parseFloat(mReceivable));
            mTime.add(dailyList.get(index).getTimeStamp());
        }
        float mIncomeMin = getMin(mIncome);
        float mIncomeMax = getMax(mIncome);

        float mProfitMin = getMin(mProfit);
        float mProfitMax = getMax(mProfit);
        mChartAssistDataModel = new ChartAssistDataModel(mIncomeMin, mIncomeMax, mProfitMin, mProfitMax);

        for (int i = 0; i < REPORT_DAILY_DATA_WINDOW_SIZE; i++) {
            if (i == 0) {
                mChartDataList.add(new ChartDataModel(mIncome.get(i),
                        mProfit.get(i),
                        DEFAULT_AVG,
                        getAvg(mProfit.get(i), mProfit.get(i + 1)),
                        mCredit.get(i),
                        mTime.get(i),
                        WSTimeStamp.getChartDayString(mTime.get(i)),
                        WSTimeStamp.getChartYearMonthDayString(mTime.get(i)),
                        LEFT_POSITION));
            } else if (i == REPORT_DAILY_DATA_WINDOW_SIZE - 1) {
                mChartDataList.add(new ChartDataModel(mIncome.get(i),
                        mProfit.get(i),
                        getAvg(mProfit.get(i - 1), mProfit.get(i)),
                        DEFAULT_AVG,
                        mCredit.get(i),
                        mTime.get(i),
                        WSTimeStamp.getChartDayString(mTime.get(i)),
                        WSTimeStamp.getChartYearMonthDayString(mTime.get(i)),
                        RIGHT_POSITION));
            } else {
                mChartDataList.add(new ChartDataModel(mIncome.get(i),
                        mProfit.get(i),
                        getAvg(mProfit.get(i - 1), mProfit.get(i)),
                        getAvg(mProfit.get(i), mProfit.get(i + 1)),
                        mCredit.get(i),
                        mTime.get(i),
                        WSTimeStamp.getChartDayString(mTime.get(i)),
                        WSTimeStamp.getChartYearMonthDayString(mTime.get(i)),
                        MIDDLE_POSITION));
            }
        }
        mDefaultPosition = mChartDataList.size() - 1;
        mCurrentPosition = mDefaultPosition;
    }

    /**
     * 月报表数据
     *
     * @param monthList
     */
    private void initChartMonthDatas(ArrayList<MonthlyIncomeEntity> monthList) {

        int mMonthMax = REPORT_MONTHLY_DATA_WINDOW_SIZE + 1;
        mChartDataList = new ArrayList<>(mMonthMax);
        ArrayList<Float> mIncome = new ArrayList<Float>(mMonthMax);
        ArrayList<Float> mProfit = new ArrayList<Float>(mMonthMax);
        ArrayList<Float> mCredit = new ArrayList<>(mMonthMax);
        ArrayList<Long> mTime = new ArrayList<>(mMonthMax);
        for (int index = 0; index < monthList.size(); index++) {
            mIncome.add(monthList.get(index).getIncome());
            mProfit.add(monthList.get(index).getProfit());
            mCredit.add(Float.parseFloat(mReceivable));
            mTime.add(monthList.get(index).getTimeStamp());
        }
        float mIncomeMin = getMin(mIncome);
        float mIncomeMax = getMax(mIncome);

        float mProfitMin = getMin(mProfit);
        float mProfitMax = getMax(mProfit);
        mChartAssistDataModel = new ChartAssistDataModel(mIncomeMin, mIncomeMax, mProfitMin, mProfitMax);

        for (int i = 0; i < mMonthMax; i++) {
            if (i == 0) {
                mChartDataList.add(new ChartDataModel(mIncome.get(i),
                        mProfit.get(i),
                        DEFAULT_AVG,
                        getAvg(mProfit.get(i), mProfit.get(i + 1)),
                        mCredit.get(i),
                        mTime.get(i),
                        WSTimeStamp.getChartMonthString(mTime.get(i)),
                        WSTimeStamp.getChartYearMonthString(mTime.get(i)),
                        LEFT_POSITION));
            } else if (i == mMonthMax - 1) {
                mChartDataList.add(new ChartDataModel(mIncome.get(i),
                        mProfit.get(i),
                        getAvg(mProfit.get(i - 1), mProfit.get(i)),
                        DEFAULT_AVG,
                        mCredit.get(i),
                        mTime.get(i),
                        WSTimeStamp.getChartMonthString(mTime.get(i)),
                        WSTimeStamp.getChartYearMonthString(mTime.get(i)),
                        RIGHT_POSITION));
            } else {
                mChartDataList.add(new ChartDataModel(mIncome.get(i),
                        mProfit.get(i),
                        getAvg(mProfit.get(i - 1), mProfit.get(i)),
                        getAvg(mProfit.get(i), mProfit.get(i + 1)),
                        mCredit.get(i),
                        mTime.get(i),
                        WSTimeStamp.getChartMonthString(mTime.get(i)),
                        WSTimeStamp.getChartYearMonthString(mTime.get(i)),
                        MIDDLE_POSITION));
            }
        }
        mDefaultPosition = mChartDataList.size() - 1;
        mCurrentPosition = mDefaultPosition;
    }

    /**
     * 年报表数据
     *
     * @param annualList
     */
    private void initChartAnnualDatas(ArrayList<AnnualIncomeEntity> annualList) {

        int mAnnualMax = REPORT_ANNUAL_DATA_WINDOW_SIZE + 1;
        mChartDataList = new ArrayList<>(mAnnualMax);
        ArrayList<Float> mIncome = new ArrayList<Float>(mAnnualMax);
        ArrayList<Float> mProfit = new ArrayList<Float>(mAnnualMax);
        ArrayList<Float> mCredit = new ArrayList<>(mAnnualMax);
        ArrayList<Long> mTime = new ArrayList<>(mAnnualMax);
        for (int index = 0; index < annualList.size(); index++) {
            mIncome.add(annualList.get(index).getIncome());
            mProfit.add(annualList.get(index).getProfit());
            mCredit.add(Float.parseFloat(mReceivable));
            mTime.add(annualList.get(index).getTimeStamp());
        }
        float mIncomeMin = getMin(mIncome);
        float mIncomeMax = getMax(mIncome);

        float mProfitMin = getMin(mProfit);
        float mProfitMax = getMax(mProfit);
        mChartAssistDataModel = new ChartAssistDataModel(mIncomeMin, mIncomeMax, mProfitMin, mProfitMax);

        for (int i = 0; i < mAnnualMax; i++) {
            if (i == 0) {
                mChartDataList.add(new ChartDataModel(mIncome.get(i),
                        mProfit.get(i),
                        DEFAULT_AVG,
                        getAvg(mProfit.get(i), mProfit.get(i + 1)),
                        mCredit.get(i),
                        mTime.get(i),
                        WSTimeStamp.getChartYearString(mTime.get(i)),
                        WSTimeStamp.getChartYearString(mTime.get(i)),
                        LEFT_POSITION));
            } else if (i == mAnnualMax - 1) {
                mChartDataList.add(new ChartDataModel(mIncome.get(i),
                        mProfit.get(i),
                        getAvg(mProfit.get(i - 1), mProfit.get(i)),
                        DEFAULT_AVG,
                        mCredit.get(i),
                        mTime.get(i),
                        WSTimeStamp.getChartYearString(mTime.get(i)),
                        WSTimeStamp.getChartYearString(mTime.get(i)),
                        RIGHT_POSITION));
            } else {
                mChartDataList.add(new ChartDataModel(mIncome.get(i),
                        mProfit.get(i),
                        getAvg(mProfit.get(i - 1), mProfit.get(i)),
                        getAvg(mProfit.get(i), mProfit.get(i + 1)),
                        mCredit.get(i),
                        mTime.get(i),
                        WSTimeStamp.getChartYearString(mTime.get(i)),
                        WSTimeStamp.getChartYearString(mTime.get(i)),
                        MIDDLE_POSITION));
            }
        }
        mDefaultPosition = mChartDataList.size() - 1;
        mCurrentPosition = mDefaultPosition;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.e(TAG, "onDestroyView");
        EventBus.getDefault().post(new SwitchFragmentEvent(String.valueOf(R.string.report_fragment_switch)));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "onDestroy");
    }

    @Override
    public String getTitle() {
        return StringUtil.getString(R.string.dashboard_tab_native_report);
    }

    @Override
    public void reLoadData() {
        Log.e(TAG, "reLoadData");
        initReportData(INCOME_REPORT_DAILY);
        switchToDay();
        switchToBoth();
    }
}
