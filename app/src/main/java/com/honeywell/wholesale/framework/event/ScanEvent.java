package com.honeywell.wholesale.framework.event;

import android.support.annotation.Nullable;

/**
 * Created by H155935 on 16/5/12.
 * Email: xiaofei.he@honeywell.com
 */
public class ScanEvent extends BaseScanEvents {
    private ScanEventType type;
    private Boolean isSuccessed = false;
    private String mEventData;

    public ScanEvent(ScanEventType type, @Nullable Boolean isSuccessed){
        this.type = type;
        this.isSuccessed = isSuccessed;
    }

    public ScanEvent(ScanEventType type, @Nullable Boolean isSuccessed, @Nullable String mEventData){
        this.type = type;
        this.isSuccessed = isSuccessed;
        this.mEventData = mEventData;
    }

    public Boolean getIsSuccessed() {
        return isSuccessed;
    }

    public ScanEventType getType() {
        return type;
    }

    public String getEventData(){
        return mEventData;
    }

}
