package com.honeywell.hybridapp.framework.view;

import com.honeywell.hybridapp.framework.HybridAppTunnelConfig;
import com.honeywell.hybridapp.framework.event.Event;

import android.graphics.Bitmap;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

public class HybridAppWebViewClient extends WebViewClient {

    private final String TAG = HybridAppWebViewClient.class.getSimpleName();

    private HybridAppWebView mHybridAppWebView;

    public HybridAppWebViewClient(HybridAppWebView hybridAppWebView) {
        this.mHybridAppWebView = hybridAppWebView;
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        Log.v(TAG, "shouldOverrideUrlLoading：" + url);

        try {
            url = URLDecoder.decode(url, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        if (url.startsWith(HybridAppTunnelConfig.URL_RESPONSE_DATA_NEW_EVENT_ALERT)) {
            Log.v(TAG, "IFrame有新消息：" + url);
            mHybridAppWebView.getHybridAppTunnel().fetchEvents();
            return true;
        } else if (url.startsWith(HybridAppTunnelConfig.URL_RESPONSE_TAG)) {
            Log.v(TAG, "IFrame有返回数据：get response event, " + url);
            mHybridAppWebView.getHybridAppTunnel().processResponse(url);
            return true;
        } else {
            Log.v(TAG, "IFrame有非命令新地址：" + url);
            return super.shouldOverrideUrlLoading(view, url);
        }
    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        Log.v(TAG, "onPageStarted");
        super.onPageStarted(view, url, favicon);
    }

    @Override
    public void onPageFinished(WebView webView, String url) {
        Log.v(TAG, "onPageFinished");

        super.onPageFinished(webView, url);

        Log.v(TAG, "onPageFinished -> webViewLoadLocalJs start: JsTunnel.js");
        if (HybridAppWebView.JAVASCRIPT_TUNNEL_FILE_NAME != null) {
            HybridAppTunnelConfig
                    .webViewLoadLocalJs(webView, HybridAppWebView.JAVASCRIPT_TUNNEL_FILE_NAME);
        }
        Log.v(TAG, "onPageFinished -> webViewLoadLocalJs finish: JsTunnel.js");

        if (mHybridAppWebView.getHybridAppTunnel().getStartupEvent() != null) {
            for (Event event : mHybridAppWebView.getHybridAppTunnel().getStartupEvent()) {
                mHybridAppWebView.getHybridAppTunnel().dispatchMessage(event);
            }
            mHybridAppWebView.getHybridAppTunnel().setStartupEvent(null);
        }
    }

    @Override
    public void onReceivedError(WebView view, int errorCode, String description,
            String failingUrl) {
        super.onReceivedError(view, errorCode, description, failingUrl);
    }
}