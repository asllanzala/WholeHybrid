package com.honeywell.wholesale.ui.transaction.preorders.network;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.honeywell.wholesale.lib.util.StringUtil;
import com.honeywell.wholesale.ui.login.module.LoginWebServiceResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.honeywell.wholesale.ui.friend.supplier.AddSupplierActivity.TAG;

/**
 * Created by H154326 on 17/3/1.
 * Email: yang.liu6@honeywell.com
 */

public class PreTransactionSaleRequest {
    @SerializedName("shop_id")
    private String shopId;
    @SerializedName("total_price")
    private String totalPrice;
    @SerializedName("discount")
    private String discount;
    @SerializedName("customer_company_name")
    private String customerCompanyName;
    @SerializedName("customer_name")
    private String customerName;
    @SerializedName("customer_phone")
    private String customerPhone;
    @SerializedName("address")
    private String address;
    @SerializedName("contact_name")
    private String contactName;
    @SerializedName("contact_phone")
    private String contactPhone;
    @SerializedName("invoice_title")
    private String invoiceTitle;
    @SerializedName("customer_id")
    private String customerId;
    @SerializedName("sale_list")
    private String saleList;

    private ArrayList<SaleList> saleLists;

    public String getShopId() {
        return shopId;
    }

    public void setShopId(String shopId) {
        this.shopId = shopId;
    }

    public String getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(String totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public String getCustomerCompanyName() {
        return customerCompanyName;
    }

    public void setCustomerCompanyName(String customerCompanyName) {
        this.customerCompanyName = customerCompanyName;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerPhone() {
        return customerPhone;
    }

    public void setCustomerPhone(String customerPhone) {
        this.customerPhone = customerPhone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public String getInvoiceTitle() {
        return invoiceTitle;
    }

    public void setInvoiceTitle(String invoiceTitle) {
        this.invoiceTitle = invoiceTitle;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getSaleList() {
        return saleList;
    }

    public void setAllShops(SaleList[] saleLists) {
        try {
            JSONArray jsonArray = new JSONArray(saleLists.toString());
            this.saleList = jsonArray.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void setSaleList(String saleList) {
        this.saleList = saleList;
    }

    public PreTransactionSaleRequest() {
    }

    public class SaleList{
        @SerializedName("product_id")
        private String productId;
        @SerializedName("name")
        private String name;
        @SerializedName("price")
        private String price;
        @SerializedName("number")
        private String number;
        @SerializedName("unit")
        private String unit;

        public SaleList() {
        }

        public SaleList(String productId, String name, String price, String number, String unit) {
            this.productId = productId;
            this.name = name;
            this.price = price;
            this.number = number;
            this.unit = unit;
        }

        public String getProductId() {
            return productId;
        }

        public void setProductId(String productId) {
            this.productId = productId;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getPrice() {
            return price;
        }

        public void setPrice(String price) {
            this.price = price;
        }

        public String getNumber() {
            return number;
        }

        public void setNumber(String number) {
            this.number = number;
        }

        public String getUnit() {
            return unit;
        }

        public void setUnit(String unit) {
            this.unit = unit;
        }
    }

    public String getJsonString() {
        return new Gson().toJson(this);
    }

    public JSONObject getJsonObject() {
        try {
            return new JSONObject(getJsonString());
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
}
