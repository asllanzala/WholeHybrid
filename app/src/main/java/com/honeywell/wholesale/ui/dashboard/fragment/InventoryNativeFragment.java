package com.honeywell.wholesale.ui.dashboard.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.android.volley.toolbox.ImageLoader;
import com.google.gson.Gson;
import com.honeywell.hybridapp.framework.log.LogHelper;
import com.honeywell.wholesale.R;
import com.honeywell.wholesale.framework.http.NativeJsonResponseListener;
import com.honeywell.wholesale.framework.http.WebClient;
import com.honeywell.wholesale.framework.model.AccountManager;
import com.honeywell.wholesale.framework.model.Category;
import com.honeywell.wholesale.framework.model.Inventory;
import com.honeywell.wholesale.framework.model.WareHouse;
import com.honeywell.wholesale.framework.model.WareHouseManager;
import com.honeywell.wholesale.framework.scanner.ScanerRespManager;
import com.honeywell.wholesale.framework.search.SearchManager;
import com.honeywell.wholesale.framework.utils.PopupWindowUtil;
import com.honeywell.wholesale.lib.util.StringUtil;
import com.honeywell.wholesale.ui.base.BaseRootFragment;
import com.honeywell.wholesale.ui.base.BaseTextView;
import com.honeywell.wholesale.ui.category.AddCategoryActivity;
import com.honeywell.wholesale.ui.dashboard.adapter.InventoryAdapter;
import com.honeywell.wholesale.ui.login.module.UpdateCategoryCloudApiRequest;
import com.honeywell.wholesale.ui.menu.setting.warehouse.WareHouseManagerActivity;
import com.honeywell.wholesale.ui.inventory.AddProductActivity;
import com.honeywell.wholesale.ui.login.module.UpdateInventoryCloudApiRequest;
import com.honeywell.wholesale.ui.menu.setting.warehouse.request.WareHouseListRequest;
import com.honeywell.wholesale.ui.menu.shop.ShopManagementActivity;
import com.honeywell.wholesale.ui.scan.ProductDetailActivity;
import com.honeywell.wholesale.ui.search.BaseSearchActivity;
import com.honeywell.wholesale.ui.search.InventorySearchActivity;
import com.honeywell.wholesale.ui.search.InventorySearchAdapter;
import com.honeywell.wholesale.ui.search.LoadMoreScrollListener;
import com.honeywell.wholesale.ui.transaction.preorders.network.PreOrderSearchRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static android.view.View.GONE;

/**
 * Created by xiaofei on 7/11/16.
 */

public class InventoryNativeFragment extends BaseRootFragment {

    public static final String TAG = InventoryNativeFragment.class.getSimpleName();
    // define all category key is 0
    private static final String CATEGORY_ALL = "0";
    private static final String CATEGORY_ADD = "-1";

    private ListView categoryListView;
    private ListView inventoryListView;

    private ArrayList<Category> categoryArrayList = new ArrayList<>();
    private ArrayList<Inventory> inventoryArrayList = new ArrayList<>();
    private ArrayList<Inventory> inventorySubList = new ArrayList<>();
    private ArrayList<String> sortArrayList;

    private ArrayList arrayList = new ArrayList<>();

    private ArrayList<WareHouse> wareHouseArrayList;

    private CategoryAdapter categoryAdapter;
    private InventoryAdapter inventoryAdapter;

    private ImageLoader imageLoader;

    private BaseTextView inventoryWarehouseTextView;

    private InventorySearchAdapter inventorySearchAdapter;

    private ImageView havingStockImageView;

    private int currentShopId = Integer.parseInt(AccountManager.getInstance().getCurrentShopId());

    private RelativeLayout sortLayout;
    private RelativeLayout havingStockLayout;
    private RelativeLayout warehouseSelectLayout;

    private Context mContext;

    private static boolean isHavingStockChecked = false;

    private int mCategorySelectedPosition = 0;

    private WareHouseListRequest wareHouseListRequest;

    private WareHouse currentWareHouse;

    private WebClient webClient;

    private PreOrderSearchRequest preOrderSearchRequest;

    private static String isLoadMore = "false";

    private String pageNumber = "0";

    private LoadMoreScrollListener loadMoreScrollListener;

    private String currentEmployeeId = AccountManager.getInstance().getCurrentAccount().getEmployeeId();

    private String stockStatus = "0";

    private int categoryId = -1;

    public InventoryNativeFragment() {

    }

    @Override
    public int getLayout() {
        return R.layout.fragment_inventory_native;
    }

    @Override
    public int getIndex() {
        return 0;
    }

    @Override
    public void initImageMore(ImageView view) {
        super.initImageMore(view);
        view.setImageResource(R.drawable.dashboard_title_plus);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), AddProductActivity.class));
            }
        });
    }

    @Override
    public void initLayoutSearch(View view) {
        super.initLayoutSearch(view);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), InventorySearchActivity.class);
                Bundle mBundle = new Bundle();
                mBundle.putString(BaseSearchActivity.SEARCH_MODE, InventorySearchActivity.SEARCH_SELECT);
                intent.putExtras(mBundle);
                startActivity(intent);
            }
        });
    }

    @Override
    public void initView(View view) {
        super.initView(view);
        Log.e(TAG, "InventoryNativeFragment view created");
        imageLoader = WebClient.getImageLoader();
        inventorySearchAdapter = new InventorySearchAdapter(getContext());
        categoryListView = (ListView) view.findViewById(R.id.category_listView);
        inventoryListView = (ListView) view.findViewById(R.id.inventory_listView);

        havingStockImageView = (ImageView) view.findViewById(R.id.inventory_choose_imageview);
        havingStockImageView.setSelected(false);
        isHavingStockChecked = false;

        inventoryWarehouseTextView = (BaseTextView) view.findViewById(R.id.inventory_warehouse_text_view);

        categoryListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        sortLayout = (RelativeLayout) view.findViewById(R.id.inventory_sort_layout);
        sortLayout.setVisibility(GONE);
        sortLayout.setEnabled(false);

        havingStockLayout = (RelativeLayout) view.findViewById(R.id.inventory_have_stock_layout);
        havingStockLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                havingStockLayout.setEnabled(false);
                inventoryListView.setEnabled(false);
                categoryListView.setEnabled(false);
                if (havingStockImageView.isSelected()) {
                    havingStockImageView.setSelected(false);
                    havingStockImageView.setImageResource(R.drawable.inventory_unselected);
                    categoryListView.setItemChecked(mCategorySelectedPosition, true);
                    categoryAdapter.notifyDataSetChanged();
                    isHavingStockChecked = false;
                    getAllStock();


                } else {
                    havingStockImageView.setSelected(true);
                    havingStockImageView.setImageResource(R.drawable.inventory_selected);
                    categoryListView.setItemChecked(mCategorySelectedPosition, true);
                    categoryAdapter.notifyDataSetChanged();
                    isHavingStockChecked = true;
                    havingStock();

                }
            }
        });

        warehouseSelectLayout = (RelativeLayout) view.findViewById(R.id.warehouse_select_layout);
        warehouseSelectLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                menuClick(view);
            }
        });

        loadMoreScrollListener = new LoadMoreScrollListener();
        inventoryListView.setOnScrollListener(loadMoreScrollListener);
        loadMoreScrollListener.setLoadMore(new LoadMoreScrollListener.LoadMore() {
            @Override
            public void loadMoreData() {
                if (isLoadMore.equals("true")) {
                    getDataFromServer(true);
                }
            }
        });
        mContext = getActivity();
        initData();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.e(TAG, "onAttach");
    }

    @Override
    public String getTitle() {
        Log.e(TAG, "getTitle");
        return StringUtil.getString(R.string.dashboard_fragment_title_tab_inventory);

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void reLoadData() {
//        pageNumber = "0";
//        if (isHavingStockChecked) {
//            getWareHouseDataFromServer();
//            Log.e(TAG, "reLoadDatahavingStock");
//        } else {
//            getWareHouseDataFromServer();
//            Log.e(TAG, "reLoadData");
//        }
    }

    @Override
    public void onResume() {
        super.onResume();
        ScanerRespManager.getInstance().setType(ScanerRespManager.ScanerRespType.SCANER_RESP_INVENTORY);
        currentShopId = Integer.parseInt(AccountManager.getInstance().getCurrentShopId());
        pageNumber = "0";
        int id = -1;

        if (currentWareHouse != null){
            id = currentWareHouse.getWareHouseId();
        }

        if (!isHavingStockChecked) {
            getCategoryByStockStatusFromServer("1", id);
        } else {
            getCategoryByStockStatusFromServer("100", id);
        }
        getWareHouseDataFromServer();
        Log.e(TAG, "onresume");


//
//        int id = -1;
//
//        if (currentWareHouse != null){
//            id = currentWareHouse.getWareHouseId();
//        }
//
//        if (isHavingStockChecked) {
//            String shopId = AccountManager.getInstance().getCurrentAccount().getCurrentShopId();
//            stockStatus = "0";
//            getDataByCategoryWithStockStatus();
//            getCategoryByStockStatusFromServer("1", -1);
//            Log.e(TAG, "reLoadDatahavingStock");
//        } else {
//            String shopId = AccountManager.getInstance().getCurrentAccount().getCurrentShopId();
//            stockStatus = "100";
//            getDataByCategoryWithStockStatus();
//            getCategoryByStockStatusFromServer("100", id);
//            Log.e(TAG, "havingStock");
//            Log.e(TAG, "reLoadData");
//        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.e(TAG, "onDestroyView");
        ScanerRespManager.getInstance().setType(ScanerRespManager.ScanerRespType.SCANER_NULL_TYPE);
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.e(TAG, "onStop");
    }


    private void initData() {
        if (isAdded()) {
            Log.e(TAG, "刷新产品分类列表");
            webClient = new WebClient();
            ScanerRespManager.getInstance().setType(ScanerRespManager.ScanerRespType.SCANER_RESP_INVENTORY);
            wareHouseListRequest = new WareHouseListRequest(currentShopId);
            ShopManagementActivity.setObserverChangeShop(observerChangeShop);
            WareHouseManagerActivity.setObserverChangeShop(observerChangeShop);
            initAdapter();
            getWareHouseDataFromServer();
            getCategoryByStockStatusFromServer("1", -1);
        } else {
            Log.e(TAG, "没有刷新产品分类列表");
        }

    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.e(TAG, "on detach");
    }

    private void initAdapter() {
        Activity activity = getActivity();
        if (activity != null) {
            inventoryAdapter = new InventoryAdapter(inventoryArrayList, activity);
            inventoryListView.setAdapter(inventoryAdapter);
            categoryAdapter = new CategoryAdapter(categoryArrayList, activity);
            categoryListView.setAdapter(categoryAdapter);
        }
        categoryListView.setItemChecked(0, true);
        categoryListView.setOnItemClickListener(onItemClickListener);
        inventoryListView.setOnItemClickListener(onItemClickListener);

    }

    private void getAllStock() {
        String shopId = AccountManager.getInstance().getCurrentAccount().getCurrentShopId();
        stockStatus = "0";
        categoryId = -1;
        getDataByCategoryWithStockStatus();
        categoryListView.setItemChecked(0, true);
        getCategoryByStockStatusFromServer("1", -1);
//        reLoadCategoryAdapter();
    }

    private void havingStock() {
        String shopId = AccountManager.getInstance().getCurrentAccount().getCurrentShopId();
        stockStatus = "100";
        categoryId = -1;
        getDataByCategoryWithStockStatus();
        categoryListView.setItemChecked(0, true);
        getCategoryByStockStatusFromServer("100", currentWareHouse.getWareHouseId());
        Log.e(TAG, "havingStock");
    }

    private AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (parent.getId() == R.id.category_listView) {
                if (categoryArrayList.get(position).getCategoryId().equals("-1")) {
                    Log.e(TAG, "add category");
                    Intent intent = new Intent(getActivity(), AddCategoryActivity.class);
                    startActivity(intent);
                    return;
                }

                ((ListView) parent).setItemChecked(position, true);

                if (position == 0) {
                    categoryId = -1;
                    getDataByCategoryWithStockStatus();
                    mCategorySelectedPosition = 0;
                    categoryListView.setItemChecked(mCategorySelectedPosition, true);
                    categoryAdapter.notifyDataSetChanged();
                    return;
                }
                Category category = categoryArrayList.get(position);
                mCategorySelectedPosition = position;
                categoryListView.setItemChecked(mCategorySelectedPosition, true);
                inventorySubList = new ArrayList<>();
                categoryId = Integer.valueOf(category.getCategoryId());
                getDataByCategoryWithStockStatus();
                categoryAdapter.notifyDataSetChanged();
            }

            if (parent.getId() == R.id.inventory_listView) {
//                String barcode = requestBodyJsonObject.optString("barcode");
                Inventory inventory = inventoryArrayList.get(position);
                String barcode = inventory.getProductCode();
                int productId = inventory.getProductId();
                String productNumber = inventory.getProductNumber();
                LogHelper.e(TAG, "productNumber" + productNumber);
                //String barid = inventory
                Intent intent = new Intent(getActivity(), ProductDetailActivity.class);
                intent.putExtra(ProductDetailActivity.INTENT_KEY_SCAN_RESULT, barcode);
                intent.putExtra(ProductDetailActivity.INTENT_KEY_PRODUCT_ID, productId);
                intent.putExtra(ProductDetailActivity.INTENT_KEY_PRODUCT_NUMBER, productNumber);
                intent.putExtra("warehouse_id", currentWareHouse.getWareHouseId());
                intent.putExtra("warehouse_name", currentWareHouse.getWareHouseName());
                startActivity(intent);
            }
        }
    };

    class CategoryAdapter extends BaseAdapter {
        private Context context;
        private ArrayList<Category> arrayList;

        private int selectedColor;
        private int defaultColor;

        private int bgSelectedColor;
        private int bgDefaultColor;

        public CategoryAdapter(ArrayList<Category> arrayList, Context context) {
            this.context = context;
            this.arrayList = arrayList;

            defaultColor = context.getResources().getColor(R.color.inventory_list_text_color);
            selectedColor = context.getResources().getColor(R.color.inventory_list_text_color);

            bgDefaultColor = context.getResources().getColor(R.color.bg_gray);
            bgSelectedColor = context.getResources().getColor(R.color.white);
        }

        @Override
        public int getCount() {
            return arrayList.size();
        }

        @Override
        public Object getItem(int position) {
            return arrayList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Category category = arrayList.get(position);
            String name = category.getName();

            if (category.getCategoryId().equals("-1")) {
                convertView = LayoutInflater.from(context).inflate(R.layout.item_category_add, parent, false);
                ImageView addCategory = (ImageView) convertView.findViewById(R.id.category_list_add);
            } else {
                convertView = LayoutInflater.from(context).inflate(R.layout.item_category_list, parent, false);
                BaseTextView categoryTextView = (BaseTextView) convertView.findViewById(R.id.category_list_title);
                categoryTextView.setText(name);

                if (categoryListView.isItemChecked(position)) {
                    categoryTextView.setTextColor(selectedColor);
                    convertView.setBackgroundColor(bgSelectedColor);
                } else {
                    categoryTextView.setTextColor(defaultColor);
                    convertView.setBackgroundColor(bgDefaultColor);
                }
            }
            return convertView;
        }

        public void setDataSource(ArrayList<Category> arrayList) {
            this.arrayList = arrayList;
            notifyDataSetChanged();
        }
    }


    private void getDataByCategoryWithStockStatus() {
        pageNumber = "0";
        getDataFromServer(false);
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
                WareHouse wareHouse1 = new WareHouse();
                wareHouseArrayList = new ArrayList<WareHouse>();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject1 = jsonArray.optJSONObject(i);
                    if (jsonObject1 != null) {
                        WareHouse wareHouse = gson.fromJson(jsonObject1.toString(), WareHouse.class);
                        if (currentWareHouse != null) {
                            if (wareHouse.getWareHouseId() == currentWareHouse.getWareHouseId()) {
                                wareHouse.setDefault(true);
                                wareHouse1 = wareHouse;
                            }
                        } else {
                            if (wareHouse.getWareHouseId() == defaultWareHouseId) {
                                wareHouse.setDefault(true);
                                wareHouse1 = wareHouse;
                            }
                        }
                        wareHouseArrayList.add(wareHouse);
                    }
                }
                if (currentWareHouse == null) {
                    currentWareHouse = wareHouse1;
                }
                WareHouseManager.getInstance().setDefaultWareHouseId(currentWareHouse.getWareHouseId());
                Log.e(TAG, "1111" + String.valueOf(WareHouseManager.getInstance().getDefaultWareHouseId()));
                WareHouseManager.getInstance().setDefaultWareHouseName(currentWareHouse.getWareHouseName());
                WareHouseManager.getInstance().setScanWareHouseId(currentWareHouse.getWareHouseId());
                WareHouseManager.getInstance().setScanWareHouseName(currentWareHouse.getWareHouseName());
                inventoryWarehouseTextView.setText(currentWareHouse.getWareHouseName());
                pageNumber = "0";
                inventoryListView.setAdapter(inventoryAdapter);
                getDataFromServer(false);
            }

            @Override
            public void errorListener(String s) {
                LogHelper.getInstance().e(TAG, s);
            }
        });
    }

    private void getDataFromServer(final boolean isCallFormLoadMore) {
        preOrderSearchRequest = new PreOrderSearchRequest(currentWareHouse.getWareHouseId(), pageNumber, stockStatus, categoryId);
        webClient.httpGetInventories(preOrderSearchRequest, new NativeJsonResponseListener<JSONObject>() {
            @Override
            public void listener(JSONObject jsonObject) {
                if (jsonObject != null) {
                    final Gson gson = new Gson();
                    ArrayList arrayList1 = new ArrayList();
                    String total = jsonObject.optString("total");
                    String nextPage = jsonObject.optString("next_page");
                    Log.e("231231312312", "nextPage:" + nextPage);
                    Log.e("231231312312", "pageNumber:" + String.valueOf(pageNumber));
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
                        inventoryArrayList.addAll(arrayList1);
                    } else {
                        inventoryArrayList = arrayList1;
                    }
                    inventoryAdapter.setDataSource(inventoryArrayList);
                    inventoryAdapter.notifyDataSetChanged();
                    LoadMoreScrollListener.setIsLoading(false);
                    havingStockLayout.setEnabled(true);
                    inventoryListView.setEnabled(true);
                    categoryListView.setEnabled(true);
                } else {
                    havingStockLayout.setEnabled(true);
                    inventoryListView.setEnabled(true);
                    categoryListView.setEnabled(true);
                }
            }

            @Override
            public void errorListener(String s) {
                havingStockLayout.setEnabled(true);
                inventoryListView.setEnabled(true);
                categoryListView.setEnabled(true);
            }
        });
    }

    private void getCategoryByStockStatusFromServer(String categoryStatus, int warehouseId) {
        UpdateCategoryCloudApiRequest updateCategoryCloudApiRequest = new UpdateCategoryCloudApiRequest(currentEmployeeId,
                currentShopId, categoryStatus, warehouseId);
        webClient.getCategories(updateCategoryCloudApiRequest, new NativeJsonResponseListener<JSONObject>() {
            @Override
            public void listener(JSONObject jsonObject) {
                if (jsonObject != null) {
                    categoryArrayList.clear();
                    String tag = mContext.getResources().getString(R.string.inventory_category_all);
                    Category allCategory = new Category(CATEGORY_ALL, tag);
                    categoryArrayList.add(allCategory);
                    final Gson gson = new Gson();
                    JSONArray jsonArray = jsonObject.optJSONArray("retbody");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject categoryItem = jsonArray.optJSONObject(i);
                        Category category = gson.fromJson(categoryItem.toString(), Category.class);
                        categoryArrayList.add(category);
                    }
                    String addTag = mContext.getResources().getString(R.string.inventory_category_add);
                    Category addCategory = new Category(CATEGORY_ADD, addTag);
                    categoryArrayList.add(addCategory);
                    categoryAdapter.setDataSource(categoryArrayList);
                    Log.e("sada", "sda");
                }
            }

            @Override
            public void errorListener(String s) {

            }
        });
    }

    public void menuClick(View view) {
        final PopupWindowUtil popupWindow = new PopupWindowUtil(getContext(), wareHouseArrayList);
        popupWindow.notifyData();
        int currentPosition = getCurrentWareHousePosition();
        if (currentPosition != -1) {
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
                WareHouseManager.getInstance().setScanWareHouseId(currentWareHouse.getWareHouseId());
                WareHouseManager.getInstance().setScanWareHouseName(currentWareHouse.getWareHouseName());
                pageNumber = "0";
                getDataFromServer(false);
                if (!isHavingStockChecked) {
                    getCategoryByStockStatusFromServer("1", currentWareHouse.getWareHouseId());
                } else {
                    getCategoryByStockStatusFromServer("100", currentWareHouse.getWareHouseId());
                }
            }
        });
        //根据后面的数字 手动调节窗口的宽度
        popupWindow.show(view, 4);
    }

    private int getCurrentWareHousePosition() {
        if (wareHouseArrayList == null) {
            return -1;
        } else {
            for (int i = 0; i < wareHouseArrayList.size(); i++) {
                if (wareHouseArrayList.get(i).isDefault()) {
                    return i;
                }
            }
            return -1;
        }
    }

    private void setWareHouseTextView(WareHouse wareHouse) {
        inventoryWarehouseTextView.setText(wareHouse.getWareHouseName());
    }

    private ObserverChangeShop observerChangeShop = new ObserverChangeShop() {
        @Override
        public void shopChange() {
            pageNumber = "0";
            currentWareHouse = null;
            currentShopId = Integer.parseInt(AccountManager.getInstance().getCurrentShopId());
            wareHouseListRequest = new WareHouseListRequest(currentShopId);
            getWareHouseDataFromServer();
        }
    };
}
