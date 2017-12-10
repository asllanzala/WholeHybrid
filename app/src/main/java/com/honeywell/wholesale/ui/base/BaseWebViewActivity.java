package com.honeywell.wholesale.ui.base;

import com.honeywell.hybridapp.framework.view.HybridAppWebView;

import android.app.Activity;

/**
 * 需要WebView的页面集成这个Activity，这样就可以在父类里面
 * Created by e887272 on 8/25/16.
 */
public abstract class BaseWebViewActivity extends Activity {

    @Override
    public void onBackPressed() {
        if (getHybridAppWebView().canGoBack()) {
            getHybridAppWebView().goBack();
        } else {
            finish();
            onActivityBackPressed();
        }
    }

    /**
     * 如果需要在点页面回退后特殊处理可以走这里
     */
    public void onActivityBackPressed(){

    }

    public abstract HybridAppWebView getHybridAppWebView();

}
