package com.honeywell.wholesale.ui.menu.supplier;

import com.google.gson.Gson;

import com.honeywell.hybridapp.framework.IHybridCallback;
import com.honeywell.hybridapp.framework.event.Event;
import com.honeywell.hybridapp.framework.event.EventHandler;
import com.honeywell.hybridapp.framework.event.JsCallNativeEventResponseData;
import com.honeywell.hybridapp.framework.view.HybridAppWebView;
import com.honeywell.wholesale.R;
import com.honeywell.wholesale.framework.application.AppInitManager;
import com.honeywell.wholesale.framework.database.SupplierDAO;
import com.honeywell.wholesale.framework.http.IWebApiCallback;
import com.honeywell.wholesale.framework.http.WebClient;
import com.honeywell.wholesale.framework.hybrid.H5PageURL;
import com.honeywell.wholesale.framework.hybrid.HybridEventHandlerUtil;
import com.honeywell.wholesale.framework.hybrid.JsCallNativeEventData;
import com.honeywell.wholesale.framework.hybrid.JsCallNativeEventName;
import com.honeywell.wholesale.framework.model.Account;
import com.honeywell.wholesale.framework.model.AccountManager;
import com.honeywell.wholesale.lib.util.StringUtil;
import com.honeywell.wholesale.ui.base.BaseWebViewActivity;
import com.honeywell.wholesale.ui.menu.customer.CustomerDetailActivity;
import com.honeywell.wholesale.ui.uimanager.UIManager;

import android.os.Bundle;
import android.util.Log;

/**
 * Created by e887272 on 7/18/16.
 */
public class SupplierManagementActivity extends BaseWebViewActivity {

    public static final String TAG = CustomerDetailActivity.class.getSimpleName();

    private HybridAppWebView mWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_common_webview);

        mWebView = (HybridAppWebView) findViewById(R.id.hybrid_app_webView);
        initWebView();
        reloadData();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void initWebView() {
        HybridEventHandlerUtil.getInstance().registerDefaultHandler(mWebView);
        HybridEventHandlerUtil.getInstance().registerWebApiHandler(mWebView);

        mWebView.getHybridAppTunnel().getHybridEventBus().registerEventHandler(
                JsCallNativeEventName.EVENT_EXECUTE_NATIVE_METHOD,
                new EventHandler() {
                    @Override
                    public void handle(Event event, final IHybridCallback callBack) {
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
                        Account account = AccountManager.getInstance().getCurrentAccount();

                        switch (nativeMethod) {
                            case JsCallNativeEventName.NATIVE_METHOD_GET_USER_INFO:
                                JsCallNativeEventResponseData responseData = new JsCallNativeEventResponseData(
                                        JsCallNativeEventResponseData.ACK_SUCCESS,
                                        "", account.getJsonString());

                                Log.e(TAG, "执行回调：" + responseData.toString());
                                callBack.onCallBack(responseData);
                                break;

                            case JsCallNativeEventName.NATIVE_METHOD_SUPPLIER_GET_ALL:
                                // 先去网络请求后给H5再发送一份数据过去
                                IWebApiCallback webApiCallback = new IWebApiCallback() {
                                    @Override
                                    public void onSuccessCallback(Object obj) {
                                        Account account = AccountManager.getInstance().getCurrentAccount();
                                        String json = SupplierDAO.getAllSupplierWithGroup(account.getCurrentShopId());
                                        JsCallNativeEventResponseData responseData = new JsCallNativeEventResponseData(JsCallNativeEventResponseData.ACK_SUCCESS,
                                                "", json);
                                        callBack.onCallBack(responseData);
                                    }

                                    @Override
                                    public void onErrorCallback(Object obj) {
                                        JsCallNativeEventResponseData responseData = new JsCallNativeEventResponseData(JsCallNativeEventResponseData.ACK_FAILED,
                                                "获取更新信息失败", "");
                                        callBack.onCallBack(responseData);
                                    }
                                };
                                AppInitManager.getInstance().syncVendorData(new WebClient(), AccountManager.getInstance().getCurrentShopId(), webApiCallback);

//                                // 返回一份数据库里面的数据
//                                String json = SupplierDAO.getAllCustomerWithGroup(account.getCurrentShopId());
//                                responseData = new JsCallNativeEventResponseData(JsCallNativeEventResponseData.ACK_SUCCESS,
//                                        "", json);
//                                callBack.onCallBack(responseData);
                                break;

                            default:
                                Log.e(TAG, "This method " + JsCallNativeEventName
                                        .getMethodName(nativeMethod) + "is not processed!");
                                break;

                        }
                    }
                });

        mWebView.getHybridAppTunnel().getHybridEventBus()
                .registerEventHandler(
                        JsCallNativeEventName.EVENT_REQUEST_SWITCH_PAGE,
                        new EventHandler() {
                            @Override
                            public void handle(Event event, IHybridCallback callBack) {
                                if (StringUtil.isEmpty(event.getData())) {
                                    Log.w(TAG, JsCallNativeEventName.EVENT_REQUEST_SWITCH_PAGE
                                            + ", event data is null.");
                                    JsCallNativeEventResponseData responseData
                                            = new JsCallNativeEventResponseData(
                                            JsCallNativeEventResponseData.ACK_FAILED,
                                            "", JsCallNativeEventName.EVENT_REQUEST_SWITCH_PAGE
                                            + " is not executed in Java");
                                    callBack.onCallBack(responseData);
                                    return;
                                }

                                Log.i(TAG, "Receive event from H5 = " + event.getData());

                                UIManager.getInstance()
                                        .switchPage(SupplierManagementActivity.this, event, callBack);
                            }
                        });
    }

    @Override
    public HybridAppWebView getHybridAppWebView() {
        return mWebView;
    }

    private void reloadData(){
        if(mWebView != null) {
            mWebView.loadUrl(H5PageURL.URL_VENDORS_MANAGEMENT_PAGE);
        }
    }
}