package com.honeywell.wholesale.framework.model;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static android.R.attr.order;
import static android.R.attr.thickness;

/**
 * Created by H154326 on 17/3/6.
 * Email: yang.liu6@honeywell.com
 */

public class PreCart {
    private int productId;
    private String productCode;
    private String productNumber;
    private String productName;
    private String unitPrice;
    private String totalNumber = "0";
    private String imageUrl;
    private String unit;
    private String stockQuantity;

    private String uuid;
    private String customerId;
    private String customerName;
    private String employeeId;

    private ArrayList<SKU> skuList = new ArrayList<SKU>();
    private String skuListString;

    public PreCart() {
    }


    public PreCart(PreOrder.PreOrderProduct preOrderProduct){
        this.productId = preOrderProduct.getProductId();
        this.productCode = preOrderProduct.getProductCode();
        this.productNumber = preOrderProduct.getProductNumber();
        this.productName = preOrderProduct.getProductName();
        for (int i = 0; i < preOrderProduct.getSkuProducts().size(); i++){
            SKU sku = new SKU();
            skuList.add(sku);
        }
    }

    public PreCart(CartRefCustomer cartRefCustomer, CartRefSKU cartRefSKU) {

        this.uuid = cartRefCustomer.getUuid();
        this.employeeId = cartRefCustomer.getEmployeeId();
        this.customerId = cartRefCustomer.getCustomerId();
        this.customerName = cartRefCustomer.getCustomerName();
        this.productId = Integer.valueOf(cartRefSKU.getProductId());
        this.productCode = cartRefSKU.getProductCode();
        this.productNumber = cartRefSKU.getProductNumber();
        this.productName = cartRefSKU.getProductName();
        this.unitPrice = cartRefSKU.getProductUnitPrice();
        this.skuListString = cartRefSKU.getSkuJson();
        this.totalNumber = cartRefSKU.getTotalNumber();
        this.imageUrl = cartRefSKU.getImageUrl();
        this.unit = cartRefSKU.getUnitName();
        this.stockQuantity = cartRefSKU.getQuantity();
        fromJson2List();
        setTotalNumber();

    }

    public PreCart(int productId, String productCode, String productNumber, String productName,
                   String unitPrice, String totalNumber, String imageUrl, String unit,
                   String stockQuantity, ArrayList<SKU> arrayList) {
        this.productId = productId;
        this.productCode = productCode;
        this.productNumber = productNumber;
        this.productName = productName;
        this.unitPrice = unitPrice;
        this.totalNumber = totalNumber;
        this.imageUrl = imageUrl;
        this.unit = unit;
        this.stockQuantity = stockQuantity;
        this.skuList = arrayList;
    }

    public PreCart(int productId, String productCode, String productName, String unitPrice,
                   String imageUrl, String stockQuantity, String totalNumber, String unit) {
        this.productId = productId;
        this.productCode = productCode;
        this.productName = productName;
        this.unitPrice = unitPrice;
        this.imageUrl = imageUrl;
        this.stockQuantity = stockQuantity;
        this.totalNumber = totalNumber;
        this.unit = unit;
    }

    public PreCart(int productId, String productCode, String productName, String unitPrice,
                   String imageUrl, String stockQuantity, String unit) {
        this.productId = productId;
        this.productCode = productCode;
        this.productName = productName;
        this.unitPrice = unitPrice;
        this.imageUrl = imageUrl;
        this.stockQuantity = stockQuantity;
        this.unit = unit;
    }

    public PreCart(Inventory inventory) {
        this.productId = inventory.getProductId();
        this.productCode = inventory.getProductCode();
        this.productName = inventory.getProductName();
        this.unitPrice = inventory.getStandardPrice();
        this.imageUrl = inventory.getPicSrc();
        this.unit = inventory.getUnit();
    }

    public PreCart(int productId, String productName, String unitPrice, String imageUrl, String unit, String stockQuantity) {
        this.productId = productId;
        this.productName = productName;
        this.unitPrice = unitPrice;
        this.imageUrl = imageUrl;
        this.unit = unit;
        this.stockQuantity = stockQuantity;
    }

    public void modifySKUData(ArrayList<SKU> skuList) {
        skuList.clear();
        skuList = skuList;
    }

    public void addSkuItem(SKU sku){
        skuList.add(sku);
    }

    public void modifySkuItem(SKU sku){
        for (int i = 0 ; i < skuList.size(); i ++){
            if (sku.getSkuId() == skuList.get(i).getSkuId()){
                skuList.get(i).setSaleAmount(sku.getSaleAmount());
                skuList.get(i).setStandardPrice(sku.getStandardPrice());
            }
        }
    }

    public void deleteSkuItem(SKU sku){
        for (int i = 0 ; i < skuList.size(); i ++){
            if (sku.getSkuId() == skuList.get(i).getSkuId()){
                skuList.remove(sku);
            }
        }
    }

    public ArrayList<SKU> getSkuList() {
        return skuList;
    }

    public void setSkuList(ArrayList<SKU> skuList) {
        this.skuList = skuList;
        setTotalNumber();
    }

    public String getSkuListString() {
        return skuListString;
    }

    public void setSkuListString(String skuListString) {
        this.skuListString = skuListString;
    }

    public void listToJson(){
        Gson gson = new Gson();
        skuListString = gson.toJson(skuList);
    }

    public void fromJson2List(){
        Gson gson = new Gson();
        skuList = new ArrayList<SKU>();
        skuList = gson.fromJson(skuListString, new TypeToken<List<SKU>>(){}.getType());
    }

    public String getFirstImageUrl() {
        String[] ImageUrl = getImageUrl().split(",");
        String firstImageUrl1 = ImageUrl[0].replaceAll("\\[","");
        String firstImageUrl2 = firstImageUrl1.replaceAll("\\]","");
        String firstImageUrl3 = firstImageUrl2.replaceAll("\"","");
        String firstImageUrl = firstImageUrl3.replaceAll("\\\\","");
        return firstImageUrl;
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

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public String getProductNumber() {
        return productNumber;
    }

    public void setProductNumber(String productNumber) {
        this.productNumber = productNumber;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(String unitPrice) {
        this.unitPrice = unitPrice;
    }

    public String getTotalNumber() {
        return totalNumber;
    }

    public void setTotalNumber() {
        BigDecimal mCountDecimal = new BigDecimal("0");
        if (skuList.isEmpty()){
            this.totalNumber = mCountDecimal.toPlainString();
        }else {
            for (SKU sku : skuList) {
                mCountDecimal = mCountDecimal.add(new BigDecimal(sku.getSaleAmount()));
                this.totalNumber = mCountDecimal.toPlainString();
            }
        }
    }

    public void setTotalNumber(String totalNumber) {
        this.totalNumber = totalNumber;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(String stockQuantity) {
        this.stockQuantity = stockQuantity;
    }
}
