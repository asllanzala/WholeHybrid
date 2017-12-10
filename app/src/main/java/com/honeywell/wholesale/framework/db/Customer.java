package com.honeywell.wholesale.framework.db;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by xiaofei on 3/14/17.
 *
 */
@Entity(nameInDb = "customer")
public class Customer {
    @Id
    private Long id;

    @Property(nameInDb = "customer_id")
    private String customerId;

    @Property(nameInDb = "contact_name")
    private String contactName;

    @Property(nameInDb = "shop_id")
    private String shopId;

    @Property(nameInDb = "contact_phone")
    private String contactPhone;

    @Property(nameInDb = "customer_name")
    private String customerName;

    // 备注信息
    @Property(nameInDb = "memo")
    private String meno;

    @Property(nameInDb = "address")
    private String customerAddress;

    @Property(nameInDb = "invoice_title")
    private String invoiceTitle;

    // 数据分组
    @Property(nameInDb = "cus_group")
    private String cusGroup;

    @Property(nameInDb = "pinyin")
    private String pinyin;

    // 首字母
    @Property(nameInDb = "initials")
    private String initias;

    public String getInitias() {
        return this.initias;
    }

    public void setInitias(String initias) {
        this.initias = initias;
    }

    public String getPinyin() {
        return this.pinyin;
    }

    public void setPinyin(String pinyin) {
        this.pinyin = pinyin;
    }

    public String getCusGroup() {
        return this.cusGroup;
    }

    public void setCusGroup(String cusGroup) {
        this.cusGroup = cusGroup;
    }

    public String getInvoiceTitle() {
        return this.invoiceTitle;
    }

    public void setInvoiceTitle(String invoiceTitle) {
        this.invoiceTitle = invoiceTitle;
    }

    public String getCustomerAddress() {
        return this.customerAddress;
    }

    public void setCustomerAddress(String customerAddress) {
        this.customerAddress = customerAddress;
    }

    public String getMeno() {
        return this.meno;
    }

    public void setMeno(String meno) {
        this.meno = meno;
    }

    public String getCustomerName() {
        return this.customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getContactPhone() {
        return this.contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public String getShopId() {
        return this.shopId;
    }

    public void setShopId(String shopId) {
        this.shopId = shopId;
    }

    public String getContactName() {
        return this.contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getCustomerId() {
        return this.customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Generated(hash = 1182552375)
    public Customer(Long id, String customerId, String contactName, String shopId,
            String contactPhone, String customerName, String meno,
            String customerAddress, String invoiceTitle, String cusGroup,
            String pinyin, String initias) {
        this.id = id;
        this.customerId = customerId;
        this.contactName = contactName;
        this.shopId = shopId;
        this.contactPhone = contactPhone;
        this.customerName = customerName;
        this.meno = meno;
        this.customerAddress = customerAddress;
        this.invoiceTitle = invoiceTitle;
        this.cusGroup = cusGroup;
        this.pinyin = pinyin;
        this.initias = initias;
    }

    @Generated(hash = 60841032)
    public Customer() {
    }

}
