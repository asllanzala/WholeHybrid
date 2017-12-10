package com.honeywell.wholesale.ui.menu;

import com.google.gson.Gson;

import com.google.gson.reflect.TypeToken;
import com.honeywell.hybridapp.framework.ICallBack;
import com.honeywell.hybridapp.framework.IHybridCallback;
import com.honeywell.hybridapp.framework.event.Event;
import com.honeywell.hybridapp.framework.event.EventHandler;
import com.honeywell.hybridapp.framework.event.JsCallNativeEventResponseData;
import com.honeywell.hybridapp.framework.log.LogHelper;
import com.honeywell.hybridapp.framework.view.HybridAppWebView;
import com.honeywell.wholesale.R;
import com.honeywell.wholesale.framework.database.AccountDAO;
import com.honeywell.wholesale.framework.database.InventoryDAO;
import com.honeywell.wholesale.framework.http.ShopQueryResponse;
import com.honeywell.wholesale.framework.hybrid.H5PageURL;
import com.honeywell.wholesale.framework.hybrid.HybridEventHandlerUtil;
import com.honeywell.wholesale.framework.hybrid.JsCallNativeEventData;
import com.honeywell.wholesale.framework.hybrid.JsCallNativeEventName;
import com.honeywell.wholesale.framework.hybrid.NativeCallJsEventName;
import com.honeywell.wholesale.framework.model.Account;
import com.honeywell.wholesale.framework.model.AccountManager;
import com.honeywell.wholesale.framework.model.CartItem;
import com.honeywell.wholesale.framework.model.CartManager;
import com.honeywell.wholesale.lib.util.StringUtil;
import com.honeywell.wholesale.ui.base.BaseActivity;
import com.honeywell.wholesale.ui.base.BaseWebViewActivity;
import com.honeywell.wholesale.ui.inventory.AddProductActivity;
import com.honeywell.wholesale.ui.selectpic.ImageSelectorActivity;
import com.honeywell.wholesale.ui.selectpic.config.SelectorSetting;
import com.honeywell.wholesale.ui.uimanager.UIManager;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by e887272 on 6/27/16.
 */
public class OutletManagementActivity extends BaseWebViewActivity {

    public static final String TAG = OutletManagementActivity.class.getSimpleName();

    private HybridAppWebView mWebView;
    public static final int REQUEST_CODE_PRODUCT_IMAGES_UPLOAD = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_common_webview);

        mWebView = (HybridAppWebView) findViewById(R.id.hybrid_app_webView);
        initWebView();

        mWebView.loadUrl(H5PageURL.URL_OUTLET_MANAGEMENT_PAGE);
    }

    public HybridAppWebView getWebView(){
        return mWebView;
    }

    private void initWebView() {
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
                                JsCallNativeEventData jsCallNativeEventData = new Gson()
                                        .fromJson(event.getData(), JsCallNativeEventData.class);

                                String nativeMethod = jsCallNativeEventData.getNativeMethod();
                                String requestJsonStr = jsCallNativeEventData.getData();

                                if (JsCallNativeEventName.NATIVE_PAGE_IMAGE_UPLOAD
                                        .equals(nativeMethod)) {
                                    JSONObject requestBodyJsonObject = UIManager
                                            .parseRequestJson(jsCallNativeEventData.getData(),
                                                    callBack);
                                    if (requestBodyJsonObject == null) {
                                        return;
                                    }

                                    String productID = requestBodyJsonObject.optString("productID");
                                    String shopID = requestBodyJsonObject.optString("shopID");

                                    Intent selectImageIntent = new Intent(OutletManagementActivity.this,
                                            ImageSelectorActivity.class);
                                    selectImageIntent.putExtra(SelectorSetting.SELECTOR_PRODUCT_ID,
                                            productID);
                                    selectImageIntent
                                            .putExtra(SelectorSetting.SELECTOR_SHOP_ID, shopID);
                                    startActivityForResult(selectImageIntent,
                                            REQUEST_CODE_PRODUCT_IMAGES_UPLOAD);
                                }else {
                                    UIManager.getInstance()
                                            .switchPage(OutletManagementActivity.this, event, callBack);
                                }
                            }
                        });
    }


    @Override
    public HybridAppWebView getHybridAppWebView() {
        return mWebView;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) {
            return;
        }
        switch (requestCode) {
            case REQUEST_CODE_PRODUCT_IMAGES_UPLOAD:
                String results = data.getStringExtra(SelectorSetting.SELECTOR_RESULTS);
                sendDataToWebview(results);
                break;

            default:
                break;
        }
    }

    protected void sendDataToWebview(String jsonData) {
        if (getWebView() != null) {
            getWebView().getHybridAppTunnel()
                    .callJS(NativeCallJsEventName.NATIVE_CALL_JS_EVENT_PRODUCT_IMAGE_UPLOAD_RESULT, jsonData,
                            new ICallBack() {
                                @Override
                                public void onCallBack(String data) {
                                    Log.v(TAG,
                                            NativeCallJsEventName.NATIVE_CALL_JS_EVENT_PRODUCT_IMAGE_UPLOAD_RESULT
                                                    + " CallBack, data=" + data);
                                }
                            });
        }
    }
}
