package com.honeywell.wholesale.ui.inventory;

import com.google.gson.Gson;

import com.honeywell.hybridapp.framework.ICallBack;
import com.honeywell.hybridapp.framework.IHybridCallback;
import com.honeywell.hybridapp.framework.event.Event;
import com.honeywell.hybridapp.framework.event.EventHandler;
import com.honeywell.hybridapp.framework.event.JsCallNativeEventResponseData;
import com.honeywell.hybridapp.framework.log.LogHelper;
import com.honeywell.wholesale.framework.database.StockDAO;
import com.honeywell.wholesale.framework.hybrid.H5PageURL;
import com.honeywell.wholesale.framework.hybrid.HybridEventHandlerUtil;
import com.honeywell.wholesale.framework.hybrid.JsCallNativeEventData;
import com.honeywell.wholesale.framework.hybrid.JsCallNativeEventName;
import com.honeywell.wholesale.framework.hybrid.NativeCallJsEventName;
import com.honeywell.wholesale.framework.model.Account;
import com.honeywell.wholesale.framework.model.AccountManager;
import com.honeywell.wholesale.lib.util.StringUtil;
import com.honeywell.wholesale.ui.base.CommonWebViewActivity;

import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

/**
 * Created by e887272 on 7/29/16.
 */
public class ProductStockInActivity extends CommonWebViewActivity {

    public static final String TAG = ProductStockInActivity.class.getSimpleName();
    public static final String INTENT_KEY_JSON_DATA = "INTENT_KEY_JSON_DATA";

    public static final String INTENT_VALUE_STOCK_IN_RECORD_ID = "stockRecordId";
    public static final String INTENT_VALUE_STANDARD_PRICE = "standardPrice";
    public static final String INTENT_VALUE_VENDOR = "vendor";
    public static final String INTENT_VALUE_NUMBER = "number";
    public static final String INTENT_VALUE_PRODUCT_NAME = "productName";
    public static final String INTENT_VALUE_PRODUCT_CODE = "productCode";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        if (intent != null) {
            String jsonData = intent.getStringExtra(INTENT_KEY_JSON_DATA);
            sendDataToWebview(jsonData);
        }
    }

    @Override
    public String getUrl() {
        return H5PageURL.URL_PRODUCT_STOCK_IN;
    }

    @Override
    protected void initWebView() {
        super.initWebView();
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
                            case JsCallNativeEventName.NATIVE_METHOD_GET_USER_INFO:
                                Account account = AccountManager.getInstance().getCurrentAccount();
                                responseData = new JsCallNativeEventResponseData(
                                        JsCallNativeEventResponseData.ACK_SUCCESS,
                                        "", account.getJsonString());

                                Log.e(TAG, "执行回调：" + responseData.toString());
                                callBack.onCallBack(responseData);
                                break;

                            case JsCallNativeEventName.NATIVE_METHOD_PRODUCT_STOCK_CONFIRM:
                                // Parse json data
                                JSONObject requestBodyJsonObject3 = HybridEventHandlerUtil
                                        .getInstance().parseRequestJson(jsCallNativeEventData.getData(), callBack);
                                if (requestBodyJsonObject3 == null) {
                                    return;
                                }

                                String stockRecordID = requestBodyJsonObject3.optString("stockRecordId");
                                StockDAO.updateStockItem(stockRecordID, StockDAO.STOCK_IN_STATUS);

                                responseData = new JsCallNativeEventResponseData(
                                        JsCallNativeEventResponseData.ACK_SUCCESS,
                                        "补登数据成功, stockRecordID=" + stockRecordID, "");
                                LogHelper.getInstance().d(TAG, "执行回调：" + responseData.toString());
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

}