package com.honeywell.hybridapp.framework.event;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import com.honeywell.hybridapp.framework.HybridAppTunnelConfig;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.SystemClock;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class Event {

    public static final String TAG = Event.class.getSimpleName();

    @SerializedName("callbackId")
    private String mCallbackId;

    @SerializedName("responseId")
    private String mResponseId;

    @SerializedName("responseData")
    private String mResponseData;

    @SerializedName("data")
    private String mData;

    @SerializedName("name")
    private String mEventName;

    private final static String CALLBACK_ID_STR = "callbackId";

    private final static String RESPONSE_ID_STR = "responseId";

    private final static String RESPONSE_DATA_STR = "responseData";

    private final static String DATA_STR = "data";

    private final static String EVENT_NAME_STR = "name";


    public Event() {

    }

    public Event(String eventName, String eventData) {
        mEventName = eventName;
        mData = eventData;
        mCallbackId = String.format(HybridAppTunnelConfig.EVENT_CALLBACK_ID_FORMAT,
                SystemClock.currentThreadTimeMillis());
    }

    public String getResponseId() {
        return mResponseId;
    }

    public void setResponseId(String responseId) {
        this.mResponseId = responseId;
    }

    public String getResponseData() {
        return mResponseData;
    }

    public void setResponseData(String responseData) {
        this.mResponseData = responseData;
    }

    public String getCallbackId() {
        return mCallbackId;
    }

    public void setCallbackId(String callbackId) {
        this.mCallbackId = callbackId;
    }

    public String getData() {
        return mData;
    }

    public void setData(String data) {
        this.mData = data;
    }

    public String getEventName() {
        return mEventName;
    }

    public void setEventName(String eventName) {
        this.mEventName = eventName;
    }

    public String toJsonString() {
        return new Gson().toJson(this);
    }

}
