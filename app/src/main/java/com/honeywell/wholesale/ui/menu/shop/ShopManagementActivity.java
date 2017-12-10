package com.honeywell.wholesale.ui.menu.shop;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.honeywell.wholesale.R;
import com.honeywell.wholesale.framework.http.NativeJsonResponseListener;
import com.honeywell.wholesale.framework.http.WebClient;
import com.honeywell.wholesale.framework.model.Account;
import com.honeywell.wholesale.framework.model.AccountManager;
import com.honeywell.wholesale.ui.activity.MainActivity;
import com.honeywell.wholesale.ui.base.BaseActivity;
import com.honeywell.wholesale.ui.dashboard.fragment.ObserverChangeShop;
import com.honeywell.wholesale.ui.login.module.LoginWebServiceResponse;
import com.honeywell.wholesale.ui.login.module.UpdateCategoryCloudApiRequest;
import com.honeywell.wholesale.ui.menu.shop.Adapter.ShopAdapter;
import com.honeywell.wholesale.ui.transaction.preorders.ObserverCartData;

import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;


/**
 * Created by xiaofei on 11/25/16.
 * Updated by Liu Yang on 11/27/16
 *
 */

public class ShopManagementActivity extends BaseActivity {
    private static final String TAG = "ShopManagementActivity";
    private ListView shopListView;
    private ShopAdapter shopAdapter;
    private List<LoginWebServiceResponse.LoginShopResponse> shopArrayList;
    private String currentShopName;
    private int currentPosition;
    private ImageView backImageView;

    private static ObserverChangeShop observerChangeShop;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_management);

        initView();
        initData();
        initAdapter();
        setCurrentShopName();
    }

    private void initView(){
        shopListView = (ListView) findViewById(R.id.shop_info_listView);
        shopListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        backImageView = (ImageView) findViewById(R.id.shop_management_back_imageView);
        backImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initData(){
        Account account = AccountManager.getInstance().getCurrentAccount();
        LoginWebServiceResponse.LoginShopResponse[] shopList = account.getShopList();
        currentShopName = account.getShopName(account.getCurrentShopId());
        shopArrayList = Arrays.asList(shopList);
    }

    public void setCurrentShopName() {
        for (int i = 0; i < shopArrayList.size(); i++) {
            if (shopArrayList.get(i).mShopName.equals(currentShopName)){
                shopAdapter.setPositionChecked(i);
                shopAdapter.notifyDataSetChanged();
                currentPosition = i;
                return;
            }
        }
    }

    public static void setObserverChangeShop(ObserverChangeShop observerChangeShop1) {
        observerChangeShop = observerChangeShop1;
    }

    private void initAdapter(){
        shopAdapter = new ShopAdapter(this,shopArrayList);
        shopListView.setAdapter(shopAdapter);
        shopListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (currentPosition != position){
                    final int mPosition = position;
                    LoginWebServiceResponse.LoginShopResponse shop = shopArrayList.get(position);
                    final String shopId = shop.getmShopId();
                    final String shopName = shop.getmShopName();

                    WebClient webClient = new WebClient();
                    String employeeId = AccountManager.getInstance().getCurrentAccount().getEmployeeId();
                    //TODO shopId is int type current must show dialog
                    int shopIdinterge = Integer.valueOf(shopId);


                    UpdateCategoryCloudApiRequest categoryRequest = new UpdateCategoryCloudApiRequest(employeeId, shopIdinterge);
                    webClient.httpGetCategories(categoryRequest, new NativeJsonResponseListener<JSONObject>() {
                        @Override
                        public void listener(JSONObject jsonObject) {
                            // TODO close dialog
                            shopAdapter.setPositionChecked(mPosition);
                            shopAdapter.notifyDataSetChanged();
                            AccountManager.getInstance().setCurrentShopId(shopId);
                            if (observerChangeShop != null){
                                observerChangeShop.shopChange();
                            }
                            Intent intent = new Intent(ShopManagementActivity.this, MainActivity.class);
                            intent.putExtra("shopId", shopId);
                            intent.putExtra("shopName",shopName);
                            setResult(RESULT_OK, intent);
                            finish();

                        }

                        @Override
                        public void errorListener(String s) {
                            Toast.makeText(getApplicationContext(), "网络错误", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }
}
