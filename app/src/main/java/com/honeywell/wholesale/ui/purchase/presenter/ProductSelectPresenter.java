package com.honeywell.wholesale.ui.purchase.presenter;

import android.content.Context;

import com.google.gson.Gson;
import com.honeywell.wholesale.R;
import com.honeywell.wholesale.framework.http.NativeJsonResponseListener;
import com.honeywell.wholesale.framework.http.WebClient;
import com.honeywell.wholesale.framework.model.Inventory;
import com.honeywell.wholesale.ui.base.WholesaleBasePresenter;
import com.honeywell.wholesale.ui.purchase.activity.PurchaseSelectActivity;
import com.honeywell.wholesale.ui.purchase.view.ProductSelectView;
import com.honeywell.wholesale.ui.search.LoadMoreScrollListener;
import com.honeywell.wholesale.ui.transaction.preorders.network.PreOrderSearchRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by liuyang on 2017/7/5.
 */

public class ProductSelectPresenter extends WholesaleBasePresenter<ProductSelectView> {

    private static final String TAG = ProductSelectPresenter.class.getSimpleName();

    private String pageNumber = "0";
    private String stockStatus = "0";

    private Context context;

    private ArrayList<Inventory> arrayList = new ArrayList<>();

    private String isLoadMore = "false";

    private PreOrderSearchRequest preOrderSearchRequest;

    private WebClient webClient;

    public ProductSelectPresenter(ProductSelectView view) {
        super(view);
        webClient = new WebClient();
    }

    public String getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(String pageNumber) {
        this.pageNumber = pageNumber;
    }

    public void getDataFromServer(String s, final boolean isCallFormLoadMore) {

        preOrderSearchRequest = new PreOrderSearchRequest(0, s, pageNumber, stockStatus);

        webClient.httpGetInventories(preOrderSearchRequest, new NativeJsonResponseListener<JSONObject>() {
            @Override
            public void listener(JSONObject jsonObject) {
                if (jsonObject != null) {
                    final Gson gson = new Gson();
                    ArrayList<Inventory> arrayList1 = new ArrayList<Inventory>();
                    String total = jsonObject.optString("total");
                    String nextPage = jsonObject.optString("next_page");
                    isLoadMore = nextPage;
                    mView.setLoadMore(isLoadMore);
                    if (isLoadMore.equals("true")) {
                        pageNumber = String.valueOf(Integer.valueOf(pageNumber) + 1);
                    }
                    JSONArray jsonArray = jsonObject.optJSONArray("product_list");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject inventoryItem = jsonArray.optJSONObject(i);
                        Inventory invetory = gson.fromJson(inventoryItem.toString(), Inventory.class);
                        arrayList1.add(invetory);
                    }
                    if (isCallFormLoadMore) {
                        arrayList.addAll(arrayList1);
                    } else {
                        arrayList = arrayList1;
                    }
                    mView.setArrayListData(arrayList);
                    mView.setAdapterDataSource(arrayList);
                    LoadMoreScrollListener.setIsLoading(false);
                    String count = String.format(mView.getContext().getResources().getString(R.string.search_result_count), total);
                    mView.setResultCountText(count);
//                    resultCountTextView.setText(count);
                }
            }

            @Override
            public void errorListener(String s) {

            }
        });
    }

    public void cancelRequest(){
        webClient.cancelRequest("TAG_CANCEL");
    }
}
