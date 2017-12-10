package com.honeywell.hybridapp.framework.event;

import com.honeywell.hybridapp.framework.ICallBack;
import com.honeywell.hybridapp.framework.IHybridCallback;

import android.util.Log;

public class DefaultEventHandler implements EventHandler {

    public static final String TAG = DefaultEventHandler.class.getSimpleName();

    @Override
    public void handle(Event event, IHybridCallback callBack) {
        if (callBack != null) {
            String response = "Event = " + event.getEventName() + ", no event handler found! Event data = " + event.getData();
            Log.e(TAG, response);
            JsCallNativeEventResponseData responseData = new JsCallNativeEventResponseData(JsCallNativeEventResponseData.ACK_SUCCESS, response, event.getData());
            callBack.onCallBack(responseData);
        }
    }

}
