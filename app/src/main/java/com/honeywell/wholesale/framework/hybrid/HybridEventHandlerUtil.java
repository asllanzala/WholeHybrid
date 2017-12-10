package com.honeywell.wholesale.framework.hybrid;

import com.google.gson.Gson;

import com.honeywell.hybridapp.framework.IHybridCallback;
import com.honeywell.hybridapp.framework.event.DefaultEventHandler;
import com.honeywell.hybridapp.framework.event.Event;
import com.honeywell.hybridapp.framework.event.EventHandler;
import com.honeywell.hybridapp.framework.event.JsCallNativeEventResponseData;
import com.honeywell.hybridapp.framework.log.LogHelper;
import com.honeywell.hybridapp.framework.view.HybridAppWebView;
import com.honeywell.wholesale.framework.http.WebClient;
import com.honeywell.wholesale.framework.model.Account;
import com.honeywell.wholesale.framework.model.AccountManager;
import com.honeywell.wholesale.framework.model.CartItem;
import com.honeywell.wholesale.framework.model.CartManager;
import com.honeywell.wholesale.lib.util.StringUtil;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;


/**
 * Created by e887272 on 6/13/16.
 */
public class HybridEventHandlerUtil {

    public static final String TAG = HybridEventHandlerUtil.class.getSimpleName();

    private static HybridEventHandlerUtil mHybridEventHandlerUtil;

    private HybridEventHandlerUtil() {

    }

    public static synchronized HybridEventHandlerUtil getInstance() {
        if (mHybridEventHandlerUtil == null) {
            mHybridEventHandlerUtil = new HybridEventHandlerUtil();
        }
        return mHybridEventHandlerUtil;
    }

    public void registerDefaultHandler(HybridAppWebView hybridAppWebView) {
        hybridAppWebView.getHybridAppTunnel().getHybridEventBus()
                .setDefaultEventHandler(new DefaultEventHandler());
    }

    public void registerWebApiHandler(HybridAppWebView hybridAppWebView) {
        hybridAppWebView.getHybridAppTunnel().getHybridEventBus()
                .registerEventHandler(
                        JsCallNativeEventName.EVENT_REQUEST_WEB_API,
                        new EventHandler() {
                            @Override
                            public void handle(Event event, IHybridCallback callBack) {
                                requestWebApi(event, callBack);
                            }
                        });
    }

    public JSONObject parseRequestJson (String requestJsonStr, IHybridCallback callBack) {
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

    // http request
    public void requestWebApi(Event event, IHybridCallback callBack) {
        WebClient webClient = new WebClient();
        webClient.httpJsonRequest(event, callBack);
    }

}
