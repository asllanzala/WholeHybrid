package com.honeywell.wholesale.framework.http;

import com.google.gson.Gson;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.honeywell.hybridapp.framework.IHybridCallback;
import com.honeywell.hybridapp.framework.event.Event;
import com.honeywell.hybridapp.framework.event.JsCallNativeEventResponseData;
import com.honeywell.hybridapp.framework.event.NativeCallJsEvent;
import com.honeywell.hybridapp.framework.event.WholesaleHttpResponse;
import com.honeywell.hybridapp.framework.log.LogHelper;
import com.honeywell.wholesale.R;
import com.honeywell.wholesale.framework.application.WholesaleApplication;
import com.honeywell.wholesale.framework.database.CategoryDAO;
import com.honeywell.wholesale.framework.database.CustomerDAO;
import com.honeywell.wholesale.framework.database.IncrementalDAO;
import com.honeywell.wholesale.framework.database.InventoryDAO;
import com.honeywell.wholesale.framework.database.SupplierDAO;
import com.honeywell.wholesale.framework.hybrid.HttpRequestParams;
import com.honeywell.wholesale.framework.model.Account;
import com.honeywell.wholesale.framework.model.AccountManager;
import com.honeywell.wholesale.framework.model.CategoriesManager;
import com.honeywell.wholesale.framework.model.Category;
import com.honeywell.wholesale.framework.model.Customer;
import com.honeywell.wholesale.framework.model.Inventory;
import com.honeywell.wholesale.framework.model.Supplier;
import com.honeywell.wholesale.framework.utils.SharePreferenceUtil;
import com.honeywell.wholesale.lib.util.StringUtil;
import com.honeywell.wholesale.ui.IOUtils;
import com.honeywell.wholesale.ui.category.model.CategoryAddRequest;
import com.honeywell.wholesale.ui.category.model.CategoryDeleteRequest;
import com.honeywell.wholesale.ui.category.model.CategoryUpdateRequest;
import com.honeywell.wholesale.ui.login.ILoginCallback;
import com.honeywell.wholesale.ui.login.SplashActivity;
import com.honeywell.wholesale.ui.login.module.ListCustomerRequest;
import com.honeywell.wholesale.ui.login.module.ListSupplierRequest;
import com.honeywell.wholesale.ui.login.module.LoginCloudApiRequest;
import com.honeywell.wholesale.ui.login.module.LoginWebServiceResponse;
import com.honeywell.wholesale.ui.login.module.TransactionOrderRequest;
import com.honeywell.wholesale.ui.login.module.TransactionStockRequest;
import com.honeywell.wholesale.ui.login.module.TransactionStockUnconfirmRequest;
import com.honeywell.wholesale.ui.login.module.UpdateCategoryCloudApiRequest;
import com.honeywell.wholesale.ui.login.module.UpdateInventoryCloudApiRequest;
import com.honeywell.wholesale.ui.menu.setting.warehouse.request.WareHouseAddRequest;
import com.honeywell.wholesale.ui.menu.setting.warehouse.request.WareHouseBlockRequest;
import com.honeywell.wholesale.ui.menu.setting.warehouse.request.WareHouseDefaultRequest;
import com.honeywell.wholesale.ui.menu.setting.warehouse.request.WareHouseDeleteRequest;
import com.honeywell.wholesale.ui.menu.setting.warehouse.request.WareHouseDetailRequest;
import com.honeywell.wholesale.ui.menu.setting.warehouse.request.WareHouseListRequest;
import com.honeywell.wholesale.ui.menu.setting.warehouse.request.WareHouseQueryOwnerRequest;
import com.honeywell.wholesale.ui.menu.setting.warehouse.request.WareHouseUpdateReuqest;
import com.honeywell.wholesale.ui.menu.shop.GetCustomerDetailApiRequest;
import com.honeywell.wholesale.ui.priceDiff.request.CustomerDetailRequest;
import com.honeywell.wholesale.ui.priceDiff.request.PriceDiffOrderRequest;
import com.honeywell.wholesale.ui.priceDiff.request.PriceDiffPayRequest;
import com.honeywell.wholesale.ui.priceDiff.request.ProductDetailPriceDiffRequest;
import com.honeywell.wholesale.ui.purchase.request.BatchAddBean;
import com.honeywell.wholesale.ui.purchase.request.ProductDetailBean;
import com.honeywell.wholesale.ui.purchase.request.PurchaseOrderRequest;
import com.honeywell.wholesale.ui.purchase.request.PurchasePayRequest;
import com.honeywell.wholesale.ui.saleReturn.request.SaleReturnOrderRequest;
import com.honeywell.wholesale.ui.saleReturn.request.SaleReturnPayRequest;
import com.honeywell.wholesale.ui.scan.network.ProductDetailRequest;
import com.honeywell.wholesale.ui.transaction.TransactionCountRequest;
import com.honeywell.wholesale.ui.transaction.TransactionDetailRequest;
import com.honeywell.wholesale.ui.transaction.TransactionQueryRequest;
import com.honeywell.wholesale.ui.transaction.preorders.network.PreOrderAddRequest;
import com.honeywell.wholesale.ui.transaction.preorders.network.PreOrderDeleteRequest;
import com.honeywell.wholesale.ui.transaction.preorders.network.PreOrderListRequest;
import com.honeywell.wholesale.ui.transaction.preorders.network.PreOrderSearchRequest;
import com.honeywell.wholesale.ui.transaction.preorders.network.PreOrderUpdateRequest;
import com.honeywell.wholesale.ui.transaction.preorders.network.PreTransactionSaleRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.Nullable;
import android.support.v4.util.LruCache;
import android.util.Log;
import android.widget.Toast;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;

import static android.R.attr.password;
import static okhttp3.Protocol.get;

/**
 * Created by H155935 on 16/6/12.
 * Email: xiaofei.he@honeywell.com
 */
public class WebClient {

    private static final String TAG = "WebClient";

    private static final String WEB_SERVICE_UPLOAD_URL = "pic/upload";
    private static final int SOCKET_TIMEOUT_MS = 10000;
    private static final int UPLOAD_SOCKET_TIMEOUT_MS = 50000;

    private static final String REQUEST_HEADER_KEY = "HEADERS";

    private static final String REQUEST_BODY_KEY = "BODY";

    private static final String API_RESPONSE_RET_CODE = "retcode";

    private static final String API_RESPONSE_MESSAGE = "message";

    // response data struct
    public static final String API_RESPONSE_RET_BODY = "retbody";

    private static final String SHARE_PREFERENCE_APP_USER_PICS = "userPics";
    private static final String SHARE_PREFERENCE_APP_USER_PICS_URL = "userPicsUrl";
    private static final String SHARE_PREFERENCE_APP_USER_PICS_HD_URL = "userPicsHdUrl";

    private static final String TOKEN = "token";

    private static final int RET_CODE_HTTP_REQUEST_PARAMS_ERROR = -1;
    private static final int RET_NO_NETWORK_CONNECTION = -2;

    private static final int RET_CODE_SUCCESS = 200;
    private static final int OUTTIME_RET_CODE = 4020;

    public static final String HTTP_HEADER_CONTENT_TYPE = "application/json;charset=utf-8";

    private IHybridCallback mCallBack;

    private Event mEvent;

    private JSONObject requestJsonBody;

    // Multiple Upload
    private final String twoHyphens = "--";

    private final String lineEnd = "\r\n";

    private final String boundary = "apiclient-" + System.currentTimeMillis();

    private final String mimeType = "multipart/form-data;boundary=" + boundary;

    // ImageLoader
    private static ImageLoader imageLoader;


    // 当网络请求由h5访问时, 由URL来判断同步本地数据库。
    private static String urlString;

    public WebClient() {
    }

    public void httpJsonRequest(Event event, IHybridCallback callBack) {
        mCallBack = callBack;
        mEvent = event;

        try {
            HttpRequestParams httpRequestParams = new Gson()
                    .fromJson(event.getData(), HttpRequestParams.class);

            final Map<String, String> header = new HashMap<>();
            int methodMapper = selectHttpMethod(httpRequestParams.getApiMethod());

            requestJsonBody = new JSONObject(httpRequestParams.getRequestBody());

            String requestURL = WebServerConfigManager.getWebServiceUrl() + httpRequestParams.getUrlMethod();

            Log.v(TAG, "requestURL=" + requestURL + ", requestJsonBody=" + requestJsonBody);
            urlString = requestURL;
            JsonObjectRequest jsonObjectRequest =
                    new JsonObjectRequest(methodMapper, requestURL, requestJsonBody,
                            mHttpOkResponseListener,
                            mHttpErrorResponseListener) {
                        @Override
                        public Map<String, String> getHeaders() throws AuthFailureError {
                            Map<String, String> httpHeader = new HashMap<>();
                            if (header.size() > 0) {
                                httpHeader.putAll(header);
                            }

                            Account currentUser = AccountManager.getInstance().getCurrentAccount();
                            // add token to cookie
                            if (currentUser != null && currentUser.getToken() != null && currentUser.getToken().length() > 0) {
                                String s = currentUser.getToken();
                                String tokenString = "token=" + s;
                                httpHeader.put("Cookie", tokenString);
                            }
                            return httpHeader;
                        }

                        @Override
                        public String getBodyContentType() {
                            return HTTP_HEADER_CONTENT_TYPE;
                        }

                    };

            jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                    SOCKET_TIMEOUT_MS,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            WholesaleApplication.getRequestQueue().add(jsonObjectRequest);
        } catch (JSONException e) {
            // 请求参数出错，也生产一个返回参数返回给H5层
            String jsonParamErrorResponse = new WholesaleHttpResponse(RET_CODE_HTTP_REQUEST_PARAMS_ERROR, StringUtil.getString(
                    R.string.http_request_json_params_error), null).getJsonString();

            JsCallNativeEventResponseData responseJson = new JsCallNativeEventResponseData(JsCallNativeEventResponseData.ACK_FAILED,
                    "", jsonParamErrorResponse);
            callBack.onCallBack(responseJson);
        }
    }

    public void nativeHttpJsonRequest(int method, String subUrl,
                                      @Nullable JSONObject requestBody, final NativeJsonResponseListener<JSONObject> listener) {
        String url = WebServerConfigManager.getWebServiceUrl() + subUrl;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequestWithCookie(
                method, url, requestBody, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                int responseCode = response.optInt(API_RESPONSE_RET_CODE);
                if (responseCode != RET_CODE_SUCCESS) {
                    LogHelper.getInstance().e(TAG, "http请求成功, 逻辑有误" + response.toString());
                    listener.errorListener(response.toString());
                    return;
                }
                listener.listener(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                LogHelper.getInstance().e(TAG, "失败：");
                String errorMessage = VolleyErrorHelper.getMessage(error);
                listener.errorListener(errorMessage);
            }
        });

        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        WholesaleApplication.getRequestQueue().add(jsonObjectRequest);
    }

    public void customHttpUpload(String url, ArrayList<String> filePath, @Nullable Map<String, String> header, @Nullable Map<String, String> textBody) throws JSONException, IOException {
        byte[] multipartBody = null;
        final String FILE_PART_NAME = "file_data";

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(bos);
        try {
            for (String pathString : filePath) {
                File file = new File(pathString);
                byte[] fileData = IOUtils.readContentIntoByteArray(file);
                buildFilePart(dos, fileData, FILE_PART_NAME, "upload.png");
            }
            if (textBody != null) {
                for (Map.Entry<String, String> entry : textBody.entrySet()) {
                    buildTextPart(dos, entry.getKey(), entry.getValue());
                }
            }

            dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
            multipartBody = bos.toByteArray();

        } catch (IOException e) {
            e.printStackTrace();
        }

        MultipartRequest multipartRequest = new MultipartRequest(url, null, mimeType, multipartBody,
                new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
                        String s = new String(response.data);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        processNetworkError(error);
                    }
                });

        multipartRequest.setRetryPolicy(new DefaultRetryPolicy(
                UPLOAD_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        WholesaleApplication.getRequestQueue().add(multipartRequest);
    }


    /**
     * 商品详情图片上传
     *
     * @param url
     * @param filePath
     * @param productId
     * @param shopId
     * @param listener
     * @param errorListener
     * @throws JSONException
     * @throws IOException
     */
    public void httpUpload(
            String url, ArrayList<String> filePath,
            String productId, String shopId, Response.Listener<NetworkResponse> listener, Response.ErrorListener errorListener)
            throws JSONException, IOException {
        final String FILE_PART_NAME = "file_data";
        final String STRING_PART_PRODUCT_ID = "product_code";
        final String STRING_PART_SHOP_ID = "shop_id";
        byte[] multipartBody = null;

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(bos);
        try {
            for (String pathString : filePath) {

                // test
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(pathString, options);
                int height = options.outHeight;
                int width = options.outWidth;
                int inSampleSize = 2;
                int minLen = Math.min(height, width);
                if (minLen > 400) {
                    float ratio = (float) minLen / 400.0f;
                    inSampleSize = (int) ratio;
                }

                options.inJustDecodeBounds = false;
                options.inSampleSize = inSampleSize;

                Bitmap bitmap = BitmapFactory.decodeFile(pathString, options);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();

//                final int lnth = bitmap.getByteCount();
//                ByteBuffer dst= ByteBuffer.allocate(lnth);
//                bitmap.copyPixelsToBuffer( dst);
//                byte[] photoData = dst.array();

                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);

                byte[] photoData = stream.toByteArray();
//                File file = new File(pathString);
//                byte[] fileData = IOUtils.readContentIntoByteArray(file);
                buildFilePart(dos, photoData, FILE_PART_NAME, "upload.png");
            }

            buildTextPart(dos, STRING_PART_PRODUCT_ID, productId);
            buildTextPart(dos, STRING_PART_SHOP_ID, shopId);

            dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
            multipartBody = bos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }

        MultipartRequest multipartRequest = new MultipartRequest(url, null, mimeType, multipartBody,
                listener, errorListener);

        WholesaleApplication.getRequestQueue().add(multipartRequest);
    }

    public DataOutputStream buildFilePart(DataOutputStream dataOutputStream, byte[] fileData, String fileKey,
                                          String fileName) throws IOException {
        dataOutputStream.writeBytes(twoHyphens + boundary + lineEnd);

        dataOutputStream
                .writeBytes("Content-Disposition: form-data; name=\"" + fileKey + "\"; filename=\""
                        + fileName + "\"" + lineEnd);
        dataOutputStream.writeBytes(lineEnd);

        ByteArrayInputStream fileInputStream = new ByteArrayInputStream(fileData);
        int bytesAvailable = fileInputStream.available();

        int maxBufferSize = 1024 * 1024;
        int bufferSize = Math.min(bytesAvailable, maxBufferSize);
        byte[] buffer = new byte[bufferSize];

        int bytesRead = fileInputStream.read(buffer, 0, bufferSize);
        while (bytesRead > 0) {
            dataOutputStream.write(buffer, 0, bufferSize);
            bytesAvailable = fileInputStream.available();
            bufferSize = Math.min(bytesAvailable, maxBufferSize);
            bytesRead = fileInputStream.read(buffer, 0, bufferSize);
        }
        dataOutputStream.writeBytes(lineEnd);
        return dataOutputStream;
    }

    public DataOutputStream buildTextPart(DataOutputStream dataOutputStream, String parameterName,
                                          String parameterValue) throws IOException {
        dataOutputStream.writeBytes(twoHyphens + boundary + lineEnd);
        dataOutputStream.writeBytes(
                "Content-Disposition: form-data; name=\"" + parameterName + "\"" + lineEnd);
        dataOutputStream.writeBytes("Content-Type: text/plain; charset=UTF-8" + lineEnd);
        dataOutputStream.writeBytes(lineEnd);
//        parameterValue = new String(parameterValue.getBytes(), "UTF-8");
        dataOutputStream.write(parameterValue.getBytes());
        dataOutputStream.writeBytes(lineEnd);
        return dataOutputStream;
    }

    private Response.Listener<JSONObject> mHttpOkResponseListener = new Response.Listener<JSONObject>() {
        @Override
        public void onResponse(JSONObject response) {
            // API执行成功，直接把数据返回到JS层
            String responseString = response.toString();

            // 同步本地店铺缓存信息
            if (urlString.equals(WebServerConfigManager.getWebServiceUrl() + "shop/query")) {
                JSONObject userInfoJsonObject;
                try {
                    userInfoJsonObject = new JSONObject(responseString);
                    String allShops = userInfoJsonObject.optString("retbody");
                    AccountManager.getInstance().getCurrentAccount().setAllShops(allShops);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            if (mCallBack != null) {
                JsCallNativeEventResponseData responseJson = new JsCallNativeEventResponseData(JsCallNativeEventResponseData.ACK_SUCCESS,
                        "", responseString);
                mCallBack.onCallBack(responseJson);
                Log.v(TAG, "Native API request callback: " + responseString);
            }
        }
    };

    /**
     * Status code是500的时候，返回下吗错误：
     * {"message": "Internal Server Error\n'product_code'", "retcode": 4000, "retbody": null}
     * case 4000: //未知错误
     * case 4001: //密码错误
     * case 4002: //老板手机已注册  //按照现在的设计登录是用云平台账号，不需要注册。
     * case 4003: //老板手机未注册  //按照现在的设计登录是用云平台账号，不需要注册。
     * case 4004: //老板手机或员工手机错误. // UI 显示的USERID是手机吗？需要确认
     * case 4005: //员工密码错误   // 现在老板和员工登录用一个API，不应区分老板，员工
     * case 4006: //产品未找到
     * case 4007: //库存未找到
     * case 4008: //条码或shop ID错误 //应该分开？
     * case 4009: //产品已存在
     * case 4010: //库存已存在 // 4009 和 4010区别？
     * case 4011: //店铺已存在
     * case 4019: //店铺不存在
     * case 4012: //员工已存在
     * case 4013: //员工不存在
     * case 4014: //供应商已存在
     * case 4018: //供应商不存在
     * case 4015: //客户已存在
     * case 4017: //客户不存在
     * case 4016: //库存不足
     * case 4020: //token超过有效期
     */
    private Response.ErrorListener mHttpErrorResponseListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            LogHelper.getInstance().e(TAG, error.toString());
            WholesaleHttpResponse wholesaleHttpResponse = processNetworkError(error);

            // 如果用户是登陆状态，但是token超时，自动帮用户从后台自动登陆一次
            NetworkResponse response = error.networkResponse;
            if (response != null && response.statusCode == OUTTIME_RET_CODE && AccountManager.getInstance().isLogin()) {
                Account currentUser = AccountManager.getInstance().getCurrentAccount();
                LoginCloudApiRequest loginCloudApiRequest = new LoginCloudApiRequest(currentUser.getCompanyAccount(), currentUser.getLoginName(), currentUser.getPassword());

                // re－login
                login(loginCloudApiRequest, new ILoginCallback() {
                    @Override
                    public void onSuccessCallback(String loginResponse) {
                        // 自动重新登录成功，重新请求API
                        httpJsonRequest(mEvent, mCallBack);
                    }

                    @Override
                    public void onErrorCallback(WholesaleHttpResponse response) {
                        // 提示用户登录失败，跳转到登录页面
                        Intent intent = new Intent(WholesaleApplication.getAppContext(), SplashActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                        WholesaleApplication.getAppContext().startActivity(intent);
                    }
                });
            } else {
                String errorMessage = VolleyErrorHelper.getMessage(error);
                String errorResponse = wholesaleHttpResponse.toString();
                LogHelper.getInstance().e(TAG, "服务器返回错误：" + errorResponse);

                // 返回错误信息，直接把错误结果返回到JS层
                if (mCallBack != null) {
                    JsCallNativeEventResponseData responseJson = new JsCallNativeEventResponseData(JsCallNativeEventResponseData.ACK_FAILED,
                            errorMessage, errorResponse);
                    mCallBack.onCallBack(responseJson);
                }
            }

        }
    };

    public void logout(){
        String logoutUrl = WebServerConfigManager.getWebServiceUrl() + "reg/employee/logout";
        JSONObject jsonObject = null;

        LogHelper.getInstance().v(TAG, "登出：loginUrl=" + logoutUrl + ", jsonObject＝" );
        JsonObjectRequest mJsonObjectRequest = new JsonObjectRequestWithCookie(Method.POST, logoutUrl, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.e(TAG, "登出成功");
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String errorMessage = VolleyErrorHelper.getMessage(error);
                Log.e(TAG, "登出失败"+ errorMessage);
            }
        });

        WholesaleApplication.getRequestQueue().add(mJsonObjectRequest);
    }

    /**
     * for token timeout
     * ('reg/employee/login', 'POST', {'phone': "", 'password': "", 'company_account':""}
     * {"message":"success","retcode":200,"retbody":{"role":0,"token":"3ed01fe8648f41c69c141f4c95034050","phone":"18516058966"}}
     */
    public void login(final LoginCloudApiRequest loginCloudApiRequest, final ILoginCallback loginCallback) {
        String loginUrl = WebServerConfigManager.getWebServiceUrl() + "reg/employee/login";
        JSONObject jsonObject = loginCloudApiRequest.getJsonObject();
        LogHelper.getInstance().v(TAG, "登录：loginUrl=" + loginUrl + ", jsonObject＝" + jsonObject.toString());

        JsonObjectRequest mJsonObjectRequest =
                new JsonObjectRequest(Method.POST, loginUrl, jsonObject,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                LogHelper.getInstance().v(TAG, "登录成功：" + response.toString());

                                int responseCode = response.optInt(API_RESPONSE_RET_CODE);
                                String responseMessage = response.optString(API_RESPONSE_MESSAGE);
                                JSONObject responseBody = response.optJSONObject(API_RESPONSE_RET_BODY);
                                String allShops = responseBody.optString("shop_list");
                                String picSrc = responseBody.optString("pic_src");
                                String picHdSrc = responseBody.optString("pic_hd_src");

                                LoginWebServiceResponse loginWebServiceResponse = new Gson()
                                        .fromJson(responseBody.toString(), LoginWebServiceResponse.class);

                                String companyAccount = loginCloudApiRequest.getCompanyAccount();
                                String loginName = loginCloudApiRequest.getLoginName();
                                String password = loginCloudApiRequest.getPassword();
                                String userName = loginWebServiceResponse.getName();
                                String employeeId = loginWebServiceResponse.getEmployeeId();
                                String role = loginWebServiceResponse.getRole();
                                String token = loginWebServiceResponse.getToken();
                                String shopId = "";
                                String shopName = "";


                                Account account = AccountManager.getInstance().getAccount(companyAccount, loginName);
                                boolean isCurrentInDb = false;
                                if (account != null) {
                                    LoginWebServiceResponse.LoginShopResponse[] shopList = loginWebServiceResponse.getShopList();
                                    if (shopList != null && shopList.length > 0) {
                                        for (int i = 0; i < shopList.length; i++) {
                                            if (account.getCurrentShopId().equals(shopList[i].mShopId)) {
                                                isCurrentInDb = true;
                                                break;
                                            } else {
                                                isCurrentInDb = false;
                                            }
                                        }
                                    }
                                    if (isCurrentInDb) {
                                        shopId = account.getCurrentShopId();
                                        shopName = account.getShopName();
                                    } else  {
                                        if (shopList != null && shopList.length > 0) {
                                            LoginWebServiceResponse.LoginShopResponse shop = shopList[0];
                                            shopId = shop.mShopId;
                                            shopName = shop.mShopName;
                                        }
                                    }
                                } else {
                                    LoginWebServiceResponse.LoginShopResponse[] shopList = loginWebServiceResponse.getShopList();
                                    if (shopList != null && shopList.length > 0) {
                                        LoginWebServiceResponse.LoginShopResponse shop = shopList[0];
                                        shopId = shop.mShopId;
                                        shopName = shop.mShopName;
                                    }
                                }

                                // 把数据保存到数据库里面
                                AccountManager.getInstance().updateAccount(companyAccount, employeeId, loginName, password, role, token,
                                        shopId, shopName, allShops, userName);

                                if (responseCode == RET_CODE_SUCCESS) {
                                    // API执行成功
                                    LogHelper.getInstance().v(TAG, "Login Response Body: " + response.toString());
                                    loginCallback.onSuccessCallback(response.toString());
                                } else {
                                    // 网络请求成果，但是有可能API执行失败。比如登陆密码错误。
                                    LogHelper.getInstance().v(TAG, "有返回值，但是登录失败，应该帐号密码问题。" + response.toString());
                                    //{"retbody":{},"message":"the number not register","retcode":4003}
                                    loginCallback.onErrorCallback(new WholesaleHttpResponse(responseCode, responseMessage, response));
                                }

                                String picNormalSrc = parseImageUrl(picSrc);
                                String picHdNormalSrc = parseImageUrl(picHdSrc);
                                SharePreferenceUtil.setPrefString(SHARE_PREFERENCE_APP_USER_PICS, SHARE_PREFERENCE_APP_USER_PICS_URL, picNormalSrc);
                                SharePreferenceUtil.setPrefString(SHARE_PREFERENCE_APP_USER_PICS, SHARE_PREFERENCE_APP_USER_PICS_HD_URL, picHdNormalSrc);

                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        loginCallback.onErrorCallback(processNetworkError(error));
                    }
                });

        WholesaleApplication.getRequestQueue().add(mJsonObjectRequest);
    }


    public void httpGetEmployeeDetail(final GetCustomerDetailApiRequest getCustomerDetailApiRequest, final NativeJsonResponseListener<JSONObject> listener) {
        String employeeUrl = WebServerConfigManager.getWebServiceUrl() + "employee/detail";
        JSONObject jsonObject = getCustomerDetailApiRequest.getJsonObject();
        //此处会导致app crash，因为API改变了，对以前的API造成影响，以前的版本用不了
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequestWithCookie(Method.POST, employeeUrl, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                LogHelper.getInstance().v(TAG, "获取category成功：" + response.toString());

                int responseCode = response.optInt(API_RESPONSE_RET_CODE);

                if (responseCode != RET_CODE_SUCCESS) {
                    LogHelper.getInstance().e(TAG, "http请求成功, 逻辑有误" + response.toString());
                    return;
                }

                String responseMessage = response.optString(API_RESPONSE_MESSAGE);
                JSONObject responseBody = response.optJSONObject(API_RESPONSE_RET_BODY);
                String picSrc = responseBody.optString("pic_src");
                String picHdSrc = responseBody.optString("pic_hd_src");

                String picNormalSrc = parseImageUrl(picSrc);
                String picHdNormalSrc = parseImageUrl(picHdSrc);
                SharePreferenceUtil.setPrefString(SHARE_PREFERENCE_APP_USER_PICS, SHARE_PREFERENCE_APP_USER_PICS_URL, picNormalSrc);
                SharePreferenceUtil.setPrefString(SHARE_PREFERENCE_APP_USER_PICS, SHARE_PREFERENCE_APP_USER_PICS_HD_URL, picHdNormalSrc);

                if (listener != null) {
                    listener.listener(null);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                LogHelper.getInstance().e(TAG, "获取category失败：");
                processNetworkError(error);
                if (listener != null) {
                    listener.errorListener(error.toString());
                }

            }
        });

        WholesaleApplication.getRequestQueue().add(jsonObjectRequest);
    }

    public void httpGetCategories(final UpdateCategoryCloudApiRequest updateCategoryCloudApiRequest, final NativeJsonResponseListener<JSONObject> listener) {
        String categoryUrl = WebServerConfigManager.getWebServiceUrl() + "category/query";
        JSONObject jsonObject = updateCategoryCloudApiRequest.getJsonObject();
        //此处会导致app crash，因为API改变了，对以前的API造成影响，以前的版本用不了
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequestWithCookie(Method.POST, categoryUrl, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                LogHelper.getInstance().v(TAG, "获取category成功：" + response.toString());

                int responseCode = response.optInt(API_RESPONSE_RET_CODE);
                if (responseCode != RET_CODE_SUCCESS) {
                    LogHelper.getInstance().e(TAG, "http请求成功, 逻辑有误" + response.toString());
                    return;
                }

                String userId = updateCategoryCloudApiRequest.getmEmplyeeId();

                ArrayList<Category> categories = CategoryDAO.queryCategory(userId);
                if (categories == null) {
                    LogHelper.getInstance().e(TAG, "Local Can not open Category Table");
                    return;
                }

                JSONArray categoryResponseBody = response.optJSONArray(API_RESPONSE_RET_BODY);
                String categoryJsonStr = categoryResponseBody.toString();
                CategoriesManager categoriesManager = CategoriesManager.getInstance();

                if (updateCategoryCloudApiRequest.getmShopId() == 0) {
                    categoriesManager.parseArrayToFullCategory(categoryJsonStr);
                } else {
                    categoriesManager.parseArrayToArrayList(categoryJsonStr);
                }

                if (listener != null) {
                    listener.listener(null);
                }
//                Iterator<String> iters = categoryResponseBody.keys();

//                while (iters.hasNext()){
//                    String key = iters.next();
//                    String value = categoryResponseBody.optString(key, "");
//                    if (value.isEmpty()){
//                        continue;
//                    }
//
//                    Category category = new Category(key, value);
//                    if (categories.contains(category)){
//                        continue;
//                    }
//                    categories.add(category);
//                    CategoryDAO.addCatyegoryItem(category, userId);
//                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                LogHelper.getInstance().e(TAG, "获取category失败：");
                processNetworkError(error);
                if (listener != null) {
                    listener.errorListener(error.toString());
                }

            }
        });

        WholesaleApplication.getRequestQueue().add(jsonObjectRequest);
    }

    public void getCategories(final UpdateCategoryCloudApiRequest updateCategoryCloudApiRequest, final NativeJsonResponseListener<JSONObject> listener) {
        String categoryUrl = WebServerConfigManager.getWebServiceUrl() + "category/query";
        JSONObject jsonObject = updateCategoryCloudApiRequest.getJsonObject();
        //此处会导致app crash，因为API改变了，对以前的API造成影响，以前的版本用不了
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequestWithCookie(Method.POST, categoryUrl, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                LogHelper.getInstance().v(TAG, "分类列表：" + response.toString());
                int responseCode = response.optInt(API_RESPONSE_RET_CODE, 0);
                String message = response.optString(API_RESPONSE_MESSAGE, "no message");
                if (responseCode != RET_CODE_SUCCESS) {
                    LogHelper.getInstance().e(TAG, "http请求成功, 逻辑有误" + response.toString());
                    listener.errorListener(message);
                }

//                JSONObject retJsonObject = response.optJSONObject(API_RESPONSE_RET_BODY);
                if (listener != null) {
                    listener.listener(response);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                LogHelper.getInstance().e(TAG, "分类列表");
                String errorString = processNetworkError(error).toString();
                try {
                    JSONObject jsonObject1 = new JSONObject(errorString);
                    String retcode = jsonObject1.get("retcode").toString();
//                    if (retcode.equals("4032")){
                    listener.errorListener(error.getMessage());
//                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        WholesaleApplication.getRequestQueue().add(jsonObjectRequest);
    }

    public void httpProductDetail(final ProductDetailRequest request,
                                  final NativeJsonResponseListener<JSONObject> listener) {
        final String productDetailUrl = WebServerConfigManager.getWebServiceUrl() + "sku_product/detail";
        JSONObject jsonObject = request.getJsonObject();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequestWithCookie(Method.POST, productDetailUrl,
                jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                LogHelper.getInstance().v(TAG, "查询货品详情" + response.toString());
                int responseCode = response.optInt(API_RESPONSE_RET_CODE, 0);
                String message = response.optString(API_RESPONSE_MESSAGE, "no message");
                if (responseCode != RET_CODE_SUCCESS) {
                    LogHelper.getInstance().e(TAG, "http请求成功, 逻辑有误" + response.toString());
                    listener.errorListener(message);
                    return;
                }

                JSONObject bodyJsonObject = response.optJSONObject(API_RESPONSE_RET_BODY);
                listener.listener(bodyJsonObject);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                LogHelper.getInstance().e(TAG, "查询货品详情失败：");
                String errorString = processNetworkError(error).toString();
                try {
                    JSONObject jsonObject1 = new JSONObject(errorString);
                    String retcode = jsonObject1.get("retcode").toString();
                    listener.errorListener(retcode);
                    processNetworkError(error);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        jsonObjectRequest.setTag("TAG_CANCEL");
        WholesaleApplication.getRequestQueue().add(jsonObjectRequest);
    }

    public void httpSkuProductDetail(final ProductDetailRequest request,
                                     final NativeJsonResponseListener<JSONObject> listener) {
        final String productDetailUrl = WebServerConfigManager.getWebServiceUrl() + "sku_product/detail";
        JSONObject jsonObject = request.getJsonObject();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequestWithCookie(Method.POST, productDetailUrl,
                jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                LogHelper.getInstance().v(TAG, "查询货品详情" + response.toString());
                int responseCode = response.optInt(API_RESPONSE_RET_CODE, 0);
                String message = response.optString(API_RESPONSE_MESSAGE, "no message");
                if (responseCode != RET_CODE_SUCCESS) {
                    LogHelper.getInstance().e(TAG, "http请求成功, 逻辑有误" + response.toString());
                    listener.errorListener(message);
                    return;
                }

                JSONObject bodyJsonObject = response.optJSONObject(API_RESPONSE_RET_BODY);
                listener.listener(bodyJsonObject);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                LogHelper.getInstance().e(TAG, "查询货品详情失败：");
                listener.errorListener(error.getMessage());
                processNetworkError(error);
            }
        });

        WholesaleApplication.getRequestQueue().add(jsonObjectRequest);
    }


    public void httpSkuProductDetailWithBean(final ProductDetailBean request,
                                     final NativeJsonResponseListener<JSONObject> listener) {
        final String productDetailUrl = WebServerConfigManager.getWebServiceUrl() + "sku_product/detail";
        JSONObject jsonObject = request.getJsonObject();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequestWithCookie(Method.POST, productDetailUrl,
                jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                LogHelper.getInstance().v(TAG, "查询货品详情" + response.toString());
                int responseCode = response.optInt(API_RESPONSE_RET_CODE, 0);
                String message = response.optString(API_RESPONSE_MESSAGE, "no message");
                if (responseCode != RET_CODE_SUCCESS) {
                    LogHelper.getInstance().e(TAG, "http请求成功, 逻辑有误" + response.toString());
                    listener.errorListener(message);
                    return;
                }

                JSONObject bodyJsonObject = response.optJSONObject(API_RESPONSE_RET_BODY);
                listener.listener(bodyJsonObject);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                LogHelper.getInstance().e(TAG, "查询货品详情失败：");
                listener.errorListener(error.getMessage());
                processNetworkError(error);
            }
        });

        jsonObjectRequest.setTag("TAG_CANCEL");

        WholesaleApplication.getRequestQueue().add(jsonObjectRequest);
    }

    public void httpSkuProductPriceDiffDetail(final ProductDetailPriceDiffRequest request,
                                     final NativeJsonResponseListener<JSONObject> listener) {
        final String productDetailUrl = WebServerConfigManager.getWebServiceUrl() + "sku_product/detail";
        JSONObject jsonObject = request.getJsonObject();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequestWithCookie(Method.POST, productDetailUrl,
                jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                LogHelper.getInstance().v(TAG, "查询货品详情" + response.toString());
                int responseCode = response.optInt(API_RESPONSE_RET_CODE, 0);
                String message = response.optString(API_RESPONSE_MESSAGE, "no message");
                if (responseCode != RET_CODE_SUCCESS) {
                    LogHelper.getInstance().e(TAG, "http请求成功, 逻辑有误" + response.toString());
                    listener.errorListener(message);
                    return;
                }

                JSONObject bodyJsonObject = response.optJSONObject(API_RESPONSE_RET_BODY);
                listener.listener(bodyJsonObject);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                LogHelper.getInstance().e(TAG, "查询货品详情失败：");
                listener.errorListener(error.getMessage());
                processNetworkError(error);
            }
        });

        WholesaleApplication.getRequestQueue().add(jsonObjectRequest);
    }

    public void httpProductSale(final PreTransactionSaleRequest request,
                                final NativeJsonResponseListener<JSONObject> listener) {
        final String productSaleUrl = WebServerConfigManager.getWebServiceUrl() + "inventory/product_list";
        JSONObject jsonObject = request.getJsonObject();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequestWithCookie(Method.POST, productSaleUrl,
                jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                LogHelper.getInstance().v(TAG, "提交订单" + response.toString());
                int responseCode = response.optInt(API_RESPONSE_RET_CODE, 0);
                String message = response.optString(API_RESPONSE_MESSAGE, "no message");
                if (responseCode != RET_CODE_SUCCESS) {
                    LogHelper.getInstance().e(TAG, "http请求成功, 逻辑有误" + response.toString());
                    listener.errorListener(message);
                    return;
                }

                JSONObject bodyJsonObject = response.optJSONObject(API_RESPONSE_RET_BODY);
                listener.listener(bodyJsonObject);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                LogHelper.getInstance().e(TAG, "提交订单失败：");
                listener.errorListener(error.getMessage());
                processNetworkError(error);
            }
        });


        WholesaleApplication.getRequestQueue().add(jsonObjectRequest);
    }

    public void cancelRequest(Object tag){
        WholesaleApplication.getRequestQueue().cancelAll(tag);
    }

    public void httpUpdateInventories(final UpdateInventoryCloudApiRequest request,
                                      final NativeJsonResponseListener<JSONObject> listener) {
        // need create time table for collection last query time
        final String inventoryUrl = WebServerConfigManager.getWebServiceUrl() + "sku_product/list";
        JSONObject jsonObject = request.getJsonObject();

        final Gson gson = new Gson();
        String shopName = AccountManager.getInstance().getCurrentAccount().getShopName();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequestWithCookie(Method.POST, inventoryUrl,
                jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                String shopName = AccountManager.getInstance().getCurrentAccount().getShopName();
                LogHelper.getInstance().v(TAG, "获取Inventory成功：" + response.toString());

                int responseCode = response.optInt(API_RESPONSE_RET_CODE, 123);
                if (responseCode != RET_CODE_SUCCESS) {
                    LogHelper.getInstance().e(TAG, "http请求成功, 逻辑有误" + response.toString());
                    return;
                }
                JSONObject inventoryResponseBody = response.optJSONObject(API_RESPONSE_RET_BODY);

                // added inventory
                JSONArray inventoryJsonArray = inventoryResponseBody.optJSONArray("product_list");
                LogHelper.e(TAG, "product_list" + inventoryJsonArray.toString());
                for (int i = 0; i < inventoryJsonArray.length(); i++) {
                    JSONObject inventoryItem = inventoryJsonArray.optJSONObject(i);
                    Inventory invetory = gson.fromJson(inventoryItem.toString(), Inventory.class);

                    Inventory dbInventory = InventoryDAO.queryInventoryItem(request.getmShopId(), invetory.getProductId());

                    if (dbInventory != null) {
                        InventoryDAO.updateInventory(invetory, request.getmShopId());
                    } else {
                        InventoryDAO.addInventoryItem(invetory, request.getmShopId());
                    }
                }

                // remove inventory
                JSONArray removeInventoryJsonArray = inventoryResponseBody.optJSONArray("deleted_list");
                for (int i = 0; i < removeInventoryJsonArray.length(); i++) {
                    JSONObject inventoryItem = removeInventoryJsonArray.optJSONObject(i);
                    Inventory invetory = gson.fromJson(inventoryItem.toString(), Inventory.class);

                    Inventory dbInventory = InventoryDAO.queryInventoryItem(request.getmShopId(),
                            invetory.getProductId());

                    if (dbInventory != null) {
                        InventoryDAO.removeInventoryItem(request.getmShopId(), invetory.getProductId());
                    } else {
                        LogHelper.getInstance().e(TAG, "no invetory in db");
                    }
                }


                String isNextPage = inventoryResponseBody.optString("next_page");
                String time = inventoryResponseBody.optString("last_update_time");

                if (IncrementalDAO.isExistItem(Inventory.class, request.getmShopId())) {
                    IncrementalDAO.updateIncrementalItem(Inventory.class, time, request.getmShopId());
                } else {
                    Log.e(TAG, "Error");
                    IncrementalDAO.addIncrementalItem(Inventory.class, time, request.getmShopId());
                }

                if (!isNextPage.equals("false")) {
                    UpdateInventoryCloudApiRequest nextPageRequest = new UpdateInventoryCloudApiRequest(request.getmShopId(), time);
                    httpUpdateInventories(nextPageRequest, listener);
                } else {
                    if (listener != null) {
                        listener.listener(inventoryResponseBody);
                    }
                }

                LogHelper.getInstance().v(TAG, "更新inventory成功：");
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                LogHelper.getInstance().e(TAG, "获取Inventory失败：");
                processNetworkError(error);
            }
        });

        WholesaleApplication.getRequestQueue().add(jsonObjectRequest);
    }

    public void httpGetTransactionOrders(final TransactionOrderRequest request,
                                         final NativeJsonResponseListener<JSONObject> listener) {
        final String transactionUrl = WebServerConfigManager.getWebServiceUrl() + "transaction/sale/list";
        JSONObject jsonObject = request.getJsonObject();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequestWithCookie(Method.POST, transactionUrl, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                LogHelper.getInstance().v(TAG, "获取 交易记录：" + response.toString());
                int responseCode = response.optInt(API_RESPONSE_RET_CODE, 123);
                String message = response.optString(API_RESPONSE_MESSAGE, "no message");
                if (responseCode != RET_CODE_SUCCESS) {
                    LogHelper.getInstance().e(TAG, "http请求成功, 逻辑有误" + response.toString());
                    listener.errorListener(message);
                    return;
                }

                JSONObject bodyJsonObject = response.optJSONObject(API_RESPONSE_RET_BODY);
                listener.listener(bodyJsonObject);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                LogHelper.getInstance().e(TAG, "获取交易记录失败：");
                processNetworkError(error);
            }
        });

        WholesaleApplication.getRequestQueue().add(jsonObjectRequest);
    }

    /**
     * 销售查询：/transaction/sale/search
     *
     * @param request
     * @param listener
     */
    public void httpQueryTransactionOrders(final TransactionQueryRequest request,
                                           final NativeJsonResponseListener<JSONObject> listener) {
        final String queryTransactionUrl = WebServerConfigManager.getWebServiceUrl() + "transaction/sale/search";

        JSONObject jsonObject = request.getJsonObject();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequestWithCookie(Method.POST, queryTransactionUrl, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                LogHelper.getInstance().v(TAG, "获取 销售查询：" + response.toString());
                int responseCode = response.optInt(API_RESPONSE_RET_CODE, 0);
                String message = response.optString(API_RESPONSE_MESSAGE, "no message");
                if (responseCode != RET_CODE_SUCCESS) {
                    LogHelper.getInstance().e(TAG, "http请求成功, 逻辑有误" + response.toString());
                    listener.errorListener(message);
                    return;
                }

                JSONObject bodyJsonObject = response.optJSONObject(API_RESPONSE_RET_BODY);
                listener.listener(bodyJsonObject);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                LogHelper.getInstance().e(TAG, "获取销售查询失败：");
                listener.errorListener(error.getMessage());
                processNetworkError(error);
            }
        });

        WholesaleApplication.getRequestQueue().add(jsonObjectRequest);
    }

    /**
     * 销售统计：/transaction/sale/get_numbers
     *
     * @param request
     * @param listener
     */
    public void httpQueryTransactionCountOrders(final TransactionCountRequest request,
                                                final NativeJsonResponseListener<JSONObject> listener) {
        final String queryTransactionUrl = WebServerConfigManager.getWebServiceUrl() + "transaction/sale/get_numbers";

        JSONObject jsonObject = request.getJsonObject();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequestWithCookie(Method.POST, queryTransactionUrl, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                LogHelper.getInstance().v(TAG, "获取 交易数量统计：" + response.toString());
                int responseCode = response.optInt(API_RESPONSE_RET_CODE, 0);
                String message = response.optString(API_RESPONSE_MESSAGE, "no message");
                if (responseCode != RET_CODE_SUCCESS) {
                    LogHelper.getInstance().e(TAG, "http请求成功, 逻辑有误" + response.toString());
                    listener.errorListener(message);
                    return;
                }

                JSONObject bodyJsonObject = response.optJSONObject(API_RESPONSE_RET_BODY);
                listener.listener(bodyJsonObject);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                LogHelper.getInstance().e(TAG, "获取交易数量统计失败：");
                processNetworkError(error);
            }
        });

        WholesaleApplication.getRequestQueue().add(jsonObjectRequest);
    }

    /**
     * shop query  shop/query
     *
     * @param request
     * @param listener
     */

    public void httpQueryShop(final ShopQueryRequest request,
                              final NativeJsonResponseListener<JSONObject> listener) {
        final String queryShopUrl = WebServerConfigManager.getWebServiceUrl() + "shop/query";

        JSONObject jsonObject = request.getJsonObject();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequestWithCookie(Method.POST, queryShopUrl, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                LogHelper.getInstance().v(TAG, "获取 店铺列表：" + response.toString());
                int responseCode = response.optInt(API_RESPONSE_RET_CODE, 0);
                String message = response.optString(API_RESPONSE_MESSAGE, "no message");
                if (responseCode != RET_CODE_SUCCESS) {
                    LogHelper.getInstance().e(TAG, "http请求成功, 逻辑有误" + response.toString());
                    listener.errorListener(message);
                    return;
                }

                JSONObject bodyJsonObject = response.optJSONObject(API_RESPONSE_RET_BODY);
                listener.listener(bodyJsonObject);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                LogHelper.getInstance().e(TAG, "获取店铺列表失败：");
                processNetworkError(error);
            }
        });

        WholesaleApplication.getRequestQueue().add(jsonObjectRequest);

    }

    public void httpQueryTransactionDetailOrders(final TransactionDetailRequest request,
                                                 final NativeJsonResponseListener<JSONObject> listener) {
        final String queryTransactionUrl = WebServerConfigManager.getWebServiceUrl() + "transaction/sale/detail";

        JSONObject jsonObject = request.getJsonObject();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequestWithCookie(Method.POST, queryTransactionUrl, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                LogHelper.getInstance().v(TAG, "单个订单详情查询：" + response.toString());
                int responseCode = response.optInt(API_RESPONSE_RET_CODE, 0);
                String message = response.optString(API_RESPONSE_MESSAGE, "no message");
                if (responseCode != RET_CODE_SUCCESS) {
                    LogHelper.getInstance().e(TAG, "http请求成功, 逻辑有误" + response.toString());
                    listener.errorListener(message);
                    return;
                }

                JSONObject bodyJsonObject = response.optJSONObject(API_RESPONSE_RET_BODY);
                listener.listener(bodyJsonObject);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                LogHelper.getInstance().e(TAG, "单个订单详情查询失败：");
                processNetworkError(error);
            }
        });

        WholesaleApplication.getRequestQueue().add(jsonObjectRequest);

    }

    public void httpGetTransactionStock(TransactionStockRequest request,
                                        final NativeJsonResponseListener<JSONObject> listener) {
        final String transactionUrl = WebServerConfigManager.getWebServiceUrl() + "transaction/buy/list";

        JSONObject jsonObject = request.getJsonObject();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequestWithCookie(Method.POST, transactionUrl, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                LogHelper.getInstance().v(TAG, "获取入库成功：" + response.toString());
                int responseCode = response.optInt(API_RESPONSE_RET_CODE, 123);
                String message = response.optString(API_RESPONSE_MESSAGE, "no message");
                if (responseCode != RET_CODE_SUCCESS) {
                    LogHelper.getInstance().e(TAG, "http请求成功, 逻辑有误" + response.toString());
                    listener.errorListener(message);
                }

                JSONObject retJsonObject = response.optJSONObject(API_RESPONSE_RET_BODY);
                listener.listener(retJsonObject);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                LogHelper.getInstance().e(TAG, "获取入库失败：");
                processNetworkError(error);
            }
        });

        WholesaleApplication.getRequestQueue().add(jsonObjectRequest);
    }

    public void httpGetTransactionUnconfirmStock(TransactionStockUnconfirmRequest request,
                                                 final NativeJsonResponseListener<JSONObject> listener) {
        final String transactionUrl = WebServerConfigManager.getWebServiceUrl() + "transaction/buy/unconfirmed_list";

        JSONObject jsonObject = request.getJsonObject();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequestWithCookie(Method.POST, transactionUrl, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                LogHelper.getInstance().v(TAG, "获取待入库成功：" + response.toString());
                int responseCode = response.optInt(API_RESPONSE_RET_CODE, 123);
                String message = response.optString(API_RESPONSE_MESSAGE, "no message");
                if (responseCode != RET_CODE_SUCCESS) {
                    LogHelper.getInstance().e(TAG, "http请求成功, 逻辑有误" + response.toString());
                    listener.errorListener(message);
                }

                JSONObject retJsonObject = response.optJSONObject(API_RESPONSE_RET_BODY);
                listener.listener(retJsonObject);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                LogHelper.getInstance().e(TAG, "获取待入库失败：");
                processNetworkError(error);
            }
        });

        WholesaleApplication.getRequestQueue().add(jsonObjectRequest);
    }

    public void httpGetCustomer(final ListCustomerRequest request, final NativeJsonResponseListener<JSONObject> listener) {
        final String customerRequestUrl = WebServerConfigManager.getWebServiceUrl() + "customer/query";
        JSONObject jsonObject = request.getJsonObject();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequestWithCookie(Method.POST, customerRequestUrl, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                LogHelper.getInstance().v(TAG, "获取客户成功：" + response.toString());
                int responseCode = response.optInt(API_RESPONSE_RET_CODE, 123);
                String message = response.optString(API_RESPONSE_MESSAGE, "no message");
                if (responseCode != RET_CODE_SUCCESS) {
                    LogHelper.getInstance().e(TAG, "http请求成功, 逻辑有误" + response.toString());
                    listener.errorListener(message);
                }
                JSONObject retJsonObject = response.optJSONObject(API_RESPONSE_RET_BODY);
                Gson gson = new Gson();

                String shopId = retJsonObject.optString("shop_id");
                // 添加客户
                // API返回多了一层Array
                JSONArray dupCustomerJsonArray = retJsonObject.optJSONArray("customer_list");
                JSONObject customerJsonObject = dupCustomerJsonArray.optJSONObject(0);

                Iterator<String> iter = customerJsonObject.keys();
                while (iter.hasNext()) {
                    String key = iter.next();
                    JSONArray customerJsonArray = customerJsonObject.optJSONArray(key);

                    for (int i = 0; i < customerJsonArray.length(); i++) {
                        JSONObject customerItem = customerJsonArray.optJSONObject(i);
                        Customer customer = gson.fromJson(customerItem.toString(), Customer.class);
                        customer.setGroup(key);
                        customer.setShopId(shopId);

                        Customer customerInDb = CustomerDAO.queryByCustomer(customer.getCustomeId());

                        if (customerInDb != null) {
                            CustomerDAO.updateCustomer(customer, request.getmShopId());
                        } else {
                            CustomerDAO.addCustomer(customer, key);
                        }
                    }
                }

                // API返回多了一层Array
                JSONArray dupDelCustomerJsonArray = retJsonObject.optJSONArray("deleted_list");
                JSONObject deleteJsonObject = dupDelCustomerJsonArray.optJSONObject(0);

                Iterator<String> delIter = deleteJsonObject.keys();
                while (delIter.hasNext()) {
                    String key = delIter.next();
                    JSONArray customerJsonArray = deleteJsonObject.optJSONArray(key);

                    for (int i = 0; customerJsonArray != null && i < customerJsonArray.length(); i++) {
                        JSONObject delCustomerItem = customerJsonArray.optJSONObject(i);
                        Customer customer = gson.fromJson(delCustomerItem.toString(), Customer.class);

                        Customer customerInDb = CustomerDAO.queryByCustomer(customer.getCustomeId());

                        if (customerInDb != null) {
                            CustomerDAO.removeCustomer(request.getmShopId(), customer.getCustomeId());
                        } else {
                        }
                    }
                }

                String isNextPage = retJsonObject.optString("next_page");
                String time = retJsonObject.optString("last_update_time");

                if (IncrementalDAO.isExistItem(Customer.class, request.getmShopId())) {
                    IncrementalDAO.updateIncrementalItem(Customer.class, time, request.getmShopId());
                } else {
                    Log.e(TAG, "Error");
                    IncrementalDAO.addIncrementalItem(Customer.class, time, request.getmShopId());
                }

                if (!isNextPage.equals("false")) {
                    ListCustomerRequest nextPageRequest = new ListCustomerRequest(request.getmShopId(), time);
                    httpGetCustomer(nextPageRequest, listener);
                } else {
                    if (listener != null) {
                        listener.listener(retJsonObject);
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                LogHelper.getInstance().e(TAG, "获取客户失败：");
                processNetworkError(error);
            }
        });
        WholesaleApplication.getRequestQueue().add(jsonObjectRequest);
    }

    public void httpGetSupplier(final ListSupplierRequest request, final NativeJsonResponseListener<JSONObject> listener) {
        final String customerRequestUrl = WebServerConfigManager.getWebServiceUrl() + "supplier/query";
        JSONObject jsonObject = request.getJsonObject();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequestWithCookie(Method.POST, customerRequestUrl, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                LogHelper.getInstance().v(TAG, "获取供应商成功：" + response.toString());
                String shopName = AccountManager.getInstance().getCurrentAccount().getShopName();
                int responseCode = response.optInt(API_RESPONSE_RET_CODE, 123);
                String message = response.optString(API_RESPONSE_MESSAGE, "no message");
                if (responseCode != RET_CODE_SUCCESS) {
                    LogHelper.getInstance().e(TAG, "http请求成功, 逻辑有误" + response.toString());
                    listener.errorListener(message);
                }

                Log.d(TAG, "请求增量更新:" + response);

                JSONObject retJsonObject = response.optJSONObject(API_RESPONSE_RET_BODY);
                Gson gson = new Gson();

                String shopId = request.getmShopId();
                // 添加客户

                JSONObject vendorJsonObject = retJsonObject.optJSONObject("supplier_list");

                Iterator<String> iter = vendorJsonObject.keys();
                while (iter.hasNext()) {
                    String key = iter.next();
                    JSONArray vendorJsonArray = vendorJsonObject.optJSONArray(key);

                    for (int i = 0; i < vendorJsonArray.length(); i++) {
                        JSONObject vendorItem = vendorJsonArray.optJSONObject(i);
                        Supplier supplier = gson.fromJson(vendorItem.toString(), Supplier.class);
                        supplier.setShopId(shopId);

                        Supplier supplierInDb = SupplierDAO.queryVendor(shopId, supplier.getSupplierId());

                        if (supplierInDb != null) {
                            SupplierDAO.updateVendor(supplier, request.getmShopId());
                        } else {
                            SupplierDAO.addVendor(supplier);
                        }
                    }
                }


                JSONObject deleteJsonObject = retJsonObject.optJSONObject("deleted_list");
                Iterator<String> delIter = deleteJsonObject.keys();
                while (delIter.hasNext()) {
                    String key = delIter.next();
                    JSONArray delVendorJsonArray = deleteJsonObject.optJSONArray(key);

                    for (int i = 0; i < delVendorJsonArray.length(); i++) {
                        JSONObject delCustomerItem = delVendorJsonArray.optJSONObject(i);
                        Supplier supplier = gson.fromJson(delCustomerItem.toString(), Supplier.class);

                        Supplier supplierInDb = SupplierDAO.queryVendor(shopId, supplier.getSupplierId());

                        if (supplierInDb != null) {
                            SupplierDAO.removeVendor(request.getmShopId(), supplier.getSupplierId());
                        } else {

                        }
                    }
                }

                String isNextPage = retJsonObject.optString("next_page");
                String time = retJsonObject.optString("last_update_time");

                if (IncrementalDAO.isExistItem(Supplier.class, request.getmShopId())) {
                    IncrementalDAO.updateIncrementalItem(Supplier.class, time, request.getmShopId());
                } else {
                    IncrementalDAO.addIncrementalItem(Supplier.class, time, request.getmShopId());
                }

                if (!isNextPage.equals("false")) {
                    ListSupplierRequest nextPageRequest = new ListSupplierRequest(request.getmShopId(), time);
                    httpGetSupplier(nextPageRequest, listener);
                } else {
                    if (listener != null) {
                        listener.listener(retJsonObject);
                    }
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                LogHelper.getInstance().e(TAG, "获取供应商失败：");
                processNetworkError(error);
            }
        });
        WholesaleApplication.getRequestQueue().add(jsonObjectRequest);
    }

    public void httpOrderDetail(final TransactionDetailRequest request,
                                final NativeJsonResponseListener<JSONObject> listener) {
        final String orderDetailUrl = WebServerConfigManager.getWebServiceUrl() + "transaction/sale/detail";
        JSONObject jsonObject = request.getJsonObject();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequestWithCookie(Method.POST, orderDetailUrl,
                jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                LogHelper.getInstance().v(TAG, "订单详情" + response.toString());
                int responseCode = response.optInt(API_RESPONSE_RET_CODE, 0);
                String message = response.optString(API_RESPONSE_MESSAGE, "no message");
                if (responseCode != RET_CODE_SUCCESS) {
                    LogHelper.getInstance().e(TAG, "http请求成功, 逻辑有误" + response.toString());
                    listener.errorListener(message);
                    return;
                }

                JSONObject bodyJsonObject = response.optJSONObject(API_RESPONSE_RET_BODY);
                listener.listener(bodyJsonObject);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                LogHelper.getInstance().e(TAG, "查询订单详情详情失败：");
                listener.errorListener(error.getMessage());
                processNetworkError(error);
            }
        });

        WholesaleApplication.getRequestQueue().add(jsonObjectRequest);
    }

    public void httpCategoryAdd(final CategoryAddRequest request, final NativeJsonResponseListener listener) {

        final String requestUrl = WebServerConfigManager.getWebServiceUrl() + "category/add";
        JSONObject jsonObject = request.getJsonObject();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequestWithCookie(Method.POST, requestUrl, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                LogHelper.getInstance().v(TAG, "添加分类：" + response.toString());
                int responseCode = response.optInt(API_RESPONSE_RET_CODE, 0);
                String message = response.optString(API_RESPONSE_MESSAGE, "no message");
                if (responseCode != RET_CODE_SUCCESS) {
                    LogHelper.getInstance().e(TAG, "http请求成功, 逻辑有误" + response.toString());
                    listener.errorListener(message);
                }

                JSONObject retJsonObject = response.optJSONObject(API_RESPONSE_RET_BODY);
                if (listener != null) {
                    listener.listener(retJsonObject);
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                LogHelper.getInstance().e(TAG, "添加分类");
                String errorString = processNetworkError(error).toString();
                try {
                    JSONObject jsonObject1 = new JSONObject(errorString);
                    String retcode = jsonObject1.get("retcode").toString();
                    if (retcode.equals("4032")) {
                        listener.errorListener(error.getMessage());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        WholesaleApplication.getRequestQueue().add(jsonObjectRequest);
    }


    public void httpCategoryDelete(final CategoryDeleteRequest request, final NativeJsonResponseListener listener) {

        final String requestUrl = WebServerConfigManager.getWebServiceUrl() + "category/delete";
        JSONObject jsonObject = request.getJsonObject();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequestWithCookie(Method.POST, requestUrl, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                LogHelper.getInstance().v(TAG, "删除分类：" + response.toString());
                int responseCode = response.optInt(API_RESPONSE_RET_CODE, 0);
                String message = response.optString(API_RESPONSE_MESSAGE, "no message");
                if (responseCode != RET_CODE_SUCCESS) {
                    LogHelper.getInstance().e(TAG, "http请求成功, 逻辑有误" + response.toString());
                    listener.errorListener(message);
                }

                JSONObject retJsonObject = response.optJSONObject(API_RESPONSE_RET_BODY);
                if (listener != null) {
                    listener.listener(retJsonObject);
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                LogHelper.getInstance().e(TAG, "删除分类");
                String errorString = processNetworkError(error).toString();
                try {
                    JSONObject jsonObject1 = new JSONObject(errorString);
                    String retcode = jsonObject1.get("retcode").toString();
                    if (retcode.equals("4033")) {
                        listener.errorListener(error.getMessage());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        WholesaleApplication.getRequestQueue().add(jsonObjectRequest);
    }


    public void httpCategoryUpdate(final CategoryUpdateRequest request, final NativeJsonResponseListener listener) {

        final String requestUrl = WebServerConfigManager.getWebServiceUrl() + "category/update";
        JSONObject jsonObject = request.getJsonObject();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequestWithCookie(Method.POST, requestUrl, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                LogHelper.getInstance().v(TAG, "更新分类：" + response.toString());
                int responseCode = response.optInt(API_RESPONSE_RET_CODE, 0);
                String message = response.optString(API_RESPONSE_MESSAGE, "no message");
                if (responseCode != RET_CODE_SUCCESS) {
                    LogHelper.getInstance().e(TAG, "http请求成功, 逻辑有误" + response.toString());
                    listener.errorListener(message);
                }

                JSONObject retJsonObject = response.optJSONObject(API_RESPONSE_RET_BODY);
                if (listener != null) {
                    listener.listener(retJsonObject);
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                LogHelper.getInstance().e(TAG, "更新分类");
                String errorString = processNetworkError(error).toString();
                try {
                    JSONObject jsonObject1 = new JSONObject(errorString);
                    String retcode = jsonObject1.get("retcode").toString();
                    if (retcode.equals("4032")) {
                        listener.errorListener(error.getMessage());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        WholesaleApplication.getRequestQueue().add(jsonObjectRequest);
    }

    public void httpAddWareHouse(final WareHouseAddRequest wareHouseAddRequest, final NativeJsonResponseListener listener) {
        final String requestUrl = WebServerConfigManager.getWebServiceUrl() + "warehouses/add";
        JSONObject jsonObject = wareHouseAddRequest.getJsonObject();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequestWithCookie(Method.POST, requestUrl, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                LogHelper.getInstance().v(TAG, "添加仓库：" + response.toString());
                int responseCode = response.optInt(API_RESPONSE_RET_CODE, 0);
                String message = response.optString(API_RESPONSE_MESSAGE, "no message");
                if (responseCode != RET_CODE_SUCCESS) {
                    LogHelper.getInstance().e(TAG, "http请求成功, 逻辑有误" + response.toString());
                    listener.errorListener(message);
                }

                JSONObject retJsonObject = response.optJSONObject(API_RESPONSE_RET_BODY);
                if (listener != null) {
                    listener.listener(retJsonObject);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                LogHelper.getInstance().e(TAG, "添加仓库");
                String errorString = processNetworkError(error).toString();
                Log.e("222", "222");
                try {
                    JSONObject jsonObject1 = new JSONObject(errorString);
                    String retcode = jsonObject1.get("retcode").toString();
//                    if (retcode.equals("4032")){
                    listener.errorListener(retcode);
//                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        WholesaleApplication.getRequestQueue().add(jsonObjectRequest);
    }

    public void httpUpdateWareHouse(final WareHouseUpdateReuqest wareHouseUpdateReuqest, final NativeJsonResponseListener listener) {
        final String requestUrl = WebServerConfigManager.getWebServiceUrl() + "warehouses/update";
        JSONObject jsonObject = wareHouseUpdateReuqest.getJsonObject();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequestWithCookie(Method.POST, requestUrl, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                LogHelper.getInstance().v(TAG, "编辑仓库：" + response.toString());
                int responseCode = response.optInt(API_RESPONSE_RET_CODE, 0);
                String message = response.optString(API_RESPONSE_MESSAGE, "no message");
                if (responseCode != RET_CODE_SUCCESS) {
                    LogHelper.getInstance().e(TAG, "http请求成功, 逻辑有误" + response.toString());
                    listener.errorListener(message);
                }

                JSONObject retJsonObject = response.optJSONObject(API_RESPONSE_RET_BODY);
                if (listener != null) {
                    listener.listener(retJsonObject);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                LogHelper.getInstance().e(TAG, "编辑仓库");
                String errorString = processNetworkError(error).toString();
                try {
                    JSONObject jsonObject1 = new JSONObject(errorString);
                    String retcode = jsonObject1.get("retcode").toString();
//                    if (retcode.equals("4032")){
                    listener.errorListener(retcode);
//                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        WholesaleApplication.getRequestQueue().add(jsonObjectRequest);
    }

    public void httpBlockWareHouse(final WareHouseBlockRequest wareHouseBlockRequest, final NativeJsonResponseListener listener) {
        final String requestUrl = WebServerConfigManager.getWebServiceUrl() + "warehouses/block";
        JSONObject jsonObject = wareHouseBlockRequest.getJsonObject();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequestWithCookie(Method.POST, requestUrl, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                LogHelper.getInstance().v(TAG, "停用仓库：" + response.toString());
                int responseCode = response.optInt(API_RESPONSE_RET_CODE, 0);
                String message = response.optString(API_RESPONSE_MESSAGE, "no message");
                if (responseCode != RET_CODE_SUCCESS) {
                    LogHelper.getInstance().e(TAG, "http请求成功, 逻辑有误" + response.toString());
                    listener.errorListener(message);
                }

                JSONObject retJsonObject = response.optJSONObject(API_RESPONSE_RET_BODY);
                if (listener != null) {
                    listener.listener(retJsonObject);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                LogHelper.getInstance().e(TAG, "停用仓库");
                String errorString = processNetworkError(error).toString();
                try {
                    JSONObject jsonObject1 = new JSONObject(errorString);
                    String retcode = jsonObject1.get("retcode").toString();
//                    if (retcode.equals("4032")){
                    listener.errorListener(error.getMessage());
//                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        WholesaleApplication.getRequestQueue().add(jsonObjectRequest);
    }

    public void httpDefaultWareHouse(final WareHouseDefaultRequest wareHouseDefaultRequest, final NativeJsonResponseListener listener) {
        final String requestUrl = WebServerConfigManager.getWebServiceUrl() + "warehouses/default";
        JSONObject jsonObject = wareHouseDefaultRequest.getJsonObject();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequestWithCookie(Method.POST, requestUrl, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                LogHelper.getInstance().v(TAG, "默认仓库设置：" + response.toString());
                int responseCode = response.optInt(API_RESPONSE_RET_CODE, 0);
                String message = response.optString(API_RESPONSE_MESSAGE, "no message");
                if (responseCode != RET_CODE_SUCCESS) {
                    LogHelper.getInstance().e(TAG, "http请求成功, 逻辑有误" + response.toString());
                    listener.errorListener(message);
                }

                JSONObject retJsonObject = response.optJSONObject(API_RESPONSE_RET_BODY);
                if (listener != null) {
                    listener.listener(retJsonObject);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                LogHelper.getInstance().e(TAG, "默认仓库设置");
                String errorString = processNetworkError(error).toString();
                try {
                    JSONObject jsonObject1 = new JSONObject(errorString);
                    String retcode = jsonObject1.get("retcode").toString();
//                    if (retcode.equals("4032")){
                    listener.errorListener(error.getMessage());
//                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        WholesaleApplication.getRequestQueue().add(jsonObjectRequest);
    }

    public void httpDeleteWareHouse(final WareHouseDeleteRequest wareHouseDeleteRequest, final NativeJsonResponseListener listener) {
        final String requestUrl = WebServerConfigManager.getWebServiceUrl() + "warehouses/delete";
        JSONObject jsonObject = wareHouseDeleteRequest.getJsonObject();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequestWithCookie(Method.POST, requestUrl, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                LogHelper.getInstance().v(TAG, "仓库删除：" + response.toString());
                int responseCode = response.optInt(API_RESPONSE_RET_CODE, 0);
                String message = response.optString(API_RESPONSE_MESSAGE, "no message");
                if (responseCode != RET_CODE_SUCCESS) {
                    LogHelper.getInstance().e(TAG, "http请求成功, 逻辑有误" + response.toString());
                    listener.errorListener(message);
                }

                JSONObject retJsonObject = response.optJSONObject(API_RESPONSE_RET_BODY);
                if (listener != null) {
                    listener.listener(retJsonObject);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                LogHelper.getInstance().e(TAG, "仓库删除");
                String errorString = processNetworkError(error).toString();
                try {
                    JSONObject jsonObject1 = new JSONObject(errorString);
                    String retcode = jsonObject1.get("retcode").toString();
//                    if (retcode.equals("4032")){
                    listener.errorListener(retcode);
//                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        WholesaleApplication.getRequestQueue().add(jsonObjectRequest);
    }

    public void httpDetailWareHouse(final WareHouseDetailRequest wareHouseDetailRequest, final NativeJsonResponseListener listener) {
        final String requestUrl = WebServerConfigManager.getWebServiceUrl() + "warehouses/detail";
        JSONObject jsonObject = wareHouseDetailRequest.getJsonObject();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequestWithCookie(Method.POST, requestUrl, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                LogHelper.getInstance().v(TAG, "仓库详情：" + response.toString());
                int responseCode = response.optInt(API_RESPONSE_RET_CODE, 0);
                String message = response.optString(API_RESPONSE_MESSAGE, "no message");
                if (responseCode != RET_CODE_SUCCESS) {
                    LogHelper.getInstance().e(TAG, "http请求成功, 逻辑有误" + response.toString());
                    listener.errorListener(message);
                }

                JSONObject retJsonObject = response.optJSONObject(API_RESPONSE_RET_BODY);
                if (listener != null) {
                    listener.listener(retJsonObject);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                LogHelper.getInstance().e(TAG, "仓库详情");
                String errorString = processNetworkError(error).toString();
                try {
                    JSONObject jsonObject1 = new JSONObject(errorString);
                    String retcode = jsonObject1.get("retcode").toString();
//                    if (retcode.equals("4032")){
                    listener.errorListener(error.getMessage());
//                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        WholesaleApplication.getRequestQueue().add(jsonObjectRequest);
    }

    public void httpListWareHouse(final WareHouseListRequest wareHouseListRequest, final NativeJsonResponseListener listener) {
        final String requestUrl = WebServerConfigManager.getWebServiceUrl() + "warehouses/list";
        JSONObject jsonObject = wareHouseListRequest.getJsonObject();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequestWithCookie(Method.POST, requestUrl, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                LogHelper.getInstance().v(TAG, "仓库列表：" + response.toString());
                int responseCode = response.optInt(API_RESPONSE_RET_CODE, 0);
                String message = response.optString(API_RESPONSE_MESSAGE, "no message");
                if (responseCode != RET_CODE_SUCCESS) {
                    LogHelper.getInstance().e(TAG, "http请求成功, 逻辑有误" + response.toString());
                    listener.errorListener(message);
                }

                JSONObject retJsonObject = response.optJSONObject(API_RESPONSE_RET_BODY);
                if (listener != null) {
                    listener.listener(retJsonObject);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                LogHelper.getInstance().e(TAG, "仓库列表");
                String errorString = processNetworkError(error).toString();
                try {
                    JSONObject jsonObject1 = new JSONObject(errorString);
                    String retcode = jsonObject1.get("retcode").toString();
//                    if (retcode.equals("4032")){
                    listener.errorListener(error.getMessage());
//                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        jsonObjectRequest.setTag("TAG_CANCEL");
        WholesaleApplication.getRequestQueue().add(jsonObjectRequest);
    }

    public void httpQueryOwnerWareHouse(final WareHouseQueryOwnerRequest wareHouseQueryOwnerRequest, final NativeJsonResponseListener listener) {
        final String requestUrl = WebServerConfigManager.getWebServiceUrl() + "employee/query";
        JSONObject jsonObject = wareHouseQueryOwnerRequest.getJsonObject();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequestWithCookie(Method.POST, requestUrl, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                LogHelper.getInstance().v(TAG, "负责人列表：" + response.toString());
                int responseCode = response.optInt(API_RESPONSE_RET_CODE, 0);
                String message = response.optString(API_RESPONSE_MESSAGE, "no message");
                if (responseCode != RET_CODE_SUCCESS) {
                    LogHelper.getInstance().e(TAG, "http请求成功, 逻辑有误" + response.toString());
                    listener.errorListener(message);
                }

                JSONObject retJsonObject = response.optJSONObject(API_RESPONSE_RET_BODY);
                if (listener != null) {
                    listener.listener(retJsonObject);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                LogHelper.getInstance().e(TAG, "负责人列表");
                String errorString = processNetworkError(error).toString();
                try {
                    JSONObject jsonObject1 = new JSONObject(errorString);
                    String retcode = jsonObject1.get("retcode").toString();
//                    if (retcode.equals("4032")){
                    listener.errorListener(error.getMessage());
//                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        WholesaleApplication.getRequestQueue().add(jsonObjectRequest);
    }

    public void httpAddPreOrder(final PreOrderAddRequest preOrderAddRequest, final NativeJsonResponseListener listener) {
        final String requestUrl = WebServerConfigManager.getWebServiceUrl() + "preorder/add";
        JSONObject jsonObject = preOrderAddRequest.getJsonObject();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequestWithCookie(Method.POST, requestUrl, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                LogHelper.getInstance().v(TAG, "添加仓库：" + response.toString());
                int responseCode = response.optInt(API_RESPONSE_RET_CODE, 0);
                String message = response.optString(API_RESPONSE_MESSAGE, "no message");
                if (responseCode != RET_CODE_SUCCESS) {
                    LogHelper.getInstance().e(TAG, "http请求成功, 逻辑有误" + response.toString());
                    listener.errorListener(message);
                }

                JSONObject retJsonObject = response.optJSONObject(API_RESPONSE_RET_BODY);
                if (listener != null) {
                    listener.listener(retJsonObject);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                LogHelper.getInstance().e(TAG, "添加仓库");
                String errorString = processNetworkError(error).toString();
                try {
                    JSONObject jsonObject1 = new JSONObject(errorString);
                    String retcode = jsonObject1.get("retcode").toString();
//                    if (retcode.equals("4032")){
                    listener.errorListener(error.getMessage());
//                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        WholesaleApplication.getRequestQueue().add(jsonObjectRequest);
    }

    public void httpUpdatePreOrder(final PreOrderUpdateRequest preOrderUpdateRequest, final NativeJsonResponseListener listener) {
        final String requestUrl = WebServerConfigManager.getWebServiceUrl() + "preorder/add";
        JSONObject jsonObject = preOrderUpdateRequest.getJsonObject();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequestWithCookie(Method.POST, requestUrl, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                LogHelper.getInstance().v(TAG, "添加仓库：" + response.toString());
                int responseCode = response.optInt(API_RESPONSE_RET_CODE, 0);
                String message = response.optString(API_RESPONSE_MESSAGE, "no message");
                if (responseCode != RET_CODE_SUCCESS) {
                    LogHelper.getInstance().e(TAG, "http请求成功, 逻辑有误" + response.toString());
                    listener.errorListener(message);
                }

                JSONObject retJsonObject = response.optJSONObject(API_RESPONSE_RET_BODY);
                if (listener != null) {
                    listener.listener(retJsonObject);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                LogHelper.getInstance().e(TAG, "添加仓库");
                String errorString = processNetworkError(error).toString();
                try {
                    JSONObject jsonObject1 = new JSONObject(errorString);
                    String retcode = jsonObject1.get("retcode").toString();
//                    if (retcode.equals("4032")){
                    listener.errorListener(error.getMessage());
//                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        WholesaleApplication.getRequestQueue().add(jsonObjectRequest);
    }

    public void httpDeletePreOrder(final PreOrderDeleteRequest preOrderDeleteRequest, final NativeJsonResponseListener listener) {
        final String requestUrl = WebServerConfigManager.getWebServiceUrl() + "preorder/add";
        JSONObject jsonObject = preOrderDeleteRequest.getJsonObject();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequestWithCookie(Method.POST, requestUrl, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                LogHelper.getInstance().v(TAG, "添加仓库：" + response.toString());
                int responseCode = response.optInt(API_RESPONSE_RET_CODE, 0);
                String message = response.optString(API_RESPONSE_MESSAGE, "no message");
                if (responseCode != RET_CODE_SUCCESS) {
                    LogHelper.getInstance().e(TAG, "http请求成功, 逻辑有误" + response.toString());
                    listener.errorListener(message);
                }

                JSONObject retJsonObject = response.optJSONObject(API_RESPONSE_RET_BODY);
                if (listener != null) {
                    listener.listener(retJsonObject);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                LogHelper.getInstance().e(TAG, "添加仓库");
                String errorString = processNetworkError(error).toString();
                try {
                    JSONObject jsonObject1 = new JSONObject(errorString);
                    String retcode = jsonObject1.get("retcode").toString();
//                    if (retcode.equals("4032")){
                    listener.errorListener(error.getMessage());
//                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        WholesaleApplication.getRequestQueue().add(jsonObjectRequest);
    }

    public void httpListPreOrder(final PreOrderListRequest preOrderListRequest, final NativeJsonResponseListener listener) {
        final String requestUrl = WebServerConfigManager.getWebServiceUrl() + "preorder/add";
        JSONObject jsonObject = preOrderListRequest.getJsonObject();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequestWithCookie(Method.POST, requestUrl, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                LogHelper.getInstance().v(TAG, "添加仓库：" + response.toString());
                int responseCode = response.optInt(API_RESPONSE_RET_CODE, 0);
                String message = response.optString(API_RESPONSE_MESSAGE, "no message");
                if (responseCode != RET_CODE_SUCCESS) {
                    LogHelper.getInstance().e(TAG, "http请求成功, 逻辑有误" + response.toString());
                    listener.errorListener(message);
                }

                JSONObject retJsonObject = response.optJSONObject(API_RESPONSE_RET_BODY);
                if (listener != null) {
                    listener.listener(retJsonObject);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                LogHelper.getInstance().e(TAG, "添加仓库");
                String errorString = processNetworkError(error).toString();
                try {
                    JSONObject jsonObject1 = new JSONObject(errorString);
                    String retcode = jsonObject1.get("retcode").toString();
//                    if (retcode.equals("4032")){
                    listener.errorListener(error.getMessage());
//                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        WholesaleApplication.getRequestQueue().add(jsonObjectRequest);
    }

    public void httpGetInventories(final PreOrderSearchRequest preOrderSearchRequest,
                                   final NativeJsonResponseListener<JSONObject> listener) {
        // need create time table for collection last query time
        final String inventoryUrl = WebServerConfigManager.getWebServiceUrl() + "sku_product/list";
        JSONObject jsonObject = preOrderSearchRequest.getJsonObject();

        final Gson gson = new Gson();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequestWithCookie(Method.POST, inventoryUrl,
                jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                LogHelper.getInstance().v(TAG, "获取Inventory成功：" + response.toString());

                int responseCode = response.optInt(API_RESPONSE_RET_CODE, 123);
                if (responseCode != RET_CODE_SUCCESS) {
                    LogHelper.getInstance().e(TAG, "http请求成功, 逻辑有误" + response.toString());
                    return;
                }
                JSONObject retJsonObject = response.optJSONObject(API_RESPONSE_RET_BODY);

                if (listener != null) {
                    listener.listener(retJsonObject);
                }

                LogHelper.getInstance().v(TAG, "更新inventory成功：");
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                LogHelper.getInstance().e(TAG, "获取Inventory失败：");
                processNetworkError(error);
            }
        });
        jsonObjectRequest.setTag("TAG_CANCEL");
        WholesaleApplication.getRequestQueue().add(jsonObjectRequest);
    }

    public void httpGetAccountPay(final NativeJsonResponseListener<JSONObject> listener){
        final String accountPayUrl = WebServerConfigManager.getWebServiceUrl() + "account/list";

        JSONObject jsonObject = new JSONObject();

        final Gson gson = new Gson();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequestWithCookie(Method.POST, accountPayUrl, jsonObject,
                new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                LogHelper.getInstance().v(TAG, "获取account成功：" + response.toString());

                int responseCode = response.optInt(API_RESPONSE_RET_CODE, 123);
                if (responseCode != RET_CODE_SUCCESS) {
                    LogHelper.getInstance().e(TAG, "http请求成功, 逻辑有误" + response.toString());
                    return;
                }
//                JSONObject retJsonObject = response.optJSONObject(API_RESPONSE_RET_BODY);

                if (listener != null) {
                    listener.listener(response);
                }

                LogHelper.getInstance().v(TAG, "更新account成功：");
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                LogHelper.getInstance().e(TAG, "获取account失败：");
                listener.errorListener(error.getMessage());
                processNetworkError(error);
            }
        });

        jsonObjectRequest.setTag("TAG_CANCEL");

        WholesaleApplication.getRequestQueue().add(jsonObjectRequest);

    }

    public void httpSaleReturnOrder(final SaleReturnOrderRequest saleReturnOrderRequest,
                                  final NativeJsonResponseListener<JSONObject> listener){
        final String inventoryUrl = WebServerConfigManager.getWebServiceUrl() + "refund";
        JSONObject jsonObject = saleReturnOrderRequest.getJsonObject();

        final Gson gson = new Gson();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequestWithCookie(Method.POST, inventoryUrl,
                jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                LogHelper.getInstance().v(TAG, "开退货单成功：" + response.toString());

                int responseCode = response.optInt(API_RESPONSE_RET_CODE, 123);
                if (responseCode != RET_CODE_SUCCESS) {
                    LogHelper.getInstance().e(TAG, "http请求成功, 逻辑有误" + response.toString());
                    return;
                }
                JSONObject retJsonObject = response.optJSONObject(API_RESPONSE_RET_BODY);

                if (listener != null) {
                    listener.listener(retJsonObject);
                }

                LogHelper.getInstance().v(TAG, "开退货单成功：");
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                LogHelper.getInstance().e(TAG, "开退货单失败：");
                LogHelper.getInstance().e(TAG, "开退货单失败：" + error.getMessage());
                listener.errorListener(error.getMessage());
                processNetworkError(error);
            }
        });
        WholesaleApplication.getRequestQueue().add(jsonObjectRequest);
    }

    public void httpSaleReturnOrderPay(final SaleReturnPayRequest saleReturnPayRequest,
                                    final NativeJsonResponseListener<JSONObject> listener){
        final String inventoryUrl = WebServerConfigManager.getWebServiceUrl() + "refund/pay/finish";
        JSONObject jsonObject = saleReturnPayRequest.getJsonObject();

        final Gson gson = new Gson();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequestWithCookie(Method.POST, inventoryUrl,
                jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                LogHelper.getInstance().v(TAG, "开退货单支付成功：" + response.toString());

                int responseCode = response.optInt(API_RESPONSE_RET_CODE, 123);
                if (responseCode != RET_CODE_SUCCESS) {
                    LogHelper.getInstance().e(TAG, "http请求成功, 逻辑有误" + response.toString());
                    return;
                }
                JSONObject retJsonObject = response.optJSONObject(API_RESPONSE_RET_BODY);

                if (listener != null) {
                    listener.listener(retJsonObject);
                }

                LogHelper.getInstance().v(TAG, "开退货单支付成功：");
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                LogHelper.getInstance().e(TAG, "开退货单支付失败：");
                listener.errorListener(error.getMessage());
//                processNetworkError(error);
            }
        });

        WholesaleApplication.getRequestQueue().add(jsonObjectRequest);
    }

    public void httpPurchaseOrder(final PurchaseOrderRequest purchaseOrderRequest,
                                    final NativeJsonResponseListener<JSONObject> listener){
        final String inventoryUrl = WebServerConfigManager.getWebServiceUrl() + "product/buy";
        JSONObject jsonObject = purchaseOrderRequest.getJsonObject();

        final Gson gson = new Gson();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequestWithCookie(Method.POST, inventoryUrl,
                jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                LogHelper.getInstance().v(TAG, "开退货单成功：" + response.toString());

                int responseCode = response.optInt(API_RESPONSE_RET_CODE, 123);
                if (responseCode != RET_CODE_SUCCESS) {
                    LogHelper.getInstance().e(TAG, "http请求成功, 逻辑有误" + response.toString());
                    return;
                }
                JSONObject retJsonObject = response.optJSONObject(API_RESPONSE_RET_BODY);

                if (listener != null) {
                    listener.listener(retJsonObject);
                }

                LogHelper.getInstance().v(TAG, "开退货单成功：");
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                LogHelper.getInstance().e(TAG, "开退货单失败：");
                int code;
                code = error.networkResponse.statusCode;
                String errorMessage = String.valueOf(code);
                LogHelper.getInstance().e(TAG, "开退货单失败：" + errorMessage);
                //WholesaleHttpResponse wholesaleHttpResponse = processNetworkError(error);
                listener.errorListener(errorMessage);

            }
        });
        jsonObjectRequest.setTag("TAG_CANCEL");
        WholesaleApplication.getRequestQueue().add(jsonObjectRequest);
    }

    public void httpPurchaseOrderPay(final PurchasePayRequest purchasePayRequest,
                                       final NativeJsonResponseListener<JSONObject> listener){
        final String inventoryUrl = WebServerConfigManager.getWebServiceUrl() + "product/buy/payfinish";
        JSONObject jsonObject = purchasePayRequest.getJsonObject();

        final Gson gson = new Gson();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequestWithCookie(Method.POST, inventoryUrl,
                jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                LogHelper.getInstance().v(TAG, "开退货单支付成功：" + response.toString());

                int responseCode = response.optInt(API_RESPONSE_RET_CODE, 123);
                if (responseCode != RET_CODE_SUCCESS) {
                    LogHelper.getInstance().e(TAG, "http请求成功, 逻辑有误" + response.toString());
                    return;
                }
                JSONObject retJsonObject = response.optJSONObject(API_RESPONSE_RET_BODY);

                if (listener != null) {
                    listener.listener(retJsonObject);
                }

                LogHelper.getInstance().v(TAG, "开退货单支付成功：");
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                LogHelper.getInstance().e(TAG, "开退货单支付失败：");
                listener.errorListener(error.getMessage());
//                processNetworkError(error);
            }
        });
        jsonObjectRequest.setTag("TAG_CANCEL");
        WholesaleApplication.getRequestQueue().add(jsonObjectRequest);
    }


    public void httpPriceDiffOrder(final PriceDiffOrderRequest priceDiffOrderRequest,
                                    final NativeJsonResponseListener<JSONObject> listener){
        final String inventoryUrl = WebServerConfigManager.getWebServiceUrl() + "fill";
        JSONObject jsonObject = priceDiffOrderRequest.getJsonObject();

        final Gson gson = new Gson();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequestWithCookie(Method.POST, inventoryUrl,
                jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                LogHelper.getInstance().v(TAG, "补差价成功：" + response.toString());

                int responseCode = response.optInt(API_RESPONSE_RET_CODE, 123);
                if (responseCode != RET_CODE_SUCCESS) {
                    LogHelper.getInstance().e(TAG, "http请求成功, 逻辑有误" + response.toString());
                    return;
                }
                JSONObject retJsonObject = response.optJSONObject(API_RESPONSE_RET_BODY);

                if (listener != null) {
                    listener.listener(retJsonObject);
                }

                LogHelper.getInstance().v(TAG, "补差价成功：");
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                LogHelper.getInstance().e(TAG, "补差价失败：");
                LogHelper.getInstance().e(TAG, "补差价失败：" + error.getMessage());
                listener.errorListener(error.getMessage());
                processNetworkError(error);
            }
        });

        WholesaleApplication.getRequestQueue().add(jsonObjectRequest);

    }

    public void httpPriceDiffOrderPay(final PriceDiffPayRequest priceDiffPayRequest,
                                    final NativeJsonResponseListener<JSONObject> listener){
        final String inventoryUrl = WebServerConfigManager.getWebServiceUrl() + "fill/pay/finish";
        JSONObject jsonObject = priceDiffPayRequest.getJsonObject();

        final Gson gson = new Gson();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequestWithCookie(Method.POST, inventoryUrl,
                jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                LogHelper.getInstance().v(TAG, "补差价支付成功：" + response.toString());

                int responseCode = response.optInt(API_RESPONSE_RET_CODE, 123);
                if (responseCode != RET_CODE_SUCCESS) {
                    LogHelper.getInstance().e(TAG, "http请求成功, 逻辑有误" + response.toString());
                    return;
                }
                JSONObject retJsonObject = response.optJSONObject(API_RESPONSE_RET_BODY);

                if (listener != null) {
                    listener.listener(retJsonObject);
                }

                LogHelper.getInstance().v(TAG, "补差价支付成功：");
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                LogHelper.getInstance().e(TAG, "补差价支付失败：");
                listener.errorListener(error.getMessage());
//                processNetworkError(error);
            }
        });

        WholesaleApplication.getRequestQueue().add(jsonObjectRequest);

    }


    public void httpQueryExtraCost(final NativeJsonResponseListener<JSONObject> listener){
        final String inventoryUrl = WebServerConfigManager.getWebServiceUrl() + "user_defined_accounts/list";
        JSONObject jsonObject = new JSONObject();

        final Gson gson = new Gson();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequestWithCookie(Method.POST, inventoryUrl,
                jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                LogHelper.getInstance().v(TAG, "额外费用查询成功：" + response.toString());

                int responseCode = response.optInt(API_RESPONSE_RET_CODE, 123);
                if (responseCode != RET_CODE_SUCCESS) {
                    LogHelper.getInstance().e(TAG, "http请求成功, 逻辑有误" + response.toString());
                    return;
                }

//                JSONObject retJsonObject = response.optJSONObject(API_RESPONSE_RET_BODY);

                if (listener != null) {
                    listener.listener(response);
                }

                LogHelper.getInstance().v(TAG, "额外费用查询成功：");
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                LogHelper.getInstance().e(TAG, "额外费用查询失败：");
                listener.errorListener(error.getMessage());
//                processNetworkError(error);
            }
        });
        WholesaleApplication.getRequestQueue().add(jsonObjectRequest);
    }


    public void httpAddBatch(final BatchAddBean batchAddBean,
                             final NativeJsonResponseListener<JSONObject> listener){
        final String inventoryUrl = WebServerConfigManager.getWebServiceUrl() + "batch/add";
        JSONObject jsonObject = batchAddBean.getJsonObject();

        final Gson gson = new Gson();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequestWithCookie(Method.POST, inventoryUrl,
                jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                LogHelper.getInstance().v(TAG, "批次添加成功：" + response.toString());

                int responseCode = response.optInt(API_RESPONSE_RET_CODE, 123);
                if (responseCode != RET_CODE_SUCCESS) {
                    LogHelper.getInstance().e(TAG, "http请求成功, 逻辑有误" + response.toString());
                    return;
                }
                JSONObject retJsonObject = response.optJSONObject(API_RESPONSE_RET_BODY);

                if (listener != null) {
                    listener.listener(retJsonObject);
                }

                LogHelper.getInstance().v(TAG, "批次添加成功：");
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                LogHelper.getInstance().e(TAG, "批次添加失败：");
                listener.errorListener(error.getMessage());
//                processNetworkError(error);
            }
        });
        WholesaleApplication.getRequestQueue().add(jsonObjectRequest);
    }

    public void httpQueryBatch(final NativeJsonResponseListener<JSONObject> listener){
        final String inventoryUrl = WebServerConfigManager.getWebServiceUrl() + "batch/list";
        JSONObject jsonObject = new JSONObject();

        final Gson gson = new Gson();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequestWithCookie(Method.POST, inventoryUrl,
                jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                LogHelper.getInstance().v(TAG, "批次查询成功：" + response.toString());

                int responseCode = response.optInt(API_RESPONSE_RET_CODE, 123);
                if (responseCode != RET_CODE_SUCCESS) {
                    LogHelper.getInstance().e(TAG, "http请求成功, 逻辑有误" + response.toString());
                    return;
                }
                JSONObject retJsonObject = response.optJSONObject(API_RESPONSE_RET_BODY);

                if (listener != null) {
                    listener.listener(retJsonObject);
                }

                LogHelper.getInstance().v(TAG, "批次查询成功：");
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                LogHelper.getInstance().e(TAG, "批次查询失败：");
                listener.errorListener(error.getMessage());
//                processNetworkError(error);
            }
        });
        WholesaleApplication.getRequestQueue().add(jsonObjectRequest);
    }



    public void httpCustomerDetail(final CustomerDetailRequest customerDetailRequest,
                                   final NativeJsonResponseListener<JSONObject> listener){
        final String inventoryUrl = WebServerConfigManager.getWebServiceUrl() + "customer/detail";
        JSONObject jsonObject = customerDetailRequest.getJsonObject();

        final Gson gson = new Gson();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequestWithCookie(Method.POST, inventoryUrl,
                jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                LogHelper.getInstance().v(TAG, "客户欠款查询成功：" + response.toString());

                int responseCode = response.optInt(API_RESPONSE_RET_CODE, 123);
                if (responseCode != RET_CODE_SUCCESS) {
                    LogHelper.getInstance().e(TAG, "http请求成功, 逻辑有误" + response.toString());
                    return;
                }
                JSONObject retJsonObject = response.optJSONObject(API_RESPONSE_RET_BODY);

                if (listener != null) {
                    listener.listener(retJsonObject);
                }

                LogHelper.getInstance().v(TAG, "客户欠款查询成功：");
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                LogHelper.getInstance().e(TAG, "客户欠款查询失败：");
                listener.errorListener(error.getMessage());
//                processNetworkError(error);
            }
        });

        WholesaleApplication.getRequestQueue().add(jsonObjectRequest);

    }


    public void httpCheckAppUpgrade(int currentVersionCode, Response.Listener httpOkResponseListener) {
        if (currentVersionCode > 0) {
            final String upgradeUrl = WebServerConfigManager.getWebServiceUrl() +
                    "admin/upgrade/00100009/device/" + String.valueOf(currentVersionCode);

            Response.ErrorListener errorListener = new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    LogHelper.getInstance().e(TAG, "获取版本信息失败：");
                }
            };

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Method.GET, upgradeUrl, null, httpOkResponseListener, errorListener);
            WholesaleApplication.getRequestQueue().add(jsonObjectRequest);
        }
    }

    /**
     * 把没网络的情况返回值封装成和服务器返回的格式一样。
     * 正常服务器返回结构：
     * {"retcode":4003,"message":"the number not register","retbody":{}}
     */
    public WholesaleHttpResponse processNetworkError(VolleyError error) {
        WholesaleHttpResponse wholesaleHttpResponse = null;
        NetworkResponse networkResponse = error.networkResponse;
        if (networkResponse == null) {
            String errorMessage = VolleyErrorHelper.getMessage(error);
            wholesaleHttpResponse = new WholesaleHttpResponse(RET_NO_NETWORK_CONNECTION, errorMessage, null);
        } else {
            String cloudResponseBody = new String(error.networkResponse.data);
            try {
                wholesaleHttpResponse = WholesaleHttpResponse.fromJson(cloudResponseBody);
                // 有可能不是服务器返回的response，比如网管屏蔽返回的错误代码。也有可能返回空导致无法解析。
                if (wholesaleHttpResponse == null) {
                    LogHelper.getInstance().e(TAG, "连接到服务器异常！cloudResponseBody＝" + cloudResponseBody);
                    wholesaleHttpResponse = new WholesaleHttpResponse(RET_NO_NETWORK_CONNECTION, StringUtil.getString(R.string.http_request_generic_error), null);
                }
            } catch (Exception e) {
                String errorMessage = VolleyErrorHelper.getMessage(error);
                wholesaleHttpResponse = new WholesaleHttpResponse(RET_NO_NETWORK_CONNECTION, errorMessage, null);
                LogHelper.getInstance().e(TAG, "连接到服务器异常！cloudResponseBody＝" + cloudResponseBody);
                e.printStackTrace();
            }
        }

        // 用Toast提示用户网络异常
//        Toast.makeText(WholesaleApplication.getAppContext(),
//                errorMessage, Toast.LENGTH_LONG).show();
        LogHelper.getInstance().e(TAG, "网络请求失败：" + wholesaleHttpResponse.toString());

        return wholesaleHttpResponse;
    }

    interface Method {

        int DEPRECATED_GET_OR_POST = -1;
        int GET = 0;
        int POST = 1;
        int PUT = 2;
        int DELETE = 3;
        int HEAD = 4;
        int OPTIONS = 5;
        int TRACE = 6;
        int PATCH = 7;
    }

    class MultipartRequest extends Request<NetworkResponse> {

        private final Response.Listener<NetworkResponse> mListener;

        private final Response.ErrorListener mErrorListener;

        private final Map<String, String> mHeaders;

        private final String mMimeType;

        private final byte[] mMultipartBody;

        public MultipartRequest(String url, Map<String, String> headers, String mimeType,
                                byte[] multipartBody, Response.Listener<NetworkResponse> listener,
                                Response.ErrorListener errorListener) {
            super(Method.POST, url, errorListener);
            this.mListener = listener;
            this.mErrorListener = errorListener;
            this.mHeaders = headers;
            this.mMimeType = mimeType;
            this.mMultipartBody = multipartBody;
        }

        @Override
        public Map<String, String> getHeaders() throws AuthFailureError {

            Map<String, String> header = super.getHeaders();
            Map<String, String> httpHeader = new HashMap<>();
            if (header.size() > 0) {
                httpHeader.putAll(header);
            }

            if (mHeaders != null && mHeaders.size() > 0) {
                httpHeader.putAll(mHeaders);
            }

            Account currentUser = AccountManager.getInstance().getCurrentAccount();
            // add token to cookie
            if (currentUser != null && currentUser.getToken() != null && currentUser.getToken().length() > 0) {
                String s = currentUser.getToken();
                String tokenString = "token=" + s;
                httpHeader.put("Cookie", tokenString);
            }

            return httpHeader;
        }

        @Override
        public String getBodyContentType() {
            return mMimeType;
        }

        @Override
        public byte[] getBody() throws AuthFailureError {
            return mMultipartBody;
        }

        @Override
        protected Response<NetworkResponse> parseNetworkResponse(NetworkResponse response) {
            try {
                return Response.success(
                        response,
                        HttpHeaderParser.parseCacheHeaders(response));
            } catch (Exception e) {
                return Response.error(new ParseError(e));
            }
        }

        @Override
        protected void deliverResponse(NetworkResponse response) {
            mListener.onResponse(response);
        }

        @Override
        public void deliverError(VolleyError error) {
            mErrorListener.onErrorResponse(error);
        }


    }

    private static int selectHttpMethod(String method) {
        if (method.toUpperCase().equals("GET")) {
            return Method.GET;
        }

        if (method.toUpperCase().equals("POST")) {
            return Method.POST;
        }

        if (method.toUpperCase().equals("PUT")) {
            return Method.PUT;
        }

        if (method.toUpperCase().equals("DELETE")) {
            return Method.DELETE;
        }

        if (method.toUpperCase().equals("HEAD")) {
            return Method.HEAD;
        }

        if (method.toUpperCase().equals("OPTIONS")) {
            return Method.OPTIONS;
        }

        if (method.toUpperCase().equals("TRACE")) {
            return Method.TRACE;
        }

        if (method.toUpperCase().equals("PATCH")) {
            return Method.PATCH;
        }

        return Method.GET;
    }


    public static ImageLoader getImageLoader() {
        if (imageLoader == null) {
            RequestQueue imageRequestQueue = WholesaleApplication.getImageLoadRequestQueue();
            ImageLoader.ImageCache imageCache = new ImageLoader.ImageCache() {
                private final LruCache<String, Bitmap> cache = new LruCache<>(50);

                @Override
                public Bitmap getBitmap(String url) {
                    return cache.get(url);
                }

                @Override
                public void putBitmap(String url, Bitmap bitmap) {
                    cache.put(url, bitmap);
                }
            };
            imageLoader = new ImageLoader(imageRequestQueue, imageCache);
        }
        return imageLoader;
    }

    public static String parseImageUrl(String url) {
        String firstImageUrl1 = url.replaceAll("\\[", "");
        String firstImageUrl2 = firstImageUrl1.replaceAll("\\]", "");
        String firstImageUrl3 = firstImageUrl2.replaceAll("\"", "");
        String firstImageUrl = firstImageUrl3.replaceAll("\\\\", "");
        return firstImageUrl;
    }
}
