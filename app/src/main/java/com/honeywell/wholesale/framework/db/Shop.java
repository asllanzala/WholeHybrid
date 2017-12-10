package com.honeywell.wholesale.framework.db;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by xiaofei on 3/14/17.
 *
 */

@Entity(nameInDb = "shop")
public class Shop {
    @Id
    private Long id;

    @Property(nameInDb = "shop_id")
    private String shopId;

    @Property(nameInDb = "account_id")
    private String accountId;

    @Property(nameInDb = "shop_name")
    private String shopName;

    public String getShopName() {
        return this.shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public String getAccountId() {
        return this.accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getShopId() {
        return this.shopId;
    }

    public void setShopId(String shopId) {
        this.shopId = shopId;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Generated(hash = 1636130018)
    public Shop(Long id, String shopId, String accountId, String shopName) {
        this.id = id;
        this.shopId = shopId;
        this.accountId = accountId;
        this.shopName = shopName;
    }

    @Generated(hash = 633476670)
    public Shop() {
    }
}
