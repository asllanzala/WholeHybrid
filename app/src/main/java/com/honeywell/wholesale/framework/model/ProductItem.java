package com.honeywell.wholesale.framework.model;

import java.util.List;

/**
 * Created by zhujunyu on 2017/4/25.
 */

public class ProductItem {

    
    private List<ProductChildItem> productChildItems;
    private String productName;


    public List<ProductChildItem> getProductChildItems() {
        return productChildItems;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public void setProductChildItems(List<ProductChildItem> productChildItems) {
        this.productChildItems = productChildItems;
    }
}
