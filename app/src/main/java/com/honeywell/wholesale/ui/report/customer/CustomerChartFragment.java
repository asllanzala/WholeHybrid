package com.honeywell.wholesale.ui.report.customer;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
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
import com.honeywell.wholesale.ui.report.AxisMoneyFormatter;
import com.honeywell.wholesale.ui.report.AxisPercentFormatter;
import com.honeywell.wholesale.ui.report.AxisStringFormatter;
import com.honeywell.wholesale.ui.report.BaseFragment;
import com.honeywell.wholesale.ui.report.ChartSubListCommonItem;
import com.honeywell.wholesale.ui.report.adapter.CommonSubChartListAdapter;
import com.honeywell.wholesale.ui.report.adapter.ReportCustomerListAdapter;


import java.util.ArrayList;
import java.util.Arrays;

import de.greenrobot.event.EventBus;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by xiaofei on 9/21/16.
 *
 */
public class CustomerChartFragment extends BaseFragment implements OnChartValueSelectedListener {
    private static final String TAG = "CustomerChartFragment";
    private View customerChartLayout, customerListLayout;

    private Retrofit retrofit;
    private ArrayList<CustomerChartEntity> customerChartList = new ArrayList();

    private CombinedChart customerChart;
    private ListView customerSubListView;
    private CommonSubChartListAdapter subChartListAdapter;

    private ListView reportCustomerListView;
    private ReportCustomerListAdapter reportCustomerListAdapter;

    private ArrayList<ChartSubListCommonItem> subListViewItemList = new ArrayList<>();

    private String currentStartTime;
    private CustomerReportRequest.TopCondition currentTopCondition;

    private ArrayList<String> customerConditionList;
    private Context context;

    private boolean isLoaded = false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_customer_chart, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.e(TAG,"onViewCreated");
        initialData();
        initialView(view);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private void initialView(View view){
        customerChartLayout = view.findViewById(R.id.customer_chart_layout);
        customerListLayout = view.findViewById(R.id.customer_list_layout);

        customerChart = (CombinedChart)view.findViewById(R.id.customer_chart);
        customerSubListView = (ListView)view.findViewById(R.id.customer_chart_listView);
        customerSubListView.setAdapter(subChartListAdapter);

        reportCustomerListView = (ListView)view.findViewById(R.id.report_customer_listview);
        reportCustomerListView.setAdapter(reportCustomerListAdapter);

        initialChart();

    }

    private void initialChart(){
        customerChart.setDescription("");
        customerChart.setTouchEnabled(false);
        customerChart.setDragEnabled(false);
        customerChart.setScaleEnabled(false);
        customerChart.setPinchZoom(false);
        customerChart.setHighlightFullBarEnabled(false);

        customerChart.setOnChartValueSelectedListener(this);
        customerChart.setDrawBarShadow(false);
        customerChart.setDrawValueAboveBar(true);

        customerChart.setDrawOrder(new CombinedChart.DrawOrder[]{
                CombinedChart.DrawOrder.BAR, CombinedChart.DrawOrder.BAR, CombinedChart.DrawOrder.BAR
        });

        // if more than 60 entries are displayed in the chart, no values will be
        // drawn
        customerChart.setMaxVisibleValueCount(25);

        Legend l = customerChart.getLegend();
        boolean b = l.isEnabled();
        l.setEnabled(true);
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);

        if (isLoaded){
            setChartAxis();
        }
    }

    private void initialData(){
        if (subChartListAdapter == null){
            subChartListAdapter = new CommonSubChartListAdapter(subListViewItemList, getContext());
        }

        if (reportCustomerListAdapter == null){
            reportCustomerListAdapter = new ReportCustomerListAdapter(customerChartList, getContext());
        }

        if (currentTopCondition == null){
            currentTopCondition = CustomerReportRequest.TopCondition.INCOME;
        }

        if (currentStartTime == null || currentStartTime.isEmpty()){
            
            try {
                initialWebClient();
            } catch (Exception e) {
                e.printStackTrace();
            }
            String startTime = String.valueOf(WSTimeStamp.getSpecialDayStartTime(-7));
            currentStartTime = startTime;
            CustomerReportRequest request = buildRequest(startTime, currentTopCondition);
            getChartDataFromWeb(request);
        }
    }

    private void setChartAxis(){
        isLoaded = true;

        ArrayList<String> customerName = new ArrayList<>();

        // 总销售额数据列表
        ArrayList<BarEntry> yVals1 = new ArrayList<>();
        // 总销售额占比
        ArrayList<BarEntry> yVals3 = new ArrayList<>();

        // 总利润额数据列表
        ArrayList<BarEntry> yVals2 = new ArrayList<>();
        // 总利润额占比
        ArrayList<BarEntry> yVals4 = new ArrayList<>();

        // 赊账额数据列表
        ArrayList<BarEntry> yVals5 = new ArrayList<>();


        for (int i = 0 ; i < customerChartList.size() ; i++){
            CustomerChartEntity item = customerChartList.get(i);
            customerName.add(item.getCustomerName());
            yVals1.add(new BarEntry(i, Float.valueOf(item.getIncomeAmount())));
            yVals3.add(new BarEntry(i, Float.valueOf(item.getIncomeRatio())));

            yVals2.add(new BarEntry(i, Float.valueOf(item.getProfitAmount())));
            yVals4.add(new BarEntry(i, Float.valueOf(item.getProfitRatio())));

            yVals5.add(new BarEntry(i, Float.valueOf(item.getReceivable())));
        }

        // set x, y and legend
        AxisStringFormatter xAxisFormatter = new AxisStringFormatter(customerName);
        XAxis xAxis = customerChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setAxisMinValue(0);
        xAxis.setAxisMaxValue(5);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f); // only intervals of 1 day
//        xAxis.setLabelCount(5);
        xAxis.setCenterAxisLabels(true);
        xAxis.setValueFormatter(xAxisFormatter);


        AxisMoneyFormatter yAxisFormatter = new AxisMoneyFormatter();
        YAxis leftAxis = customerChart.getAxisLeft();
        leftAxis.setLabelCount(8, false);
        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        leftAxis.setSpaceTop(15f);
        leftAxis.setAxisMinValue(0f);
        leftAxis.setValueFormatter(yAxisFormatter);
        // this replaces setStartAtZero(true)

        AxisPercentFormatter yrAxisFormatter = new AxisPercentFormatter();
        YAxis rightAxis = customerChart.getAxisRight();
        rightAxis.setDrawGridLines(false);
        rightAxis.setAxisMinValue(0);
        rightAxis.setAxisMaxValue(1);
        rightAxis.setSpaceTop(15f);
        rightAxis.setValueFormatter(yrAxisFormatter);

        float groupSpace = 0.06f;
        float barSpace = 0.02f; // x2 dataset
        float barWidth = 0.45f; // x2 dataset
        // (0.45 + 0.02) * 2 + 0.06 = 1.00 -> interval per "group"
        BarDataSet set1, set2, set3;

        if (customerChart.getBarData() != null &&
                customerChart.getBarData().getDataSetCount() > 0){
            set1 = (BarDataSet)customerChart.getBarData().getDataSetByIndex(0);
            set3 = (BarDataSet)customerChart.getBarData().getDataSetByIndex(1);

            if (currentTopCondition.equals(CustomerReportRequest.TopCondition.INCOME)){
                set1.setValues(yVals1);
                set3.setValues(yVals3);
                set1.setLabel("交易额");
                set3.setLabel("交易占比");
            }

            if (currentTopCondition.equals(CustomerReportRequest.TopCondition.PROFIT)){
                set1.setValues(yVals2);
                set3.setValues(yVals4);
                set1.setLabel("利润额");
                set3.setLabel("利润占比");
            }

            if (currentTopCondition.equals(CustomerReportRequest.TopCondition.RECEIVE)){
                set1.setValues(yVals5);
                set3.setValues(yVals3);
                set1.setLabel("赊账额");
                set3.setLabel("利润占比");
            }


            customerChart.getData().getBarData().setBarWidth(barWidth);
            customerChart.getData().getBarData().groupBars(0, groupSpace, barSpace);
            customerChart.getData().setDrawValues(false);

            customerChart.getData().notifyDataChanged();
            customerChart.notifyDataSetChanged();
            customerChart.invalidate();
        }else {
            set1 = new BarDataSet(yVals1, "Bar 1");
            set1.setColor(Color.rgb(60, 220, 78));
            set1.setValueTextColor(Color.rgb(60, 220, 78));
            set1.setValueTextSize(10f);
            set1.setAxisDependency(YAxis.AxisDependency.LEFT);
            set1.setLabel("交易额");

            set3 = new BarDataSet(yVals3, "Bar 3");
            set3.setColor(Color.rgb(23, 197, 255));
            set3.setValueTextColor(Color.rgb(161, 165, 255));
            set3.setValueTextSize(10f);
            set3.setAxisDependency(YAxis.AxisDependency.RIGHT);
            set3.setLabel("利润占比");

            ArrayList<IBarDataSet> dataSets = new ArrayList<>();
            dataSets.add(set1);
            dataSets.add(set3);

            BarData barData = new BarData(dataSets);
            barData.setBarWidth(barWidth);
            barData.setDrawValues(false);

            barData.groupBars(0, groupSpace, barSpace);
            CombinedData data = new CombinedData();
            data.setData(barData);
            customerChart.setData(data);
            customerChart.invalidate();
        }
    }

    private void generateBarData(){

    }

    private void initialWebClient() throws Exception{
        NetworkManager networkManager = NetworkManager.getInstance();

        String baseUrl = WebServerConfigManager.getWebServiceUrl();
        retrofit = new Retrofit.Builder()
                .client(networkManager.setSSL().build())
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();
    }

    private CustomerReportRequest buildRequest(String startTime, CustomerReportRequest.TopCondition topCondition){
        String shopId = AccountManager.getInstance().getCurrentAccount().getCurrentShopId();

        CustomerReportRequest customerReportRequest = new CustomerReportRequest(shopId, startTime);
        if (topCondition != null){
            customerReportRequest.setTopCondition(topCondition);
        }
        return customerReportRequest;
    }

    private void getChartDataFromWeb(CustomerReportRequest request){

        Account currentUser = AccountManager.getInstance().getCurrentAccount();
        String s = currentUser.getToken();
        String token = "token=" + s;

        CustomerReportService customerReportService = retrofit.create(CustomerReportService.class);

        Call<HttpResult<CustomerReportResponse<CustomerChartEntity>>> call = customerReportService.
                getDailyStatistic(token, request);

        call.enqueue(new Callback<HttpResult<CustomerReportResponse<CustomerChartEntity>>>() {
            @Override
            public void onResponse(Call<HttpResult<CustomerReportResponse<CustomerChartEntity>>> call, Response<HttpResult<CustomerReportResponse<CustomerChartEntity>>> response) {
                if(!response.isSuccessful()) {
                    LogHelper.getInstance().e(TAG, "server return error");
                    return;
                }

                Log.e("data",new Gson().toJson(response.body()));
                HttpResult httpResult = response.body();
                CustomerReportResponse report = (CustomerReportResponse)httpResult.getData();
                customerChartList = report.getCustomerList();

                reportCustomerListAdapter.setDataSource(customerChartList);
                reportCustomerListAdapter.notifyDataSetChanged();

                CustomerSummary customerSummary = report.getCustomerSummary();
                calcSubList(customerSummary);

                setChartAxis();
            }

            @Override
            public void onFailure(Call<HttpResult<CustomerReportResponse<CustomerChartEntity>>> call, Throwable t) {
                LogHelper.getInstance().e(TAG, t.getMessage());
            }
        });
    }

    private void calcSubList(CustomerSummary customerSummary){
        subListViewItemList.clear();
        Float income = 0f, incomeRatio = 0f, profit = 0f, profitRatio = 0f;
        for (CustomerChartEntity item :customerChartList) {
            income += Float.valueOf(item.getIncomeAmount());
            incomeRatio += Float.valueOf(item.getIncomeRatio());
            profit += Float.valueOf(item.getProfitAmount());
            profitRatio += Float.valueOf(item.getProfitRatio());
        }

        ArrayList<String> valueList = new ArrayList<>();

        String incomeString = "￥" + customerSummary.getIncomeAmount();
        valueList.add(incomeString);

        String incomeRatioString = String.valueOf(100 * customerSummary.getIncomeRatio()) + "%";
        valueList.add(incomeRatioString);

        String profitString = "￥" + customerSummary.getProfitAmount();
        valueList.add(profitString);

        String profitRatioString = String.valueOf(100 * customerSummary.getProfitRatio()) + "%";
        valueList.add(profitRatioString);

        ArrayList<String> titleList = new ArrayList<>();
        if (this.context != null){
            if (!isAdded()){
                return ;
            }
            titleList = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.customer_sub_list)));
        }


        for (int i = 0 ; i < titleList.size() ; i ++){
            ChartSubListCommonItem item = new ChartSubListCommonItem(titleList.get(i), String.valueOf(valueList.get(i)));
            subListViewItemList.add(item);
        }
        subChartListAdapter.setDataSource(subListViewItemList);
        subChartListAdapter.notifyDataSetChanged();
    }

    @Override
    public void setDays(int days) {
        String startTime = String.valueOf(WSTimeStamp.getSpecialDayStartTime(days));
        currentStartTime = startTime;
        CustomerReportRequest request = buildRequest(currentStartTime, currentTopCondition);
        getChartDataFromWeb(request);
    }

    @Override
    public void setMonths(int months) {
        String startTime = WSTimeStamp.getSpecialMonthStartTime(months);
        currentStartTime = startTime;
        CustomerReportRequest request = buildRequest(currentStartTime, currentTopCondition);
        getChartDataFromWeb(request);
    }

    @Override
    public void setYears(int years) {
        String startTime = WSTimeStamp.getSpecialYearsStartTime(years);
        currentStartTime = startTime;
        CustomerReportRequest request = buildRequest(currentStartTime, currentTopCondition);
        getChartDataFromWeb(request);
    }

    @Override
    public void setCondition(String condition) {
        customerConditionList = new ArrayList<>(Arrays.asList(getResources().
                getStringArray(R.array.report_select_customer)));

        if (customerConditionList.get(0).equals(condition)){
            currentTopCondition = CustomerReportRequest.TopCondition.INCOME;
        }

        if (customerConditionList.get(1).equals(condition)){
            currentTopCondition = CustomerReportRequest.TopCondition.PROFIT;
        }

        if (customerConditionList.get(2).equals(condition)){
            currentTopCondition = CustomerReportRequest.TopCondition.RECEIVE;
        }

        CustomerReportRequest request = buildRequest(currentStartTime, currentTopCondition);
        getChartDataFromWeb(request);
    }

    @Override
    public void toggle() {
        if (customerChartLayout.getVisibility() == View.VISIBLE){
            customerChartLayout.setVisibility(View.GONE);
            customerListLayout.setVisibility(View.VISIBLE);
        }else{
            customerChartLayout.setVisibility(View.VISIBLE);
            customerListLayout.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean isShownChart() {
        if (customerChartLayout.getVisibility() == View.VISIBLE){
            return true;
        }
        return false;
    }


    @Override
    public void onValueSelected(Entry e, Highlight h) {

    }

    @Override
    public void onNothingSelected() {

    }

    public void onEventMainThread(SwitchFragmentEvent switchFragmentEvent){
        String msg = switchFragmentEvent.getmMsg();
        Log.e(TAG,msg);
        if (msg.equals(String.valueOf(R.string.report_fragment_switch))){
            subListViewItemList.clear();
            customerChartList.clear();
        }
    }
}
