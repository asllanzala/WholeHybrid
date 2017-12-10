package com.honeywell.wholesale.ui.newreport;

/**
 * Created by H154326 on 16/12/29.
 * Email: yang.liu6@honeywell.com
 */

public class ChartDataModel {
    private float mIncome;
    private float mProfit;
    private float mPreAvgProfit;
    private float mBehAvgProfit;
    private float mCreditCreateAmount;
    private long mCurrentDay;
    private String mAxisDate;
    private String mCurrentDate;
    private String mPosition;

    public String getmAxisDate() {
        return mAxisDate;
    }

    public void setmAxisDate(String mAxisDate) {
        this.mAxisDate = mAxisDate;
    }

    public String getmCurrentDate() {
        return mCurrentDate;
    }

    public void setmCurrentDate(String mCurrentDate) {
        this.mCurrentDate = mCurrentDate;
    }

    public ChartDataModel(float mIncome, float mProfit, float mPreAvgProfit, float mBehAvgProfit,float mCreditCreateAmount, long mCurrentDay) {
        this.mIncome = mIncome;
        this.mProfit = mProfit;
        this.mPreAvgProfit = mPreAvgProfit;
        this.mBehAvgProfit = mBehAvgProfit;
        this.mCreditCreateAmount = mCreditCreateAmount;
        this.mCurrentDay = mCurrentDay;
    }

    public ChartDataModel(float mIncome, float mProfit, float mPreAvgProfit, float mBehAvgProfit, float mCreditCreateAmount, long mCurrentDay, String mAxisDate, String mCurrentDate, String mPosition) {
        this.mIncome = mIncome;
        this.mProfit = mProfit;
        this.mPreAvgProfit = mPreAvgProfit;
        this.mBehAvgProfit = mBehAvgProfit;
        this.mCreditCreateAmount = mCreditCreateAmount;
        this.mCurrentDay = mCurrentDay;
        this.mAxisDate = mAxisDate;
        this.mCurrentDate = mCurrentDate;
        this.mPosition = mPosition;
    }

    public float getmIncome() {
        return mIncome;
    }

    public float getmProfit() {
        return mProfit;
    }

    public float getmPreAvgProfit() {
        return mPreAvgProfit;
    }

    public float getmBehAvgProfit() {
        return mBehAvgProfit;
    }

    public long getmCurrentDay() {
        return mCurrentDay;
    }

    public void setmCurrentDay(long mCurrentDay) {
        this.mCurrentDay = mCurrentDay;
    }

    public float getmCreditCreateAmount() {
        return mCreditCreateAmount;
    }

    public void setmCreditCreateAmount(float mCreditCreateAmount) {
        this.mCreditCreateAmount = mCreditCreateAmount;
    }

    public String getmPosition() {
        return mPosition;
    }

    public void setmPosition(String mPosition) {
        this.mPosition = mPosition;
    }
}
