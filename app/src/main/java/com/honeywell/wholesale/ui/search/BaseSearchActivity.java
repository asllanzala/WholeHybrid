package com.honeywell.wholesale.ui.search;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.honeywell.hybridapp.framework.log.LogHelper;
import com.honeywell.wholesale.R;
import com.honeywell.wholesale.framework.database.CartDAO;
import com.honeywell.wholesale.framework.database.CustomerDAO;
import com.honeywell.wholesale.framework.database.IncrementalDAO;
import com.honeywell.wholesale.framework.database.InventoryDAO;
import com.honeywell.wholesale.framework.database.OrderDAO;
import com.honeywell.wholesale.framework.database.SupplierDAO;
import com.honeywell.wholesale.framework.event.PayRIghtNowEvent;
import com.honeywell.wholesale.framework.http.NativeJsonResponseListener;
import com.honeywell.wholesale.framework.http.WebClient;
import com.honeywell.wholesale.framework.model.AccountManager;
import com.honeywell.wholesale.framework.model.CartItem;
import com.honeywell.wholesale.framework.model.Customer;
import com.honeywell.wholesale.framework.model.Inventory;
import com.honeywell.wholesale.framework.model.Supplier;
import com.honeywell.wholesale.framework.search.ApiSearch;
import com.honeywell.wholesale.framework.search.SearchManager;
import com.honeywell.wholesale.ui.base.BaseActivity;
import com.honeywell.wholesale.framework.search.SearchResultItem.ResultType;
import com.honeywell.wholesale.ui.inventory.ProductShipmentActivity;
import com.honeywell.wholesale.ui.login.module.ListCustomerRequest;
import com.honeywell.wholesale.ui.login.module.ListSupplierRequest;
import com.honeywell.wholesale.ui.menu.customer.CustomerDetailActivity;
import com.honeywell.wholesale.ui.menu.supplier.SupplierDetailActivity;
import com.honeywell.wholesale.ui.scan.ProductDetailActivity;
import com.honeywell.wholesale.ui.transaction.preorders.PreTransactionActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import de.greenrobot.event.EventBus;

/**
 * Created by xiaofei on 9/2/16.
 */
public class BaseSearchActivity extends BaseActivity {
    private static final String TAG = "BaseSearchActivity";

    private ImageView backImageView;
    private ImageView searchCancelImageView;
    private EditText editText;
    private ListView resultListView;
    private TextView resultCountTextView;

    // select search scale
    private View searchSelectView;
    private TextView searchSelectTextView;
    private PopupWindow selectionPopup;

    public static final String SEARCH_KEY = "Search_Key";
    public static final String SEARCH_MODE = "SEARCH_MODE";
    public static final String SEARCH_SELECT = "SEARCH_SELECT";
    public static final String SEARCH_DATA = "SEARCH_DATA";

    private static final String INTENT_VALUE_INVENTORY = "INTENT_VALUE_INVENTORY";
    private static final String INTENT_VALUE_CARTITEM = "INTENT_VALUE_CARTITEM";
    private static final String INTENT_VALUE_CART_CUSTOMER_ID = "INTENT_VALUE_CART_CUSTOMER_ID";


    private ResultType type;
    private String searchData;

    private String searchMode = "";
    private String currentCustomerID;
    private String currentCustomerName;
    private String judgeNewTransactionProcess = "";

    private SearchListAdapter searchListAdapter;
    private ArrayList arrayList = new ArrayList<>();

    private SearchManager searchManager = SearchManager.getInstance();

    private WebClient webClient;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        setContentView(R.layout.activity_base_search);
        initialData();
        initialView();
        localSearch(editText);
    }

    @Override
    protected void onResume() {
        super.onResume();
//        if (type == ResultType.CUSTOMER){
//            syncCustomerData();
//        }
//
//        if (type == ResultType.INVENTORY){
//        }
//
//        if (type == ResultType.TRANSACTION){
//        }
//
//        if (type == ResultType.VENDOR){
//            syncVendorData();
//        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);//反注册EventBus
    }

    public void onEventMainThread(PayRIghtNowEvent event) {
        String msg = "BaseSearchActivity onEventMainThread收到了消息：" + event.getmMsg();
        Log.e(TAG, msg);
        if (event.getmMsg().equals(ProductShipmentActivity.INTENT_KEY_PAY_RIGHT_NOW_EVENT)) {
            finish();
        }
    }

    private void initialData() {
        webClient = new WebClient();
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (!intent.equals(null)) {
            if (!intent.hasExtra(PreTransactionActivity.SEARCH_FOR_TRANSACTION_ORDER)) {
                judgeNewTransactionProcess = "";
            }else {
                judgeNewTransactionProcess = intent.getStringExtra(PreTransactionActivity.SEARCH_FOR_TRANSACTION_ORDER);
            }
        }
        currentCustomerID = intent.getStringExtra(INTENT_VALUE_CART_CUSTOMER_ID);

        if (bundle != null) {
            type = (ResultType) intent.getExtras().getSerializable(SEARCH_KEY);
        } else {
            type = ResultType.INVENTORY;
        }

        if (bundle != null) {
            searchData = intent.getExtras().getString(SEARCH_DATA, "");
        }

        if (bundle != null) {
            searchMode = intent.getExtras().getString(SEARCH_MODE, "");
        }
        if (type == ResultType.CUSTOMER) {
            syncCustomerData();
        }

        if (type == ResultType.INVENTORY) {
        }

        if (type == ResultType.TRANSACTION) {
        }

        if (type == ResultType.VENDOR) {
            syncVendorData();
        }

    }

    private void initialView() {
        backImageView = (ImageView) findViewById(R.id.icon_back);
        backImageView.setOnClickListener(onClickListener);
        resultCountTextView = (TextView) findViewById(R.id.base_search_result_count);
        searchCancelImageView = (ImageView) findViewById(R.id.base_search_cancel);

        editText = (EditText) findViewById(R.id.base_search_edittext);

        searchSelectView = findViewById(R.id.base_search_select_layout);
        searchSelectTextView = (TextView) findViewById(R.id.search_select_textView);

        // set default selection
        searchSelectTextView.setText(R.string.search_popup_inventory);
        //searchSelectTextView.setOnClickListener(onClickListener);

        if (searchMode.equals(SEARCH_SELECT)) {
            searchSelectView.setVisibility(View.VISIBLE);
            //searchSelectTextView.setOnClickListener(onClickListener);
        } else {
            searchSelectView.setVisibility(View.GONE);
            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) searchCancelImageView.getLayoutParams();
            int endMargin = lp.getMarginEnd();
            lp.setMarginEnd(endMargin / 10);
        }


        String count = String.format(getResources().getString(R.string.search_result_count), String.valueOf(arrayList.size()));
        resultCountTextView.setText(count);
        String hintString = "";
        if (type == ResultType.CUSTOMER) {
            hintString = getString(R.string.search_customer_hint);
            searchSelectTextView.setText(R.string.search_popup_customer);
        }

        if (type == ResultType.INVENTORY) {
            hintString = getString(R.string.search_inventory_hint);
            searchSelectTextView.setText(R.string.search_popup_inventory);
        }

        if (type == ResultType.TRANSACTION) {
            hintString = getString(R.string.search_transaction_hint);
            searchSelectTextView.setText(R.string.search_popup_transaction);
        }

        if (type == ResultType.VENDOR) {
            hintString = getString(R.string.search_vendor_hint);
            searchSelectTextView.setText(R.string.search_popup_vendor);
        }

        if (searchData == null) {
            searchData = "";
            editText.setHint(hintString);
        } else {
            editText.setText(searchData);
        }
        arrayList = initialSearch(searchData);

        searchListAdapter = new SearchListAdapter(arrayList, this, type);

        resultListView = (ListView) findViewById(R.id.base_search_result_list);
        resultListView.setAdapter(searchListAdapter);
        resultListView.setOnItemClickListener(onItemClickListener);
        searchCancelImageView.setOnClickListener(onClickListener);
    }

    private ArrayList initialSearch(String s) {
        // search in local db
        if (type == ResultType.CUSTOMER) {
            arrayList = CustomerDAO.queryCustomerBySearch(s, AccountManager.getInstance().getCurrentAccount().getCurrentShopId());
        }

        if (type == ResultType.INVENTORY) {
//            String start = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
//            LogHelper.getInstance().e(TAG, "start time" + start);
            arrayList = InventoryDAO.queryInventoryBySearch(s, AccountManager.getInstance().getCurrentShopId());
//            String end = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
//            LogHelper.getInstance().e(TAG, "end time" + end);
        }

        if (type == ResultType.TRANSACTION) {
            try {
                arrayList = OrderDAO.queryOrderBySearch(s, AccountManager.getInstance().getCurrentAccount().getCurrentShopId());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        if (type == ResultType.VENDOR) {
            arrayList = SupplierDAO.querySupplierBySearch(s, AccountManager.getInstance().getCurrentAccount().getCurrentShopId());
        }

        return arrayList;
    }

    private void initPopupWindow() {
        final View popupWindowView = LayoutInflater.from(this).inflate(R.layout.popup_search_select_view, null, false);
        final ListView popupListView = (ListView) popupWindowView.findViewById(R.id.search_select_listView);

        ArrayList<String> selectionArrayList = initialSearchSelectList();

        SearchSelectAdapter searchSelectAdapter = new SearchSelectAdapter(selectionArrayList, this);
        popupListView.setAdapter(searchSelectAdapter);
        popupListView.setOnItemClickListener(selectItemClickListener);

        selectionPopup = new PopupWindow(popupWindowView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);

        popupWindowView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (popupWindowView.isShown()) {
                    selectionPopup.dismiss();
                    selectionPopup = null;
                }
                return false;
            }
        });
        selectionPopup.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.white)));
    }

    private ArrayList<String> initialSearchSelectList() {
        ArrayList<String> arrayList = new ArrayList<>();

        arrayList.add(getString(R.string.search_popup_inventory));
        arrayList.add(getString(R.string.search_popup_customer));
        arrayList.add(getString(R.string.search_popup_vendor));
        return arrayList;
    }

    private void localSearch(View view) {
        searchManager.setApiSearch(new ApiSearch() {
            @Override
            public void onSearchResultListener(ArrayList al) {
                searchListAdapter.setDataSource(al, type);
                searchListAdapter.notifyDataSetChanged();

                String count = String.format(getResources().getString(R.string.search_result_count), String.valueOf(al.size()));
                resultCountTextView.setText(count);
            }

            @Override
            public ArrayList queryFromLocal(String s) {
                return initialSearch(s);
            }
        });
        searchManager.searchInLocal(view);
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.base_search_cancel) {
                editText.setText("");
            }

            if (v.getId() == R.id.icon_back) {
                finish();
            }

            if (v.getId() == R.id.search_select_textView) {
                initPopupWindow();
                int left = Math.round(v.getX()) + v.getWidth() / 2;
                int top = v.getTop() + 10;
                selectionPopup.showAsDropDown(v, left, top);
            }
        }
    };

    private AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (type == ResultType.CUSTOMER) {
                LogHelper.getInstance().e(TAG, "click custome type");

                Customer customer = (Customer) arrayList.get(position);
                Intent intent = new Intent(BaseSearchActivity.this, CustomerDetailActivity.class);
                // pass  params
                intent.putExtra(CustomerDetailActivity.INTENT_KEY_CUSTOMER_INFO, customer.getJsonString());
                startActivity(intent);
            }

            if (type == ResultType.INVENTORY) {

                if (currentCustomerID == null) {
                    LogHelper.getInstance().e(TAG, "click inventory type");
                    Inventory inventory = (Inventory) arrayList.get(position);

                    String barcode = inventory.getProductCode();
                    int productId = inventory.getProductId();
                    String stockQuantity = inventory.getQuantity();
                    if (judgeNewTransactionProcess.equals(PreTransactionActivity.SEARCH_FOR_TRANSACTION_ORDER)) {
                        if (Integer.valueOf(stockQuantity) < 1) {
                            Toast.makeText(getApplicationContext(), "该商品库存不足", Toast.LENGTH_SHORT).show();
                        } else {
                            Intent intent = new Intent();
                            intent.putExtra(PreTransactionActivity.INTENT_KEY_PRODUCT_ID, productId);
                            setResult(PreTransactionActivity.RESULT_PRODUCT_SUCCEED_CODE, intent);
                            finish();
                        }
                    } else {
                        Intent intent = new Intent(BaseSearchActivity.this, ProductDetailActivity.class);
                        intent.putExtra(ProductDetailActivity.INTENT_KEY_SCAN_RESULT, barcode);
                        intent.putExtra(ProductDetailActivity.INTENT_KEY_PRODUCT_ID, productId);
                        startActivity(intent);
                    }
                }

                if (currentCustomerID != null) {
                    Inventory inventory = (Inventory) arrayList.get(position);

                    String employeeId = AccountManager.getInstance().getCurrentAccount().getEmployeeId();
                    String userName = AccountManager.getInstance().getCurrentAccount().getUserName();
                    String shopId = AccountManager.getInstance().getCurrentShopId();
                    String shopName = AccountManager.getInstance().getCurrentAccount().getShopName();
                    String customerId = currentCustomerID;

                    ArrayList<CartItem> cartItemList = CartDAO.getAllCartItemsByCustomer(employeeId, shopId, customerId);
                    String customerName = cartItemList.get(0).getCustomerName();
                    String contactName = cartItemList.get(0).getContactName();
                    String contactPhone = cartItemList.get(0).getContactPhone();
                    String invoiceTitle = cartItemList.get(0).getInvoiceTitle();
                    String address = cartItemList.get(0).getAddress();
                    int productId = inventory.getProductId();
                    String productName = inventory.getProductName();
                    String unitPrice = inventory.getStandardPrice();
                    String imageUrl = inventory.getPicSrc();
                    String productCode = inventory.getProductCode();
                    String productNumber = inventory.getProductNumber();
                    String stockQuantity = inventory.getQuantity();
                    //TODO unit没有数据啊
                    String unit = "没有数据啊";
                    CartItem cartItem = new CartItem(employeeId, userName, shopId, shopName, customerId, customerName, contactName,
                            contactPhone, invoiceTitle, address, productId, productName, unitPrice, imageUrl, unit, productCode, productNumber, stockQuantity);

                    String cartItemJson = new Gson().toJson(cartItem);
                    Intent intent = new Intent(BaseSearchActivity.this, ProductShipmentActivity.class);
                    intent.putExtra(INTENT_VALUE_CARTITEM, cartItemJson);
                    intent.putExtra(INTENT_VALUE_CART_CUSTOMER_ID, currentCustomerID);
                    startActivity(intent);
                }
            }

            if (type == ResultType.TRANSACTION) {
                LogHelper.getInstance().e(TAG, "click transaction type");

            }

            if (type == ResultType.VENDOR) {
                LogHelper.getInstance().e(TAG, "click supplier type");
                Supplier supplier = (Supplier) arrayList.get(position);

                Intent intent = new Intent(BaseSearchActivity.this, SupplierDetailActivity.class);
                // pass  params
                intent.putExtra(SupplierDetailActivity.INTENT_KEY_VENDOR_INFO, supplier.getJsonString());
                startActivity(intent);

            }
        }
    };

    private AdapterView.OnItemClickListener selectItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (position == 0) {
                type = ResultType.INVENTORY;
            }

            if (position == 1) {
                type = ResultType.CUSTOMER;
            }

            if (position == 2) {
                type = ResultType.VENDOR;
            }

            String hintString = "";
            if (type == ResultType.CUSTOMER) {
                hintString = getString(R.string.search_customer_hint);
                searchSelectTextView.setText(R.string.search_popup_customer);
            }

            if (type == ResultType.INVENTORY) {
                hintString = getString(R.string.search_inventory_hint);
                searchSelectTextView.setText(R.string.search_popup_inventory);
            }

            if (type == ResultType.TRANSACTION) {
                hintString = getString(R.string.search_transaction_hint);
                searchSelectTextView.setText(R.string.search_popup_transaction);
            }

            if (type == ResultType.VENDOR) {
                hintString = getString(R.string.search_vendor_hint);
                searchSelectTextView.setText(R.string.search_popup_vendor);
            }

            editText.setText("");
            editText.setHint(hintString);
            initialSearch("");

            if (selectionPopup.isShowing()) {
                selectionPopup.dismiss();
                selectionPopup = null;
            }

        }
    };

    public void syncVendorData() {
        String shopId = AccountManager.getInstance().getCurrentShopId();
        String vendorLastRequestTime = IncrementalDAO.queryIncrementalItem(Supplier.class, shopId);
        ListSupplierRequest listSupplierRequest = new ListSupplierRequest(shopId, vendorLastRequestTime);
        webClient.httpGetSupplier(listSupplierRequest, new NativeJsonResponseListener<JSONObject>() {
            @Override
            public void listener(JSONObject jsonObject) {
                ArrayList arrayList = initialSearch("");
                searchListAdapter.setDataSource(arrayList, type);
                searchListAdapter.notifyDataSetChanged();
                LogHelper.getInstance().e(TAG, "同步供应商成功");
            }

            @Override
            public void errorListener(String s) {
                LogHelper.getInstance().e(TAG, "同步供应商失败");
            }
        });
    }

    public void syncCustomerData() {

        String shopId = AccountManager.getInstance().getCurrentShopId();
        String customerLastRequestTime = IncrementalDAO.queryIncrementalItem(Customer.class, shopId);
        ListCustomerRequest listCustomerRequest = new ListCustomerRequest(shopId, customerLastRequestTime);
        webClient.httpGetCustomer(listCustomerRequest, new NativeJsonResponseListener<JSONObject>() {
            @Override
            public void listener(JSONObject jsonObject) {
                ArrayList arrayList = initialSearch("");
                searchListAdapter.setDataSource(arrayList, type);
                searchListAdapter.notifyDataSetChanged();
                LogHelper.getInstance().e(TAG, "同步用户成功");
            }

            @Override
            public void errorListener(String s) {
                LogHelper.getInstance().e(TAG, "同步用户失败");
            }
        });
    }

}
