package com.honeywell.wholesale.ui.report.income;


import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Matrix;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.AxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ViewPortHandler;
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
import com.honeywell.wholesale.ui.base.BaseTextView;
import com.honeywell.wholesale.ui.newreport.network.AnnualIncomeEntity;
import com.honeywell.wholesale.ui.newreport.network.AnnualReportResponse;
import com.honeywell.wholesale.ui.newreport.network.DailyIncomeEntity;
import com.honeywell.wholesale.ui.newreport.network.DailyIncomeResponse;
import com.honeywell.wholesale.ui.newreport.network.IncomeReportRequest;
import com.honeywell.wholesale.ui.newreport.network.IncomeReportService;
import com.honeywell.wholesale.ui.newreport.network.MonthlyIncomeEntity;
import com.honeywell.wholesale.ui.newreport.network.MonthlyIncomeResponse;
import com.honeywell.wholesale.ui.report.BaseFragment;
import com.honeywell.wholesale.ui.report.ChartSubListCommonItem;
import com.honeywell.wholesale.ui.report.adapter.CommonSubChartListAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.greenrobot.event.EventBus;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by xiaofei on 9/21/16.
 */
public class IncomeChartFragment extends BaseFragment {
    private static final String TAG = "IncomeChartFragment";

    public static final int INCOME_REPORT_DAILY = 1;
    public static final int INCOME_REPORT_MONTHLY = 2;
    public static final int INCOME_REPORT_YEARLY = 3;

    private static int mCurrentReportType = INCOME_REPORT_DAILY;

    private Retrofit retrofit;
    private IncomeReportService incomeReportService;

    private ArrayList<DailyIncomeEntity> dailyList;
    private ArrayList<MonthlyIncomeEntity> monthList;
    private ArrayList<AnnualIncomeEntity> annualList;

    private int mLastDailyEndDayDiff = 0;
    private int mLastDailyEndTime = 0;

    private int mLastMonthlyEndDayDiff = 0;
    private int mLastMonthlyEndTime = 0;

    private int mLastAnnualEndDayDiff = 0;
    private int mLastAnnualEndTime = 0;


    private ArrayList<ChartSubListCommonItem> subListViewItemList = new ArrayList<>();
    private CommonSubChartListAdapter subChartListAdapter;

    private CombinedChart mChart;
    private ListView incomeSubListView;

    private ProgressDialog mProgressDialog;

    private int mCurrentDataStartIndex = 0;
    private int mCurrentHighlightIndex = mCurrentDataStartIndex;

    private final int REPORT_DAILY_DATA_WINDOW_SIZE = 30;
    private final int REPORT_MONTHLY_DATA_WINDOW_SIZE = 12;
    private final int REPORT_ANNUAL_DATA_WINDOW_SIZE = 10;

    public static final float MIN_TRANS_X_0F = 0.0f;
    private final int SCREEN_WINDOW_SIZE = 6;
    private List<IncomeReportData> mIncomeReportDataList = new ArrayList<IncomeReportData>();

    private float mLastTransX = -1;
    private float mLastMotionEventX = -1;

    private boolean mIsDataReloadedWhenTouching = false;

    private BaseTextView mIncomeFormatTextView;

    private Context context;

    public void setIncomeFormatTextView(BaseTextView incomeFormatTextView) {
        mIncomeFormatTextView = incomeFormatTextView;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_income_chart, container, false);
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.e(TAG,"onViewCreated");
        initialView(view);
        setSummaryList(0, INCOME_REPORT_DAILY);
        initReportData(INCOME_REPORT_DAILY);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    public void switchReportType(int reportType) {
        switch (reportType) {
            case INCOME_REPORT_DAILY:
                mLastMonthlyEndDayDiff = 0;
                mLastAnnualEndDayDiff = 0;
                break;

            case INCOME_REPORT_MONTHLY:
                mLastDailyEndDayDiff = 0;
                mLastAnnualEndDayDiff = 0;
                break;

            case INCOME_REPORT_YEARLY:
                mLastDailyEndDayDiff = 0;
                mLastMonthlyEndDayDiff = 0;
                break;
            default:
                break;
        }

        initReportData(reportType);
    }

    /**
     * Get report data from cloud, and redraw the cart.
     *
     * @param reportType INCOME_REPORT_DAILY, INCOME_REPORT_MONTHLY, INCOME_REPORT_YEARLY
     */
    public void initReportData(int reportType) {
        mCurrentReportType = reportType;
        mCurrentDataStartIndex = 0;
        mCurrentHighlightIndex = 0;
        mProgressDialog.show();
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

    private void initialView(View view) {
        mChart = (CombinedChart) view.findViewById(R.id.income_chart);
        incomeSubListView = (ListView) view.findViewById(R.id.income_summary_listview);
        if (subChartListAdapter == null) {
            subChartListAdapter = new CommonSubChartListAdapter(subListViewItemList, getContext());
        }
        incomeSubListView.setAdapter(subChartListAdapter);

        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setTitle("");
        mProgressDialog.setMessage("加载中...");
        mProgressDialog.setCancelable(true);
    }

    private void setSummaryList(int currentDateIndex, int reportType) {
        subListViewItemList.clear();
        IncomeReportData incomeData = null;
        if (currentDateIndex > 0 && currentDateIndex < mIncomeReportDataList.size()) {
            incomeData = mIncomeReportDataList.get(currentDateIndex);
        }

        if (incomeData == null) {
            return;
        }

        Log.v(TAG, "选中: currentDateIndex=" + currentDateIndex + ", incomeData=" + incomeData.toString());

        ArrayList<String> valueList = new ArrayList<>();
        ArrayList<String> titleList = null;
        switch (reportType) {
            case INCOME_REPORT_DAILY:
                valueList.add("￥" + incomeData.mIncome);
                valueList.add("￥" + incomeData.mProfit);
                valueList.add(String.valueOf(incomeData.mDeals) + "笔");
                valueList.add("￥" + incomeData.mNewOutstandings + "   共" + incomeData.mNewOutstandingBills + "笔");
                valueList.add("￥" + incomeData.mSettleOutstandings + "   共" + incomeData.mSettleOutstandingBills + "笔");

                if (getActivity() == null){
                    titleList = new ArrayList<>();
                }else {
                    titleList = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.income_daily_sub_list)));
                }
                // 设置时间显示
                mIncomeFormatTextView.setText(WSTimeStamp.getYearMonthDayString(incomeData.mTimeStamp));
                break;

            case INCOME_REPORT_MONTHLY:
                valueList.add("￥" + incomeData.mIncome);
                valueList.add("￥" + incomeData.mProfit);
                valueList.add(String.valueOf(incomeData.mDeals) + "笔");
                valueList.add("￥" + incomeData.mNewOutstandings + "   共" + incomeData.mNewOutstandingBills + "笔");
                valueList.add("￥" + incomeData.mSettleOutstandings + "   共" + incomeData.mSettleOutstandingBills + "笔");
                valueList.add(incomeData.mNewCustomers + "个");
                titleList = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.income_monthly_sub_list)));
                // 设置时间显示
                mIncomeFormatTextView.setText(WSTimeStamp.getYearMonthString(incomeData.mTimeStamp));
                break;

            case INCOME_REPORT_YEARLY:
                valueList.add("￥" + incomeData.mIncome);
                valueList.add("￥" + incomeData.mProfit);
                valueList.add(String.valueOf(incomeData.mDeals) + "笔");
                valueList.add("￥" + incomeData.mNewOutstandings + "   共" + incomeData.mNewOutstandingBills + "笔");
                valueList.add("￥" + incomeData.mSettleOutstandings + "   共" + incomeData.mSettleOutstandingBills + "笔");
                valueList.add(incomeData.mNewCustomers + "个");
                titleList = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.income_yearly_sub_list)));
                // 设置时间显示
                mIncomeFormatTextView.setText(WSTimeStamp.getYearString(incomeData.mTimeStamp));
                break;
            default:
                break;
        }

        for (int i = 0; i < titleList.size(); i++) {
            ChartSubListCommonItem item = new ChartSubListCommonItem(titleList.get(i), valueList.size() == 0 ? "-" : valueList.get(i));
            subListViewItemList.add(item);
        }
        subChartListAdapter.setDataSource(subListViewItemList);
        subChartListAdapter.notifyDataSetChanged();
    }

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
                    mProgressDialog.dismiss();
                    return;
                }
                if(response.body() == null){
                    Log.e(TAG, "Cloud response body is null!!!");
                    return;
                }
                Log.e("data", new Gson().toJson(response.body()));
                HttpResult httpResult = response.body();
                DailyIncomeResponse report = (DailyIncomeResponse) httpResult.getData();
                dailyList = report.getIncomeList();

                prepareReportData(INCOME_REPORT_DAILY);
                drawChart(mCurrentDataStartIndex, INCOME_REPORT_DAILY);

                Log.e("data", new Gson().toJson(response.body()));
            }

            @Override
            public void onFailure(Call<HttpResult<DailyIncomeResponse<DailyIncomeEntity>>> call, Throwable t) {
                if (context != null){
                    Toast.makeText(context, "获取数据失败", Toast.LENGTH_SHORT).show();
                }

                mProgressDialog.dismiss();
            }
        });
    }

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
                    mProgressDialog.dismiss();
                    return;
                }
                Log.e("data", new Gson().toJson(response.body()));
                HttpResult httpResult = response.body();
                MonthlyIncomeResponse report = (MonthlyIncomeResponse) httpResult.getData();
                monthList = report.getIncomeList();

                prepareReportData(INCOME_REPORT_MONTHLY);
                drawChart(mCurrentDataStartIndex, INCOME_REPORT_MONTHLY);

                Log.e("data", new Gson().toJson(response.body()));
            }

            @Override
            public void onFailure(Call<HttpResult<MonthlyIncomeResponse<MonthlyIncomeEntity>>> call, Throwable t) {

                Toast.makeText(getContext(), "获取数据失败", Toast.LENGTH_SHORT).show();
                mProgressDialog.dismiss();
            }
        });

    }

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
                    mProgressDialog.dismiss();
                    return;
                }
                Log.e("data", new Gson().toJson(response.body()));
                HttpResult httpResult = response.body();
                AnnualReportResponse report = (AnnualReportResponse) httpResult.getData();
                annualList = report.getIncomeList();

                prepareReportData(INCOME_REPORT_YEARLY);
                drawChart(mCurrentDataStartIndex, INCOME_REPORT_YEARLY);

                Log.e("data", new Gson().toJson(response.body()));
            }

            @Override
            public void onFailure(Call<HttpResult<AnnualReportResponse<AnnualIncomeEntity>>> call, Throwable t) {
                Toast.makeText(getContext(), "获取数据失败", Toast.LENGTH_SHORT).show();
                mProgressDialog.dismiss();
            }
        });
    }

    private String getToken() {
        Account currentUser = AccountManager.getInstance().getCurrentAccount();
        String s = currentUser.getToken();
        return "token=" + s;
    }

    @Override
    public void setDays(int days) {

    }

    @Override
    public void setMonths(int months) {

    }

    @Override
    public void setYears(int years) {

    }

    @Override
    public void setCondition(String condition) {

    }

    private void initChart() {
        //在chart上的右下角加描述
        mChart.setDescription("");
        mChart.setBackgroundColor(Color.WHITE);
        mChart.setDrawBarShadow(false);
        mChart.setHighlightFullBarEnabled(false);
        // 是否可以缩放 x和y轴, 默认是true
        mChart.setScaleYEnabled(false);
        mChart.setScaleXEnabled(true);

        // 设置是否可以拖拽，缩放
        mChart.setDragEnabled(true);
        mChart.setScaleEnabled(false);
        //是否启用网格背景
        mChart.setDrawGridBackground(false);
        mChart.setHighlightPerDragEnabled(true);

        mChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                Log.v(TAG, "选中:" + e.toString() + ", h=" + h.toString());
                setSummaryList((int) e.getX(), mCurrentReportType);
//                mChart.highlightValue(h);
            }

            @Override
            public void onNothingSelected() {

            }
        });
        mChart.setHorizontalScrollBarEnabled(true);

        // draw bars behind lines
        mChart.setDrawOrder(new CombinedChart.DrawOrder[]{
                CombinedChart.DrawOrder.BAR, CombinedChart.DrawOrder.BUBBLE, CombinedChart.DrawOrder.CANDLE, CombinedChart.DrawOrder.LINE, CombinedChart.DrawOrder.SCATTER
        });

        mChart.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        mLastTransX = -1;
                        mLastMotionEventX = -1;
                        mIsDataReloadedWhenTouching = false;
                        break;
                    case MotionEvent.ACTION_MOVE:
                        // 当用户手点下拖动到抬起来,或者取消拖动之内,如果数据已经移动过,就不再刷新。
                        if (mIsDataReloadedWhenTouching) {
                            return false;
                        }
                        // X轴上没有移动,或者移动距离小于1
                        int xDiff = Math.abs((int) (mLastMotionEventX - event.getX()));
                        if (xDiff < 5) {
                            return false;
                        }
//                        boolean isChartMoveToLeft = mLastMotionEventX > event.getX();
                        mLastMotionEventX = event.getX();

                        //===================Start=================================
                        // 下面的逻辑用来判断是否报表被滚动到了边界
                        ViewPortHandler handler = mChart.getViewPortHandler();
                        float transX = handler.getTransX();
                        float[] matrixBuffer = new float[9];
                        handler.getMatrixTouch().getValues(matrixBuffer);
                        float curScaleX = matrixBuffer[Matrix.MSCALE_X];
                        float mMinScaleX = handler.getMinScaleX();
                        float mMaxScaleX = handler.getMaxScaleX();
                        // min scale-x is 1f
                        float scaleX = Math.min(Math.max(mMinScaleX, curScaleX), mMaxScaleX);
                        // width = 614, mScaleX = 6;
                        float chartContentWidth = handler.contentWidth();
                        float maxTransX = -chartContentWidth * (scaleX - 1f);
                        // 下面逻辑用来判断是否上次已经滚动报表的边界,避免多次请求数据
                        if (mLastTransX == transX || (mLastTransX == MIN_TRANS_X_0F && transX == maxTransX) || (transX == MIN_TRANS_X_0F && mLastTransX == maxTransX)) {
                            return false;
                        }
                        mLastTransX = transX;
                        // 滚动表表到了数据的最两边,需要去服务器请求更新的数据
                        if (transX == MIN_TRANS_X_0F) {
                            // 滑动到最左边
                            moveRight();
                        } else if (transX == maxTransX) {
                            // 滑动到最右边
                            moveLeft();
                        } else {
                            // 图表在滑动的过程中联动图表下方的列表里面的数据显示,把屏幕当前可见的最大的值作为默认选中值
                            int highestVisibleX = Math.round(mChart.getHighestVisibleX());
                            mCurrentHighlightIndex = highestVisibleX;
                            setSummaryList(mCurrentHighlightIndex, mCurrentReportType);
                            mChart.highlightValue(mCurrentHighlightIndex, mCurrentHighlightIndex);
                        }
                        //===================End=================================
                        break;

                    default:
                        break;
                }

                return false;
            }
        });

        mChart.invalidate();
    }

    private void drawChart(int startIndex, int reportType) {
        mChart.clear();

        initChart();
        if (startIndex < 0 || startIndex > mIncomeReportDataList.size() - 1) {
            Log.v("MZ", "到达数据边界,已经没有更多值");
            return;
        }

        mIsDataReloadedWhenTouching = true;


        List<Entry> lineEntryList = new ArrayList<>();
        List<BarEntry> incomeBarEntryList = new ArrayList<>();
        List<BarEntry> profitBarEntryList = new ArrayList<>();

        Map<Float, String> xAxisDataMap = new HashMap<Float, String>();
        IncomeReportData incomeReportData;
        for (int i = 0; i < mIncomeReportDataList.size(); i++) {
            incomeReportData = mIncomeReportDataList.get(i);
            switch (reportType) {
                case INCOME_REPORT_DAILY:
                    xAxisDataMap.put((float) i, WSTimeStamp.getMonthDateString(incomeReportData.mTimeStamp));
                    break;
                case INCOME_REPORT_MONTHLY:
                    xAxisDataMap.put((float) i, WSTimeStamp.getMonthString(incomeReportData.mTimeStamp));
                    break;
                case INCOME_REPORT_YEARLY:
                    xAxisDataMap.put((float) i, WSTimeStamp.getYearString(incomeReportData.mTimeStamp));
                    break;
                default:
                    break;
            }
            // 曲线的X加上0.5是为了让曲线的点剧中在bar图内部
            lineEntryList.add(new Entry(i + 0.5f, incomeReportData.mDeals));
            incomeBarEntryList.add(new BarEntry((float) i, incomeReportData.mIncome));
            profitBarEntryList.add(new BarEntry((float) i, incomeReportData.mProfit));
        }

//        // 柱状图的数据准备
//        LineData lineData = new LineData();
//        LineDataSet lineDataSet = new LineDataSet(lineEntryList, "Line DataSet");
//        lineDataSet.setColor(Color.rgb(210, 105, 30));
//        lineDataSet.setLineWidth(2.5f);
//        lineDataSet.setCircleColor(Color.rgb(210, 105, 30));
//        lineDataSet.setCircleRadius(5f);
//        lineDataSet.setFillColor(Color.rgb(210, 105, 30));
//        lineDataSet.setMode(LineDataSet.Mode.LINEAR);
//        lineDataSet.setDrawValues(false);
//        lineDataSet.setValueTextSize(10f);
//        lineDataSet.setValueTextColor(Color.rgb(210, 105, 30));
//        lineDataSet.setHighLightColor(Color.rgb(250, 3, 44));
//        // set this to false to disable the drawing of highlight indicator (lines)
//        // 这个如果开启了,点曲线上的圆圈,会出现红色十字线
//        lineDataSet.setDrawHighlightIndicators(false);
//        lineData.setHighlightEnabled(true);
//        lineDataSet.setAxisDependency(YAxis.AxisDependency.RIGHT);
//        lineDataSet.setLabel("交易数量");
//        lineData.addDataSet(lineDataSet);

        BarDataSet incomeBarDataSet = new BarDataSet(incomeBarEntryList, "Bar1");
        incomeBarDataSet.setColor(Color.rgb(60, 220, 78));
        incomeBarDataSet.setValueTextColor(Color.rgb(60, 220, 78));
        incomeBarDataSet.setValueTextSize(10f);
        incomeBarDataSet.setDrawValues(false);
        incomeBarDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        // color for highlight indicator
        incomeBarDataSet.setHighLightColor(Color.rgb(250, 3, 44));
        incomeBarDataSet.setLabel("销售额");

        BarDataSet profitBarDataSet = new BarDataSet(profitBarEntryList, "Bar2");
        profitBarDataSet.setStackLabels(new String[]{"Stack 1"});
        profitBarDataSet.setColor(Color.rgb(61, 165, 255));
        profitBarDataSet.setValueTextColor(Color.rgb(61, 165, 255));
        profitBarDataSet.setValueTextSize(10f);
        profitBarDataSet.setDrawValues(false);
        profitBarDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        // color for highlight indicator
        profitBarDataSet.setHighLightColor(Color.rgb(250, 3, 44));
        profitBarDataSet.setLabel("利润");

        float groupSpace = 0.06f;
        float barSpace = 0.02f; // x2 dataset
        float barWidth = 0.45f; // x2 dataset
        // (0.45 + 0.02) * 2 + 0.06 = 1.00 -> interval per "group"
        BarData barData = new BarData(incomeBarDataSet, profitBarDataSet);
        barData.setBarWidth(barWidth);
        // make this BarData object grouped, 设置Bar的起始位置
        barData.groupBars(0, groupSpace, barSpace); // start at x = 0

        float incomeBarYMin = incomeBarDataSet.getYMin();
        float incomeBarYMax = incomeBarDataSet.getYMax();
        float profitBarYMin = profitBarDataSet.getYMin();
        float profitBarYMax = profitBarDataSet.getYMax();

        if (incomeBarYMin == incomeBarYMax) {
            if (incomeBarYMin == 0) {
                incomeBarYMax = 10000;
            } else if (incomeBarYMin < 0) {
                incomeBarYMax = 0;
            } else if (incomeBarYMin > 0) {
                incomeBarYMin = 0;
            }
        }
        if (profitBarYMin == profitBarYMax) {
            if (profitBarYMin == 0) {
                profitBarYMax = 1000;
            } else if (profitBarYMin < 0) {
                profitBarYMax = 0;
            } else if (profitBarYMin > 0) {
                profitBarYMin = 0;
            }
        }
        incomeBarYMax = incomeBarYMax + ((incomeBarYMax - incomeBarYMin) * 0.1f);
        profitBarYMax = profitBarYMax + ((profitBarYMax - profitBarYMin) * 0.1f);

        CombinedData combinedData = new CombinedData();
//        combinedData.setData(lineData);
        combinedData.setData(barData);

        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.setTextColor(incomeBarDataSet.getColor());
        leftAxis.setAxisMaxValue(incomeBarYMax);
        leftAxis.setAxisMinValue(incomeBarYMin);
        leftAxis.setDrawGridLines(true);
        leftAxis.setGranularityEnabled(true);
        leftAxis.setMaxWidth(30.0f);
        leftAxis.setValueFormatter(new YAxisLeftValueFormatter());

        YAxis rightAxis = mChart.getAxisRight();
        rightAxis.setTextColor(profitBarDataSet.getColor());
        rightAxis.setAxisMaxValue(profitBarYMax);
        rightAxis.setAxisMinValue(profitBarYMin);
        rightAxis.setDrawGridLines(false);
        rightAxis.setDrawZeroLine(false);
        rightAxis.setGranularityEnabled(true);
        rightAxis.setMaxWidth(30.0f);
        rightAxis.setValueFormatter(new YAxisLeftValueFormatter());

        // X轴
        XAxis xAxis = mChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setAxisMaxValue(combinedData.getXMax() + 1);
        xAxis.setAxisMinValue(combinedData.getXMin());
        xAxis.setValueFormatter(new XAxisValueFormatter(xAxisDataMap));
        xAxis.setCenterAxisLabels(true);
        xAxis.setAvoidFirstLastClipping(true);

        // 图例说明
        Legend legend = mChart.getLegend();
        legend.setEnabled(true);
        legend.setWordWrapEnabled(false);
        legend.setPosition(Legend.LegendPosition.BELOW_CHART_CENTER);
        legend.setForm(Legend.LegendForm.SQUARE);
        legend.setFormSize(9f);
        legend.setTextSize(11f);
        legend.setXEntrySpace(10f);

        mChart.setScaleXEnabled(true);
//        // 设置缩放比例。这方法可以用来控制左右滚动
        LogHelper.getInstance().d(TAG, "mIncomeReportDataList.size()=" + mIncomeReportDataList.size());
        mChart.setVisibleXRangeMaximum(SCREEN_WINDOW_SIZE);
        mChart.setVisibleXRangeMinimum(SCREEN_WINDOW_SIZE);

        final float xMin = combinedData.getXMin();
        final float xMax = combinedData.getXMax();
        // 这里非常重要,决定加载后的数据是否显示的流程,这个没算好会导致滑动到边缘的时候数据跳跃。移动到报表的最中间
//        final float xAxisMiddle = xMin + (xMax - xMin) / 2 - SCREEN_WINDOW_SIZE / 2;
//        Log.v("MZ", "xAxisMiddle=" + xAxisMiddle);
        switch (reportType) {
            case INCOME_REPORT_DAILY:
                mCurrentHighlightIndex = (int) xMax + mLastDailyEndDayDiff;
                break;
            case INCOME_REPORT_MONTHLY:
                mCurrentHighlightIndex = (int) xMax + mLastMonthlyEndDayDiff;
                break;
            case INCOME_REPORT_YEARLY:
                mCurrentHighlightIndex = (int) xMax + mLastAnnualEndDayDiff;
                break;
            default:
                break;
        }

        // TODO 这个方法不起作用,一直没找到解决办法
        // 将左边的边放到指定的位置
        mChart.moveViewToX(mCurrentHighlightIndex);
        Log.v("MZ", " mChart.moveViewToX(" + mCurrentHighlightIndex + ")" + ", mCurrentHighlightIndex=" + mCurrentHighlightIndex);

//        // let the chart know it's data changed
        mChart.setData(combinedData);
        // refresh
        mChart.invalidate();
        mChart.refreshDrawableState();

        setSummaryList(mCurrentHighlightIndex, reportType);
        mProgressDialog.dismiss();
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
                mIncomeReportDataList.clear();

                for (int index = 0; index < dailyList.size(); index++) {
                    DailyIncomeEntity dailyIncome = dailyList.get(index);
                    Log.v(TAG, "第" + index + "个: " + dailyIncome.getTimeStamp() + " = " + WSTimeStamp.getFullTimeString(dailyIncome.getTimeStamp()));

                    mIncomeReportDataList.add(new IncomeReportData(dailyIncome.getTimeStamp(), dailyIncome.getIncome(),
                            dailyIncome.getProfit(), dailyIncome.getDeals(), dailyIncome.getNewOutstandings(),
                            dailyIncome.getNewOutstandingBills(), dailyIncome.getSettleOutstandings(), dailyIncome.getSettleOutstandingBills(),
                            dailyIncome.getNewCustomers()));
                }
                break;

            case INCOME_REPORT_MONTHLY:
                if (monthList == null) {
                    Toast.makeText(getContext(), "没有数据", Toast.LENGTH_SHORT).show();
                    return;
                }
                mIncomeReportDataList.clear();

                for (int index = 0; index < monthList.size(); index++) {
                    MonthlyIncomeEntity monthlyIncome = monthList.get(index);
                    mIncomeReportDataList.add(new IncomeReportData(monthlyIncome.getTimeStamp(), monthlyIncome.getIncome(),
                            monthlyIncome.getProfit(), monthlyIncome.getDeals(), monthlyIncome.getNewOutstandings(),
                            monthlyIncome.getNewOutstandingBills(), monthlyIncome.getSettleOutstandings(), monthlyIncome.getSettleOutstandingBills(),
                            monthlyIncome.getNewCustomers()));
                }
                break;

            case INCOME_REPORT_YEARLY:
                if (annualList == null) {
                    Toast.makeText(getContext(), "没有数据", Toast.LENGTH_SHORT).show();
                    return;
                }
                mIncomeReportDataList.clear();

                for (int index = 0; index < annualList.size(); index++) {
                    AnnualIncomeEntity annualIncome = annualList.get(index);
                    mIncomeReportDataList.add(new IncomeReportData(annualIncome.getTimeStamp(), annualIncome.getIncome(),
                            annualIncome.getProfit(), annualIncome.getDeals(), annualIncome.getNewOutstandings(),
                            annualIncome.getNewOutstandingBills(), annualIncome.getSettleOutstandings(), annualIncome.getSettleOutstandingBills(),
                            annualIncome.getNewCustomers()));
                }
                break;
            default:
                break;
        }
    }

    private void moveRight() {
        switch (mCurrentReportType) {
            case INCOME_REPORT_DAILY:
                // TODO 考虑网络异常,没请求到的逻辑
                mLastDailyEndDayDiff = mLastDailyEndDayDiff - REPORT_DAILY_DATA_WINDOW_SIZE;
                break;

            case INCOME_REPORT_MONTHLY:
                mLastMonthlyEndDayDiff = mLastMonthlyEndDayDiff - REPORT_MONTHLY_DATA_WINDOW_SIZE;
                break;

            case INCOME_REPORT_YEARLY:
                mLastAnnualEndDayDiff = mLastAnnualEndDayDiff - REPORT_ANNUAL_DATA_WINDOW_SIZE;
                break;
            default:
                break;
        }
        initReportData(mCurrentReportType);
    }

    private void moveLeft() {
        switch (mCurrentReportType) {
            case INCOME_REPORT_DAILY:
                if(mLastDailyEndDayDiff >= 0) {
                    return;
                }
                mLastDailyEndDayDiff = mLastDailyEndDayDiff + REPORT_DAILY_DATA_WINDOW_SIZE;
                break;

            case INCOME_REPORT_MONTHLY:
                if(mLastMonthlyEndDayDiff >= 0) {
                    return;
                }
                mLastMonthlyEndDayDiff = mLastMonthlyEndDayDiff + REPORT_MONTHLY_DATA_WINDOW_SIZE;
                break;

            case INCOME_REPORT_YEARLY:
                if(mLastAnnualEndDayDiff >= 0) {
                    return;
                }
                mLastAnnualEndDayDiff = mLastAnnualEndDayDiff + REPORT_ANNUAL_DATA_WINDOW_SIZE;
                break;
            default:
                break;
        }
        initReportData(mCurrentReportType);
    }

    public static class XAxisValueFormatter implements AxisValueFormatter {

        private Map<Float, String> mXAxisDataMap = new HashMap<Float, String>();

        public XAxisValueFormatter(Map<Float, String> xAxisDataMap) {
            mXAxisDataMap = xAxisDataMap;
        }

        @Override
        public String getFormattedValue(float value, AxisBase axis) {
            return mXAxisDataMap.get(value);
        }

        @Override
        public int getDecimalDigits() {
            return 0;
        }
    }

    public static class YAxisLeftValueFormatter implements AxisValueFormatter {

        public YAxisLeftValueFormatter() {
        }

        @Override
        public String getFormattedValue(float value, AxisBase axis) {
            if (value > 10000) {
                return ((int) value / 10000) + "万";
            } if (value > 1000) {
                return ((int) value / 1000) + "千";
            } else {
                return String.valueOf((int) value);
            }
        }

        @Override
        public int getDecimalDigits() {
            return 0;
        }
    }

    public static class IncomeReportData {
        public long mTimeStamp;
        public float mIncome;
        public float mProfit;
        public int mDeals;
        public float mNewOutstandings;
        public int mNewOutstandingBills;
        public float mSettleOutstandings;
        public int mSettleOutstandingBills;
        public int mNewCustomers;

        public IncomeReportData(long timeStamp,
                                float income,
                                float profit,
                                int deals,
                                float newOutstandings,
                                int newOutstandingBills,
                                float settleOutstandings,
                                int settleOutstandingBills,
                                int newCustomers) {
            mTimeStamp = timeStamp;
            mIncome = income;
            mProfit = profit;
            mDeals = deals;
            mNewOutstandings = newOutstandings;
            mNewOutstandingBills = newOutstandingBills;
            mSettleOutstandings = settleOutstandings;
            mSettleOutstandingBills = settleOutstandingBills;
            mNewCustomers = newCustomers;
        }

        @Override
        public String toString() {
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append("mTimeStamp=");
            stringBuffer.append(WSTimeStamp.getFullTimeString(mTimeStamp));
            stringBuffer.append(",mIncome=");
            stringBuffer.append(mIncome);
            stringBuffer.append(",mProfit=");
            stringBuffer.append(mProfit);
            stringBuffer.append(",mDeals=");
            stringBuffer.append(mDeals);
            stringBuffer.append(",mNewOutstandings=");
            stringBuffer.append(mNewOutstandings);
            stringBuffer.append(",mNewOutstandingBills=");
            stringBuffer.append(mSettleOutstandings);
            stringBuffer.append(",mSettleOutstandings=");
            stringBuffer.append(mSettleOutstandingBills);
            stringBuffer.append(",mSettleOutstandingBills=");
            stringBuffer.append(mNewCustomers);
            stringBuffer.append(",mNewCustomers=");
            return stringBuffer.toString();
        }
    }

    @Override
    public void toggle() {

    }

    @Override
    public boolean isShownChart() {
        return true;
    }

    public void clickMoveChartToLeftBtn() {
        if (mCurrentHighlightIndex > 0) {
            mCurrentHighlightIndex = mCurrentHighlightIndex - 1;
            Log.e("MZ", "xPosition=" + mCurrentHighlightIndex);
        } else {
            return;
        }

        // move to the certain data position
        mChart.moveViewToX(mCurrentHighlightIndex);
        setSummaryList(mCurrentHighlightIndex, mCurrentReportType);
    }

    public void clickMoveChartToRightBtn() {
        if (mCurrentHighlightIndex < mIncomeReportDataList.size()) {
            mCurrentHighlightIndex = mCurrentHighlightIndex + 1;
            Log.e("MZ", "xPosition=" + mCurrentHighlightIndex);
        } else {
            return;
        }

        // move to the certain data position
        mChart.moveViewToX(mCurrentHighlightIndex);
        setSummaryList(mCurrentHighlightIndex, mCurrentReportType);
    }

    public String getCurrentDateStr() {
        if (mCurrentHighlightIndex > 0 && mCurrentHighlightIndex < mIncomeReportDataList.size()) {
            IncomeReportData incomeReportData = mIncomeReportDataList.get(mCurrentHighlightIndex);
            return WSTimeStamp.getYearMonthDayString(incomeReportData.mTimeStamp);
        } else {
            Log.e(TAG, "mCurrentHighlightIndex=" + mCurrentHighlightIndex + ", 不在范围内");
            return "";
        }
    }

    public void onEventMainThread(SwitchFragmentEvent switchFragmentEvent){
        String msg = switchFragmentEvent.getmMsg();
        Log.e(TAG,msg);
        if (msg.equals(String.valueOf(R.string.report_fragment_switch))){
            subListViewItemList.clear();
            mIncomeReportDataList.clear();
        }
    }

}
