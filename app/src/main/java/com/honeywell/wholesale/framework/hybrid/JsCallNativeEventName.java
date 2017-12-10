package com.honeywell.wholesale.framework.hybrid;

import android.util.Log;

/**
 * Created by e887272 on 6/13/16.
 */
public class JsCallNativeEventName {

    public static final String TAG = JsCallNativeEventName.class.getSimpleName();

    // 执行本地方法的事件
    public static final String EVENT_EXECUTE_NATIVE_METHOD = "ExecuteNativeMethod";

    // 执行网络请求的事件
    public static final String EVENT_REQUEST_WEB_API = "RequestWebAPI";

    // 执行网络请求的事件
    public static final String EVENT_REQUEST_SWITCH_PAGE = "SwitchPage";

    public static final String NATIVE_PAGE_CLOSE = "close";

    public static final String NATIVE_PAGE_DASHBOARD_HOME = "dashboard.pg?tab=home";
    public static final String NATIVE_PAGE_DASHBOARD_PAY_TRANSACTION = "dashboard.pg?tab=transaction";
    public static final String NATIVE_PAGE_DASHBOARD_CART = "dashboard.pg?tab=cart";
    public static final String NATIVE_PAGE_IMAGE_UPLOAD = "imageUpload.pg";
    public static final String NATIVE_PAGE_PRODUCT_DETAIL = "productDetail.pg";

    public static final String NATIVE_PAGE_PAY_ONLINE = "payonline.pg";
    public static final String NATIVE_PAGE_PAY_OFFLINE = "payoffline.pg";
    public static final String NATIVE_PAGE_PAY_MONTHLY = "paymonthly.pg";
    public static final String NATIVE_PAGE_CHECKOUT = "checkout.pg";

    public static final String NATIVE_PAGE_SUPPLIER_DETAIL = "supplierDetail.pg";
    public static final String NATIVE_PAGE_SUPPLIER_SEARCH = "supplierSearch.pg";

    public static final String NATIVE_PAGE_CUSTOMER_DETAIL = "customerDetail.pg";
    public static final String NATIVE_PAGE_CUSTOMER_SEARCH = "customerSearch.pg";

    public static final String NATIVE_PAGE_CREATE_NEW_ORDERS = "createNewOrders.pg";
    public static final String NATIVE_PAGE_WAREHOUSE_MANAGER = "wareHouseManager.pg";
    public static final String NATIVE_METHOD_PRODUCT_DETAIL_NEW_ORDERS = "productDetailNewOrders.pg";


    // login / logout
    public static final int NATIVE_METHOD_LOGOUT = 100001;
    public static final int NATIVE_METHOD_LOGIN = 100002;
    public static final int NATIVE_METHOD_LOGIN_GET_LOCAL_USER_LIST = 100003;

    // common method
    public static final int NATIVE_METHOD_GET_USER_INFO = 200001;
    public static final int NATIVE_METHOD_GET_USER_INFO_WITH_PASSWORD = 200002;

    // Cart
    public static final int NATIVE_METHOD_CART_ADD_TO = 300001;
    public static final int NATIVE_METHOD_CART_GET_ALL = 300002;
    public static final int NATIVE_METHOD_CART_REMOVE_BY_CUSTOMER_ID = 300003;
    public static final int NATIVE_METHOD_CART_REMOVE_BY_PRODUCT_ID = 300004;
    public static final int NATIVE_METHOD_CART_REMOVE_ALL = 300005;
    public static final int NATIVE_METHOD_CART_UPDATE_CUSTOMER_INFO = 300006;

    // product
    public static final int NATIVE_METHOD_PRODUCT_DELETE = 400001;
    public static final int NATIVE_METHOD_PRODUCT_ADD = 400002;
    public static final int NATIVE_METHOD_PRODUCT_EDIT = 400003;
    public static final int NATIVE_METHOD_PRODUCT_STOCK_CONFIRM = 400004;
    public static final int NATIVE_METHOD_PRODUCT_CLOSE_OLD_PAGE = 400005;

    // customer
    public static final int NATIVE_METHOD_CUSTOMER_GET_ALL = 500001;

    // vendor
    public static final int NATIVE_METHOD_SUPPLIER_GET_ALL = 600001;

    public static final int NATIVE_METHOD_BLUETOOTH_PRINTER = 700001;


    /**
     * 这个方法值用来debug
     */
    public static String getMethodName(int method) {

        switch (method) {
            case JsCallNativeEventName.NATIVE_METHOD_LOGOUT:
                return "logout";

            case JsCallNativeEventName.NATIVE_METHOD_CART_ADD_TO:
                return "NATIVE_METHOD_CART_ADD_TO";

            case JsCallNativeEventName.NATIVE_METHOD_CART_GET_ALL:
                return "NATIVE_METHOD_CART_GET_ALL";

            case JsCallNativeEventName.NATIVE_METHOD_CART_REMOVE_BY_CUSTOMER_ID:
                return "NATIVE_METHOD_CART_REMOVE_BY_CUSTOMER_ID";

            case JsCallNativeEventName.NATIVE_METHOD_CART_REMOVE_BY_PRODUCT_ID:
                return "NATIVE_METHOD_CART_REMOVE_BY_PRODUCT_ID";

            default:
                Log.e(TAG, "Method " + method + " is not supported!");
                break;
        }

        return null;
    }


}
