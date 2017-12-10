package com.honeywell.wholesale.framework.model;

import java.util.List;

/**
 * Created by zhujunyu on 2017/4/25.
 */

public class OrderBean {



    private String zhifubao;
    private String weixin;
    private String cash;
    private String bankCard;
    private String customerName;
    private String shopName;
    private String emloyName;
    private String totalPrice;
    private String discountPrice;
    private String adjust;
    private String productTotalPrice;
    private String debt;
    private String orderTime;
    private List<ProductItem> productItemList;


    public String getCustomerName() {
        return customerName;
    }

    public String getShopName() {
        return shopName;
    }

    public String getEmloyName() {
        return emloyName;
    }

    public String getTotalPrice() {
        return totalPrice;
    }

    public String getDiscountPrice() {
        return discountPrice;
    }

    public List<ProductItem> getProductItemList() {
        return productItemList;
    }

    public String getZhifubao() {
        if(zhifubao==null||"null".equals(zhifubao)){
            return "";
        }
        return zhifubao;
    }

    public String getWeixin() {
        if(weixin==null||"null".equals(weixin)){
            return "";
        }
        return weixin;
    }

    public String getCash() {
        if(cash==null||"null".equals(cash)){
            return "";
        }
        return cash;
    }

    public String getBankCard() {
        if(bankCard==null||"null".equals(bankCard)){
            return "";
        }
        return bankCard;
    }

    public String getProductTotalPrice() {
        return productTotalPrice;
    }

    public String getDebt() {
        if(debt==null||"null".equals(debt)){
            return "";
        }
        return debt;
    }

    public String getOrderTime() {
        return orderTime;
    }

    public String getAdjust() {
        return adjust;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public void setEmloyName(String emloyName) {
        this.emloyName = emloyName;
    }

    public void setTotalPrice(String totalPrice) {
        this.totalPrice = totalPrice;
    }

    public void setDiscountPrice(String discountPrice) {
        this.discountPrice = discountPrice;
    }

    public void setProductItemList(List<ProductItem> productItemList) {
        this.productItemList = productItemList;
    }

    public void setDebt(String debt) {
        this.debt = debt;
    }

    public void setOrderTime(String orderTime) {
        this.orderTime = orderTime;
    }

    public void setZhifubao(String zhifubao) {
        this.zhifubao = zhifubao;
    }

    public void setWeixin(String weixin) {
        this.weixin = weixin;
    }

    public void setCash(String cash) {
        this.cash = cash;
    }

    public void setBankCard(String bankCard) {
        this.bankCard = bankCard;
    }

    public void setProductTotalPrice(String productTotalPrice) {
        this.productTotalPrice = productTotalPrice;
    }

    public void setAdjust(String adjust) {
        this.adjust = adjust;
    }
}
