package com.honeywell.wholesale.ui.purchase.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.google.gson.Gson;
import com.honeywell.hybridapp.framework.log.LogHelper;
import com.honeywell.wholesale.R;
import com.honeywell.wholesale.framework.database.SupplierDAO;
import com.honeywell.wholesale.framework.event.PayRIghtNowEvent;
import com.honeywell.wholesale.framework.event.ScannerToTransactionEvent;
import com.honeywell.wholesale.framework.http.NativeJsonResponseListener;
import com.honeywell.wholesale.framework.http.WebClient;
import com.honeywell.wholesale.framework.model.AccountManager;
import com.honeywell.wholesale.framework.model.ExtraCost;
import com.honeywell.wholesale.framework.model.PreCart;
import com.honeywell.wholesale.framework.model.PreOrder;
import com.honeywell.wholesale.framework.model.SKU;
import com.honeywell.wholesale.framework.model.Supplier;
import com.honeywell.wholesale.framework.model.WareHouse;
import com.honeywell.wholesale.framework.scanner.ScanerRespManager;
import com.honeywell.wholesale.framework.utils.PopupWindowUtil;
import com.honeywell.wholesale.framework.utils.SwitchButton;
import com.honeywell.wholesale.ui.base.BaseActivity;
import com.honeywell.wholesale.ui.base.BaseTextView;
import com.honeywell.wholesale.ui.menu.setting.warehouse.request.WareHouseListRequest;
import com.honeywell.wholesale.ui.order.OrderConfirmActivity;
import com.honeywell.wholesale.ui.purchase.presenter.OrderPresenter;
import com.honeywell.wholesale.ui.purchase.view.OrderView;
import com.honeywell.wholesale.ui.saleReturn.presenter.SaleReturnPresenter;
import com.honeywell.wholesale.ui.scan.ProductDetailActivity;
import com.honeywell.wholesale.ui.scan.network.ProductDetailRequest;
import com.honeywell.wholesale.ui.transaction.cart.adapter.CartMoreAdapter;
import com.honeywell.wholesale.ui.transaction.preorders.CartModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;

import de.greenrobot.event.EventBus;

import static com.honeywell.wholesale.framework.database.CartRefCustomerDao.removeCartRefCustomer;
import static com.honeywell.wholesale.framework.database.CartRefSKUDao.removeCartRefSKUItem;

public class PurchaseActivity extends BaseActivity implements OrderView {

    private static final String TAG = PurchaseActivity.class.getSimpleName();

    public static final String INTENT_TYPE = "INTENT_TYPE";
    public static final String INTENT_ADD_TO_TRANSACTION = "INTENT_ADD_TO_TRANSACTION";
    public static final String INTENT_CART_TO_TRANSACTION = "INTENT_CART_TO_TRANSACTION";
    public static final String INTENT_CUSTOMER_TO_TRANSACTION = "INTENT_CUSTOMER_TO_TRANSACTION";
    public static final String INTENT_INVENTORY_TO_TRANSACTION = "INTENT_INVENTORY_TO_TRANSACTION";
    public static final String INTENT_VALUE_CART_CUSTOMER_ID = "INTENT_VALUE_CART_CUSTOMER_ID";
    public static final String SEARCH_FOR_TRANSACTION_ORDER = "SEARCH_FOR_TRANSACTION_ORDER";
    public static final String INTENT_KEY_PRODUCT_ID = "INTENT_KEY_PRODUCT_ID";
    public static final String INTENT_PRODUCT_LIST = "INTENT_PRODUCT_LIST";
    public static final String INTENT_CHOOSE_SUPPLIER = "INTENT_CHOOSE_SUPPLIER";
    public static final String INTENT_SUPPLIER_ID = "INTENT_SUPPLIER_ID";
    public static final String INTENT_SUPPLIER_NAME = "INTENT_SUPPLIER_NAME";

    public static final String INTENT_EXTRA_COST_ID = "INTENT_EXTRA_COST_ID";
    public static final String INTENT_EXTRA_COST_NAME = "INTENT_EXTRA_COST_NAME";

    public static final String INTENT_BATCH_ID = "INTENT_BATCH_ID";
    public static final String INTENT_BATCH_NAME = "INTENT_BATCH_NAME";

    private static final String INDIVIDUAL_GUEST_1_ID = "0";

    public static final int REQUEST_CODE = 1;
    public static final int RESULT_PRODUCT_SUCCEED_CODE = 2;
    public static final int RESULT_SUPPLIER_SUCCEED_CODE = 3;

    private static final Integer TITLE_VIEW_TAG_SKU_KEY = -1;

    private ScanerRespManager scanerRespManager;

    private Context context = this;

    private int preOrderId;

    private String cartUuid;
    private String currentSupplierID;
    private String shopId = AccountManager.getInstance().getCurrentShopId();
    private String employeeId = AccountManager.getInstance().getCurrentAccount().getEmployeeId();
    private String userName = AccountManager.getInstance().getCurrentAccount().getUserName();
    private String shopName = AccountManager.getInstance().getCurrentAccount().getShopName();

    private Supplier supplier;

    private CartMoreAdapter mCartMoreAdapter;

    private LinearLayout productContainerSection;

    private RelativeLayout addProductLayout;
    private RelativeLayout selectWareHouseLayout;
    private View orderCustomerField;

    private BaseTextView addProductTextView;
    private BaseTextView totalMoneyTextView;
    private BaseTextView totalCountTextView;
    private BaseTextView supplierTextView;
    private BaseTextView moreTextView;
    private BaseTextView wareHouseTextView;
    private BaseTextView titleTextView;

    private ImageView backImageView;
    private ImageView moreImageView;
    private ImageView wareHouseImageView;

    private Button saveButton;

    private SwitchButton switchButton;

    private ArrayList<PreCart> defaultPreCartItemList;
    private ArrayList<PreCart> preCartList;//本页面暂存的数据结构
    private ArrayList<String> mCartMoreList;
    private ArrayList<WareHouse> wareHouseArrayList;

    private WebClient webClient;
    private ProductDetailRequest productDetailRequest;
    private PopupWindow cartMorePopupWindow;
    private GradientDrawable drawable;
    private int currentShopId = Integer.parseInt(AccountManager.getInstance().getCurrentShopId());
    private CartModel cartModel = CartModel.getInstance();
    private WareHouseListRequest wareHouseListRequest;
    private WareHouse currentWareHouse;
    private PreOrder preOrder;

    private OrderPresenter orderPresenter;

    public static PurchaseActivity purchaseActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchase);
        EventBus.getDefault().register(this);

        if (ProductDetailActivity.productDetailActivity != null) {
            ProductDetailActivity.productDetailActivity.finish();
        }
        purchaseActivity = this;

        orderPresenter = new OrderPresenter(this);
//        orderPresenter.attach(this);
        initView();
        initData();
        orderPresenter.getWareHouseDataFromServer();
//        getWareHouseDataFromServer();

    }

    @Override
    protected void onResume() {
        super.onResume();
//        getWareHouseDataFromServer();
        reloadData();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_CODE) {
            if (resultCode == RESULT_PRODUCT_SUCCEED_CODE) {//接收选择货品页面传递的信息

            } else if (resultCode == RESULT_SUPPLIER_SUCCEED_CODE) {//接受选择客户页面传递的信息
                currentSupplierID = data.getStringExtra(INTENT_SUPPLIER_ID);
                String supplierName = data.getStringExtra(INTENT_SUPPLIER_NAME);
                supplierTextView.setText(supplierName);
                supplier = new Supplier();
                supplier = SupplierDAO.queryVendor(shopId, currentSupplierID);

                cartModel.setSupplierId(supplier.getSupplierId());
                cartModel.setSupplierName(supplier.getSupplierName());
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        orderPresenter.cancelRequest();
        orderPresenter.detach();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onBackPressed() {
        showNormalDialog();
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
    public void setAddProductLayoutEnabled(boolean flag) {
        addProductLayout.setEnabled(flag);
    }

    @Override
    public void setWarehouseListData(ArrayList<WareHouse> wareHouseArrayList) {
        this.wareHouseArrayList = wareHouseArrayList;
    }

    @Override
    public void setCurrentWarehouse(WareHouse currentWarehouse) {
        this.currentWareHouse = currentWarehouse;
    }

    @Override
    public void judgeButtonEnable() {
        if (cartModel.isEmpty()) {
            setAddOrderButtonUnEnable();
        } else {
            setAddOrderButtonEnable();
        }
    }

    @Override
    public void setWarehouseText(String warehouseName) {
        wareHouseTextView.setText(warehouseName);
    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public void addProductItemController(final PreCart preCart, final SKU sku) {
        int i = getExistItemPosition(preCart.getProductId());

        if (i == -1) {
            addProductData(preCart);
            addProductTitleView(preCart);
            addProductSubView(preCart, sku);
        } else {
            SKU sku1 = getExistItemPosition1(sku);
            if (sku1 == null) {
                addProductData(preCart, sku);
                addProductSubView(preCart, sku);
            } else {
                BigDecimal bigDecimal = new BigDecimal("1");
                try {
                    bigDecimal = new BigDecimal(cartModel.getById(preCart.getProductId()).getSkuList().get(getExistItemPosition(sku)).getSaleAmount());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                bigDecimal = bigDecimal.add(new BigDecimal("1"));
                cartModel.getById(preCart.getProductId()).getSkuList().get(getExistItemPosition(sku)).setSaleAmount(
                        bigDecimal.toPlainString());
                modifyProductSubView(preCart, sku, bigDecimal.toPlainString());
            }
        }
        judgeButtonEnable();
    }

    public void onEventMainThread(ScannerToTransactionEvent event) {
        String productCode = event.getMsg();
        Log.e(TAG, productCode);
        orderPresenter.productSale(productCode);
//        productSale(productCode);
    }

    public void onEventMainThread(PayRIghtNowEvent event) {
        if (event.getmMsg().equals(SaleReturnPresenter.SALE_RETURN_SUCCEED)) {
            finish();
        }
    }

    private void initData() {
        webClient = new WebClient();
        preOrder = new PreOrder();
        productDetailRequest = new ProductDetailRequest();
        wareHouseListRequest = new WareHouseListRequest(currentShopId);
        preCartList = new ArrayList<>();
        defaultPreCartItemList = new ArrayList<>();
        mCartMoreList = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.cart_more_list)));

        if (SupplierDAO.getAllVendor(shopId).size() != 0) {
            supplier = (Supplier) SupplierDAO.getAllVendor(shopId).get(0);
            cartModel.setSupplierId(supplier.getSupplierId());
            cartModel.setSupplierName(supplier.getSupplierName());
            supplierTextView.setText(supplier.getSupplierName());
        }
        scanerRespManager = ScanerRespManager.getInstance();
        scanerRespManager.setType(ScanerRespManager.ScanerRespType.SCANER_RESP_SEARCH);
        judgeButtonEnable();
    }

    private void reloadData() {
        scanerRespManager.setType(ScanerRespManager.ScanerRespType.SCANER_RESP_SEARCH);
        saveButton.setEnabled(true);
        productContainerSection.removeAllViews();
        for (int i = 0; i < cartModel.size(); i++) {
            addProductTitleView(cartModel.get(i));
            for (int j = 0; j < cartModel.get(i).getSkuList().size(); j++) {
                addProductSubView(cartModel.get(i), cartModel.get(i).getSkuList().get(j));
            }
        }
        judgeButtonEnable();
    }

    private void initView() {
        Log.e(TAG, "initView");
        productContainerSection = (LinearLayout) findViewById(R.id.product_containner);
        totalMoneyTextView = (BaseTextView) findViewById(R.id.total_text_view);
        totalCountTextView = (BaseTextView) findViewById(R.id.total_amount_text_view);
        addProductTextView = (BaseTextView) findViewById(R.id.add_product_textView);
        addProductLayout = (RelativeLayout) findViewById(R.id.add_product_layout);
        totalMoneyTextView.setText("合计：¥" + String.valueOf(0.0));
        totalCountTextView.setText("共" + String.valueOf(0) + "种货品");
        wareHouseTextView = (BaseTextView) findViewById(R.id.pre_order_warehouse_text_view);
        wareHouseTextView.setTextColor(getResources().getColor(R.color.warehouse_enabled_text_color));
        titleTextView = (BaseTextView) findViewById(R.id.cart_management_title_textview);
        titleTextView.setText("采购单");
        wareHouseImageView = (ImageView) findViewById(R.id.right_warehouse_image_view);
        wareHouseImageView.setImageResource(R.drawable.preorder_warehouse_normal);

        switchButton = (SwitchButton) findViewById(R.id.warehouse_switch_button);
        switchButton.setVisibility(View.GONE);
        switchButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    selectWareHouseLayout.setEnabled(true);
                    wareHouseTextView.setVisibility(View.VISIBLE);
                    wareHouseImageView.setVisibility(View.VISIBLE);

                } else {
                    selectWareHouseLayout.setEnabled(false);
                    wareHouseTextView.setVisibility(View.GONE);
                    wareHouseImageView.setVisibility(View.GONE);
                }
            }
        });

        orderCustomerField = findViewById(R.id.order_info_field);
        supplierTextView = (BaseTextView) findViewById(R.id.pre_order_customer_text_view);
        moreTextView = (BaseTextView) findViewById(R.id.cart_management_more_textview);
        moreImageView = (ImageView) findViewById(R.id.cart_management_more_imageview);
        moreImageView.setVisibility(View.GONE);
        moreImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initCartMorePopupWindow();
                int left = Math.round(v.getX()) + v.getWidth() / 2 - 160 - 55;
                int top = v.getTop();
                cartMorePopupWindow.showAsDropDown(v, left, top);
            }
        });
        drawable = new GradientDrawable();
        moreTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initCartMorePopupWindow();
                int left = Math.round(v.getX()) + v.getWidth() / 2 - 160 - 55;
                int top = v.getTop();
                cartMorePopupWindow.showAsDropDown(v, left, top);
            }
        });
        backImageView = (ImageView) findViewById(R.id.cart_management_back_imageView);
        backImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showNormalDialog();
            }
        });
        orderCustomerField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scanerRespManager.setType(ScanerRespManager.ScanerRespType.SCANER_NULL_TYPE);
                Intent intent = new Intent(PurchaseActivity.this, SupplierSelectActivity.class);
                intent.putExtra(INTENT_CHOOSE_SUPPLIER, INTENT_CHOOSE_SUPPLIER);
                startActivityForResult(intent, REQUEST_CODE);
            }
        });

        addProductLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scanerRespManager.setType(ScanerRespManager.ScanerRespType.SCANER_NULL_TYPE);
                int wareHouseId;
                if (currentWareHouse == null) {
                    wareHouseId = 0;
                } else {
                    wareHouseId = currentWareHouse.getWareHouseId();
                }
                Intent intent = new Intent(PurchaseActivity.this, PurchaseSelectActivity.class);
                Bundle bundle = new Bundle();
                bundle.putInt("wareHouseId", wareHouseId);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        selectWareHouseLayout = (RelativeLayout) findViewById(R.id.order_warehouse_info_field);
        selectWareHouseLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                menuClick(view);
            }
        });

        saveButton = (Button) findViewById(R.id.pre_transaction_add_button);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cartModel.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "请添加商品", Toast.LENGTH_SHORT).show();
                } else {
                    if (cartModel.getSupplierId() == null){
                        onToast("请选择供应商");
                    }else {

                        if (!checkProductList()) {
                            Toast.makeText(getApplicationContext(), R.string.pre_order_buy_info, Toast.LENGTH_SHORT).show();
                            return;
                        }
                        saveButton.setEnabled(false);
                        JSONObject jsonObject = null;
                        try {
                            String json = new Gson().toJson(preCartList);
                            jsonObject = new JSONObject();
                            jsonObject.put("productList", json);
                            jsonObject.put("customerName", supplier.getSupplierName());
                            jsonObject.put("customerId", supplier.getSupplierId());
                            jsonObject.put("shopName", shopName);
                            jsonObject.put("employeeName", userName);
                            jsonObject.put("shopId", shopId);
                            jsonObject.put("wareHouseId", currentWareHouse.getWareHouseId());
                            jsonObject.put("wareHouseName", currentWareHouse.getWareHouseName());
                            jsonObject.put("uuid", cartUuid);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if (switchButton.isChecked()) {
                            cartModel.setWarehouseId(String.valueOf(currentWareHouse.getWareHouseId()));
                            cartModel.setWarehouseName(currentWareHouse.getWareHouseName());
                        } else {
                            cartModel.setWarehouseId("");
                            cartModel.setWarehouseName("");
                        }
                        String json = new Gson().toJson(jsonObject);
                        scanerRespManager.setType(ScanerRespManager.ScanerRespType.SCANER_NULL_TYPE);
                        Intent intent = new Intent(PurchaseActivity.this, PurchaseOrderConfirmActivity.class);
                        intent.putExtra(OrderConfirmActivity.INTENT_KEY_SELECTED_PRODUCTS, json);
                        startActivity(intent);
                    }
                }
//                finish();
            }
        });

        switchButton.setChecked(true);
        selectWareHouseLayout.setEnabled(true);
        wareHouseTextView.setVisibility(View.VISIBLE);
        wareHouseImageView.setVisibility(View.VISIBLE);
    }

    /**
     * 清空订单货品
     */
    private void clearProduct() {
        productContainerSection.removeAllViews();
        cartModel.clear();
        totalMoneyTextView.setText("合计：¥" + "0");
        totalCountTextView.setText("共" + "0" + "种货品");
    }

    /**
     * 检查商品列表单价或数量是否为空或0
     */

    private boolean checkProductList() {
        SKU sku = null;
        int type = 0;
        OUT:
        for (int i = 0; i < cartModel.size(); i++) {
            for (int j = 0; j < cartModel.get(i).getSkuList().size(); j++) {
                sku = cartModel.get(i).getSkuList().get(j);
                BigDecimal valueDecimal = new BigDecimal(sku.getSaleAmount());
                if (valueDecimal.compareTo(new BigDecimal("0")) == 0) {
                    Log.e(TAG, sku.getSkuName() + "total number is 0");
                    type = 1;
                    break OUT;
                }
//                valueDecimal = new BigDecimal(sku.getStockPrice());
//                if (valueDecimal.compareTo(new BigDecimal("0")) == 0) {
//                    Log.e(TAG, sku.getSkuName() + "price is 0");
//                    type = 2;
//                    break OUT;
//                }
                sku = null;
            }
        }

        if (sku != null) {
            View view = getSubViewByTag(sku.getSkuId());

            if (view != null) {
                if (type == 1) {
                    final EditText countEditText = (EditText) view.findViewById(R.id.product_no_text_view);
                    countEditText.requestFocus();
                }

//                if (type == 2) {
//                    final EditText unitPriceEditText = (EditText) view.findViewById(R.id.product_unit_price);
//                    unitPriceEditText.requestFocus();
//                }
                return false;
            }
        }

        return true;
    }

    /**
     * 根据productId到server获取商品详情
     *
     * @param productId
     */
    private void productSale(final int productId) {
        productDetailRequest = new ProductDetailRequest(productId, currentWareHouse.getWareHouseId());
        webClient.httpProductDetail(productDetailRequest, new NativeJsonResponseListener<JSONObject>() {
            @Override
            public void listener(JSONObject jsonObject) {
                String productName = jsonObject.optString("product_name");
                String unitPrice = jsonObject.optString("standard_price");
                String imageUrl = jsonObject.optString("pic_src");
                String unit = jsonObject.optString("unit");
                String stockQuantity = jsonObject.optString("quantity");
                String productCode = jsonObject.optString("product_code");
                PreCart preCart = new PreCart(productId, productCode, productName, unitPrice, imageUrl, stockQuantity, String.valueOf(1), unit);

                if (Integer.valueOf(stockQuantity) < 1) {
                    Toast.makeText(getApplicationContext(), "该商品库存不足", Toast.LENGTH_SHORT).show();
                } else {

                }
            }

            @Override
            public void errorListener(String s) {
                LogHelper.getInstance().e(TAG, "查询货品详情失败：" + s);
                Toast.makeText(getApplicationContext(), "查询货品详情失败", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * 根据条形码productCode到server获取商品详情
     *
     * @param productCode
     */
    private void productSale(final String productCode) {
        productDetailRequest = new ProductDetailRequest(productCode, currentWareHouse.getWareHouseId());
        webClient.httpProductDetail(productDetailRequest, new NativeJsonResponseListener<JSONObject>() {
            @Override
            public void listener(JSONObject jsonObject) {

                ArrayList<SKU> skuList = new ArrayList<SKU>();
                String inventoryList = jsonObject.optString("inventory_list");
                Log.e("inventoryList", inventoryList);
                skuList = SKU.fromJson2List(inventoryList);
                SKU skuCurrent = new SKU();
                skuCurrent = null;
                //从服务器拿的数据，很乱，自己慢慢想逻辑，分三种情况
                for (SKU sku : skuList) {
                    if (sku.isSelected()) {
                        skuCurrent = sku;
                        break;
                    }
                }
                if (skuCurrent == null) {
                    for (SKU sku : skuList) {
                        if (sku.getProductCode().equals(productCode)) {
                            skuCurrent = sku;
                            break;
                        }
                    }
                }
                if (skuCurrent == null) {
                    Toast.makeText(getApplicationContext(), "未识别出该商品", Toast.LENGTH_SHORT).show();
                } else {
                    String skuQuantity = skuCurrent.getQuantity();
                    int productId = jsonObject.optInt("product_id");
                    String productName = jsonObject.optString("product_name");
                    String unitPrice = jsonObject.optString("stock_price");
                    String imageUrl = jsonObject.optString("pic_src");
                    String unit = jsonObject.optString("unit");
                    String stockQuantity = jsonObject.optString("total_quantity");
                    skuCurrent.setSaleAmount("1");
                    PreCart preCart = new PreCart(productId, productCode, productName, unitPrice, imageUrl, stockQuantity, unit);
                    preCart.getSkuList().add(skuCurrent);
                    addProductItemController(preCart, skuCurrent);
                    judgeButtonEnable();
                }
            }

            @Override
            public void errorListener(String s) {
                if (s.equals("180000021")) {
                    LogHelper.getInstance().e(TAG, "查询货品详情失败：" + s);
                    Toast.makeText(getApplicationContext(), "商品在该仓库内没有库存", Toast.LENGTH_SHORT).show();
                } else if (s.equals("4006")) {
                    LogHelper.getInstance().e(TAG, "查询货品详情失败：" + s);
                    Toast.makeText(getApplicationContext(), "未识别出该商品", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

//    productSale

    /**
     * 删除订单
     */
    private void deleteCartOrder(ArrayList<PreCart> preCartList) {
        for (PreCart preCart : preCartList) {
            removeCartRefCustomer(preCart.getUuid());
            removeCartRefSKUItem(preCart.getUuid());
        }
    }

    /**
     * 获取当前商品的position
     *
     * @param productId
     * @return
     */
    private int getExistItemPosition(int productId) {
        for (int i = 0; i < cartModel.size(); i++) {
            if (cartModel.get(i).getProductId() == productId) {
                return i;
            }
        }
        return -1;
    }

    private int getExistItemPosition(SKU sku) {
        for (int i = 0; i < cartModel.size(); i++) {
            for (int j = 0; j < cartModel.get(i).getSkuList().size(); j++) {
                if (cartModel.get(i).getSkuList().get(j).getSkuId() == sku.getSkuId()) {
                    return j;
                }
            }
        }
        return -1;
    }

    private SKU getExistItemPosition1(SKU sku) {
        for (int i = 0; i < cartModel.size(); i++) {
            for (int j = 0; j < cartModel.get(i).getSkuList().size(); j++) {
                if (cartModel.get(i).getSkuList().get(j).getSkuId() == sku.getSkuId()) {
                    return cartModel.get(i).getSkuList().get(j);
                }
            }
        }
        return null;
    }


    /**
     * 修改购物车商品的数据
     *
     * @param sku
     * @param price
     * @param judge
     */
    private void modifyProductData(SKU sku, String price, int judge) {
        for (int i = 0; i < cartModel.size(); i++) {
            for (int j = 0; j < cartModel.get(i).getSkuList().size(); j++) {
                if (cartModel.get(i).getSkuList().get(j).getSkuId() == sku.getSkuId()) {
                    cartModel.get(i).getSkuList().get(j).setStockPrice(price);
                }
            }
        }
    }

    /**
     * 修改购物车商品的数据
     *
     * @param sku
     * @param number
     */
    private void modifyProductData(SKU sku, String number) {
        for (int i = 0; i < cartModel.size(); i++) {
            for (int j = 0; j < cartModel.get(i).getSkuList().size(); j++) {
                if (cartModel.get(i).getSkuList().get(j).getSkuId() == sku.getSkuId()) {
                    cartModel.get(i).getSkuList().get(j).setSaleAmount(number);
                    cartModel.get(i).setTotalNumber();
                }
            }
        }
    }

    /**
     * 新增购物车商品的数据
     *
     * @param preCart
     */
    private void addProductData(PreCart preCart) {
        cartModel.add(preCart);
    }


    private void addProductData(PreCart preCart, SKU sku) {
        cartModel.getById(preCart.getProductId()).getSkuList().add(sku);
    }

    /**
     * 删除购物车商品的数据
     *
     * @param preCart
     */
    private void deleteProductData(PreCart preCart) {
        cartModel.remove(preCart);
    }

    private void deleteProductData(PreCart preCart, SKU sku) {
        cartModel.getById(preCart.getProductId()).deleteSkuItem(sku);
        if (cartModel.getById(preCart.getProductId()).getSkuList().isEmpty()) {
            deleteProductData(preCart);
        }
    }

    /**
     * 删除某一条购物车中商品
     *
     * @param view
     * @param preCart
     */
    private void removeProductItemController(View view, PreCart preCart, SKU sku) {
        deleteProductData(preCart, sku);
        int amountCount = cartModel.size();
        totalMoneyTextView.setText("合计：¥" + cartModel.getTotalMoneyByStock());
        totalCountTextView.setText("共" + String.valueOf(amountCount) + "种货品");
        int preCartTag = preCart.getProductId();
        removeProductSubView(view);
        if (cartModel.getById(preCart.getProductId()) == null) {
            View view1 = getViewByTag(preCartTag);
            removeProductSubView(view1);
        }
        judgeButtonEnable();
    }


    /**
     * 删除某一条购物车中商品的view
     *
     * @param view
     */
    private void removeProductSubView(View view) {
        productContainerSection.removeView(view);
    }

    /**
     * 修改某一条购物车中商品的view，用于新增已存在于购物车中的商品
     *
     * @param preCart
     * @param sku
     * @param totalNumber
     */
    private void modifyProductSubView(PreCart preCart, SKU sku, String totalNumber) {
        int skuId = sku.getSkuId();
        View view = getSubViewByTag(skuId);
        final EditText countEditText = (EditText) view.findViewById(R.id.product_no_text_view);
        countEditText.setText(totalNumber);
    }

    private View getViewByTag(int tagKey) {
        for (int i = 0; i < productContainerSection.getChildCount(); i++) {
            if (tagKey == Integer.valueOf(productContainerSection.getChildAt(i).getTag(R.id.precart_view_tag).toString())) {
                return productContainerSection.getChildAt(i);
            }
        }
        return null;
    }

    private View getSubViewByTag(int tagKey) {
        for (int i = 0; i < productContainerSection.getChildCount(); i++) {
            if (tagKey == Integer.valueOf(productContainerSection.getChildAt(i).getTag(R.id.sku_view_tag).toString())) {
                return productContainerSection.getChildAt(i);
            }
        }
        return null;
    }

    private void setViewTag(View view, PreCart preCart) {
        view.setTag(R.id.precart_view_tag, preCart.getProductId());
        view.setTag(R.id.sku_view_tag, TITLE_VIEW_TAG_SKU_KEY);
    }

    private void setViewTag(View view, PreCart preCart, SKU sku) {
        view.setTag(R.id.precart_view_tag, preCart.getProductId());
        if (sku == null) {
            view.setTag(R.id.sku_view_tag, preCart.getProductId());
        } else {
            view.setTag(R.id.sku_view_tag, sku.getSkuId());
        }
    }

    private void addProductTitleView(final PreCart preCart) {
        final View child = getLayoutInflater().inflate(R.layout.item_purchase_title, null);
        setViewTag(child, preCart);
        productContainerSection.addView(child);
        final BaseTextView mProductNameTextView = (BaseTextView) child.findViewById(R.id.product_name);
        final BaseTextView mProductStockQuantityTextView = (BaseTextView) child.findViewById(R.id.product_stock_quantity);
        final ImageView mPoductImageView = (ImageView) child.findViewById(R.id.product_imagmeView);
        mProductNameTextView.setText(preCart.getProductName() + "    ");
        mProductStockQuantityTextView.setVisibility(View.GONE);
        mProductStockQuantityTextView.setText("库存：" + preCart.getStockQuantity());
        String url;
        url = preCart.getImageUrl();
        if (url == null) {
            url = "";
        } else {
            url = preCart.getFirstImageUrl();
        }

        if (url.equals("")) {
            url = String.valueOf(R.drawable.inventory_default_icon);
        }
        Glide
                .with(getApplicationContext())
                .load(url)
                .placeholder(R.drawable.inventory_default_icon)
                .error(R.drawable.inventory_default_icon)
                .priority(Priority.LOW)
                .into(mPoductImageView);
    }

    private void addProductSubView(final PreCart preCart, final SKU sku) {
        final View child = getLayoutInflater().inflate(R.layout.item_purchase, null);
        setViewTag(child, preCart, sku);
        productContainerSection.addView(child);
        final PreCart preCart1 = preCart;
        final RelativeLayout batchLayout = (RelativeLayout) child.findViewById(R.id.batch_layout);
        final BaseTextView batchTextView = (BaseTextView) child.findViewById(R.id.batch_content_text_view);
        batchTextView.setText(sku.getBatchName());
        final BaseTextView extraCostTextView = (BaseTextView) child.findViewById(R.id.extra_cost_content_text_view);
        if (sku.getTotalPremium() != null) {
            extraCostTextView.setText("￥ " + sku.getTotalPremium());
        }
        final RelativeLayout ExtraCostLayout = (RelativeLayout) child.findViewById(R.id.extra_cost_layout);
        final EditText countEditText = (EditText) child.findViewById(R.id.product_no_text_view);
        final BaseTextView mProductNameTextView = (BaseTextView) child.findViewById(R.id.product_name);
        final EditText mProductUnitPriceEditView = (EditText) child.findViewById(R.id.product_unit_price);
        final EditText mTotalPriceEditText = (EditText) child.findViewById(R.id.product_total_price);
        mTotalPriceEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    final String value = mTotalPriceEditText.getText().toString();
                    if (value.endsWith(".")) {
                        mTotalPriceEditText.setText(value.replace(".", ""));
                    }
                }
            }
        });
        mTotalPriceEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                decimalFilter(s, 2, 10, mTotalPriceEditText);
            }

            @Override
            public void afterTextChanged(Editable s) {


                int len = mTotalPriceEditText.getText().toString().length();
                BigDecimal mTotalPriceDecimal = new BigDecimal("0");
                if (mTotalPriceEditText.getText().toString().equals("") || (len == 1 && mTotalPriceEditText.getText().toString().equals("."))) {
                    mTotalPriceDecimal = new BigDecimal("0");
                } else {
                    mTotalPriceDecimal = new BigDecimal(mTotalPriceEditText.getText().toString());
                }
                Log.e(TAG, "afterTextChanged  s = " + mTotalPriceEditText.getText().toString() + " -- " + s.toString());

                Log.e(TAG, "mTotalPriceEditText" + String.valueOf(mTotalPriceEditText.hasFocus()));

                if (mTotalPriceEditText.hasFocus()) {
                    String countString = countEditText.getText().toString();
                    BigDecimal countDecimal = new BigDecimal("0");
                    BigDecimal priceDecimal = new BigDecimal("0");
                    countDecimal = new BigDecimal(countString);
                    priceDecimal = mTotalPriceDecimal.divide(countDecimal, 2, BigDecimal.ROUND_HALF_EVEN);
                    mProductUnitPriceEditView.setText(priceDecimal.toPlainString());

                }

            }
        });
        final View minus = child.findViewById(R.id.product_no_minus);
        final View add = child.findViewById(R.id.product_no_add);
        batchLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scanerRespManager.setType(ScanerRespManager.ScanerRespType.SCANER_NULL_TYPE);
                Intent intent = new Intent(context, BatchSelectActivity.class);
                intent.putExtra(BatchSelectActivity.SKU_ID, sku.getSkuId());
                intent.putExtra(BatchSelectActivity.PRODUCT_ID, preCart.getProductId());
                startActivity(intent);
            }
        });
        ExtraCostLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scanerRespManager.setType(ScanerRespManager.ScanerRespType.SCANER_NULL_TYPE);
                Intent intent = new Intent(context, ExtraCostSelectActivity.class);
                intent.putExtra(ExtraCostSelectActivity.SKU_ID, sku.getSkuId());
                intent.putExtra(ExtraCostSelectActivity.PRODUCT_ID, preCart.getProductId());
                startActivity(intent);
            }
        });
        mProductUnitPriceEditView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    final String value = mProductUnitPriceEditView.getText().toString();
                    if (value.endsWith(".")) {
                        mProductUnitPriceEditView.setText(value.replace(".", ""));
                    }
                }
            }
        });
        mProductUnitPriceEditView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                decimalFilter(s, 2, 10, mProductUnitPriceEditView);
            }

            @Override
            public void afterTextChanged(Editable s) {

                int len = mProductUnitPriceEditView.getText().toString().length();
                BigDecimal mUnitPriceDecimal = new BigDecimal("0");
                if (mProductUnitPriceEditView.getText().toString().equals("") || (len == 1 && mProductUnitPriceEditView.getText().toString().equals("."))) {
                    mUnitPriceDecimal = new BigDecimal("0");
                } else {
                    mUnitPriceDecimal = new BigDecimal(mProductUnitPriceEditView.getText().toString());
                }
                Log.e(TAG, "afterTextChanged  s = " + mProductUnitPriceEditView.getText().toString() + " -- " + s.toString());
                modifyProductData(sku, mUnitPriceDecimal.toPlainString(), 1);

                Log.e(TAG, "mProductUnitPriceEditView" + String.valueOf(mProductUnitPriceEditView.hasFocus()));

                if (mProductUnitPriceEditView.hasFocus()) {
                    String totalPriceString = "0";
                    String countString = countEditText.getText().toString();
                    if (countString.equals("")) {
                        countString = "0";
                    }
                    BigDecimal totalPriceDecimal = new BigDecimal(totalPriceString);
                    BigDecimal countDecimal = new BigDecimal(countString);
                    totalPriceDecimal = countDecimal.multiply(mUnitPriceDecimal);
                    BigDecimal bigDecimal = new BigDecimal(totalPriceDecimal.toPlainString()).setScale(2, BigDecimal.ROUND_HALF_UP);
                    mTotalPriceEditText.setText(bigDecimal.toPlainString());
                }

                totalMoneyTextView.setText("合计：¥" + cartModel.getTotalMoneyByStock());
                if (cartModel.size() == 1 && s.toString().equals("0")) {
                    setAddOrderButtonUnEnable();
                } else {
                    setAddOrderButtonEnable();
                }
            }
        });

        totalCountTextView.setText("共" + String.valueOf(cartModel.size()) + "种货品");
        BigDecimal big1 = new BigDecimal("0");
        try {
            big1 = new BigDecimal(sku.getStockPrice()).setScale(2, BigDecimal.ROUND_HALF_UP);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        mProductUnitPriceEditView.setText(big1.toPlainString());
        mProductNameTextView.setText(sku.getSkuName() + "    ");
        BigDecimal cartDecimal = new BigDecimal("1");
        try {
            cartDecimal = new BigDecimal(sku.getSaleAmount());
        } catch (Exception e) {
            e.printStackTrace();
        }
        countEditText.setText(cartDecimal.toPlainString());
        countEditText.setFocusable(true);
        countEditText.setFocusableInTouchMode(true);
        countEditText.selectAll();
        child.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showNormalDialog(child, preCart, sku);
                return true;
            }
        });

        countEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    final String value = countEditText.getText().toString();
                    if (value.endsWith(".")) {
                        countEditText.setText(value.replace(".", ""));
                    }
                }
            }
        });

        countEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.e(TAG, "onTextChanged before decimalFilter s = " + s);
                decimalFilter(s, 2, 10, countEditText);
                Log.e(TAG, "onTextChanged after decimalFilter s = " + countEditText.getText().toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                BigDecimal countDecimal = new BigDecimal("0");
                if (countEditText.getText().toString().equals("")){
                    countDecimal = new BigDecimal("0");
                }else {
                    countDecimal = new BigDecimal(countEditText.getText().toString());
                }
                if (countDecimal.compareTo(new BigDecimal("0")) == 0) {
                    mProductUnitPriceEditView.setEnabled(false);
                    mProductUnitPriceEditView.setTextColor(getResources().getColor(R.color.gray_very_light));
                    mTotalPriceEditText.setEnabled(false);
                    mTotalPriceEditText.setTextColor(getResources().getColor(R.color.gray_very_light));
                } else {
                    mProductUnitPriceEditView.setEnabled(true);
                    mProductUnitPriceEditView.setTextColor(getResources().getColor(R.color.black));
                    mTotalPriceEditText.setEnabled(true);
                    mTotalPriceEditText.setTextColor(getResources().getColor(R.color.black));


                    String totalPriceString = "0";
                    String priceString = mProductUnitPriceEditView.getText().toString();
                    BigDecimal totalPriceDecimal = new BigDecimal(totalPriceString);
                    BigDecimal priceDecimal = new BigDecimal(priceString);
                    totalPriceDecimal = countDecimal.multiply(priceDecimal);
                    BigDecimal bigDecimal = new BigDecimal(totalPriceDecimal.toPlainString()).setScale(2, BigDecimal.ROUND_HALF_UP);
                    mTotalPriceEditText.setText(bigDecimal.toPlainString());

                }
                int len = countEditText.getText().toString().length();
                BigDecimal mCountDecimal = new BigDecimal("0");
                if (countEditText.getText().toString().equals("") || (len == 1 && countEditText.getText().toString().equals("."))) {
                    mCountDecimal = new BigDecimal("0");
                } else {
                    mCountDecimal = new BigDecimal(countEditText.getText().toString());
                }
                Log.e(TAG, "afterTextChanged  s = " + countEditText.getText().toString() + " -- " + s.toString());
                modifyProductData(sku, mCountDecimal.toPlainString());
                if (mCountDecimal.compareTo(new BigDecimal("1")) == -1) {
                    minus.setEnabled(false);
                } else {
                    minus.setEnabled(true);
                }

                totalMoneyTextView.setText("合计：¥" + cartModel.getTotalMoneyByStock());
                if (cartModel.size() == 1 && len == 0) {
                    setAddOrderButtonUnEnable();
                } else {
                    setAddOrderButtonEnable();
                }
            }
        });

        minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BigDecimal countDecimal = new BigDecimal("0");
                if (!countEditText.getText().toString().equals("")) {
                    countDecimal = new BigDecimal(countEditText.getText().toString());
                } else {
                    countDecimal = new BigDecimal("0");
                }
                try {
                    countDecimal = countDecimal.subtract(new BigDecimal("1"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (countDecimal.compareTo(new BigDecimal("0")) == -1) {
                    countDecimal = new BigDecimal("0");
                }
                countEditText.setText(countDecimal.toPlainString());
            }
        });

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BigDecimal countDecimal = new BigDecimal("0");
                if (!countEditText.getText().toString().equals("")) {
                    countDecimal = new BigDecimal(countEditText.getText().toString());
                } else {
                    countDecimal = new BigDecimal("0");
                }
                try {
                    countDecimal = countDecimal.add(new BigDecimal("1"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                countEditText.setText(countDecimal.toPlainString());
            }
        });

        String totalPriceString = "0";
        String priceString = mProductUnitPriceEditView.getText().toString();
        String countString = countEditText.getText().toString();
        BigDecimal totalPriceDecimal = new BigDecimal(totalPriceString);
        BigDecimal countDecimal = new BigDecimal(countString);
        BigDecimal priceDecimal = new BigDecimal(priceString);
        totalPriceDecimal = countDecimal.multiply(priceDecimal);
        mTotalPriceEditText.setText(totalPriceDecimal.toPlainString());
    }

    /**
     * 弹出删除商品的对话框
     *
     * @param view    待删除的view
     * @param preCart 该view的数据
     */

    private void showNormalDialog(final View view, final PreCart preCart, final SKU sku) {
        /* @setIcon 设置对话框图标
         * @setTitle 设置对话框标题
         * @setMessage 设置对话框消息提示
         * setXXX方法返回Dialog对象，因此可以链式设置属性
         */
        final AlertDialog.Builder normalDialog =
                new AlertDialog.Builder(PurchaseActivity.this);
        //normalDialog.setIcon(R.drawable.icon_dialog);
        normalDialog.setTitle("删除商品");
        normalDialog.setMessage("是否删除该条商品？");
        normalDialog.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        removeProductItemController(view, preCart, sku);
                    }
                });
        normalDialog.setNegativeButton("关闭",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //...To-do
                    }
                });
        // 显示
        normalDialog.show();
    }

    /**
     * 弹出是否退出当前页面的对话框
     */
    private void showNormalDialog() {
        /* @setIcon 设置对话框图标
         * @setTitle 设置对话框标题
         * @setMessage 设置对话框消息提示
         * setXXX方法返回Dialog对象，因此可以链式设置属性
         */
        final AlertDialog.Builder normalDialog =
                new AlertDialog.Builder(PurchaseActivity.this);
        //normalDialog.setIcon(R.drawable.icon_dialog);
        normalDialog.setMessage("确认退出当前开采购单流程?");
        normalDialog.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
//                        addCartItemToDb();
                        scanerRespManager.setType(ScanerRespManager.ScanerRespType.SCANER_NULL_TYPE);
                        cartModel.clear();
                        finish();
                    }
                });
        normalDialog.setNegativeButton("取消",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        // 显示
        normalDialog.show();
    }

    /**
     * 初始化 更多 popupWindow
     */
    private void initCartMorePopupWindow() {
        final View cartMorePopupModelWindowView = LayoutInflater.from(this).inflate(R.layout.popup_cart_management_more_select, null, false);
        final ListView cartMorePopupListView = (ListView) cartMorePopupModelWindowView.findViewById(R.id.cart_management_more_listView);

        mCartMoreAdapter = new CartMoreAdapter(this, mCartMoreList);
        cartMorePopupListView.setAdapter(mCartMoreAdapter);

        cartMorePopupWindow = new PopupWindow(cartMorePopupModelWindowView, ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, true);

        cartMorePopupListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                cartMorePopupWindow.dismiss();
                if (position == 0) {
                    deleteCartOrder(defaultPreCartItemList);
                    cartModel.clear();
                    scanerRespManager.setType(ScanerRespManager.ScanerRespType.SCANNER_RESP_DEFAULT);
                    finish();
                } else if (position == 1) {
                    cartModel.clear();
                    clearProduct();
                    judgeButtonEnable();
                }
            }
        });

        cartMorePopupWindow.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.transaction_shop_headview_divider_line_bg_color)));
        cartMorePopupModelWindowView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (cartMorePopupModelWindowView != null && cartMorePopupModelWindowView.isShown()) {
                    cartMorePopupWindow.dismiss();
                    cartMorePopupWindow = null;
                }
                return false;
            }
        });
    }

    public void menuClick(View view) {
        final PopupWindowUtil popupWindow = new PopupWindowUtil(context, wareHouseArrayList);
        popupWindow.notifyData();
        int currentPosition = getCurrentWareHousePosition();
        if (currentPosition != -1) {
            popupWindow.setListViewLocation(currentPosition);
        }
        popupWindow.setItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                popupWindow.clearAllSelected();
                wareHouseArrayList.get(position).setSelected(true);
                currentWareHouse = wareHouseArrayList.get(position);
                cartModel.setWarehouseId(String.valueOf(currentWareHouse.getWareHouseId()));
                cartModel.setWarehouseName(currentWareHouse.getWareHouseName());
                popupWindow.dismiss();
                setWareHouseTextView(wareHouseArrayList.get(position));
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
                if (wareHouseArrayList.get(i).isSelected()) {
                    return i;
                }
            }
            return -1;
        }
    }

    private void setWareHouseTextView(WareHouse wareHouse) {
        wareHouseTextView.setText(wareHouse.getWareHouseName());
    }

    public void setAddOrderButtonEnable() {
        drawable.setShape(GradientDrawable.RECTANGLE); // 画框
        drawable.setStroke(1, getResources().getColor(R.color.transaction_tab_selected_bg_color)); // 边框粗细及颜色
        drawable.setColor(getResources().getColor(R.color.transaction_tab_selected_bg_color)); // 边框内部颜色
        saveButton.setEnabled(true);
        saveButton.setBackground(drawable);
        saveButton.setTextColor(getResources().getColor(R.color.white));
    }

    public void setAddOrderButtonUnEnable() {
        drawable.setShape(GradientDrawable.RECTANGLE); // 画框
        drawable.setStroke(1, getResources().getColor(R.color.transaction_precart_normal_bg_colcor)); // 边框粗细及颜色
        drawable.setColor(getResources().getColor(R.color.transaction_precart_normal_bg_colcor)); // 边框内部颜色
        saveButton.setEnabled(false);
        saveButton.setBackground(drawable);
        saveButton.setTextColor(getResources().getColor(R.color.white));
    }

    public static void decimalFilter(CharSequence s, int decimalLength, int integerLegth, EditText editText) {
        if (integerLegth > 0) {
            if (!s.toString().contains(".")) {
                if (s.length() > integerLegth) {
                    s = s.toString().subSequence(0, integerLegth);
                    editText.setText(s);
                    editText.setSelection(s.length());
                }
            }
        }
        if (s.toString().contains(".")) {
            if (s.length() - 1 - s.toString().indexOf(".") > decimalLength) {
                s = s.toString().subSequence(0,
                        s.toString().indexOf(".") + decimalLength + 1);
                editText.setText(s);
                editText.setSelection(s.length());
            }
        }
        if (s.toString().trim().substring(0).equals(".")) {
            s = "0" + s;
            editText.setText(s);
            editText.setSelection(2);
        }

        if (s.toString().startsWith("0")
                && s.toString().trim().length() > 1) {
            if (!s.toString().substring(1, 2).equals(".")) {
                editText.setText(s.subSequence(0, 1));
                editText.setSelection(1);
                return;
            }
        }
    }
}
