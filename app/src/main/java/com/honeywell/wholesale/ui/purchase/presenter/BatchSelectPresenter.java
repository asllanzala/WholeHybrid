package com.honeywell.wholesale.ui.purchase.presenter;

import android.util.Log;

import com.google.gson.Gson;
import com.honeywell.hybridapp.framework.log.LogHelper;
import com.honeywell.wholesale.framework.http.NativeJsonResponseListener;
import com.honeywell.wholesale.framework.http.WebClient;
import com.honeywell.wholesale.framework.model.BatchModel;
import com.honeywell.wholesale.framework.model.ExtraCost;
import com.honeywell.wholesale.ui.base.WholesaleBasePresenter;
import com.honeywell.wholesale.ui.purchase.request.BatchAddBean;
import com.honeywell.wholesale.ui.purchase.view.BatchSelectView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by liuyang on 2017/7/5.
 */

public class BatchSelectPresenter extends WholesaleBasePresenter<BatchSelectView> {

    private static final String TAG = ExtraCostPresenter.class.getSimpleName();

    private WebClient webClient;

    private BatchAddBean batchAddBean;

    private String currentDay = "";

    public BatchSelectPresenter(BatchSelectView view) {
        super(view);
        webClient = new WebClient();
    }

    public void getBatchDataFromServer() {
        Log.e(TAG, "getBatchDataFromServer");
        webClient.httpQueryBatch(new NativeJsonResponseListener<JSONObject>() {
            @Override
            public void listener(JSONObject jsonObject) {
                if (jsonObject != null) {
                    ArrayList<BatchModel> arrayList = new ArrayList();
                    currentDay = jsonObject.optString("current_time");
                    JSONArray jsonArray = jsonObject.optJSONArray("batch_list");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        final Gson gson = new Gson();
                        JSONObject batchDayItem = jsonArray.optJSONObject(i);
                        BatchModel batchDay = gson.fromJson(batchDayItem.toString(), BatchModel.class);
                        arrayList.add(batchDay);
                    }
                    mView.setListData(currentDay, arrayList);
                }
            }

            @Override
            public void errorListener(String s) {
                if (s != null) {
                    LogHelper.getInstance().e(TAG, s);
                }
            }
        });
    }

    public void addBatchDataFromServer(String batchName) {
        Log.e(TAG, "getExtraCostDataFromServer");

        batchAddBean = new BatchAddBean(batchName);

        webClient.httpAddBatch(batchAddBean, new NativeJsonResponseListener<JSONObject>() {
            @Override
            public void listener(JSONObject jsonObject) {
                if (jsonObject != null) {
                    getBatchDataFromServer();
                }
            }

            @Override
            public void errorListener(String s) {
                mView.onToast("单日内，批次号不可重复");
            }
        });
    }
}
