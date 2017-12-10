package com.honeywell.wholesale.framework.model;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by xiaofei on 3/22/17.
 *
 */

public class SKU {

    @SerializedName("inventory_id")
    private int skuId;

    @SerializedName("warehouse_inventory_id")
    private int warehouseInventoryId;

    @SerializedName("product_code")
    private String productCode;

    @SerializedName("product_number")
    private String productNumber;

    @SerializedName("quantity")
    private String quantity = "0";

    @SerializedName("standard_price")
    private String standardPrice = "0";

    @SerializedName("stock_price")
    private String stockPrice = "0";

    @SerializedName("sku_name")
    private String skuName;

    @SerializedName("selected")
    private boolean selected;

    @SerializedName("sale_amount")
    private String saleAmount = "0";

    @SerializedName("sku_value_id1")
    private String skuValueId1;

    @SerializedName("sku_value_id2")
    private String skuValueId2;

    @SerializedName("sku_value_id3")
    private String skuValueId3;

    @SerializedName("sku_value_id4")
    private String skuValueId4;

    @SerializedName("sku_key_id1")
    private String skuKey_Id1;

    @SerializedName("sku_key_id2")
    private String skuKey_Id2;

    @SerializedName("sku_key_id3")
    private String skuKey_Id3;

    @SerializedName("sku_key_id4")
    private String skuKey_Id4;

    @SerializedName("sku_value_name1")
    private String skuValueName1;

    @SerializedName("sku_value_name2")
    private String skuValueName2;

    @SerializedName("sku_value_name3")
    private String skuValueName3;

    @SerializedName("sku_value_name4")
    private String skuValueName4;

    @SerializedName("sku_key_name1")
    private String skuKeyName1;

    @SerializedName("sku_key_name2")
    private String skuKeyName2;

    @SerializedName("sku_key_name3")
    private String skuKeyName3;

    @SerializedName("sku_key_name4")
    private String skuKeyName4;

    @SerializedName("pic_src")
    private List<String> picSrcList;

    @SerializedName("pic_hd_src")
    private List<String> picHdSrcList;

    @SerializedName("batch_id")
    private String batchId;

    @SerializedName("batch_name")
    private String batchName;

    @SerializedName("toatlpremium")
    private String totalPremium;

    @SerializedName("premium_list")
    private ArrayList<ExtraCost> premiumLists;

    public SKU() {
    }

    public SKU(PreOrder.PreOrderSKUProduct preOrderSKUProduct) {

    }

    public SKU(int skuId, String skuValueName1, String skuValueName2, String skuValueName3) {
        this.skuId = skuId;
        this.skuValueName1 = skuValueName1;
        this.skuValueName2 = skuValueName2;
        this.skuValueName3 = skuValueName3;
    }

    public SKU(int skuId, String skuValueName1, String skuValueName2, String skuValueName3, String saleAmount) {
        this.skuId = skuId;
        this.skuValueName1 = skuValueName1;
        this.skuValueName2 = skuValueName2;
        this.skuValueName3 = skuValueName3;
        this.skuValueName4 = skuValueName4;
        this.saleAmount = saleAmount;
    }

    public SKU(int skuId, String productCode, String productNumber, String quantity,
               String standardPrice, String stockPrice, boolean selected, String saleAmount,
               String skuValueName1, String skuKeyName4, String skuValueName2, String skuValueName3,
               String skuValueName4, String skuKeyName1, String skuKeyName2, String skuKeyName3) {
        this.skuId = skuId;
        this.productCode = productCode;
        this.productNumber = productNumber;
        this.quantity = quantity;
        this.standardPrice = standardPrice;
        this.stockPrice = stockPrice;
        this.selected = selected;
        this.saleAmount = saleAmount;
        this.skuValueName1 = skuValueName1;
        this.skuKeyName4 = skuKeyName4;
        this.skuValueName2 = skuValueName2;
        this.skuValueName3 = skuValueName3;
        this.skuValueName4 = skuValueName4;
        this.skuKeyName1 = skuKeyName1;
        this.skuKeyName2 = skuKeyName2;
        this.skuKeyName3 = skuKeyName3;
    }

    public SKU(int skuId, String productCode, String productNumber, String quantity, String standardPrice,
               String skuValueName1, String skuValueName2, String skuValueName3, String skuValueName4,
               String skuKeyName1, String skuKeyName2, String skuKeyName3, String skuKeyName4) {
        this.skuId = skuId;
        this.productCode = productCode;
        this.productNumber = productNumber;
        this.quantity = quantity;
        this.standardPrice = standardPrice;
        this.skuValueName1 = skuValueName1;
        this.skuValueName2 = skuValueName2;
        this.skuValueName3 = skuValueName3;
        this.skuValueName4 = skuValueName4;
        this.skuKeyName1 = skuKeyName1;
        this.skuKeyName2 = skuKeyName2;
        this.skuKeyName3 = skuKeyName3;
        this.skuKeyName4 = skuKeyName4;
    }

    public class Premium{

        @SerializedName("flow_type_id")
        private String flowTypeId;

        @SerializedName("remark")
        private String remark;

        @SerializedName("premium_price")
        private String premiumPrice;

        public Premium() {
        }

        public Premium(String flowTypeId, String remark, String premiumPrice) {
            this.flowTypeId = flowTypeId;
            this.remark = remark;
            this.premiumPrice = premiumPrice;
        }

        public String getFlowTypeId() {
            return flowTypeId;
        }

        public void setFlowTypeId(String flowTypeId) {
            this.flowTypeId = flowTypeId;
        }

        public String getRemark() {
            return remark;
        }

        public void setRemark(String remark) {
            this.remark = remark;
        }

        public String getPremiumPrice() {
            return premiumPrice;
        }

        public void setPremiumPrice(String premiumPrice) {
            this.premiumPrice = premiumPrice;
        }
    }

    public String getBatchId() {
        return batchId;
    }

    public void setBatchId(String batchId) {
        this.batchId = batchId;
    }

    public String getBatchName() {
        return batchName;
    }

    public void setBatchName(String batchName) {
        this.batchName = batchName;
    }

    public int getWarehouseInventoryId() {
        return warehouseInventoryId;
    }

    public void setWarehouseInventoryId(int warehouseInventoryId) {
        this.warehouseInventoryId = warehouseInventoryId;
    }

    public int getSkuId() {
        return skuId;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public void setSkuName(String skuName) {
        this.skuName = skuName;
    }

    public String getSkuName(){
        return skuName;
//        if ((getSkuValueName1() == null) && (getSkuValueName2() == null) && (getSkuValueName3() == null)){
//            return "";
//        }else {
//            return getSkuValueName1() + " , " + getSkuValueName2() + " , " + getSkuValueName3();
//        }
    }

    public String getStockPrice() {
        return stockPrice;
    }

    public void setStockPrice(String stockPrice) {
        this.stockPrice = stockPrice;
    }

    public void setSkuId(int skuId) {
        this.skuId = skuId;
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

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getSaleAmount() {
        return saleAmount;
    }

    public void setSaleAmount(String saleAmount) {
        this.saleAmount = saleAmount;
    }

    public String getStandardPrice() {
        return standardPrice;
    }

    public void setStandardPrice(String standardPrice) {
        this.standardPrice = standardPrice;
    }

    public String getSkuValueName1() {
        return skuValueName1;
    }

    public void setSkuValueName1(String skuValueName1) {
        this.skuValueName1 = skuValueName1;
    }

    public String getSkuValueName2() {
        return skuValueName2;
    }

    public void setSkuValueName2(String skuValueName2) {
        this.skuValueName2 = skuValueName2;
    }

    public String getSkuValueName3() {
        return skuValueName3;
    }

    public void setSkuValueName3(String skuValueName3) {
        this.skuValueName3 = skuValueName3;
    }

    public String getSkuValueName4() {
        return skuValueName4;
    }

    public void setSkuValueName4(String skuValueName4) {
        this.skuValueName4 = skuValueName4;
    }

    public String getSkuKeyName1() {
        return skuKeyName1;
    }

    public void setSkuKeyName1(String skuKeyName1) {
        this.skuKeyName1 = skuKeyName1;
    }

    public String getSkuKeyName2() {
        return skuKeyName2;
    }

    public void setSkuKeyName2(String skuKeyName2) {
        this.skuKeyName2 = skuKeyName2;
    }

    public String getSkuKeyName3() {
        return skuKeyName3;
    }

    public void setSkuKeyName3(String skuKeyName3) {
        this.skuKeyName3 = skuKeyName3;
    }

    public String getSkuKeyName4() {
        return skuKeyName4;
    }

    public void setSkuKeyName4(String skuKeyName4) {
        this.skuKeyName4 = skuKeyName4;
    }

    public String getTotalPremium() {
        if (totalPremium == null){
            return null;
        }else {
            return totalPremium;
        }
    }

    public void setTotalPremium(String totalPremium) {
        this.totalPremium = totalPremium;
    }

    public ArrayList<ExtraCost> getPremiumLists() {
        return premiumLists;
    }

    public void clearPremiumLists(){
        this.premiumLists = new ArrayList<ExtraCost>();
    }

    public void setPremiumLists(ArrayList<ExtraCost> premiumLists) {
        this.premiumLists = premiumLists;
        computeTotalPremium();
    }

    public void computeTotalPremium(){
        BigDecimal totalDecimal = new BigDecimal("0");
        if (getPremiumLists() != null) {
            for (int i = 0; i < getPremiumLists().size(); i++) {
                if (getPremiumLists().get(i).getPremiumPrice() != null) {
                    if ((getPremiumLists().get(i).getPremiumPrice() != null) || (getPremiumLists().get(i).getPremiumPrice().equals(""))) {
                        BigDecimal priceDecimal = new BigDecimal(getPremiumLists().get(i).getPremiumPrice());
                        totalDecimal = totalDecimal.add(priceDecimal);
                        setTotalPremium(totalDecimal.toPlainString());
                    }
                }
            }
        }else {
            setTotalPremium(null);
        }
    }

    public String getJsonString(){
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    public static SKU fromJson(String jsonStr){
        Gson gson = new Gson();
        return gson.fromJson(jsonStr, SKU.class);
    }

    public static ArrayList<SKU> fromJson2List(String jsonStr){
        Gson gson = new Gson();
        return gson.fromJson(jsonStr, new TypeToken<List<SKU>>(){}.getType());
    }
}
