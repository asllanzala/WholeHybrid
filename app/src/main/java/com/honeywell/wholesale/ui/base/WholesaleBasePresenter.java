package com.honeywell.wholesale.ui.base;

/**
 * Created by liuyang on 2017/7/3.
 */

public abstract class WholesaleBasePresenter<T extends WholesaleBaseView>{
    public T mView;
   public WholesaleBasePresenter(T mView){
        this.mView = mView;
    }
    public void detach(){
        this.mView = null;
    }
}
