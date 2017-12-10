package com.honeywell.wholesale.framework.network;

import android.content.Context;
import android.widget.Toast;

import rx.Subscriber;

/**
 * Created by xiaofei on 3/27/17.
 *
 */

public class ProgressSubscriber<T> extends Subscriber<T> {

    private SubscriberOnNextListener onNextListener;

    private Context context;

    public ProgressSubscriber(Context context, SubscriberOnNextListener onNextListener) {
        this.onNextListener = onNextListener;
        this.context = context;
    }

    private void showProgressDialog(){

    }

    private void dismissProgressDialog(){

    }

    @Override
    public void onStart() {
        showProgressDialog();
    }

    @Override
    public void onCompleted() {
        dismissProgressDialog();
    }

    @Override
    public void onError(Throwable e) {
        Toast.makeText(context, "error:" + e.getMessage(), Toast.LENGTH_SHORT).show();
        dismissProgressDialog();
    }

    @Override
    public void onNext(T t) {
        if (onNextListener != null) {
            onNextListener.onNext(t);
        }
    }

}
