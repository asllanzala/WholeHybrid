package com.honeywell.wholesale.framework.model;

import java.util.ArrayList;

/**
 * Created by xiaofei on 4/24/17.
 */

public class WareHouseManager {

    private static WareHouseManager instance;
    private static ArrayList<WareHouse> wareHouses;
    private static int defaultWareHouseId;
    private static String defaultWareHouseName;
    private static int scanWareHouseId;
    private static String scanWareHouseName;

    private WareHouseManager() {
        wareHouses = new ArrayList<>();
    }

    public static WareHouseManager getInstance() {
        if (instance == null) {
            instance = new WareHouseManager();
        }
        return instance;
    }

    public ArrayList<WareHouse> getWareHouses() {
        return wareHouses;
    }

    public void setWareHouse(ArrayList<WareHouse> wareHouses1) {
        WareHouseManager.wareHouses = wareHouses1;
    }

    public int getDefaultWareHouseId() {
        return defaultWareHouseId;
    }

    public void setDefaultWareHouseId(int defaultWareHouseId) {
        WareHouseManager.defaultWareHouseId = defaultWareHouseId;
    }

    public String getDefaultWareHouseName() {
        return defaultWareHouseName;
    }

    public void setDefaultWareHouseName(String defaultWareHouseName) {
        WareHouseManager.defaultWareHouseName = defaultWareHouseName;
    }

    public int getScanWareHouseId() {
        return scanWareHouseId;
    }

    public void setScanWareHouseId(int scanWareHouseId) {
        WareHouseManager.scanWareHouseId = scanWareHouseId;
    }

    public String getScanWareHouseName() {
        return scanWareHouseName;
    }

    public void setScanWareHouseName(String scanWareHouseName) {
        WareHouseManager.scanWareHouseName = scanWareHouseName;
    }
}
