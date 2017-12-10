package com.honeywell.wholesale.framework.model;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * “name”: “红茶”,
 * "count": 8,
 * "unit": "箱"
 * "unit_price": "12.33",
 * “stock_in_employee_name”: “阿花”,
 * “stock_in_time”: “23414321421”,  // 时间用long形
 * “stock_finish_time”: “吴富贵”
 * “stock_finish_employee_name”: “1234124124”,
 */
public class StockItem {

    @SerializedName("product_number")
    public String mProductNumber;

    @SerializedName("product_code")
    public String mProductCode;

    @SerializedName("name")
    public String mName;

    @SerializedName("count")
    public String mCount;

    @SerializedName("unit")
    public String mUnit;

    @SerializedName("unit_price")
    public String mUnitPrice;

    @SerializedName("stock_in_employee_name")
    public String mStockInEmployeeName;

    @SerializedName("stock_in_time")
    public String mStockInTime;

    @SerializedName("stock_finish_time")
    public String mStockFinishTime;

    @SerializedName("stock_finish_name")
    public String mStockFinishEmployeeName;

    @SerializedName("shop_id")
    public String mShopId;

    @SerializedName("stock_record_id")
    public String mStockRecordId;

    @Expose
    private String status;

    public StockItem(String count, String name, String stockFinishEmployeeName,
            String stockFinishTime, String stockInEmployeeName, String stockInTime,
            String unit, String unitPrice, String productCode, String stockRecordId, String shopId, String productNumber) {
        mCount = count;
        mName = name;
        mStockFinishEmployeeName = stockFinishEmployeeName;
        mStockFinishTime = stockFinishTime;
        mStockInEmployeeName = stockInEmployeeName;
        mStockInTime = stockInTime;
        mUnit = unit;
        mUnitPrice = unitPrice;
        mProductCode = productCode;
        mStockRecordId = stockRecordId;
        mShopId = shopId;
        mProductNumber = productNumber;
    }

    public String getJsonString() {
        return (new Gson()).toJson(this);
    }

    public String getmName() {
        return mName;
    }

    public void setmName(String mName) {
        this.mName = mName;
    }

    public String getmCount() {
        return mCount;
    }

    public void setmCount(String mCount) {
        this.mCount = mCount;
    }

    public String getmUnit() {
        return mUnit;
    }

    public void setmUnit(String mUnit) {
        this.mUnit = mUnit;
    }

    public String getmUnitPrice() {
        return mUnitPrice;
    }

    public void setmUnitPrice(String mUnitPrice) {
        this.mUnitPrice = mUnitPrice;
    }

    public String getmStockInEmployeeName() {
        return mStockInEmployeeName;
    }

    public void setmStockInEmployeeName(String mStockInEmployeeName) {
        this.mStockInEmployeeName = mStockInEmployeeName;
    }

    public String getmStockInTime() {
        return mStockInTime;
    }

    public void setmStockInTime(String mStockInTime) {
        this.mStockInTime = mStockInTime;
    }

    public String getmStockFinishTime() {
        return mStockFinishTime;
    }

    public void setmStockFinishTime(String mStockFinishTime) {
        this.mStockFinishTime = mStockFinishTime;
    }

    public String getmStockFinishEmployeeName() {
        return mStockFinishEmployeeName;
    }

    public void setmStockFinishEmployeeName(String mStockFinishEmployeeName) {
        this.mStockFinishEmployeeName = mStockFinishEmployeeName;
    }

    public String getmProductCode() {
        return mProductCode;
    }

    public void setmProductCode(String mProductCode) {
        this.mProductCode = mProductCode;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStockRecordId() {
        return mStockRecordId;
    }

    public String getmProductNumber() {
        return mProductNumber;
    }

    public void setmProductNumber(String mProductNumber) {
        this.mProductNumber = mProductNumber;
    }
}
