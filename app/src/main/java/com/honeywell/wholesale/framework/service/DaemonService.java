package com.honeywell.wholesale.framework.service;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.wearable.MessageEvent;
import com.honeywell.hybridapp.framework.log.LogHelper;
import com.honeywell.wholesale.framework.event.BaseScanEvents;
import com.honeywell.wholesale.framework.event.ScanEvent;
import com.honeywell.wholesale.framework.event.ScannerToTransactionEvent;
import com.honeywell.wholesale.framework.model.AccountManager;
import com.honeywell.wholesale.framework.model.WareHouseManager;
import com.honeywell.wholesale.framework.scanner.ScanerRespManager;
import com.honeywell.wholesale.framework.scanner.ScannerManager;
import com.honeywell.wholesale.ui.login.SplashActivity;
import com.honeywell.wholesale.ui.scan.ProductDetailActivity;


import java.util.List;

import de.greenrobot.event.EventBus;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.L;

/**
 * Created by H155935 on 16/5/13.
 * Email: xiaofei.he@honeywell.com
 */
public class DaemonService extends Service {

    private static final String TAG = "DaemonService";

    private static final String SCAN_JSON_KEY = "SCAN_JSON_KEY";

    private ScannerManager mScannerManager;

    private ScanerRespManager scanerRespManager;

    private Context context;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e("=========", "***** DaemonService *****: onCreate");
        EventBus.getDefault().register(this);
        context = this;
        mScannerManager = ScannerManager.getInstance();
        mScannerManager.initScannerComponent(context);

        scanerRespManager = ScanerRespManager.getInstance();
//        mScannerManager.initScannerComponent(context);
    }

    @Override
    public void onDestroy() {
        Log.e(TAG, "service onDestroy");
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @SuppressWarnings("unused")
    public void onEventMainThread(BaseScanEvents event) {
        if (event instanceof ScanEvent) {
            ScanEvent scanEvent = (ScanEvent) event;

            if (scanEvent.getType() == BaseScanEvents.ScanEventType.SCANNER_RESULT_EVENT) {
                if (scanEvent.getIsSuccessed()) {
                    String barcode = scanEvent.getEventData();
                    showScanResultActivity(barcode);
                }
            }
        }
    }

    private void showScanResultActivity(String barcode) {
        LogHelper.getInstance().d(TAG, "showScanResultActivity");

//        AppProcessInfo.AppInfo appInfo = AppProcessInfo.isAppOnForeground(context);
//        if (appInfo == AppProcessInfo.AppInfo.APP_DEAD){
//            Intent intent = new Intent(this, SplashActivity.class);
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            Bundle bundle = new Bundle();
//            bundle.putString(SCAN_JSON_KEY, jsonString);
//            intent.putExtras(bundle);
//            startActivity(intent);
//        }

//        if (appInfo == AppProcessInfo.AppInfo.APP_BACKGROUND){
//            Intent intent = new Intent();
//            intent.setClass(getApplicationContext(), MainActivity.class);
//
//            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
//            Bundle bundle = new Bundle();
//            bundle.putString(SCAN_JSON_KEY, jsonString);
//            intent.putExtras(bundle);
//            startActivity(intent);
//
////            final ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
////            final List<ActivityManager.RunningTaskInfo> recentTasks = activityManager.getRunningTasks(Integer.MAX_VALUE);
////
////            for (int i = 0; i < recentTasks.size(); i++) {
////                if (recentTasks.get(i).baseActivity.toShortString().contains("com.honeywell.wholesale.ui.DashboardActivity")) {
////                    Log.e(TAG, "Move to front");
////                    Log.e(TAG, String.valueOf(recentTasks.get(i).id));
////                    activityManager.moveTaskToFront(recentTasks.get(i).id, ActivityManager.MOVE_TASK_WITH_HOME);
////                }
////            }
//        }

//        if (appInfo == AppProcessInfo.AppInfo.APP_FOREGROUND){
//            ScanEvent scanEvent = new ScanEvent(BaseScanEvents.ScanEventType.SCANNER_RESULT_TOUI_EVENT, true, jsonString);
//            EventBus.getDefault().post(scanEvent);

        if (AccountManager.getInstance().isLogin()) {
            int productId = -1;
            LogHelper.getInstance().d(TAG, "showScanResultActivity1");
            LogHelper.getInstance().d(TAG, "showScanResultActivity1" + scanerRespManager.getType().toString());
            if (scanerRespManager.getType().equals(ScanerRespManager.ScanerRespType.SCANNER_RESP_DEFAULT)) {
                if (isActivityRunning()) {
                    ProductDetailActivity.mProductDetailActivityInstance.finish();
                }

                Intent intent = new Intent(getApplicationContext(), ProductDetailActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra(ProductDetailActivity.INTENT_KEY_SCAN_RESULT, barcode);
                intent.putExtra(ProductDetailActivity.INTENT_KEY_PRODUCT_ID, productId);
                startActivity(intent);
            } else if (scanerRespManager.getType().equals(ScanerRespManager.ScanerRespType.SCANER_RESP_SEARCH)){
                EventBus.getDefault().post(new ScannerToTransactionEvent(barcode));
            } else if (scanerRespManager.getType().equals(ScanerRespManager.ScanerRespType.SCANER_RESP_INVENTORY)){
                if (isActivityRunning()) {
                    ProductDetailActivity.mProductDetailActivityInstance.finish();
                }

                int warehouseId;
                String warehouseName;
                warehouseId = WareHouseManager.getInstance().getScanWareHouseId();
                warehouseName = WareHouseManager.getInstance().getScanWareHouseName();


                Log.e("adadada",String.valueOf(warehouseId) + "---" + warehouseName);

                Intent intent = new Intent(getApplicationContext(), ProductDetailActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("warehouse_id", warehouseId);
                intent.putExtra(ProductDetailActivity.INTENT_KEY_SCAN_RESULT, barcode);
                intent.putExtra(ProductDetailActivity.INTENT_KEY_PRODUCT_ID, productId);
                startActivity(intent);
            } else if (scanerRespManager.getType().equals(ScanerRespManager.ScanerRespType.SCANER_NULL_TYPE)){

            }
        } else {
            Intent intent = new Intent(getApplicationContext(), SplashActivity.class);
            intent.setFlags(
                    Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }

    }

    public boolean isActivityRunning() {

        ActivityManager am = (ActivityManager) this.getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(Integer.MAX_VALUE);
        boolean isActivityFound = false;
        for (int i = 0; i < taskInfo.size(); i++) {
            if (taskInfo.get(i).topActivity.toString().equalsIgnoreCase("ComponentInfo{com.honeywell.wholesale/com.honeywell.wholesale.ui.scan.ProductDetailActivity}")) {
                isActivityFound = true;
            }
        }
        return isActivityFound;
    }

}
