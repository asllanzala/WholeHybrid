package com.honeywell.wholesale.ui.login.module;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

/**
 * Created by e887272 on 6/21/16.
 * "role":2,"token":"07308c09e9e242b3bf7bd9bd338d0871","phone":"1234"
 *
 *
 "  retbody": {
 "      "token": "c53aed3d42724af48b7a509a866ac604",
 "      "role": 0,
 "      "shop_list": [
 "           {
 "              "shop_address": "上海市普陀区景色区", // May be no need
 "              "shop_id": 39,
 "              "shop_name": "个性设计师店铺"
 "           },
 "           {
 "              "shop_address": "上海市闸北区平型关路551弄34号",
 "              "shop_id": 40,
 "              "shop_name": "壹间studio银饰 手工原创艺术品 传承银饰设计"
 "           }
 "       ]
 "   },
 "  message": "success",
 "  retcode": 200
 " }
 */
public class LoginWebServiceResponse {

    @SerializedName("role")
    private String mRole;

    @SerializedName("name")
    private String mName;

    @SerializedName("employee_id")
    private String mEmployeeId;

    @SerializedName("token")
    private String mToken;

    @SerializedName("shop_list")
    private LoginShopResponse[] mShopList;

    public String getJsonString() {
        return new Gson().toJson(this);
    }

    public String getToken() {
        return mToken;
    }

    public void setToken(String token) {
        mToken = token;
    }

    public String getEmployeeId() {
        return mEmployeeId;
    }

    public void setEmployeeId(String mEmployeeId) {
        this.mEmployeeId = mEmployeeId;
    }

    public String getRole() {
        return mRole;
    }

    public void setRole(String role) {
        this.mRole = role;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public LoginShopResponse[] getShopList() {
        return mShopList;
    }

    public void setShopList(
            LoginShopResponse[] shopList) {
        mShopList = shopList;
    }

    public static class LoginShopResponse {

        @SerializedName("shop_id")
        public String mShopId;

        @SerializedName("shop_name")
        public String mShopName;

        @SerializedName("shop_address")
        public String mShopAddress;

        public String getJsonString() {
            return new Gson().toJson(this);
        }

        public String getmShopId() {
            return mShopId;
        }

        public void setmShopId(String mShopId) {
            this.mShopId = mShopId;
        }

        public String getmShopName() {
            return mShopName;
        }

        public void setmShopName(String mShopName) {
            this.mShopName = mShopName;
        }

        public String getmShopAddress() {
            return mShopAddress;
        }

        public void setmShopAddress(String mShopAddress) {
            this.mShopAddress = mShopAddress;
        }
    }

}
