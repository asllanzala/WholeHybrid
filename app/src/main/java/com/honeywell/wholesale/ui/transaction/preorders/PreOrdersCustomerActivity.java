package com.honeywell.wholesale.ui.transaction.preorders;

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
import com.honeywell.wholesale.framework.database.CustomerDAO;
import com.honeywell.wholesale.framework.event.PayRIghtNowEvent;
import com.honeywell.wholesale.framework.model.AccountManager;
import com.honeywell.wholesale.framework.model.Customer;
import com.honeywell.wholesale.ui.base.BaseActivity;
import com.honeywell.wholesale.ui.inventory.ProductShipmentActivity;
import com.honeywell.wholesale.ui.transaction.cart.CartManagementActivity;
import com.honeywell.wholesale.ui.transaction.preorders.Adapter.CustomerListAdapter;
import com.honeywell.wholesale.ui.transaction.preorders.Adapter.IndividualGuestAdapter;

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
 * Created by xiaofei on 11/30/16.
 *
 */

public class PreOrdersCustomerActivity extends BaseActivity {
    private static final String TAG = PreOrdersCustomerActivity.class.getSimpleName();
    private static final String INTENT_VALUE_CART_CUSTOMER_ID = "INTENT_VALUE_CART_CUSTOMER_ID";
    private ImageView backImageView;

    private ListView customerListView;
    private ListView individualGuestListView;

    private CustomerListAdapter listAdapter;
    private IndividualGuestAdapter individualGuestAdapter;

    private ArrayList<Object> customerWithGroupList = new ArrayList<>();
    private List<String> individualGuestList;

    private static final String INDIVIDUAL_GUEST_1_ID = "individual_id_1";
    private static final String INDIVIDUAL_GUEST_2_ID = "individual_id_2";
    private static final String INDIVIDUAL_GUEST_3_ID = "individual_id_3";

    private String judgePreTransaction = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        setContentView(R.layout.activity_preorders_customer);
        initView();
        initData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);//反注册EventBus
    }

    public void onEventMainThread(PayRIghtNowEvent event) {
        String msg = "PreOrdersCustomerActivity onEventMainThread收到了消息：" + event.getmMsg();
        Log.e(TAG, msg);
        if (event.getmMsg().equals(ProductShipmentActivity.INTENT_KEY_PAY_RIGHT_NOW_EVENT)){
            finish();
        }
    }

    private void initView(){
        backImageView = (ImageView)findViewById(R.id.icon_back);
        customerListView = (ListView) findViewById(R.id.customer_listView);
        individualGuestListView = (ListView)findViewById(R.id.guest_listView);

        backImageView.setOnClickListener(onClickListener);
        customerListView.setOnItemClickListener(onItemClickListener);
        individualGuestListView.setOnItemClickListener(onItemClickListener);
    }

    private void initData(){
        try {
            initCustomerListData();
        } catch (JSONException e) {
            Log.e(TAG, "初始化用户列表错误");
            e.printStackTrace();
        }

        Intent intent = getIntent();
        judgePreTransaction = intent.getStringExtra(PreTransactionActivity.INTENT_CHOOSE_CUSTOMER);

        listAdapter = new CustomerListAdapter(this, customerWithGroupList);
        customerListView.setAdapter(listAdapter);

        String[] resArray = getResources().getStringArray(R.array.individual_guest);
        individualGuestList = Arrays.asList(resArray);
        individualGuestAdapter = new IndividualGuestAdapter(individualGuestList, this);
        individualGuestListView.setAdapter(individualGuestAdapter);
    }

    private void initCustomerListData() throws JSONException{
        String shopId = AccountManager.getInstance().getCurrentAccount().getCurrentShopId();
        String customerJsonString = CustomerDAO.getAllCustomerWithGroup(shopId);

        JSONObject customerJsonObject = null;
        Gson gson = new Gson();

        customerJsonObject = new JSONObject(customerJsonString);

        Iterator<String> bfKeys = customerJsonObject.keys();

        List<String> list = new ArrayList<String>();
        while (bfKeys.hasNext()) {
            String key = bfKeys.next();
            list.add(key);
        }
        Collections.sort(list);
        Iterator<String> keys = list.iterator();

        while(keys.hasNext()) {
            String key = keys.next();
            if (customerJsonObject.get(key) instanceof JSONArray) {
                JSONArray jsonArray = customerJsonObject.optJSONArray(key);
                if (jsonArray.length() == 0){
                    continue;
                }
                customerWithGroupList.add(key);
                for (int i=0; i < jsonArray.length(); i++) {
                    JSONObject customerItem = jsonArray.getJSONObject(i);
                    Customer customer = gson.fromJson(customerItem.toString(), Customer.class);
                    customerWithGroupList.add(customer);
                }
            }
        }
    }


    private AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (parent.getId() == R.id.customer_listView){
                if (customerWithGroupList.get(position) instanceof String){
                    Log.e(TAG, "click section");
                }else {
                    String shopId = AccountManager.getInstance().getCurrentShopId();
                    String employeeId = AccountManager.getInstance().getCurrentAccount().getEmployeeId();
                    Customer customer = (Customer) customerWithGroupList.get(position);
                    int count = CartDAO.getCartItemsCount(shopId, customer.getCustomeId(), employeeId);

                    if (judgePreTransaction.equals(PreTransactionActivity.INTENT_CHOOSE_CUSTOMER)){
                        Intent intent =new Intent();
                        intent.putExtra(PreTransactionActivity.INTENT_CUSTOMER_ID, customer.getCustomeId());
                        intent.putExtra(PreTransactionActivity.INTENT_CUSTOMER_NAME,customer.getCustomerName());
                        setResult(PreTransactionActivity.RESULT_CUSTOMER_SUCCEED_CODE, intent);
                        finish();
                    }else {
//                        if (count == 0){
//                            Order.createNewOrder(customer);
//                        }
                        Intent intent = new Intent(PreOrdersCustomerActivity.this, CartManagementActivity.class);
                        intent.putExtra(CartManagementActivity.INTENT_VALUE_CART_CUSTOMER_ID, customer.getCustomeId());
                        startActivity(intent);
                        Log.e(TAG, "customer click");
                    }
                }
            }

            if (parent.getId() == R.id.guest_listView){
                String customerId = "";
                String customerName = "";
                if (position == 0){
                    customerId = "0";
                    customerName = individualGuestList.get(0);
                }
//                if (position == 1){
//                    customerId = INDIVIDUAL_GUEST_2_ID;
//                    customerName = individualGuestList.get(1);
//                }
//
//                if (position == 2){
//                    customerId = INDIVIDUAL_GUEST_3_ID;
//                    customerName = individualGuestList.get(2);
//                }

                String shopId = AccountManager.getInstance().getCurrentShopId();
                String employeeId = AccountManager.getInstance().getCurrentAccount().getEmployeeId();
                Customer customer = new Customer(customerId, customerName, shopId);
                customer.setCustomerName(customerName);

                int count = CartDAO.getCartItemsCount(shopId, customer.getCustomeId(), employeeId);

//                if (count == 0){
//                    Order.createNewOrder(customer);
//                }

                if (judgePreTransaction.equals(PreTransactionActivity.INTENT_CHOOSE_CUSTOMER)){
                    Intent intent =new Intent();
                    intent.putExtra(PreTransactionActivity.INTENT_CUSTOMER_ID, customer.getCustomeId());
                    intent.putExtra(PreTransactionActivity.INTENT_CUSTOMER_NAME,customer.getCustomerName());
                    setResult(PreTransactionActivity.RESULT_CUSTOMER_SUCCEED_CODE, intent);
                    finish();
                }else {
                    Intent intent = new Intent(PreOrdersCustomerActivity.this, CartManagementActivity.class);
                    intent.putExtra(CartManagementActivity.INTENT_VALUE_CART_CUSTOMER_ID, customerId);
                    startActivity(intent);
                }
            }
        }
    };

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.icon_back){
                finish();
            }
        }
    };

}
