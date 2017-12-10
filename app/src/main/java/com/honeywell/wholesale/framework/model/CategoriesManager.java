package com.honeywell.wholesale.framework.model;


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Created by xiaofei on 12/22/16.
 *
 */

public class CategoriesManager {
    private static final String TAG = CategoriesManager.class.getSimpleName();

    private static ArrayList<Category> categories = new ArrayList<>();

    private ArrayList<Category> fullCategories = new ArrayList<>();

    private static CategoriesManager instance;
    private Category category;

    private Category[] categoryArray = new Category []{};

    public static CategoriesManager getInstance() {
        if (instance == null){
            instance = new CategoriesManager();
        }
        return instance;
    }

    public ArrayList<Category> getCategories() {
        return categories;
    }


    public void parseArrayToArrayList(String categoryJsonStr){
        Gson gson = new Gson();
        Type listType = new TypeToken<ArrayList<Category>>(){}.getType();
        categories = gson.fromJson(categoryJsonStr, listType);
    }

    public void parseArrayToFullCategory(String categoryJsonStr){
        Gson gson = new Gson();
        Type listType = new TypeToken<ArrayList<Category>>(){}.getType();
        fullCategories = gson.fromJson(categoryJsonStr, listType);
    }

    public ArrayList<Category> getFullCategories() {
        return fullCategories;
    }
}
