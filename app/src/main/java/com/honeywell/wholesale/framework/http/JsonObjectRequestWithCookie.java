package com.honeywell.wholesale.framework.http;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.honeywell.wholesale.framework.model.Account;
import com.honeywell.wholesale.framework.model.AccountManager;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by xiaofei on 8/17/16.
 *
 */
public class JsonObjectRequestWithCookie extends JsonObjectRequest {
    public JsonObjectRequestWithCookie(int method, String url, JSONObject jsonRequest, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {
        super(method, url, jsonRequest, listener, errorListener);
    }

    public JsonObjectRequestWithCookie(String url, JSONObject jsonRequest, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {
        super(url, jsonRequest, listener, errorListener);
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        Map<String, String> header = super.getHeaders();

        Map<String, String> httpHeader = new HashMap<>();
        if (header.size() > 0) {
            httpHeader.putAll(header);
        }

        Account currentUser = AccountManager.getInstance().getCurrentAccount();
        // add token to cookie
        if (currentUser != null && currentUser.getToken() != null && currentUser.getToken().length() > 0) {
            String s = currentUser.getToken();

            String tokenString = "token=" + s;
            httpHeader.put("Cookie", tokenString);
        }

        return httpHeader;
    }
}
