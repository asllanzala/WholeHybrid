package com.honeywell.wholesale.ui.menu.setting.warehouse;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.gson.Gson;
import com.honeywell.hybridapp.framework.log.LogHelper;
import com.honeywell.wholesale.R;
import com.honeywell.wholesale.framework.http.NativeJsonResponseListener;
import com.honeywell.wholesale.framework.http.WebClient;
import com.honeywell.wholesale.framework.model.AccountManager;
import com.honeywell.wholesale.framework.model.Employee;
import com.honeywell.wholesale.framework.model.WareHouse;
import com.honeywell.wholesale.ui.base.BaseActivity;
import com.honeywell.wholesale.ui.base.BaseTextView;
import com.honeywell.wholesale.ui.menu.setting.warehouse.request.WareHouseListRequest;
import com.honeywell.wholesale.ui.menu.setting.warehouse.request.WareHouseQueryOwnerRequest;
import com.honeywell.wholesale.ui.menu.setting.warehouse.request.WareHouseUpdateReuqest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by H154326 on 17/4/18.
 * Email: yang.liu6@honeywell.com
 */

public class WareHouseUpdateActivity extends BaseActivity {
    private static final String TAG = WareHouseUpdateActivity.class.getSimpleName();

//    private static final String WAREHOUSE_OWNER_SELECT_TYPE = "WAREHOUSE_UPDATE_TO_OWNER_SELECT";

    private static final int WAREHOUSE_OWNER_SELECT_TYPE = 1001;
    private static final int WAREHOUSE_OWNER_SELECT_SUCCEED = 2001;

    private RelativeLayout ownerLayout;

    private BaseTextView ownerTextView;
    private BaseTextView titleTextView;

    private EditText nameEditText;
    private EditText locationEditText;
    private EditText remarksEditText;

    private ImageView backImageView;
    private ImageView deleteImageView;

    private Button saveButton;

    private View lineView;

    private WareHouse wareHouse;

    private WebClient webClient;

    private WareHouseUpdateReuqest wareHouseUpdateReuqest;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_warehouse_edit);

        initData();
        initView();
        setDataToView();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if ((requestCode == WAREHOUSE_OWNER_SELECT_TYPE) && (resultCode == WAREHOUSE_OWNER_SELECT_SUCCEED)) {
            int employeeId = data.getExtras().getInt("employeeId", -1);
            String employeeName = data.getExtras().getString("employeeName");
            wareHouse.setWareHouseOwner(employeeId, employeeName);
        }
        setDataToView();
    }

    private void initData() {
        Intent intent = getIntent();
        webClient = new WebClient();
        wareHouse = new WareHouse();
        wareHouseUpdateReuqest = new WareHouseUpdateReuqest();
        Gson gson = new Gson();
        if (intent != null) {
            String wareHouseString = intent.getStringExtra("warehouse");
            try {
                JSONObject jsonObject = new JSONObject(wareHouseString);
                wareHouse = gson.fromJson(jsonObject.toString(), WareHouse.class);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        WareHouseDelete.setObserverDeleteWareHouse(observerDeleteWareHouse);
    }

    private void initView() {
        ownerLayout = (RelativeLayout) findViewById(R.id.warehouse_edit_owner_layout);
        ownerLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String employeeName = wareHouse.getWareHouseOwnerName();
                int employeeId = wareHouse.getWareHouseOwnerId();
                Intent intent = new Intent(getApplicationContext(), WareHouseSelectOwnerActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("employeeName", employeeName);
                bundle.putInt("employeeId", employeeId);
                intent.putExtras(bundle);
                startActivityForResult(intent, WAREHOUSE_OWNER_SELECT_TYPE);
            }
        });
        lineView = (View) findViewById(R.id.error_change_color_line);
        lineView.setBackgroundColor(getResources().getColor(R.color.bg_gray));
        nameEditText = (EditText) findViewById(R.id.warehouse_edit_name_edit_text);
//        nameEditText.setTextColor(getResources().getColor(R.color.warehouse_add_text_color));
        nameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!wareHouse.getWareHouseName().equals(editable.toString())){
                    lineView.setBackgroundColor(getResources().getColor(R.color.bg_gray));
                }
                wareHouse.setWareHouseName(editable.toString());
            }
        });
        locationEditText = (EditText) findViewById(R.id.warehouse_edit_address_edit_text);
        locationEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                wareHouse.setLocation(editable.toString());
            }
        });
        remarksEditText = (EditText) findViewById(R.id.warehouse_edit_comment_edit_text);
        remarksEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                wareHouse.setRemarks(editable.toString());
            }
        });

        ownerTextView = (BaseTextView) findViewById(R.id.warehouse_edit_owner_right_text_view);
        titleTextView = (BaseTextView) findViewById(R.id.cart_management_title_textview);
        titleTextView.setText("编辑仓库");
        backImageView = (ImageView) findViewById(R.id.cart_management_back_imageView);
        backImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        deleteImageView = (ImageView) findViewById(R.id.warehouse_delete_imageview);
        deleteImageView.setVisibility(View.VISIBLE);
        deleteImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), WareHouseDelete.class);
                int warehouseId = wareHouse.getWareHouseId();
                intent.putExtra("warehouseId", warehouseId);
                startActivity(intent);
            }
        });

        saveButton = (Button) findViewById(R.id.warehouse_edit_submit_button);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (nameEditText.getText().toString().equals("")){
                    setErrorToView();
                    Toast.makeText(getApplicationContext(), "请输入仓库名", Toast.LENGTH_SHORT).show();
                }else {
                    getDataFromServer();
                }
            }
        });
    }

    private ObserverDeleteWareHouse observerDeleteWareHouse = new ObserverDeleteWareHouse() {
        @Override
        public void deleteWareHouse() {
            finish();
        }
    };

    private void getDataFromServer() {
        wareHouseUpdateReuqest = new WareHouseUpdateReuqest(wareHouse);
        webClient.httpUpdateWareHouse(wareHouseUpdateReuqest, new NativeJsonResponseListener<JSONObject>() {
            @Override
            public void listener(JSONObject jsonObject) {
                if (jsonObject != null) {
                    LogHelper.e(TAG, "成功修改");
                    finish();
                    Toast.makeText(getApplicationContext(), "仓库修改成功", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void errorListener(String s) {
                setErrorToView();
                Toast.makeText(getApplicationContext(), "仓库名称重复", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setErrorToView(){
        lineView.setBackgroundColor(getResources().getColor(R.color.warehouse_add_text_star_color));
//        nameEditText.setTextColor(getResources().getColor(R.color.warehouse_add_text_star_color));
    }

    private void setDataToView() {
        ownerTextView.setText(wareHouse.getWareHouseOwnerName());
        nameEditText.setText(wareHouse.getWareHouseName());
        locationEditText.setText(wareHouse.getLocation());
        remarksEditText.setText(wareHouse.getRemarks());
    }


}
