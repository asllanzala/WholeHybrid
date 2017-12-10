package com.honeywell.wholesale.ui.newreport;

/**
 * Created by H154326 on 17/1/4.
 * Email: yang.liu6@honeywell.com
 */

public class ChartAssistDataModel {
    private float mLowestIncome;
    private float mHighestIncome;

    private float mLowestProfit;
    private float mHighestProfit;

    public ChartAssistDataModel() {
    }

    public ChartAssistDataModel(float mLowestIncome, float mHighestIncome, float mLowestProfit, float mHighestProfit) {
        this.mLowestIncome = mLowestIncome;
        this.mHighestIncome = mHighestIncome;
        this.mLowestProfit = mLowestProfit;
        this.mHighestProfit = mHighestProfit;
    }

    public void setAssistData(float mLowestIncome, float mHighestIncome, float mLowestProfit, float mHighestProfit){
        this.mLowestIncome = mLowestIncome;
        this.mHighestIncome = mHighestIncome;
        this.mLowestProfit = mLowestProfit;
        this.mHighestProfit = mHighestProfit;
    }

    public void setmLowestIncome(float mLowestIncome) {
        this.mLowestIncome = mLowestIncome;
    }

    public void setmHighestIncome(float mHighestIncome) {
        this.mHighestIncome = mHighestIncome;
    }

    public void setmLowestProfit(float mLowestProfit) {
        this.mLowestProfit = mLowestProfit;
    }

    public void setmHighestProfit(float mHighestProfit) {
        this.mHighestProfit = mHighestProfit;
    }

    public float getmLowestIncome() {
        return mLowestIncome;
    }

    public float getmHighestIncome() {
        return mHighestIncome;
    }

    public float getmLowestProfit() {
        return mLowestProfit;
    }

    public float getmHighestProfit() {
        return mHighestProfit;
    }
}
