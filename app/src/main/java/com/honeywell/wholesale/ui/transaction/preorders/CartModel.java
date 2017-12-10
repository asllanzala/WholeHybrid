package com.honeywell.wholesale.ui.transaction.preorders;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.honeywell.wholesale.framework.model.AccountManager;
import com.honeywell.wholesale.framework.model.ExtraCost;
import com.honeywell.wholesale.framework.model.PreCart;
import com.honeywell.wholesale.framework.model.SKU;
import com.honeywell.wholesale.framework.utils.Arith;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static okhttp3.internal.Internal.instance;


/**
 * Created by H154326 on 17/3/14.
 * Email: yang.liu6@honeywell.com
 */

public class CartModel {

    public int shopId;
    public String totalPrice;
    public String address;
    public String contact_name;
    public String contact_phone;
    public String remark;
    public String discount;
    public String customer_company_name;
    public String customer_phone;
    public String invoiceTitle;
    public String warehouseId;
    public String warehouseName;
    public String customerId;
    public String customerName;
    public String supplierId;
    public String supplierName;
    public String currentShopId;
    public String currentShopName;
    public String currentEmployeeId;
    public String currentEmployeeName;
    public String debtDealsPrice = "0";
//    public String currentShopId = AccountManager.getInstance().getCurrentAccount().getCurrentShopId();
//    public String currentShopName = AccountManager.getInstance().getCurrentAccount().getShopName();
//    public String currentEmployeeId = AccountManager.getInstance().getCurrentAccount().getEmployeeId();
//    public String currentEmployeeName = AccountManager.getInstance().getCurrentAccount().getUserName();
    public ArrayList<PreCart> preCartList = new ArrayList<PreCart>();//本页面暂存的数据结构

    //懒汉式单例模式
    /* 持有私有静态实例，防止被引用，此处赋值为null，目的是实现延迟加载 */
    private static CartModel cartModel = null;
    /* 私有构造方法，防止被实例化 */
    private CartModel() {
    }
    public static CartModel getInstance() {
        if (cartModel == null) {
            cartModel = new CartModel();
        }
        return cartModel;
    }

    public ArrayList<PreCart> getPreCartList() {
        return preCartList;
    }

    public void setPreCartList(ArrayList<PreCart> preCartList) {
        this.preCartList = preCartList;
    }

    public boolean isEmpty(){
        return preCartList.isEmpty();
    }

    public void clear(){
        preCartList.clear();
    }

    public int size(){
        return preCartList.size();
    }

    public PreCart get(int i){
        return preCartList.get(i);
    }

    public void modifyBatchById(int productId, int skuId, String batchId, String batchName){
        PreCart preCart = getById(productId);
        for (int i = 0; i < preCart.getSkuList().size(); i ++){
            if (preCart.getSkuList().get(i).getSkuId() == skuId){
                preCart.getSkuList().get(i).setBatchId(batchId);
                preCart.getSkuList().get(i).setBatchName(batchName);
            }
        }
    }

    public ArrayList<ExtraCost> getExtraCostById(int productId, int skuId){
        PreCart preCart = getById(productId);
        for (int i = 0; i < preCart.getSkuList().size(); i ++){
            if (preCart.getSkuList().get(i).getSkuId() == skuId){
                return preCart.getSkuList().get(i).getPremiumLists();
            }
        }
        return null;
    }

    public void modifyExtraCostById(int productId, int skuId, ArrayList<ExtraCost> extraCostList){
        PreCart preCart = getById(productId);
        for (int i = 0; i < preCart.getSkuList().size(); i ++){
            if (preCart.getSkuList().get(i).getSkuId() == skuId){
                preCart.getSkuList().get(i).clearPremiumLists();
                preCart.getSkuList().get(i).setPremiumLists(extraCostList);
                preCart.setTotalNumber();
            }
        }
    }

    public PreCart getById(int productId){
        for (int i = 0; i < preCartList.size(); i++) {
            if (preCartList.get(i).getProductId() == productId) {
                return preCartList.get(i);
            }
        }
        return null;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public int indexOf(PreCart preCart){
        return preCartList.indexOf(preCart);

    }

    public void add(PreCart preCart){
        preCartList.add(preCart);
    }

    public void add(int i, PreCart preCart){
        preCartList.add(i, preCart);
    }

    public void remove(PreCart preCart){
        preCartList.remove(preCart);
    }
    public void remove(int productId ){
        for (int i = 0; i < preCartList.size(); i++) {
            if (preCartList.get(i).getProductId() == productId) {
                preCartList.remove(i);
            }
        }
    }

    public String getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(String supplierId) {
        this.supplierId = supplierId;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public String getWarehouseId() {
        return warehouseId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public void setWarehouseId(String warehouseId) {
        this.warehouseId = warehouseId;
    }

    public String getWarehouseName() {
        return warehouseName;
    }

    public void setWarehouseName(String warehouseName) {
        this.warehouseName = warehouseName;
    }

    public String getCurrentShopId() {
        return currentShopId;
    }

    public void setCurrentShopId(String currentShopId) {
        this.currentShopId = currentShopId;
    }

    public String getCurrentShopName() {
        return currentShopName;
    }

    public String getDebtDealsPrice() {
        return debtDealsPrice;
    }

    public void setDebtDealsPrice(String debtDealsPrice) {
        this.debtDealsPrice = debtDealsPrice;
    }

    public void setCurrentShopName(String currentShopName) {
        this.currentShopName = currentShopName;
    }

    public String getCurrentEmployeeId() {
        return currentEmployeeId;
    }

    public void setCurrentEmployeeId(String currentEmployeeId) {
        this.currentEmployeeId = currentEmployeeId;
    }

    public String getCurrentEmployeeName() {
        return currentEmployeeName;
    }

    public void setCurrentEmployeeName(String currentEmployeeName) {
        this.currentEmployeeName = currentEmployeeName;
    }

    /**
     * 修改购物车商品的数据
     *
     * @param preCart
     * @param number
     */
    public void modifyProductData(PreCart preCart, String number) {
        for (int i = 0; i < preCartList.size(); i++) {
            if (preCartList.get(i).getProductId() == preCart.getProductId()) {
                preCartList.get(i).setTotalNumber(number);
            }
        }
    }

    /**
     * 修改购物车商品的数据
     *
     * @param preCart
     */
    public void modifyProductData(PreCart preCart) {
        for (int i = 0; i < preCartList.size(); i++) {
            if (preCartList.get(i).getProductId() == preCart.getProductId()) {
                preCartList.get(i).setSkuList(preCart.getSkuList());
                preCartList.get(i).setTotalNumber(preCart.getTotalNumber());
            }
        }
    }

    /**
     * 修改购物车商品的数据
     *
     * @param productId
     * @param number
     */
    public void modifyProductData(int productId, String number) {
        for (int i = 0; i < preCartList.size(); i++) {
            if (preCartList.get(i).getProductId() == productId) {
                preCartList.get(i).setTotalNumber(number);
            }
        }
    }

    /**
     * 获取购物车商品的数量
     *
     * @param productId
     */
    public String getProductNumber(int productId) {
        String number = "";
        for (int i = 0; i < preCartList.size(); i++) {
            if (preCartList.get(i).getProductId() == productId) {
                number = preCartList.get(i).getTotalNumber();
                break;
            }
        }
        return number;
    }

    public boolean checkProductExist(int productId) {
        for (int i = 0; i < preCartList.size(); i++) {
            if (preCartList.get(i).getProductId() == productId) {
                return true;
            }
        }
        return false;
    }

    public String getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(String totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getTotalAmount(){
        BigDecimal mCountDecimal = new BigDecimal("0");
        for (int i = 0; i<preCartList.size(); i++){
            try {
                mCountDecimal = mCountDecimal.add(new BigDecimal(preCartList.get(i).getTotalNumber()));
            }catch (NumberFormatException e){
                e.printStackTrace();
            }
        }
        return mCountDecimal.toPlainString();
    }


    public String getTotalMoneyByStock(){
        setTotalMoneyByStock();
        return getTotalPrice();
    }

    public void setTotalMoneyByStock(){
        BigDecimal zeroBigDecimal = new BigDecimal("0");
        for (int i = 0; i<preCartList.size(); i++){
            for (int j = 0; j<preCartList.get(i).getSkuList().size();j++){
                BigDecimal priceBigDecimal = new BigDecimal(preCartList.get(i).getSkuList().get(j).getStockPrice());
                BigDecimal amountBigDecimal = new BigDecimal(preCartList.get(i).getSkuList().get(j).getSaleAmount());
                zeroBigDecimal = zeroBigDecimal.add(priceBigDecimal.multiply(amountBigDecimal));
                if (preCartList.get(i).getSkuList().get(j).getTotalPremium() != null) {
                    zeroBigDecimal = zeroBigDecimal.add(new BigDecimal(preCartList.get(i).getSkuList().get(j).getTotalPremium()));
                }
            }
        }
        BigDecimal bigDecimal = new BigDecimal(zeroBigDecimal.toPlainString()).setScale(2, BigDecimal.ROUND_HALF_UP);
        totalPrice = bigDecimal.toPlainString();
        Log.e("sadada", "sad" + totalPrice);
    }


    public String getTotalMoney(){
        setTotalMoney();
        return getTotalPrice();
    }

    public void setTotalMoney(){
        BigDecimal zeroBigDecimal = new BigDecimal("0");
        for (int i = 0; i<preCartList.size(); i++){
            for (int j = 0; j<preCartList.get(i).getSkuList().size();j++){
                BigDecimal priceBigDecimal = new BigDecimal(preCartList.get(i).getSkuList().get(j).getStandardPrice());
                BigDecimal amountBigDecimal = new BigDecimal(preCartList.get(i).getSkuList().get(j).getSaleAmount());
                zeroBigDecimal = zeroBigDecimal.add(priceBigDecimal.multiply(amountBigDecimal));
                if (preCartList.get(i).getSkuList().get(j).getTotalPremium() != null) {
                    zeroBigDecimal = zeroBigDecimal.add(new BigDecimal(preCartList.get(i).getSkuList().get(j).getTotalPremium()));
                }
            }
        }
        BigDecimal bigDecimal = new BigDecimal(zeroBigDecimal.toPlainString()).setScale(2, BigDecimal.ROUND_HALF_UP);
        totalPrice = bigDecimal.toPlainString();
        Log.e("sadada", "sad" + totalPrice);
    }

    public void listToJson(){
        for (int i = 0; i<preCartList.size(); i++){
            preCartList.get(i).listToJson();
        }
    }

    public void fromJson2List(String jsonStr){
        for (int i = 0; i<preCartList.size(); i++){
            preCartList.get(i).fromJson2List();
        }
    }
}
