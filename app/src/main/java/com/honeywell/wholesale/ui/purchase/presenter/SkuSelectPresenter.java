package com.honeywell.wholesale.ui.purchase.presenter;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.toolbox.JsonObjectRequest;
import com.honeywell.hybridapp.framework.log.LogHelper;
import com.honeywell.wholesale.framework.http.NativeJsonResponseListener;
import com.honeywell.wholesale.framework.http.WebClient;
import com.honeywell.wholesale.framework.model.AccountManager;
import com.honeywell.wholesale.framework.model.SKU;
import com.honeywell.wholesale.ui.base.WholesaleBasePresenter;
import com.honeywell.wholesale.ui.base.WholesaleBaseView;
import com.honeywell.wholesale.ui.purchase.request.ProductDetailBean;
import com.honeywell.wholesale.ui.purchase.view.SkuSelectView;
import com.honeywell.wholesale.ui.transaction.preorders.search.PreTransactionSkuActivity;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by liuyang on 2017/7/5.
 */

public class SkuSelectPresenter extends WholesaleBasePresenter<SkuSelectView> {

    private static final String TAG = SkuSelectPresenter.class.getSimpleName();

    private String shopId = AccountManager.getInstance().getCurrentShopId();

    private WebClient webClient;

    private ArrayList<SKU> skuList = new ArrayList<SKU>();

    private ProductDetailBean productDetailBean;

    public SkuSelectPresenter(SkuSelectView view) {
        super(view);
        webClient = new WebClient();

    }

    /**
     * 根据productId到server获取商品详情
     *
     * @param productId
     */
    public void productSale(final int productId) {
        productDetailBean = new ProductDetailBean(productId, Integer.valueOf(shopId));
        webClient.httpSkuProductDetailWithBean(productDetailBean, new NativeJsonResponseListener<JSONObject>() {
            @Override
            public void listener(JSONObject jsonObject) {
                String inventoryList = jsonObject.optString("inventory_list");
                Log.e("inventoryList", inventoryList);
                String totalQuantity = jsonObject.optString("total_quantity");
//                if (mView != null) {
                Log.e(TAG, mView.toString());
                    mView.setStockText(totalQuantity);
                    skuList = SKU.fromJson2List(inventoryList);
                    mView.setSkuListData(skuList);
                    mView.addSkuView();
                    Log.e("inventoryList", inventoryList);
//                }
            }

            @Override
            public void errorListener(String s) {
                LogHelper.getInstance().e(TAG, "查询货品详情失败：" + s);
                mView.onToast("查询货品详情失败");
            }
        });
    }

    public void cancelRequest(){
        webClient.cancelRequest("TAG_CANCEL");
    }

}
