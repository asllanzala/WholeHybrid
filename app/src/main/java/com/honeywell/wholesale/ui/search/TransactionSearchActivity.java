package com.honeywell.wholesale.ui.search;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.honeywell.hybridapp.framework.log.LogHelper;
import com.honeywell.wholesale.R;
import com.honeywell.wholesale.framework.http.HttpResult;
import com.honeywell.wholesale.framework.http.WebServerConfigManager;
import com.honeywell.wholesale.framework.model.Account;
import com.honeywell.wholesale.framework.model.AccountManager;
import com.honeywell.wholesale.framework.model.Order;
import com.honeywell.wholesale.framework.network.NetworkManager;
import com.honeywell.wholesale.framework.utils.WSTimeStamp;
import com.honeywell.wholesale.ui.base.BaseTextView;
import com.honeywell.wholesale.ui.order.OrderDetailActivity;
import com.honeywell.wholesale.ui.transaction.TransactionQueryRequest;
import com.jakewharton.rxbinding.widget.RxTextView;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import static java.security.AccessController.getContext;

/**
 * Created by H155935 on 16/6/29.
 * Email: xiaofei.he@honeywell.com
 */
public class TransactionSearchActivity extends Activity {
    private static final String TAG = "TransactionSearchActivity";
    private EditText searchView;
    private ImageView backImageView;
    private ImageView searchCancelImageView;
    private ListView searchResultListView;

    private PopupWindow selectionRangePopup;
    private PopupWindow selectionOrderPopup;

    private BaseTextView timeSelectTextView;
    private BaseTextView orderSelectTextView;

    private BaseTextView searchCountTextView;

    private ArrayList<String> selectionArrayList;
    private ArrayList<String> selectionOrderList;

    private SearchResultAdapter searchResultAdapter;

    private  String isHavaNextPage;
    private static int pageNumber = 0;
    private static String startTime;
    private static String orderBy;
    private static String searchString = "";
    private int pageNumberRefactor = 0;
    private static String payMent;
    private static String orderStatus;

    private static ArrayList<Order> orders = new ArrayList<>();

    private LoadMoreScrollListener loadMoreScrollListener;

    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_transaction);

        initData();
        initView();

//        searchView.setOnQueryTextListener(onQueryTextListener);
        startTime = WSTimeStamp.getLastWeek();
        orderBy = "sale_time";
//        getTransaction(startTime, "", orderBy, pageNumber);

        searchResultAdapter = new SearchResultAdapter(this, orders);
        searchResultListView.setAdapter(searchResultAdapter);

        Observable<ArrayList<Order>> observable = RxTextView.textChanges(searchView)
                .debounce(500, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(new Action1<CharSequence>() {
                    @Override
                    public void call(CharSequence charSequence) {
                        pageNumberRefactor = 0;
                        mProgressDialog.show();
                        mProgressDialog.setContentView(R.layout.dialog_loading_content);
                        orders.clear();
                        searchResultAdapter.notifyDataSetChanged();
                    }
                })
                .map(new Func1<CharSequence, String>() {
                    @Override
                    public String call(CharSequence charSequence) {
                        return charSequence.toString();
                    }})
                .observeOn(Schedulers.io())
                .map(new Func1<String, ArrayList<Order>>(){
                    @Override
                    public ArrayList<Order> call(String s) {
                        searchString = s;
                        //getTransaction(startTime, s, orderBy, pageNumber);

                        try {
                            getTransaction(searchString, orderStatus, payMent, pageNumberRefactor);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return new ArrayList<>();
                    }
                }).observeOn(AndroidSchedulers.mainThread());

        Subscription subscription = observable.subscribe(new Action1<ArrayList<Order>>() {
            @Override
            public void call(ArrayList<Order> items) {

            }
        });
    }

    @Override
    protected void onDestroy() {
        if(mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
        super.onDestroy();
    }

    private void initData(){

        Intent intent = getIntent();
        orderStatus = intent.getStringExtra("orderstatus");
        payMent = intent.getStringExtra("payment");
        selectionArrayList = initialRangeSelectList();
        selectionOrderList = initialOrderSelectList();

        loadMoreScrollListener = new LoadMoreScrollListener();
        loadMoreScrollListener.setLoadMore(new LoadMoreScrollListener.LoadMore() {
            @Override
            public void loadMoreData() {
                LogHelper.getInstance().e(TAG, "load more");
                if ("true".equalsIgnoreCase(isHavaNextPage)){
                    //getTransaction(startTime, searchString, orderBy, pageNumber);

                    try {
                        getTransaction(searchString, orderStatus, payMent, pageNumberRefactor);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });

    }

    private void initView(){
        backImageView = (ImageView)findViewById(R.id.icon_back);

        searchView = (EditText)findViewById(R.id.transaction_search_edittext);
        searchCancelImageView = (ImageView)findViewById(R.id.transaction_search_cancel);
        searchCancelImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchView.setText("");
            }
        });
        searchResultListView = (ListView)findViewById(R.id.transaction_search_result_list);

        timeSelectTextView = (BaseTextView)findViewById(R.id.transaction_select_textView);
        orderSelectTextView = (BaseTextView)findViewById(R.id.transaction_search_select_order);

        searchCountTextView = (BaseTextView)findViewById(R.id.transaction_search_result_count);

        timeSelectTextView.setOnClickListener(onClickListener);
        orderSelectTextView.setOnClickListener(onClickListener);
        backImageView.setOnClickListener(onClickListener);
        searchResultListView.setOnItemClickListener(onItemClickListener);

        timeSelectTextView.setText(selectionArrayList.get(0));
        orderSelectTextView.setText(selectionOrderList.get(0));

        searchResultListView.setOnScrollListener(loadMoreScrollListener);
        initDialog();
    }

    private void initDialog() {
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(false);
        mProgressDialog.setMessage("Loading...");
        mProgressDialog.setCancelable(false);
    }

    private void initRangePopupWindow(){
        final View popupWindowView = LayoutInflater.from(this).inflate(R.layout.popup_transaction_search_range, null, false);
        final ListView popupListView = (ListView)popupWindowView.findViewById(R.id.search_range_listView);

        if (selectionArrayList == null){
            selectionArrayList = initialRangeSelectList();
        }

        SearchSelectAdapter searchSelectAdapter = new SearchSelectAdapter(selectionArrayList, this);
        popupListView.setAdapter(searchSelectAdapter);
        popupListView.setOnItemClickListener(onItemClickListener);

        selectionRangePopup = new PopupWindow(popupWindowView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);

        popupWindowView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (popupWindowView.isShown()) {
                    selectionRangePopup.dismiss();
                    selectionRangePopup = null;
                }
                return false;
            }
        });
        selectionRangePopup.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.white)));
    }

    private void initOrderPopupWindow(){
        final View popupWindowView = LayoutInflater.from(this).inflate(R.layout.popup_transaction_search_order, null, false);
        final ListView popupListView = (ListView)popupWindowView.findViewById(R.id.search_order_listView);

        if (selectionOrderList == null){
            selectionOrderList = initialOrderSelectList();
        }
        SearchSelectAdapter searchSelectAdapter = new SearchSelectAdapter(selectionOrderList, this);
        popupListView.setAdapter(searchSelectAdapter);
        popupListView.setOnItemClickListener(onItemClickListener);

        selectionOrderPopup = new PopupWindow(popupWindowView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);

        popupWindowView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (popupWindowView.isShown()) {
                    selectionOrderPopup.dismiss();
                    selectionOrderPopup = null;
                }
                return false;
            }
        });
        selectionOrderPopup.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.white)));
    }

    private ArrayList<String> initialRangeSelectList(){
        final String[] rangeArray = getResources().getStringArray(R.array.transaction_select_range);
        return new ArrayList<>(Arrays.asList(rangeArray));
    }

    private ArrayList<String> initialOrderSelectList(){
        final String[] orderArray = getResources().getStringArray(R.array.transaction_select_order);
        return new ArrayList<>(Arrays.asList(orderArray));
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.icon_back){
                finish();
            }

            if (v.getId() == R.id.transaction_search_cancel){

            }

            if (v.getId() == R.id.transaction_select_textView){
                LogHelper.getInstance().e(TAG, "click select time range");
                initRangePopupWindow();

                int left = Math.round(v.getX()) + v.getWidth() / 2 - 160 - 115;
                int top = v.getTop() - 120;
                selectionRangePopup.showAsDropDown(v, left, top);

            }

            if (v.getId() == R.id.transaction_search_select_order){
                LogHelper.getInstance().e(TAG, "click select order type");
                initOrderPopupWindow();
                int left = Math.round(v.getX()) + v.getWidth() / 2 - 810;
                int top = v.getTop() - 90;
                selectionOrderPopup.showAsDropDown(v, left, top);
            }
        }
    };

    private AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (parent.getId() == R.id.search_range_listView){
                timeSelectTextView.setText(selectionArrayList.get(position));

                if (position == 0){
                    startTime = WSTimeStamp.getLastWeek();
                }

                if (position == 1){
                    startTime = WSTimeStamp.getLastMonth();
                }

                if (position == 2){
                    startTime = WSTimeStamp.getLast3Month();
                }

                if (position == 3){
                    startTime = "";
                }

                orders.clear();
                searchResultAdapter.notifyDataSetChanged();
                searchView.setText("");

                if (selectionRangePopup.isShowing()){
                    selectionRangePopup.dismiss();
                    selectionRangePopup = null;
                }
            }

            if (parent.getId() == R.id.search_order_listView){
                orderSelectTextView.setText(selectionOrderList.get(position));
                if (position == 0){
                    orderBy = "sale_time";
                }

                if (position == 1){
                    orderBy = "total_price";
                }

                if (position == 2){
                    orderBy = "customer_name_pinyin";
                }

                orders.clear();
                searchResultAdapter.notifyDataSetChanged();
                searchView.setText("");

                if (selectionOrderPopup.isShowing()){
                    selectionOrderPopup.dismiss();
                    selectionOrderPopup = null;
                }
            }

            if (parent.getId() == R.id.transaction_search_result_list){
                Order order = orders.get(position);
                // 打开订单详情
                Intent intent = new Intent(TransactionSearchActivity.this, OrderDetailActivity.class);
                intent.putExtra(OrderDetailActivity.INTENT_VALUE_ORDER_DETAIL_JSON, order.getJsonString());
                startActivity(intent);
            }
        }
    };


    class SearchResultAdapter extends BaseAdapter{

        private ArrayList<Order> arrayList;
        private Context context;

        public SearchResultAdapter(Context context, ArrayList<Order> arrayList) {
            this.context = context;
            this.arrayList = arrayList;
        }

        @Override
        public int getCount() {
            return arrayList.size();
        }

        @Override
        public Object getItem(int position) {
            return arrayList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final ViewHolder holder;
            if (convertView == null){
                holder = new ViewHolder();
                convertView = LayoutInflater.from(context).inflate(R.layout.item_transaction_search, null);
                holder.customerName = (BaseTextView) convertView.findViewById(R.id.transaction_search_listview_item_customer_name_textView);
                holder.totalMoney = (BaseTextView) convertView.findViewById(R.id.transaction_search_listview_item_total_money_textView);
                holder.products = (BaseTextView) convertView.findViewById(R.id.transaction_search_listview_item_products_textView);
                holder.orderTime = (BaseTextView) convertView.findViewById(R.id.transaction_search_listview_item_order_time_textView);
                holder.employeeName = (BaseTextView) convertView.findViewById(R.id.transaction_search_listview_item_employee_name_textView);
                holder.saleAmount = (BaseTextView) convertView.findViewById(R.id.transaction_search_listview_item_sale_amount_textView);
                holder.orderStatus = (BaseTextView) convertView.findViewById(R.id.transaction_search_listview_item_status_tag_textView);
                holder.orderDeadLine = (BaseTextView) convertView.findViewById(R.id.transaction_search_listview_item_deadline_textView);
                holder.signText = (BaseTextView) convertView.findViewById(R.id.transaction_search_listview_item_sign_textView);
                holder.orderNumber = (BaseTextView) convertView.findViewById(R.id.order_listview_item_order_number_textView);
                convertView.setTag(holder);
            }else{
                holder = (ViewHolder) convertView.getTag();
            }

            holder.saleAmount.setVisibility(View.GONE);
            holder.totalMoney.setVisibility(View.VISIBLE);
            holder.employeeName.setVisibility(View.VISIBLE);
            holder.orderTime.setVisibility(View.VISIBLE);
            holder.orderDeadLine.setVisibility(View.GONE);
            holder.orderStatus.setVisibility(View.VISIBLE);
            holder.signText.setVisibility(View.VISIBLE);
            holder.orderNumber.setVisibility(View.VISIBLE);

            Order order = arrayList.get(position);

            holder.customerName.setText(order.getCustomerName());
            holder.totalMoney.setText(order.getTotalPrice());
            holder.orderNumber.setText(order.getOrderNumber());

            String productsName = "";
            Order.Product[] products = order.getSaleList();
            for(int i = 0; i < products.length; i++) {
                productsName += products[i].mName;
                if (i != products.length - 1) {
                    productsName += ",";
                }
            }
            holder.products.setText(productsName);

            if (order.getOrderStatus() == null){
                return convertView;
            }

            //已完成订单(order_status==1)
            if (order.getOrderStatus().equals("1")){
                holder.orderStatus.setText("已结清");
                holder.orderStatus.setTextColor(context.getResources().getColor(R.color.black));
                holder.orderDeadLine.setVisibility(View.GONE);
//                String mFinishTime = WSTimeStamp.getFullTimeString(Long.parseLong(order.getmFinishDt()));
//                holder.orderTime.setText(mFinishTime);
                holder.orderTime.setText(order.getFinishDtFormat());
                holder.employeeName.setText(order.getmFinishEmployeeName()+"结清账单处理");
            }

            //已取消订单(order_status==100)
            if (order.getOrderStatus().equals("100")){
                holder.orderStatus.setText("已取消");
                holder.orderStatus.setTextColor(context.getResources().getColor(R.color.black));
                holder.orderDeadLine.setVisibility(View.GONE);
                holder.orderTime.setText(order.getCancelDtFormat());
                holder.employeeName.setText(order.getmCancelEmployeeName()+"取消订单处理");
//                holder.orderTime.setText(order.getSaleTime());
            }

            //未支付(payment=0 and order_status=0)
            if (order.getOrderStatus().equals("0") && order.getPayment().equals("0")){
                holder.orderStatus.setText("待付款");
                holder.orderStatus.setTextColor(context.getResources().getColor(R.color.light_blue));
                holder.orderDeadLine.setVisibility(View.GONE);
                holder.orderTime.setText(order.getSaleTime());
                holder.employeeName.setText(order.getEmployeeName()+"开单处理");

                long millsSeconds = 0l;
                try{
                    //将科学计数法的字符串转换成long型
                    millsSeconds = new BigDecimal(order.getmOrderDeadLine()).longValue();

                }catch (Exception e){
                    e.printStackTrace();
                    LogHelper.e("Error", "时间切换 string 转换为long 异常");
                }

                String simpleDateFormat = "dd/MM/yyyy hh:mm";
                String deadLineString = WSTimeStamp.getDate(millsSeconds, simpleDateFormat);
                holder.orderDeadLine.setText(deadLineString);
            }

            //赊账中(payment=2 and order_status=0)
            if (order.getOrderStatus().equals("0") && order.getPayment().equals("2")){
                holder.orderStatus.setText("赊账中");
                holder.orderStatus.setTextColor(context.getResources().getColor(R.color.red));
                holder.orderDeadLine.setVisibility(View.GONE);
                holder.orderTime.setText(order.getSetPayDtFormat());
                holder.employeeName.setText(order.getmSetPayEmpolyeeName()+"赊账处理");
//                holder.orderTime.setText(order.getSaleTime());
            }

            return convertView;
        }

        public void setArrayList(ArrayList<Order> arrayList) {
            this.arrayList = arrayList;
        }
    }

    private static class ViewHolder{
        BaseTextView customerName;
        BaseTextView totalMoney;
        BaseTextView products;
        BaseTextView orderTime;
        BaseTextView employeeName;
        BaseTextView signText;
        BaseTextView orderNumber;

        BaseTextView saleAmount;
        // 订单状态, 未支付截止日期
        BaseTextView orderStatus;
        BaseTextView orderDeadLine;
    }

//    private void getTransactionByServer(String searchString, String orderStatus,
//                                String payMent, final int pageNumber) {
//        String baseUrl = WebServerConfigManager.getWebServiceUrl();
//        Retrofit retrofit = new Retrofit.Builder()
//                .baseUrl(baseUrl)
//                .addConverterFactory(GsonConverterFactory.create())
//                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
//                .build();
//
//        String shopId = AccountManager.getInstance().getCurrentAccount().getCurrentShopId();
//        TransactionQueryRequest transactionQueryRequest = new TransactionQueryRequest(searchString, shopId, pageNumber, orderBy, payMent, orderStatus);
//
//        Account currentUser = AccountManager.getInstance().getCurrentAccount();
//        String s = currentUser.getToken();
//        String token = "token=" + s;
//
//        TransantionRefactorService transantionRefactorService = retrofit.create(TransantionRefactorService.class);
//        Call<HttpResult<TransactionResult<Order>>> call = transantionRefactorService.getTransactionList(
//                token,
//                transactionQueryRequest);
//
//        call.enqueue(new Callback<HttpResult<TransactionResult<Order>>>() {
//            @Override
//            public void onResponse(Call<HttpResult<TransactionResult<Order>>> call,
//                                   Response<HttpResult<TransactionResult<Order>>> response) {
//
//                if (mProgressDialog.isShowing()){
//                    mProgressDialog.dismiss();
//                }
//
//                if (!response.isSuccessful()){
//                    Toast.makeText(TransactionSearchActivity.this, "获取数据失败", Toast.LENGTH_SHORT).show();
//                    return;
//                }
//
//                HttpResult httpResult = response.body();
//                TransactionResult result = (TransactionResult) httpResult.getData();
//                isHavaNextPage = result.getNextPage();
//                Log.e("weaweada", "isHavaNextPage--" +isHavaNextPage);
//                Log.e("weaweada", result.toString());
//                if (!isHavaNextPage.equals("false")){
//                    //TransactionSearchActivity.pageNumber += 1;
//                    String.valueOf(Integer.valueOf(TransactionSearchActivity.pageNumber) + 1);
//                    pageNumberRefactor += 1;
//                }
//
//                String count = String.format(getResources().getString(R.string.search_result_count),
//                        String.valueOf(result.getSearchCount()));
//                searchCountTextView.setText(count);
//                orders.addAll(result.getOrderList());
//                searchResultAdapter.notifyDataSetChanged();
//                LoadMoreScrollListener.setIsLoading(false);
//            }
//
//            @Override
//            public void onFailure(Call<HttpResult<TransactionResult<Order>>> call, Throwable t) {
//                LogHelper.getInstance().e(TAG, t.getMessage());
//                LoadMoreScrollListener.setIsLoading(false);
//                if (mProgressDialog.isShowing()){
//                    mProgressDialog.dismiss();
//                }
//            }
//        });
//    }

    private void getTransaction(String searchString, String orderStatus,
                                String payMent, final int pageNumber) throws Exception {
        String baseUrl = WebServerConfigManager.getWebServiceUrl();
        NetworkManager networkManager = NetworkManager.getInstance();
        Retrofit retrofit = new Retrofit.Builder()
                .client(networkManager.setSSL().build())
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();

        String shopId = AccountManager.getInstance().getCurrentAccount().getCurrentShopId();
        TransactionSearchRefactorRequest transactionSearchRefactorRequest = new TransactionSearchRefactorRequest(
                shopId, startTime, searchString, orderBy, pageNumber, payMent, orderStatus);
//        TransactionSearchRefactorRequest transactionSearchRefactorRequest = new TransactionSearchRefactorRequest(
//                shopId, searchString, orderBy, pageNumber, "", "1");

        Account currentUser = AccountManager.getInstance().getCurrentAccount();
        String s = currentUser.getToken();
        String token = "token=" + s;

        TransantionRefactorService transantionRefactorService = retrofit.create(TransantionRefactorService.class);
        Call<HttpResult<TransactionResult<Order>>> call = transantionRefactorService.getTransactionList(
                token,
                transactionSearchRefactorRequest);

        call.enqueue(new Callback<HttpResult<TransactionResult<Order>>>() {
            @Override
            public void onResponse(Call<HttpResult<TransactionResult<Order>>> call,
                                   Response<HttpResult<TransactionResult<Order>>> response) {

                if (mProgressDialog.isShowing()){
                    mProgressDialog.dismiss();
                }

                if (!response.isSuccessful()){
                    Toast.makeText(TransactionSearchActivity.this, "获取数据失败", Toast.LENGTH_SHORT).show();
                    return;
                }

                HttpResult httpResult = response.body();
                TransactionResult result = (TransactionResult) httpResult.getData();
                isHavaNextPage = result.getNextPage();
                Log.e("weaweada", "isHavaNextPage--" +isHavaNextPage);
                Log.e("weaweada", result.toString());
                if (!isHavaNextPage.equals("false")){
                    //TransactionSearchActivity.pageNumber += 1;
                    String.valueOf(Integer.valueOf(TransactionSearchActivity.pageNumber) + 1);
                    pageNumberRefactor += 1;
                }

                String count = String.format(getResources().getString(R.string.search_result_count),
                        String.valueOf(result.getSearchCount()));
                searchCountTextView.setText(count);
                orders.addAll(result.getOrderList());
                searchResultAdapter.notifyDataSetChanged();
                LoadMoreScrollListener.setIsLoading(false);
            }

            @Override
            public void onFailure(Call<HttpResult<TransactionResult<Order>>> call, Throwable t) {
                LogHelper.getInstance().e(TAG, t.getMessage());
                LoadMoreScrollListener.setIsLoading(false);
                if (mProgressDialog.isShowing()){
                    mProgressDialog.dismiss();
                }
            }
        });
    }


    private void getTransaction(String startTime, String searchString, String orderBy, final int pageNumber, int i){
        String baseUrl = WebServerConfigManager.getWebServiceUrl();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();

        String shopId = AccountManager.getInstance().getCurrentAccount().getCurrentShopId();
//        String startTime = WSTimeStamp.getLastWeek();

        // init data for request
        TransactionSearchRequest transactionSearchRequest = new TransactionSearchRequest(
                shopId, startTime, searchString, orderBy, pageNumber);

        Account currentUser = AccountManager.getInstance().getCurrentAccount();
        String s = currentUser.getToken();
        String token = "token=" + s;

        TransactionService transactionService = retrofit.create(TransactionService.class);
        Call<HttpResult<TransactionResult<Order>>> call = transactionService.getTransactionList(
                token,
                transactionSearchRequest);

        call.enqueue(new Callback<HttpResult<TransactionResult<Order>>>() {
            @Override
            public void onResponse(Call<HttpResult<TransactionResult<Order>>> call,
                                   Response<HttpResult<TransactionResult<Order>>> response) {

                if (mProgressDialog.isShowing()){
                    mProgressDialog.dismiss();
                }

                if (!response.isSuccessful()){
                    Toast.makeText(TransactionSearchActivity.this, "获取数据失败", Toast.LENGTH_SHORT).show();
                    return;
                }

                HttpResult httpResult = response.body();
                TransactionResult result = (TransactionResult) httpResult.getData();
                isHavaNextPage = result.getNextPage();
                Log.e("weaweada", "isHavaNextPage--" +isHavaNextPage);
                Log.e("weaweada", result.toString());
                if (!isHavaNextPage.equals("false")){
                    //TransactionSearchActivity.pageNumber += 1;
                    String.valueOf(Integer.valueOf(TransactionSearchActivity.pageNumber) + 1);
                    pageNumberRefactor += 1;
                }

                String count = String.format(getResources().getString(R.string.search_result_count),
                        String.valueOf(result.getSearchCount()));
                searchCountTextView.setText(count);
                orders.addAll(result.getOrderList());
                searchResultAdapter.notifyDataSetChanged();
                LoadMoreScrollListener.setIsLoading(false);
            }

            @Override
            public void onFailure(Call<HttpResult<TransactionResult<Order>>> call, Throwable t) {
                LogHelper.getInstance().e(TAG, t.getMessage());
                LoadMoreScrollListener.setIsLoading(false);
                if (mProgressDialog.isShowing()){
                    mProgressDialog.dismiss();
                }
            }
        });
    }
}
