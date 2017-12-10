package com.honeywell.wholesale.ui.menu.customer;

import com.google.gson.Gson;
import com.honeywell.hybridapp.framework.IHybridCallback;
import com.honeywell.hybridapp.framework.event.Event;
import com.honeywell.hybridapp.framework.event.EventHandler;
import com.honeywell.hybridapp.framework.event.HybridEventBus;
import com.honeywell.hybridapp.framework.event.JsCallNativeEventResponseData;
import com.honeywell.hybridapp.framework.event.NativeCallJsEvent;
import com.honeywell.hybridapp.framework.view.HybridAppWebView;
import com.honeywell.wholesale.R;
import com.honeywell.wholesale.framework.http.NativeJsonResponseListener;
import com.honeywell.wholesale.framework.http.WebClient;
import com.honeywell.wholesale.framework.hybrid.H5PageURL;
import com.honeywell.wholesale.framework.hybrid.HybridEventHandlerUtil;
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
import com.honeywell.wholesale.ui.base.BaseWebViewActivity;
import com.honeywell.wholesale.ui.transaction.TransactionDetailRequest;
import com.honeywell.wholesale.ui.uimanager.UIManager;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothDevice;
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
 * Created by e887272 on 6/30/16.
 */
public class CustomerDetailActivity extends BaseWebViewActivity {

    public static final String TAG = CustomerDetailActivity.class.getSimpleName();
    public static final String INTENT_KEY_CUSTOMER_INFO = "INTENT_KEY_CUSTOMER_INFO";

    private HybridAppWebView mWebView;
    private String mJsonString = "";
    private JSONObject jsonResult;
    public static BluetoothService mService = null;
    private List<BluetoothDevice> mBluetoothDevices;
    protected BluetoothDevice mBluetoothDevice = null;
    private int blueDeivceIndex = -1;
    private MyHandler myHandler;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_common_webview);
        initService();
        Intent intent = getIntent();
        if (intent != null) {
            mJsonString = intent.getStringExtra(INTENT_KEY_CUSTOMER_INFO);
        }

        // "客户详情: {"contact_name":"r","customer_id":55,"contact_phone":5,"address":"","customer_name":"aaaaaaaa","memo":"f","invoice_title":"c"}"
        mWebView = (HybridAppWebView) findViewById(R.id.hybrid_app_webView);
        initWebView();

        NativeCallJsEvent callJsEvent = new NativeCallJsEvent(NativeCallJsEventName.NATIVE_CALL_JS_EVENT_TRANSFER_DATA, mJsonString);
        mWebView.loadUrl(H5PageURL.URL_CUSTOMER_DETAIL, callJsEvent);
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setTitle("");
        mProgressDialog.setMessage("加载中...");
        mProgressDialog.setCancelable(true);
        mProgressDialog.setCanceledOnTouchOutside(false);
    }

    void initService() {
        myHandler = new MyHandler();
        mService = BluetoothService.getInstance(this, myHandler);
    }





    private class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case BluetoothService.MESSAGE_STATE_CHANGE:
                    switch (msg.arg1) {
                        case BluetoothService.STATE_CONNECTED:   //已连接
                            Log.d(TAG, "蓝牙调试: " + "连接成功");
                            printer(jsonResult);
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
                    mProgressDialog.dismiss();
                    Log.d(TAG, "蓝牙调试 :" + "打印成功");
                    Toast.makeText(CustomerDetailActivity.this,"打印成功",Toast.LENGTH_LONG).show();
                    break;
                case ParseXml.RequestType.REQUEST_SETLOCALIZATION:
//                    Toast.makeText(context, msg.obj.toString(), Toast.LENGTH_SHORT).show();
                    break;
            }

        }
    }

    private void initWebView() {
        HybridEventHandlerUtil.getInstance().registerDefaultHandler(mWebView);
        HybridEventHandlerUtil.getInstance().registerWebApiHandler(mWebView);

        HybridEventBus hybridEventBus = mWebView.getHybridAppTunnel().getHybridEventBus();

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
                                        mProgressDialog.show();
                                        Log.e(TAG, jsonObject.toString());
                                        Log.e(TAG, "获取订单详情");

                                        jsonResult = jsonObject;
                                        //连接打印机
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
                                        .getMethodName(nativeMethod) + " is not processed!");
                                break;

                        }
                    }
                });

        hybridEventBus.registerEventHandler(
                        JsCallNativeEventName.EVENT_REQUEST_SWITCH_PAGE,
                        new EventHandler() {
                            @Override
                            public void handle(Event event, IHybridCallback callBack) {
                                UIManager.getInstance().switchPage(CustomerDetailActivity.this, event, callBack);
                            }
                        });
    }

    @Override
    public HybridAppWebView getHybridAppWebView() {
        return mWebView;
    }

    public static BluetoothService getBluetoothService() {
        return mService;
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
            mProgressDialog.dismiss();
            Toast.makeText(CustomerDetailActivity.this,"打印失败",Toast.LENGTH_LONG).show();
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
            mProgressDialog.dismiss();
            Toast.makeText(this,"请连接打印机",Toast.LENGTH_LONG).show();
            return;
        }
        blueDeivceIndex = -1;
        //对打印机进行连接
        startConnectBT();
    }

    private void startConnectBT() {
        int size = mBluetoothDevices.size();
        blueDeivceIndex = blueDeivceIndex + 1;

        if (blueDeivceIndex  > (size-1)) {
            mProgressDialog.dismiss();
            Toast.makeText(this,"打印机连接失败",Toast.LENGTH_LONG).show();
            return;
        }
        mBluetoothDevice = mBluetoothDevices.get(blueDeivceIndex);
        Log.e("========", "" + mBluetoothDevice.getName());
        getBluetoothService().connect(mBluetoothDevice);
    }
}