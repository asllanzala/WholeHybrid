package com.honeywell.wholesale.ui.login.module;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.honeywell.wholesale.framework.model.Inventory;

import java.util.ArrayList;

/**
 * Created by xiaofei on 7/18/16.
 *
 */
public class UpdateInventoryCloudApiResponse {

    @SerializedName("last_update_time")
    private String mLastUpdateTime;

    @SerializedName("next_page")
    private String isNextPage;

    @SerializedName("product_list")
    private ArrayList<Inventory> arrayList;


    public String getJsonString() {
        return new Gson().toJson(this);
    }
}
