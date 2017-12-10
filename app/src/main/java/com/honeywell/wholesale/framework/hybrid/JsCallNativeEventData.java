package com.honeywell.wholesale.framework.hybrid;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

/**
 * Created by e887272 on 6/15/16.
 */
public class JsCallNativeEventData {

    // var data = {method: 100001, data: "this is a logout method json body"};
    @SerializedName("method")
    private String mNativeMethod;

    @SerializedName("data")
    private String mData;

    public String getNativeMethod() {
        return mNativeMethod;
    }

    public void setNativeMethod(String nativeMethod) {
        mNativeMethod = nativeMethod;
    }

    public String getData() {
        return mData;
    }

    public void setData(String data) {
        mData = data;
    }

    public String getJsonString() {
        return (new Gson()).toJson(this);
    }


}
