package com.honeywell.hybridapp.framework.demo;

import com.honeywell.hybridapp.framework.ICallBack;
import com.honeywell.hybridapp.framework.IHybridCallback;
import com.honeywell.hybridapp.framework.event.DefaultEventHandler;
import com.honeywell.hybridapp.framework.R;
import com.honeywell.hybridapp.framework.event.DemoEventName;
import com.honeywell.hybridapp.framework.event.Event;
import com.honeywell.hybridapp.framework.event.EventHandler;
import com.honeywell.hybridapp.framework.view.HybridAppWebView;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.widget.Button;

public class DemoActivity extends Activity {

    private final String TAG = DemoActivity.class.getSimpleName();

    private HybridAppWebView mWebView;

    private Button button;

    private int RESULT_CODE = 0;

    private ValueCallback<Uri> mUploadMessage;

    private static class Location {

        String address;
    }

    static class User {

        String name;

        Location location;

        String testStr;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hybrid_app_main);

        mWebView = (HybridAppWebView) findViewById(R.id.hybrid_app_webView);

        button = (Button) findViewById(R.id.hybrid_app_button);

        button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (button.equals(v)) {
                    mWebView.getHybridAppTunnel().callJS(DemoEventName.EVENT_NAME_1, "data from Java", new ICallBack() {
                        @Override
                        public void onCallBack(String data) {
                            Log.i(TAG, "reponse data from js " + data);
                        }
                    });
                }
            }
        });

        mWebView.getHybridAppTunnel().getHybridEventBus().setDefaultEventHandler(new DefaultEventHandler());

        mWebView.setWebChromeClient(new WebChromeClient() {

            @SuppressWarnings("unused")
            public void openFileChooser(ValueCallback<Uri> uploadMsg, String AcceptType,
                    String capture) {
                this.openFileChooser(uploadMsg);
            }

            @SuppressWarnings("unused")
            public void openFileChooser(ValueCallback<Uri> uploadMsg, String AcceptType) {
                this.openFileChooser(uploadMsg);
            }

            public void openFileChooser(ValueCallback<Uri> uploadMsg) {
                mUploadMessage = uploadMsg;
                pickFile();
            }
        });

        mWebView.loadUrl("file:///android_asset/demo.html");

//        mWebView.getHybridAppTunnel().getHybridEventBus()
//                .registerEventHandler(DemoEventName.EVENT_NAME_2, new EventHandler() {
//                    @Override
//                    public void handle(Event event, IHybridCallback callBack) {
//                        Log.i(TAG, "handler = " + DemoEventName.EVENT_NAME_2 + ", data from web = " + event.getData());
//                        callBack.onCallBack(DemoEventName.EVENT_NAME_2 + " is executed in Java");
//                    }
//                });
//
//        mWebView.getHybridAppTunnel().getHybridEventBus()
//                .registerEventHandler(DemoEventName.EVENT_NAME_1, new EventHandler() {
//                    @Override
//                    public void handle(Event event, IHybridCallback callBack) {
//                        Log.i(TAG, "handler = " + DemoEventName.EVENT_NAME_1 + ", data from web = " + event.getData());
//                        callBack.onCallBack(DemoEventName.EVENT_NAME_1 + " is executed in Java");
//                    }
//                });

//        User user = new User();
//        Location location = new Location();
//        location.address = "SDU";
//        user.location = location;
//        user.name = "大头鬼";
//
//        mWebView.getHybridAppTunnel().callJS(DemoEventName.EVENT_NAME_1, new Gson().toJsonString(user), new ICallBack() {
//            @Override
//            public void onCallBack(String data) {
//                Log.e("MZ", "callHandler.onCallBack");
//            }
//        });
//
//        mWebView.getHybridAppTunnel().callJS("hello");
    }

    public void pickFile() {
        Intent chooserIntent = new Intent(Intent.ACTION_GET_CONTENT);
        chooserIntent.setType("image/*");
        startActivityForResult(chooserIntent, RESULT_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == RESULT_CODE) {
            if (null == mUploadMessage) {
                return;
            }
            Uri result = intent == null || resultCode != RESULT_OK ? null : intent.getData();
            mUploadMessage.onReceiveValue(result);
            mUploadMessage = null;
        }
    }

}
