package com.honeywell.wholesale.ui.menu.setting.warehouse;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import com.google.gson.Gson;
import com.honeywell.hybridapp.framework.log.LogHelper;
import com.honeywell.wholesale.R;
import com.honeywell.wholesale.framework.http.NativeJsonResponseListener;
import com.honeywell.wholesale.framework.http.WebClient;
import com.honeywell.wholesale.framework.model.AccountManager;
import com.honeywell.wholesale.framework.model.WareHouseOwnerWithFlag;
import com.honeywell.wholesale.ui.base.BaseActivity;
import com.honeywell.wholesale.ui.base.BaseTextView;
import com.honeywell.wholesale.ui.menu.setting.warehouse.adapter.WareHouseSelectOwnerAdapter;
import com.honeywell.wholesale.ui.menu.setting.warehouse.request.WareHouseQueryOwnerRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import static android.R.attr.start;

/**
 * Created by H154326 on 17/4/27.
 * Email: yang.liu6@honeywell.com
 */

public class WareHouseSelectOwnerActivity extends BaseActivity {
    private static final String TAG = WareHouseSelectOwnerActivity.class.getSimpleName();
    private static final int WAREHOUSE_OWNER_SELECT_SUCCEED = 2001;

    private ArrayList<WareHouseOwnerWithFlag> wareHouseOwnerWithFlagList;

    private ListView listView;

    private BaseTextView titleTextView;

    private ImageView backImageView;

    private WebClient webClient;
    private WareHouseQueryOwnerRequest wareHouseQueryOwnerRequest;

    private int employeeId;

    private String employeeName;
    private String companyAccount = AccountManager.getInstance().getCurrentAccount().getCompanyAccount();

    private WareHouseSelectOwnerAdapter wareHouseSelectOwnerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_warehouse_select_owner);

        initData();
        initView();
        getOwnerDataFromServer();
    }

    private void initData(){
        Intent intent = new Intent();
        intent = getIntent();
        Bundle bundle = intent.getExtras();
        employeeId = bundle.getInt("employeeId");
        employeeName = bundle.getString("employeeName");
//        employeeId = intent.getIntExtra("employeeId", -1);
//        employeeName = intent.getStringExtra("employeeName");
        wareHouseOwnerWithFlagList = new ArrayList<>();
        wareHouseSelectOwnerAdapter = new WareHouseSelectOwnerAdapter(getApplicationContext());

        webClient = new WebClient();
        wareHouseQueryOwnerRequest = new WareHouseQueryOwnerRequest(companyAccount);
    }
    private void initView(){
        titleTextView  = (BaseTextView) findViewById(R.id.cart_management_title_textview);
        titleTextView.setText("选择负责人");
        backImageView = (ImageView) findViewById(R.id.cart_management_back_imageView);
        backImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        listView = (ListView) findViewById(R.id.warehouse_manager_select_owner_listview);
        listView.setAdapter(wareHouseSelectOwnerAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                employeeId = wareHouseOwnerWithFlagList.get(i).getEmployeeId();
                employeeName = wareHouseOwnerWithFlagList.get(i).getEmployeeName();
                Intent intent = new Intent();
                intent.putExtra("employeeId", employeeId);
                intent.putExtra("employeeName", employeeName);
                setResult(WAREHOUSE_OWNER_SELECT_SUCCEED, intent);
                finish();
            }
        });
    }

    private void getOwnerDataFromServer(){
        webClient.httpQueryOwnerWareHouse(wareHouseQueryOwnerRequest, new NativeJsonResponseListener<JSONObject>(){
            @Override
            public void listener(JSONObject jsonObject) {
                JSONArray jsonArray = jsonObject.optJSONArray("userList");
                Gson gson = new Gson();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject1 = jsonArray.optJSONObject(i);
                    if (jsonObject1 != null) {
                        WareHouseOwnerWithFlag wareHouseOwnerWithFlag = gson.fromJson(jsonObject1.toString(), WareHouseOwnerWithFlag.class);
                        if (wareHouseOwnerWithFlag.getEmployeeId() == employeeId){
                            wareHouseOwnerWithFlag.setOwner(true);
                        }
                        wareHouseOwnerWithFlagList.add(wareHouseOwnerWithFlag);
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

    private void setDataToView(){
        wareHouseSelectOwnerAdapter.setArrayList(wareHouseOwnerWithFlagList);
        wareHouseSelectOwnerAdapter.notifyDataSetChanged();
    }
}
