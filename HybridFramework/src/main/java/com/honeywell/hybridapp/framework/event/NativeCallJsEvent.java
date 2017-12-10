package com.honeywell.hybridapp.framework.event;

/**
 * Created by e887272 on 6/30/16.
 */
public class NativeCallJsEvent {

    private String mEventName;

    private String mEventJsonStr;

    public NativeCallJsEvent(String eventName, String eventJsonStr) {
        mEventName = eventName;
        mEventJsonStr = eventJsonStr;
    }

    public String getEventJsonStr() {
        return mEventJsonStr;
    }

    public void setEventJsonStr(String eventJsonStr) {
        mEventJsonStr = eventJsonStr;
    }

    public String getEventName() {
        return mEventName;
    }

    public void setEventName(String eventName) {
        mEventName = eventName;
    }
}
