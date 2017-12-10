package com.honeywell.wholesale.framework.network;

/**
 * Created by xiaofei on 3/27/17.
 *
 */

public interface SubscriberOnNextListener<T> {
    void onNext(T t);
}
