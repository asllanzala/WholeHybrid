package com.honeywell.wholesale.framework.scanner;

import android.content.Context;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.google.gson.Gson;
import com.honeywell.aidc.AidcManager;
import com.honeywell.aidc.BarcodeFailureEvent;
import com.honeywell.aidc.BarcodeReadEvent;
import com.honeywell.aidc.BarcodeReader;
import com.honeywell.aidc.BarcodeReader.BarcodeListener;
import com.honeywell.aidc.BarcodeReader.TriggerListener;
import com.honeywell.aidc.ScannerNotClaimedException;
import com.honeywell.aidc.ScannerUnavailableException;
import com.honeywell.aidc.TriggerStateChangeEvent;
import com.honeywell.aidc.UnsupportedPropertyException;
import com.honeywell.hybridapp.framework.log.LogHelper;
import com.honeywell.wholesale.framework.event.BaseScanEvents;
import com.honeywell.wholesale.framework.event.ScanEvent;
import com.honeywell.wholesale.lib.util.StringUtil;

import java.util.HashMap;
import java.util.Map;

import de.greenrobot.event.EventBus;

/**
 * Created by allanhwmac on 16/5/9.
 *
 */
public class ScannerManager {
    private static String TAG = "ScannerManager";
    private static ScannerManager instance = null;

    private BarcodeReader barcodeReader;
    private AidcManager mAidcManager;

    private ScannerManager() {
        EventBus.getDefault().register(this);
    }

    public void initScannerComponent(Context context){
        if (context == null){
            closeScan();
            return;
        }

        AidcManager.create(context, new AidcManager.CreatedCallback() {
            @Override
            public void onCreated(AidcManager aidcManager) {
                mAidcManager = aidcManager;
                barcodeReader = mAidcManager.createBarcodeReader();

                LogHelper.getInstance().v(TAG, "Scan SCANNER_INITIAL_EVENT");

                EventBus.getDefault().post(new ScanEvent(BaseScanEvents.ScanEventType.SCANNER_INITIAL_EVENT, true));
            }
        });
    }


    public static ScannerManager getInstance() {
        if (instance == null){
            instance = new ScannerManager();
        }
        return instance;
    }

    public void closeScan(){
        if (barcodeReader != null) {
            // close BarcodeReader to clean up resources.
            barcodeReader.close();
            barcodeReader = null;
        }

        if (mAidcManager != null) {
            mAidcManager.close();
        }
        EventBus.getDefault().unregister(this);
        instance = null;
    }

    public boolean initForScan(){

        if (barcodeReader != null){
            try {
                // prepare for scanner
                barcodeReader.claim();
            } catch (ScannerUnavailableException e) {
                e.printStackTrace();
                closeScan();
                return false;
            }

            barcodeReader.addBarcodeListener(barcodeListener);

            try {
                barcodeReader.setProperty(
                        BarcodeReader.PROPERTY_TRIGGER_CONTROL_MODE,
                        BarcodeReader.TRIGGER_CONTROL_MODE_CLIENT_CONTROL);
            } catch (UnsupportedPropertyException e) {
                e.printStackTrace();
                closeScan();
                return false;
            }

            barcodeReader.addTriggerListener(triggerListener);
            Map<String, Object> properties = new HashMap<>();

            // Set Symbologies On/Off
            properties.put(BarcodeReader.PROPERTY_CODE_128_ENABLED, true);
            properties.put(BarcodeReader.PROPERTY_GS1_128_ENABLED, true);
            properties.put(BarcodeReader.PROPERTY_QR_CODE_ENABLED, true);
            properties.put(BarcodeReader.PROPERTY_EAN_13_CHECK_DIGIT_TRANSMIT_ENABLED, true);
            properties.put(BarcodeReader.PROPERTY_EAN_8_CHECK_DIGIT_TRANSMIT_ENABLED, true);
            properties.put(BarcodeReader.PROPERTY_UPC_E_CHECK_DIGIT_TRANSMIT_ENABLED, true);
            properties.put(BarcodeReader.PROPERTY_POSTAL_2D_POSTNET_CHECK_DIGIT_TRANSMIT_ENABLED, true);
            properties.put(BarcodeReader.PROPERTY_POSTAL_2D_PLANET_CHECK_DIGIT_TRANSMIT_ENABLED, true);

            barcodeReader.setProperties(properties);

            return true;
        }
        return false;
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            try {
                barcodeReader.aim(true);
                barcodeReader.light(true);
                barcodeReader.decode(true);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    public View.OnTouchListener getOnTouchListener() {
        return onTouchListener;
    }

    public void scanButtonOnActionDown(){
        try {
            barcodeReader.aim(true);
            barcodeReader.light(true);
            barcodeReader.decode(true);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void scanButtonOnActionUp(){
        try {
            barcodeReader.aim(false);
            barcodeReader.light(false);
            barcodeReader.decode(false);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private View.OnTouchListener onTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()){
                case MotionEvent.ACTION_DOWN:
                    try {
                        barcodeReader.aim(true);
                        barcodeReader.light(true);
                        barcodeReader.decode(true);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    try {
                        barcodeReader.aim(false);
                        barcodeReader.light(false);
                        barcodeReader.decode(false);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
            }
            return false;
        }
    };


    private BarcodeListener barcodeListener = new BarcodeListener() {
        @Override
        public void onBarcodeEvent(BarcodeReadEvent barcodeReadEvent) {
//            Gson gson = new Gson();
//            String json = gson.toJson(barcodeReadEvent);
//            JSONObject jsonObject = null;
//
//            try {
//                jsonObject = new JSONObject(json);
//                jsonObject.put("mResult", "success");
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//
//            if (jsonObject != null){
//                ScanEvent scanEvent = new ScanEvent(BaseScanEvents.ScanEventType.SCANNER_RESULT_EVENT, true, jsonObject.toString());
//                EventBus.getDefault().post(scanEvent);
//            }

            if(barcodeReadEvent != null) {
                String barCodeData = barcodeReadEvent.getBarcodeData();
                if(!StringUtil.isEmpty(barCodeData)) {
                    ScanEvent scanEvent = new ScanEvent(BaseScanEvents.ScanEventType.SCANNER_RESULT_EVENT, true, barCodeData);
                    EventBus.getDefault().post(scanEvent);
                    return;
                }
            }

            Gson gson = new Gson();
            String json = gson.toJson(barcodeReadEvent);
            Log.w(TAG, "扫码结果不能正确解析。json=" + json);

        }

        @Override
        public void onFailureEvent(BarcodeFailureEvent barcodeFailureEvent) {

//            JSONObject jsonObject = null;
//            try {
//                jsonObject = new JSONObject();
//                jsonObject.put("mResult", "fail");
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//
//            ScanEvent scanEvent = new ScanEvent(BaseScanEvents.ScanEventType.SCANNER_RESULT_EVENT, false, jsonObject.toString());
//            EventBus.getDefault().post(scanEvent);

            LogHelper.getInstance().w(TAG, "Scan barcode failed!");

            // TODO 扫码失败，什么都不需要做

        }
    };

    private TriggerListener triggerListener = new TriggerListener() {
        @Override
        public void onTriggerEvent(TriggerStateChangeEvent triggerStateChangeEvent) {
            try {
                // only handle trigger presses
                // turn on/off aimer, illumination and decoding
                barcodeReader.aim(triggerStateChangeEvent.getState());
                barcodeReader.light(triggerStateChangeEvent.getState());
                barcodeReader.decode(triggerStateChangeEvent.getState());

            } catch (ScannerNotClaimedException e) {
                e.printStackTrace();
            } catch (ScannerUnavailableException e) {
                e.printStackTrace();
                Log.e(TAG, "ScannerUnavailableException");
            }
        }
    };


    public void showScanPage() {

    }

    public void getScanResult() {

    }

    @SuppressWarnings("unused")
    public void onEventMainThread(BaseScanEvents event){
        if (event instanceof ScanEvent){
            ScanEvent scanEvent = (ScanEvent)event;

            // initial scanner
            if (scanEvent.getType() == BaseScanEvents.ScanEventType.SCANNER_INITIAL_EVENT){
                if (scanEvent.getIsSuccessed() && initForScan()){
                    LogHelper.getInstance().v(TAG, "Init done");
                }else {
                    LogHelper.getInstance().e(TAG, "Init scanner failed!!!");
                    closeScan();
                }
            }
        }
    }

}
