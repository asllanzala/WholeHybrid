package com.honeywell.wholesale.ui.menu.setting.warehouse.request;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.honeywell.wholesale.framework.model.WareHouse;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by H154326 on 17/4/27.
 * Email: yang.liu6@honeywell.com
 */

public class WareHouseAddRequest {

    @SerializedName("warehouse_name")
    private String wareHouseName = "";
    @SerializedName("warehouse_owner")
    private int wareHouseOwner;
    @SerializedName("warehouse_location")
    private String wareHouseLocation;
    @SerializedName("warehouse_remarks")
    private String wareHouseRemarks;

    public WareHouseAddRequest() {
    }

    public WareHouseAddRequest(WareHouse wareHouse){
        this.wareHouseName = wareHouse.getWareHouseName();
        this.wareHouseOwner = wareHouse.getWareHouseOwnerId();
        this.wareHouseLocation = wareHouse.getLocation();
        this.wareHouseRemarks = wareHouse.getRemarks();
    }

    public WareHouseAddRequest(String wareHouseName, int wareHouseOwner, String wareHouseLocation, String wareHouseRemarks) {
        this.wareHouseName = wareHouseName;
        this.wareHouseOwner = wareHouseOwner;
        this.wareHouseLocation = wareHouseLocation;
        this.wareHouseRemarks = wareHouseRemarks;
    }

    public String getWareHouseName() {
        return wareHouseName;
    }

    public void setWareHouseName(String wareHouseName) {
        this.wareHouseName = wareHouseName;
    }

    public int getWareHouseOwner() {
        return wareHouseOwner;
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

    public String getWareHouseRemarks() {
        return wareHouseRemarks;
    }

    public void setWareHouseRemarks(String wareHouseRemarks) {
        this.wareHouseRemarks = wareHouseRemarks;
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
