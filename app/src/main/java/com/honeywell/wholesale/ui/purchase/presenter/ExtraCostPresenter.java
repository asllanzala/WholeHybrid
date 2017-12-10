package com.honeywell.wholesale.ui.purchase.presenter;

import android.util.Log;

import com.google.gson.Gson;
import com.honeywell.hybridapp.framework.log.LogHelper;
import com.honeywell.wholesale.framework.http.NativeJsonResponseListener;
import com.honeywell.wholesale.framework.http.WebClient;
import com.honeywell.wholesale.framework.model.ExtraCost;
import com.honeywell.wholesale.framework.model.Inventory;
import com.honeywell.wholesale.framework.model.WareHouse;
import com.honeywell.wholesale.ui.base.WholesaleBasePresenter;
import com.honeywell.wholesale.ui.menu.setting.warehouse.request.WareHouseListRequest;
import com.honeywell.wholesale.ui.purchase.activity.ExtraCostSelectActivity;
import com.honeywell.wholesale.ui.purchase.view.ExtraCostView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by liuyang on 2017/7/5.
 */

public class ExtraCostPresenter extends WholesaleBasePresenter<ExtraCostView> {

    private static final String TAG = ExtraCostPresenter.class.getSimpleName();

    private WebClient webClient;

    public ExtraCostPresenter(ExtraCostView view) {
        super(view);
        webClient = new WebClient();
    }

    public void getExtraCostDataFromServer() {
        Log.e(TAG, "getExtraCostDataFromServer");
        webClient.httpQueryExtraCost(new NativeJsonResponseListener<JSONObject>() {
            @Override
            public void listener(JSONObject jsonObject) {
                if (jsonObject != null) {
                    ArrayList<ExtraCost> arrayList = new ArrayList();
                    JSONArray jsonArray = jsonObject.optJSONArray(WebClient.API_RESPONSE_RET_BODY);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        final Gson gson = new Gson();
                        JSONObject extraCostItem = jsonArray.optJSONObject(i);
                        ExtraCost extraCost = gson.fromJson(extraCostItem.toString(), ExtraCost.class);
                        arrayList.add(extraCost);
                    }
                    mView.setListData(arrayList);
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

}
