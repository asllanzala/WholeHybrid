package com.honeywell.wholesale.ui.priceDiff;

import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.honeywell.hybridapp.framework.log.LogHelper;
import com.honeywell.wholesale.R;
import com.honeywell.wholesale.framework.http.NativeJsonResponseListener;
import com.honeywell.wholesale.framework.http.WebClient;
import com.honeywell.wholesale.framework.model.AccountManager;
import com.honeywell.wholesale.framework.model.PreCart;
import com.honeywell.wholesale.framework.model.SKU;
import com.honeywell.wholesale.ui.base.BaseActivity;
import com.honeywell.wholesale.ui.base.BaseTextView;
import com.honeywell.wholesale.ui.priceDiff.request.ProductDetailPriceDiffRequest;
import com.honeywell.wholesale.ui.scan.network.ProductDetailRequest;
import com.honeywell.wholesale.ui.transaction.preorders.CartModel;
import com.honeywell.wholesale.ui.transaction.preorders.search.PreTransactionSkuActivity;

import org.json.JSONObject;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class PriceDiffSkuSelectActicity extends BaseActivity {

    private static final String TAG = PreTransactionSkuActivity.class.getSimpleName();

    private GradientDrawable drawable;
    private GradientDrawable drawable1;

    private RelativeLayout blankRelativeLayout;

    private LinearLayout skuProductContainerSection;

    private Button preContinueButton;

    private ImageView mPoductImageView;

    private BaseTextView mProductNameTextView;
    private BaseTextView mProductStockTextView;
    private BaseTextView mProductPriceTextView;
    private BaseTextView mProductSaleAmountTextView;
    private BaseTextView mProductSaleMoneyTextView;

    private CartModel cartModel = CartModel.getInstance();

    private PreCart currentPreCart;

    private ArrayList<SKU> skuList;

    private int productId;
    private int wareHouseId;

    private String url;
    private String productName;
    private String productStock;
    private String productPrice;
    private String productUnit;

    private String shopId = AccountManager.getInstance().getCurrentShopId();
    private String employeeId = AccountManager.getInstance().getCurrentAccount().getEmployeeId();
    private String userName = AccountManager.getInstance().getCurrentAccount().getUserName();
    private String shopName = AccountManager.getInstance().getCurrentAccount().getShopName();

    private WebClient webClient;
    private ProductDetailPriceDiffRequest productDetailRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_price_diff_sku_select);
        initData();
        initView();
        productSale(productId);
        //addSkuView();
    }

    /**
     * 初始化View
     */
    private void initView() {
        drawable = new GradientDrawable();
        drawable1 = new GradientDrawable();

        blankRelativeLayout = (RelativeLayout) findViewById(R.id.sku_activity_blank_layout);
        blankRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PriceDiffSkuSelectActicity.this.finish();
            }
        });

        skuProductContainerSection = (LinearLayout) findViewById(R.id.sku_product_containner);

        preContinueButton = (Button) findViewById(R.id.order_to_cart_button);
        preContinueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cartModel.checkProductExist(currentPreCart.getProductId())){
                    if (currentPreCart.getSkuList().isEmpty()){
                        cartModel.remove(currentPreCart);
                    }else {
                        cartModel.modifyProductData(currentPreCart);
                    }
                }else {
                    if (!currentPreCart.getSkuList().isEmpty()) {
                        cartModel.add(currentPreCart);
                    }
                }
                finish();
            }
        });

        mPoductImageView = (ImageView) findViewById(R.id.sku_pic_imageview);

        Glide
                .with(getApplicationContext())
                .load(url)
                .placeholder(R.drawable.inventory_default_icon)
                .error(R.drawable.inventory_default_icon)
                .priority(Priority.LOW)
                .into(mPoductImageView);

        mProductNameTextView = (BaseTextView) findViewById(R.id.sku_product_name);
        mProductNameTextView.setText(productName);

        mProductStockTextView = (BaseTextView) findViewById(R.id.sku_product_stock);
        mProductStockTextView.setVisibility(View.GONE);
        mProductStockTextView.setText("库存 ");

        mProductPriceTextView = (BaseTextView) findViewById(R.id.sku_product_price);
        mProductPriceTextView.setText("¥ " + productPrice);

        mProductSaleAmountTextView = (BaseTextView) findViewById(R.id.total_amount_text_view);
        mProductSaleAmountTextView.setVisibility(View.GONE);
        mProductSaleAmountTextView.setText(currentPreCart.getTotalNumber());

        mProductSaleMoneyTextView = (BaseTextView) findViewById(R.id.total_money_text_view);
        mProductSaleMoneyTextView.setVisibility(View.GONE);
        mProductSaleMoneyTextView.setText("¥ " + getTotalMoney());

        setAddOrderButtonEnable();
    }

    private void initData() {
        webClient = new WebClient();
        productDetailRequest = new ProductDetailPriceDiffRequest();
        Intent intent = getIntent();
        url = intent.getStringExtra("productPic");
        productId = intent.getIntExtra("productId", -1);
        productName = intent.getStringExtra("productName");
        productStock = intent.getStringExtra("productStock");
        productPrice = intent.getStringExtra("productPrice");
        productUnit = intent.getStringExtra("productUnit");
        wareHouseId = intent.getIntExtra("wareHouseId", 0);
        if (url == null) {
            url = "";
        }

        if (url.equals("")) {
            url = String.valueOf(R.drawable.inventory_default_icon);
        }
        currentPreCart = new PreCart();
        currentPreCart = cartModel.getById(productId);
        if (currentPreCart == null){
            currentPreCart = new PreCart(productId, productName, productPrice, url, productUnit, productStock);
        }

//        ArrayList<SKU> arraylist = new ArrayList<SKU>();
//        arraylist.add(new SKU(6, "紫", "L", "30G", "30"));
//
//        currentPreCart.setSkuList(arraylist);
//
        skuList = new ArrayList<SKU>();
//        skuList.add(new SKU(1, "红", "L", "30G"));
//        skuList.add(new SKU(2, "蓝", "L", "30G"));
//        skuList.add(new SKU(3, "白", "L", "30G"));
//        skuList.add(new SKU(4, "黄", "L", "30G"));
//        skuList.add(new SKU(5, "绿", "L", "30G"));
//        skuList.add(new SKU(6, "紫", "L", "30G"));

    }

    /**
     * 初始化cart数据
     */
    private void addSkuView() {
        for (SKU sku : skuList) {
            addProductItemController(sku);
        }
    }

    /**
     * 添加一条商品订单
     *
     * @param sku
     */
    private void addProductItemController(SKU sku) {
        int i = getExistItemPosition(sku.getSkuId());
        if (i == -1) {
            addProductSubView(sku);
        } else {
            if (currentPreCart != null) {
                sku.setSaleAmount(currentPreCart.getSkuList().get(i).getSaleAmount());
                addProductSubView(sku);
            }
        }
    }

    /**
     * 获取当前SKU的position
     *
     * @param skuId
     * @return
     */
    private int getExistItemPosition(int skuId) {
        if (currentPreCart != null) {
            for (int i = 0; i < currentPreCart.getSkuList().size(); i++) {
                if (currentPreCart.getSkuList().get(i).getSkuId() == skuId) {
                    return i;
                }
            }
        }
        return -1;
    }

    private String getTotalMoney(){
        float totalMoney= 0;
        for (int i = 0;i<currentPreCart.getSkuList().size();i++){
            totalMoney += (Integer.valueOf(currentPreCart.getSkuList().get(i).getSaleAmount()) *
                    Float.valueOf(currentPreCart.getSkuList().get(i).getStandardPrice()));
        }
        return String.valueOf(totalMoney);
    }

    /**
     * 新增购物车商品的数据
     *
     * @param sku
     */
    private void addProductData(SKU sku, String number) {
        if (currentPreCart != null) {
            sku.setSaleAmount(number);
            currentPreCart.getSkuList().add(sku);
        }
    }

    /**
     * 修改购物车商品的数量数据
     *
     * @param sku
     * @param number
     */
    private void modifyProductData(SKU sku, String number) {
        if (currentPreCart != null) {
            int i = getExistItemPosition(sku.getSkuId());
            if (i == -1) {
                if (!number.equals("0")){
                    addProductData(sku, number);
                }
            }else {
                if (number.equals("0")){
                    deleteProductData(sku);
                }else {
                    currentPreCart.getSkuList().get(i).setSaleAmount(number);
                }
            }
            currentPreCart.setTotalNumber();
        }
    }

    /**
     * 删除购物车商品的数据
     *
     * @param sku
     */
    private void deleteProductData(SKU sku) {
        if (currentPreCart != null) {
            for (int i = 0; i<currentPreCart.getSkuList().size() ;i++){
                if (currentPreCart.getSkuList().get(i).getSkuId() == sku.getSkuId()){
                    currentPreCart.getSkuList().remove(currentPreCart.getSkuList().get(i));
                    sku.setSaleAmount("0");
                }
            }
        }
    }


    /**
     * 修改某一条购物车中商品的view，用于新增已存在于购物车中的商品
     *
     * @param i
     * @param totalNumber
     */
    private void modifyProductSubView(int i, String totalNumber) {
        View view = skuProductContainerSection.getChildAt(i);
        final EditText countEditText = (EditText) view.findViewById(R.id.product_no_text_view);
        countEditText.setText(totalNumber);

    }

    /**
     * 删除某一条购物车中商品
     *
     * @param view
     * @param sku
     */
    private void removeProductItemController(View view, SKU sku) {
        deleteProductData(sku);
        removeProductSubView(view);
        if (currentPreCart.getSkuList().isEmpty()) {
            finish();
        }
    }

    /**
     * 删除某一条购物车中商品的view
     *
     * @param view
     */
    private void removeProductSubView(View view) {
        skuProductContainerSection.removeView(view);
    }

    private String doubleTrans(float d) {
        if (Math.round(d) - d == 0) {
            return String.valueOf((long) d);
        }
        return String.valueOf(d);
    }

    /**
     * 新增某一条购物车中商品的view
     *
     * @param sku
     */
    private void addProductSubView(final SKU sku) {
        final View child = getLayoutInflater().inflate(R.layout.item_select_sku, null);
        skuProductContainerSection.addView(child);

        final EditText countEditText = (EditText) child.findViewById(R.id.sku_product_no_edittext);
        countEditText.setVisibility(View.GONE);
        final BaseTextView mProductPriceTextView = (BaseTextView) child.findViewById(R.id.sku_product_unit_price);
        final BaseTextView mProductNameTextView = (BaseTextView) child.findViewById(R.id.sku_product_name);
        final BaseTextView mProductStockQuantityTextView = (BaseTextView) child.findViewById(R.id.sku_product_stock_quantity);
        final View minus = child.findViewById(R.id.sku_product_no_minus);
        minus.setVisibility(View.GONE);
        final View add = child.findViewById(R.id.sku_product_no_add);
        add.setVisibility(View.GONE);

        final SKU sku1 = sku;
        child.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int cartItemNo = 1;
                try {
                    cartItemNo = Integer.valueOf(sku.getSaleAmount());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (cartItemNo == 0){
                    cartItemNo =1;
                    modifyProductData(sku, String.valueOf(cartItemNo));
                    child.setBackground(getResources().getDrawable(R.color.wholesale_base_skin_color));
                }else {
                    cartItemNo = 0;
                    modifyProductData(sku, String.valueOf(cartItemNo));
                    child.setBackground(getResources().getDrawable(R.color.white));
                }
            }
        });
        mProductStockQuantityTextView.setText("库存：" + sku.getQuantity());
        mProductStockQuantityTextView.setVisibility(View.GONE);
        mProductNameTextView.setText(sku.getSkuName() + "    ");
        BigDecimal big1 = new BigDecimal(sku.getStandardPrice());
        DecimalFormat format = new DecimalFormat("##0.00");
        String value = format.format(big1);
        mProductPriceTextView.setText("¥  " + value);

        int cartItemNo = 0;
        try {
            cartItemNo = Integer.valueOf(sku.getSaleAmount());
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (cartItemNo == 0){
            child.setBackground(getResources().getDrawable(R.color.white));
        }else {
            child.setBackground(getResources().getDrawable(R.color.wholesale_base_skin_color));
        }
    }



    /**
     * 根据productId到server获取商品详情
     *
     * @param productId
     */
    private void productSale(final int productId) {
        productDetailRequest = new ProductDetailPriceDiffRequest(productId, shopId);
        webClient.httpSkuProductPriceDiffDetail(productDetailRequest, new NativeJsonResponseListener<JSONObject>() {
            @Override
            public void listener(JSONObject jsonObject) {
//                JSONObject jsonObject1 = jsonObject.optJSONObject("inventory_list");
//                skuList = SKU.fromJson2List(jsonObject1.toString());
                String inventoryList = jsonObject.optString("inventory_list");
                Log.e("inventoryList", inventoryList);
                String totalQuantity = jsonObject.optString("total_quantity");
                mProductStockTextView.setText("库存 " + totalQuantity + " " + productUnit);
                skuList = SKU.fromJson2List(inventoryList);
                addSkuView();
                Log.e("inventoryList", inventoryList);
            }

            @Override
            public void errorListener(String s) {
                LogHelper.getInstance().e(TAG, "查询货品详情失败：" + s);
                Toast.makeText(getApplicationContext(), "查询货品详情失败", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void setAddOrderButtonEnable() {
        drawable.setShape(GradientDrawable.RECTANGLE); // 画框
        drawable.setStroke(1, getResources().getColor(R.color.transaction_tab_selected_bg_color)); // 边框粗细及颜色
        drawable.setColor(getResources().getColor(R.color.transaction_tab_selected_bg_color)); // 边框内部颜色
        preContinueButton.setEnabled(true);
        preContinueButton.setBackground(drawable);
        preContinueButton.setTextColor(getResources().getColor(R.color.white));
    }
}
