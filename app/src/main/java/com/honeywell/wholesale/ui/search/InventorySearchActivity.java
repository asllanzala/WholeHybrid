package com.honeywell.wholesale.ui.search;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.google.gson.Gson;
import com.honeywell.hybridapp.framework.log.LogHelper;
import com.honeywell.wholesale.R;
import com.honeywell.wholesale.framework.http.NativeJsonResponseListener;
import com.honeywell.wholesale.framework.http.WebClient;
import com.honeywell.wholesale.framework.model.AccountManager;
import com.honeywell.wholesale.framework.model.Inventory;
import com.honeywell.wholesale.framework.model.PreCart;
import com.honeywell.wholesale.framework.model.WareHouse;
import com.honeywell.wholesale.framework.model.WareHouseManager;
import com.honeywell.wholesale.framework.scanner.ScanerRespManager;
import com.honeywell.wholesale.framework.search.SearchManager;
import com.honeywell.wholesale.framework.search.SearchResultItem;
import com.honeywell.wholesale.framework.utils.PopupWindowUtil;
import com.honeywell.wholesale.ui.base.BaseActivity;
import com.honeywell.wholesale.ui.base.BaseTextView;
import com.honeywell.wholesale.ui.menu.setting.warehouse.request.WareHouseListRequest;
import com.honeywell.wholesale.ui.scan.ProductDetailActivity;
import com.honeywell.wholesale.ui.transaction.preorders.Adapter.PreSearchListAdapter;
import com.honeywell.wholesale.ui.transaction.preorders.CartModel;
import com.honeywell.wholesale.ui.transaction.preorders.network.PreOrderSearchRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;


/**
 * Created by H154326 on 17/5/3.
 * Email: yang.liu6@honeywell.com
 */

public class InventorySearchActivity extends BaseActivity {
    private static final String TAG = InventorySearchActivity.class.getSimpleName();

    private ArrayList<PreCart> preCartList;//本页面暂存的数据结构

    private ImageView backImageView;
    private ImageView searchCancelImageView;
    private EditText editText;
    private ListView resultListView;

    private BaseTextView resultCountTextView;
    private BaseTextView searchSelectTextView;
    private BaseTextView wareHouseTextView;

    private Context context = this;
    private ArrayList<WareHouse> wareHouseArrayList;

    // select search scale
    private View searchSelectView;

    public static final String SEARCH_KEY = "Search_Key";
    public static final String SEARCH_MODE = "SEARCH_MODE";
    public static final String SEARCH_SELECT = "SEARCH_SELECT";
    public static final String SEARCH_DATA = "SEARCH_DATA";

    private SearchResultItem.ResultType type;

    private String searchData;
    private String searchMode = "";

    private RelativeLayout selectWareHouseLayout;

    private WareHouse currentWareHouse;

    private WareHouseListRequest wareHouseListRequest;

    private InventorySearchAdapter inventorySearchAdapter;
    private ArrayList arrayList = new ArrayList<>();

    private SearchManager searchManager = SearchManager.getInstance();

    private CartModel cartModel = CartModel.getInstance();

    private GradientDrawable drawable;

    private WebClient webClient;

    private int currentShopId = Integer.parseInt(AccountManager.getInstance().getCurrentShopId());

    private PreOrderSearchRequest preOrderSearchRequest;

    private static String isLoadMore = "false";


    private String pageNumber = "0";
    private LoadMoreScrollListener loadMoreScrollListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory_search);
        initialData();
        initialView();
        //localSearch(editText);
        getWareHouseDataFromServer();

    }

    @Override
    protected void onResume() {
        super.onResume();
        ScanerRespManager.getInstance().setType(ScanerRespManager.ScanerRespType.SCANER_RESP_INVENTORY);
        if (inventorySearchAdapter != null) {
            inventorySearchAdapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e("InventorySearchActivity", "onDestroy");
//        ScanerRespManager.getInstance().setType(ScanerRespManager.ScanerRespType.SCANER_NULL_TYPE);
    }

    private void initialData() {
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        wareHouseListRequest = new WareHouseListRequest(currentShopId);
        webClient = new WebClient();

        if (bundle != null) {
            type = (SearchResultItem.ResultType) intent.getExtras().getSerializable(SEARCH_KEY);
        } else {
            type = SearchResultItem.ResultType.INVENTORY;
        }

        if (bundle != null) {
            searchData = intent.getExtras().getString(SEARCH_DATA, "");
        }

        if (bundle != null) {
            searchMode = intent.getExtras().getString(SEARCH_MODE, "");
        }

        if (type == SearchResultItem.ResultType.INVENTORY) {
        }
    }

    private void initialView() {
        backImageView = (ImageView) findViewById(R.id.pre_icon_back);
        backImageView.setOnClickListener(onClickListener);
        resultCountTextView = (BaseTextView) findViewById(R.id.pre_search_result_count);
        wareHouseTextView = (BaseTextView) findViewById(R.id.pre_order_warehouse_text_view);
        searchCancelImageView = (ImageView) findViewById(R.id.pre_search_cancel);
        searchCancelImageView.bringToFront();
        drawable = new GradientDrawable();

        selectWareHouseLayout = (RelativeLayout) findViewById(R.id.order_warehouse_info_field);
        selectWareHouseLayout.setEnabled(true);
        selectWareHouseLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                menuClick(view);
            }
        });

        editText = (EditText) findViewById(R.id.pre_search_edittext);
        editText.addTextChangedListener(
                new TextWatcher() {
                    @Override public void onTextChanged(CharSequence s, int start, int before, int count) { }
                    @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

                    private Timer timer=new Timer();
                    private final long DELAY = 300; // milliseconds

                    @Override
                    public void afterTextChanged(final Editable s) {
                        if (currentWareHouse != null) {
                            timer.cancel();
                            timer = new Timer();
                            timer.schedule(
                                    new TimerTask() {
                                        @Override
                                        public void run() {
                                            initialSearch(s.toString());
                                        }
                                    },
                                    DELAY
                            );
                        }
                    }
                }
        );
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (currentWareHouse != null) {
                    initialSearch(editable.toString());
                    Log.e("1111","1111");
                }
            }
        });
        searchSelectView = findViewById(R.id.pre_search_select_layout);
        searchSelectTextView = (BaseTextView) findViewById(R.id.pre_search_select_textView);

        // set default selection
        searchSelectTextView.setText(R.string.search_popup_inventory);
        searchSelectTextView.setOnClickListener(onClickListener);

        if (searchMode.equals(SEARCH_SELECT)) {
            searchSelectView.setVisibility(View.VISIBLE);
            searchSelectTextView.setOnClickListener(onClickListener);
        } else {
            searchSelectView.setVisibility(View.GONE);
            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) searchCancelImageView.getLayoutParams();
            int endMargin = lp.getMarginEnd();
            lp.setMarginEnd(endMargin / 10);
        }

        String count = String.format(getResources().getString(R.string.search_result_count), "0");

//        String count = String.format(getResources().getString(R.string.search_result_count), String.valueOf(arrayList.size()));
        resultCountTextView.setText(count);
        String hintString = "";
        if (type == SearchResultItem.ResultType.INVENTORY) {
            hintString = getString(R.string.search_inventory_hint);
            searchSelectTextView.setText(R.string.search_popup_inventory);
        }

        if (searchData == null) {
            searchData = "";
            editText.setHint(hintString);
        } else {
            editText.setText(searchData);
        }
//        arrayList = initialSearch(searchData);

        inventorySearchAdapter = new InventorySearchAdapter(this);
//        inventorySearchAdapter = new PreSearchListAdapter(arrayList, this);

        resultListView = (ListView) findViewById(R.id.pre_search_result_list);
        resultListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                LogHelper.getInstance().e(TAG, "click inventory type");
                Inventory inventory = (Inventory) arrayList.get(position);

                String barcode = inventory.getProductCode();
                int productId = inventory.getProductId();
                String stockQuantity = inventory.getQuantity();

                Intent intent = new Intent(InventorySearchActivity.this, ProductDetailActivity.class);
                intent.putExtra(ProductDetailActivity.INTENT_KEY_SCAN_RESULT, barcode);
                intent.putExtra(ProductDetailActivity.INTENT_KEY_PRODUCT_ID, productId);
                intent.putExtra("warehouse_id", currentWareHouse.getWareHouseId());
                intent.putExtra("warehouse_name", currentWareHouse.getWareHouseName());
                startActivity(intent);
            }
        });
        resultListView.setAdapter(inventorySearchAdapter);
//        initialSearch(searchData);
        searchCancelImageView.setOnClickListener(onClickListener);

        loadMoreScrollListener = new LoadMoreScrollListener();
        resultListView.setOnScrollListener(loadMoreScrollListener);
        loadMoreScrollListener.setLoadMore(new LoadMoreScrollListener.LoadMore() {
            @Override
            public void loadMoreData() {
                if (isLoadMore.equals("true")) {
                    getDataFromServer(editText.getText().toString(),true);
                }
            }
        });

    }

    private void initialSearch(String s) {
        pageNumber = "0";
        getDataFromServer(s,false);
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.pre_search_cancel) {
                editText.setText("");
            }

            if (v.getId() == R.id.pre_icon_back) {
                finish();
            }

        }
    };

    private void getDataFromServer(String s, final boolean isCallFormLoadMore) {
        preOrderSearchRequest = new PreOrderSearchRequest(currentWareHouse.getWareHouseId(), s, pageNumber);
        Log.e(TAG,s);
        webClient.httpGetInventories(preOrderSearchRequest, new NativeJsonResponseListener<JSONObject>() {
            @Override
            public void listener(JSONObject jsonObject) {
                if (jsonObject != null) {
                    final Gson gson = new Gson();
//                    arrayList = new ArrayList();

                    ArrayList arrayList1 = new ArrayList();

                    String total = jsonObject.optString("total");

                    String nextPage = jsonObject.optString("next_page");
                    isLoadMore = nextPage;
                    if (isLoadMore.equals("true")) {
                        pageNumber = String.valueOf(Integer.valueOf(pageNumber) + 1);
                    }

                    JSONArray jsonArray = jsonObject.optJSONArray("product_list");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject inventoryItem = jsonArray.optJSONObject(i);
                        Inventory invetory = gson.fromJson(inventoryItem.toString(), Inventory.class);
                        arrayList1.add(invetory);
                    }

                    if (isCallFormLoadMore) {
                        arrayList.addAll(arrayList1);
                    } else {
                        arrayList = arrayList1;
                    }


                    inventorySearchAdapter.setDataSource(arrayList);
                    inventorySearchAdapter.notifyDataSetChanged();

                    LoadMoreScrollListener.setIsLoading(false);
//                    String count = String.format(getResources().getString(R.string.search_result_count), String.valueOf(arrayList.size()));

                    String count = String.format(getResources().getString(R.string.search_result_count), total);

                    resultCountTextView.setText(count);
                }
            }

            @Override
            public void errorListener(String s) {

            }
        });
    }

    private void getWareHouseDataFromServer() {
        webClient.httpListWareHouse(wareHouseListRequest, new NativeJsonResponseListener<JSONObject>() {
            @Override
            public void listener(JSONObject jsonObject) {
                int defaultWareHouseId = -1;
                try {
                    defaultWareHouseId = jsonObject.getInt("default_warehouse_id");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                String defaultWareHouseName = "";
                try {
                    defaultWareHouseName = jsonObject.getString("default_warehouse_name");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                JSONArray jsonArray = jsonObject.optJSONArray("warehouses");
                Gson gson = new Gson();
                wareHouseArrayList = new ArrayList<WareHouse>();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject1 = jsonArray.optJSONObject(i);
                    if (jsonObject1 != null) {
                        WareHouse wareHouse = gson.fromJson(jsonObject1.toString(), WareHouse.class);
                        if (wareHouse.getWareHouseId() == defaultWareHouseId) {
                            wareHouse.setDefault(true);
                            currentWareHouse = wareHouse;
                        }
                        wareHouseArrayList.add(wareHouse);
                    }
                }
                WareHouseManager.getInstance().setScanWareHouseId(currentWareHouse.getWareHouseId());
                WareHouseManager.getInstance().setScanWareHouseName(currentWareHouse.getWareHouseName());
                wareHouseTextView.setText(currentWareHouse.getWareHouseName());
                resultListView.setAdapter(inventorySearchAdapter);
                initialSearch(editText.getText().toString());
            }

            @Override
            public void errorListener(String s) {
                LogHelper.getInstance().e(TAG, s);
            }
        });
    }

    public void menuClick(View view) {
        final PopupWindowUtil popupWindow = new PopupWindowUtil(context, wareHouseArrayList);
        popupWindow.notifyData();
        int currentPosition = getCurrentWareHousePosition();
        if (currentPosition != -1){
            popupWindow.setListViewLocation(currentPosition);
        }
        popupWindow.setItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                popupWindow.clearAllDeafault();
                wareHouseArrayList.get(position).setDefault(true);
                currentWareHouse = wareHouseArrayList.get(position);
                popupWindow.dismiss();
                setWareHouseTextView(wareHouseArrayList.get(position));
                initialSearch(editText.getText().toString());
            }
        });
        //根据后面的数字 手动调节窗口的宽度
        popupWindow.show(view, 4);
    }

    private int getCurrentWareHousePosition(){
        if (wareHouseArrayList == null){
            return -1;
        }else {
            for (int i = 0; i < wareHouseArrayList.size(); i++) {
                if (wareHouseArrayList.get(i).isDefault()) {
                    return i;
                }
            }
            return -1;
        }
    }
    private void setWareHouseTextView(WareHouse wareHouse) {
        wareHouseTextView.setText(wareHouse.getWareHouseName());
    }

}
