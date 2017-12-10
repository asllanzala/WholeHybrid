package com.honeywell.wholesale.ui.report.supplier;


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
import com.honeywell.wholesale.ui.report.adapter.ReportSupplierListAdapter;

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
public class SupplierChartFragment extends BaseFragment implements OnChartValueSelectedListener {
    private static final String TAG = "SupplierChartFragment";
    private Retrofit retrofit;
    private ArrayList<SupplierChartEntity> supplierList = new ArrayList<>() ;

    private CommonSubChartListAdapter subChartListAdapter;
    private ArrayList<ChartSubListCommonItem> subListViewItemList = new ArrayList<>();

    private CombinedChart supplierChart;
    private ListView supplierSubListView;

    private String currentStartTime;
    private SupplierReportRequest.TopCondition currentCondition;

    private ListView reportSupplierListView;
    private ReportSupplierListAdapter reportSupplierListAdapter;

    private ArrayList<String> supplierConditionList;

    private View reportLayout, listLayout;
    private Context context;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Nullable @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_supplier_chart, container, false);
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

    private void initialData(){
        if (subChartListAdapter == null){
            subChartListAdapter = new CommonSubChartListAdapter(subListViewItemList, getContext());
        }

        if (reportSupplierListAdapter == null){
            reportSupplierListAdapter = new ReportSupplierListAdapter(getContext(), supplierList);
        }

        if (currentCondition == null){
            currentCondition = SupplierReportRequest.TopCondition.AMOUNT;
        }

        if (currentStartTime == null || currentStartTime.isEmpty()){
            initialWebClient();
            String startTime = String.valueOf(WSTimeStamp.getSpecialDayStartTime(-7));
            currentStartTime = startTime;
            SupplierReportRequest request = buildRequest(startTime, null);
            getChartDataFromWeb(request);
        }
    }

    private void initialView(View view){
        reportLayout = view.findViewById(R.id.supplier_chart_layout);
        listLayout = view.findViewById(R.id.supplier_list_layout);

        supplierChart = (CombinedChart)view.findViewById(R.id.supplier_chart);
        supplierSubListView = (ListView)view.findViewById(R.id.supplier_chart_listView);
        supplierSubListView.setAdapter(subChartListAdapter);

        reportSupplierListView = (ListView)view.findViewById(R.id.report_supplier_listView);
        reportSupplierListView.setAdapter(reportSupplierListAdapter);

        initialChart();
    }

    private void initialChart(){
        supplierChart.setOnChartValueSelectedListener(this);
        supplierChart.setDrawBarShadow(false);
        supplierChart.setDrawValueAboveBar(true);
        supplierChart.setDescription("");
        // if more than 60 entries are displayed in the chart, no values will be
        // drawn
        supplierChart.setMaxVisibleValueCount(5);
        // scaling can now only be done on x- and y-axis separately
        supplierChart.setPinchZoom(false);
        // draw shadows for each bar that show the maximum value
        // mChart.setDrawBarShadow(true);
        supplierChart.setDrawGridBackground(false);

        supplierChart.setHighlightPerTapEnabled(false);
        supplierChart.setHighlightPerDragEnabled(false);
        supplierChart.setDoubleTapToZoomEnabled(false);

        setChartAxis();
    }

    private void setChartAxis(){
        ArrayList<String> supplierName = new ArrayList<>();
        //进货额数据列表
        ArrayList<BarEntry> yVals1 = new ArrayList<>();
        //进货额占比
        ArrayList<BarEntry> yVals2 = new ArrayList<>();
        ArrayList<BarEntry> yVals3 = new ArrayList<>();
        ArrayList<BarEntry> yVals4 = new ArrayList<>();

        for (int i = 0 ; i < supplierList.size() ; i++){
            SupplierChartEntity item = supplierList.get(i);
            supplierName.add(item.getSupplierName());
            yVals1.add(new BarEntry(i, Float.valueOf(item.getBuyAmount())));
            yVals2.add(new BarEntry(i, Float.valueOf(item.getBuyRatio())));

        }


        // set x, y and legend
        AxisStringFormatter xAxisFormatter = new AxisStringFormatter(supplierName);
        XAxis xAxis = supplierChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setAxisMinValue(0);
        xAxis.setAxisMaxValue(5);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f); // only intervals of 1 day
        xAxis.setCenterAxisLabels(true);
//        xAxis.setLabelCount(5);
        xAxis.setValueFormatter(xAxisFormatter);

        AxisMoneyFormatter yAxisFormatter = new AxisMoneyFormatter();
        YAxis leftAxis = supplierChart.getAxisLeft();
        leftAxis.setLabelCount(8, false);
        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        leftAxis.setSpaceTop(15f);
        leftAxis.setAxisMinValue(0f);
        leftAxis.setValueFormatter(yAxisFormatter);

        AxisPercentFormatter yrAxisFormatter = new AxisPercentFormatter();
        YAxis rightAxis = supplierChart.getAxisRight();
        rightAxis.setDrawGridLines(false);
        rightAxis.setAxisMinValue(0);
        rightAxis.setAxisMaxValue(1);
        rightAxis.setSpaceTop(15f);
        rightAxis.setValueFormatter(yrAxisFormatter);

        Legend l = supplierChart.getLegend();
        l.setPosition(Legend.LegendPosition.BELOW_CHART_LEFT);
        l.setForm(Legend.LegendForm.SQUARE);
        l.setFormSize(9f);
        l.setTextSize(11f);
        l.setXEntrySpace(4f);

        float groupSpace = 0.06f;
        float barSpace = 0.02f; // x2 dataset
        float barWidth = 0.45f; // x2 dataset

        BarDataSet set1, set2;

        if (supplierChart.getBarData() != null &&
                supplierChart.getBarData().getDataSetCount() > 0)
        {
            set1 = (BarDataSet)supplierChart.getBarData().getDataSetByIndex(0);
            set2 = (BarDataSet)supplierChart.getBarData().getDataSetByIndex(1);

            //if (currentCondition.equals(SupplierReportRequest.TopCondition.AMOUNT)){
                set1.setValues(yVals1);
                set2.setValues(yVals3);
                set1.setLabel("进货额");
                set2.setLabel("进货占比");
            //}


            supplierChart.getData().getBarData().setBarWidth(barWidth);
            supplierChart.getData().getBarData().groupBars(0, groupSpace, barSpace);
            supplierChart.getData().setDrawValues(false);

            supplierChart.getData().notifyDataChanged();
            supplierChart.notifyDataSetChanged();
            supplierChart.invalidate();

        } else {

            set1 = new BarDataSet(yVals1, "Bar 1");
            set1.setColor(Color.rgb(60, 220, 78));
            set1.setValueTextColor(Color.rgb(60, 220, 78));
            set1.setValueTextSize(10f);
            set1.setAxisDependency(YAxis.AxisDependency.LEFT);

            set2 = new BarDataSet(yVals2, "Bar 2");
            set2.setColor(Color.rgb(23, 197, 255));
            set2.setValueTextColor(Color.rgb(61, 165, 255));
            set2.setValueTextSize(10f);
            set2.setAxisDependency(YAxis.AxisDependency.RIGHT);


            ArrayList<IBarDataSet> dataSets = new ArrayList<>();
            dataSets.add(set1);
            dataSets.add(set2);

            BarData barData = new BarData(dataSets);
            barData.setBarWidth(barWidth);
            barData.setDrawValues(false);

            barData.groupBars(0, groupSpace, barSpace);
            CombinedData data = new CombinedData();
            data.setData(barData);

            supplierChart.setData(data);
            supplierChart.invalidate();
        }
    }

    private void initialWebClient(){
        String baseUrl = WebServerConfigManager.getWebServiceUrl();
        NetworkManager networkManager = NetworkManager.getInstance();
        try {
            retrofit = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .client(networkManager.setSSL().build())
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private SupplierReportRequest buildRequest(String startTime, SupplierReportRequest.TopCondition topCondition){
        String shopId = AccountManager.getInstance().getCurrentAccount().getCurrentShopId();
        SupplierReportRequest supplierReportRequest = new SupplierReportRequest(shopId, startTime);

        if (topCondition != null){
            supplierReportRequest.setTopCondition(topCondition);
        }
        return supplierReportRequest;
    }

    private void getChartDataFromWeb(SupplierReportRequest reportRequest){

        Account currentUser = AccountManager.getInstance().getCurrentAccount();
        String s = currentUser.getToken();
        String token = "token=" + s;

        SupplierReportService supplierReportService = retrofit.create(SupplierReportService.class);

        Call<HttpResult<SupplierReportResponse<SupplierChartEntity>>> call = supplierReportService.
                getSupplierStatistic(token, reportRequest);

        call.enqueue(new Callback<HttpResult<SupplierReportResponse<SupplierChartEntity>>>() {
            @Override
            public void onResponse(Call<HttpResult<SupplierReportResponse<SupplierChartEntity>>> call,
                                   Response<HttpResult<SupplierReportResponse<SupplierChartEntity>>> response) {
                if(!response.isSuccessful()) {
                    LogHelper.getInstance().e(TAG, "server return error");
                    return;
                }
                Log.e("data",new Gson().toJson(response.body()));
                HttpResult httpResult = response.body();

                if (httpResult.getResultCode() != 200){
                    LogHelper.getInstance().e(TAG, httpResult.getResultMessage());
                    return;
                }

                //TODO statistic/supplier response并没有top_condition这个字段
                SupplierReportResponse report = (SupplierReportResponse)httpResult.getData();
                supplierList = report.getSupplierList();
                reportSupplierListAdapter.setDataSource(supplierList);
                reportSupplierListAdapter.notifyDataSetChanged();

                SupplierSummary supplierSummary = report.getSupplierSummary();
                calcSubList(supplierSummary);

                setChartAxis();
            }

            @Override
            public void onFailure(Call<HttpResult<SupplierReportResponse<SupplierChartEntity>>> call, Throwable t) {

            }
        });
    }

    private void calcSubList(SupplierSummary supplierSummary){
        subListViewItemList.clear();
        Float buyAmount = 0f, butRatio = 0f, profit = 0f, profitRatio = 0f;
        for (SupplierChartEntity item :supplierList) {
            buyAmount += Float.valueOf(item.getBuyAmount());
            butRatio += Float.valueOf(item.getBuyRatio());
//            profit += Float.valueOf(item.getProfit());
//            profitRatio += Float.valueOf(item.getProfitRatio());
        }


        ArrayList<String> valueList = new ArrayList<>();
        String incomeString = "￥" + supplierSummary.getBuyAmount();
        valueList.add(incomeString);

        String incomeRatioString = String.valueOf(100 * supplierSummary.getBuyRatio()) + "%";
        valueList.add(incomeRatioString);

//        valueList.add(profit);
//        valueList.add(profitRatio);
        ArrayList<String> titleList = new ArrayList<>();
        if (this.context != null){
            if (!isAdded()){
                return;
            }
            titleList = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.supplier_sub_list)));
        }

        for (int i = 0 ; i < valueList.size() ; i ++){
            ChartSubListCommonItem item = new ChartSubListCommonItem(titleList.get(i), valueList.get(i));
            subListViewItemList.add(item);
        }
        subChartListAdapter.setDataSource(subListViewItemList);
        subChartListAdapter.notifyDataSetChanged();
    }

    @Override
    public void setDays(int days) {
        String startTime = String.valueOf(WSTimeStamp.getSpecialDayStartTime(days));
        currentStartTime = startTime;
        SupplierReportRequest request = buildRequest(startTime, currentCondition);
        getChartDataFromWeb(request);
    }

    @Override
    public void setMonths(int months) {
        String startTime = WSTimeStamp.getSpecialMonthStartTime(months);
        currentStartTime = startTime;
        SupplierReportRequest request = buildRequest(startTime, currentCondition);
        getChartDataFromWeb(request);
    }

    @Override
    public void setYears(int years) {
        String startTime = WSTimeStamp.getSpecialYearsStartTime(years);
        currentStartTime = startTime;
        SupplierReportRequest request = buildRequest(startTime, currentCondition);
        getChartDataFromWeb(request);
    }

    @Override
    public void setCondition(String condition) {
        supplierConditionList = new ArrayList<>(Arrays.asList(getResources().
                getStringArray(R.array.report_select_supplier)));

        if (supplierConditionList.get(0).equals(condition)){
            currentCondition = SupplierReportRequest.TopCondition.AMOUNT;
            SupplierReportRequest request = buildRequest(currentStartTime, currentCondition);
            getChartDataFromWeb(request);
        }
    }

    @Override
    public void toggle() {
        if (reportLayout.getVisibility() == View.VISIBLE){
            reportLayout.setVisibility(View.GONE);
            listLayout.setVisibility(View.VISIBLE);
        }else{
            reportLayout.setVisibility(View.VISIBLE);
            listLayout.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean isShownChart() {
        if (reportLayout.getVisibility() == View.VISIBLE){
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
            supplierList.clear();
        }
    }
}
