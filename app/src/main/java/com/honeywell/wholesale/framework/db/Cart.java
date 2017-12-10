package com.honeywell.wholesale.framework.db;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by xiaofei on 3/14/17.
 *
 */
@Entity(nameInDb = "cart")
public class Cart {
    @Id
    private Long id;

    @Property(nameInDb = "login_name")
    private String loginName;

    @Property(nameInDb = "user_name")
    private String userName;

    @Property(nameInDb = "shop_id")
    private String shopId;

    @Property(nameInDb = "customer_id")
    private String customerId;

    @Property(nameInDb = "customer_name")
    private String customerName;

    @Property(nameInDb = "contact_name")
    private String contactName;

    @Property(nameInDb = "contact_phone")
    private String contactPhone;

    @Property(nameInDb = "customer_notes")
    private String contactNotes;

    public String getContactNotes() {
        return this.contactNotes;
    }

    public void setContactNotes(String contactNotes) {
        this.contactNotes = contactNotes;
    }

    public String getContactPhone() {
        return this.contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public String getContactName() {
        return this.contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getCustomerName() {
        return this.customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerId() {
        return this.customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getShopId() {
        return this.shopId;
    }

    public void setShopId(String shopId) {
        this.shopId = shopId;
    }

    public String getUserName() {
        return this.userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getLoginName() {
        return this.loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Generated(hash = 220167163)
    public Cart(Long id, String loginName, String userName, String shopId,
            String customerId, String customerName, String contactName,
            String contactPhone, String contactNotes) {
        this.id = id;
        this.loginName = loginName;
        this.userName = userName;
        this.shopId = shopId;
        this.customerId = customerId;
        this.customerName = customerName;
        this.contactName = contactName;
        this.contactPhone = contactPhone;
        this.contactNotes = contactNotes;
    }

    @Generated(hash = 1029823171)
    public Cart() {
    }
}
