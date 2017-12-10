package com.honeywell.wholesale.ui.purchase.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.gson.Gson;
import com.honeywell.wholesale.R;
import com.honeywell.wholesale.framework.event.PreCartToCartEvent;
import com.honeywell.wholesale.framework.http.NativeJsonResponseListener;
import com.honeywell.wholesale.framework.http.WebClient;
import com.honeywell.wholesale.framework.model.Inventory;
import com.honeywell.wholesale.framework.model.PreCart;
import com.honeywell.wholesale.framework.search.SearchManager;
import com.honeywell.wholesale.framework.search.SearchResultItem;
import com.honeywell.wholesale.ui.base.BaseActivity;
import com.honeywell.wholesale.ui.base.BaseTextView;
import com.honeywell.wholesale.ui.purchase.adapter.PurchaseSearchAdapter;
import com.honeywell.wholesale.ui.purchase.presenter.ProductSelectPresenter;
import com.honeywell.wholesale.ui.purchase.view.ProductSelectView;
import com.honeywell.wholesale.ui.search.LoadMoreScrollListener;
import com.honeywell.wholesale.ui.transaction.preorders.CartModel;
import com.honeywell.wholesale.ui.transaction.preorders.ObserverCartData;
import com.honeywell.wholesale.ui.transaction.preorders.PreTransactionActivity;
import com.honeywell.wholesale.ui.transaction.preorders.PreTransactionCartActivity;
import com.honeywell.wholesale.ui.transaction.preorders.network.PreOrderSearchRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import de.greenrobot.event.EventBus;

public class PurchaseSelectActivity extends BaseActivity implements ProductSelectView {

    private static final String TAG = PurchaseSelectActivity.class.getSimpleName();

    private ArrayList<PreCart> preCartList;//本页面暂存的数据结构

    private ImageView backImageView;
    private ImageView searchCancelImageView;
    private EditText editText;
    private ListView resultListView;
    private BaseTextView resultCountTextView;
    private BaseTextView totalMoneyTextView;
    private BaseTextView totalAmountTextView;
    private BaseTextView searchSelectTextView;

    private LinearLayout totalInfoLayout;
    private Context context = this;

    // select search scale
    private View searchSelectView;

    private Button saveButton;

    public static final String SEARCH_KEY = "Search_Key";
    public static final String SEARCH_MODE = "SEARCH_MODE";
    public static final String SEARCH_SELECT = "SEARCH_SELECT";
    public static final String SEARCH_DATA = "SEARCH_DATA";

    private SearchResultItem.ResultType type;

    private String searchData;
    private String searchMode = "";

    private int wareHouseId;

    private ProductSelectPresenter productSelectPresenter;

    private PurchaseSearchAdapter purchaseSearchAdapter;
    private ArrayList<Inventory> arrayList = new ArrayList<>();

    private SearchManager searchManager = SearchManager.getInstance();

    private CartModel cartModel = CartModel.getInstance();

    private GradientDrawable drawable;

    private WebClient webClient;

    private PreOrderSearchRequest preOrderSearchRequest;

    private static String isLoadMore = "false";

    private String stockStatus = "0";

    private String pageNumber = "0";
    private LoadMoreScrollListener loadMoreScrollListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        setContentView(R.layout.activity_pre_sale_return_search);
        productSelectPresenter = new ProductSelectPresenter(this);
//        productSelectPresenter.attach(this);
        initialData();
        initialView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        resultListView.setEnabled(true);
        setBottomShow();
        if (purchaseSearchAdapter != null) {
            purchaseSearchAdapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        productSelectPresenter.cancelRequest();
        EventBus.getDefault().unregister(this);//取消注册
        productSelectPresenter.detach();
    }

    @Override
    public void onShowLoading() {

    }

    @Override
    public void onHideLoading() {

    }

    @Override
    public void onToast(String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onIntent() {

    }

    @Override
    public void setAdapterDataSource(ArrayList<Inventory> arrayList) {
        Log.e(TAG,"2121212");
        purchaseSearchAdapter.setDataSource(arrayList);
        purchaseSearchAdapter.notifyDataSetChanged();
    }

    @Override
    public void setResultCountText(String value) {
        resultCountTextView.setText(value);
    }

    @Override
    public void setLoadMore(String loadMore) {
        isLoadMore = loadMore;
    }

    @Override
    public String getLoadMore() {
        return isLoadMore;
    }


    @Override
    public void setArrayListData(ArrayList<Inventory> arrayList) {
        this.arrayList = arrayList;
    }

    @Override
    public Context getContext() {
        return this;
    }

    public void onEventMainThread(PreCartToCartEvent event) {
        Log.e("TAG", "LoginSuccessdEvent");
        PurchaseSelectActivity.this.finish();//收到订阅事件之后关闭当前界面
    }


    private void initialData() {
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        webClient = new WebClient();
        if (bundle != null) {
            wareHouseId = bundle.getInt("wareHouseId");
        }

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
        totalInfoLayout = (LinearLayout) findViewById(R.id.total_info_layout);
        totalInfoLayout.setVisibility(View.GONE);
        totalInfoLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PurchaseSelectActivity.this, PreTransactionCartActivity.class);
                startActivity(intent);
            }
        });
        backImageView = (ImageView) findViewById(R.id.pre_icon_back);
        backImageView.setOnClickListener(onClickListener);
        resultCountTextView = (BaseTextView) findViewById(R.id.pre_search_result_count);
        totalAmountTextView = (BaseTextView) findViewById(R.id.total_amount_text_view);
        searchCancelImageView = (ImageView) findViewById(R.id.pre_search_cancel);
        searchCancelImageView.bringToFront();
        drawable = new GradientDrawable();

        saveButton = (Button) findViewById(R.id.order_to_cart_button);
        setAddOrderButtonEnable();
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                setResult(PreTransactionActivity.RESULT_CUSTOMER_SUCCEED_CODE, intent);
                finish();
            }
        });

        totalMoneyTextView = (BaseTextView) findViewById(R.id.total_money_text_view);
        editText = (EditText) findViewById(R.id.pre_search_edittext);

        editText.addTextChangedListener(
                new TextWatcher() {
                    @Override public void onTextChanged(CharSequence s, int start, int before, int count) { }
                    @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

                    private Timer timer=new Timer();
                    private final long DELAY = 300; // milliseconds

                    @Override
                    public void afterTextChanged(final Editable s) {
//                        pageNumber = "0";
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
        );
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

        purchaseSearchAdapter = new PurchaseSearchAdapter(this);

        resultListView = (ListView) findViewById(R.id.pre_search_result_list);
        resultListView.setAdapter(purchaseSearchAdapter);
        resultListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                resultListView.setEnabled(false);
                Intent intent = new Intent(getApplicationContext(), PurchaseSkuSelectActicity.class);
                intent.putExtra("wareHouseId", wareHouseId);
                intent.putExtra("productId", arrayList.get(i).getProductId());
                intent.putExtra("productName", arrayList.get(i).getProductName());
                intent.putExtra("productPic", arrayList.get(i).getPicSrc());
                intent.putExtra("productStock", arrayList.get(i).getQuantity());
                intent.putExtra("productPrice", arrayList.get(i).getStandardPrice());
                intent.putExtra("productUnit", arrayList.get(i).getUnit());
                startActivity(intent);
            }
        });

        searchCancelImageView.setOnClickListener(onClickListener);
        loadMoreScrollListener = new LoadMoreScrollListener();
        resultListView.setOnScrollListener(loadMoreScrollListener);
        loadMoreScrollListener.setLoadMore(new LoadMoreScrollListener.LoadMore() {
            @Override
            public void loadMoreData() {
                if (isLoadMore.equals("true")) {
                    productSelectPresenter.getDataFromServer(editText.getText().toString(), true);
//                    getDataFromServer(editText.getText().toString(), true);
                }
            }
        });

        setBottomShow();
    }

    private void initialSearch(String s) {
        pageNumber = "0";
        productSelectPresenter.setPageNumber(pageNumber);
        productSelectPresenter.getDataFromServer(s, false);
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

    private void setBottomShow() {
        if (cartModel.isEmpty()) {
            totalAmountTextView.setVisibility(View.GONE);
            totalMoneyTextView.setText("未选择商品");
            totalInfoLayout.setEnabled(false);
        } else {
            totalInfoLayout.setEnabled(true);
            totalAmountTextView.setVisibility(View.VISIBLE);

            BigDecimal big1 = new BigDecimal(cartModel.getTotalMoneyByStock());
            DecimalFormat format = new DecimalFormat("##0.00");
            String value = format.format(big1);

            totalMoneyTextView.setText("¥ " + value);
            totalAmountTextView.setText(cartModel.getTotalAmount());
        }
    }

    private void getDataFromServer(String s, final boolean isCallFormLoadMore) {
        preOrderSearchRequest = new PreOrderSearchRequest(0, s, pageNumber, stockStatus);
        webClient.httpGetInventories(preOrderSearchRequest, new NativeJsonResponseListener<JSONObject>() {
            @Override
            public void listener(JSONObject jsonObject) {
                if (jsonObject != null) {
                    final Gson gson = new Gson();
                    ArrayList<Inventory> arrayList1 = new ArrayList<Inventory>();
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
                    purchaseSearchAdapter.setDataSource(arrayList);
                    purchaseSearchAdapter.notifyDataSetChanged();
                    LoadMoreScrollListener.setIsLoading(false);
                    String count = String.format(getResources().getString(R.string.search_result_count), total);
                    resultCountTextView.setText(count);
                }
            }

            @Override
            public void errorListener(String s) {

            }
        });
    }

    private ObserverCartData observerCartData = new ObserverCartData() {
        @Override
        public void dataChanged() {
            setBottomShow();
        }
    };

    public void setAddOrderButtonEnable() {
        drawable.setShape(GradientDrawable.RECTANGLE); // 画框
        drawable.setStroke(1, getResources().getColor(R.color.transaction_tab_selected_bg_color)); // 边框粗细及颜色
        drawable.setColor(getResources().getColor(R.color.transaction_tab_selected_bg_color)); // 边框内部颜色
        saveButton.setEnabled(true);
        saveButton.setBackground(drawable);
        saveButton.setTextColor(getResources().getColor(R.color.white));
    }
}
