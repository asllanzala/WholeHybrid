package com.honeywell.wholesale.ui.login.module;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * for token timeout
 *('reg/employee/login', 'POST', {'login_name': "", 'password': "", 'company_account':""}
 * {"message":"success","retcode":200,"retbody":{"role":0,"token":"3ed01fe8648f41c69c141f4c95034050","phone":"18516058966"}}
 */
public class LoginCloudApiRequest {

    @SerializedName("company_account")
    private String mCompanyAccount;

    @SerializedName("login_name")
    private String mLoginName;

    @SerializedName("password")
    private String mPassword;

    public LoginCloudApiRequest(String companyAccount, String loginName, String password){
        mCompanyAccount = companyAccount;
        mLoginName = loginName;
        mPassword = password;
    }

    public String getCompanyAccount() {
        return mCompanyAccount;
    }

    public void setCompanyAccount(String companyAccount) {
        mCompanyAccount = companyAccount;
    }

    public String getLoginName() {
        return mLoginName;
    }

    public void setLoginName(String loginName) {
        mLoginName = loginName;
    }

    public String getPassword() {
        return mPassword;
    }

    public void setPassword(String password) {
        mPassword = password;
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

    public static LoginCloudApiRequest fromJson(String jsonString) {
        LoginCloudApiRequest loginWebServiceResponse = new Gson()
                .fromJson(jsonString, LoginCloudApiRequest.class);
        return loginWebServiceResponse;
    }

}
