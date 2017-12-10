package com.honeywell.wholesale.ui.menu.setting.warehouse;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.honeywell.hybridapp.framework.log.LogHelper;
import com.honeywell.wholesale.R;
import com.honeywell.wholesale.framework.http.NativeJsonResponseListener;
import com.honeywell.wholesale.framework.http.WebClient;
import com.honeywell.wholesale.framework.model.WareHouse;
import com.honeywell.wholesale.framework.utils.PointLengthFilter;
import com.honeywell.wholesale.ui.base.BaseActivity;
import com.honeywell.wholesale.ui.base.BaseTextView;
import com.honeywell.wholesale.ui.menu.setting.warehouse.request.WareHouseAddRequest;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by H154326 on 17/4/18.
 * Email: yang.liu6@honeywell.com
 */

public class WareHouseAddActivity extends BaseActivity {
    private static final String TAG = WareHouseAddActivity.class.getSimpleName();

    private static final int WAREHOUSE_OWNER_SELECT_TYPE = 1002;
    private static final int WAREHOUSE_OWNER_SELECT_SUCCEED = 2001;

    private RelativeLayout ownerLayout;

    private BaseTextView ownerTextView;
    private BaseTextView titleTextView;

    private EditText nameEditText;
    private EditText locationEditText;
    private EditText remarksEditText;

    private ImageView backImageView;

    private Button submitButton;

    private View lineView;

    private WebClient webClient;

    private WareHouseAddRequest wareHouseAddRequest;

    private WareHouse wareHouse;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_warehouse_add);
        initView();
        initData();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if ((requestCode == WAREHOUSE_OWNER_SELECT_TYPE) && (resultCode == WAREHOUSE_OWNER_SELECT_SUCCEED)) {
            if (data != null) {
                int employeeId = data.getExtras().getInt("employeeId", -1);
                String employeeName = data.getExtras().getString("employeeName");
                if (employeeName == null) {
                    employeeName = "";
                }
                wareHouse.setWareHouseOwner(employeeId, employeeName);
            }
        }
        setDataToView();
    }

    private void initData() {
        webClient = new WebClient();
        wareHouseAddRequest = new WareHouseAddRequest();
        wareHouse = new WareHouse("");
    }

    private void initView() {
        ownerLayout = (RelativeLayout) findViewById(R.id.warehouse_add_owner_layout);
        ownerLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String employeeName;
                int employeeId;
                if (ownerTextView.getText().toString().equals("")) {
                    employeeName = "";
                    employeeId = -1;
                } else {
                    employeeName = wareHouse.getWareHouseOwnerName();
                    employeeId = wareHouse.getWareHouseOwnerId();
                }
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
        nameEditText = (EditText) findViewById(R.id.warehouse_add_name_edit_text);
        nameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (wareHouse.getWareHouseName() != null) {
                    if (!wareHouse.getWareHouseName().equals(editable.toString())) {
                        lineView.setBackgroundColor(getResources().getColor(R.color.bg_gray));
                    }
                }
                wareHouse.setWareHouseName(editable.toString());
            }
        });
        locationEditText = (EditText) findViewById(R.id.warehouse_add_location_edit_text);
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
        remarksEditText = (EditText) findViewById(R.id.warehouse_add_remark_edit_text);
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
        ownerTextView = (BaseTextView) findViewById(R.id.warehouse_add_owner_right_text_view);
        titleTextView = (BaseTextView) findViewById(R.id.cart_management_title_textview);
        titleTextView.setText("添加仓库");

        backImageView = (ImageView) findViewById(R.id.cart_management_back_imageView);
        backImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        submitButton = (Button) findViewById(R.id.warehouse_add_submit_button);
        submitButton.setOnClickListener(new View.OnClickListener() {
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

    private void getDataFromServer() {
        wareHouseAddRequest = new WareHouseAddRequest(wareHouse);
        Log.e(TAG, wareHouseAddRequest.getJsonString());
        webClient.httpAddWareHouse(wareHouseAddRequest, new NativeJsonResponseListener<JSONObject>() {
            @Override
            public void listener(JSONObject jsonObject) {
                if (jsonObject != null) {
                    LogHelper.e(TAG, "成功添加");
                    finish();
                    Toast.makeText(getApplicationContext(), "仓库添加成功", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void errorListener(String s) {
                //LogHelper.getInstance().e(TAG, s);
                Log.e("121",s);
                setErrorToView();
                if (s.equals("600")){
                    Toast.makeText(getApplicationContext(), "仓库名称字数最大为30", Toast.LENGTH_SHORT).show();
                }else if (s.equals("4051")){
                    Toast.makeText(getApplicationContext(), "仓库名称重复", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void setErrorToView(){
        lineView.setBackgroundColor(getResources().getColor(R.color.warehouse_add_text_star_color));
//        nameEditText.setTextColor(getResources().getColor(R.color.warehouse_add_text_star_color));
    }

    private void setDataToView() {
        ownerTextView.setText(wareHouse.getWareHouseOwnerName());
//        nameEditText.setText(wareHouse.getWareHouseName());
//        locationEditText.setText(wareHouse.getLocation());
//        remarksEditText.setText(wareHouse.getRemarks());
    }
}
