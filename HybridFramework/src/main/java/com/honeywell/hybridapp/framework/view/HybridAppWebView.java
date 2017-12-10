package com.honeywell.hybridapp.framework.view;

import com.google.gson.Gson;

import com.honeywell.hybridapp.framework.BuildConfig;
import com.honeywell.hybridapp.framework.HybridAppTunnel;
import com.honeywell.hybridapp.framework.HybridWebChromeClient;
import com.honeywell.hybridapp.framework.ICallBack;
import com.honeywell.hybridapp.framework.R;
import com.honeywell.hybridapp.framework.event.DemoEventName;
import com.honeywell.hybridapp.framework.event.Event;
import com.honeywell.hybridapp.framework.event.EventHandler;
import com.honeywell.hybridapp.framework.event.NativeCallJsEvent;

import org.json.JSONException;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class HybridAppWebView extends WebView {

    private final String TAG = HybridAppWebView.class.getSimpleName();

    public static final String JAVASCRIPT_TUNNEL_FILE_NAME = "JsTunnel.js";

    private HybridAppTunnel mHybridAppTunnel;

    private HybridAppWebViewClient mHybridAppWebViewClient;

    public HybridAppWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mHybridAppTunnel = new HybridAppTunnel(this);
        init();
    }

    public HybridAppWebView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public HybridAppWebView(Context context) {
        super(context);
        init();
    }

    public HybridAppTunnel getHybridAppTunnel() {
        return mHybridAppTunnel;
    }

    private void init() {
        // Adndroid 4.0以上背景失效
        // 当程序在4.0上使用时，发现居然这种设置方法无法，即使通过上面设置背景为0，照样显示出原来默认的白色背景。
        // 通过网上查找，发现原来是由于硬件加速导致的，此时就想到了使用代码关闭当前webview的硬件加速，方法如下：
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        // 这只灰色的背景色
        setBackgroundColor(0XEEEEEE);  //透明背景

        this.setVerticalScrollBarEnabled(false);
        this.setHorizontalScrollBarEnabled(false);

        Log.v("MZ", "Build.VERSION.SDK_INT=" + Build.VERSION.SDK_INT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true);
        }

//        if (BuildConfig.DEBUG && Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
//            WebView.setWebContentsDebuggingEnabled(true);
//        }else if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
//            WebView.setWebContentsDebuggingEnabled(false);
//        }

        mHybridAppWebViewClient = new HybridAppWebViewClient(this);
        this.setWebViewClient(mHybridAppWebViewClient);

        //增加接口方法,让html页面调用
        //注意第二个参数JsTest，这个是JS网页调用Android方法的一个类似ID的东西
        WebSettings mWebSettings = getSettings();
        mWebSettings.setJavaScriptEnabled(true);   //加上这句话才能使用javascript方法

        mWebSettings.setSupportZoom(false);
        mWebSettings.setTextSize(WebSettings.TextSize.NORMAL);//

        //启用数据库
        mWebSettings.setDatabaseEnabled(true);
        String dbPath = getContext().getDir("database", Context.MODE_PRIVATE)
                .getPath();
        mWebSettings.setDatabasePath(dbPath);

        // 使用localStorage则必须打开
        mWebSettings.setDomStorageEnabled(true);
        // 可以读取文件缓存(manifest生效)
        mWebSettings.setAllowFileAccess(true);

        mWebSettings.setAppCacheEnabled(true);
        //缓存最多可以有8M
        mWebSettings.setAppCacheMaxSize(1024 * 1024 * 8);
        String appCachePath = getContext().getCacheDir().getAbsolutePath();
        mWebSettings.setAppCachePath(appCachePath);
        // 默认使用缓存
        mWebSettings.setCacheMode(WebSettings.LOAD_DEFAULT);

        // Build.VERSION_CODES.KITKAT == 16
        try {
            if (Build.VERSION.SDK_INT >= 16) {
                Class<?> clazz = mWebSettings.getClass();
                Method method = clazz.getMethod(
                        "setAllowUniversalAccessFromFileURLs", boolean.class);
                if (method != null) {
                    method.invoke(mWebSettings, true);
                }
            }
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        invalidate();
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public void loadUrl(String url, NativeCallJsEvent nativeCallJsEvent) {

        ICallBack callBack = new ICallBack() {
            @Override
            public void onCallBack(String data) {
                Log.i(TAG, "reponse data from js " + data);
            }
        };

        mHybridAppTunnel.callJS(nativeCallJsEvent.getEventName(), nativeCallJsEvent.getEventJsonStr(), callBack);
        super.loadUrl(url);
    }
}
