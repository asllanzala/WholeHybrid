package com.honeywell.wholesale.ui.report.product;

import android.content.Context;
import android.graphics.Color;
import android.graphics.RectF;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.MPPointF;
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
import com.honeywell.wholesale.ui.report.adapter.ReportProductListAdapter;

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
public class ProductChartFragment extends BaseFragment implements OnChartValueSelectedListener {
    private static final String TAG = "ProductChartFragment";
    private HorizontalBarChart productBarChart;
    private ListView productSubListView;
    private CommonSubChartListAdapter subChartListAdapter;
    private ReportProductListAdapter productListAdapter;

    private Retrofit retrofit;
    private ArrayList<ProductChartEntity> productChartList = new ArrayList<>();
    private ArrayList<ChartSubListCommonItem> subListViewItemList = new ArrayList<>();

    private String currentStartTime;
    private ProductReportRequest.TopCondition currentTopCondition;

    private ListView reportProductListView;

    private ArrayList<String> productConditionList;

    private View reportLayout, listLayout;
    private Context context;

    private boolean isLoaded = false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Nullable @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_product_chart, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.e(TAG,"onViewCreated");
        initialData();
        initialView(view);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private void initialView(View view){
        productBarChart = (HorizontalBarChart)view.findViewById(R.id.product_chart);
        productSubListView = (ListView)view.findViewById(R.id.product_chart_listView);
        productSubListView.setAdapter(subChartListAdapter);

        reportProductListView = (ListView)view.findViewById(R.id.report_product_listview);
        reportProductListView.setAdapter(productListAdapter);

        reportLayout = view.findViewById(R.id.product_chart_layout);
        listLayout = view.findViewById(R.id.product_list_layout);

        initialChart();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    private void initialChart(){

        productBarChart.setTouchEnabled(false);

        productBarChart.setDrawBarShadow(false);
        productBarChart.setDescription("");
        productBarChart.setBackgroundColor(Color.WHITE);
        productBarChart.setDrawGridBackground(false);
        productBarChart.setDrawValueAboveBar(true);
        // setting data
//        Legend l = productBarChart.getLegend();
//        l.setPosition(Legend.LegendPosition.BELOW_CHART_LEFT);
//        l.setForm(Legend.LegendForm.SQUARE);
//        l.setFormSize(9f);
//        l.setTextSize(11f);
//        l.setXEntrySpace(4f);
////        legend.setWordWrapEnabled(true);


        if (isLoaded){
            calcChartData();
        }

    }

    protected RectF mOnValueSelectedRectF = new RectF();


    private void calcChartData(){

        isLoaded = true;

        float groupSpace = 0.06f;
        float barSpace = 0.02f; // x2 dataset
        float barWidth = 0.45f; // x2 dataset

        ArrayList<String> productName = new ArrayList<>();

        //总销售额数据列表
        ArrayList<BarEntry> yVals = new ArrayList<>();
        //总销售额占比
        ArrayList<BarEntry> yVals2 = new ArrayList<>();

        // 总利润额数据列表
        ArrayList<BarEntry> yVals3 = new ArrayList<>();
        // 总利润额占比
        ArrayList<BarEntry> yVals4 = new ArrayList<>();

        // 赊账额数据列表
        ArrayList<BarEntry> yVals5 = new ArrayList<>();

        for (int i = 0; i < productChartList.size(); i++){
            ProductChartEntity item = productChartList.get(i);
            productName.add(item.getProductName());
            //yVals.add(new BarEntry(Float.valueOf(item.getIncome()), i));
            yVals.add(new BarEntry(i, Float.valueOf(item.getIncome())));
            yVals2.add(new BarEntry(i, Float.valueOf(item.getIncomeRatio())));
            yVals3.add(new BarEntry(i,Float.valueOf(item.getProfit())));
            yVals4.add(new BarEntry(i,Float.valueOf(item.getProfitRatio())));
            yVals5.add(new BarEntry(i,Float.valueOf(item.getSaleQuantity())));
        }

        AxisStringFormatter xAxisFormatter = new AxisStringFormatter(productName);

        XAxis xl = productBarChart.getXAxis();
        xl.setPosition(XAxis.XAxisPosition.BOTTOM);
        xl.setAxisMinValue(0);
        xl.setAxisMaxValue(5);
        xl.setDrawGridLines(false);
        xl.setGranularity(1f); // only intervals of 1 day
//        xAxis.setLabelCount(5);
        xl.setCenterAxisLabels(true);

        xl.setValueFormatter(xAxisFormatter);

        AxisPercentFormatter ylAxisFormatter = new AxisPercentFormatter();
        YAxis yl = productBarChart.getAxisLeft();

        yl.setDrawGridLines(false);
        yl.setAxisMinValue(0);
        yl.setAxisMaxValue(1);
        yl.setSpaceTop(15f);
        yl.setSpaceBottom(15f);
        yl.setLabelCount(4);
        yl.setValueFormatter(ylAxisFormatter);


        AxisMoneyFormatter yrAxisFormatter = new AxisMoneyFormatter();
        YAxis yr = productBarChart.getAxisRight();
        yr.setDrawAxisLine(false);
        yr.setDrawGridLines(false);
        yr.setAxisMinValue(0);
        yr.setSpaceBottom(15f);
        yr.setSpaceTop(15f);
        yr.setValueFormatter(yrAxisFormatter);
        yr.setLabelCount(4);

        Legend legend = productBarChart.getLegend();
        legend.setEnabled(true);
        legend.setWordWrapEnabled(false);
        legend.setPosition(Legend.LegendPosition.BELOW_CHART_CENTER);
        legend.setForm(Legend.LegendForm.SQUARE);
        legend.setFormSize(9f);
        legend.setTextSize(11f);
        legend.setXEntrySpace(10f);

        BarDataSet set1, set2;

        if (productBarChart.getData() != null &&
                productBarChart.getData().getDataSetCount() > 0) {
            set1 = (BarDataSet)productBarChart.getData().getDataSetByIndex(0);
            set2 = (BarDataSet)productBarChart.getData().getDataSetByIndex(1);

            if (currentTopCondition.equals(ProductReportRequest.TopCondition.INCOME)){
                set1.setValues(yVals);
                set2.setValues(yVals2);
                set1.setLabel("交易额");
                set2.setLabel("交易占比");
            }

            if (currentTopCondition.equals(ProductReportRequest.TopCondition.PROFIT)){
                set1.setValues(yVals3);
                set2.setValues(yVals4);
                set1.setLabel("利润额");
                set2.setLabel("利润占比");
            }

            if (currentTopCondition.equals(ProductReportRequest.TopCondition.SALES)){
                set1.setValues(yVals5);
                set2.setValues(yVals2);
                set1.setLabel("赊账额");
                set2.setLabel("利润占比");
            }


            set1.setAxisDependency(YAxis.AxisDependency.RIGHT);
            set2.setAxisDependency(YAxis.AxisDependency.LEFT);

            productBarChart.getData().setBarWidth(barWidth);
            productBarChart.getData().groupBars(0, groupSpace, barSpace);
            productBarChart.getData().setDrawValues(false);

            productBarChart.getData().notifyDataChanged();
            productBarChart.notifyDataSetChanged();
            productBarChart.invalidate();
        } else {
            set1 = new BarDataSet(yVals, "DataSet 1");
            set2 = new BarDataSet(yVals2, "DataSet 2");

            set1.setLabel("销售额");
            set2.setLabel("销售占比");

            set1.setColor(Color.rgb(104, 241, 175));
            set2.setColor(Color.rgb(164, 228, 251));

            ArrayList<IBarDataSet> dataSets = new ArrayList<>();
            set1.setAxisDependency(YAxis.AxisDependency.RIGHT);
            set2.setAxisDependency(YAxis.AxisDependency.LEFT);

            dataSets.add(set1);
            dataSets.add(set2);
            BarData data = new BarData(dataSets);

            data.setBarWidth(barWidth);
            data.groupBars(0, groupSpace, barSpace);
//            data.setValueTextSize(10f);
            data.setDrawValues(false);

            productBarChart.setData(data);
            productBarChart.invalidate();
//            productBarChart.getBarData().setBarWidth(barWidth);
        }
    }

    private void initialData(){
        if (subChartListAdapter == null){
            subChartListAdapter = new CommonSubChartListAdapter(subListViewItemList, getContext());
        }

        if (productListAdapter == null){
            productListAdapter = new ReportProductListAdapter(productChartList, getContext());
        }

        if  (currentTopCondition == null){
            currentTopCondition = ProductReportRequest.TopCondition.INCOME;
        }

        if (currentStartTime == null || currentStartTime.isEmpty()){
            initialWebClient();
            String startTime = String.valueOf(WSTimeStamp.getSpecialDayStartTime(-7));
            currentStartTime = startTime;
            ProductReportRequest request = buildRequest(startTime, currentTopCondition);
            getChartDataFromWeb(request);
        }
    }

    private void initialWebClient(){
        String baseUrl = WebServerConfigManager.getWebServiceUrl();
        NetworkManager networkManager = NetworkManager.getInstance();
        try {
            retrofit = new Retrofit.Builder()
                    .client(networkManager.setSSL().build())
                    .baseUrl(baseUrl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private ProductReportRequest buildRequest(String startTime, ProductReportRequest.TopCondition topCondition){
        String shopId = AccountManager.getInstance().getCurrentAccount().getCurrentShopId();

        ProductReportRequest productReportRequest = new ProductReportRequest(shopId, startTime);
        if (topCondition != null){
            productReportRequest.setTopCondition(topCondition);
        }
        return  productReportRequest;
    }

    private void getChartDataFromWeb(ProductReportRequest request){

        Account currentUser = AccountManager.getInstance().getCurrentAccount();
        String s = currentUser.getToken();
        String token = "token=" + s;

        ProductReportService productReportService = retrofit.create(ProductReportService.class);
        Call<HttpResult<ProductReportResponse<ProductChartEntity>>> call = productReportService.getProductStatistic(
                token,
                request);

        call.enqueue(new Callback<HttpResult<ProductReportResponse<ProductChartEntity>>>() {
            @Override
            public void onResponse(Call<HttpResult<ProductReportResponse<ProductChartEntity>>> call,
                                   Response<HttpResult<ProductReportResponse<ProductChartEntity>>> response) {

                Log.e("data",new Gson().toJson(response.body()));
                if(!response.isSuccessful()) {
                    LogHelper.getInstance().e(TAG, "server return error");
                    return;
                }
                HttpResult httpResult = response.body();

                if (httpResult.getResultCode() != 200){
                    LogHelper.getInstance().e(TAG, httpResult.getResultMessage());
                    return;
                }

                //TODO 此处server返回3个数据，但第三个的productname为null，产生空指针，导致app crash
                ProductReportResponse report = (ProductReportResponse) httpResult.getData();
                productChartList = report.getProductsList();

                ProductSummary productSummary = report.getProductSummary();
                calcSubList(productSummary);
                productListAdapter.setDataSource(productChartList);
                productListAdapter.notifyDataSetChanged();

                calcChartData();
            }

            @Override
            public void onFailure(Call<HttpResult<ProductReportResponse<ProductChartEntity>>> call, Throwable t) {
                LogHelper.getInstance().e(TAG, t.getMessage());
            }
        });
    }

    private void calcSubList(ProductSummary productSummary){
        subListViewItemList.clear();
        Float income = 0f, incomeRatio = 0f, profit = 0f, profitRatio = 0f;
        for (ProductChartEntity item :productChartList) {
            income += Float.valueOf(item.getIncome());
            incomeRatio += Float.valueOf(item.getIncomeRatio());
            profit += Float.valueOf(item.getProfit());
            profitRatio += Float.valueOf(item.getProfitRatio());
        }

        ArrayList<String> valueList = new ArrayList<>();
        String incomeString = "￥" + productSummary.getIncomeAmount();
        valueList.add(incomeString);


        String incomeRatioString = String.valueOf(100 *  productSummary.getIncomeRatio()) + "%";
        valueList.add(incomeRatioString);

        String profitString = "￥" + productSummary.getProfitAmount();
        valueList.add(profitString);

        String profitRatioString = String.valueOf(100 * productSummary.getProfitRatio()) + "%";
        valueList.add(profitRatioString);

        ArrayList<String> titleList = new ArrayList<>();
        if (this.context != null){
            if (!isAdded()){
                return;
            }
            titleList = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.product_sub_list)));
        }

        for (int i = 0 ; i < valueList.size() ; i ++){
            ChartSubListCommonItem item = new ChartSubListCommonItem(titleList.get(i), valueList.get(i));
            subListViewItemList.add(item);
        }
        subChartListAdapter.setDataSource(subListViewItemList);
        subChartListAdapter.notifyDataSetChanged();
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {
        if (e == null){
            return;
        }

        RectF bounds = mOnValueSelectedRectF;
        productBarChart.getBarBounds((BarEntry) e, bounds);

        MPPointF position = productBarChart.getPosition(e, productBarChart.getData().getDataSetByIndex(h.getDataSetIndex())
                .getAxisDependency());

        Log.i("bounds", bounds.toString());
        Log.i("position", position.toString());

        MPPointF.recycleInstance(position);
    }

    @Override
    public void onNothingSelected() {

    }

    @Override
    public void setDays(int days) {
        String startTime = String.valueOf(WSTimeStamp.getSpecialDayStartTime(days));
        ProductReportRequest request = buildRequest(startTime, currentTopCondition);
        getChartDataFromWeb(request);
    }

    @Override
    public void setMonths(int months) {
        String startTime = WSTimeStamp.getSpecialMonthStartTime(months);
        ProductReportRequest request = buildRequest(startTime, currentTopCondition);
        getChartDataFromWeb(request);
    }

    @Override
    public void setYears(int years) {
        String startTime = WSTimeStamp.getSpecialYearsStartTime(years);
        ProductReportRequest request = buildRequest(startTime, currentTopCondition);
        getChartDataFromWeb(request);
    }

    @Override
    public void setCondition(String condition) {
        productConditionList =  new ArrayList<>(Arrays.asList(getResources().
                getStringArray(R.array.report_select_product)));

        if (productConditionList.get(0).equals(condition)){
            currentTopCondition = ProductReportRequest.TopCondition.INCOME;
        }

        if (productConditionList.get(1).equals(condition)){
            currentTopCondition = ProductReportRequest.TopCondition.PROFIT;
        }

        if (productConditionList.get(2).equals(condition)){
            currentTopCondition = ProductReportRequest.TopCondition.SALES;
        }

        ProductReportRequest request = buildRequest(currentStartTime, currentTopCondition);
        getChartDataFromWeb(request);
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

    public void onEventMainThread(SwitchFragmentEvent switchFragmentEvent){
        String msg = switchFragmentEvent.getmMsg();
        Log.e(TAG,msg);
        if (msg.equals(String.valueOf(R.string.report_fragment_switch))){
            subListViewItemList.clear();
            productChartList.clear();
        }
    }
}
