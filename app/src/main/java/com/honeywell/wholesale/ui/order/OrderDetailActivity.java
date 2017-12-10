package com.honeywell.wholesale.ui.order;

import com.google.gson.Gson;

import com.honeywell.hybridapp.framework.ICallBack;
import com.honeywell.hybridapp.framework.IHybridCallback;
import com.honeywell.hybridapp.framework.event.Event;
import com.honeywell.hybridapp.framework.event.EventHandler;
import com.honeywell.hybridapp.framework.event.HybridEventBus;
import com.honeywell.hybridapp.framework.event.JsCallNativeEventResponseData;
import com.honeywell.wholesale.framework.http.NativeJsonResponseListener;
import com.honeywell.wholesale.framework.http.WebClient;
import com.honeywell.wholesale.framework.hybrid.H5PageURL;
import com.honeywell.wholesale.framework.hybrid.JsCallNativeEventData;
import com.honeywell.wholesale.framework.hybrid.JsCallNativeEventName;
import com.honeywell.wholesale.framework.hybrid.NativeCallJsEventName;
import com.honeywell.wholesale.framework.model.Account;
import com.honeywell.wholesale.framework.model.AccountManager;
import com.honeywell.wholesale.framework.model.ListItem;
import com.honeywell.wholesale.framework.service.btservice.BluetoothService;
import com.honeywell.wholesale.framework.utils.FileUtil;
import com.honeywell.wholesale.framework.utils.ParseXml;
import com.honeywell.wholesale.framework.utils.XmlUtil;
import com.honeywell.wholesale.lib.util.StringUtil;
import com.honeywell.wholesale.ui.base.CommonWebViewActivity;
import com.honeywell.wholesale.ui.transaction.TransactionDetailRequest;
import com.honeywell.wholesale.ui.uimanager.UIManager;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONObject;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by e887272 on 6/29/16.
 */
public class OrderDetailActivity extends CommonWebViewActivity {


    public static final String TAG = OrderDetailActivity.class.getSimpleName();

    public static final String INTENT_VALUE_ORDER_DETAIL_JSON = "INTENT_VALUE_ORDER_DETAIL_JSON";
    public static final String INTENT_VALUE_CART_DETAIL_JSON = "INTENT_VALUE_CART_DETAIL_JSON";

    private ProgressDialog mProgressDialog ;

    public static BluetoothService mService = null;

    private Context context = OrderDetailActivity.this;
    private List<BluetoothDevice> mBluetoothDevices;
    protected BluetoothDevice mBluetoothDevice = null;
    private int blueDeivceIndex = -1;
    private MyHandler myHandler;

    private JSONObject jsonResult;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        Log.d(TAG, "============== :" + "=========="+"初始化Dialog"+ mProgressDialog);
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setTitle("");
        mProgressDialog.setMessage("加载中...");
        mProgressDialog.setCancelable(true);
        mProgressDialog.setCanceledOnTouchOutside(false);

        if (intent != null) {
            String jsonData = intent.getStringExtra(INTENT_VALUE_ORDER_DETAIL_JSON);
//            mProgressDialog.show();
            sendDataToWebview(jsonData);
        }

        initService();
    }



    @Override
    public String getUrl() {
        return H5PageURL.URL_ORDER_DETAIL_PAGE;
    }

    void initService() {

        myHandler = new MyHandler(mProgressDialog);
        mService = BluetoothService.getInstance(this, myHandler);
    }



//    private PrinterCallBack printerCallBack = new PrinterCallBack() {
//        @Override
//        public void onResponse(String response) {
//
//        }
//
//        @Override
//        public void onConnectionStateChanged(int state) {
//
//        }
//
//        @Override
//        public void onBluetoothStateChanged(int state) {
//
//        }
//
//        @Override
//        public void onBluetoothConnectFailed() {
//
//        }
//
//        @Override
//        public void onPrinterSuccess() {
//            Log.d(TAG, "蓝牙调试 :" + "打印成功"+ mProgressDialog);
//            dismissDialog();
////                    Log.d(TAG, "蓝牙调试 :" + "打印成功");
////            Toast.makeText(OrderDetailActivity.this, "打印成功", Toast.LENGTH_LONG).show();
//        }
//
//        @Override
//        public void onConnected(String deviceName, String deviceAddress) {
//
//            Log.e(TAG, "获取订单详情" + jsonResult);
//            printer(jsonResult);
//        }
//
//        @Override
//        public void onError(int type, String detail) {
//
//        }
//    };

    private class MyHandler extends Handler {

        private ProgressDialog mProgressDialog ;
        public MyHandler(ProgressDialog mProgressDialog) {
            this.mProgressDialog = mProgressDialog;
        }

        private JSONObject jsonResult1;

        public void setJsonResult(JSONObject jsonObject) {
            jsonResult1 = jsonObject;
            Log.e(TAG, "获取订单详情" + jsonResult1);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case BluetoothService.MESSAGE_STATE_CHANGE:
                    switch (msg.arg1) {
                        case BluetoothService.STATE_CONNECTED:   //已连接
                            Log.d(TAG, "蓝牙调试: " + "连接成功");


                            Log.e(TAG, "获取订单详情" + jsonResult1);
                            printer(jsonResult1);
                            break;
                        case BluetoothService.STATE_CONNECTING:  //正在连接
                            Log.d(TAG, "蓝牙调试: " + "正在连接.....");
                            break;
                        case BluetoothService.STATE_LISTEN:     //监听连接的到来
                        case BluetoothService.STATE_NONE:
                            Log.d(TAG, "蓝牙调试: " + "等待连接.....");
                            break;
                    }
                    break;
                case BluetoothService.MESSAGE_CONNECTION_LOST:    //蓝牙已断开连接
                    Log.d(TAG, "蓝牙调试: " + "断开连接.....");
                    break;
                case BluetoothService.MESSAGE_UNABLE_CONNECT:     //无法连接设备
                    Log.d(TAG, "蓝牙调试:" + "无法连接.....");
                    startConnectBT();
                    break;
                case BluetoothService.MESSAGE_READ:
                    ByteBuffer byteBuffer = (ByteBuffer) msg.obj;
                    byte[] bs = byteBuffer.array();
                    break;
                case BluetoothService.MESSAGE_WRITE:
                    Log.d(TAG, "蓝牙调试 :" + "发送指令给打印机");
                    break;
                case BluetoothService.PRINTER_SUCCESS:
                    Log.d(TAG, "蓝牙调试 :" + "打印成功"+ mProgressDialog);
                    dismissDialog();
//                    Log.d(TAG, "蓝牙调试 :" + "打印成功");
                    Toast.makeText(OrderDetailActivity.this, "打印成功", Toast.LENGTH_LONG).show();
                    break;
                case ParseXml.RequestType.REQUEST_SETLOCALIZATION:
//                    Toast.makeText(context, msg.obj.toString(), Toast.LENGTH_SHORT).show();
                    break;
            }

        }
    }

    public static BluetoothService getBluetoothService() {
        return mService;
    }

    public JSONObject get() {
        return jsonResult;
    }

    private void printer(JSONObject jsonObject) {
        List<ListItem> listItems = new ArrayList<ListItem>();
//        String string = "";
        try {
//            JSONObject jsonObject = new JSONObject(string);
            listItems = FileUtil.getListItem(jsonObject);
            Log.e("=====", "" + listItems.size());
            XmlUtil.printSendFileToPrinter(this, FileUtil.getSourceFile2(this, listItems));
        } catch (Exception e) {
            Toast.makeText(OrderDetailActivity.this, "打印失败", Toast.LENGTH_LONG).show();

            dismissDialog();
            e.printStackTrace();
        }


    }

    private void getBTList() {
        Set<BluetoothDevice> set = getBluetoothService().getPairedDev();
        mBluetoothDevices = new ArrayList<>();
        for (BluetoothDevice bluetoothDevice : set) {
            if (bluetoothDevice.getBluetoothClass().getMajorDeviceClass() == 1536) {
                mBluetoothDevices.add(bluetoothDevice);
            }
        }
        Log.e("====size====", "" + mBluetoothDevices.size());
        if (mBluetoothDevices.size() == 0) {
            dismissDialog();
            Toast.makeText(this, "请连接打印机", Toast.LENGTH_LONG).show();
            return;
        }
        blueDeivceIndex = -1;
        //对打印机进行连接
        startConnectBT();
    }

    private void startConnectBT() {
        int size = mBluetoothDevices.size();
        blueDeivceIndex = blueDeivceIndex + 1;

        if (blueDeivceIndex > (size - 1)) {

            dismissDialog();
            Toast.makeText(this, "打印机连接失败", Toast.LENGTH_LONG).show();
            return;
        }
        mBluetoothDevice = mBluetoothDevices.get(blueDeivceIndex);
        Log.e("========", "" + mBluetoothDevice.getName());
        getBluetoothService().connect(mBluetoothDevice);
    }


    @Override
    protected void initWebView() {
        super.initWebView();

        HybridEventBus hybridEventBus = getWebView().getHybridAppTunnel().getHybridEventBus();
        hybridEventBus.registerEventHandler(
                JsCallNativeEventName.EVENT_EXECUTE_NATIVE_METHOD,
                new EventHandler() {
                    @Override
                    public void handle(Event event, IHybridCallback callBack) {
                        if (StringUtil.isEmpty(event.getData())) {
                            Log.w(TAG, JsCallNativeEventName.EVENT_EXECUTE_NATIVE_METHOD
                                    + ", event data is null.");
                            JsCallNativeEventResponseData responseData
                                    = new JsCallNativeEventResponseData(
                                    JsCallNativeEventResponseData.ACK_SUCCESS,
                                    "", JsCallNativeEventName.EVENT_EXECUTE_NATIVE_METHOD
                                    + " is executed in Java");
                            callBack.onCallBack(responseData);
                        }

                        Log.i(TAG, "Receive event from H5 = " + event.getData());

                        JsCallNativeEventData jsCallNativeEventData = new Gson()
                                .fromJson(event.getData(), JsCallNativeEventData.class);

                        int nativeMethod = Integer
                                .parseInt(jsCallNativeEventData.getNativeMethod());
                        JsCallNativeEventResponseData responseData = null;

                        switch (nativeMethod) {
                            case JsCallNativeEventName.NATIVE_METHOD_GET_USER_INFO:
                                Account account = AccountManager.getInstance().getCurrentAccount();
                                responseData = new JsCallNativeEventResponseData(
                                        JsCallNativeEventResponseData.ACK_SUCCESS,
                                        "", account.getJsonString());

                                Log.e(TAG, "执行回调：" + responseData.toString());
                                callBack.onCallBack(responseData);
                                break;

                            case JsCallNativeEventName.NATIVE_METHOD_BLUETOOTH_PRINTER:


                                Log.e(TAG, "执行回调：" + jsCallNativeEventData.getData());
                                account = AccountManager.getInstance().getCurrentAccount();
                                String shopId = account.getCurrentShopId();

                                TransactionDetailRequest request = new TransactionDetailRequest(jsCallNativeEventData.getData(),
                                        shopId);

                                WebClient webClient = new WebClient();
                                webClient.httpOrderDetail(request, new NativeJsonResponseListener<JSONObject>() {
                                    @Override
                                    public void listener(JSONObject jsonObject) {
                                        showDialog();
                                        Log.e(TAG, jsonObject.toString());
                                        Log.e(TAG, "获取订单详情==========");
                                        jsonResult = jsonObject;
                                        Log.e(TAG, jsonResult.toString());
                                        myHandler.setJsonResult(jsonResult);
//                                        //连接打印机
                                        if (getBluetoothService().getState() == BluetoothService.STATE_CONNECTED) {
                                            printer(jsonObject);
                                        } else {
                                            getBTList();
                                        }
                                    }

                                    @Override
                                    public void errorListener(String s) {

                                    }
                                });


                                responseData = new JsCallNativeEventResponseData(
                                        JsCallNativeEventResponseData.ACK_SUCCESS,
                                        "", "");
                                callBack.onCallBack(responseData);
                                break;

                            default:
                                Log.e(TAG, "This method " + JsCallNativeEventName
                                        .getMethodName(nativeMethod) + "is not processed!");
                                break;

                        }
                    }
                });


        hybridEventBus.registerEventHandler(
                JsCallNativeEventName.EVENT_REQUEST_SWITCH_PAGE,
                new EventHandler() {
                    @Override
                    public void handle(Event event, IHybridCallback callBack) {
                        if (StringUtil.isEmpty(event.getData())) {
                            Log.w(TAG, JsCallNativeEventName.EVENT_REQUEST_SWITCH_PAGE
                                    + ", event data is null.");
                            JsCallNativeEventResponseData responseData
                                    = new JsCallNativeEventResponseData(
                                    JsCallNativeEventResponseData.ACK_FAILED,
                                    "", JsCallNativeEventName.EVENT_REQUEST_SWITCH_PAGE
                                    + " is not executed in Java");
                            callBack.onCallBack(responseData);
                            return;
                        }

                        Log.i(TAG, "Receive event from H5 = " + event.getData());

                        UIManager.getInstance()
                                .switchPage(OrderDetailActivity.this, event, callBack);
                    }
                });

    }

    protected void sendDataToWebview(String jsonData) {
        if (getWebView() != null) {
            getWebView().getHybridAppTunnel()
                    .callJS(NativeCallJsEventName.NATIVE_CALL_JS_EVENT_TRANSFER_DATA, jsonData,
                            new ICallBack() {
                                @Override
                                public void onCallBack(String data) {
                                    Log.v(TAG,
                                            NativeCallJsEventName.NATIVE_CALL_JS_EVENT_TRANSFER_DATA
                                                    + " CallBack, data=" + data);
//                                    mProgressDialog.dismiss();
                                }
                            });
        }
    }



    private void showDialog(){
        Log.d(TAG, "============== :" + "=========="+"showDialog"+ mProgressDialog);
        if(mProgressDialog != null&&!mProgressDialog.isShowing()){
            mProgressDialog.show();
        }
//        Log.d(TAG, "============== :" + "=========="+"showDialog"+ mProgressDialog.isShowing());
    }

    private void dismissDialog(){
        Log.d(TAG, "============== :" + "=========="+"dismissDialog"+ mProgressDialog);
        if(mProgressDialog != null){
            mProgressDialog.dismiss();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "============== :" + "=========="+"onDestroy"+ mProgressDialog);
        myHandler =null;
        mService = null;
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }

    }
}