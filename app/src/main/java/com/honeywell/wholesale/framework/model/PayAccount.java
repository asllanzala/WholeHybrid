package com.honeywell.wholesale.framework.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by liuyang on 2017/6/7.
 */

public class PayAccount {
    @SerializedName("account_id")
    private int accountId;
    @SerializedName("account_name")
    private String accountName;
    @SerializedName("account_number")
    private String accountNumber;
    @SerializedName("income")
    private String income;


    public PayAccount(int accountId, String accountName, String accountNumber) {
        this.accountId = accountId;
        this.accountName = accountName;
        this.accountNumber = accountNumber;
    }

    public PayAccount(int accountId, String accountName) {
        this.accountId = accountId;
        this.accountName = accountName;
    }

    public int getAccountId() {
        return accountId;
    }

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getIncome() {
        return income;
    }

    public void setIncome(String income) {
        this.income = income;
    }
}
