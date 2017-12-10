package com.honeywell.wholesale.framework.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by H155935 on 16/5/11.
 * Email: xiaofei.he@honeywell.com
 */
public class Employee {
    @SerializedName("employee_id")
    private int employeeId;
    private String username;

    // TODO may not do not have this property
    private String password;
    @SerializedName("employee_name")
    private String employeeName;
    private int shopId;
    private boolean enabled;


    public int getEmployeeId() {
        return employeeId;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public int getShopId() {
        return shopId;
    }

    public boolean isEnabled() {
        return enabled;
    }
}
