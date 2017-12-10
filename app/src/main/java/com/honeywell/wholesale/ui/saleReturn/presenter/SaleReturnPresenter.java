package com.honeywell.wholesale.ui.saleReturn.presenter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.google.gson.Gson;
import com.honeywell.wholesale.framework.event.SaleReturnEvent;
import com.honeywell.wholesale.framework.http.NativeJsonResponseListener;
import com.honeywell.wholesale.framework.http.WebClient;
import com.honeywell.wholesale.framework.model.PayAccount;
import com.honeywell.wholesale.framework.model.SaleReturn;
import com.honeywell.wholesale.ui.activity.MainActivity;
import com.honeywell.wholesale.ui.saleReturn.PreSaleReturnActivity;
import com.honeywell.wholesale.ui.saleReturn.SaleReturnOrderConfirmActivity;
import com.honeywell.wholesale.ui.saleReturn.SaleReturnPaySelectActivity;
import com.honeywell.wholesale.ui.saleReturn.request.SaleReturnOrderRequest;
import com.honeywell.wholesale.ui.saleReturn.request.SaleReturnPayRequest;
import com.honeywell.wholesale.ui.transaction.preorders.CartModel;
import com.honeywell.wholesale.ui.transaction.preorders.PreOrdersCustomerActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import de.greenrobot.event.EventBus;

/**
 * Created by liuyang on 2017/6/7.
 */

public class SaleReturnPresenter {

    private static final String TAG = SaleReturnPresenter.class.getSimpleName();

    public static final String SALE_RETURN_SUCCEED = "sale return succeed";

    private SaleReturnPayRequest saleReturnPayRequest;

    private SaleReturnOrderRequest saleReturnOrderRequest;

    private ArrayList<PayAccount> payAccountArrayList;

    private CartModel cartModel = CartModel.getInstance();

    private WebClient webClient;

    private String mSaleId;

    private Context context;

    public SaleReturnPresenter(Context context) {
        this.context = context;
        webClient = new WebClient();
    }

    public SaleReturnPresenter(SaleReturnOrderRequest saleReturnOrderRequest) {
        this.saleReturnOrderRequest = saleReturnOrderRequest;
    }

    public SaleReturnOrderRequest getSaleReturnOrderRequest() {
        return saleReturnOrderRequest;
    }

    public void setSaleReturnOrderRequest(SaleReturnOrderRequest saleReturnOrderRequest) {
        this.saleReturnOrderRequest = saleReturnOrderRequest;
    }

    public void traModel(){
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        Date curDate = new Date(System.currentTimeMillis());
        String str = formatter.format(curDate);
        Log.e(TAG,str);
        saleReturnOrderRequest = new SaleReturnOrderRequest(
                Integer.valueOf(cartModel.getCurrentShopId()),
                negativeQuantity(cartModel.getTotalPrice()),
                cartModel.getCustomerName(),
                Integer.valueOf(cartModel.getCustomerId()),
                cartModel.getRemark());
        ArrayList<SaleReturnOrderRequest.Sale> saleArrayList = new ArrayList<SaleReturnOrderRequest.Sale>();

        for (int i = 0; i < cartModel.getPreCartList().size(); i++){

            SaleReturnOrderRequest.Sale sale = new SaleReturnOrderRequest.Sale(
                    cartModel.getPreCartList().get(i).getProductId(),
                    cartModel.getPreCartList().get(i).getProductName());
            ArrayList<SaleReturnOrderRequest.SaleSku> saleSkuArrayList = new ArrayList<SaleReturnOrderRequest.SaleSku>();
            ArrayList<SaleReturnOrderRequest.WarehouseSku> warehouseSkuArrayList1 = new ArrayList<SaleReturnOrderRequest.WarehouseSku>();
            for (int j = 0; j < cartModel.getPreCartList().get(i).getSkuList().size(); j++){

                if (cartModel.getWarehouseId() != ""){
                    SaleReturnOrderRequest.WarehouseSku warehouseSku = new SaleReturnOrderRequest.WarehouseSku(
                            Integer.valueOf(cartModel.getWarehouseId()),
                            negativeQuantity(cartModel.getPreCartList().get(i).getSkuList().get(j).getSaleAmount()));
                    warehouseSkuArrayList1.add(warehouseSku);
                }else {
                    warehouseSkuArrayList1 = null;
                }
                ArrayList<SaleReturnOrderRequest.WarehouseSku> warehouseSkuArrayList = new ArrayList<SaleReturnOrderRequest.WarehouseSku>();
                if (warehouseSkuArrayList1 != null){
                    warehouseSkuArrayList.add(warehouseSkuArrayList1.get(j));
                }

                SaleReturnOrderRequest.SaleSku saleSku = new SaleReturnOrderRequest.SaleSku(
                        cartModel.getPreCartList().get(i).getSkuList().get(j).getSkuId(),
                        cartModel.getPreCartList().get(i).getSkuList().get(j).getStandardPrice(),
                        negativeQuantity(cartModel.getPreCartList().get(i).getSkuList().get(j).getSaleAmount()),
                        warehouseSkuArrayList);
                saleSkuArrayList.add(saleSku);
                sale.setSaleSkuList(saleSkuArrayList);
            }

            saleArrayList.add(sale);
        }
        saleReturnOrderRequest.setSaleList(saleArrayList);
        Gson gson = new Gson();
        String a = gson.toJson(saleReturnOrderRequest);
        Log.e("sada2",a);
    }

    public void setPayAccountArrayList(ArrayList<PayAccount> payAccountArrayList) {
        this.payAccountArrayList = payAccountArrayList;
    }

    public void traPayModel(String mSaleId, ArrayList<PayAccount> payAccountArrayList){
        saleReturnPayRequest = new SaleReturnPayRequest(mSaleId, payAccountArrayList);
        Gson gson = new Gson();
        String a = gson.toJson(saleReturnPayRequest);
        Log.e("sada",a);

    }

    public void sareReturnOrder(final Button saveButton){
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        Date curDate = new Date(System.currentTimeMillis());
        String str = formatter.format(curDate);
        Log.e(TAG,str);
        webClient.httpSaleReturnOrder(saleReturnOrderRequest, new NativeJsonResponseListener<JSONObject>() {
            @Override
            public void listener(JSONObject jsonObject) {
                mSaleId = jsonObject.optString("sale_id");
                Log.e("13213","j"+ jsonObject.toString());
                traPayModel(mSaleId, payAccountArrayList);
                sareReturnPay(saveButton);
            }

            @Override
            public void errorListener(String s) {
                saveButton.setEnabled(true);
                Toast.makeText(context, "服务器错误，请稍后再试", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void sareReturnPay(final Button saveButton){
        webClient.httpSaleReturnOrderPay(saleReturnPayRequest, new NativeJsonResponseListener<JSONObject>() {
            @Override
            public void listener(JSONObject jsonObject) {
                saveButton.setEnabled(true);
                JSONArray jsonArray = jsonObject.optJSONArray(WebClient.API_RESPONSE_RET_BODY);
                Gson gson = new Gson();
                SaleReturnPaySelectActivity.saleReturnPaySelectActivity.jumpToMain();
                Toast.makeText(context, "退货完成", Toast.LENGTH_SHORT).show();
                cartModel.clear();
            }

            @Override
            public void errorListener(String s) {
                saveButton.setEnabled(true);
                Toast.makeText(context, "服务器错误，请稍后再试", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String negativeQuantity(String quantity){
        BigDecimal quantityBigDecimal = new BigDecimal(quantity);
        BigDecimal zeroBigDecimal = new BigDecimal("0");
        BigDecimal negativeBigDecimal = zeroBigDecimal.subtract(quantityBigDecimal);
        return negativeBigDecimal.toString();
    }
}
