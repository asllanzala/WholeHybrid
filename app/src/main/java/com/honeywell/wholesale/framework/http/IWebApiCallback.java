package com.honeywell.wholesale.framework.http;

/**
 * Created by e887272 on 9/7/16.
 */

public interface IWebApiCallback {

    public void onSuccessCallback(Object obj);
    public void onErrorCallback(Object obj);

}
