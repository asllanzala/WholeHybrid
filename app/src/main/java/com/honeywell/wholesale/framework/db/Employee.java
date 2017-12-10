package com.honeywell.wholesale.framework.db;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by xiaofei on 3/14/17.
 *
 */
@Entity(nameInDb = "employee")
public class Employee {
    @Id
    private Long id;

    @Property(nameInDb = "employee_id")
    private int employeeId;

    @Property(nameInDb = "username")
    private String userName;

    @Property(nameInDb = "password")
    private String password;

    @Property(nameInDb = "employee_name")
    private String employeeName;

    @Property(nameInDb = "shop_id")
    private int shopId;

    @Property(nameInDb = "enabled")
    private int enabled;

    public int getEnabled() {
        return this.enabled;
    }

    public void setEnabled(int enabled) {
        this.enabled = enabled;
    }

    public int getShopId() {
        return this.shopId;
    }

    public void setShopId(int shopId) {
        this.shopId = shopId;
    }

    public String getEmployeeName() {
        return this.employeeName;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUserName() {
        return this.userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getEmployeeId() {
        return this.employeeId;
    }

    public void setEmployeeId(int employeeId) {
        this.employeeId = employeeId;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Generated(hash = 698561170)
    public Employee(Long id, int employeeId, String userName, String password,
            String employeeName, int shopId, int enabled) {
        this.id = id;
        this.employeeId = employeeId;
        this.userName = userName;
        this.password = password;
        this.employeeName = employeeName;
        this.shopId = shopId;
        this.enabled = enabled;
    }

    @Generated(hash = 202356944)
    public Employee() {
    }

}
