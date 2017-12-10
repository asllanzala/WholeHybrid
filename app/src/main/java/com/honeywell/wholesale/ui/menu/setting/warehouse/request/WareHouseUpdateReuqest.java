package com.honeywell.wholesale.ui.menu.setting.warehouse.request;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;
import com.honeywell.wholesale.framework.model.WareHouse;

import org.json.JSONObject;

/**
 * Created by H154326 on 17/4/27.
 * Email: yang.liu6@honeywell.com
 */

public class WareHouseUpdateReuqest {

    @SerializedName("warehouse_id")
    private int wareHouseId;
    @SerializedName("warehouse_name")
    private String wareHouseName;
    @SerializedName("warehouse_owner")
    private  int wareHouseOwner;
    @SerializedName("warehouse_location")
    private String wareHouseLocation;
    @SerializedName("warehouse_remarks")
    private String wareHouseRemarks;

    public WareHouseUpdateReuqest() {
    }

    public WareHouseUpdateReuqest(WareHouse wareHouse) {
        this.wareHouseId = wareHouse.getWareHouseId();
        this.wareHouseName = wareHouse.getWareHouseName();
        this.wareHouseOwner = wareHouse.getWareHouseOwnerId();
        this.wareHouseLocation = wareHouse.getLocation();
        this.wareHouseRemarks = wareHouse.getRemarks();
    }

    public WareHouseUpdateReuqest(int wareHouseId, String wareHouseName, int wareHouseOwner, String wareHouseLocation, String wareHouseRemarks) {
        this.wareHouseId = wareHouseId;
        this.wareHouseName = wareHouseName;
        this.wareHouseOwner = wareHouseOwner;
        this.wareHouseLocation = wareHouseLocation;
        this.wareHouseRemarks = wareHouseRemarks;
    }

    public int getWareHouseId() {
        return wareHouseId;
    }

    public void setWareHouseId(int wareHouseId) {
        this.wareHouseId = wareHouseId;
    }

    public String getWareHouseName() {
        return wareHouseName;
    }

    public void setWareHouseName(String wareHouseName) {
        this.wareHouseName = wareHouseName;
    }

    public void setWareHouseOwner(int wareHouseOwner) {
        this.wareHouseOwner = wareHouseOwner;
    }

    public String getWareHouseLocation() {
        return wareHouseLocation;
    }

    public void setWareHouseLocation(String wareHouseLocation) {
        this.wareHouseLocation = wareHouseLocation;
    }

    public int getWareHouseOwner() {
        return wareHouseOwner;
    }

    public String getWareHouseRemarks() {
        return wareHouseRemarks;
    }

    public void setWareHouseRemarks(String wareHouseRemarks) {
        this.wareHouseRemarks = wareHouseRemarks;
    }

    public String getJsonString(){
        return new Gson().toJson(this);
    }

    public JSONObject getJsonObject(){
        try {
            return new JSONObject(getJsonString());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
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
            return employeeName;
        }
    }
}
