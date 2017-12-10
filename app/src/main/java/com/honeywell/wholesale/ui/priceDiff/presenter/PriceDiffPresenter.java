package com.honeywell.wholesale.ui.priceDiff.presenter;

import android.content.Context;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.google.gson.Gson;
import com.honeywell.wholesale.framework.http.NativeJsonResponseListener;
import com.honeywell.wholesale.framework.http.WebClient;
import com.honeywell.wholesale.framework.model.PayAccount;
import com.honeywell.wholesale.ui.base.BaseTextView;
import com.honeywell.wholesale.ui.priceDiff.PriceDiffPaySelectActivity;
import com.honeywell.wholesale.ui.priceDiff.request.CustomerDetailRequest;
import com.honeywell.wholesale.ui.priceDiff.request.PriceDiffOrderRequest;
import com.honeywell.wholesale.ui.priceDiff.request.PriceDiffPayRequest;
import com.honeywell.wholesale.ui.saleReturn.SaleReturnPaySelectActivity;
import com.honeywell.wholesale.ui.saleReturn.request.SaleReturnOrderRequest;
import com.honeywell.wholesale.ui.saleReturn.request.SaleReturnPayRequest;
import com.honeywell.wholesale.ui.transaction.preorders.CartModel;

import org.json.JSONArray;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by liuyang on 2017/6/7.
 */

public class PriceDiffPresenter {

    private static final String TAG = PriceDiffPresenter.class.getSimpleName();

    public static final String SALE_RETURN_SUCCEED = "sale return succeed";

    private CustomerDetailRequest customerDetailRequest;

    private PriceDiffPayRequest priceDiffPayRequest;

    private PriceDiffOrderRequest priceDiffOrderRequest;

    private ArrayList<PayAccount> payAccountArrayList;

    private CartModel cartModel = CartModel.getInstance();

    private WebClient webClient;

    private String mSaleId;

    private Context context;

    public PriceDiffPresenter(Context context) {
        this.context = context;
        webClient = new WebClient();
    }

    public PriceDiffPresenter(PriceDiffOrderRequest priceDiffOrderRequest) {
        this.priceDiffOrderRequest = priceDiffOrderRequest;
    }

    public PriceDiffOrderRequest getPriceDiffOrderRequest() {
        return priceDiffOrderRequest;
    }

    public void setPriceDiffOrderRequest(PriceDiffOrderRequest priceDiffOrderRequest) {
        this.priceDiffOrderRequest = priceDiffOrderRequest;
    }

    public void traCustomerDebt(final BaseTextView baseTextView){
        customerDetailRequest = new CustomerDetailRequest(
                Integer.valueOf(cartModel.getCurrentShopId()),
                Integer.valueOf(cartModel.getCustomerId()));
        webClient.httpCustomerDetail(customerDetailRequest, new NativeJsonResponseListener<JSONObject>() {
            @Override
            public void listener(JSONObject jsonObject) {
                String debtString = jsonObject.optString("debt_deals_price");
                cartModel.setDebtDealsPrice(debtString);
                baseTextView.setText(cartModel.getDebtDealsPrice());
            }

            @Override
            public void errorListener(String s) {

            }
        });
    }

    public void traModel(){
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        Date curDate = new Date(System.currentTimeMillis());
        String str = formatter.format(curDate);
        Log.e(TAG,str);
        priceDiffOrderRequest = new PriceDiffOrderRequest(
                Integer.valueOf(cartModel.getCurrentShopId()),
                negativeQuantity(cartModel.getTotalPrice()),
                cartModel.getCustomerName(),
                Integer.valueOf(cartModel.getCustomerId()),
                cartModel.getRemark());
        ArrayList<PriceDiffOrderRequest.Sale> saleArrayList = new ArrayList<PriceDiffOrderRequest.Sale>();

        for (int i = 0; i < cartModel.getPreCartList().size(); i++){

            PriceDiffOrderRequest.Sale sale = new PriceDiffOrderRequest.Sale(
                    cartModel.getPreCartList().get(i).getProductId(),
                    cartModel.getPreCartList().get(i).getProductName());
            ArrayList<PriceDiffOrderRequest.SaleSku> saleSkuArrayList = new ArrayList<PriceDiffOrderRequest.SaleSku>();
            ArrayList<PriceDiffOrderRequest.WarehouseSku> warehouseSkuArrayList1 = new ArrayList<PriceDiffOrderRequest.WarehouseSku>();
            for (int j = 0; j < cartModel.getPreCartList().get(i).getSkuList().size(); j++){

                if (cartModel.getWarehouseId() != ""){
                    PriceDiffOrderRequest.WarehouseSku warehouseSku = new PriceDiffOrderRequest.WarehouseSku(
                            Integer.valueOf(cartModel.getWarehouseId()),
                            negativeQuantity(cartModel.getPreCartList().get(i).getSkuList().get(j).getSaleAmount()));
                    warehouseSkuArrayList1.add(warehouseSku);
                }else {
                    warehouseSkuArrayList1 = null;
                }
                ArrayList<PriceDiffOrderRequest.WarehouseSku> warehouseSkuArrayList = new ArrayList<PriceDiffOrderRequest.WarehouseSku>();
                if (warehouseSkuArrayList1 != null){
                    warehouseSkuArrayList.add(warehouseSkuArrayList1.get(j));
                }

                PriceDiffOrderRequest.SaleSku saleSku = new PriceDiffOrderRequest.SaleSku(
                        cartModel.getPreCartList().get(i).getSkuList().get(j).getSkuId(),
                        cartModel.getPreCartList().get(i).getSkuList().get(j).getStandardPrice(),
                        negativeQuantity(cartModel.getPreCartList().get(i).getSkuList().get(j).getSaleAmount()),
                        warehouseSkuArrayList);
                saleSkuArrayList.add(saleSku);
                sale.setSaleSkuList(saleSkuArrayList);
            }

            saleArrayList.add(sale);
        }
        priceDiffOrderRequest.setSaleList(saleArrayList);
        Gson gson = new Gson();
        String a = gson.toJson(priceDiffOrderRequest);
        Log.e("sada2",a);
    }

    public void setPayAccountArrayList(ArrayList<PayAccount> payAccountArrayList) {
        this.payAccountArrayList = payAccountArrayList;
    }

    public void traPayModel(String mSaleId, ArrayList<PayAccount> payAccountArrayList){
        priceDiffPayRequest = new PriceDiffPayRequest(mSaleId, cartModel.getCurrentShopId(), payAccountArrayList);
        Gson gson = new Gson();
        String a = gson.toJson(priceDiffPayRequest);
        Log.e("sada",a);

    }

    public void sareReturnOrder(final Button saveButton){
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        Date curDate = new Date(System.currentTimeMillis());
        String str = formatter.format(curDate);
        Log.e(TAG,str);
        webClient.httpPriceDiffOrder(priceDiffOrderRequest, new NativeJsonResponseListener<JSONObject>() {
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
        webClient.httpPriceDiffOrderPay(priceDiffPayRequest, new NativeJsonResponseListener<JSONObject>() {
            @Override
            public void listener(JSONObject jsonObject) {
                saveButton.setEnabled(true);
                JSONArray jsonArray = jsonObject.optJSONArray(WebClient.API_RESPONSE_RET_BODY);
                Gson gson = new Gson();
                PriceDiffPaySelectActivity.priceDiffPaySelectActivity.jumpToMain();
                Toast.makeText(context, "补差价完成", Toast.LENGTH_SHORT).show();
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
