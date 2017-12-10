package com.honeywell.wholesale.ui.menu.setting;

import android.os.Bundle;
import android.util.Log;

import com.google.gson.Gson;
import com.honeywell.hybridapp.framework.IHybridCallback;
import com.honeywell.hybridapp.framework.event.Event;
import com.honeywell.hybridapp.framework.event.EventHandler;
import com.honeywell.hybridapp.framework.event.JsCallNativeEventResponseData;
import com.honeywell.hybridapp.framework.view.HybridAppWebView;
import com.honeywell.wholesale.R;
import com.honeywell.wholesale.framework.hybrid.H5PageURL;
import com.honeywell.wholesale.framework.hybrid.HybridEventHandlerUtil;
import com.honeywell.wholesale.framework.hybrid.JsCallNativeEventData;
import com.honeywell.wholesale.framework.hybrid.JsCallNativeEventName;
import com.honeywell.wholesale.framework.model.Account;
import com.honeywell.wholesale.framework.model.AccountManager;
import com.honeywell.wholesale.lib.util.StringUtil;
import com.honeywell.wholesale.ui.base.BaseWebViewActivity;
import com.honeywell.wholesale.ui.uimanager.UIManager;

/**
 * Created by H154326 on 17/4/11.
 * Email: yang.liu6@honeywell.com
 */

public class MenuSettingActivity extends BaseWebViewActivity {

    public static final String TAG = MenuSettingActivity.class.getSimpleName();

    private HybridAppWebView mWebView;
    public static final int REQUEST_CODE_PRODUCT_IMAGES_UPLOAD = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_common_webview);

        mWebView = (HybridAppWebView) findViewById(R.id.hybrid_app_webView);
        initWebView();

        mWebView.loadUrl(H5PageURL.URL_LEFT_MENU_SETTING_PAGE);
    }



    @Override
    public HybridAppWebView getHybridAppWebView() {
        return mWebView;
    }

    public HybridAppWebView getWebView(){
        return mWebView;
    }

    private void initWebView(){
        HybridEventHandlerUtil.getInstance().registerDefaultHandler(mWebView);
        HybridEventHandlerUtil.getInstance().registerWebApiHandler(mWebView);
        mWebView.getHybridAppTunnel().getHybridEventBus().registerEventHandler(
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
                                        .getMethodName(nativeMethod) + "is not processed!");
                                break;

                        }
                    }
                }
        );

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
                                        .switchPage(MenuSettingActivity.this, event, callBack);
                            }
                        });
    }
}
