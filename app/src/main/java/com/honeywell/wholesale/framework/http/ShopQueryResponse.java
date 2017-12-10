package com.honeywell.wholesale.framework.http;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by H154326 on 16/11/8.
 * Email: yang.liu6@honeywell.com
 */

public class ShopQueryResponse {

    @SerializedName("allShops")
    private String shopListString;

    private ArrayList<ShopListResponse> mShopList;

    public String getShopListString() {
        return shopListString;
    }

    public ArrayList<ShopListResponse> getmShopList() {
        Gson gson = new Gson();
        mShopList = gson.fromJson(shopListString, new TypeToken<List<ShopListResponse>>(){}.getType());
        return mShopList;
    }


    public static class ShopListResponse {
        @SerializedName("shop_id")
        public String mShopId;
        @SerializedName("shop_name")
        public String mShopName;
        @SerializedName("shop_address")
        public String mShopAddress;
        public String getJsonString() {
            return new Gson().toJson(this);
        }
    }

}
