package com.honeywell.wholesale.ui.purchase.presenter;

import android.util.Log;

import com.google.gson.Gson;
import com.honeywell.wholesale.framework.http.NativeJsonResponseListener;
import com.honeywell.wholesale.framework.http.WebClient;
import com.honeywell.wholesale.framework.model.AccountManager;
import com.honeywell.wholesale.framework.model.ExtraCost;
import com.honeywell.wholesale.framework.model.PayAccount;
import com.honeywell.wholesale.ui.base.WholesaleBasePresenter;
import com.honeywell.wholesale.ui.purchase.view.PaySelectView;
import com.honeywell.wholesale.ui.purchase.request.PurchaseOrderRequest;
import com.honeywell.wholesale.ui.purchase.request.PurchasePayRequest;
import com.honeywell.wholesale.ui.transaction.preorders.CartModel;
import org.json.JSONArray;
import org.json.JSONObject;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


/**
 * Created by liuyang on 2017/7/3.
 */

public class PaySelectPresenter extends WholesaleBasePresenter<PaySelectView> {
    private static final String TAG = PaySelectPresenter.class.getSimpleName();

    private PurchasePayRequest purchasePayRequest;

    private PurchaseOrderRequest purchaseOrderRequest;

    private ArrayList<PayAccount> payAccountArrayList;

    private CartModel cartModel = CartModel.getInstance();

    private WebClient webClient;

    private String mPurchaseSummaryId;

    private String mShopId = "";

    public PaySelectPresenter(PaySelectView view) {
        super(view);
        webClient = new WebClient();
        mShopId = AccountManager.getInstance().getCurrentShopId();
    }

    public void setPayAccountArrayList(ArrayList<PayAccount> payAccountArrayList) {
        this.payAccountArrayList = payAccountArrayList;
    }

    public void traModel(){
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        Date curDate = new Date(System.currentTimeMillis());
        String str = formatter.format(curDate);
        Log.e(TAG,str);
        purchaseOrderRequest = new PurchaseOrderRequest(
                Integer.valueOf(cartModel.getCurrentShopId()),
                cartModel.getTotalPrice(),
                cartModel.getSupplierName(),
                Integer.valueOf(cartModel.getSupplierId()),
                cartModel.getRemark());
        ArrayList<PurchaseOrderRequest.Buy> saleArrayList = new ArrayList<PurchaseOrderRequest.Buy>();

        for (int i = 0; i < cartModel.getPreCartList().size(); i++){
            PurchaseOrderRequest.Buy sale = new PurchaseOrderRequest.Buy(
                    cartModel.getPreCartList().get(i).getProductId(),
                    cartModel.getPreCartList().get(i).getProductName());
            ArrayList<PurchaseOrderRequest.BuySku> saleSkuArrayList = new ArrayList<PurchaseOrderRequest.BuySku>();
            ArrayList<ExtraCost> premium_list = new ArrayList<ExtraCost>();
            for (int j = 0; j < cartModel.getPreCartList().get(i).getSkuList().size(); j++){

                premium_list = new ArrayList<ExtraCost>();
                premium_list = cartModel.getPreCartList().get(i).getSkuList().get(j).getPremiumLists();
                PurchaseOrderRequest.BuySku saleSku = new PurchaseOrderRequest.BuySku(
                        cartModel.getPreCartList().get(i).getSkuList().get(j).getSkuId(),
                        cartModel.getPreCartList().get(i).getSkuList().get(j).getStandardPrice(),
                        cartModel.getPreCartList().get(i).getSkuList().get(j).getSaleAmount(),
                        Integer.valueOf(cartModel.getWarehouseId()),
                        cartModel.getPreCartList().get(i).getUnit(),
                        cartModel.getPreCartList().get(i).getSkuList().get(j).getBatchId(),
                        cartModel.getPreCartList().get(i).getSkuList().get(j).getBatchName(),
                        cartModel.getPreCartList().get(i).getSkuList().get(j).getTotalPremium(),
                        premium_list);
                saleSkuArrayList.add(saleSku);
                sale.setBuySkuList(saleSkuArrayList);
            }
            saleArrayList.add(sale);
        }
        purchaseOrderRequest.setSaleList(saleArrayList);
        Gson gson = new Gson();
        String a = gson.toJson(purchaseOrderRequest);
        Log.e("sada2",a);
    }

    public void traPayModel(String mPurchaseSummaryId, ArrayList<PayAccount> payAccountArrayList){
//        purchasePayRequest = new PurchasePayRequest(mPurchaseSummaryId, payAccountArrayList);
        purchasePayRequest = new PurchasePayRequest(mPurchaseSummaryId, mShopId, payAccountArrayList);
        Gson gson = new Gson();
        String a = gson.toJson(purchasePayRequest);
        Log.e("sada",a);

    }

    public void sareReturnOrder(){
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        Date curDate = new Date(System.currentTimeMillis());
        String str = formatter.format(curDate);
        Log.e(TAG,str);
        webClient = new WebClient();
        webClient.httpPurchaseOrder(purchaseOrderRequest, new NativeJsonResponseListener<JSONObject>() {
            @Override
            public void listener(JSONObject jsonObject) {
                mPurchaseSummaryId = jsonObject.optString("purchase_summary_id");
                Log.e("13213","j"+ jsonObject.toString());
                traPayModel(mPurchaseSummaryId, payAccountArrayList);
                sareReturnPay();
            }

            @Override
            public void errorListener(String s) {
                Log.e(TAG, "SADA" + s);
                mView.onSetButtonEnabled(true);
                if (s.equals("180000029")){
                    mView.onToast("额外费用已删除");
                }
                mView.onToast("服务器错误，请稍后再试");
            }
        });
    }

    public void sareReturnPay(){
        webClient.httpPurchaseOrderPay(purchasePayRequest, new NativeJsonResponseListener<JSONObject>() {
            @Override
            public void listener(JSONObject jsonObject) {
                mView.onSetButtonEnabled(true);
                JSONArray jsonArray = jsonObject.optJSONArray(WebClient.API_RESPONSE_RET_BODY);
                Gson gson = new Gson();
                mView.onIntent();
                mView.onToast("采购完成");
                cartModel.clear();
            }

            @Override
            public void errorListener(String s) {
                mView.onSetButtonEnabled(true);
                mView.onToast("服务器错误，请稍后再试");
            }
        });
    }

    public void getAccountPay(final ArrayList<PayAccount> payAccountList) {
        webClient.httpGetAccountPay(new NativeJsonResponseListener<JSONObject>() {
            @Override
            public void listener(JSONObject jsonObject) {
                JSONArray jsonArray = jsonObject.optJSONArray(WebClient.API_RESPONSE_RET_BODY);
                Gson gson = new Gson();
//                payAccountList = new ArrayList<PayAccount>();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject1 = jsonArray.optJSONObject(i);
                    if (jsonObject1 != null) {
                        PayAccount payAccount = gson.fromJson(jsonObject1.toString(), PayAccount.class);
                        payAccountList.add(payAccount);
                    }
                }
                Log.e(TAG, mView.toString());
                mView.onShowPayment();
//                initShow();
            }

            @Override
            public void errorListener(String s) {

            }
        });
    }

    public void cancelRequest(){
        webClient.cancelRequest("TAG_CANCEL");
    }
}
