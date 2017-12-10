package com.honeywell.wholesale.ui.search;

import com.honeywell.wholesale.framework.model.Customer;
import com.honeywell.wholesale.framework.model.Inventory;
import com.honeywell.wholesale.framework.model.Supplier;

/**
 * Created by xiaofei on 1/11/17.
 *
 */

public class GlobalSearchResultItem {
    private GlobalSearchResultType type;
    private String section;

    private Customer customer;
    private Supplier supplier;
    private Inventory inventory;

    public GlobalSearchResultItem(GlobalSearchResultType type, String section) {
        this.type = type;
        this.section = section;
    }

    public GlobalSearchResultItem(GlobalSearchResultType type, Customer customer) {
        this.type = type;
        this.customer = customer;
    }

    public GlobalSearchResultItem(GlobalSearchResultType type, Supplier supplier) {
        this.type = type;
        this.supplier = supplier;
    }

    public GlobalSearchResultItem(GlobalSearchResultType type, Inventory inventory) {
        this.type = type;
        this.inventory = inventory;
    }

    public GlobalSearchResultType getType() {
        return type;
    }

    public Customer getCustomer() {
        return customer;
    }

    public Supplier getSupplier() {
        return supplier;
    }

    public Inventory getInventory() {
        return inventory;
    }

    public String getSection() {
        return section;
    }

    public enum GlobalSearchResultType {
        // initial
        SEARCH_RESULT_SECTION,
        SEARCH_RESULT_INVENTORY,
        SEARCH_RESULT_CUSTOMER,
        SEARCH_RESULT_VENDOR,
        SEARCH_RESULT_MORE,
    }
}
