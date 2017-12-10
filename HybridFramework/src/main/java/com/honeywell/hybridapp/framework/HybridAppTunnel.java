package com.honeywell.hybridapp.framework;


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import com.honeywell.hybridapp.framework.event.*;
import com.honeywell.hybridapp.framework.event.JsCallNativeEventResponseData;
import com.honeywell.hybridapp.framework.log.LogHelper;
import com.honeywell.hybridapp.framework.view.HybridAppWebView;

import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class HybridAppTunnel {

    public static final String TAG = HybridAppTunnel.class.getSimpleName();

    private List<Event> mStartupEvent = new ArrayList<Event>();

    // Process the Javascript response data in the ICallBack.
    // The Map key is the loadUrl request URL.
    private Map<String, ICallBack> mJsResponseCallbacks = new HashMap<String, ICallBack>();

    private HybridAppWebView mHybridAppWebView;

    private HybridEventBus mHybridEventBus;

    public HybridAppTunnel(HybridAppWebView hybridAppWebView) {
        mHybridAppWebView = hybridAppWebView;
        mHybridEventBus = new HybridEventBus();
    }

    public void callJS(String data) {
        callJS(data, null);
    }

    public void callJS(String data, ICallBack callback) {
        callJS(null, data, callback);
    }

    public void callJS(String eventName, String data, ICallBack callback) {
        Event event = new Event(eventName, data);
        Log.v(TAG, "callJS(), event=" + event.toJsonString());

        if (callback != null) {
            mJsResponseCallbacks.put(event.getCallbackId(), callback);
        }

        enqueueEvent(event);
    }

    private void enqueueEvent(Event event) {
        if (mStartupEvent != null) {
            LogHelper.getInstance().v(TAG, "mStartupEvent.add(event)");
            mStartupEvent.add(event);
        } else {
            dispatchMessage(event);
            LogHelper.getInstance().v(TAG, "dispatchMessage() + event=" + event.toJsonString());
        }
    }

    /**
     * This method should be executed in main UI thread.
     */
    public void dispatchMessage(Event event) {
        String eventJson = event.toJsonString();
        //escape special characters for json string
        eventJson = eventJson.replaceAll("(\\\\)([^utrn])", "\\\\\\\\$1$2");
        eventJson = eventJson.replaceAll("(?<=[^\\\\])(\")", "\\\\\"");

        LogHelper.getInstance().v(TAG, "dispatchMessage() + eventJson=" + eventJson);

        String handelRequestUrl = HybridAppTunnelConfig
                .getFinalURL(HybridAppTunnelConfig.JS_TUNNEL_CALL_JS_API);
        String javascriptCommand = String.format(handelRequestUrl, eventJson);


        LogHelper.getInstance().v(TAG, "dispatchMessage() --> WebView.loadUrl(" + javascriptCommand + ")");
        mHybridAppWebView.loadUrl(javascriptCommand);
    }

    /**
     * Execut WebView.loadUrl(url) and save this url's callback in a map.
     */
    public void loadUrl(String url, ICallBack returnCallback) {
        mHybridAppWebView.loadUrl(url);

        Log.v(TAG, "loadUrl() --> HybridAppTunnelConfig.parseFunctionName(url) = "
                + HybridAppTunnelConfig.parseFunctionName(url));
        mJsResponseCallbacks.put(HybridAppTunnelConfig.parseFunctionName(url), returnCallback);
    }

    /**
     * This method should been executed in UI main thread.
     */
    public void fetchEvents() {
        String fetchEventUrl = HybridAppTunnelConfig
                .getFinalURL(HybridAppTunnelConfig.JS_TUNNEL_API_FETCH_EVENTS);

        LogHelper.getInstance().d(TAG, "fetchEvents(): " + fetchEventUrl);

        ICallBack fetchEventsJSCallBack = new ICallBack() {
            @Override
            public void onCallBack(String data) {
                Log.d(TAG, "fetchEvents -> onCallBack(): " + data);

                List<Event> eventList = null;
                try {
                    eventList = new Gson().fromJson(data, new TypeToken<List<Event>>(){}.getType());
                } catch (Exception e) {
                    e.printStackTrace();
                    return;
                }

                if (eventList == null || eventList.size() == 0) {
                    return;
                }

                for (int i = 0; i < eventList.size(); i++) {
                    Event event = eventList.get(i);
                    String responseId = event.getResponseId();

                    // 是否是response
                    if (!TextUtils.isEmpty(responseId)) {
                        LogHelper.getInstance().d(TAG, "responseId=" + responseId);

                        ICallBack function = mJsResponseCallbacks.get(responseId);
                        if (function != null) {
                            String responseData = event.getResponseData();
                            function.onCallBack(responseData);
                            mJsResponseCallbacks.remove(responseId);

                        } else {
                            // No handler for this response
                            StringBuffer stringBuffer = new StringBuffer();
                            Set s = mJsResponseCallbacks.entrySet();
                            Iterator iterator = s.iterator();

                            while (iterator.hasNext()) {
                                Map.Entry o = (Map.Entry) iterator.next();
                                stringBuffer.append(o.getKey() + " -- " + o.getValue());
                            }

                            Log.w(TAG, "No mJsResponseCallbacks. responseId=" + responseId + ", "
                                    + stringBuffer.toString());
                        }

                    } else {
                        IHybridCallback jsCallBack = null;
                        // if had callbackId
                        final String callbackId = event.getCallbackId();

                        if (!TextUtils.isEmpty(callbackId)) {
                            jsCallBack = new IHybridCallback() {

                                @Override
                                public void onCallBack(JsCallNativeEventResponseData data) {
                                    Event responseEvent = new Event();
                                    responseEvent.setResponseId(callbackId);
                                    responseEvent.setResponseData(data.getJsonString());
                                    enqueueEvent(responseEvent);

                                    LogHelper.getInstance().v(TAG, "Native Send call back event to H5. callbackId=" + callbackId + ", data=" + data);
                                }
                            };
                        } else {
                            jsCallBack = new IHybridCallback() {
                                @Override
                                public void onCallBack(JsCallNativeEventResponseData data) {
                                    LogHelper.getInstance().v(TAG, "No need callback");
                                }
                            };
                        }

                        EventHandler handler = mHybridEventBus.getEventHandler(event);
                        if (handler != null) {
                            handler.handle(event, jsCallBack);
                        } else {
                            LogHelper.getInstance().w(TAG, "No handler for the receive event. " + event.toJsonString());
                        }
                    }
                }
            }
        };

        loadUrl(fetchEventUrl, fetchEventsJSCallBack);
    }

    public void processResponse(String url) {
        Log.e(TAG, "processResponse：url=" + url);

        String functionName = HybridAppTunnelConfig.getFunctionFromReturnUrl(url);
        ICallBack f = mJsResponseCallbacks.get(functionName);
        String data = HybridAppTunnelConfig.getDataFromReturnUrl(url);
        if (f != null) {
            f.onCallBack(data);
            mJsResponseCallbacks.remove(functionName);
            return;
        }
    }

    public List<Event> getStartupEvent() {
        return mStartupEvent;
    }

    public void setStartupEvent(List<Event> startupEvent) {
        this.mStartupEvent = startupEvent;
    }

    public HybridEventBus getHybridEventBus() {
        return mHybridEventBus;
    }
}