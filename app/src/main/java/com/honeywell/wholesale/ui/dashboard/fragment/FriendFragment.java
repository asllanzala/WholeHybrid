package com.honeywell.wholesale.ui.dashboard.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.google.gson.Gson;
import com.honeywell.hybridapp.framework.log.LogHelper;
import com.honeywell.wholesale.R;
import com.honeywell.wholesale.framework.database.CustomerDAO;
import com.honeywell.wholesale.framework.database.IncrementalDAO;
import com.honeywell.wholesale.framework.database.SupplierDAO;
import com.honeywell.wholesale.framework.http.NativeJsonResponseListener;
import com.honeywell.wholesale.framework.http.WebClient;
import com.honeywell.wholesale.framework.model.Account;
import com.honeywell.wholesale.framework.model.AccountManager;
import com.honeywell.wholesale.framework.model.Customer;
import com.honeywell.wholesale.framework.model.Supplier;
import com.honeywell.wholesale.framework.search.SearchResultItem;

import com.honeywell.wholesale.lib.util.StringUtil;
import com.honeywell.wholesale.ui.base.BaseRootFragment;
import com.honeywell.wholesale.ui.base.BaseTextView;
import com.honeywell.wholesale.ui.friend.customer.AddCustomerActivity;
import com.honeywell.wholesale.ui.friend.customer.CustomerAdapter;
import com.honeywell.wholesale.ui.friend.supplier.AddSupplierActivity;
import com.honeywell.wholesale.ui.friend.supplier.SupplierAdapter;
import com.honeywell.wholesale.ui.inventory.AddProductActivity;
import com.honeywell.wholesale.ui.login.module.ListCustomerRequest;
import com.honeywell.wholesale.ui.login.module.ListSupplierRequest;
import com.honeywell.wholesale.ui.menu.customer.CustomerDetailActivity;
import com.honeywell.wholesale.ui.menu.supplier.SupplierDetailActivity;
import com.honeywell.wholesale.ui.search.BaseSearchActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import static com.honeywell.wholesale.R.color.transaction_tab_bg_color;
import static com.honeywell.wholesale.R.color.transaction_tab_selected_bg_color;


/**
 * Created by H154326 on 16/12/21.
 * Email: yang.liu6@honeywell.com
 */

public class FriendFragment extends BaseRootFragment {
    public static final String TAG = FriendFragment.class.getSimpleName();
    public static String judgeSelectedTab = "";
    private String shopId;

    private View customerSelectedView;
    private View supplierSelectedView;

    private BaseTextView customerTextView;
    private BaseTextView supplierTextView;

    private ListView customerListView;

    private WebClient webClient;

    private ArrayList<Object> customerWithGroupList;
    private ArrayList<Object> supplierWithGroupList;

    private CustomerAdapter customerAdapter;
    private SupplierAdapter supplierAdapter;


    public FriendFragment() {
    }

    @Override
    public int getLayout() {
        return R.layout.fragment_friend_native;
    }

    @Override
    public int getIndex() {
        return 3;
    }

    @Override
    public void initImageMore(ImageView view) {
        super.initImageMore(view);
        view.setImageResource(R.drawable.dashboard_title_plus);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (judgeSelectedTab.equals(getString(R.string.friend_fragment_tab_customer))) {
                    startActivity(new Intent(getActivity(), AddCustomerActivity.class));
                }
                if (judgeSelectedTab.equals(getResources().getString(R.string.friend_fragment_tab_supplier))) {
                    startActivity(new Intent(getActivity(), AddSupplierActivity.class));
                }

            }
        });
    }

    @Override
    public void initLayoutSearch(View view) {
        super.initLayoutSearch(view);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), BaseSearchActivity.class);
                Bundle mBundle = new Bundle();
                mBundle.putString(BaseSearchActivity.SEARCH_MODE, BaseSearchActivity.SEARCH_SELECT);
                mBundle.putSerializable(BaseSearchActivity.SEARCH_KEY, getJudgeSelectedTab() == 1 ? SearchResultItem.ResultType.CUSTOMER : SearchResultItem.ResultType.VENDOR);
                intent.putExtras(mBundle);
                startActivity(intent);
            }
        });
    }
    @Override
    public void initView(View view) {
        super.initView(view);
        initViews(view);
        switchToCustomer();
    }

    @Override
    public void reLoadData() {

    }

    @Override
    public String getTitle() {
        return StringUtil.getString(R.string.dashboard_fragment_title_tab_friend);
    }


    @Override
    public void onStart() {
        super.onStart();
        //同步客户的数据
        Log.e(TAG,"onstart");
        initData();

    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e(TAG,"onresume");
        syncCustomerDataFromServer();
        //同步供应商的数据
        syncSupplierDataFromServer();
    }

    private void initData() {
        Account account = AccountManager.getInstance().getCurrentAccount();
        shopId = account.getCurrentShopId();

        try {
            initCustomerListData();
        } catch (JSONException e) {
            Log.e(TAG, "初始化用户列表错误");
            e.printStackTrace();
        }

        try {
            initSupplierListData();
        } catch (JSONException e) {
            Log.e(TAG, "初始化用户列表错误");
            e.printStackTrace();
        }

        customerAdapter = new CustomerAdapter(getActivity(), customerWithGroupList);
        customerListView.setAdapter(customerAdapter);
        customerAdapter.notifyDataSetChanged();

        supplierAdapter = new SupplierAdapter(getActivity(), supplierWithGroupList);
        supplierAdapter.notifyDataSetChanged();


        if (judgeSelectedTab.equals(getResources().getString(R.string.friend_fragment_tab_customer))) {
            customerListView.setAdapter(customerAdapter);
        } else if (judgeSelectedTab.equals(getResources().getString(R.string.friend_fragment_tab_supplier))) {
            customerListView.setAdapter(supplierAdapter);
        }
    }

    private void initViews(View view) {

        customerSelectedView = view.findViewById(R.id.friend_tab_customer_selected_sign);
        customerSelectedView.setSelected(true);
        supplierSelectedView = view.findViewById(R.id.friend_tab_supplier_selected_sign);

        customerListView = (ListView) view.findViewById(R.id.friend_customer_listView);
        customerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (customerTextView.isSelected()) {
                    if (customerWithGroupList.get(position) instanceof String) {
                        Log.e(TAG, "click section");
                    } else {

                        Customer customer = (Customer) customerWithGroupList.get(position);
                        Intent intent = new Intent(getActivity(), CustomerDetailActivity.class);
                        intent.putExtra(CustomerDetailActivity.INTENT_KEY_CUSTOMER_INFO, customer.getJsonString());
                        startActivity(intent);

                        Log.e(TAG, "customer click");
                    }
                } else if (supplierTextView.isSelected()) {
                    if (supplierWithGroupList.get(position) instanceof String) {
                        Log.e(TAG, "click section");
                    } else {

                        Supplier supplier = (Supplier) supplierWithGroupList.get(position);
                        Intent intent = new Intent(getActivity(), SupplierDetailActivity.class);
                        intent.putExtra(SupplierDetailActivity.INTENT_KEY_VENDOR_INFO, supplier.getJsonString());
                        startActivity(intent);

                        Log.e(TAG, "supplier click");
                    }
                }
            }
        });

        customerTextView = (BaseTextView) view.findViewById(R.id.friend_tab_customer);
        customerTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                switchToCustomer();
                customerListView.setAdapter(customerAdapter);
                customerAdapter.setListData(customerWithGroupList);
                customerAdapter.notifyDataSetChanged();
            }
        });
        supplierTextView = (BaseTextView) view.findViewById(R.id.friend_tab_supplier);
        supplierTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                switchToSupplier();
                customerListView.setAdapter(supplierAdapter);
                supplierAdapter.setListData(supplierWithGroupList);
                supplierAdapter.notifyDataSetChanged();
            }
        });

//        searchLayout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (judgeSelectedTab.equals(getResources().getString(R.string.friend_fragment_tab_customer))) {
//                    Intent intent = new Intent(getActivity(), BaseSearchActivity.class);
//                    Bundle mBundle = new Bundle();
//                    mBundle.putSerializable(BaseSearchActivity.SEARCH_KEY, SearchResultItem.ResultType.CUSTOMER);
//                    intent.putExtras(mBundle);
//                    startActivity(intent);
//                } else if (judgeSelectedTab.equals(getResources().getString(R.string.friend_fragment_tab_supplier))) {
//                    Intent intent = new Intent(getActivity(), BaseSearchActivity.class);
//                    Bundle mBundle = new Bundle();
//                    mBundle.putSerializable(BaseSearchActivity.SEARCH_KEY, SearchResultItem.ResultType.VENDOR);
//                    intent.putExtras(mBundle);
//                    startActivity(intent);
//                }
//            }
//        });
    }


    private void initCustomerListData() throws JSONException {
        customerWithGroupList = new ArrayList<>();
        shopId = AccountManager.getInstance().getCurrentAccount().getCurrentShopId();
        String customerJsonString = CustomerDAO.getAllCustomerWithGroup(shopId);

        //JSONObject jsonObject =null;
        JSONObject customerJsonObject = null;
        //customerJsonObject = new JSONObject();

        //jsonObject = CustomerDAO.getAllCustomerObjectWithGroup(shopId);
        //customerJsonObject = CustomerDAO.getAllCustomerObjectWithGroup(shopId);


        //WSLinkedJSONObject customerJsonObject = null;
        //JSONObject customerJsonObject = null;
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

        while (keys.hasNext()) {
            String key = keys.next();
            if (customerJsonObject.get(key) instanceof JSONArray) {
                JSONArray jsonArray = customerJsonObject.optJSONArray(key);
                if (jsonArray.length() == 0) {
                    continue;
                }
                customerWithGroupList.add(key);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject customerItem = jsonArray.getJSONObject(i);
                    Customer customer = gson.fromJson(customerItem.toString(), Customer.class);
                    customerWithGroupList.add(customer);
                }
            }
        }
    }


    private void initSupplierListData() throws JSONException {
        supplierWithGroupList = new ArrayList<>();
        shopId = AccountManager.getInstance().getCurrentAccount().getCurrentShopId();
        String supplierJsonString = SupplierDAO.getAllSupplierWithGroup(shopId);

        JSONObject supplierJsonObject = null;
        Gson gson = new Gson();

        supplierJsonObject = new JSONObject(supplierJsonString);

        Iterator<String> bfKeys = supplierJsonObject.keys();

        List<String> list = new ArrayList<String>();
        while (bfKeys.hasNext()) {
            String key = bfKeys.next();
            list.add(key);
        }
        Collections.sort(list);
        Iterator<String> keys = list.iterator();
        while (keys.hasNext()) {
            String key = keys.next();
            if (supplierJsonObject.get(key) instanceof JSONArray) {
                JSONArray jsonArray = supplierJsonObject.optJSONArray(key);
                if (jsonArray.length() == 0) {
                    continue;
                }
                supplierWithGroupList.add(key);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject supplierItem = jsonArray.getJSONObject(i);
                    Supplier supplier = gson.fromJson(supplierItem.toString(), Supplier.class);
                    supplierWithGroupList.add(supplier);
                }
            }
        }
    }

    private void syncCustomerDataFromServer() {
        shopId = AccountManager.getInstance().getCurrentAccount().getCurrentShopId();
        String lastRequestTime = IncrementalDAO.queryIncrementalItem(Customer.class, shopId);
        ListCustomerRequest customerRequest = new ListCustomerRequest(shopId, lastRequestTime);
        webClient = new WebClient();

        webClient.httpGetCustomer(customerRequest, new NativeJsonResponseListener<JSONObject>() {
            @Override
            public void listener(JSONObject jsonObject) {
                try {
                    initCustomerListData();
                } catch (JSONException e) {
                    Log.e(TAG, "初始化客户列表错误");
                    e.printStackTrace();
                }
                customerAdapter.setListData(customerWithGroupList);
                customerAdapter.notifyDataSetChanged();
            }

            @Override
            public void errorListener(String s) {
                LogHelper.e("客户网络访问失败", s);
            }
        });
    }

    private void syncSupplierDataFromServer() {
        shopId = AccountManager.getInstance().getCurrentAccount().getCurrentShopId();
        String lastRequestTime = IncrementalDAO.queryIncrementalItem(Supplier.class, shopId);
        ListSupplierRequest supplierRequest = new ListSupplierRequest(shopId, lastRequestTime);
        webClient = new WebClient();

        webClient.httpGetSupplier(supplierRequest, new NativeJsonResponseListener<JSONObject>() {
            @Override
            public void listener(JSONObject jsonObject) {
                try {
                    initSupplierListData();
                } catch (JSONException e) {
                    Log.e(TAG, "初始化供应商列表错误");
                    e.printStackTrace();
                }
                supplierAdapter.setListData(supplierWithGroupList);
                supplierAdapter.notifyDataSetChanged();
            }

            @Override
            public void errorListener(String s) {
                LogHelper.e("供应商网络访问失败", s);
            }
        });
    }

    public static int getJudgeSelectedTab(){
        if (judgeSelectedTab.equals("客户")) {
            return 1;
        } else if (judgeSelectedTab.equals("供应商")) {
            return 2;
        }
        return 0;
    }

    private void switchToCustomer() {
        customerTextView.setSelected(true);
        supplierTextView.setSelected(false);
        customerTextView.setBackgroundColor(getContext().getResources().getColor(R.color.transaction_tab_selected_bg_color));
        customerTextView.setTextColor(getContext().getResources().getColor(R.color.white));
        supplierTextView.setBackgroundColor(getContext().getResources().getColor(R.color.transaction_tab_bg_color));
        supplierTextView.setTextColor(getContext().getResources().getColor(R.color.black));
        judgeSelectedTab = getResources().getString(R.string.friend_fragment_tab_customer);
        customerSelectedView.setBackgroundColor(getContext().getResources().getColor(R.color.taransaction_tab_selected_color));
        supplierSelectedView.setBackgroundColor(getContext().getResources().getColor(transaction_tab_bg_color));
    }

    private void switchToSupplier() {
        customerTextView.setSelected(false);
        supplierTextView.setSelected(true);
        customerTextView.setBackgroundColor(getContext().getResources().getColor(R.color.transaction_tab_bg_color));
        customerTextView.setTextColor(getContext().getResources().getColor(R.color.black));
        supplierTextView.setBackgroundColor(getContext().getResources().getColor(R.color.transaction_tab_selected_bg_color));
        supplierTextView.setTextColor(getContext().getResources().getColor(R.color.white));
        judgeSelectedTab = getResources().getString(R.string.friend_fragment_tab_supplier);
        customerSelectedView.setBackgroundColor(getContext().getResources().getColor(transaction_tab_bg_color));
        supplierSelectedView.setBackgroundColor(getContext().getResources().getColor(R.color.taransaction_tab_selected_color));
    }
}
