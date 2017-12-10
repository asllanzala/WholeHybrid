package com.honeywell.hybridapp.framework.event;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

/**
 * Created by e887272 on 6/21/16.
 * 数据返回格式为{ack : '1', msg: '执行成功', response : '这里不同的API可以定义不同的返回值'｝;
 * //          {ack : '0', msg: '执行失败，以及具体错误内容，方便调试', response : '这里不同的API可以定义不同的返回值'｝;
 */
public class JsCallNativeEventResponseData {

    public static final int ACK_SUCCESS = 1;
    public static final int ACK_FAILED = 0;


    @SerializedName("ack")
    private int mAck;

    @SerializedName("msg")
    private String mMessage;

    @SerializedName("response")
    private String mResponse;

    public JsCallNativeEventResponseData(int ack, String msg, String responseJson){
        mAck = ack;
        mMessage = msg;
        mResponse = responseJson;
    }

    public int getAck() {
        return mAck;
    }

    public void setAck(int ack) {
        mAck = ack;
    }

    public String getMessage() {
        return mMessage;
    }

    public void setMessage(String message) {
        mMessage = message;
    }

    public String getResponse() {
        return mResponse;
    }

    public void setResponse(String response) {
        mResponse = response;
    }

    public String getJsonString() {
        return (new Gson()).toJson(this);
    }

}
