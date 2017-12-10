package com.honeywell.wholesale.ui.login;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.honeywell.hybridapp.framework.event.WholesaleHttpResponse;
import com.honeywell.wholesale.R;
import com.honeywell.wholesale.framework.application.AppInitManager;
import com.honeywell.wholesale.framework.http.NativeJsonResponseListener;
import com.honeywell.wholesale.ui.activity.MainActivity;
import com.honeywell.wholesale.ui.util.CommonUtil;

import org.json.JSONObject;

/**
 * Created by H155935 on 16/6/20.
 * Email: xiaofei.he@honeywell.com
 */
public class SplashActivity extends Activity {
    private static final String TAG = "SplashActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // press home button to quit, after that, show main page if return back to app.
        if(!isTaskRoot())
        {
            finish();
            return;
        }

        setContentView(R.layout.activity_splash);

        ILoginCallback loginCallback = new ILoginCallback() {
            @Override
            public void onSuccessCallback(String loginResponse) {

                AppInitManager.getInstance().getCategory(new NativeJsonResponseListener<JSONObject>() {
                    @Override
                    public void listener(JSONObject jsonObject) {
                        startActivity(new Intent(SplashActivity.this, MainActivity.class));
                        finish();
                    }
                    @Override
                    public void errorListener(String s) {
                        Log.e(TAG, "获取分类失败");
                    }
                });

            }

            @Override
            public void onErrorCallback(WholesaleHttpResponse responseString) {
                Intent mainIntent = new Intent(SplashActivity.this, LoginActivity.class);
                startActivity(mainIntent);
                finish();
            }
        };

        LoginApplication.getInstance().autoLogin(loginCallback);
    }



}
