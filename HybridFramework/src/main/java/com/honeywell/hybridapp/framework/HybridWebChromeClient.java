package com.honeywell.hybridapp.framework;

import android.util.Log;
import android.view.View;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

/**
 * Created by e887272 on 5/26/16.
 */
public class HybridWebChromeClient extends WebChromeClient {

    public static final String TAG = HybridWebChromeClient.class.getSimpleName();

    public HybridWebChromeClient() {
    }

//    //扩充缓存的容量
//    @Override
//    public void onReachedMaxAppCacheSize(long spaceNeeded,
//            long totalUsedQuota, WebStorage.QuotaUpdater quotaUpdater) {
//        quotaUpdater.updateQuota(spaceNeeded * 2);
//    }
//
//    // 扩充缓存的容量
//    @Override
//    public void onExceededDatabaseQuota(String url, String databaseIdentifier,
//            long currentQuota,long estimatedSize,long totalUsedQuota,
//            WebStorage.QuotaUpdater quotaUpdater)
//    {
//        quotaUpdater.updateQuota(estimatedSize * 2);
//    }

    @Override
    public void onProgressChanged(WebView view, int newProgress) {
        Log.v(TAG, "进度" + newProgress + "%");
    }


    @Override
    public boolean onJsAlert(WebView view, String url, String message,
            JsResult result) {
        // 对alert的简单封装
//        new AlertDialog.Builder(mActivity)
//                .setTitle("提示")
//                .setMessage(message)
//                .setPositiveButton("确定",
//                        new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface arg0,
//                                    int arg1) {
//                                System.out.println("测试");
//                            }
//                        }).create().show();
//        result.confirm(); // 处理来自用户的确认回复。

        Log.v(TAG, "确认弹出框");


        return true;
    }

    @Override
    public boolean onJsConfirm(WebView view, String url, String message,
            JsResult result) {
//        // 对alert的简单封装
//        new AlertDialog.Builder(mActivity)
//                .setTitle("提示")
//                .setMessage(message)
//                .setPositiveButton("确定",
//                        new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface arg0,
//                                    int arg1) {
//                                System.out.println("测试1");
//                            }
//                        }).create().show();
//        result.confirm(); // 处理来自用户的确认回复。

        Log.v(TAG, "onJsConfirm提示弹出框");

        return true;
    }

    @Override
    public boolean onJsPrompt(WebView view, String url, String message,
            String defaultValue, JsPromptResult result) {
        // 对alert的简单封装
//        new AlertDialog.Builder(mActivity)
//                .setTitle("提示" + defaultValue)
//                .setMessage(message)
//                .setPositiveButton("确定",
//                        new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface arg0,
//                                    int arg1) {
//                                System.out.println("测试");
//                            }
//                        }).create().show();
//        result.confirm(); // 处理来自用户的确认回复。

        Log.v(TAG, "onJsPrompt弹出框");

        return true;
    }

    @Override
    public void onRequestFocus(WebView view) {
        super.onRequestFocus(view);
    }

    @Override
    public void onShowCustomView(View view, WebChromeClient.CustomViewCallback callback) {
        super.onShowCustomView(view, callback);
    }

}