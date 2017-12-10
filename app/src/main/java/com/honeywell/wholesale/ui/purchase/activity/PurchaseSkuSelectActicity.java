package com.honeywell.wholesale.ui.purchase.activity;

import android.content.Context;
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
import com.honeywell.wholesale.ui.purchase.presenter.SkuSelectPresenter;
import com.honeywell.wholesale.ui.purchase.request.ProductDetailBean;
import com.honeywell.wholesale.ui.purchase.view.SkuSelectView;
import com.honeywell.wholesale.ui.scan.network.ProductDetailRequest;
import com.honeywell.wholesale.ui.transaction.preorders.CartModel;
import com.honeywell.wholesale.ui.transaction.preorders.search.PreTransactionSkuActivity;

import org.json.JSONObject;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class PurchaseSkuSelectActicity extends BaseActivity implements SkuSelectView{

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
    private ProductDetailBean productDetailBean;

    private SkuSelectPresenter skuSelectPresenter ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchase_sku_select);
        skuSelectPresenter=new SkuSelectPresenter(this);
        initData();
        initView();
        skuSelectPresenter.productSale(productId);
//        addSkuView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        skuSelectPresenter.cancelRequest();
        skuSelectPresenter.detach();
    }

    @Override
    public void onShowLoading() {

    }

    @Override
    public void onHideLoading() {

    }

    @Override
    public void onToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onIntent() {

    }

    @Override
    public void addSkuView() {
        for (SKU sku : skuList) {
            addProductItemController(sku);
        }
    }

    @Override
    public void setStockText(String totalQuantity) {
        mProductStockTextView.setText("库存 " + totalQuantity + " " + productUnit);
    }

    @Override
    public void setSkuListData(ArrayList<SKU> skuList) {
        this.skuList = skuList;
    }

    @Override
    public Context getContext() {
        return this;
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
                PurchaseSkuSelectActicity.this.finish();
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
        productDetailBean = new ProductDetailBean();
        Intent intent = getIntent();
        url = intent.getStringExtra("productPic");
        productId = intent.getIntExtra("productId", -1);
        productName = intent.getStringExtra("productName");
        productStock = intent.getStringExtra("productStock");
        productPrice = intent.getStringExtra("productPrice");
        productUnit = intent.getStringExtra("productUnit");
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

        skuList = new ArrayList<SKU>();
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
        BigDecimal totalDecimal = new BigDecimal("0");
        for (int i = 0;i<currentPreCart.getSkuList().size();i++){
            BigDecimal amountDecimal = new BigDecimal(currentPreCart.getSkuList().get(i).getSaleAmount());
            BigDecimal priceDecimal = new BigDecimal(currentPreCart.getSkuList().get(i).getStockPrice());
            BigDecimal tempDecimal = amountDecimal.multiply(priceDecimal);
            totalDecimal = totalDecimal.add(tempDecimal);
        }
        BigDecimal bigDecimal = new BigDecimal(totalDecimal.toPlainString()).setScale(2, BigDecimal.ROUND_HALF_UP);
        return bigDecimal.toPlainString();
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
                }
            }
        }
    }

    /**
     * 新增某一条购物车中商品的view
     *
     * @param sku
     */
    private void addProductSubView(final SKU sku) {
        final View child = getLayoutInflater().inflate(R.layout.item_purchase_select_sku, null);
        skuProductContainerSection.addView(child);

        final EditText countEditText = (EditText) child.findViewById(R.id.sku_product_no_edittext);
        final BaseTextView mProductPriceTextView = (BaseTextView) child.findViewById(R.id.sku_product_unit_price);
        final BaseTextView mProductNameTextView = (BaseTextView) child.findViewById(R.id.sku_product_name);
        final BaseTextView mProductStockQuantityTextView = (BaseTextView) child.findViewById(R.id.sku_product_stock_quantity);
        final View minus = child.findViewById(R.id.sku_product_no_minus);
        final View add = child.findViewById(R.id.sku_product_no_add);

        final SKU sku1 = sku;

        if (sku.getSaleAmount().equals("0")){
            minus.setVisibility(View.GONE);
        }

        mProductStockQuantityTextView.setText("库存：" + sku.getQuantity());
        mProductStockQuantityTextView.setVisibility(View.GONE);
        mProductNameTextView.setText(sku.getSkuName() + "    ");
        BigDecimal big1 = new BigDecimal(sku.getStockPrice());
        DecimalFormat format = new DecimalFormat("##0.00");
        String value = format.format(big1);
        mProductPriceTextView.setText("¥  " + value);

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
                decimalFilter(s, 2, 10, countEditText);
            }

            @Override
            public void afterTextChanged(Editable s) {
                int len = countEditText.getText().toString().length();
                BigDecimal countNumber = new BigDecimal("0");
                if (countEditText.getText().toString().equals("") || (len == 1 && countEditText.getText().toString().equals("."))) {
                    countNumber = new BigDecimal("0");
                } else {
                    countNumber =  new BigDecimal(countEditText.getText().toString());
                }

                if (countNumber.compareTo(new BigDecimal("1")) == -1) {
                    minus.setVisibility(View.GONE);
                } else {
                    minus.setVisibility(View.VISIBLE);
                }

                modifyProductData(sku, String.valueOf(countNumber));

                countEditText.setSelection(countEditText.getText().length());

                mProductSaleMoneyTextView.setText("¥  " + getTotalMoney());

                mProductSaleAmountTextView.setText(currentPreCart.getTotalNumber());


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
    }

    public void setAddOrderButtonEnable() {
        drawable.setShape(GradientDrawable.RECTANGLE); // 画框
        drawable.setStroke(1, getResources().getColor(R.color.transaction_tab_selected_bg_color)); // 边框粗细及颜色
        drawable.setColor(getResources().getColor(R.color.transaction_tab_selected_bg_color)); // 边框内部颜色
        preContinueButton.setEnabled(true);
        preContinueButton.setBackground(drawable);
        preContinueButton.setTextColor(getResources().getColor(R.color.white));
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
