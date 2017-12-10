package com.honeywell.wholesale.framework.network;

import com.google.gson.annotations.SerializedName;

/**
 * Created by xiaofei on 3/27/17.
 *
 */

public class HttpResult<T> {

    @SerializedName("retcode")
    private int resultCode;

    @SerializedName("message")
    private String resultMessage;

    @SerializedName("retbody")
    private T data;

    public int getResultCode() {
        return resultCode;
    }

    public T getData() {
        return data;
    }

    public String getResultMessage() {
        return resultMessage;
    }
}
