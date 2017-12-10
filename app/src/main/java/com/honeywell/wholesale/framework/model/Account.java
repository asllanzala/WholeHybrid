package com.honeywell.wholesale.framework.model;

import com.google.gson.Gson;

import com.honeywell.wholesale.lib.util.StringUtil;
import com.honeywell.wholesale.ui.login.module.LoginWebServiceResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

/**
 * Created by H155935 on 16/6/22.
 * Email: xiaofei.he@honeywell.com
 */
public class Account {
    public static final String TAG = Account.class.getSimpleName();

    private String companyAccount;
    private String employeeId;
    private String loginName;
    private String password;
    private String role;
    private String token;

    private String currentShopId;
    private String shopName;

    private String updateTime;

    private String username;
    private String allShops; // for boss

    private LoginWebServiceResponse.LoginShopResponse[] mShopList;

    public Account(
            String companyAccount, String employeeId, String loginName,
            String password, String role, String token,
            String currentShopId, String shopName, String allShops, String username){
        this.employeeId = employeeId;
        this.companyAccount = companyAccount;
        this.loginName = loginName;
        this.password = password;
        this.role = role;
        this.currentShopId = currentShopId;
        this.shopName = shopName;
        this.token = token;
        this.username = username;
        setAllShops(allShops);
    }

    public String getCompanyAccount() {
        return companyAccount;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public String getLoginName() {
        return loginName;
    }

    public String getPassword() {
        return password;
    }

    public String getRole() {
        return role;
    }

    public String getToken() {
        return token;
    }

    public String getCurrentShopId() {
        return currentShopId;
    }

    public String getShopName() {
        return shopName;
    }

    public void setCurrentShopId(String currentShopId) {
        this.currentShopId = currentShopId;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }
    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public String getAllShops() {
        return allShops;
    }

    public void setAllShops(String allShops) {
        this.allShops = allShops;

        if (StringUtil.isEmpty(allShops)) {
            Log.e(TAG, "allShops is null");
            return;
        }

        try {
            JSONArray allShopsJSONArray = new JSONArray(allShops);
            mShopList = new LoginWebServiceResponse.LoginShopResponse[allShopsJSONArray.length()];

            for (int i = 0; i < allShopsJSONArray.length(); i++) {
                String jsonString = allShopsJSONArray.getString(i);

                LoginWebServiceResponse.LoginShopResponse loginShopResponse = new Gson()
                        .fromJson(jsonString, LoginWebServiceResponse.LoginShopResponse.class);
                mShopList[i] = loginShopResponse;
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public LoginWebServiceResponse.LoginShopResponse[] getShopList() {
        return mShopList;
    }

    public String getShopName(String shopId){
        if(mShopList != null) {
            for(LoginWebServiceResponse.LoginShopResponse loginShopResponse : mShopList) {
                if(loginShopResponse.mShopId.equals(shopId)) {
                    return loginShopResponse.mShopName;
                }
            }
        }
        return  "";
    }

    public String getUserName() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getJsonStringWithPassword() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("companyAccount", companyAccount);
            jsonObject.put("employeeId", employeeId);
            jsonObject.put("loginName", loginName);
            jsonObject.put("userName", username);
            jsonObject.put("password", password);
            jsonObject.put("shopID", currentShopId);
            jsonObject.put("shopName", shopName);
            jsonObject.put("role", role);
            jsonObject.put("token", token);
            jsonObject.put("allShops", allShops);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

    public String getJsonString() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("companyAccount", companyAccount);
            jsonObject.put("employeeId", employeeId);
            jsonObject.put("loginName", loginName);
            jsonObject.put("userName", username);
            jsonObject.put("shopID", currentShopId);
            jsonObject.put("shopName", shopName);
            jsonObject.put("role", role);
            jsonObject.put("token", token);
            jsonObject.put("allShops", allShops);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

}
