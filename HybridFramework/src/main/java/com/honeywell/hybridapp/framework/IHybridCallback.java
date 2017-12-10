package com.honeywell.hybridapp.framework;

import com.honeywell.hybridapp.framework.event.JsCallNativeEventResponseData;

/**
 * Created by e887272 on 6/28/16.
 */
public interface IHybridCallback {
    public void onCallBack(JsCallNativeEventResponseData data);
}
