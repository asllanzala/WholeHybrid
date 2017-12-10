package com.honeywell.wholesale.framework.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by H155935 on 16/5/11.
 * Email: xiaofei.he@honeywell.com
 */
public class CurrentShop {
    private String shopId;

    private HashMap<String, ArrayList<Inventory>> inventoryMap = new HashMap<>();
    private ArrayList<Category> categories = new ArrayList<>();

    public CurrentShop(String shopId) {
        this.shopId = shopId;
    }

    public HashMap<String, ArrayList<Inventory>> getInventoryMap() {
        return inventoryMap;
    }

    public ArrayList<Category> getCategories() {
        return categories;
    }

    public void setCategories(ArrayList<Category> categories) {
        this.categories = categories;
    }

    public void setInventoryMap(ArrayList<Inventory> arrayList){
        if (categories.size() == 0){
            throw new RuntimeException("CurrentShop categories have not set");
        }

        ArrayList<Inventory> copyArrayList = new ArrayList<>(arrayList);

        for (Category category: categories) {
            inventoryMap.put(category.getName(), new ArrayList<Inventory>());
        }

        for (Inventory inventory: copyArrayList){
            ArrayList<Inventory> inventoryList = inventoryMap.get(inventory.getCategory());

            if (inventoryList != null){
                inventoryList.add(inventory);
            }

        }
    }
}
