package com.honeywell.wholesale.ui.purchase.presenter;

import com.google.gson.Gson;
import com.honeywell.wholesale.framework.database.SupplierDAO;
import com.honeywell.wholesale.framework.model.AccountManager;
import com.honeywell.wholesale.framework.model.Supplier;
import com.honeywell.wholesale.ui.base.WholesaleBasePresenter;
import com.honeywell.wholesale.ui.purchase.view.SupplierSelectView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Created by liuyang on 2017/7/5.
 */

public class SupplierSelectPresenter extends WholesaleBasePresenter<SupplierSelectView> {


    public SupplierSelectPresenter(SupplierSelectView view) {
        super(view);
    }

    public ArrayList<Object> initSupplierListData() throws JSONException {

        ArrayList<Object> supplierWithGroupList = new ArrayList<>();
        String shopId = AccountManager.getInstance().getCurrentAccount().getCurrentShopId();
        String supplierJsonString = SupplierDAO.getAllSupplierWithGroup(shopId);

        JSONObject supplierJsonObject = null;
        Gson gson = new Gson();

        supplierJsonObject = new JSONObject(supplierJsonString);

        Iterator<String> bfKeys = supplierJsonObject.keys();

        List<String> list = new ArrayList<String>();
        while (bfKeys.hasNext()) {
            String key = bfKeys.next();
            list.add(key);
        }
        Collections.sort(list);
        Iterator<String> keys = list.iterator();
        while (keys.hasNext()) {
            String key = keys.next();
            if (supplierJsonObject.get(key) instanceof JSONArray) {
                JSONArray jsonArray = supplierJsonObject.optJSONArray(key);
                if (jsonArray.length() == 0) {
                    continue;
                }
                supplierWithGroupList.add(key);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject supplierItem = jsonArray.getJSONObject(i);
                    Supplier supplier = gson.fromJson(supplierItem.toString(), Supplier.class);
                    supplierWithGroupList.add(supplier);
                }
            }
        }
        return supplierWithGroupList;
    }
}
