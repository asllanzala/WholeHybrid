package com.honeywell.wholesale.framework.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by xiaofei on 4/24/17.
 *
 */

public class WareHouse {
    private static final String TAG = WareHouse.class.getSimpleName();

    @SerializedName("warehouse_id")
    private int wareHouseId;

    @SerializedName("warehouse_name")
    private String wareHouseName;

    @SerializedName("warehouse_location")
    private String location;

    @SerializedName("warehouse_remarks")
    private String remarks;

    @SerializedName("warehouse_owner")
    private WareHouseOwner wareHouseOwner = new WareHouseOwner();

    private boolean isDefault = false;

    private boolean isSelected = false;

    public WareHouse() {
    }

    public WareHouse(String wareHouseOwnerName) {
        this.wareHouseOwner.employeeName = wareHouseOwnerName;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public void setDefault(boolean aDefault) {
        isDefault = aDefault;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public int getWareHouseId() {
        return wareHouseId;
    }

    public String getWareHouseName() {
        return wareHouseName;
    }

    public String getLocation() {
        return location;
    }

    public String getRemarks() {
        return remarks;
    }

    public WareHouseOwner getWareHouseOwner() {
        return wareHouseOwner;
    }

    public String getWareHouseOwnerName(){
        if (wareHouseOwner.getEmployeeName() == null){
            return "";
        }else {
            return wareHouseOwner.getEmployeeName();
        }
    }

    public int getWareHouseOwnerId(){
        return wareHouseOwner.getEmployeeId();
    }

    public void setWareHouseId(int wareHouseId) {
        this.wareHouseId = wareHouseId;
    }

    public void setWareHouseName(String wareHouseName) {
        this.wareHouseName = wareHouseName;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public void setWareHouseOwner(int employeeId, String employeeName){
        setWareHouseOwner(new WareHouseOwner(employeeId, employeeName));
    }

    public void setWareHouseOwner(WareHouseOwner wareHouseOwner) {
        this.wareHouseOwner = wareHouseOwner;
    }

    class WareHouseOwner{
        @SerializedName("employee_id")
        private int employeeId;

        @SerializedName("employee_name")
        private String employeeName;

        public WareHouseOwner() {
        }

        public WareHouseOwner(int employeeId, String employeeName) {
            this.employeeId = employeeId;
            this.employeeName = employeeName;
        }

        public int getEmployeeId() {
            return employeeId;
        }

        public String getEmployeeName() {
            if (employeeName == null){
                return "";
            }else {
                return employeeName;
            }
        }
    }
}
