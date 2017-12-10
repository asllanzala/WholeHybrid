package com.honeywell.wholesale.framework.service;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.honeywell.wholesale.framework.utils.FileManager;

import java.io.File;

/**
 * Created by xiaofei on 7/7/16.
 *
 */
public class DownloadReceiver extends BroadcastReceiver {
    private static final String TAG = "DownloadReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        long downloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
        Log.e(TAG, String.valueOf(downloadId));

        if (downloadId == FileManager.getDownloadApkRefId()){
            String filePath = FileManager.getDownloadApkPath();
            Intent i = new Intent(Intent.ACTION_VIEW);
            File file = new File(filePath);
            if (file.length() > 0 && file.exists() && file.isFile()) {
                i.setDataAndType(Uri.parse("file://" + filePath),
                        "application/vnd.android.package-archive");
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(i);
            }
        }
    }
}
