package com.honeywell.wholesale.ui.purchase.presenter;

import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.honeywell.hybridapp.framework.log.LogHelper;
import com.honeywell.wholesale.framework.http.NativeJsonResponseListener;
import com.honeywell.wholesale.framework.http.WebClient;
import com.honeywell.wholesale.framework.model.AccountManager;
import com.honeywell.wholesale.framework.model.PreCart;
import com.honeywell.wholesale.framework.model.SKU;
import com.honeywell.wholesale.framework.model.WareHouse;
import com.honeywell.wholesale.ui.base.WholesaleBasePresenter;
import com.honeywell.wholesale.ui.menu.setting.warehouse.request.WareHouseListRequest;
import com.honeywell.wholesale.ui.purchase.activity.PurchaseActivity;
import com.honeywell.wholesale.ui.purchase.view.OrderConfirmView;
import com.honeywell.wholesale.ui.purchase.view.OrderView;
import com.honeywell.wholesale.ui.scan.network.ProductDetailRequest;
import com.honeywell.wholesale.ui.transaction.preorders.CartModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by liuyang on 2017/7/5.
 */

public class OrderPresenter extends WholesaleBasePresenter<OrderView> {

    private static final String TAG = OrderPresenter.class.getSimpleName();
    private int currentShopId = Integer.parseInt(AccountManager.getInstance().getCurrentShopId());
    private WebClient webClient = new WebClient();
    private WareHouseListRequest wareHouseListRequest = new WareHouseListRequest();
    private ProductDetailRequest productDetailRequest = new ProductDetailRequest();


    private WareHouse currentWareHouse;
    private CartModel cartModel = CartModel.getInstance();

    public OrderPresenter(OrderView view) {
        super(view);
    }

    public void setCurrentWareHouse(WareHouse currentWareHouse) {
        this.currentWareHouse = currentWareHouse;
    }

    /**
     * 根据条形码productCode到server获取商品详情
     *
     * @param productCode
     */
    public void productSale(final String productCode) {
        productDetailRequest = new ProductDetailRequest(productCode, currentWareHouse.getWareHouseId());
        webClient.httpProductDetail(productDetailRequest, new NativeJsonResponseListener<JSONObject>() {
            @Override
            public void listener(JSONObject jsonObject) {

                ArrayList<SKU> skuList = new ArrayList<SKU>();
                String inventoryList = jsonObject.optString("inventory_list");
                Log.e("inventoryList", inventoryList);
                skuList = SKU.fromJson2List(inventoryList);
                SKU skuCurrent = new SKU();
                skuCurrent = null;
                //从服务器拿的数据，很乱，自己慢慢想逻辑，分三种情况
                for (SKU sku : skuList) {
                    if (sku.isSelected()) {
                        skuCurrent = sku;
                        break;
                    }
                }
                if (skuCurrent == null) {
                    for (SKU sku : skuList) {
                        if (sku.getProductCode().equals(productCode)) {
                            skuCurrent = sku;
                            break;
                        }
                    }
                }
                if (skuCurrent == null) {
                    mView.onToast("未识别出该商品");
                } else {
                    String skuQuantity = skuCurrent.getQuantity();
                    int productId = jsonObject.optInt("product_id");
                    String productName = jsonObject.optString("product_name");
                    String unitPrice = jsonObject.optString("standard_price");
                    String imageUrl = jsonObject.optString("pic_src");
                    String unit = jsonObject.optString("unit");
                    String stockQuantity = jsonObject.optString("total_quantity");
                    skuCurrent.setSaleAmount("1");
                    PreCart preCart = new PreCart(productId, productCode, productName, unitPrice, imageUrl, stockQuantity, unit);
                    preCart.getSkuList().add(skuCurrent);
                    mView.addProductItemController(preCart, skuCurrent);
                    mView.judgeButtonEnable();
                }
            }

            @Override
            public void errorListener(String s) {
                if (s.equals("180000021")) {
                    LogHelper.getInstance().e(TAG, "查询货品详情失败：" + s);
                    mView.onToast("商品在该仓库内没有库存");
                } else if (s.equals("4006")) {
                    LogHelper.getInstance().e(TAG, "查询货品详情失败：" + s);
                    mView.onToast("未识别出该商品");
                }
            }
        });
    }

    public void getWareHouseDataFromServer() {
        Log.e(TAG, "getWareHouseDataFromServer");
        wareHouseListRequest = new WareHouseListRequest(currentShopId);
        webClient.httpListWareHouse(wareHouseListRequest, new NativeJsonResponseListener<JSONObject>() {
            @Override
            public void listener(JSONObject jsonObject) {
                int defaultWareHouseId = -1;
                try {
                    defaultWareHouseId = jsonObject.getInt("default_warehouse_id");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                JSONArray jsonArray = jsonObject.optJSONArray("warehouses");
                Gson gson = new Gson();
                WareHouse wareHouseLocal = new WareHouse();
                ArrayList<WareHouse> wareHouseArrayList = new ArrayList<WareHouse>();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject1 = jsonArray.optJSONObject(i);
                    if (jsonObject1 != null) {
                        WareHouse wareHouse = gson.fromJson(jsonObject1.toString(), WareHouse.class);
                        if (wareHouse.getWareHouseId() == defaultWareHouseId) {
                            wareHouse.setDefault(true);
                            wareHouseLocal = wareHouse;
                        }
                        wareHouseArrayList.add(wareHouse);
                    }
                }
                mView.setWarehouseListData(wareHouseArrayList);
                if (currentWareHouse == null) {
                    currentWareHouse = wareHouseLocal;
                    wareHouseArrayList.get(wareHouseArrayList.indexOf(wareHouseLocal)).setSelected(true);
                    mView.setWarehouseText(currentWareHouse.getWareHouseName());
                }

                mView.setCurrentWarehouse(currentWareHouse);
                mView.setAddProductLayoutEnabled(true);
                cartModel.setWarehouseId(String.valueOf(currentWareHouse.getWareHouseId()));
                cartModel.setWarehouseName(currentWareHouse.getWareHouseName());
            }

            @Override
            public void errorListener(String s) {
                LogHelper.getInstance().e(TAG, s);
            }
        });
    }


    public void cancelRequest(){
        webClient.cancelRequest("TAG_CANCEL");
    }
}
