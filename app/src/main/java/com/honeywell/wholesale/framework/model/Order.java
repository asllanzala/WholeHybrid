package com.honeywell.wholesale.framework.model;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.honeywell.wholesale.framework.database.CartDAO;

import java.util.Arrays;

/**
 * “order_number”:
 * "sale_time": "2016-06-23 10:47:32",
 *
 * "employee_name":"xiaowang",
 * "employee_id": "2",
 *
 * "customer_id": "1212",
 * "customer_name":"xiaoli",
 * "address": "上海浦东",
 * "invoice_title":"clothes",
 * "contact_name": "Tom",
 * "contact_phone": "18911",
 *
 * "total_price": "22.22",
 * "payment": "1",  0-未知渠道, 1-现款，2-赊账，3-电子支付
 * "order_status": 0 or 1 or 100    //optional, 0:待支付订单，  1:已支付完成订单， 100：已取消订单
 * "shop_id":"19",
 * "sale_list": [{"price": "23", "name": "红茶", "number": "2", "product_code": "999888998", "unit":"箱"}]
 */
public class Order implements BaseModel{

    @SerializedName("order_number")
    private String mOrderNumber;
    @SerializedName("sale_id")
    private String mSaleId;

    @SerializedName("sale_time")
    private String mSaleTime;
    @SerializedName("employee_name")
    private String mEmployeeName;
    @SerializedName("employee_id")
    private String mEmployeeID;

    @SerializedName("customer_id")
    private String mCustomerID;
    @SerializedName("customer_name")
    private String mCustomerName;
    @SerializedName("address")
    private String mAddress;
    @SerializedName("contact_name")
    private String mContactName;
    @SerializedName("contact_phone")
    private String mContactPhone;
    @SerializedName("invoice_title")
    private String mInvoiceTitle;

    @SerializedName("total_price")
    private String mTotalPrice;
    @SerializedName("payment")
    private String mPayment;

    @SerializedName("order_status")
    private String mOrderStatus;

    @SerializedName("gmt_expired")
    private String mOrderDeadLine;

    @SerializedName("shop_id")
    private String mShopId;
    @SerializedName("sale_list")
    private Product[] mSaleList;

    @SerializedName("set_pay_employee_name")
    private String mSetPayEmpolyeeName;

    @SerializedName("cancel_comment")
    private String mCancelComment;

    @SerializedName("finish_dt")
    private String mFinishDt;

    @SerializedName("cancel_employee_name")
    private String mCancelEmployeeName;

    @SerializedName("set_pay_dt")
    private String mSetPayDt;

    @SerializedName("cancel_dt")
    private String mCancelDt;

    @SerializedName("finish_employee_name")
    private String mFinishEmployeeName;

    @SerializedName("shop_name")
    private String shopName;

    @SerializedName("remark")
    private String remark = "";

    @SerializedName("finish_dt_format")
    private String finishDtFormat;

    @SerializedName("set_pay_dt_format")
    private String setPayDtFormat;

    @SerializedName("cancel_dt_format")
    private String cancelDtFormat;


    public Order() {

    }

    public Order(String mOrderNumber, String mSaleTime, String mEmployeeName, String mEmployeeID,
                 String mCustomerID, String mCustomerName, String mAddress, String mContactName,
                 String mContactPhone, String mInvoiceTitle, String mTotalPrice, String mPayment,
                 String mOrderStatus, String mOrderDeadLine, String mShopId, Product[] mSaleList,
                 String mSetPayEmpolyeeName, String mCancelComment, String mFinishDt, String mCancelEmployeeName,
                 String mSetPayDt, String mCancelDt, String mFinishEmployeeName) {
        this.mOrderNumber = mOrderNumber;
        this.mSaleTime = mSaleTime;
        this.mEmployeeName = mEmployeeName;
        this.mEmployeeID = mEmployeeID;
        this.mCustomerID = mCustomerID;
        this.mCustomerName = mCustomerName;
        this.mAddress = mAddress;
        this.mContactName = mContactName;
        this.mContactPhone = mContactPhone;
        this.mInvoiceTitle = mInvoiceTitle;
        this.mTotalPrice = mTotalPrice;
        this.mPayment = mPayment;
        this.mOrderStatus = mOrderStatus;
        this.mOrderDeadLine = mOrderDeadLine;
        this.mShopId = mShopId;
        this.mSaleList = mSaleList;
        this.mSetPayEmpolyeeName = mSetPayEmpolyeeName;
        this.mCancelComment = mCancelComment;
        this.mFinishDt = mFinishDt;
        this.mCancelEmployeeName = mCancelEmployeeName;
        this.mSetPayDt = mSetPayDt;
        this.mCancelDt = mCancelDt;
        this.mFinishEmployeeName = mFinishEmployeeName;
    }

    public Order(String totalPrice, String address, String contactName, String contactPhone,
                 String customerID, String customerName, String employeeID, String employeeName,
                 String invoiceTitle, String orderNumber, String payment, String paymentStatus,
                 Product[] saleList, String saleTime, String shopId) {
        mTotalPrice = totalPrice;
        mAddress = address;
        mContactName = contactName;
        mContactPhone = contactPhone;
        mCustomerID = customerID;
        mCustomerName = customerName;
        mEmployeeID = employeeID;
        mEmployeeName = employeeName;
        mInvoiceTitle = invoiceTitle;
        mOrderNumber = orderNumber;
        mPayment = payment;
        mOrderStatus = paymentStatus;
        mSaleList = saleList;
        mSaleTime = saleTime;
        mShopId = shopId;
    }

    public String getCancelDtFormat() {
        return cancelDtFormat;
    }

    public void setCancelDtFormat(String cancelDtFormat) {
        this.cancelDtFormat = cancelDtFormat;
    }

    public String getmSetPayEmpolyeeName() {
        return mSetPayEmpolyeeName;
    }

    public String getmCancelComment() {
        return mCancelComment;
    }

    public String getmFinishDt() {
        return mFinishDt;
    }

    public String getFinishDtFormat() {
        return finishDtFormat;
    }

    public void setFinishDtFormat(String finishDtFormat) {
        this.finishDtFormat = finishDtFormat;
    }

    public String getSetPayDtFormat() {
        return setPayDtFormat;
    }

    public void setSetPayDtFormat(String setPayDtFormat) {
        this.setPayDtFormat = setPayDtFormat;
    }

    public String getmCancelEmployeeName() {
        return mCancelEmployeeName;
    }

    public String getmSetPayDt() {
        return mSetPayDt;
    }

    public String getmCancelDt() {
        return mCancelDt;
    }

    public String getmFinishEmployeeName() {
        return mFinishEmployeeName;
    }

    public void setmSetPayEmpolyeeName(String mSetPayEmpolyeeName) {
        this.mSetPayEmpolyeeName = mSetPayEmpolyeeName;
    }

    public void setmCancelComment(String mCancelComment) {
        this.mCancelComment = mCancelComment;
    }

    public void setmFinishDt(String mFinishDt) {
        this.mFinishDt = mFinishDt;
    }

    public void setmCancelEmployeeName(String mCancelEmployeeName) {
        this.mCancelEmployeeName = mCancelEmployeeName;
    }

    public void setmSetPayDt(String mSetPayDt) {
        this.mSetPayDt = mSetPayDt;
    }

    public void setmCancelDt(String mCancelDt) {
        this.mCancelDt = mCancelDt;
    }

    public void setmFinishEmployeeName(String mFinishEmployeeName) {
        this.mFinishEmployeeName = mFinishEmployeeName;
    }

    public void setAddress(String address) {
        mAddress = address;
    }

    public void setContactName(String contactName) {
        mContactName = contactName;
    }

    public void setContactPhone(String contactPhone) {
        mContactPhone = contactPhone;
    }

    public void setCustomerID(String customerID) {
        mCustomerID = customerID;
    }

    public void setCustomerName(String customerName) {
        mCustomerName = customerName;
    }

    public void setEmployeeID(String employeeID) {
        mEmployeeID = employeeID;
    }

    public void setEmployeeName(String employeeName) {
        mEmployeeName = employeeName;
    }

    public void setInvoiceTitle(String invoiceTitle) {
        mInvoiceTitle = invoiceTitle;
    }

    public void setOrderNumber(String orderNumber) {
        mOrderNumber = orderNumber;
    }

    public void setPayment(String payment) {
        mPayment = payment;
    }

    public void setPaymentStatus(String paymentStatus) {
        mOrderStatus = paymentStatus;
    }

    public void setSaleList(Product[] saleList) {
        mSaleList = saleList;
    }

    public void setSaleTime(String saleTime) {
        mSaleTime = saleTime;
    }

    public void setShopId(String shopId) {
        mShopId = shopId;
    }

    public void setTotalPrice(String totalPrice) {
        mTotalPrice = totalPrice;
    }

    public String getAddress() {
        return mAddress;
    }

    public String getContactName() {
        return mContactName;
    }

    public String getContactPhone() {
        return mContactPhone;
    }

    public String getCustomerID() {
        return mCustomerID;
    }

    public String getCustomerName() {
        return mCustomerName;
    }

    public String getEmployeeID() {
        return mEmployeeID;
    }

    public String getEmployeeName() {
        return mEmployeeName;
    }

    public String getInvoiceTitle() {
        return mInvoiceTitle;
    }

    public String getOrderNumber() {
        return mOrderNumber;
    }

    public String getPayment() {
        return mPayment;
    }

    public String getOrderStatus() {
        return mOrderStatus;
    }

    public Product[] getSaleList() {
        return mSaleList;
    }

    public String getSaleTime() {
        return mSaleTime;
    }

    public String getShopId() {
        return mShopId;
    }

    public String getTotalPrice() {
        return mTotalPrice;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public String getJsonString() {
        return (new Gson()).toJson(this);
    }


    public String getmOrderDeadLine() {
        return mOrderDeadLine;
    }



    public void setmOrderDeadLine(String mOrderDeadLine) {
        this.mOrderDeadLine = mOrderDeadLine;
    }

    /**
     * {"price": "23", "name": "红茶", "number": "2", "product_code": "999888998", "unit":"箱"}
     */
    public static class Product{
        @SerializedName("name")
        public String mName;
        @SerializedName("price")
        public String mPrice;
        @SerializedName("number")
        public String mNumber;
        @SerializedName("product_code")
        public String mProductCode;
        @SerializedName("unit")
        public String mUnit;
        @SerializedName("product_number")
        public String mProductNumber;

        public Product(String name, String price, String number, String productCode, String unit, String productNumber) {
            mName = name;
            mPrice = price;
            mNumber = number;
            mProductCode = productCode;
            mUnit = unit;
            mProductNumber = productNumber;
        }

        public String getJsonString() {
            return (new Gson()).toJson(this);
        }
    }
//    public static void createNewOrder(Customer customer){
//        String employeeId = AccountManager.getInstance().getCurrentAccount().getEmployeeId();
//        String userName = AccountManager.getInstance().getCurrentAccount().getUserName();
//        String shopId = AccountManager.getInstance().getCurrentShopId();
//        String shopName = AccountManager.getInstance().getCurrentAccount().getShopName();
//
//        String customerId = customer.getCustomeId();
//        String customerName = customer.getCustomerName();
//        String contactName = customer.getContactName();
//        String contactPhone = customer.getContactPhone();
//        String customerNotes = "";
//        String invoiceTitle = customer.getInvoiceTitle();
//        String address = customer.getAddress();
//
//        CartItem cartItem = new CartItem(employeeId, userName, shopId, shopName,
//                customerId, customerName, contactName, contactPhone, customerNotes,
//                invoiceTitle, address, "");
//
//        CartDAO.addCartItem(cartItem);
//    }
    public static void createNewOrder(Customer customer){
        String employeeId = AccountManager.getInstance().getCurrentAccount().getEmployeeId();
        String userName = AccountManager.getInstance().getCurrentAccount().getUserName();
        String shopId = AccountManager.getInstance().getCurrentShopId();
        String shopName = AccountManager.getInstance().getCurrentAccount().getShopName();

        String customerId = customer.getCustomeId();
        String customerName = customer.getCustomerName();
        String contactName = customer.getContactName();
        String contactPhone = customer.getContactPhone();
        String customerNotes = "";
        String invoiceTitle = customer.getInvoiceTitle();
        String address = customer.getAddress();

        CartItem cartItem = new CartItem(employeeId, userName, shopId, shopName,
                customerId, customerName, contactName, contactPhone, customerNotes,
                invoiceTitle, address, "");

        CartDAO.addCartItem(cartItem);
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    @Override
    public String toString() {
        return "Order{" +
                "mOrderNumber='" + mOrderNumber + '\'' +
                ", mSaleTime='" + mSaleTime + '\'' +
                ", mEmployeeName='" + mEmployeeName + '\'' +
                ", mEmployeeID='" + mEmployeeID + '\'' +
                ", mCustomerID='" + mCustomerID + '\'' +
                ", mCustomerName='" + mCustomerName + '\'' +
                ", mAddress='" + mAddress + '\'' +
                ", mContactName='" + mContactName + '\'' +
                ", mContactPhone='" + mContactPhone + '\'' +
                ", mInvoiceTitle='" + mInvoiceTitle + '\'' +
                ", mTotalPrice='" + mTotalPrice + '\'' +
                ", mPayment='" + mPayment + '\'' +
                ", mOrderStatus='" + mOrderStatus + '\'' +
                ", mOrderDeadLine='" + mOrderDeadLine + '\'' +
                ", mShopId='" + mShopId + '\'' +
                ", mSaleList=" + Arrays.toString(mSaleList) +
                ", mSetPayEmpolyeeName='" + mSetPayEmpolyeeName + '\'' +
                ", mCancelComment='" + mCancelComment + '\'' +
                ", mFinishDt='" + mFinishDt + '\'' +
                ", mCancelEmployeeName='" + mCancelEmployeeName + '\'' +
                ", mSetPayDt='" + mSetPayDt + '\'' +
                ", mCancelDt='" + mCancelDt + '\'' +
                ", mFinishEmployeeName='" + mFinishEmployeeName + '\'' +
                ", shopName='" + shopName + '\'' +
                ", remark='" + remark + '\'' +
                ", finishDtFormat='" + finishDtFormat + '\'' +
                ", setPayDtFormat='" + setPayDtFormat + '\'' +
                ", cancelDtFormat='" + cancelDtFormat + '\'' +
                '}';
    }
}
