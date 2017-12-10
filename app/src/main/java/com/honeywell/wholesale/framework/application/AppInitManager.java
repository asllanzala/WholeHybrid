package com.honeywell.wholesale.framework.application;

import com.honeywell.hybridapp.framework.log.LogHelper;
import com.honeywell.wholesale.framework.database.IncrementalDAO;
import com.honeywell.wholesale.framework.database.OrderDAO;
import com.honeywell.wholesale.framework.database.StockDAO;
import com.honeywell.wholesale.framework.http.IWebApiCallback;
import com.honeywell.wholesale.framework.http.NativeJsonResponseListener;
import com.honeywell.wholesale.framework.http.WebClient;
import com.honeywell.wholesale.framework.model.AccountManager;
import com.honeywell.wholesale.framework.model.Customer;
import com.honeywell.wholesale.framework.model.Inventory;
import com.honeywell.wholesale.framework.model.Supplier;
import com.honeywell.wholesale.ui.login.module.ListCustomerRequest;
import com.honeywell.wholesale.ui.login.module.ListSupplierRequest;
import com.honeywell.wholesale.ui.login.module.UpdateCategoryCloudApiRequest;
import com.honeywell.wholesale.ui.login.module.UpdateInventoryCloudApiRequest;

import org.json.JSONObject;

/**
 * Created by e887272 on 9/7/16.
 *
 */

public class AppInitManager {

    public static final String TAG = AppInitManager.class.getSimpleName();
    private static AppInitManager mAppInitManager = null;

    public static synchronized AppInitManager getInstance() {
        if (mAppInitManager == null) {
            mAppInitManager = new AppInitManager();
        }

        return mAppInitManager;
    }

    private AppInitManager() {
        // empty
    }

    /**
     * 此方法可以运行在UI主线程上
     */
    public void startSyncData() {
        String shopId = AccountManager.getInstance().getCurrentAccount().getCurrentShopId();
        WebClient webClient = new WebClient();

        syncInventoryData(webClient, shopId);
        syncVendorData(webClient, shopId, null);
        syncCustomerData(webClient, shopId, null);

        getFullCategory(null);
        removeOldTransactionData();
    }

    public void getCategory(NativeJsonResponseListener<JSONObject> nativeJsonResponseListener){
        WebClient webClient = new WebClient();
        String employeeId = AccountManager.getInstance().getCurrentAccount().getEmployeeId();

        //TODO shopId is int type current
        int shopId = Integer.valueOf(AccountManager.getInstance().getCurrentAccount().getCurrentShopId());
        UpdateCategoryCloudApiRequest categoryRequest = new UpdateCategoryCloudApiRequest(employeeId, shopId);
        webClient.httpGetCategories(categoryRequest, nativeJsonResponseListener);
    }

    public void getFullCategory(NativeJsonResponseListener<JSONObject> nativeJsonResponseListener){
        WebClient webClient = new WebClient();

        String employeeId = AccountManager.getInstance().getCurrentAccount().getEmployeeId();
        UpdateCategoryCloudApiRequest categoryRequest = new UpdateCategoryCloudApiRequest(employeeId, 0);
        webClient.httpGetCategories(categoryRequest, nativeJsonResponseListener);
    }

    public void syncInventoryData(WebClient webClient, String shopId) {
        // 更新库存
        // TODO Looper refresh Account each shopId
        String lastRequestTime = IncrementalDAO.queryIncrementalItem(Inventory.class, shopId);
        UpdateInventoryCloudApiRequest inventoryRequest = new UpdateInventoryCloudApiRequest(shopId, lastRequestTime);
        webClient.httpUpdateInventories(inventoryRequest, null);
    }

    public void syncVendorData(WebClient webClient, String shopId, final IWebApiCallback apiCallback) {
        // 更新供应商
        String vendorLastRequestTime = IncrementalDAO.queryIncrementalItem(Supplier.class, shopId);
        ListSupplierRequest listSupplierRequest = new ListSupplierRequest(shopId, vendorLastRequestTime);
        webClient.httpGetSupplier(listSupplierRequest, new NativeJsonResponseListener<JSONObject>() {
            @Override
            public void listener(JSONObject jsonObject) {
                if(apiCallback != null) {
                    apiCallback.onSuccessCallback(jsonObject);
                }
            }

            @Override
            public void errorListener(String s) {
                if(apiCallback != null) {
                    apiCallback.onErrorCallback(s);
                }
                LogHelper.getInstance().e(TAG, "get vendor error");
            }
        });
    }

    public void syncCustomerData(WebClient webClient, String shopId, final IWebApiCallback apiCallback) {
        // 更新Customer
        String customerLastRequestTime = IncrementalDAO.queryIncrementalItem(Customer.class, shopId);
        ListCustomerRequest listCustomerRequest = new ListCustomerRequest(shopId, customerLastRequestTime);
        webClient.httpGetCustomer(listCustomerRequest, new NativeJsonResponseListener<JSONObject>() {
            @Override
            public void listener(JSONObject jsonObject) {
                if(apiCallback != null) {
                    apiCallback.onSuccessCallback(jsonObject);
                }
            }

            @Override
            public void errorListener(String s) {
                if(apiCallback != null) {
                    apiCallback.onErrorCallback(s);
                }
                LogHelper.getInstance().e(TAG, "get customer error");
            }
        });
    }

    public void removeOldTransactionData() {
        // 删除超过30天的交易记录，入库记录
        OrderDAO.removeDataAfter30days();
        StockDAO.removeDataAfter30days();
    }


}
