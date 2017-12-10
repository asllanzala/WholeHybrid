package com.honeywell.wholesale.framework.db;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by xiaofei on 3/14/17.
 *
 */

@Entity(nameInDb = "account")
public class Account {
    @Id
    private Long id;

    @Property(nameInDb = "company_account")
    private String companyAccount;

    @Property(nameInDb = "employee_id")
    private String employeeId;

    @Property(nameInDb = "login_name")
    private String loginName;

    @Property(nameInDb = "user_password")
    private String userPassword;

    @Property(nameInDb = "account_role")
    private String accountRole;

    @Property(nameInDb = "token")
    private String token;

    @Property(nameInDb = "shop_id")
    private String shopId;

    @Property(nameInDb = "shop_name")
    private String shopName;

    @Property(nameInDb = "all_shops")
    private String allShops;

    @Property(nameInDb = "user_name")
    private String userName;

    @Property(nameInDb = "update_time")
    private String updateTime;

    public String getUpdateTime() {
        return this.updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public String getUserName() {
        return this.userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getAllShops() {
        return this.allShops;
    }

    public void setAllShops(String allShops) {
        this.allShops = allShops;
    }

    public String getShopName() {
        return this.shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public String getShopId() {
        return this.shopId;
    }

    public void setShopId(String shopId) {
        this.shopId = shopId;
    }

    public String getToken() {
        return this.token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getAccountRole() {
        return this.accountRole;
    }

    public void setAccountRole(String accountRole) {
        this.accountRole = accountRole;
    }

    public String getUserPassword() {
        return this.userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    public String getLoginName() {
        return this.loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public String getEmployeeId() {
        return this.employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public String getCompanyAccount() {
        return this.companyAccount;
    }

    public void setCompanyAccount(String companyAccount) {
        this.companyAccount = companyAccount;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Generated(hash = 898856309)
    public Account(Long id, String companyAccount, String employeeId,
            String loginName, String userPassword, String accountRole,
            String token, String shopId, String shopName, String allShops,
            String userName, String updateTime) {
        this.id = id;
        this.companyAccount = companyAccount;
        this.employeeId = employeeId;
        this.loginName = loginName;
        this.userPassword = userPassword;
        this.accountRole = accountRole;
        this.token = token;
        this.shopId = shopId;
        this.shopName = shopName;
        this.allShops = allShops;
        this.userName = userName;
        this.updateTime = updateTime;
    }

    @Generated(hash = 882125521)
    public Account() {
    }


}
