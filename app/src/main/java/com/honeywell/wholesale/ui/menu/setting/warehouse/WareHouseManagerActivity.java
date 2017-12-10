package com.honeywell.wholesale.ui.menu.setting.warehouse;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.honeywell.hybridapp.framework.log.LogHelper;
import com.honeywell.wholesale.R;
import com.honeywell.wholesale.framework.http.NativeJsonResponseListener;
import com.honeywell.wholesale.framework.http.WebClient;
import com.honeywell.wholesale.framework.model.AccountManager;
import com.honeywell.wholesale.framework.model.WareHouse;
import com.honeywell.wholesale.framework.model.WareHouseManager;
import com.honeywell.wholesale.ui.base.BaseActivity;
import com.honeywell.wholesale.ui.base.BaseTextView;
import com.honeywell.wholesale.ui.dashboard.fragment.ObserverChangeShop;
import com.honeywell.wholesale.ui.menu.setting.warehouse.adapter.WareHouseManagerAdaper;
import com.honeywell.wholesale.ui.menu.setting.warehouse.request.WareHouseAddRequest;
import com.honeywell.wholesale.ui.menu.setting.warehouse.request.WareHouseDefaultRequest;
import com.honeywell.wholesale.ui.menu.setting.warehouse.request.WareHouseListRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by H154326 on 17/4/18.
 * Email: yang.liu6@honeywell.com
 */

public class WareHouseManagerActivity extends BaseActivity {

    private static final String TAG = WareHouseManagerActivity.class.getSimpleName();

    private BaseTextView defaultWareHouseNameTextView;
    private BaseTextView defaultWareHouseSettingTextView;

    private BaseTextView titleTextView;

    private ListView listView;

    private ImageView plusImageView;
    private ImageView backImageView;

    private WareHouseManagerAdaper wareHouseManagerAdaper;

    private ArrayList<WareHouse> wareHouseList;
    private ArrayList<WareHouse> allWareHouseList;

    private int defaultWareHouseId;
    private int currentShopId = Integer.parseInt(AccountManager.getInstance().getCurrentShopId());

    private String defaultWareHouseName;

    private WebClient webClient;

    private WareHouseListRequest wareHouseListRequest;

    private WareHouseDefaultRequest wareHouseDefaultRequest;

    private WareHouseManager wareHouseManager;

    private static ObserverChangeShop observerChangeShop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_warehouse_manager);

        initData();
        initView();

    }

    @Override
    protected void onResume() {
        super.onResume();
        refresh();
    }

    private void initData() {
        wareHouseManager = WareHouseManager.getInstance();
        webClient = new WebClient();
        wareHouseListRequest = new WareHouseListRequest(currentShopId);
        wareHouseDefaultRequest = new WareHouseDefaultRequest();
        wareHouseList = new ArrayList<>();
        allWareHouseList = new ArrayList<>();
        defaultWareHouseId = -1;
        defaultWareHouseName = "";
        wareHouseManagerAdaper = new WareHouseManagerAdaper(getApplicationContext());
        wareHouseManagerAdaper.setObserverCartData(observerDefaultWareHouse);
    }

    private void initView() {
        defaultWareHouseNameTextView = (BaseTextView) findViewById(R.id.warehouse_manager_default_name_textview);
        defaultWareHouseSettingTextView = (BaseTextView) findViewById(R.id.warehouse_manager_default_setting_textview);
        titleTextView = (BaseTextView) findViewById(R.id.cart_management_title_textview);
        titleTextView.setText("仓库管理");
        listView = (ListView) findViewById(R.id.warehouse_manager_listview);
        listView.setAdapter(wareHouseManagerAdaper);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                WareHouse currentWareHouse = wareHouseList.get(position);
                String wareHouseString = new Gson().toJson(currentWareHouse);
                Intent intent = new Intent(getApplicationContext(), WareHouseUpdateActivity.class);
                intent.putExtra("warehouse", wareHouseString);
                startActivity(intent);
            }
        });
        backImageView = (ImageView) findViewById(R.id.cart_management_back_imageView);
        backImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        plusImageView = (ImageView) findViewById(R.id.warehouse_add_imageview);
        plusImageView.setVisibility(View.VISIBLE);
        plusImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), WareHouseAddActivity.class);
                startActivity(intent);
            }
        });
    }

    public static void setObserverChangeShop(ObserverChangeShop observerChangeShop1) {
        observerChangeShop = observerChangeShop1;
    }

    private void getDataFromServer() {
        webClient.httpListWareHouse(wareHouseListRequest, new NativeJsonResponseListener<JSONObject>() {
            @Override
            public void listener(JSONObject jsonObject) {
                int defaultWareHouseId = -1;
                try {
                    defaultWareHouseId = jsonObject.getInt("default_warehouse_id");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                String defaultWareHouseName = "";
                try {
                    defaultWareHouseName = jsonObject.getString("default_warehouse_name");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                JSONArray jsonArray = jsonObject.optJSONArray("warehouses");
                Gson gson = new Gson();
                allWareHouseList = new ArrayList<WareHouse>();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject1 = jsonArray.optJSONObject(i);
                    if (jsonObject1 != null) {
                        WareHouse wareHouse = gson.fromJson(jsonObject1.toString(), WareHouse.class);
                        allWareHouseList.add(wareHouse);
                    }
                }
                wareHouseManager.setDefaultWareHouseId(defaultWareHouseId);
                wareHouseManager.setDefaultWareHouseName(defaultWareHouseName);
                wareHouseManager.setWareHouse(allWareHouseList);

                wareHouseList = allWareHouseList;
                for (WareHouse wareHouse : allWareHouseList) {
                    if (wareHouse.getWareHouseId() == defaultWareHouseId) {
                        wareHouseList.remove(wareHouse);
                        break;
                    }
                }
                setDataToView();

            }

            @Override
            public void errorListener(String s) {
                LogHelper.getInstance().e(TAG, s);
            }
        });
    }

    private void setDefaultWareHouseFromServer(final int wareHouseId, final String wareHouseName) {
        wareHouseDefaultRequest = new WareHouseDefaultRequest(wareHouseId, currentShopId);
        webClient.httpDefaultWareHouse(wareHouseDefaultRequest, new NativeJsonResponseListener<JSONObject>() {
            @Override
            public void listener(JSONObject jsonObject) {
                LogHelper.e(TAG, "默认仓库设置成功");
                Toast.makeText(getApplicationContext(), "默认仓库设置成功", Toast.LENGTH_SHORT).show();
                wareHouseManager.setDefaultWareHouseId(wareHouseId);
                wareHouseManager.setDefaultWareHouseName(wareHouseName);
                if (observerChangeShop != null){
                    observerChangeShop.shopChange();
                }
                refresh();
            }

            @Override
            public void errorListener(String s) {
                LogHelper.getInstance().e(TAG, s);
            }
        });
    }

    private ObserverDefaultWareHouse observerDefaultWareHouse = new ObserverDefaultWareHouse() {
        @Override
        public void defaultWareHouse(int wareHouseId, String wareHouseName) {
            setDefaultWareHouseFromServer(wareHouseId, wareHouseName);
        }
    };

    private void refresh() {
        getDataFromServer();
    }

    private void setDataToView() {
        defaultWareHouseNameTextView.setText(wareHouseManager.getDefaultWareHouseName());
        defaultWareHouseSettingTextView.setText(R.string.warehouse_default);
        wareHouseManagerAdaper.setArrayList(wareHouseList);
        wareHouseManagerAdaper.notifyDataSetChanged();
    }
}
