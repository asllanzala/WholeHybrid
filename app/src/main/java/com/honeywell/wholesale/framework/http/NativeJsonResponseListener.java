package com.honeywell.wholesale.framework.http;

/**
 * Created by xiaofei on 7/22/16.
 *
 */
public interface NativeJsonResponseListener<T> {
    void listener (T t);
    void errorListener(String s);
}
