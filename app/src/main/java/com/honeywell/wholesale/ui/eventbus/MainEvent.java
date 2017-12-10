package com.honeywell.wholesale.ui.eventbus;

/**
 * Created by milton_lin on 17/5/8.
 */

public class MainEvent {
    public static final int REFRESH_HEAD_PORTRAIT = 1;
    private int mMessage;

    public MainEvent(int msg) {
        mMessage = msg;
    }

    public int getMessage() {
        return mMessage;
    }

    public void setMessage(int message) {
        this.mMessage = message;
    }
}
