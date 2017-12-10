package com.honeywell.wholesale.ui.base;

import android.content.Context;

/**
 * Created by liuyang on 2017/7/3.
 */

public interface WholesaleBaseView {
    public void onShowLoading();
    public void onHideLoading();
    public void onToast(String message);
    public void onIntent();
    public Context getContext();

}
