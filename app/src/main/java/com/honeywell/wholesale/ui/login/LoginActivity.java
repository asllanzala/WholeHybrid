package com.honeywell.wholesale.ui.login;

import com.google.gson.Gson;

import com.honeywell.hybridapp.framework.IHybridCallback;
import com.honeywell.hybridapp.framework.event.Event;
import com.honeywell.hybridapp.framework.event.EventHandler;
import com.honeywell.hybridapp.framework.event.HybridEventBus;
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

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

/**
 * Created by e887272 on 6/17/16.
 *  Test Account : 13712345670 Password:123456
 */
public class LoginActivity extends Activity {

    public static final String TAG = LoginActivity.class.getSimpleName();

    private HybridAppWebView mWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mWebView = (HybridAppWebView) findViewById(R.id.hybrid_app_login_webView);
        initWebView();

        Button button = (Button) findViewById(R.id.open_gesture_lock_view_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, GestureLockActivity.class);
                startActivity(intent);
            }
        });

    }

    private void initWebView() {
        HybridEventHandlerUtil.getInstance().registerDefaultHandler(mWebView);
        HybridEventHandlerUtil.getInstance().registerWebApiHandler(mWebView);

        HybridEventBus hybridEventBus = mWebView.getHybridAppTunnel().getHybridEventBus();

        hybridEventBus.registerEventHandler(
                JsCallNativeEventName.EVENT_EXECUTE_NATIVE_METHOD,
                new EventHandler() {
                    @Override
                    public void handle(Event event, final IHybridCallback callBack) {
                        if (StringUtil.isEmpty(event.getData())) {
                            Log.e(TAG, JsCallNativeEventName.EVENT_EXECUTE_NATIVE_METHOD
                                    + ", event data is null.");

                            JsCallNativeEventResponseData responseData = new JsCallNativeEventResponseData(JsCallNativeEventResponseData.ACK_SUCCESS,
                                    "", JsCallNativeEventName.EVENT_EXECUTE_NATIVE_METHOD
                                    + " is executed in Java");

                            callBack.onCallBack(responseData);
                            return;
                        }

                        Log.i(TAG, "Receive event from H5 = " + event.getData());

                        JsCallNativeEventData jsCallNativeEventData = new Gson()
                                .fromJson(event.getData(), JsCallNativeEventData.class);

                        int nativeMethod = Integer.parseInt(jsCallNativeEventData.getNativeMethod());
                        String eventData = jsCallNativeEventData.getData();
                        JsCallNativeEventResponseData responseData = null;

                        switch (nativeMethod) {
                            case JsCallNativeEventName.NATIVE_METHOD_LOGIN:
                                LoginApplication.getInstance().userTriggerLogin(LoginActivity.this, eventData, callBack);
                                break;

                            case JsCallNativeEventName.NATIVE_METHOD_GET_USER_INFO:
                                Account account = AccountManager.getInstance().getCurrentAccount();
                                responseData = new JsCallNativeEventResponseData(JsCallNativeEventResponseData.ACK_SUCCESS,
                                        "", account.getJsonString());
                                Log.e(TAG, "执行回调：" + responseData.toString());
                                callBack.onCallBack(responseData);
                                break;

                            case JsCallNativeEventName.NATIVE_METHOD_LOGIN_GET_LOCAL_USER_LIST:
                                String allUserJson = AccountManager.getInstance().getAllAccountJson();
                                responseData = new JsCallNativeEventResponseData(JsCallNativeEventResponseData.ACK_SUCCESS,
                                        "", allUserJson);
                                Log.e(TAG, "获取所有用户信息：" + allUserJson);
                                callBack.onCallBack(responseData);
                                break;

                            default:
                                Log.e(TAG, "This method " + JsCallNativeEventName
                                        .getMethodName(nativeMethod) + "is not processed!");
                                break;

                        }
                    }


                });

        mWebView.loadUrl(H5PageURL.URL_LOGIN_PAGE);

    }

    private void updateCategory(String jsonData){

    }

    private void updateInventory(String jsonData){

    }
}
