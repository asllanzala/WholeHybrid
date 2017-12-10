package com.honeywell.wholesale.ui.menu.setting.warehouse;

import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.honeywell.hybridapp.framework.log.LogHelper;
import com.honeywell.wholesale.R;
import com.honeywell.wholesale.framework.http.NativeJsonResponseListener;
import com.honeywell.wholesale.framework.http.WebClient;
import com.honeywell.wholesale.ui.base.BaseActivity;
import com.honeywell.wholesale.ui.menu.setting.warehouse.request.WareHouseDeleteRequest;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by H154326 on 17/4/28.
 * Email: yang.liu6@honeywell.com
 */

public class WareHouseDelete extends BaseActivity {
    private static final String TAG = WareHouseDelete.class.getSimpleName();

    private ImageView closeImageView;

    private Button saveButton;

    private GradientDrawable drawable;

    private WebClient webClient;

    private WareHouseDeleteRequest wareHouseDeleteRequest;

    private int warehouseId;

    private static ObserverDeleteWareHouse observerDeleteWareHouse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_warehouse_delete);
        initData();
        initView();
    }

    private void initData() {
        webClient = new WebClient();
        Intent intent = new Intent();
        intent = getIntent();
        warehouseId = intent.getIntExtra("warehouseId", -1);
        wareHouseDeleteRequest = new WareHouseDeleteRequest(warehouseId);
    }

    /**
     * 初始化View
     */
    private void initView() {
        drawable = new GradientDrawable();

        closeImageView = (ImageView) findViewById(R.id.warehouse_delete_close_iamgeview);
        closeImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        saveButton = (Button) findViewById(R.id.warehouse_delete_save_button);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDataFromServer();
            }
        });
        setAddOrderButtonEnable();
    }

    private void getDataFromServer() {
        webClient.httpDeleteWareHouse(wareHouseDeleteRequest, new NativeJsonResponseListener<JSONObject>() {
            @Override
            public void listener(JSONObject jsonObject) {
                LogHelper.e(TAG, "成功删除");
                observerDeleteWareHouse.deleteWareHouse();
                finish();
                Toast.makeText(getApplicationContext(), "仓库删除成功", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void errorListener(String s) {
                LogHelper.getInstance().e(TAG, s);
                if (s.equals("4054")){
                    Toast.makeText(getApplicationContext(), "当前删除的是默认仓库，请为对应店铺设置新的默认仓库后再删除", Toast.LENGTH_SHORT).show();
                }
                if (s.equals("4053")){
                    Toast.makeText(getApplicationContext(), "已启用的仓库不可删除", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public static void setObserverDeleteWareHouse(ObserverDeleteWareHouse observerDeleteWareHouse1){
        observerDeleteWareHouse = observerDeleteWareHouse1;
    }

    public void setAddOrderButtonEnable() {
        drawable.setShape(GradientDrawable.RECTANGLE); // 画框
        drawable.setStroke(1, getResources().getColor(R.color.warehouse_delete_button_color)); // 边框粗细及颜色
        drawable.setColor(getResources().getColor(R.color.warehouse_delete_button_color)); // 边框内部颜色
        saveButton.setEnabled(true);
        saveButton.setBackground(drawable);
        saveButton.setTextColor(getResources().getColor(R.color.white));
    }
}
