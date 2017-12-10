package com.honeywell.wholesale.framework.event;

/**
 * Created by liuyang on 2017/6/8.
 */

public class SaleReturnEvent {
    private String msg;

    public SaleReturnEvent(String msg) {//事件传递参数
        this.msg = msg;
    }

    public String getMsg() {//取出事件参数
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
