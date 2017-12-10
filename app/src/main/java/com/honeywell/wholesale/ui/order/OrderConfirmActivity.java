package com.honeywell.wholesale.ui.order;

import com.google.gson.Gson;

import com.honeywell.hybridapp.framework.IHybridCallback;
import com.honeywell.hybridapp.framework.event.Event;
import com.honeywell.hybridapp.framework.event.EventHandler;
import com.honeywell.hybridapp.framework.event.HybridEventBus;
import com.honeywell.hybridapp.framework.event.JsCallNativeEventResponseData;
import com.honeywell.hybridapp.framework.event.NativeCallJsEvent;
import com.honeywell.hybridapp.framework.log.LogHelper;
import com.honeywell.hybridapp.framework.view.HybridAppWebView;
import com.honeywell.wholesale.R;
import com.honeywell.wholesale.framework.database.CartRefCustomerDao;
import com.honeywell.wholesale.framework.http.NativeJsonResponseListener;
import com.honeywell.wholesale.framework.http.WebClient;
import com.honeywell.wholesale.framework.hybrid.H5PageURL;
import com.honeywell.wholesale.framework.hybrid.HybridEventHandlerUtil;
import com.honeywell.wholesale.framework.hybrid.JsCallNativeEventData;
import com.honeywell.wholesale.framework.hybrid.JsCallNativeEventName;
import com.honeywell.wholesale.framework.hybrid.NativeCallJsEventName;
import com.honeywell.wholesale.framework.model.Account;
import com.honeywell.wholesale.framework.model.AccountManager;
import com.honeywell.wholesale.framework.model.CartManager;
import com.honeywell.wholesale.framework.model.CartRefCustomer;
import com.honeywell.wholesale.framework.model.ListItem;
import com.honeywell.wholesale.framework.service.btservice.BluetoothService;
import com.honeywell.wholesale.framework.utils.FileUtil;
import com.honeywell.wholesale.framework.utils.ParseXml;
import com.honeywell.wholesale.framework.utils.XmlUtil;
import com.honeywell.wholesale.lib.util.StringUtil;
import com.honeywell.wholesale.ui.base.BaseActivity;
import com.honeywell.wholesale.ui.payment.PaymentResultActivity;
import com.honeywell.wholesale.ui.payment.SignatureActivity;
import com.honeywell.wholesale.ui.transaction.TransactionDetailRequest;
import com.honeywell.wholesale.ui.uimanager.UIManager;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static com.honeywell.wholesale.framework.database.CartRefCustomerDao.queryCartRefCustomerItems;
import static com.honeywell.wholesale.framework.database.CartRefCustomerDao.removeCartRefCustomer;
import static com.honeywell.wholesale.framework.database.CartRefSKUDao.removeCartRefSKUItem;

/**
 * Created by e887272 on 6/29/16.
 */
public class OrderConfirmActivity extends BaseActivity {

    public static final String TAG = OrderConfirmActivity.class.getSimpleName();

    public static final String INTENT_KEY_SELECTED_PRODUCTS = "INTENT_KEY_SELECTED_PRODUCTS";

    public static final int REQUEST_CODE_PAY_MONTHLY_SIGNATURE = 100;

    public static final int REQUEST_CODE_PAY_OFFLINE = 101;

    public static final int REQUEST_CODE_PAY_ONLINE = 102;

    private HybridAppWebView mWebView;

    private PopupWindow mpopupWindow;
    private JSONObject jsonResult;
    public static BluetoothService mService = null;
    private OrderConfirmActivity.MyHandler myHandler;
    private List<BluetoothDevice> mBluetoothDevices;
    protected BluetoothDevice mBluetoothDevice = null;
    private int blueDeivceIndex = -1;
    private ProgressDialog mProgressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_common_webview);
        initService();
        String selectedProductsJson = "";
        Intent intent = getIntent();
        if (intent != null) {
            selectedProductsJson = intent.getStringExtra(INTENT_KEY_SELECTED_PRODUCTS);
            LogHelper.getInstance().d(TAG, "Selected products: " + selectedProductsJson);
        }

        mWebView = (HybridAppWebView) findViewById(R.id.hybrid_app_webView);
        initWebView();

        NativeCallJsEvent callJsEvent = new NativeCallJsEvent(
                NativeCallJsEventName.NATIVE_CALL_JS_EVENT_TRANSFER_DATA, selectedProductsJson);
        mWebView.loadUrl(H5PageURL.URL_CHECKOUT_PAGE, callJsEvent);
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setTitle("");
        mProgressDialog.setMessage("加载中...");
        mProgressDialog.setCancelable(true);
        mProgressDialog.setCanceledOnTouchOutside(false);
    }

    public static BluetoothService getBluetoothService() {
        return mService;
    }

    void initService() {
        myHandler = new OrderConfirmActivity.MyHandler();
        mService = BluetoothService.getInstance(this, myHandler);
    }

    private void printer(JSONObject jsonObject) {
        List<ListItem> listItems = new ArrayList<ListItem>();
        try {
            listItems = FileUtil.getListItem(jsonObject);
            Log.e("=====", "" + listItems.size());

            XmlUtil.printSendFileToPrinter(this, FileUtil.getSourceFile2(this, listItems));
        } catch (Exception e) {
            mProgressDialog.dismiss();
            Toast.makeText(OrderConfirmActivity.this,"打印失败",Toast.LENGTH_LONG).show();
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
                    Toast.makeText(OrderConfirmActivity.this,"打印成功",Toast.LENGTH_LONG).show();
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
                        String userId = null;
                        String customerId = null;

                        switch (nativeMethod) {
                            case JsCallNativeEventName.NATIVE_METHOD_GET_USER_INFO:
                                Account account = AccountManager.getInstance().getCurrentAccount();
                                responseData = new JsCallNativeEventResponseData(
                                        JsCallNativeEventResponseData.ACK_SUCCESS,
                                        "", account.getJsonString());

                                Log.e(TAG, "执行回调：" + responseData.toString());
                                callBack.onCallBack(responseData);
                                break;

                            case JsCallNativeEventName.NATIVE_METHOD_CART_REMOVE_BY_CUSTOMER_ID:
                                // Parse json data
                                JSONObject requestBodyJsonObject3 = HybridEventHandlerUtil
                                        .getInstance()
                                        .parseRequestJson(jsCallNativeEventData.getData(),
                                                callBack);
                                if (requestBodyJsonObject3 == null) {
                                    return;
                                }

                                String uuid = requestBodyJsonObject3.optString("uuid");
                                customerId = requestBodyJsonObject3.optString("customerId");
                                String employeeId = AccountManager.getInstance().getCurrentAccount().getEmployeeId();
                                ArrayList<CartRefCustomer> cartRefCustomerArrayList = CartRefCustomerDao.queryCartRefCustomerItems(customerId, employeeId);
                                removeCartRefSKUItem(uuid);
//                                removeCartRefCustomer(employeeId, customerId);
                                removeCartRefCustomer(uuid);
                                responseData = new JsCallNativeEventResponseData(
                                        JsCallNativeEventResponseData.ACK_SUCCESS,
                                        "删除购物车全部数据成功", "");
//                                LogHelper.getInstance()
//                                        .d(TAG, "执行回调：" + responseData.toString());
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

                            case JsCallNativeEventName.NATIVE_METHOD_CART_UPDATE_CUSTOMER_INFO:
//                                JSONObject productJsonObject = HybridEventHandlerUtil.getInstance().parseRequestJson(jsCallNativeEventData.getData(), callBack);
//                                if(productJsonObject == null) {
//                                    responseData = new JsCallNativeEventResponseData(JsCallNativeEventResponseData.ACK_FAILED,
//                                            "更新购物车客户信息失败，没收到要更新的参数", "");
//                                    return;
//                                }
//
//                                try {
//                                    String shopId = productJsonObject.getString("shopId");
//                                    String employeeID = productJsonObject.getString("employeeID");
//                                    String oldCustomerID = productJsonObject.getString("oldCustomerID");
//                                    String newCustomerId = productJsonObject.getString("newCustomerId");
//                                    String customerName = productJsonObject.getString("customerName");
//                                    String contactName = productJsonObject.getString("contactName");
//                                    String contactPhone = productJsonObject.getString("contactPhone");
//                                    String customerNotes = productJsonObject.getString("customerNotes");
//                                    String invoiceTitle = productJsonObject.getString("invoiceTitle");
//                                    String address = productJsonObject.getString("address");
//
//                                    CartManager.getInstance().updateCustomerInfo(shopId, employeeID, oldCustomerID, newCustomerId, customerName,
//                                            contactName, contactPhone, customerNotes, invoiceTitle, address);
//
//                                    responseData = new JsCallNativeEventResponseData(JsCallNativeEventResponseData.ACK_SUCCESS,
//                                            "更新购物车客户信息成功", "");
//                                    Log.e(TAG, "执行回调：" + responseData.toString());
//                                    callBack.onCallBack(responseData);
//                                } catch (JSONException e) {
//                                    responseData = new JsCallNativeEventResponseData(JsCallNativeEventResponseData.ACK_FAILED,
//                                            "保存购物车客户信息失败，没收到要更新的参数", "");
//                                    e.printStackTrace();
//                                }
                                break;

                            case JsCallNativeEventName.NATIVE_METHOD_CART_REMOVE_ALL:

                                Log.e(TAG,"order confim lee is a sha" + "bbb");
                                // Parse json data
                                JSONObject requestBodyJsonObject4 = HybridEventHandlerUtil
                                        .getInstance()
                                        .parseRequestJson(jsCallNativeEventData.getData(),
                                                callBack);
                                if (requestBodyJsonObject4 == null) {
                                    return;
                                }

                                String customerID11 = requestBodyJsonObject4.optString("customerID");
                                Log.e(TAG,"customerID = " + customerID11);
                                String employeeID1 = AccountManager.getInstance().getCurrentAccount().getEmployeeId();

                                CartManager.getInstance().removeCart(employeeID1, customerID11);
                                responseData = new JsCallNativeEventResponseData(
                                        JsCallNativeEventResponseData.ACK_SUCCESS,
                                        "删除购物车全部数据成功", "");
                                LogHelper.getInstance()
                                        .d(TAG, "执行回调：" + responseData.toString());
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

                        JsCallNativeEventData jsCallNativeEventData = new Gson()
                                .fromJson(event.getData(), JsCallNativeEventData.class);

                        String nativeMethod = jsCallNativeEventData.getNativeMethod();

                        if (JsCallNativeEventName.NATIVE_PAGE_PAY_OFFLINE
                                .equals(nativeMethod)) {
                            Intent intent = new Intent(OrderConfirmActivity.this,
                                    PaymentResultActivity.class);
                            startActivityForResult(intent, REQUEST_CODE_PAY_OFFLINE);
                            finish();
                        } else if (JsCallNativeEventName.NATIVE_PAGE_PAY_MONTHLY
                                .equals(nativeMethod)) {
                            Intent intent = new Intent(OrderConfirmActivity.this, SignatureActivity.class);
                            startActivityForResult(intent, REQUEST_CODE_PAY_MONTHLY_SIGNATURE);
                        } else if (JsCallNativeEventName.NATIVE_PAGE_PAY_ONLINE
                                .equals(nativeMethod)) {
                            // show popup menu for payment select
                            ArrayList<JSONObject> arrayList = initTestData();
                            showPopMenu(arrayList);
//                                    Intent intent = new Intent(OrderConfirmActivity.this, SelectPayActivity.class);
//                                    startActivityForResult(intent, REQUEST_CODE_PAY_ONLINE);
                        } else {
                            UIManager.getInstance().switchPage(OrderConfirmActivity.this, event, callBack);
                        }
                    }
                });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }
        switch (requestCode) {
            case REQUEST_CODE_PAY_OFFLINE:
                finish();
                break;
            case REQUEST_CODE_PAY_MONTHLY_SIGNATURE:
                finish();
                break;
            case REQUEST_CODE_PAY_ONLINE:
                finish();
                break;
            default:
                break;
        }

    }

    private ArrayList<JSONObject> initTestData() {
        // data for demo
        ArrayList<JSONObject> demoDataArrayList = new ArrayList<>();
        JSONObject aliJsonObject = new JSONObject();
        JSONObject weChatJsonObject = new JSONObject();
        try {
            aliJsonObject.put("name", "Alipay");
            aliJsonObject.put("url", R.drawable.alipay);
            demoDataArrayList.add(aliJsonObject);
            weChatJsonObject.put("name", "WeChat");
            weChatJsonObject.put("url", R.drawable.wechat_pay);
            demoDataArrayList.add(weChatJsonObject);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return demoDataArrayList;
    }

    private void showPopMenu(ArrayList<JSONObject> demoDataArrayList) {
        View view = View.inflate(getApplicationContext(), R.layout.menu_select_payment, null);
        GridView gridView = (GridView) view.findViewById(R.id.pay_menu_gridView);

        // TODO want to show dialog with bottom bar need discuss with allan
        // can not show
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        AlertDialog alertDialog = builder.create();
//        alertDialog.setCanceledOnTouchOutside(false);
//        alertDialog.show();
//
//        Window window = alertDialog.getWindow();
//        WindowManager.LayoutParams wlp = window.getAttributes();
//        wlp.gravity = Gravity.BOTTOM;
//        wlp.width = WindowManager.LayoutParams.MATCH_PARENT;
//        window.setAttributes(wlp);
//        window.setContentView(view);

        gridView.setAdapter(new GridViewAdapter(this, demoDataArrayList));
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (mpopupWindow.isShowing()) {
                    mpopupWindow.dismiss();
                }
//                Intent intent = new Intent();
//                intent.setClass(OrderConfirmActivity.this, ScanQrPayActivity.class);
//                startActivityForResult(intent, REQUEST_CODE_PAY_MONTHLY_SIGNATURE);
            }
        });
        if (mpopupWindow == null) {
            mpopupWindow = new PopupWindow(this);
            mpopupWindow.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
            mpopupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
            mpopupWindow.setBackgroundDrawable(new BitmapDrawable());
            mpopupWindow.setFocusable(true);
            mpopupWindow.setOutsideTouchable(true);
            mpopupWindow.setAnimationStyle(R.style.PopupAnimation);
        }

//        WindowManager windowManager = getWindowManager();
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.alpha = 0.5f;
        getWindow().setAttributes(params);
        mpopupWindow.setContentView(view);

        View parentView = this.getWindow().getDecorView().findViewById(android.R.id.content);
        mpopupWindow.showAtLocation(parentView, Gravity.BOTTOM, 0, 0);
        mpopupWindow.update();

        mpopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                WindowManager.LayoutParams params = getWindow().getAttributes();
                params.alpha = 1f;
                getWindow().setAttributes(params);
            }
        });
    }

    class GridViewAdapter extends BaseAdapter {

        private ArrayList<JSONObject> arrayList;

        private Context context;

        public GridViewAdapter(Context context, ArrayList<JSONObject> arrayList) {
            this.context = context;
            this.arrayList = arrayList;
        }

        @Override
        public int getCount() {
            return arrayList.size();
        }

        @Override
        public Object getItem(int position) {
            return arrayList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = LayoutInflater.from(context)
                        .inflate(R.layout.item_select_payment, null);
                holder.textView = (TextView) convertView.findViewById(R.id.payment_item_text);
                holder.imageView = (ImageView) convertView.findViewById(R.id.payment_item_icon);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            JSONObject jsonObject = arrayList.get(position);

            String text = jsonObject.optString("name", "default");
            int drawableId = jsonObject.optInt("url", R.drawable.main_page_cart_bg_selected);

            holder.textView.setText(text);
            holder.imageView.setImageResource(drawableId);

            return convertView;
        }
    }

    private static class ViewHolder {

        TextView textView;

        ImageView imageView;
    }

}