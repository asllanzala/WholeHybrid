package com.honeywell.wholesale.ui.login;

import com.honeywell.hybridapp.framework.IHybridCallback;
import com.honeywell.hybridapp.framework.event.WholesaleHttpResponse;
import com.honeywell.hybridapp.framework.event.JsCallNativeEventResponseData;
import com.honeywell.hybridapp.framework.log.LogHelper;
import com.honeywell.wholesale.framework.application.AppInitManager;
import com.honeywell.wholesale.framework.http.NativeJsonResponseListener;
import com.honeywell.wholesale.framework.http.WebClient;
import com.honeywell.wholesale.framework.model.Account;
import com.honeywell.wholesale.framework.model.AccountManager;
import com.honeywell.wholesale.lib.util.StringUtil;
import com.honeywell.wholesale.ui.activity.MainActivity;
import com.honeywell.wholesale.ui.login.module.LoginCloudApiRequest;
import com.honeywell.wholesale.ui.util.CommonUtil;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import org.json.JSONObject;

/**
 * Created by e887272 on 7/19/16.
 */
public class LoginApplication {

    public static final String TAG = LoginApplication.class.getSimpleName();

    private static LoginApplication mLoginApplication = null;

    public static synchronized LoginApplication getInstance() {
        if(mLoginApplication == null) {
            mLoginApplication = new LoginApplication();
        }
        return mLoginApplication;
    }

    private void callLoginApi(LoginCloudApiRequest loginCloudApiRequest, ILoginCallback loginCallback) {
        WebClient webClient = new WebClient();
        webClient.login(loginCloudApiRequest, loginCallback);
    }

    public void autoLogin(final ILoginCallback loginCallback) {
        Account account = AccountManager.getInstance().getCurrentAccount();
        if(account == null || StringUtil.isEmpty(account.getToken())) {
            loginCallback.onErrorCallback(null);
            return;
        }

        ILoginCallback loginInternalCallback = new ILoginCallback() {
            @Override
            public void onSuccessCallback(String response) {
                AppInitManager.getInstance().startSyncData();

                loginCallback.onSuccessCallback(response);
            }

            @Override
            public void onErrorCallback(WholesaleHttpResponse response) {
                loginCallback.onErrorCallback(response);
            }
        };

        LoginCloudApiRequest loginCloudApiRequest = new LoginCloudApiRequest(account.getCompanyAccount(), account.getLoginName(), account.getPassword());
        callLoginApi(loginCloudApiRequest, loginInternalCallback);
    }

    /**
     * @param jsonData {'login_name': "", 'password': "", 'company_account':""}
     * @param h5CallBack
     */
    public void userTriggerLogin(final Activity activity, String jsonData, final IHybridCallback h5CallBack) {

        ILoginCallback loginCallback = new ILoginCallback() {
            @Override
            public void onSuccessCallback(String response) {
                JsCallNativeEventResponseData responseData = new JsCallNativeEventResponseData(JsCallNativeEventResponseData.ACK_SUCCESS,
                        "登陆成功了", response);
                h5CallBack.onCallBack(responseData);
                AppInitManager.getInstance().startSyncData();
                AppInitManager.getInstance().getFullCategory(null);

                AppInitManager.getInstance().getCategory(new NativeJsonResponseListener<JSONObject>() {
                    @Override
                    public void listener(JSONObject jsonObject) {
                        // 打开Dashboard主页面
                        activity.startActivity(new Intent(activity, MainActivity.class));
                        activity.finish();
                    }
                    @Override
                    public void errorListener(String s) {
                        Log.e(TAG, "获取分类失败");
                    }
                });

            }

            @Override
            public void onErrorCallback(WholesaleHttpResponse response) {
                LogHelper.getInstance().i(TAG, "登陆失败, response=" + response);
                JsCallNativeEventResponseData responseData = new JsCallNativeEventResponseData(JsCallNativeEventResponseData.ACK_FAILED,
                        "登陆失败", response.getJsonString());
                h5CallBack.onCallBack(responseData);
            }
        };

        LoginCloudApiRequest loginCloudApiRequest = null;
        try {
            loginCloudApiRequest = LoginCloudApiRequest.fromJson(jsonData);
            callLoginApi(loginCloudApiRequest, loginCallback);
        } catch (Exception e) {
            e.printStackTrace();
            JsCallNativeEventResponseData responseData = new JsCallNativeEventResponseData(JsCallNativeEventResponseData.ACK_FAILED,
                    "收到的json数据不能正确解析: " + jsonData, "");
            h5CallBack.onCallBack(responseData);
            return;
        }

    }
}
