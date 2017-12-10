package com.honeywell.wholesale.framework.hybrid;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

/**
 * Created by e887272 on 6/13/16.
 */
public class HttpRequestParams implements IHttpRequestParams{

    // urlMethod: 'reg/boss/register', apiMethod: 'POST',requestBody

    @SerializedName("urlMethod")
    private String mUrlMethod;

    @SerializedName("apiMethod")
    private String mApiMethod;

    @SerializedName("requestBody")
    private String mRequestBody;

    public String getUrlMethod() {
        return mUrlMethod;
    }

    public void setUrlMethod(String urlMethod) {
        mUrlMethod = urlMethod;
    }

    public String getRequestBody() {
        return mRequestBody;
    }

    public void setRequestBody(String requestBody) {
        mRequestBody = requestBody;
    }

    public String getApiMethod() {
        return mApiMethod;
    }

    public void setApiMethod(String apiMethod) {
        mApiMethod = apiMethod;
    }

    @Override
    public String getJsonString() {
        return new Gson().toJson(this);
    }


}
