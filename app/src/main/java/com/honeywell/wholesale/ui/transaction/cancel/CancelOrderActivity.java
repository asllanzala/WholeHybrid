package com.honeywell.wholesale.ui.transaction.cancel;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.honeywell.hybridapp.framework.log.LogHelper;
import com.honeywell.wholesale.R;
import com.honeywell.wholesale.framework.http.NativeJsonResponseListener;
import com.honeywell.wholesale.framework.http.WebClient;
import com.honeywell.wholesale.framework.model.AccountManager;
import com.honeywell.wholesale.framework.model.Order;
import com.honeywell.wholesale.ui.base.BaseActivity;
import com.honeywell.wholesale.ui.dashboard.adapter.TransactionOrderListAdapter;
import com.honeywell.wholesale.ui.order.OrderDetailActivity;
import com.honeywell.wholesale.ui.search.LoadMoreScrollListener;
import com.honeywell.wholesale.ui.transaction.TransactionQueryRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by H154326 on 16/12/7.
 * Email: yang.liu6@honeywell.com
 */

public class CancelOrderActivity extends BaseActivity {

    private static final String TAG = "CancelOrderActivity";
    private TransactionOrderListAdapter transactionOrderListAdapter;
    private ArrayList<Order> mOrderDataList;

    private ListView mOrdersListView;

    private ImageView backImageView;

    private WebClient webClient;

    private TransactionQueryRequest transactionQueryRequest;

    private String pageNumber = "0";
    private static String isLoadMore = "false";

    private LoadMoreScrollListener loadMoreScrollListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_cancel);
    }

    @Override
    protected void onResume() {
        super.onResume();
        initData();
        initView();
        initAdapter();
    }

    private void initData(){

        webClient = new WebClient();
        transactionQueryRequest = new TransactionQueryRequest();
        if (mOrdersListView == null){
            mOrderDataList = new ArrayList<>();
        }
        pageNumber = "0";
        queryCancelOrder("100", false);
    }

    private void initView(){
        mOrdersListView = (ListView) findViewById(R.id.transaction_cancel_info_listView);

        backImageView = (ImageView) findViewById(R.id.transaction_cancel_back_imageView);
        backImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        loadMoreScrollListener = new LoadMoreScrollListener();
        mOrdersListView.setOnScrollListener(loadMoreScrollListener);
        loadMoreScrollListener.setLoadMore(new LoadMoreScrollListener.LoadMore(){
            @Override
            public void loadMoreData() {
                if (isLoadMore.equals("true")) {
                    queryCancelOrder("100", true);
                }
            }
        });

    }

    private void initAdapter(){
        transactionOrderListAdapter = new TransactionOrderListAdapter(mOrderDataList,this);
        mOrdersListView.setAdapter(transactionOrderListAdapter);
        mOrdersListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                transactionOrderListAdapter.notifyDataSetChanged();
                Order order = mOrderDataList.get(position);
                //H5 页面需要展现Order的shopName,所以把shopName
                // TODO 服务器没有返回,客户端 HACK
                order.setShopName(AccountManager.getInstance().getCurrentAccount().getShopName());
                // 打开订单详情

                Intent intent = new Intent(CancelOrderActivity.this, OrderDetailActivity.class);
                intent.putExtra(OrderDetailActivity.INTENT_VALUE_ORDER_DETAIL_JSON, order.getJsonString());
                startActivity(intent);
            }
        });

    }

    private void queryCancelOrder(String orderStatus, final boolean isCallFromLoadMore) {
        String shopId = AccountManager.getInstance().getCurrentAccount().getCurrentShopId();

        transactionQueryRequest = new TransactionQueryRequest(shopId, pageNumber, "finish_time",
                "", orderStatus);

        webClient.httpQueryTransactionOrders(transactionQueryRequest, new NativeJsonResponseListener<JSONObject>() {
            @Override
            public void listener(JSONObject jsonObject) {
                String orderCount = jsonObject.optString("count");
                JSONArray jsonArray = jsonObject.optJSONArray("total_order_list");
                String nextPage = jsonObject.optString("next_page");
                Log.e(TAG, "已取消" + jsonArray.toString());
                isLoadMore = nextPage;
                if (isLoadMore.equals("true")) {
                    pageNumber = String.valueOf(Integer.valueOf(pageNumber) + 1);
                }

                // 解析order数据
                Gson gson = new Gson();
                String ordersJson = jsonArray.toString();

                Type collectionType = new TypeToken<Collection<Order>>() {
                }.getType();
                ArrayList<Order> orders = gson.fromJson(ordersJson, collectionType);

                if (isCallFromLoadMore) {
                    mOrderDataList.addAll(orders);
                } else {
                    mOrderDataList = orders;
                }

//                mOrdersListView.setAdapter(transactionOrderListAdapter);
                transactionOrderListAdapter.setListData(mOrderDataList);
                transactionOrderListAdapter.notifyDataSetChanged();

                LoadMoreScrollListener.setIsLoading(false);

            }

            @Override
            public void errorListener(String s) {
                //错误的情况下,页面展现
                LogHelper.getInstance().e(TAG, "查询销售订单下的已取消失败:");

            }
        });
    }
}
