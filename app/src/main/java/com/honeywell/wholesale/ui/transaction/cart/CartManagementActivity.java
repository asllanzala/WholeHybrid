package com.honeywell.wholesale.ui.transaction.cart;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
import com.google.gson.Gson;
import com.honeywell.wholesale.R;
import com.honeywell.wholesale.framework.application.WholesaleApplication;
import com.honeywell.wholesale.framework.database.CartDAO;
import com.honeywell.wholesale.framework.event.PayRIghtNowEvent;
import com.honeywell.wholesale.framework.http.WebClient;
import com.honeywell.wholesale.framework.model.AccountManager;
import com.honeywell.wholesale.framework.model.CartItem;
import com.honeywell.wholesale.framework.search.SearchResultItem;
import com.honeywell.wholesale.ui.base.BaseActivity;
import com.honeywell.wholesale.ui.base.BaseTextView;
import com.honeywell.wholesale.ui.inventory.ProductShipmentActivity;
import com.honeywell.wholesale.ui.transaction.cart.adapter.CartManagementAdapter;
import com.honeywell.wholesale.ui.transaction.cart.adapter.CartMoreAdapter;

import com.honeywell.wholesale.ui.search.BaseSearchActivity;

import com.honeywell.wholesale.ui.order.OrderConfirmActivity;


import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

import de.greenrobot.event.EventBus;

/**
 * Created by H154326 on 16/11/30.
 * Email: yang.liu6@honeywell.com
 */

public class CartManagementActivity extends BaseActivity {

    private static final String TAG = "CartManagementActivity";
    public static final String INTENT_VALUE_CART_CUSTOMER_ID = "INTENT_VALUE_CART_CUSTOMER_ID";
    public static final String INTENT_VALUE_CART_CUSTOMER_NAME = "INTENT_VALUE_CART_CUSTOMER_NAME";
    public static final String INTENT_KEY_ALL_PRODUCTS = "INTENT_KEY_ALL_PRODUCTS";

    private ListView cartListView;

    private ImageView allSelectImageView;
    private ImageView backImageView;

    private BaseTextView totalPriceTextView;
    private BaseTextView moreTextView;
    private BaseTextView cartManagementTitleTextView;

    private Button addOrderButton;


    private RelativeLayout allSelecteLayout;
    private LinearLayout addInventoryLayout;

    private ArrayList<CartItem> mCartItemList;
    private ArrayList<CartItem> mCartJudgeNewItemList;
    private ArrayList<String> mCartMoreList;

    private CartManagementAdapter mCartAdapter;
    private CartMoreAdapter mCartMoreAdapter;

    private PopupWindow cartMorePopupWindow;

    private ImageLoader imageLoader;

    private GradientDrawable drawable;

    private Float totalPrice = 0F;

    private String titleName;
    private String currentCustomerID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        setContentView(R.layout.activity_cart_management);
    }

    @Override
    protected void onResume() {
        super.onResume();
        initCartData();
        initView();
        initCartItemAdapter();
    }



    /**
     * 初始化View
     */
    private void initView() {
        drawable = new GradientDrawable();
        cartListView = (ListView) findViewById(R.id.cart_management_info_listView);

        allSelectImageView = (ImageView) findViewById(R.id.cart_management_all_select_imageView);

        allSelecteLayout = (RelativeLayout) findViewById(R.id.cart_management_all_select_sublayout);

        if (mCartItemList.size() == 0) {
            allSelecteLayout.setEnabled(false);
        } else {
            allSelecteLayout.setEnabled(true);
        }
        allSelecteLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                allSelecteLayout.setEnabled(true);
                if (cartListView.getCheckedItemCount() == mCartAdapter.getCount()) {
                    unSelectedAll();
                } else {
                    selectedAll();
                }
                totalPriceTextView.setText("总 价 ¥ " + String.valueOf(totalPrice));
                mCartAdapter.notifyDataSetChanged();
            }
        });

        totalPriceTextView = (BaseTextView) findViewById(R.id.cart_management_total_price_textview);
        totalPriceTextView.setText("总 价 ¥ " + String.valueOf(totalPrice));

        addOrderButton = (Button) findViewById(R.id.cart_management_add_button);
        addOrderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<CartItem> cartItems = getSelectedItem();
                String json = new Gson().toJson(cartItems);
                Intent intent = new Intent(CartManagementActivity.this, OrderConfirmActivity.class);
                intent.putExtra(OrderConfirmActivity.INTENT_KEY_SELECTED_PRODUCTS, json);
                startActivity(intent);
                finish();
            }
        });

        backImageView = (ImageView) findViewById(R.id.cart_management_back_imageView);
        backImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        moreTextView = (BaseTextView) findViewById(R.id.cart_management_more_textview);
        moreTextView.setText(getResources().getText(R.string.cart_management_more_text));
        moreTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initCartMorePopupWindow();
                int left = Math.round(v.getX()) + v.getWidth() / 2 - 160 - 115;
                int top = v.getTop();
                cartMorePopupWindow.showAsDropDown(v, left, top);

            }
        });

        cartManagementTitleTextView = (BaseTextView) findViewById(R.id.cart_management_title_textview);
        cartManagementTitleTextView.setText(titleName);

        addInventoryLayout = (LinearLayout) findViewById(R.id.trasnsaction_cart_headerview);
        addInventoryLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CartManagementActivity.this, BaseSearchActivity.class);
                Bundle mBundle = new Bundle();
                mBundle.putSerializable(BaseSearchActivity.SEARCH_KEY, SearchResultItem.ResultType.INVENTORY);
                intent.putExtras(mBundle);
                intent.putExtra(CartManagementActivity.INTENT_VALUE_CART_CUSTOMER_ID, currentCustomerID);
                intent.putExtra(CartManagementActivity.INTENT_VALUE_CART_CUSTOMER_NAME, titleName);
                startActivity(intent);
            }
        });

        unSelectedAll();
    }

    /**
     * 初始化 更多 popupWindow
     */
    private void initCartMorePopupWindow() {
        final View cartMorePopupModelWindowView = LayoutInflater.from(this).inflate(R.layout.popup_cart_management_more_select, null, false);
        final ListView cartMorePopupListView = (ListView) cartMorePopupModelWindowView.findViewById(R.id.cart_management_more_listView);

        mCartMoreAdapter = new CartMoreAdapter(this, mCartMoreList);
        cartMorePopupListView.setAdapter(mCartMoreAdapter);

        cartMorePopupWindow = new PopupWindow(cartMorePopupModelWindowView, ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, true);

        cartMorePopupListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                cartMorePopupWindow.dismiss();
                if (position == 0) {
                    deleteCartOrder();
                    finish();
                } else if (position == 1) {
                    deleteCartSelectedInventory();
                    onResume();
                }
            }
        });

        cartMorePopupWindow.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.transaction_shop_headview_divider_line_bg_color)));
        cartMorePopupModelWindowView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (cartMorePopupModelWindowView != null && cartMorePopupModelWindowView.isShown()) {
                    cartMorePopupWindow.dismiss();
                    cartMorePopupWindow = null;
                }
                return false;
            }
        });
    }

    /**
     * 初始化cart数据
     */
    private void initCartData() {
        Intent intent = getIntent();
        imageLoader = WebClient.getImageLoader();
        currentCustomerID = intent.getStringExtra(INTENT_VALUE_CART_CUSTOMER_ID);
        String shopId = AccountManager.getInstance().getCurrentAccount().getCurrentShopId();
        String employeeId = AccountManager.getInstance().getCurrentAccount().getEmployeeId();

        WholesaleApplication.setCurrentCustmerId(currentCustomerID);
        int count = CartDAO.getCartItemsCount(shopId, currentCustomerID, employeeId);

        mCartJudgeNewItemList = CartDAO.getAllCartItemsByCustomer(employeeId, shopId, currentCustomerID);

        titleName = mCartJudgeNewItemList.get(0).getCustomerName();

        if ((count == 1) && (mCartJudgeNewItemList.get(0).getProductId() == 0)) {
            mCartItemList = new ArrayList<>();
        } else {
            mCartItemList = mCartJudgeNewItemList;
        }
        mCartMoreList = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.cart_more_list)));
    }

    /**
     * 初始化CartItem的adapter
     */
    private void initCartItemAdapter() {
        mCartAdapter = new CartManagementAdapter(this, mCartItemList, imageLoader);
        cartListView.setAdapter(mCartAdapter);
        cartListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mCartAdapter.notifyDataSetChanged();
                if (cartListView.isItemChecked(position)) {
                    totalPrice += Float.parseFloat(mCartItemList.get(position).getUnitPrice()) *
                            Float.parseFloat(mCartItemList.get(position).getTotalNumber());
                } else {
                    totalPrice -= Float.parseFloat(mCartItemList.get(position).getUnitPrice()) *
                            Float.parseFloat(mCartItemList.get(position).getTotalNumber());
                }

                totalPriceTextView.setText("总 价 ¥ " + String.valueOf(totalPrice));

                if (cartListView.getCheckedItemCount() == mCartAdapter.getCount()) {
                    allSelectImageView.setSelected(true);
                    setAddOrderButtonEnable();
                } else {
                    if (cartListView.getCheckedItemCount() != 0) {
                        setAddOrderButtonEnable();
                    } else {
                        setAddOrderButtonUnEnable();
                    }
                    allSelectImageView.setSelected(false);
                }
            }
        });
    }

    /**
     * 删除订单
     */
    private void deleteCartOrder() {
        for (CartItem cartItem : mCartJudgeNewItemList) {
            CartDAO.removeCart(cartItem.getEmployeeId(), cartItem.getCustomerId());
        }
    }

    /**
     * 删除选中的商品
     */
    private void deleteCartSelectedInventory() {
        //TODO 全选的时候删除，是删除货品还是订单？若是删除货品，那么没有订单了，该怎么处理
        ArrayList<CartItem> arrayList = getSelectedItem();

        if (allSelectImageView.isSelected()) {
            CartDAO.updateCartItem(arrayList.get(0).getEmployeeId(), arrayList.get(0).getShopId(),
                    arrayList.get(0).getCustomerId(), arrayList.get(0).getProductId());
            for (int i = 1; i < arrayList.size(); i++) {
                CartDAO.removeCartItem(arrayList.get(i).getEmployeeId(), arrayList.get(i).getCustomerId(), arrayList.get(i).getProductId());
            }
            allSelectImageView.setSelected(false);
        } else {
            for (CartItem cartItem : arrayList) {
                CartDAO.removeCartItem(cartItem.getEmployeeId(), cartItem.getCustomerId(), cartItem.getProductId());
            }
        }
        cartListView.clearChoices();
    }

    /**
     * 获取选中项
     *
     * @return 选中项的arraylist
     */
    private ArrayList<CartItem> getAllItem() {
        ArrayList<CartItem> arrayList = new ArrayList<CartItem>();
        if (mCartAdapter.getCount() != 0) {
            for (int i = 0; i < mCartAdapter.getCount(); i++) {
                arrayList.add(mCartItemList.get(i));
            }
        }
        return arrayList;
    }

    /**
     * 获取选中项
     *
     * @return 选中项的arraylist
     */
    private ArrayList<CartItem> getSelectedItem() {
        ArrayList<CartItem> arrayList = new ArrayList<CartItem>();
        for (int i = 0; i < mCartAdapter.getCount(); i++) {
            if (cartListView.isItemChecked(i)) {
                arrayList.add(mCartItemList.get(i));
            }
        }
        return arrayList;
    }

    /**
     * 全选
     */
    public void selectedAll() {
        for (int i = 0; i < mCartAdapter.getCount(); i++) {
            cartListView.setItemChecked(i, true);
            totalPrice += Float.parseFloat(mCartItemList.get(i).getUnitPrice()) *
                    Float.parseFloat(mCartItemList.get(i).getTotalNumber());
        }
        allSelectImageView.setSelected(true);
        setAddOrderButtonEnable();
    }

    /**
     * 全不选
     */
    public void unSelectedAll() {
        totalPrice = 0F;
        cartListView.clearChoices();
        allSelectImageView.setSelected(false);
        setAddOrderButtonUnEnable();
    }

    public void setAddOrderButtonEnable() {
        drawable.setShape(GradientDrawable.RECTANGLE); // 画框
        drawable.setStroke(1, Color.BLACK); // 边框粗细及颜色
        drawable.setColor(getResources().getColor(R.color.white)); // 边框内部颜色
        addOrderButton.setEnabled(true);
        addOrderButton.setBackground(drawable);
        addOrderButton.setTextColor(getResources().getColor(R.color.cart_management_add_order_button_selected_text_color));
    }

    public void setAddOrderButtonUnEnable() {
        drawable.setShape(GradientDrawable.RECTANGLE); // 画框
        drawable.setStroke(1, getResources().getColor(R.color.cart_management_add_order_button_normal_bg_color)); // 边框粗细及颜色
        drawable.setColor(getResources().getColor(R.color.cart_management_add_order_button_normal_bg_color)); // 边框内部颜色
        addOrderButton.setEnabled(false);
        addOrderButton.setBackground(drawable);
        addOrderButton.setTextColor(getResources().getColor(R.color.cart_management_add_order_button_normal_text_color));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);//反注册EventBus
        WholesaleApplication.setCurrentCustmerId(null);
    }

    public void onEventMainThread(PayRIghtNowEvent event) {
        String msg = "CartManagementActivity onEventMainThread收到了消息：" + event.getmMsg();
        Log.e(TAG, msg);
        if (event.getmMsg().equals(ProductShipmentActivity.INTENT_KEY_PAY_RIGHT_NOW_EVENT)){
            finish();
        }
    }
}
