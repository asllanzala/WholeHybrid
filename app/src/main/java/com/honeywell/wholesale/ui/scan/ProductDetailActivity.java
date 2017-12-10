package com.honeywell.wholesale.ui.scan;

import com.google.gson.Gson;

import com.honeywell.hybridapp.framework.ICallBack;
import com.honeywell.hybridapp.framework.IHybridCallback;
import com.honeywell.hybridapp.framework.event.Event;
import com.honeywell.hybridapp.framework.event.EventHandler;
import com.honeywell.hybridapp.framework.event.JsCallNativeEventResponseData;
import com.honeywell.hybridapp.framework.log.LogHelper;
import com.honeywell.hybridapp.framework.view.HybridAppWebView;
import com.honeywell.wholesale.R;
import com.honeywell.wholesale.framework.application.AppInitManager;
import com.honeywell.wholesale.framework.application.WholesaleApplication;
import com.honeywell.wholesale.framework.database.CartDAO;
import com.honeywell.wholesale.framework.database.CustomerDAO;
import com.honeywell.wholesale.framework.database.InventoryDAO;
import com.honeywell.wholesale.framework.http.IWebApiCallback;
import com.honeywell.wholesale.framework.http.WebClient;
import com.honeywell.wholesale.framework.hybrid.H5PageURL;
import com.honeywell.wholesale.framework.hybrid.HybridEventHandlerUtil;
import com.honeywell.wholesale.framework.hybrid.JsCallNativeEventData;
import com.honeywell.wholesale.framework.hybrid.JsCallNativeEventName;
import com.honeywell.wholesale.framework.hybrid.NativeCallJsEventName;
import com.honeywell.wholesale.framework.model.Account;
import com.honeywell.wholesale.framework.model.AccountManager;
import com.honeywell.wholesale.framework.model.CartItem;
import com.honeywell.wholesale.framework.model.CartManager;
import com.honeywell.wholesale.framework.model.Customer;
import com.honeywell.wholesale.lib.util.StringUtil;
import com.honeywell.wholesale.ui.activity.MainActivity;
import com.honeywell.wholesale.ui.selectpic.ImageSelectorActivity;
import com.honeywell.wholesale.ui.selectpic.config.SelectorSetting;
import com.honeywell.wholesale.ui.uimanager.UIManager;
import com.honeywell.wholesale.ui.util.CommonUtil;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

import static com.honeywell.wholesale.framework.database.CustomerDAO.queryByCustomer;
import static com.honeywell.wholesale.ui.search.SearchKey.customer;

/**
 * Created by e887272 on 6/17/16.
 *
 */
public class ProductDetailActivity extends Activity {

    public static final String TAG = ProductDetailActivity.class.getSimpleName();

    public static ProductDetailActivity productDetailActivity = null;

    public static ProductDetailActivity mProductDetailActivityInstance = null;

    public static final String INTENT_KEY_SCAN_RESULT = "INTENT_KEY_SCAN_RESULT";
    public static final String INTENT_KEY_PRODUCT_ID = "INTENT_KEY_PRODUCT_ID";
    public static final String INTENT_KEY_PRODUCT_NUMBER = "INTENT_KEY_PRODUCT_NUMBER";
    public static final String INTENT_KEY_PRODUCT_CLOSE_OLD_PAGE = "PRODUCT_CLOSE_OLD_PAGE";
    public static final String INTENT_KEY_EDIT_PRODUCT_CLOSE_PAGE = "EDIT_PRODUCT_CLOSE_PAGE";
    public static final String INTENT_KEY_EDIT_PRODUCT_CLOSE_PAGE_CALLBACK = "EDIT_PRODUCT_CLOSE_PAGE_CALLBACK";


    public static final int REQUEST_CODE_SIGNATURE = 100;

    public static final int REQUEST_CODE_PRODUCT_IMAGES_UPLOAD = 101;

    public String mProductCloseMessage = "";


    private HybridAppWebView mWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        productDetailActivity = this;
        LogHelper.getInstance().d(TAG, "showScanResultActivity4");
        setContentView(R.layout.activity_scan_result);
        LogHelper.getInstance().d(TAG, "showScanResultActivity3");
        mProductDetailActivityInstance = this;
        mProductCloseMessage = "";
        mWebView = (HybridAppWebView) findViewById(R.id.hybrid_app_scan_result_webView);
        mWebView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return true;
            }
        });
        initWebView();
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        int wareHouseId = bundle.getInt("warehouse_id");
        String wareHouseName = bundle.getString("warehouse_name");
        loadScanResultPageUrl(parseScanResult(getIntent()), parseParseResult(getIntent()), parseNumberResult(getIntent()),wareHouseId,wareHouseName);
    }

    private void initWebView() {
        HybridEventHandlerUtil.getInstance().registerDefaultHandler(mWebView);
        HybridEventHandlerUtil.getInstance().registerWebApiHandler(mWebView);

        mWebView.getHybridAppTunnel().getHybridEventBus().registerEventHandler(
                JsCallNativeEventName.EVENT_EXECUTE_NATIVE_METHOD,
                new EventHandler() {
                    @Override
                    public void handle(Event event, final IHybridCallback callBack) {
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

                        int nativeMethod = Integer.parseInt(jsCallNativeEventData.getNativeMethod());
                        JsCallNativeEventResponseData responseData = null;
                        Account account = AccountManager.getInstance().getCurrentAccount();

                        switch (nativeMethod) {

                            case JsCallNativeEventName.NATIVE_METHOD_PRODUCT_CLOSE_OLD_PAGE:
                                if(event.getData() == null) {
                                    return;
                                }

                                JSONObject jsonObject =new JSONObject();

                                try {
                                    jsonObject = new JSONObject(event.getData());
                                    mProductCloseMessage = jsonObject.getString("data");
                                } catch (JSONException e){
                                    e.printStackTrace();
                                }
                                responseData = new JsCallNativeEventResponseData(
                                        JsCallNativeEventResponseData.ACK_SUCCESS,
                                        "", INTENT_KEY_EDIT_PRODUCT_CLOSE_PAGE_CALLBACK);
                                //callBack.onCallBack(responseData);
                                Log.e(TAG, "执行回调：NATIVE_METHOD_PRODUCT_CLOSE_OLD_PAGE:" + mProductCloseMessage);
                                break;

                            case JsCallNativeEventName.NATIVE_METHOD_GET_USER_INFO:
                                responseData = new JsCallNativeEventResponseData(
                                        JsCallNativeEventResponseData.ACK_SUCCESS,
                                        "", account.getJsonString());

                                Log.e(TAG, "执行回调：" + responseData.toString());
                                callBack.onCallBack(responseData);
                                break;

                            case JsCallNativeEventName.NATIVE_METHOD_CART_ADD_TO:
                                // Parse json data
                                JSONObject requestBodyJsonObject = HybridEventHandlerUtil.getInstance().parseRequestJson(jsCallNativeEventData.getData(), callBack);
                                if(requestBodyJsonObject == null) {
                                    return;
                                }

                                String oneCartItem = requestBodyJsonObject.toString();
                                try {
                                    JSONObject oneCartItemJson = new JSONObject(oneCartItem);
                                    CartItem cartItem = new CartItem(oneCartItemJson);

                                    String mEmployeeId = cartItem.getEmployeeId();
                                    String mShopId = cartItem.getShopId();
                                    String mCustomerId = cartItem.getCustomerId();

                                    int count = CartDAO.getCartItemsCount(mShopId, mCustomerId, mEmployeeId);
                                    ArrayList<CartItem> mCartJudgeNewItemList =
                                            CartDAO.getAllCartItemsByCustomer(mEmployeeId, mShopId, mCustomerId);

                                    if ((count == 1) && (mCartJudgeNewItemList.get(0).getProductId() == 0)) {
                                        CartManager.getInstance().updateCart(cartItem);
                                    }else {
                                        CartManager.getInstance().addToCart(cartItem);
                                    }

                                    Log.d(TAG, "保存商品到购物车：cartItem=" + cartItem.toJsonString());

                                    responseData = new JsCallNativeEventResponseData(JsCallNativeEventResponseData.ACK_SUCCESS,
                                            "保存购物车数据成功", "");
                                } catch (JSONException e) {
                                    responseData = new JsCallNativeEventResponseData(JsCallNativeEventResponseData.ACK_FAILED,
                                            "保存购物车数据失败，" + e.getLocalizedMessage(), "");
                                    e.printStackTrace();
                                }

                                LogHelper.getInstance().d(TAG, "执行回调：" + responseData.toString());
                                callBack.onCallBack(responseData);
                                break;

                            case JsCallNativeEventName.NATIVE_METHOD_PRODUCT_DELETE:
                                JSONObject productJsonObject = HybridEventHandlerUtil.getInstance().parseRequestJson(jsCallNativeEventData.getData(), callBack);
                                if(productJsonObject == null) {
                                    return;
                                }

                                try {
                                    String shopId = productJsonObject.getString("shopId");
                                    int productId = productJsonObject.getInt("productId");

                                    InventoryDAO.removeInventoryItem(shopId, productId);
                                    responseData = new JsCallNativeEventResponseData(JsCallNativeEventResponseData.ACK_SUCCESS,
                                            "保存购物车数据成功", "");
                                    Log.e(TAG, "执行回调：" + responseData.toString());
                                    callBack.onCallBack(responseData);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                break;

                            case JsCallNativeEventName.NATIVE_METHOD_CUSTOMER_GET_ALL:

                                IWebApiCallback webApiCallback = new IWebApiCallback() {
                                    @Override
                                    public void onSuccessCallback(Object obj) {
                                        String json = CustomerDAO.getAllCustomerWithGroup(AccountManager.getInstance().getCurrentAccount().getCurrentShopId());
                                        LogHelper.getInstance().d(TAG, "商品详情客户分组: " + json);
                                        JsCallNativeEventResponseData responseData = new JsCallNativeEventResponseData(JsCallNativeEventResponseData.ACK_SUCCESS,
                                                "", json);
                                        callBack.onCallBack(responseData);
                                    }

                                    @Override
                                    public void onErrorCallback(Object obj) {
                                        JsCallNativeEventResponseData responseData = new JsCallNativeEventResponseData(JsCallNativeEventResponseData.ACK_FAILED,
                                                "获取更新信息失败", "");
                                        callBack.onCallBack(responseData);
                                    }
                                };

                                AppInitManager.getInstance().syncCustomerData(new WebClient(),
                                        AccountManager.getInstance().getCurrentShopId(), webApiCallback);

//                                CustomerDAO.getAllCustomer(account.getCurrentShopId());
//
//                                String json = "{" +
//                                        "\"a\": [" +
//                                        "{" +
//                                        "\"memo\": \"ggg\"," +
//                                        "\"invoice_title\": \"honeywell\"," +
//                                        "\"address\": \"tyjh\"," +
//                                        "\"customer_name_py\": \"c\"," +
//                                        "\"customer_name\": \"customer1\"," +
//                                        "\"contact_phone\": \"555\"," +
//                                        "\"contact_name\": \"allan\"," +
//                                        "\"customer_name_pinyin\": \"customer1\"" +
//                                        "}" +
//                                        ", " +
//                                        "{" +
//                                        "\"memo\": \"ggg\"," +
//                                        "\"invoice_title\": \"honeywell\"," +
//                                        "\"address\": \"tyjh\"," +
//                                        "\"customer_name_py\": \"c\"," +
//                                        "\"customer_name\": \"ccsadfsa\"," +
//                                        "\"contact_phone\": \"555\"," +
//                                        "\"contact_name\": \"allan\"," +
//                                        "\"customer_name_pinyin\": \"customer1\"" +
//                                        "}" +
//                                        "]" +
//                                        ", " +
//                                        "\"b\": [" +
//                                        "{" +
//                                        "\"memo\": \"ggg\"," +
//                                        "\"invoice_title\": \"honeywell\"," +
//                                        "\"address\": \"tyjh\"," +
//                                        "\"customer_name_py\": \"c\"," +
//                                        "\"customer_name\": \"bbbbb\"," +
//                                        "\"contact_phone\": \"555\"," +
//                                        "\"contact_name\": \"allan\"," +
//                                        "\"customer_name_pinyin\": \"customer1\"" +
//                                        "}" +
//                                        ", " +
//                                        "{" +
//                                        "\"memo\": \"ggg\"," +
//                                        "\"invoice_title\": \"honeywell\"," +
//                                        "\"address\": \"tyjh\"," +
//                                        "\"customer_name_py\": \"c\"," +
//                                        "\"customer_name\": \"b2323\"," +
//                                        "\"contact_phone\": \"555\"," +
//                                        "\"contact_name\": \"allan\"," +
//                                        "\"customer_name_pinyin\": \"customer1\"" +
//                                        "}" +
//                                        "]" +
//                                        "}";
//                                responseData = new JsCallNativeEventResponseData(JsCallNativeEventResponseData.ACK_SUCCESS,
//                                        "", json);
//                                callBack.onCallBack(responseData);
                                break;

                            default:
                                Log.e(TAG, "This method " + JsCallNativeEventName
                                        .getMethodName(nativeMethod) + "is not processed!");
                                break;

                        }
                    }
                });

        mWebView.getHybridAppTunnel().getHybridEventBus()
                .registerEventHandler(
                        JsCallNativeEventName.EVENT_REQUEST_SWITCH_PAGE,
                        new EventHandler() {
                            @Override
                            public void handle(Event event, IHybridCallback callBack) {
                                if (StringUtil.isEmpty(event.getData())) {
                                    Log.w(TAG, JsCallNativeEventName.EVENT_REQUEST_SWITCH_PAGE
                                            + ", event data is null.");
                                    JsCallNativeEventResponseData responseData
                                            = new JsCallNativeEventResponseData(JsCallNativeEventResponseData.ACK_FAILED,
                                            "", JsCallNativeEventName.EVENT_REQUEST_SWITCH_PAGE
                                            + " is not executed in Java");
                                    callBack.onCallBack(responseData);
                                    return;
                                }

                                Log.i(TAG, "Receive event from H5 = " + event.getData());

                                JsCallNativeEventData jsCallNativeEventData = new Gson()
                                        .fromJson(event.getData(), JsCallNativeEventData.class);

                                String nativeMethod = jsCallNativeEventData.getNativeMethod();
                                String requestJsonStr = jsCallNativeEventData.getData();

                                // 图片上传
                                if (JsCallNativeEventName.NATIVE_PAGE_IMAGE_UPLOAD
                                        .equals(nativeMethod)) {
                                    JSONObject requestBodyJsonObject = UIManager
                                            .parseRequestJson(jsCallNativeEventData.getData(),
                                                    callBack);
                                    if (requestBodyJsonObject == null) {
                                        return;
                                    }

                                    String productID = requestBodyJsonObject.optString("productID");
                                    String shopID = requestBodyJsonObject.optString("shopID");

                                    Intent selectImageIntent = new Intent(ProductDetailActivity.this,
                                            ImageSelectorActivity.class);
                                    selectImageIntent.putExtra(SelectorSetting.SELECTOR_PRODUCT_ID,
                                            productID);
                                    selectImageIntent
                                            .putExtra(SelectorSetting.SELECTOR_SHOP_ID, shopID);
                                    startActivityForResult(selectImageIntent,
                                            REQUEST_CODE_PRODUCT_IMAGES_UPLOAD);
                                } else {
                                    UIManager.getInstance()
                                            .switchPage(ProductDetailActivity.this, event, callBack);
                                }
                            }
                        });
    }

    @Override
    protected void onNewIntent(Intent intent) {
        Log.e(TAG, "onNewIntent");
        setIntent(intent);
        String barcode = parseScanResult(intent);
        int productId = parseParseResult(intent);
        String productNumber = parseNumberResult(intent);
        if (!StringUtil.isEmpty(barcode)) {
            loadScanResultPageUrl(barcode, productId, productNumber);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) {
            return;
        }
        switch (requestCode) {
            case REQUEST_CODE_SIGNATURE:
                mWebView.loadUrl(H5PageURL.URL_SCAN_PAYMENT_PAGE_AFTER_SIGNATURE);
                break;

            case REQUEST_CODE_PRODUCT_IMAGES_UPLOAD:
                String results = data.getStringExtra(SelectorSetting.SELECTOR_RESULTS);
                sendDataToWebview(results);
                break;

            default:
                break;
        }
    }

    private String parseScanResult(@Nullable Intent intent) {
        if (intent == null) {
            return null;
        }

        Bundle bundle = intent.getExtras();
        if (bundle == null) {
            return null;
        }

        String barcode = bundle.getString(INTENT_KEY_SCAN_RESULT);
        return barcode;
    }

    private int parseParseResult(@Nullable Intent intent){
        if (intent == null){
            return 0;
        }

        Bundle bundle = intent.getExtras();
        if (bundle == null) {
            return 0;
        }

        int productId = bundle.getInt(INTENT_KEY_PRODUCT_ID, 0);
        return productId;
    }

    private String parseNumberResult(@Nullable Intent intent){
        if (intent == null) {
            return null;
        }

        Bundle bundle = intent.getExtras();
        if (bundle == null) {
            return null;
        }

        String productNumber = bundle.getString(INTENT_KEY_PRODUCT_NUMBER);
        if (productNumber == null){
            productNumber = "";
        }
        return productNumber;
    }

    public void loadScanResultPageUrl(String barcode, int productId, String productNumber) {
        String shopID = AccountManager.getInstance().getCurrentAccount().getCurrentShopId();
        String productIdStr = String.valueOf(productId);
        try {
            shopID = URLEncoder.encode(shopID, "UTF-8");
            barcode = URLEncoder.encode(barcode, "UTF-8");
            productIdStr = URLEncoder.encode(productIdStr, "UTF-8");
            LogHelper.e(TAG, productNumber);
            productNumber = URLEncoder.encode(productNumber, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        String scanResultUrl;
        String s = WholesaleApplication.getCurrentCustmerId();
        if (WholesaleApplication.getCurrentCustmerId() == null){
            scanResultUrl = H5PageURL.URL_PRODUCT_DETAIL_PAGE + "?barcode=" + barcode + "&shopid="
                    + shopID + "&productId=" + productIdStr + "&productNumber=" + productNumber;
        }else{
            String customerName;
            if (WholesaleApplication.getCurrentCustmerId().equals("0")){
                customerName = "散客/新客户";
            } else if (WholesaleApplication.getCurrentCustmerId().equals("individual_id_1")){
                customerName = "散客一";
            } else if (WholesaleApplication.getCurrentCustmerId().equals("individual_id_2")){
                customerName = "散客二";
            } else if (WholesaleApplication.getCurrentCustmerId().equals("individual_id_3")){
                customerName = "散客三";
            } else {
                Customer customer = queryByCustomer(WholesaleApplication.getCurrentCustmerId());
                customerName = customer.getCustomerName();
            }
            scanResultUrl = H5PageURL.URL_PRODUCT_DETAIL_PAGE + "?barcode=" + barcode + "&shopid="
                    + shopID + "&productId=" + productIdStr + "&customerId=" + WholesaleApplication.getCurrentCustmerId()
                    + "&customerName=" + customerName + "&productNumber=" + productNumber;
        }

        scanResultUrl = scanResultUrl.replaceAll("\\+", "%20");
        Log.v(TAG, "scanResultUrl=" + scanResultUrl);

        mWebView.loadUrl(scanResultUrl);
    }

    public void loadScanResultPageUrl(String barcode, int productId, String productNumber, int wareHouseId, String wareHouseName) {
        String shopID = AccountManager.getInstance().getCurrentAccount().getCurrentShopId();
        String productIdStr = String.valueOf(productId);
        if (wareHouseName == null){
            wareHouseName = "";
        }
        try {
            shopID = URLEncoder.encode(shopID, "UTF-8");
            barcode = URLEncoder.encode(barcode, "UTF-8");
            productIdStr = URLEncoder.encode(productIdStr, "UTF-8");
            LogHelper.e(TAG, productNumber);
            productNumber = URLEncoder.encode(productNumber, "UTF-8");
            wareHouseName = URLEncoder.encode(wareHouseName, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        String scanResultUrl;
        String s = WholesaleApplication.getCurrentCustmerId();
        if (WholesaleApplication.getCurrentCustmerId() == null){
            scanResultUrl = H5PageURL.URL_PRODUCT_DETAIL_PAGE + "?barcode=" + barcode + "&shopid="
                    + shopID + "&productId=" + productIdStr + "&productNumber=" + productNumber
                    + "&wareHouseId=" + wareHouseId + "&wareHouseName=" + wareHouseName;
        }else{
            String customerName;
            if (WholesaleApplication.getCurrentCustmerId().equals("0")){
                customerName = "散客/新客户";
            } else if (WholesaleApplication.getCurrentCustmerId().equals("individual_id_1")){
                customerName = "散客一";
            } else if (WholesaleApplication.getCurrentCustmerId().equals("individual_id_2")){
                customerName = "散客二";
            } else if (WholesaleApplication.getCurrentCustmerId().equals("individual_id_3")){
                customerName = "散客三";
            } else {
                Customer customer = queryByCustomer(WholesaleApplication.getCurrentCustmerId());
                customerName = customer.getCustomerName();
            }
            scanResultUrl = H5PageURL.URL_PRODUCT_DETAIL_PAGE + "?barcode=" + barcode + "&shopid="
                    + shopID + "&productId=" + productIdStr + "&customerId=" + WholesaleApplication.getCurrentCustmerId()
                    + "&customerName=" + customerName + "&productNumber=" + productNumber
                    + "&wareHouseId=" + wareHouseId + "&wareHouseName=" + wareHouseName;
        }

        scanResultUrl = scanResultUrl.replaceAll("\\+", "%20");
        Log.v(TAG, "scanResultUrl=" + scanResultUrl);

        mWebView.loadUrl(scanResultUrl);
    }

    @Override
    public void onBackPressed() {
        if (mProductCloseMessage.equals(INTENT_KEY_PRODUCT_CLOSE_OLD_PAGE)){
            finish();
        } else if (mProductCloseMessage.equals(INTENT_KEY_EDIT_PRODUCT_CLOSE_PAGE)){
            sendDataToWebView(INTENT_KEY_EDIT_PRODUCT_CLOSE_PAGE_CALLBACK);
            Log.e(TAG,this.getPackageName());
        } else{
            if (mWebView.canGoBack()) {
                mWebView.goBack();
            } else {
                showDashboard(MainActivity.INTENT_VALUE_CURRENT_FRAGMENT_HOME);
            }
        }
    }

    private void showDashboard(int dashboardPage) {
        Intent intent = new Intent();
        intent.setClass(ProductDetailActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        intent.putExtra(MainActivity.INTENT_KEY_CURRENT_FRAGMENT, dashboardPage);
        startActivity(intent);
        finish();
    }

    @Override
    public void finish() {
        super.finish();
    }

    protected void sendDataToWebView(String jsonData) {
        if (mWebView != null) {
            mWebView.getHybridAppTunnel()
                    .callJS(NativeCallJsEventName.NATIVE_CALL_JS_EVENT_TRANSFER_DATA, jsonData,
                            new ICallBack() {
                                @Override
                                public void onCallBack(String data) {
                                    Log.v(TAG,
                                            NativeCallJsEventName.NATIVE_CALL_JS_EVENT_TRANSFER_DATA
                                                    + " CallBack, data=" + data);
                                }
                            });
        }
    }

    protected void sendDataToWebview(String jsonData) {
        if (mWebView != null) {
            mWebView.getHybridAppTunnel()
                    .callJS(NativeCallJsEventName.NATIVE_CALL_JS_EVENT_PRODUCT_IMAGE_UPLOAD_RESULT, jsonData,
                            new ICallBack() {
                                @Override
                                public void onCallBack(String data) {
                                    Log.v(TAG,
                                            NativeCallJsEventName.NATIVE_CALL_JS_EVENT_PRODUCT_IMAGE_UPLOAD_RESULT
                                                    + " CallBack, data=" + data);
                                }
                            });
        }
    }

}