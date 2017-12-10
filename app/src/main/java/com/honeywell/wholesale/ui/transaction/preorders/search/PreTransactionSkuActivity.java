package com.honeywell.wholesale.ui.transaction.preorders.search;

import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
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
import com.honeywell.wholesale.framework.utils.PointLengthFilter;
import com.honeywell.wholesale.ui.base.BaseActivity;
import com.honeywell.wholesale.ui.base.BaseTextView;
import com.honeywell.wholesale.ui.scan.network.ProductDetailRequest;
import com.honeywell.wholesale.ui.transaction.preorders.CartModel;

import org.json.JSONObject;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.util.ArrayList;

import static android.media.CamcorderProfile.get;

/**
 * Created by H154326 on 17/3/21.
 * Email: yang.liu6@honeywell.com
 */

public class PreTransactionSkuActivity extends BaseActivity {

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
    private ProductDetailRequest productDetailRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sku);
        initData();
        initView();
        productSale(productId);
        addSkuView();
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
                PreTransactionSkuActivity.this.finish();
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
        mProductStockTextView.setText("库存 ");

        mProductPriceTextView = (BaseTextView) findViewById(R.id.sku_product_price);
        mProductPriceTextView.setText("¥ " + productPrice);

        mProductSaleAmountTextView = (BaseTextView) findViewById(R.id.total_amount_text_view);
        mProductSaleAmountTextView.setText(currentPreCart.getTotalNumber());

        mProductSaleMoneyTextView = (BaseTextView) findViewById(R.id.total_money_text_view);
        mProductSaleMoneyTextView.setText("¥ " + getTotalMoney());

        setAddOrderButtonEnable();
    }

    private void initData() {
        webClient = new WebClient();
        productDetailRequest = new ProductDetailRequest();
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
        BigDecimal totalDecimal = new BigDecimal("0");
        for (int i = 0;i<currentPreCart.getSkuList().size();i++){

            BigDecimal amountDecimal = new BigDecimal(currentPreCart.getSkuList().get(i).getSaleAmount());
            BigDecimal priceDecimal = new BigDecimal(currentPreCart.getSkuList().get(i).getStandardPrice());
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
//        countEditText.setFilters(new InputFilter[]{new PointLengthFilter()});
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
        mProductNameTextView.setText(sku.getSkuName() + "    ");
        BigDecimal big1 = new BigDecimal(sku.getStandardPrice());
        DecimalFormat format = new DecimalFormat("##0.00");
        String value = format.format(big1);
        mProductPriceTextView.setText("¥  " + value);
//        mProductPriceTextView.setText("¥  " + preCart.getUnitPrice());

        BigDecimal cartItemDecimal = new BigDecimal("1");
//        int cartItemNo = 1;
        try {
            cartItemDecimal = new BigDecimal(sku.getSaleAmount());
//            cartItemNo = Integer.valueOf(sku.getSaleAmount());
        } catch (Exception e) {
            e.printStackTrace();
        }

        countEditText.setText(cartItemDecimal.toPlainString());
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
                BigDecimal mCountDecimal = new BigDecimal("0");
                if (countEditText.getText().toString().equals("") || (len == 1 && countEditText.getText().toString().equals("."))) {
                    mCountDecimal = new BigDecimal("0");
                }else {
                    mCountDecimal = new BigDecimal(countEditText.getText().toString());
                }

                BigDecimal maxQuantity = new BigDecimal(sku.getQuantity());
                //maxQuantity = maxQuantity.add(new BigInteger("1"));

                if (maxQuantity.compareTo(mCountDecimal) == -1 ) {
                    countEditText.removeTextChangedListener(this);//先移除监听，再设置文本，否则会无限死循环
                    countEditText.setText(sku.getQuantity());//对输入框文本进行分段显示
                    countEditText.addTextChangedListener(this);
                    mCountDecimal = new BigDecimal(sku.getQuantity());
                    Toast.makeText(getApplicationContext(), R.string.pre_order_sale_number, Toast.LENGTH_SHORT).show();
                } else {
                    add.setEnabled(true);
                }

                if (mCountDecimal.compareTo(new BigDecimal("1")) == -1) {
                    minus.setVisibility(View.GONE);
                } else {
                    minus.setVisibility(View.VISIBLE);
                }

                modifyProductData(sku, mCountDecimal.toPlainString());

                countEditText.setSelection(countEditText.getText().length());

                mProductSaleMoneyTextView.setText("¥  " + getTotalMoney());

                mProductSaleAmountTextView.setText(currentPreCart.getTotalNumber());


            }
        });

        minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BigDecimal countNumber = new BigDecimal("0");
//                int count = 0;
                if (!countEditText.getText().toString().equals("")) {
                    countNumber = new BigDecimal(countEditText.getText().toString());
//                    count = Integer.valueOf(countEditText.getText().toString());
                } else {
                    countNumber = new BigDecimal("0");
//                    count = 0;
                }

                try {
                    countNumber = countNumber.subtract(new BigDecimal("1"));
//                    count = count - 1;
                } catch (Exception e) {
                    e.printStackTrace();
                }


                if (countNumber.compareTo(new BigDecimal("0")) == -1) {
                    countNumber = new BigDecimal("0");
//                    count = 0;
                }

                countEditText.setText(countNumber.toPlainString());
            }
        });

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BigDecimal countNumber = new BigDecimal("0");
//                int count = 0;
                if (!countEditText.getText().toString().equals("")) {
                    countNumber = new BigDecimal(countEditText.getText().toString());
//                    count = Integer.valueOf(countEditText.getText().toString());
                } else {
                    countNumber = new BigDecimal("0");
//                    count = 0;
                }

                try {
                    countNumber = countNumber.add(new BigDecimal("1"));
//                    count = count + 1;
                } catch (Exception e) {
                    e.printStackTrace();
                }

                countEditText.setText(countNumber.toPlainString());
            }
        });
    }



    /**
     * 根据productId到server获取商品详情
     *
     * @param productId
     */
    private void productSale(final int productId) {
        productDetailRequest = new ProductDetailRequest(productId, wareHouseId);
        webClient.httpSkuProductDetail(productDetailRequest, new NativeJsonResponseListener<JSONObject>() {
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
