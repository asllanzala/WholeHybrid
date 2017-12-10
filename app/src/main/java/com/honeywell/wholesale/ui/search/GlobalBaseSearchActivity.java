package com.honeywell.wholesale.ui.search;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.honeywell.wholesale.R;
import com.honeywell.wholesale.framework.database.CustomerDAO;
import com.honeywell.wholesale.framework.database.InventoryDAO;
import com.honeywell.wholesale.framework.database.SupplierDAO;
import com.honeywell.wholesale.framework.model.AccountManager;
import com.honeywell.wholesale.framework.model.Customer;
import com.honeywell.wholesale.framework.model.Inventory;
import com.honeywell.wholesale.framework.model.Supplier;
import com.honeywell.wholesale.framework.search.ApiSearch;
import com.honeywell.wholesale.framework.search.SearchManager;
import com.honeywell.wholesale.framework.search.SearchResultItem;
import com.honeywell.wholesale.ui.base.BaseActivity;
import com.honeywell.wholesale.ui.menu.customer.CustomerDetailActivity;
import com.honeywell.wholesale.ui.menu.supplier.SupplierDetailActivity;
import com.honeywell.wholesale.ui.scan.ProductDetailActivity;


import java.util.ArrayList;


/**
 * Created by xiaofei on 1/9/17.
 *
 */

public class GlobalBaseSearchActivity extends BaseActivity {
    private static final String TAG =  "GlobalBaseSearchActivity";

    private ImageView backImageView;
    private ImageView searchCancelImageView;
    private EditText editText;

    private View searchInventoryView;
    private View searchCustomerView;
    private View searchTransactionView;

    private ImageView searchInventoryIcon;
    private ImageView searchCustomerIcon;
    private ImageView searchTransactionIcon;

    private TextView inventoryIconTextView;
    private TextView customerIconTextView;
    private TextView supplierIconTextView;

    private ListView searchResultListView;

    private static int inventoriesSize = 0;
    private static int customersSize = 0;
    private static int supplierSize = 0;

    private SearchManager searchManager = SearchManager.getInstance();
    private ArrayList<GlobalSearchResultItem> arrayList = new ArrayList<>();

    private GlobalSearchListAdapter searchListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_base_search);
        backImageView = (ImageView)findViewById(R.id.icon_back);
        searchCancelImageView = (ImageView)findViewById(R.id.base_search_cancel);
        editText = (EditText)findViewById(R.id.base_search_edittext);

        searchInventoryIcon = (ImageView)findViewById(R.id.search_product_icon);
        searchCustomerIcon = (ImageView)findViewById(R.id.search_customer_icon);
        searchTransactionIcon = (ImageView)findViewById(R.id.search_transaction_icon);

        searchInventoryView = findViewById(R.id.search_product_layout);
        searchCustomerView = findViewById(R.id.search_customer_layout);
        searchTransactionView = findViewById(R.id.search_transaction_layout);

        inventoryIconTextView = (TextView)findViewById(R.id.search_product_text);
        customerIconTextView = (TextView)findViewById(R.id.search_customer_text);
        supplierIconTextView = (TextView)findViewById(R.id.search_supplier_text);

        searchListAdapter = new GlobalSearchListAdapter(this, arrayList);
        searchResultListView = (ListView)findViewById(R.id.base_search_result_list);
        searchResultListView.setAdapter(searchListAdapter);
        searchResultListView.setOnItemClickListener(onItemClickListener);

        backImageView.setOnClickListener(onClickListener);
        searchInventoryIcon.setOnClickListener(onClickListener);
        searchCustomerIcon.setOnClickListener(onClickListener);
        searchTransactionIcon.setOnClickListener(onClickListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        localSearch(editText);
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.icon_back){
                finish();
            }

            if (v.getId() == R.id.search_product_icon){
                Intent intent = new Intent(GlobalBaseSearchActivity.this, BaseSearchActivity.class);
                Bundle mBundle = new Bundle();
                mBundle.putString(BaseSearchActivity.SEARCH_MODE, BaseSearchActivity.SEARCH_SELECT);
                mBundle.putSerializable(BaseSearchActivity.SEARCH_KEY, SearchResultItem.ResultType.INVENTORY);
                mBundle.putString(BaseSearchActivity.SEARCH_DATA, editText.getText().toString());
                intent.putExtras(mBundle);
                startActivity(intent);
            }

            if (v.getId() == R.id.search_customer_icon){
                Intent intent = new Intent(GlobalBaseSearchActivity.this, BaseSearchActivity.class);
                Bundle mBundle = new Bundle();
                mBundle.putString(BaseSearchActivity.SEARCH_MODE, BaseSearchActivity.SEARCH_SELECT);
                mBundle.putSerializable(BaseSearchActivity.SEARCH_KEY, SearchResultItem.ResultType.CUSTOMER);
                mBundle.putString(BaseSearchActivity.SEARCH_DATA, editText.getText().toString());
                intent.putExtras(mBundle);
                startActivity(intent);
            }

            if (v.getId() == R.id.search_transaction_icon){
                Intent intent = new Intent(GlobalBaseSearchActivity.this, BaseSearchActivity.class);
                Bundle mBundle = new Bundle();
                mBundle.putString(BaseSearchActivity.SEARCH_MODE, BaseSearchActivity.SEARCH_SELECT);
                mBundle.putSerializable(BaseSearchActivity.SEARCH_KEY, SearchResultItem.ResultType.VENDOR);
                mBundle.putString(BaseSearchActivity.SEARCH_DATA, editText.getText().toString());
                intent.putExtras(mBundle);
                startActivity(intent);
            }
        }
    };

    private AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            GlobalSearchResultItem item = arrayList.get(position);

            if (item.getType() == GlobalSearchResultItem.GlobalSearchResultType.SEARCH_RESULT_SECTION){
                return;
            }

            if (item.getType() == GlobalSearchResultItem.GlobalSearchResultType.SEARCH_RESULT_INVENTORY){
                Inventory inventory = item.getInventory();

                String barcode = inventory.getProductCode();
                int productId = inventory.getProductId();
                //String barid = inventory
                Intent intent = new Intent(GlobalBaseSearchActivity.this, ProductDetailActivity.class);
                intent.putExtra(ProductDetailActivity.INTENT_KEY_SCAN_RESULT, barcode);
                intent.putExtra(ProductDetailActivity.INTENT_KEY_PRODUCT_ID, productId);
                startActivity(intent);
            }

            if (item.getType() == GlobalSearchResultItem.GlobalSearchResultType.SEARCH_RESULT_CUSTOMER){
                Customer customer = item.getCustomer();

                Intent intent = new Intent(GlobalBaseSearchActivity.this, CustomerDetailActivity.class);
                intent.putExtra(CustomerDetailActivity.INTENT_KEY_CUSTOMER_INFO, customer.getJsonString());
                startActivity(intent);
            }

            if (item.getType() == GlobalSearchResultItem.GlobalSearchResultType.SEARCH_RESULT_VENDOR){
                Supplier supplier = item.getSupplier();
                Intent intent = new Intent(GlobalBaseSearchActivity.this, SupplierDetailActivity.class);
                intent.putExtra(SupplierDetailActivity.INTENT_KEY_VENDOR_INFO, supplier.getJsonString());
                startActivity(intent);
            }

            if (item.getType() == GlobalSearchResultItem.GlobalSearchResultType.SEARCH_RESULT_MORE){

                if (item.getSection().equals(getResources().getString(R.string.global_search_product_more))){
                    Intent intent = new Intent(GlobalBaseSearchActivity.this, BaseSearchActivity.class);
                    Bundle mBundle = new Bundle();
                    mBundle.putString(BaseSearchActivity.SEARCH_MODE, BaseSearchActivity.SEARCH_SELECT);
                    mBundle.putSerializable(BaseSearchActivity.SEARCH_KEY, SearchResultItem.ResultType.INVENTORY);
                    mBundle.putString(BaseSearchActivity.SEARCH_DATA, editText.getText().toString());
                    intent.putExtras(mBundle);
                    startActivity(intent);
                }

                if (item.getSection().equals(getResources().getString(R.string.global_search_customer_more))){
                    Intent intent = new Intent(GlobalBaseSearchActivity.this, BaseSearchActivity.class);
                    Bundle mBundle = new Bundle();
                    mBundle.putString(BaseSearchActivity.SEARCH_MODE, BaseSearchActivity.SEARCH_SELECT);
                    mBundle.putSerializable(BaseSearchActivity.SEARCH_KEY, SearchResultItem.ResultType.CUSTOMER);
                    mBundle.putString(BaseSearchActivity.SEARCH_DATA, editText.getText().toString());
                    intent.putExtras(mBundle);
                    startActivity(intent);
                }

                if (item.getSection().equals(getResources().getString(R.string.global_search_supplier_more))){
                    Intent intent = new Intent(GlobalBaseSearchActivity.this, BaseSearchActivity.class);
                    Bundle mBundle = new Bundle();
                    mBundle.putString(BaseSearchActivity.SEARCH_MODE, BaseSearchActivity.SEARCH_SELECT);
                    mBundle.putSerializable(BaseSearchActivity.SEARCH_KEY, SearchResultItem.ResultType.VENDOR);
                    mBundle.putString(BaseSearchActivity.SEARCH_DATA, editText.getText().toString());
                    intent.putExtras(mBundle);
                    startActivity(intent);
                }

            }
        }
    };

    private void localSearch(View view){
        Log.e("Thread 1", Thread.currentThread().toString());
        searchManager.setApiSearch(new ApiSearch() {
            @Override
            public void onSearchResultListener(ArrayList al) {
                arrayList.clear();
                arrayList.addAll(al);
                searchListAdapter.setAdapteData(arrayList);

                Log.e("Thread 2", Thread.currentThread().toString());
                if (inventoriesSize != 0){
                    inventoryIconTextView.setText("共" + inventoriesSize + "条");
                }else {
                    inventoryIconTextView.setText(getString(R.string.search_inventory));
                }

                if (customersSize != 0){
                    customerIconTextView.setText("共" + customersSize + "条");
                }else {
                    customerIconTextView.setText(getString(R.string.search_customer));
                }

                if (supplierSize != 0){
                    supplierIconTextView.setText("共" + supplierSize + "条");
                }else {
                    supplierIconTextView.setText(getString(R.string.search_vendor));
                }

            }

            @Override
            public ArrayList queryFromLocal(String s) {
                return initialSearch(s);
            }
        });
        searchManager.searchInLocal(view);
    }

    private ArrayList initialSearch(String s){
        // search in local db
        Log.e("Thread 3", Thread.currentThread().toString());
        String shopId = AccountManager.getInstance().getCurrentAccount().getCurrentShopId();

        ArrayList subThreadArrayList = new ArrayList();
        ArrayList<Customer> customers = CustomerDAO.queryCustomerBySearch(s, shopId);
        ArrayList<Inventory> inventories = InventoryDAO.queryInventoryBySearch(s, shopId);
        ArrayList<Supplier> suppliers = SupplierDAO.querySupplierBySearch(s, shopId);


        if (inventories != null){
            inventoriesSize = inventories.size();
        }

        if (customers != null){
            customersSize = customers.size();
        }

        if (suppliers != null){
            supplierSize = suppliers.size();
        }

        if (inventories != null && inventories.size() > 0){
            subThreadArrayList.add(new GlobalSearchResultItem(GlobalSearchResultItem.GlobalSearchResultType.SEARCH_RESULT_SECTION
                    , getResources().getString(R.string.global_search_product)));

            if (inventories.size() > 3){
                for (int i = 0 ; i < 3; i ++){
                    subThreadArrayList.add(new GlobalSearchResultItem(GlobalSearchResultItem.GlobalSearchResultType.SEARCH_RESULT_INVENTORY
                            , inventories.get(i)));
                }
                subThreadArrayList.add(new GlobalSearchResultItem(GlobalSearchResultItem.GlobalSearchResultType.SEARCH_RESULT_MORE
                        , getResources().getString(R.string.global_search_product_more)));
            }else {
                for (Inventory inventory: inventories){
                    subThreadArrayList.add(new GlobalSearchResultItem(GlobalSearchResultItem.GlobalSearchResultType.SEARCH_RESULT_INVENTORY
                            , inventory));
                }
            }
        }

        if (customers != null && customers.size() > 0){
            subThreadArrayList.add(new GlobalSearchResultItem(GlobalSearchResultItem.GlobalSearchResultType.SEARCH_RESULT_SECTION
                    , getResources().getString(R.string.global_search_customer)));

            if (customers.size() > 3){
                for (int i = 0 ; i < 3; i ++){
                    subThreadArrayList.add(new GlobalSearchResultItem(GlobalSearchResultItem.GlobalSearchResultType.SEARCH_RESULT_CUSTOMER
                            , customers.get(i)));
                }
                subThreadArrayList.add(new GlobalSearchResultItem(GlobalSearchResultItem.GlobalSearchResultType.SEARCH_RESULT_MORE
                        , getResources().getString(R.string.global_search_customer_more)));
            }else {
                for (Customer customer: customers){
                    subThreadArrayList.add(new GlobalSearchResultItem(GlobalSearchResultItem.GlobalSearchResultType.SEARCH_RESULT_CUSTOMER
                            , customer));
                }
            }

            customersSize = customers.size();

        }

        if (suppliers != null && suppliers.size() > 0){
            subThreadArrayList.add(new GlobalSearchResultItem(GlobalSearchResultItem.GlobalSearchResultType.SEARCH_RESULT_SECTION
                    , getResources().getString(R.string.global_search_supplier)));

            if (suppliers.size() > 3){
                for (int i = 0 ; i < 3; i ++){
                    subThreadArrayList.add(new GlobalSearchResultItem(GlobalSearchResultItem.GlobalSearchResultType.SEARCH_RESULT_VENDOR
                            , suppliers.get(i)));
                }
                subThreadArrayList.add(new GlobalSearchResultItem(GlobalSearchResultItem.GlobalSearchResultType.SEARCH_RESULT_MORE
                        , getResources().getString(R.string.global_search_supplier_more)));
            }else {
                for (Supplier supplier: suppliers){
                    subThreadArrayList.add(new GlobalSearchResultItem(GlobalSearchResultItem.GlobalSearchResultType.SEARCH_RESULT_VENDOR
                            , supplier));
                }
            }

            supplierSize = suppliers.size();
        }

        return subThreadArrayList;
    }
}
