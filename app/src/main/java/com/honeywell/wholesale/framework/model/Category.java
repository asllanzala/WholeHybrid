package com.honeywell.wholesale.framework.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by H155935 on 16/5/11.
 * Email: xiaofei.he@honeywell.com
 */
public class Category {
    @SerializedName("category_id")
    private String categoryId;
    @SerializedName("category_name")
    private String name;

    public Category(String categoryId, String name) {
        this.name = name;
        this.categoryId = categoryId;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getCategoryId() {
        return categoryId;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null){
            return false;
        }

        if (o == this){
            return true;
        }

        if (!(o instanceof Category)){
            return false;
        }

        Category other = (Category)o;

        if (other.getCategoryId().equals(this.categoryId) && other.getName().equals(this.name)){
            return true;
        }

        return false;
    }
}
