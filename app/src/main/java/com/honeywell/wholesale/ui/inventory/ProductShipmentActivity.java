package com.honeywell.wholesale.ui.inventory;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.gson.Gson;
import com.honeywell.hybridapp.framework.ICallBack;
import com.honeywell.hybridapp.framework.IHybridCallback;
import com.honeywell.hybridapp.framework.event.Event;
import com.honeywell.hybridapp.framework.event.EventHandler;
import com.honeywell.hybridapp.framework.event.HybridEventBus;
import com.honeywell.hybridapp.framework.event.JsCallNativeEventResponseData;
import com.honeywell.hybridapp.framework.log.LogHelper;
import com.honeywell.wholesale.framework.database.CartDAO;
import com.honeywell.wholesale.framework.event.PayRIghtNowEvent;
import com.honeywell.wholesale.framework.hybrid.H5PageURL;
import com.honeywell.wholesale.framework.hybrid.HybridEventHandlerUtil;
import com.honeywell.wholesale.framework.hybrid.JsCallNativeEventData;
import com.honeywell.wholesale.framework.hybrid.JsCallNativeEventName;
import com.honeywell.wholesale.framework.hybrid.NativeCallJsEventName;
import com.honeywell.wholesale.framework.model.AccountManager;
import com.honeywell.wholesale.framework.model.CartItem;
import com.honeywell.wholesale.framework.model.CartManager;
import com.honeywell.wholesale.lib.util.StringUtil;
import com.honeywell.wholesale.ui.base.CommonWebViewActivity;
import com.honeywell.wholesale.ui.menu.customer.CustomerDetailActivity;
import com.honeywell.wholesale.ui.uimanager.UIManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import de.greenrobot.event.EventBus;

import static com.honeywell.wholesale.framework.hybrid.JsCallNativeEventName.NATIVE_PAGE_DASHBOARD_PAY_TRANSACTION;

/**
 * Created by xiaofei on 12/6/16.
 */

public class ProductShipmentActivity extends CommonWebViewActivity {
    public static final String TAG = ProductShipmentActivity.class.getSimpleName();

    public static final String INTENT_KEY_CART_RESULT = "INTENT_KEY_SCART_RESULT";
    public static final String INTENT_KEY_PAY_RIGHT_NOW_EVENT = "PayRIghtNowEvent is coming";
    private static final String INTENT_VALUE_INVENTORY = "INTENT_VALUE_INVENTORY";
    private static final String INTENT_VALUE_CARTITEM = "INTENT_VALUE_CARTITEM";
    public static final String INTENT_KEY_ALL_PRODUCTS = "INTENT_KEY_ALL_PRODUCTS";
    private static final String INTENT_VALUE_CART_CUSTOMER_ID = "INTENT_VALUE_CART_CUSTOMER_ID";

    private String currentCustomerId = "";
    private String allCartItemString = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        if (intent != null) {
            String cartItemJson = intent.getStringExtra(INTENT_VALUE_CARTITEM);
            currentCustomerId = intent.getStringExtra(INTENT_VALUE_CART_CUSTOMER_ID);
            String shopId = AccountManager.getInstance().getCurrentAccount().getCurrentShopId();
            String employeeId = AccountManager.getInstance().getCurrentAccount().getEmployeeId();
            ArrayList<CartItem> allCartItemList = CartDAO.getAllCartItemsByCustomer(employeeId,shopId,currentCustomerId);
            int count = CartDAO.getCartItemsCount(shopId, currentCustomerId, employeeId);
            if ((count == 1) && (allCartItemList.get(0).getProductId() == 0)) {
                allCartItemList = new ArrayList<>();
            }
            if (allCartItemList != null){
                allCartItemString = new Gson().toJson(allCartItemList);
                } else {
                allCartItemString ="";
                }

            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject(cartItemJson);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            JSONArray jsonArray = null;

            try {
                jsonArray = new JSONArray(allCartItemString);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if (jsonObject != null) {
                jsonArray.put(jsonObject);
            }

            if (jsonArray != null) {
                Log.e(TAG,jsonArray.toString());
                sendDataToWebview(jsonArray.toString());
            }
        }
    }

    @Override
    public String getUrl() {
        return H5PageURL.URL_PRODUCT_SHIPMENT;
    }



    protected void sendDataToWebview(String jsonData) {
        if (getWebView() != null) {
            getWebView().getHybridAppTunnel()
                    .callJS(NativeCallJsEventName.NATIVE_CALL_JS_EVENT_TRANSFER_DATA, jsonData,
                            new ICallBack() {
                                @Override
                                public void onCallBack(String data) {
                                    Log.v(TAG,
                                            NativeCallJsEventName.NATIVE_CALL_JS_EVENT_TRANSFER_DATA
                                                    + " CallBack, data=" + data);
                                }
                            });
        }
    }

    @Override
    public void onBackPressed() {
        EventBus.getDefault().post(new PayRIghtNowEvent(INTENT_KEY_PAY_RIGHT_NOW_EVENT));
        finish();

    }

    @Override
    protected void initWebView() {
        super.initWebView();

        getWebView().getHybridAppTunnel().getHybridEventBus().registerEventHandler(
                JsCallNativeEventName.EVENT_REQUEST_SWITCH_PAGE,
                new EventHandler() {
                    @Override
                    public void handle(Event event, IHybridCallback callBack) {
                        String data =  event.getData();
                        JsCallNativeEventData jsCallNativeEventData = new Gson()
                                .fromJson(data, JsCallNativeEventData.class);

                        String nativeMethod = jsCallNativeEventData.getNativeMethod();

                        if(JsCallNativeEventName.NATIVE_PAGE_DASHBOARD_PAY_TRANSACTION.equals(nativeMethod)){
                            Log.e(TAG,"NATIVE_PAGE_DASHBOARD_PAY_TRANSACTION");
                            EventBus.getDefault().post(new PayRIghtNowEvent(INTENT_KEY_PAY_RIGHT_NOW_EVENT));
                            finish();
                        }

                        if(JsCallNativeEventName.NATIVE_PAGE_DASHBOARD_HOME.equals(nativeMethod)) {
//                            EventBus.getDefault().post(new PayRIghtNowEvent(INTENT_KEY_PAY_RIGHT_NOW_EVENT));
//                            finish();
                            Log.e(TAG,"NATIVE_PAGE_DASHBOARD_HOME");
                            finish();
//                            Intent intent = new Intent(getApplicationContext(), DashboardActivity.class);
//                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                            startActivity(intent);
                        }
                    }
                });

        getWebView().getHybridAppTunnel().getHybridEventBus().registerEventHandler(
                JsCallNativeEventName.EVENT_EXECUTE_NATIVE_METHOD,
                new EventHandler() {
                    @Override
                    public void handle(Event event, IHybridCallback callBack) {
                        if (StringUtil.isEmpty(event.getData())) {
                            Log.w(TAG, JsCallNativeEventName.EVENT_EXECUTE_NATIVE_METHOD
                                    + ", event data is null.");
                            JsCallNativeEventResponseData responseData
                                    = new JsCallNativeEventResponseData(
                                    JsCallNativeEventResponseData.ACK_SUCCESS,
                                    "", JsCallNativeEventName.EVENT_EXECUTE_NATIVE_METHOD
                                    + " is executed in Java");
                            callBack.onCallBack(responseData);
                        }

                        Log.i(TAG, "Receive event from H5 = " + event.getData());

                        JsCallNativeEventData jsCallNativeEventData = new Gson()
                                .fromJson(event.getData(), JsCallNativeEventData.class);

                        int nativeMethod = Integer
                                .parseInt(jsCallNativeEventData.getNativeMethod());
                        JsCallNativeEventResponseData responseData = null;

                        switch (nativeMethod) {
                            case JsCallNativeEventName.NATIVE_METHOD_CART_ADD_TO:
                                // Parse json data
                                JSONObject requestBodyJsonObject = HybridEventHandlerUtil.getInstance().parseRequestJson(jsCallNativeEventData.getData(), callBack);
                                if (requestBodyJsonObject == null) {
                                    return;
                                }

                                String oneCartItem = requestBodyJsonObject.toString();
                                try {
                                    JSONObject oneCartItemJson = new JSONObject(oneCartItem);
                                    CartItem cartItem = new CartItem(oneCartItemJson);

                                    String mEmployeeId = cartItem.getEmployeeId();
                                    String mShopId = cartItem.getShopId();
                                    String mCustomerId = cartItem.getCustomerId();

                                    int count = CartDAO.getCartItemsCount(mShopId, mCustomerId, mEmployeeId);
                                    ArrayList<CartItem> mCartJudgeNewItemList =
                                            CartDAO.getAllCartItemsByCustomer(mEmployeeId, mShopId, mCustomerId);

                                    if ((count == 1) && (mCartJudgeNewItemList.get(0).getProductId() == 0)) {
                                        CartManager.getInstance().updateCart(cartItem);
                                    } else {
                                        CartManager.getInstance().addToCart(cartItem);
                                    }

                                    Log.d(TAG, "保存商品到购物车：cartItem=" + cartItem.toJsonString());

                                    responseData = new JsCallNativeEventResponseData(JsCallNativeEventResponseData.ACK_SUCCESS,
                                            "保存购物车数据成功", "");
                                } catch (JSONException e) {
                                    responseData = new JsCallNativeEventResponseData(JsCallNativeEventResponseData.ACK_FAILED,
                                            "保存购物车数据失败，" + e.getLocalizedMessage(), "");
                                    e.printStackTrace();
                                }

                                LogHelper.getInstance().d(TAG, "执行回调：" + responseData.toString());
                                callBack.onCallBack(responseData);
                                finish();
                                break;

                            case JsCallNativeEventName.NATIVE_METHOD_GET_USER_INFO:
                                responseData = new JsCallNativeEventResponseData(
                                        JsCallNativeEventResponseData.ACK_SUCCESS,
                                        "", AccountManager.getInstance().getCurrentAccount().getJsonString());

                                Log.e(TAG, "执行回调：" + responseData.toString());
                                callBack.onCallBack(responseData);
                                break;

                            case JsCallNativeEventName.NATIVE_METHOD_CART_REMOVE_ALL:

                                Log.e(TAG,"shipment  lee is a sha" + "bbb");
                                // Parse json data
                                JSONObject requestBodyJsonObject4 = HybridEventHandlerUtil
                                        .getInstance()
                                        .parseRequestJson(jsCallNativeEventData.getData(),
                                                callBack);
                                if (requestBodyJsonObject4 == null) {
                                    return;
                                }

                                String customerID11 = requestBodyJsonObject4.optString("customerID");
                                Log.e(TAG,requestBodyJsonObject4.toString());
                                Log.e(TAG,"customerID = " + customerID11);
                                String employeeID1 = AccountManager.getInstance().getCurrentAccount().getEmployeeId();

                                CartManager.getInstance().removeCart(employeeID1, customerID11);
                                responseData = new JsCallNativeEventResponseData(
                                        JsCallNativeEventResponseData.ACK_SUCCESS,
                                        "删除购物车全部数据成功", "");
                                LogHelper.getInstance()
                                        .d(TAG, "执行回调：" + responseData.toString());
                                callBack.onCallBack(responseData);
                                break;

                            default:
                                Log.e(TAG, "This method " + JsCallNativeEventName
                                        .getMethodName(nativeMethod) + " is not processed!");
                                break;

                        }
                    }
                });
    }
}
