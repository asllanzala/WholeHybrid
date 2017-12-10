package com.honeywell.wholesale.framework.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.util.Log;

/**
 * Created by H155935 on 16/5/13.
 * Email: xiaofei.he@honeywell.com
 */
public class BootReceiver extends BroadcastReceiver {
    private static final String TAG = "BootReceiver";
    // 5 seconnds
    private static final int TIME_INTERVAL = 5 * 1000;
    private static final String ALARM_ACTION = "honeywell.alarm.action";
    private static final String SCANNER_SERVICE_ACTION = "honeywell.scan.action";

    @Override
    public void onReceive(Context context, Intent intent) {

        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            Log.e(TAG, "ACTION_BOOT_COMPLETED");
            sendAlarmBroadcast(context);
        }

        if (SCANNER_SERVICE_ACTION.equals(intent.getAction())) {
            Log.e(TAG, "SCANNER_SERVICE_ACTION");
            sendAlarmBroadcast(context);
        }
    }

    private void sendAlarmBroadcast(Context context){

        Intent actionIntent = new Intent(context, AlarmReceiver.class);
        actionIntent.setAction(ALARM_ACTION);
        PendingIntent sender = PendingIntent.getBroadcast(context, 0,
                actionIntent, 0);
        long firsttime = SystemClock.elapsedRealtime();
        AlarmManager am = (AlarmManager) context
                .getSystemService(Context.ALARM_SERVICE);

        am.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, firsttime,
                TIME_INTERVAL, sender);
    }

}
