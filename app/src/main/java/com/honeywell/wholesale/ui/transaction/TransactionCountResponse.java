package com.honeywell.wholesale.ui.transaction;

import com.google.gson.annotations.SerializedName;

/**
 * Created by xiaofei on 10/14/16.
 *
 * Response http://159.99.93.113:8099/transaction/sale/get_numbers
 * 销售统计：/transaction/sale/get_numbers, 返回  已完成、已取消、未支付、赊账中  四种订单的数量
 */

public class TransactionCountResponse {
    @SerializedName("finished_number")
    private String finishedNumber;

    @SerializedName("inited_number")
    private String initedNumber;

    @SerializedName("canceled_number")
    private String canceledNumber;

    @SerializedName("debting_number")
    private String debtingNumber;

    public TransactionCountResponse(String finishedNumber, String initedNumber, String canceledNumber, String debtingNumber) {
        this.finishedNumber = finishedNumber;
        this.initedNumber = initedNumber;
        this.canceledNumber = canceledNumber;
        this.debtingNumber = debtingNumber;
    }

    public String getFinishedNumber() {
        return finishedNumber;
    }

    public String getInitedNumber() {
        return initedNumber;
    }

    public String getCanceledNumber() {
        return canceledNumber;
    }

    public String getDebtingNumber() {
        return debtingNumber;
    }

    public void setFinishedNumber(String finishedNumber) {
        this.finishedNumber = finishedNumber;
    }

    public void setInitedNumber(String initedNumber) {
        this.initedNumber = initedNumber;
    }

    public void setCanceledNumber(String canceledNumber) {
        this.canceledNumber = canceledNumber;
    }

    public void setDebtingNumber(String debtingNumber) {
        this.debtingNumber = debtingNumber;
    }
}
