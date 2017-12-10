package com.honeywell.wholesale.ui.purchase.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import com.google.gson.Gson;
import com.honeywell.wholesale.R;
import com.honeywell.wholesale.framework.database.CartDAO;
import com.honeywell.wholesale.framework.database.SupplierDAO;
import com.honeywell.wholesale.framework.event.PayRIghtNowEvent;
import com.honeywell.wholesale.framework.model.AccountManager;
import com.honeywell.wholesale.framework.model.Supplier;
import com.honeywell.wholesale.ui.base.BaseActivity;
import com.honeywell.wholesale.ui.inventory.ProductShipmentActivity;
import com.honeywell.wholesale.ui.purchase.adapter.SupplierSelectAdapter;
import com.honeywell.wholesale.ui.purchase.presenter.SupplierSelectPresenter;
import com.honeywell.wholesale.ui.purchase.view.SupplierSelectView;
import com.honeywell.wholesale.ui.transaction.cart.CartManagementActivity;
import com.honeywell.wholesale.ui.transaction.preorders.Adapter.IndividualGuestAdapter;
import com.honeywell.wholesale.ui.transaction.preorders.PreTransactionActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * Created by liuyang on 2017/7/5.
 */

public class SupplierSelectActivity extends BaseActivity implements SupplierSelectView {
    private static final String TAG = SupplierSelectActivity.class.getSimpleName();
    private static final String INTENT_VALUE_CART_CUSTOMER_ID = "INTENT_VALUE_CART_CUSTOMER_ID";
    private ImageView backImageView;

    private ListView supplierListView;

    private SupplierSelectPresenter supplierSelectPresenter;

    private SupplierSelectAdapter listAdapter;
    private IndividualGuestAdapter individualGuestAdapter;

    private ArrayList<Object> supplierWithGroupList = new ArrayList<>();
    private List<String> individualGuestList;

    private static final String INDIVIDUAL_GUEST_1_ID = "individual_id_1";
    private static final String INDIVIDUAL_GUEST_2_ID = "individual_id_2";
    private static final String INDIVIDUAL_GUEST_3_ID = "individual_id_3";

//    private String judgePreTransaction = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        setContentView(R.layout.activity_select_supplier);
        supplierSelectPresenter = new SupplierSelectPresenter(this);
//        supplierSelectPresenter.attach(this);
        initView();
        initData();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        supplierSelectPresenter.detach();
        EventBus.getDefault().unregister(this);//反注册EventBus
    }

    @Override
    public void onShowLoading() {

    }

    @Override
    public void onHideLoading() {

    }

    @Override
    public void onToast(String message) {

    }

    @Override
    public void onIntent() {

    }

    @Override
    public Context getContext() {
        return this;
    }

    public void onEventMainThread(PayRIghtNowEvent event) {
        String msg = "SupplierSelectActivity onEventMainThread收到了消息：" + event.getmMsg();
        Log.e(TAG, msg);
        if (event.getmMsg().equals(ProductShipmentActivity.INTENT_KEY_PAY_RIGHT_NOW_EVENT)) {
            finish();
        }
    }

    private void initView() {
        backImageView = (ImageView) findViewById(R.id.icon_back);
        supplierListView = (ListView) findViewById(R.id.customer_listView);

        backImageView.setOnClickListener(onClickListener);
        supplierListView.setOnItemClickListener(onItemClickListener);
    }

    private void initData() {
        try {
            supplierWithGroupList = supplierSelectPresenter.initSupplierListData();
        } catch (JSONException e) {
            Log.e(TAG, "初始化供应商列表错误");
            e.printStackTrace();
        }

        listAdapter = new SupplierSelectAdapter(this, supplierWithGroupList);
        supplierListView.setAdapter(listAdapter);
    }


    private AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (parent.getId() == R.id.customer_listView) {
                if (supplierWithGroupList.get(position) instanceof String) {
                    Log.e(TAG, "click section");
                } else {
                    String shopId = AccountManager.getInstance().getCurrentShopId();
                    String employeeId = AccountManager.getInstance().getCurrentAccount().getEmployeeId();
                    Supplier supplier = (Supplier) supplierWithGroupList.get(position);
                    Intent intent = new Intent();
                    intent.putExtra(PurchaseActivity.INTENT_SUPPLIER_ID, supplier.getSupplierId());
                    intent.putExtra(PurchaseActivity.INTENT_SUPPLIER_NAME, supplier.getSupplierName());
                    setResult(PurchaseActivity.RESULT_SUPPLIER_SUCCEED_CODE, intent);
                    finish();
                }
            }
        }
    };

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.icon_back) {
                finish();
            }
        }
    };
}
