package com.honeywell.wholesale.framework.event;

/**
 * Created by H154326 on 17/1/18.
 * Email: yang.liu6@honeywell.com
 */

public class PayRIghtNowEvent {
    private String mMsg;

    public PayRIghtNowEvent(String mMsg) {
        this.mMsg = mMsg;
    }

    public String getmMsg() {
        return mMsg;
    }

    public void setmMsg(String mMsg) {
        this.mMsg = mMsg;
    }
}
