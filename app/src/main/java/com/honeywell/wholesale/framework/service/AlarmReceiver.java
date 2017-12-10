package com.honeywell.wholesale.framework.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by H155935 on 16/5/13.
 * Email: xiaofei.he@honeywell.com
 */
public class AlarmReceiver extends BroadcastReceiver {
    private static final String ALARM_ACTION = "honeywell.alarm.action";

    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getAction().equals(ALARM_ACTION)) {
            Intent i = new Intent();
            i.setClass(context, DaemonService.class);
            context.startService(i);
        }
    }
}
