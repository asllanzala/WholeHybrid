package com.honeywell.wholesale.ui.dashboard.fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.honeywell.wholesale.R;
import com.honeywell.wholesale.framework.application.AppInitManager;
import com.honeywell.wholesale.framework.application.WholesaleApplication;
import com.honeywell.wholesale.framework.http.NativeJsonResponseListener;
import com.honeywell.wholesale.framework.http.WebClient;
import com.honeywell.wholesale.framework.model.Account;
import com.honeywell.wholesale.framework.model.AccountManager;
import com.honeywell.wholesale.framework.model.CartManager;
import com.honeywell.wholesale.framework.utils.SharePreferenceUtil;
import com.honeywell.wholesale.ui.activity.MainActivity;
import com.honeywell.wholesale.ui.dashboard.CircleImageView;
import com.honeywell.wholesale.ui.eventbus.MainEvent;
import com.honeywell.wholesale.ui.menu.setting.MenuSettingActivity;
import com.honeywell.wholesale.ui.menu.customer.CustomerManagementActivity;
import com.honeywell.wholesale.ui.login.LoginActivity;
import com.honeywell.wholesale.ui.login.module.LoginWebServiceResponse;
import com.honeywell.wholesale.ui.menu.MenuShopItemLayout;
import com.honeywell.wholesale.ui.menu.OutletManagementActivity;
import com.honeywell.wholesale.ui.menu.UserAccountsActivity;
import com.honeywell.wholesale.ui.menu.shop.GetCustomerDetailApiRequest;
import com.honeywell.wholesale.ui.menu.shop.ShopManagementActivity;
import com.honeywell.wholesale.ui.menu.supplier.SupplierManagementActivity;
import com.honeywell.wholesale.ui.priceDiff.PriceDiffActivity;
import com.honeywell.wholesale.ui.saleReturn.PreSaleReturnActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;

import static android.app.Activity.RESULT_OK;

public class LeftMenuFragment extends Fragment implements OnClickListener {

    private static final String SHARE_PREFERENCE_APP_USER_PICS = "userPics";
    private static final String SHARE_PREFERENCE_APP_USER_PICS_URL = "userPicsUrl";
    private static final String SHARE_PREFERENCE_APP_USER_PICS_HD_URL = "userPicsHdUrl";
    public static final String TAG = LeftMenuFragment.class.getSimpleName();

    private static final int SHOP = 111;

    private TextView mUserNameTextView;
    private TextView mUserRoleTextView;
    private TextView mVersionTextView;
    private LinearLayout mShopsListLayout;
    private List<MenuShopItemLayout> mShopItemLayoutList;

    private LinearLayout mCustomersManagementView;
    private LinearLayout mVendorsManagementLayout;
    private LinearLayout mMyOutletsView;
    private LinearLayout mUserManagementView;
    private LinearLayout mSettingView;
    private LinearLayout mPriceDiffView;

    private View mLogoutView;

    private View mLeftMenuView;

    private View outletDividerView;
    private View vendorsDividerView;

    private String currentAccountRole;

    private CircleImageView userImageView;

    private GetCustomerDetailApiRequest getCustomerDetailApiRequest;

    private WebClient webClient;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mLeftMenuView = inflater.inflate(R.layout.layout_sliding_menu_left, null);
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        findViews(mLeftMenuView);
        return mLeftMenuView;
    }

    public void refleshView() {
        Account account = AccountManager.getInstance().getCurrentAccount();
        LoginWebServiceResponse.LoginShopResponse[] shopList = account.getShopList();

        account.getShopName(account.getCurrentShopId());
        mShopItemLayoutList = new ArrayList<>();
        Log.e(TAG, "size" + shopList.length);
        mShopsListLayout.removeAllViews();
        Log.e(TAG, "shop 1");
        if (shopList != null) {
            Log.e(TAG, "shop 2");
            MenuShopItemLayout shopItemLayout = new MenuShopItemLayout(mLeftMenuView.getContext());
            mShopsListLayout.addView(shopItemLayout);
            setUserRoleAction(shopItemLayout);
        }
        Log.e(TAG, "shop 3");
    }

    @Override
    public void onResume() {
        super.onResume();
        refleshView();
//        ((MainActivity)getActivity()).reflashCurrentPic(userImageView);
        Log.e("alinmi", "LeftMenu onResume");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.e(TAG, "onPause");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.e(TAG, "onStop");
    }

    public void findViews(View view) {

        getCustomerDetailApiRequest = new GetCustomerDetailApiRequest();
        webClient = new WebClient();

        vendorsDividerView = view.findViewById(R.id.menu_down_vendors_management_divider);
        outletDividerView = view.findViewById(R.id.menu_down_outlet_management_divider);
        mUserNameTextView = (TextView) view.findViewById(R.id.menu_user_name_textview);
        mUserRoleTextView = (TextView) view.findViewById(R.id.menu_user_role_textview);
        mShopsListLayout = (LinearLayout) view.findViewById(R.id.menu_shops_list_layout);
        mVersionTextView = (TextView) view.findViewById(R.id.menu_current_version_textview);
        mVersionTextView.setText(getCurrentVersionName());
        mSettingView = (LinearLayout) view.findViewById(R.id.menu_setting);
        mSettingView.setOnClickListener(this);
        mPriceDiffView = (LinearLayout) view.findViewById(R.id.menu_price_diff);
        mPriceDiffView.setOnClickListener(this);

        userImageView = (CircleImageView) view.findViewById(R.id.profile_image);

        mCustomersManagementView = (LinearLayout) view.findViewById(R.id.menu_customers_management);
        mVendorsManagementLayout = (LinearLayout) view.findViewById(R.id.menu_vendors_management);
        mUserManagementView = (LinearLayout) view.findViewById(R.id.menu_users_and_accounts);
        mMyOutletsView = (LinearLayout) view.findViewById(R.id.menu_outlet_management);

        mLogoutView = view.findViewById(R.id.menu_logout);

        mShopsListLayout.setOnClickListener(this);
        mCustomersManagementView.setOnClickListener(this);
        mVendorsManagementLayout.setOnClickListener(this);
        mMyOutletsView.setOnClickListener(this);
        mUserManagementView.setOnClickListener(this);

        mLogoutView.setOnClickListener(this);

        Account currentAccount = AccountManager.getInstance().getCurrentAccount();
        mUserNameTextView.setText(currentAccount.getUserName());
        if (currentAccount.getRole().equals("0")) {
            currentAccountRole = "店主";
        } else {
            currentAccountRole = "店员";
        }
        mUserRoleTextView.setText(currentAccountRole);
        updateHeadPortrait(userImageView);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);

    }

    public void onEventMainThread(MainEvent event) {

        Log.e("alinmi", "onEventMainThread收到了消息：" + event.getMessage());
        final int msg = event.getMessage();
        switch (msg) {
            case MainEvent.REFRESH_HEAD_PORTRAIT:
                updateHeadPortrait(userImageView);
                break;
            default:
                break;
        }
    }

    private void updateHeadPortrait(ImageView imageView) {
        ((MainActivity) getActivity()).refreshHeadPortrait(imageView);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.menu_shops_list_layout:
                Log.e(TAG, "switch shop layout");
                Intent shopManagementIntent = new Intent(getActivity(), ShopManagementActivity.class);
                getActivity().startActivityForResult(shopManagementIntent, SHOP);
                break;
            case R.id.menu_customers_management:
                Intent customersIntent = new Intent(getActivity(), CustomerManagementActivity.class);
                getActivity().startActivity(customersIntent);
                break;

            case R.id.menu_vendors_management:
                Intent vendorIntent = new Intent(getActivity(), SupplierManagementActivity.class);
                getActivity().startActivity(vendorIntent);
                break;

            case R.id.menu_outlet_management:
                Intent outletMgtIntent = new Intent(getActivity(), OutletManagementActivity.class);
                getActivity().startActivity(outletMgtIntent);
                break;
            case R.id.menu_users_and_accounts:
                Intent userAccountsMgtIntent = new Intent(getActivity(),
                        UserAccountsActivity.class);
                getActivity().startActivity(userAccountsMgtIntent);
                break;
//            case R.id.menu_sell_online:
//                // TODO
//                break;
//            case R.id.menu_settings:
//                // TODO
//                Intent scanResultIntent = new Intent(getActivity(), ProductDetailActivity.class);
//                scanResultIntent.setFlags(
//                        Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
//                scanResultIntent.putExtra(ProductDetailActivity.INTENT_KEY_SCAN_RESULT, "888888");
//                startActivity(scanResultIntent);
//                break;
            case R.id.menu_logout:
                CartManager.getInstance().removeAllCarts();
                AccountManager.getInstance().logout();
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
                getActivity().finish();
                break;
            case R.id.menu_setting:
                Intent intentSetting = new Intent(getActivity(), MenuSettingActivity.class);
                getActivity().startActivity(intentSetting);
                break;

            case R.id.menu_price_diff:
                Intent intentPriceDiff = new Intent(getActivity(), PriceDiffActivity.class);
                getActivity().startActivity(intentPriceDiff);
                break;

            default:
                break;
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e(TAG, "onActivityResult");

        if (requestCode == SHOP && resultCode == RESULT_OK && data != null) {
            Bundle bundle = data.getExtras();
            String shopId = bundle.getString("shopId");
            String shopName = bundle.getString("shopName");
            ShopSwith(shopId, shopName);
        }
    }

    private String getCurrentVersionName() {
        final Context context = WholesaleApplication.getAppContext();
        PackageInfo pInfo = null;
        try {
            pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (pInfo == null) {
            return "";
        }

        final String currentVersionName = pInfo.versionName;
        return currentVersionName;
    }

    /**
     * 根据用户的角色，显示不同的子TAB
     *
     * @param shopItemLayout
     */
    private void setUserRoleAction(MenuShopItemLayout shopItemLayout) {
        if (currentAccountRole == "店主") {
            mShopsListLayout.setEnabled(true);
            mCustomersManagementView.setVisibility(View.GONE);
            mVendorsManagementLayout.setVisibility(View.GONE);
            mMyOutletsView.setVisibility(View.VISIBLE);
            vendorsDividerView.setVisibility(View.GONE);
            outletDividerView.setVisibility(View.VISIBLE);
        } else if (currentAccountRole == "店员") {
            mShopsListLayout.setEnabled(false);
            mCustomersManagementView.setVisibility(View.GONE);
            mVendorsManagementLayout.setVisibility(View.GONE);
            mMyOutletsView.setVisibility(View.GONE);
            vendorsDividerView.setVisibility(View.GONE);
            outletDividerView.setVisibility(View.GONE);
        }
        shopItemLayout.setSwitchByRole(currentAccountRole);
    }

    /**
     * 切换店铺时，同步数据
     *
     * @param shopId
     * @param shopName
     */
    public void ShopSwith(String shopId, String shopName) {
        Account account = AccountManager.getInstance().getCurrentAccount();

        // 更新数据库
        account.setCurrentShopId(shopId);
        account.setShopName(shopName);
        AccountManager.getInstance().updateAccount(account);

        // 切换店铺时同步数据
        AppInitManager.getInstance().startSyncData();

        MenuShopItemLayout shopItem = new MenuShopItemLayout(getContext());
        // 设置选中的当前店铺
        shopItem.setCurrentShopName();

        ((MainActivity) getActivity()).closeLeftMenu(false);
        ((MainActivity) getActivity()).reLoadDataForCurrentFragment();
    }
}
