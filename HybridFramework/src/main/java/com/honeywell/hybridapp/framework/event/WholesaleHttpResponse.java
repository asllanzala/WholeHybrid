package com.honeywell.hybridapp.framework.event;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import org.json.JSONObject;

/**
 * 正常服务器返回结构：
 * {"retcode":4003,"message":"the number not register","retbody":{}}
 */
public class WholesaleHttpResponse {

    @SerializedName("retcode")
    private int mResponseCode;

    @SerializedName("message")
    private String mMessage;

    @SerializedName("retbody")
    private JSONObject mResponseBody;

    public WholesaleHttpResponse(int responseCode, String msg, JSONObject responseJson){
        mResponseCode = responseCode;
        mMessage = msg;
        if(responseJson == null) {
            mResponseBody = new JSONObject();
        } else {
            mResponseBody = responseJson;
        }
    }

    public int getResponseCode() {
        return mResponseCode;
    }

    public void setResponseCode(int responseCode) {
        mResponseCode = responseCode;
    }

    public JSONObject getResponseBody() {
        return mResponseBody;
    }

    public void setResponseBody(JSONObject responseBody) {
        mResponseBody = responseBody;
    }

    public String getMessage() {
        return mMessage;
    }

    public void setMessage(String message) {
        mMessage = message;
    }

    public String getJsonString() {
        return (new Gson()).toJson(this);
    }

    @Override
    public String toString() {
        return getJsonString();
    }

    /**
     * json字符串转成对象
     * @param jsonStr
     * @return
     */
    public static WholesaleHttpResponse fromJson(String jsonStr) {
        Gson gson = new Gson();
        return gson.fromJson(jsonStr, WholesaleHttpResponse.class);
    }

}
