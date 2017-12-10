package com.honeywell.wholesale.framework.application;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.multidex.MultiDexApplication;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HttpStack;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.security.ProviderInstaller;
import com.honeywell.lib.application.CrashHandler;
import com.honeywell.lib.application.LogcatHelper;
import com.honeywell.wholesale.framework.db.DaoMaster;
import com.honeywell.wholesale.framework.db.DaoSession;
import com.honeywell.wholesale.framework.database.DataBaseHelper;
import com.honeywell.wholesale.framework.http.TLSSocketFactory;
import com.honeywell.wholesale.framework.scanner.ScanerRespManager;
import com.honeywell.wholesale.framework.service.DaemonService;
//import com.qualcomm.qcnvitems.QcNvItemIds;
//import com.qualcomm.qcrilhook.QcRilHook;
//import com.qualcomm.qcrilhook.QcRilHookCallback;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

/**
 * Created by H155935 on 16/5/11.
 * Email: xiaofei.he@honeywell.com
 */
public class WholesaleApplication extends MultiDexApplication {

    private static Context context;

    private static boolean activityVisible = false;

    private static RequestQueue mRequestQueue;
    private static RequestQueue imageLoadRequestQueue;

    private static String currentCustmerId;

    public ScanerRespManager scanerRespManager;

    private static WholesaleApplication mWholesaleApplication = null;

    public static WholesaleApplication getInstance() {
        return mWholesaleApplication;
    }

    //  Get Pn No
//    private QcRilHook mQcRilOemHook = null;

    private static String pn = "";

//    private QcRilHookCallback mQcrilHookCb = new QcRilHookCallback() {
//        public void onQcRilHookReady() {
//            pn = mQcRilOemHook.getDeviceNV(QcNvItemIds.NV_OEM_ITEM_4_I);
//            Toast.makeText(getApplicationContext(), "pn is :" + pn, Toast.LENGTH_LONG).show();
//        }
//    };

//    private void getPnNo(){
//        mQcRilOemHook = new QcRilHook(mWholesaleApplication, mQcrilHookCb);
//    }


    public void onCreate() {
        super.onCreate();
        mWholesaleApplication = this;
//        CrashHandler.getInstance().init(this);
//        LogcatHelper.getInstance(this).start();
        WholesaleApplication.context = getApplicationContext();
        DataBaseHelper.getInstance(context);
        scanerRespManager = ScanerRespManager.getInstance();
        Log.e("WholesaleApplication", "WholesaleApplication1");
        scanerRespManager.setType(ScanerRespManager.ScanerRespType.SCANER_NULL_TYPE);
        startScanService();
        try{
            ProviderInstaller.installIfNeeded(this);
        }catch (Exception e){
            e.printStackTrace();
        }

        //  Get Pn No
        //getPnNo();
    }

    public static boolean isActivityVisible() {
        return activityVisible;
    }

    public static void activityResumed() {
        activityVisible = true;
    }

    public static void activityPaused() {
        activityVisible = false;
    }


    public static Context getAppContext() {
        return WholesaleApplication.context;
    }

    public static RequestQueue getRequestQueue() {
        if (mRequestQueue == null){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN
                    && Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
                HttpStack stack = null;
                try {
                    stack = new HurlStack(null, new TLSSocketFactory());
                } catch (KeyManagementException e) {
                    e.printStackTrace();
                    Log.d("Your Wrapper Class", "Could not create new stack for TLS v1.2");
                    stack = new HurlStack();
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                    Log.d("Your Wrapper Class", "Could not create new stack for TLS v1.2");
                    stack = new HurlStack();
                }
                mRequestQueue = Volley.newRequestQueue(WholesaleApplication.context, stack);
            } else {
                mRequestQueue = Volley.newRequestQueue(WholesaleApplication.context);
            }
        }

        return mRequestQueue;
    }

    public static void cancelAllRequest(){
        if (mRequestQueue == null){
            return;
        }
        mRequestQueue.cancelAll(new RequestQueue.RequestFilter() {
            @Override
            public boolean apply(Request<?> request) {
                return true;
            }
        });
    }


    public static RequestQueue getImageLoadRequestQueue(){
        if (imageLoadRequestQueue == null){
            Cache cache = new DiskBasedCache(context.getCacheDir(), 20 * 1024 * 1024);
            HttpStack stack = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN
                    && Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
                try {
                    stack = new HurlStack(null, new TLSSocketFactory());
                } catch (KeyManagementException e) {
                    e.printStackTrace();
                    Log.d("Your Wrapper Class", "Could not create new stack for TLS v1.2");
                    stack = new HurlStack();
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                    Log.d("Your Wrapper Class", "Could not create new stack for TLS v1.2");
                    stack = new HurlStack();
                }
            } else {
                stack = new HurlStack();
            }

            Network network = new BasicNetwork(stack);
            imageLoadRequestQueue = new RequestQueue(cache, network);
            imageLoadRequestQueue.start();
        }
        return imageLoadRequestQueue;
    }

    private void startScanService() {
        Intent intent = new Intent();
        intent.setClass(this, DaemonService.class);
        startService(intent);
    }

    public static String getCurrentCustmerId() {
        return currentCustmerId;
    }

    public static void setCurrentCustmerId(String currentCustmerId) {
        WholesaleApplication.currentCustmerId = currentCustmerId;
    }

    public static String getPn() {
        return pn;
    }

    private void initDB(){
        DaoMaster.DevOpenHelper devOpenHelper = new DaoMaster.DevOpenHelper(getApplicationContext(), "wholesale.db", null);
        DaoMaster daoMaster = new DaoMaster(devOpenHelper.getWritableDb());
        DaoSession daoSession = daoMaster.newSession();

    }

}
