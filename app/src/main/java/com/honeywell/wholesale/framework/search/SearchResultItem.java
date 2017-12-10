package com.honeywell.wholesale.framework.search;

/**
 * Created by xiaofei on 8/31/16.
 *
 */
public class SearchResultItem {
    private String title;
    private String info;

    public SearchResultItem(String title, String info) {
        this.title = title;
        this.info = info;
    }

    public String getTitle() {
        return title;
    }

    public String getInfo() {
        return info;
    }

    public enum ResultType {
        INVENTORY,
        TRANSACTION,
        CUSTOMER,
        VENDOR,
    }
}
