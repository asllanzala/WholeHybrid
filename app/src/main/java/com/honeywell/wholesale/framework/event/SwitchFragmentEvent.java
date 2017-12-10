package com.honeywell.wholesale.framework.event;

/**
 * Created by H154326 on 16/11/13.
 * Email: yang.liu6@honeywell.com
 */

public class SwitchFragmentEvent {

    private String mMsg;

    public SwitchFragmentEvent(String msg) {
        this.mMsg = msg;
    }

    public String getmMsg() {
        return mMsg;
    }
}
