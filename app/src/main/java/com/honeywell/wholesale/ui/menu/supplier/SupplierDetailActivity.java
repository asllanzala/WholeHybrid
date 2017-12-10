package com.honeywell.wholesale.ui.menu.supplier;

import com.google.gson.Gson;
import com.honeywell.hybridapp.framework.IHybridCallback;
import com.honeywell.hybridapp.framework.event.Event;
import com.honeywell.hybridapp.framework.event.EventHandler;
import com.honeywell.hybridapp.framework.event.HybridEventBus;
import com.honeywell.hybridapp.framework.event.JsCallNativeEventResponseData;
import com.honeywell.hybridapp.framework.event.NativeCallJsEvent;
import com.honeywell.hybridapp.framework.view.HybridAppWebView;
import com.honeywell.wholesale.R;
import com.honeywell.wholesale.framework.hybrid.H5PageURL;
import com.honeywell.wholesale.framework.hybrid.HybridEventHandlerUtil;
import com.honeywell.wholesale.framework.hybrid.JsCallNativeEventData;
import com.honeywell.wholesale.framework.hybrid.JsCallNativeEventName;
import com.honeywell.wholesale.framework.hybrid.NativeCallJsEventName;
import com.honeywell.wholesale.framework.model.Account;
import com.honeywell.wholesale.framework.model.AccountManager;
import com.honeywell.wholesale.lib.util.StringUtil;
import com.honeywell.wholesale.ui.base.BaseWebViewActivity;
import com.honeywell.wholesale.ui.menu.customer.CustomerDetailActivity;
import com.honeywell.wholesale.ui.uimanager.UIManager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

/**
 * Created by e887272 on 6/30/16.
 */
public class SupplierDetailActivity extends BaseWebViewActivity {

    public static final String TAG = SupplierDetailActivity.class.getSimpleName();
    public static final String INTENT_KEY_VENDOR_INFO = "INTENT_KEY_VENDOR_INFO";

    private HybridAppWebView mWebView;
    private String mJsonString = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_common_webview);

        Intent intent = getIntent();
        if (intent != null) {
            mJsonString = intent.getStringExtra(INTENT_KEY_VENDOR_INFO);
        }

        mWebView = (HybridAppWebView) findViewById(R.id.hybrid_app_webView);
        initWebView();

        NativeCallJsEvent callJsEvent = new NativeCallJsEvent(NativeCallJsEventName.NATIVE_CALL_JS_EVENT_TRANSFER_DATA, mJsonString);
        mWebView.loadUrl(H5PageURL.URL_SUPPLIER_DETAIL, callJsEvent);
    }


    private void initWebView() {
        HybridEventHandlerUtil.getInstance().registerDefaultHandler(mWebView);
        HybridEventHandlerUtil.getInstance().registerWebApiHandler(mWebView);

        HybridEventBus hybridEventBus = mWebView.getHybridAppTunnel().getHybridEventBus();

        hybridEventBus.registerEventHandler(
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
                            case JsCallNativeEventName.NATIVE_METHOD_GET_USER_INFO:
                                Account account = AccountManager.getInstance().getCurrentAccount();
                                responseData = new JsCallNativeEventResponseData(
                                        JsCallNativeEventResponseData.ACK_SUCCESS,
                                        "", account.getJsonString());

                                Log.e(TAG, "执行回调：" + responseData.toString());
                                callBack.onCallBack(responseData);
                                break;

                            default:
                                Log.e(TAG, "This method " + JsCallNativeEventName
                                        .getMethodName(nativeMethod) + " is not processed!");
                                break;

                        }
                    }
                });



        hybridEventBus.registerEventHandler(
                        JsCallNativeEventName.EVENT_REQUEST_SWITCH_PAGE,
                        new EventHandler() {
                            @Override
                            public void handle(Event event, IHybridCallback callBack) {
                                UIManager.getInstance().switchPage(SupplierDetailActivity.this, event, callBack);
                            }

                        });

    }

    @Override
    public HybridAppWebView getHybridAppWebView() {
        return mWebView;
    }
}