package com.honeywell.wholesale.ui.uimanager;

import com.google.gson.Gson;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.honeywell.hybridapp.framework.IHybridCallback;
import com.honeywell.hybridapp.framework.event.Event;
import com.honeywell.hybridapp.framework.event.JsCallNativeEventResponseData;
import com.honeywell.hybridapp.framework.log.LogHelper;
import com.honeywell.wholesale.framework.database.CartDAO;
import com.honeywell.wholesale.framework.database.CustomerDAO;
import com.honeywell.wholesale.framework.hybrid.JsCallNativeEventData;
import com.honeywell.wholesale.framework.hybrid.JsCallNativeEventName;
import com.honeywell.wholesale.framework.model.AccountManager;
import com.honeywell.wholesale.framework.model.Customer;
import com.honeywell.wholesale.framework.model.Inventory;
import com.honeywell.wholesale.framework.model.Order;
import com.honeywell.wholesale.framework.search.SearchResultItem;
import com.honeywell.wholesale.lib.util.StringUtil;
import com.honeywell.wholesale.ui.activity.MainActivity;
import com.honeywell.wholesale.ui.menu.customer.CustomerDetailActivity;
import com.honeywell.wholesale.ui.menu.setting.warehouse.WareHouseManagerActivity;
import com.honeywell.wholesale.ui.order.OrderConfirmActivity;
import com.honeywell.wholesale.ui.menu.supplier.SupplierDetailActivity;
import com.honeywell.wholesale.ui.search.BaseSearchActivity;
import com.honeywell.wholesale.ui.transaction.cart.CartManagementActivity;
import com.honeywell.wholesale.ui.transaction.preorders.PreTransactionActivity;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.honeywell.wholesale.R.id.result;
import static com.honeywell.wholesale.ui.search.SearchKey.inventory;

/**
 * Created by e887272 on 6/30/16.
 */
public class UIManager {

    private static final String TAG = UIManager.class.getSimpleName();

    private static UIManager mUIManager;

    private UIManager(){

    }

    public static synchronized UIManager getInstance() {
        if(mUIManager == null) {
            mUIManager = new UIManager();
        }
        return mUIManager;
    }

    public void switchPage(Activity activity, Event event, IHybridCallback callBack) {
        if (StringUtil.isEmpty(event.getData())) {
            Log.w(TAG, JsCallNativeEventName.EVENT_REQUEST_SWITCH_PAGE
                    + ", event data is null.");
            JsCallNativeEventResponseData responseData = new JsCallNativeEventResponseData(JsCallNativeEventResponseData.ACK_FAILED,
                    "", JsCallNativeEventName.EVENT_REQUEST_SWITCH_PAGE
                    + " is not executed in Java");
            callBack.onCallBack(responseData);
            return;
        }

        Log.i(TAG, "Receive event from H5 = " + event.getData());
        String data =  event.getData();
        JsCallNativeEventData jsCallNativeEventData = new Gson()
                .fromJson(data, JsCallNativeEventData.class);

        String nativeMethod = jsCallNativeEventData.getNativeMethod();
        String requestJsonStr = jsCallNativeEventData.getData();

        if(JsCallNativeEventName.NATIVE_PAGE_CLOSE.equals(nativeMethod)) {
            activity.finish();
        } else if(JsCallNativeEventName.NATIVE_PAGE_DASHBOARD_HOME.equals(nativeMethod)) {
            showDashboard(activity, MainActivity.INTENT_VALUE_CURRENT_FRAGMENT_HOME);
            Log.e(TAG,"NATIVE_PAGE_DASHBOARD_HOME");
            Log.e(TAG,activity.getPackageName());
        } else if (JsCallNativeEventName.NATIVE_PAGE_DASHBOARD_CART.equals(nativeMethod)) {
            showDashboard(activity, MainActivity.INTENT_VALUE_CURRENT_FRAGMENT_CART);

        }else if (JsCallNativeEventName.NATIVE_PAGE_CHECKOUT
                .equals(nativeMethod)) {
            if(!StringUtil.isEmpty(requestJsonStr)) {
                LogHelper.getInstance().w(TAG, "结算，选中的产品：" + requestJsonStr);
                Intent intent = new Intent(activity, OrderConfirmActivity.class);
                intent.putExtra(OrderConfirmActivity.INTENT_KEY_SELECTED_PRODUCTS, requestJsonStr);
                activity.startActivity(intent);
            } else {
                LogHelper.getInstance().w(TAG, "准备打开确认订单页面，但是错误：没有参数：" + requestJsonStr);
            }


        } else if (JsCallNativeEventName.NATIVE_PAGE_CUSTOMER_DETAIL
                .equals(nativeMethod)) {
            Intent intent = new Intent(activity, CustomerDetailActivity.class);
            intent.putExtra(CustomerDetailActivity.INTENT_KEY_CUSTOMER_INFO, requestJsonStr);
            activity.startActivity(intent);

        }  else if (JsCallNativeEventName.NATIVE_PAGE_CUSTOMER_SEARCH
                .equals(nativeMethod)) {
            //客户搜索
            Intent intent = new Intent(activity, BaseSearchActivity.class);
            Bundle mBundle = new Bundle();
            mBundle.putSerializable(BaseSearchActivity.SEARCH_KEY, SearchResultItem.ResultType.CUSTOMER);
            intent.putExtras(mBundle);
            activity.startActivity(intent);
        } else if (JsCallNativeEventName.NATIVE_PAGE_SUPPLIER_SEARCH
                .equals(nativeMethod)) {
            //供应商搜索
            Intent intent = new Intent(activity, BaseSearchActivity.class);
            Bundle mBundle = new Bundle();
            mBundle.putSerializable(BaseSearchActivity.SEARCH_KEY, SearchResultItem.ResultType.VENDOR);
            intent.putExtras(mBundle);
            activity.startActivity(intent);

        }else if (JsCallNativeEventName.NATIVE_PAGE_CREATE_NEW_ORDERS
                .equals(nativeMethod)){
            // 创建新订单

            requestJsonStr = StringUtil.jsJsonToJJson(requestJsonStr);

            Customer customer1 = new Gson().fromJson(requestJsonStr, Customer.class);

            String customerId = customer1.getCustomeId();


            JSONObject jsonObject = null;
            String shopId = AccountManager.getInstance().getCurrentShopId();
            String employeeId = AccountManager.getInstance().getCurrentAccount().getEmployeeId();
            int count = CartDAO.getCartItemsCount(shopId, customerId, employeeId);

//            Customer customer = CustomerDAO.queryByCustomer(customerId);
//            if (count == 0){
//                Order.createNewOrder(customer);
//            }
//            Customer customer = new Gson().fromJson(requestJsonStr, Customer.class);
            Intent intent = new Intent(activity, PreTransactionActivity.class);
            intent.putExtra(PreTransactionActivity.INTENT_VALUE_CART_CUSTOMER_ID, customerId);
            intent.putExtra(PreTransactionActivity.INTENT_TYPE, PreTransactionActivity.INTENT_CUSTOMER_TO_TRANSACTION);
            activity.startActivity(intent);

        }else if (JsCallNativeEventName.NATIVE_PAGE_WAREHOUSE_MANAGER
                .equals(nativeMethod)){
            // 仓库管理

//            requestJsonStr = StringUtil.jsJsonToJJson(requestJsonStr);

//            Customer customer1 = new Gson().fromJson(requestJsonStr, Customer.class);
//
//            String customerId = customer1.getCustomeId();
//
//
//            JSONObject jsonObject = null;
//            String shopId = AccountManager.getInstance().getCurrentShopId();
//            String employeeId = AccountManager.getInstance().getCurrentAccount().getEmployeeId();
//            int count = CartDAO.getCartItemsCount(shopId, customerId, employeeId);

//            Customer customer = CustomerDAO.queryByCustomer(customerId);
//            if (count == 0){
//                Order.createNewOrder(customer);
//            }
//            Customer customer = new Gson().fromJson(requestJsonStr, Customer.class);
            Intent intent = new Intent(activity, WareHouseManagerActivity.class);

            activity.startActivity(intent);

        } else if (JsCallNativeEventName.NATIVE_METHOD_PRODUCT_DETAIL_NEW_ORDERS
                .equals(nativeMethod)){
            // 创建新订单

            String inventoryListString = requestJsonStr;
            Intent intent = new Intent(activity, PreTransactionActivity.class);
            intent.putExtra(PreTransactionActivity.INTENT_PRODUCT_LIST, inventoryListString);
            intent.putExtra(PreTransactionActivity.INTENT_TYPE, PreTransactionActivity.INTENT_INVENTORY_TO_TRANSACTION);
            activity.startActivity(intent);

        }if (JsCallNativeEventName.NATIVE_PAGE_SUPPLIER_DETAIL
                .equals(nativeMethod)) {
            Intent intent = new Intent(activity, SupplierDetailActivity.class);
            intent.putExtra(SupplierDetailActivity.INTENT_KEY_VENDOR_INFO, requestJsonStr);
            activity.startActivity(intent);

        } else{
            LogHelper.getInstance().w(TAG, "收到无效的页面地址");
        }

    }

    private void showDashboard(Activity activity, int dashboardPage) {
//        Intent intent = new Intent();
//        intent.setClass(activity, DashboardActivity.class);
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
//        intent.putExtra(DashboardActivity.INTENT_KEY_CURRENT_FRAGMENT, dashboardPage);
//        activity.startActivity(intent);
        activity.finish();
    }

    public static JSONObject parseRequestJson (String requestJsonStr, IHybridCallback callBack) {
        JSONObject requestBodyJsonObject = null;
        if(!StringUtil.isEmpty(requestJsonStr)) {
            try {
                requestBodyJsonObject = new JSONObject(requestJsonStr);
            } catch (JSONException e) {
                e.printStackTrace();
                Log.e(TAG, "Request Json parse error! " + e.getLocalizedMessage());
            }
        } else {
            Log.d(TAG, "Request Json is null!");
        }

        if(requestBodyJsonObject == null) {
            JsCallNativeEventResponseData responseData = new JsCallNativeEventResponseData(JsCallNativeEventResponseData.ACK_FAILED,
                    "Response json is null.", "");
            callBack.onCallBack(responseData);
        }

        return requestBodyJsonObject;
    }


}
