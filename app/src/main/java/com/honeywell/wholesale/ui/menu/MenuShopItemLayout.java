package com.honeywell.wholesale.ui.menu;

import com.honeywell.wholesale.R;
import com.honeywell.wholesale.framework.model.Account;
import com.honeywell.wholesale.framework.model.AccountManager;
import com.honeywell.wholesale.framework.model.Image;
import com.honeywell.wholesale.lib.util.DensityUtil;
import com.honeywell.wholesale.ui.base.BaseTextView;
import com.honeywell.wholesale.ui.login.module.LoginWebServiceResponse;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by e887272 on 6/27/16.
 */
public class MenuShopItemLayout extends LinearLayout {

    private static final String TAG = "MenuShopItemLayout";
    private BaseTextView mOutletNameTextView;
    private BaseTextView mOutletSwitchTextView;
    private LoginWebServiceResponse.LoginShopResponse mLoginShopResponse;

    public MenuShopItemLayout(Context context) {
        super(context);
        initView(context);
    }

    public MenuShopItemLayout(Context context, AttributeSet attrs, int defStyle, LoginWebServiceResponse.LoginShopResponse loginShopResponse) {
        super(context, attrs, defStyle);
        mLoginShopResponse = loginShopResponse;
        initView(context);

    }

    public void initView(Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.menu_shop_item_layout, null);
        addView(view);

        mOutletNameTextView = (BaseTextView) view.findViewById(R.id.menu_current_shop_name);
        mOutletSwitchTextView = (BaseTextView) view.findViewById(R.id.menu_switch_shop);

        setCurrentShopName();
        Log.e(TAG,"initView-setCurrentShopName");

    }


    public void setSwitchByRole(String mUserRole){
        if (mUserRole == "店主"){
            mOutletSwitchTextView.setVisibility(VISIBLE);
        } else if (mUserRole == "店员"){
            mOutletSwitchTextView.setVisibility(GONE);
        }
    }

    /**
     * 设置选中的当前店铺
      */
    public void setCurrentShopName() {
        Log.e(TAG,"+1");
        Account account = AccountManager.getInstance().getCurrentAccount();
        Log.e(TAG,"+2");
        String shopName = account.getShopName(account.getCurrentShopId());
        Log.e(TAG,"shopname=" +shopName);
        Log.e(TAG,shopName + "+3");
        mOutletNameTextView.setText(shopName);
        Log.e(TAG,shopName + "+4");
    }

    public LoginWebServiceResponse.LoginShopResponse getShop() {
        return mLoginShopResponse;
    }


}
