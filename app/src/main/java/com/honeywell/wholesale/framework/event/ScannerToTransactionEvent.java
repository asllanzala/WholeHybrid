package com.honeywell.wholesale.framework.event;

/**
 * Created by H154326 on 17/3/6.
 * Email: yang.liu6@honeywell.com
 */

public class ScannerToTransactionEvent {
    private String msg;

    public ScannerToTransactionEvent(String msg) {//事件传递参数
        this.msg = msg;
    }

    public String getMsg() {//取出事件参数
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
