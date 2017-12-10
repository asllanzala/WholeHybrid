package com.honeywell.wholesale.ui.transaction.preorders;

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
import android.widget.Toast;

import com.honeywell.wholesale.R;
import com.honeywell.wholesale.framework.application.WholesaleApplication;
import com.honeywell.wholesale.framework.event.PayRIghtNowEvent;
import com.honeywell.wholesale.framework.event.PreCartToCartEvent;
import com.honeywell.wholesale.framework.model.PreCart;
import com.honeywell.wholesale.framework.model.SKU;
import com.honeywell.wholesale.ui.base.BaseActivity;
import com.honeywell.wholesale.ui.base.BaseTextView;
import com.honeywell.wholesale.ui.inventory.ProductShipmentActivity;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import de.greenrobot.event.EventBus;


/**
 * Created by H154326 on 17/3/14.
 * Email: yang.liu6@honeywell.com
 */

public class PreTransactionCartActivity extends BaseActivity {

    private static final String TAG = PreTransactionCartActivity.class.getSimpleName();

    private static final Integer PRECART_VIEW_TAG = 1;
    private static final Integer SKU_VIEW_TAG = 2;


    private static final Integer TITLE_VIEW_TAG_SKU_KEY = -1;
    private static final Integer NO_SKU_VIEW_TAG_KEY = -2;

    private ImageView backImageView;
    private ImageView moreImageView;

    private BaseTextView preCartTitleTextView;
    private BaseTextView moreTextView;

    private Button preContinueButton;
    private Button addOrderButton;

    private LinearLayout productContainerSection;

    private GradientDrawable drawable;
    private GradientDrawable drawable1;

    private String titleName;

    private CartModel cartModel = CartModel.getInstance();

    private ArrayList<PreCart> preCartArrayList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pre_transaction_cart);
        initData();
        initView();
        addCartView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);//反注册EventBus
        WholesaleApplication.setCurrentCustmerId(null);
    }

    public void onEventMainThread(PayRIghtNowEvent event) {
        String msg = "CartManagementActivity onEventMainThread收到了消息：" + event.getmMsg();
        Log.e(TAG, msg);
        if (event.getmMsg().equals(ProductShipmentActivity.INTENT_KEY_PAY_RIGHT_NOW_EVENT)){
            finish();
        }
    }

    private void initData(){
        preCartArrayList = new ArrayList<>();
    }

    /**
     * 初始化View
     */
    private void initView() {
        drawable = new GradientDrawable();
        drawable1 = new GradientDrawable();
        moreTextView = (BaseTextView) findViewById(R.id.cart_management_more_textview);
        moreTextView.setVisibility(View.GONE);
        moreImageView = (ImageView) findViewById(R.id.cart_management_more_imageview);
        moreImageView.setVisibility(View.GONE);
        productContainerSection = (LinearLayout) findViewById(R.id.pre_cart_product_containner);

        addOrderButton = (Button) findViewById(R.id.pre_order_to_cart_button);
        addOrderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getDefault().post(new PreCartToCartEvent("to cart"));
                finish();
            }
        });

        preContinueButton = (Button) findViewById(R.id.pre_continue_button);
        preContinueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        backImageView = (ImageView) findViewById(R.id.cart_management_back_imageView);
        backImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        titleName = "已选择商品";
        preCartTitleTextView = (BaseTextView) findViewById(R.id.cart_management_title_textview);
        preCartTitleTextView.setText(titleName);

        setAddOrderButtonUnEnable();
        setAddOrderButtonEnable();

    }

    /**
     * 初始化cart数据
     */
    private void addCartView() {
        for (int i = 0; i < cartModel.size(); i++){
            addProductTitleView(cartModel.get(i));
            for (int j = 0; j < cartModel.get(i).getSkuList().size(); j++) {
                addProductSubView(cartModel.get(i), cartModel.get(i).getSkuList().get(j));
            }
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

    /**
     * 新增购物车商品的数据
     *
     * @param preCart
     */
    private void addProductData(PreCart preCart) {
        cartModel.add(preCart);
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
        if (cartModel.getById(preCart.getProductId()) != null) {
            cartModel.getById(preCart.getProductId()).deleteSkuItem(sku);
            if (cartModel.getById(preCart.getProductId()).getSkuList().isEmpty()) {
                deleteProductData(preCart);
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
        View view = productContainerSection.getChildAt(i);
        final EditText countEditText = (EditText) view.findViewById(R.id.product_no_text_view);
        countEditText.setText(totalNumber);

    }

    private View getViewByTag(int tagKey){
        for (int i = 0; i<productContainerSection.getChildCount(); i++){
            if ( tagKey == Integer.valueOf(productContainerSection.getChildAt(i).getTag(R.id.precart_view_tag).toString())){
                return productContainerSection.getChildAt(i);
            }
        }
        return null;
    }

    private View getBeforeViewByTag(int tagKey){
        for (int i = 0; i<productContainerSection.getChildCount(); i++){
            if ( tagKey == Integer.valueOf(productContainerSection.getChildAt(i).getTag(R.id.sku_view_tag).toString())){
                return productContainerSection.getChildAt(i-1);
            }
        }
        return null;
    }

    private void setViewTag(View view, PreCart preCart){
        view.setTag(R.id.precart_view_tag, preCart.getProductId());
        view.setTag(R.id.sku_view_tag, TITLE_VIEW_TAG_SKU_KEY);
    }

    private void setViewTag(View view, PreCart preCart, SKU sku){
        view.setTag(R.id.precart_view_tag, preCart.getProductId());
        if (sku == null){
            view.setTag(R.id.sku_view_tag, preCart.getProductId());
        }else {
            view.setTag(R.id.sku_view_tag, sku.getSkuId());
        }
    }

    private int getViewTag(View view, int tagType){
        if (PRECART_VIEW_TAG == tagType ){
            return Integer.valueOf(view.getTag(R.id.precart_view_tag).toString());
        }else if (SKU_VIEW_TAG == tagType ){
            return Integer.valueOf(view.getTag(R.id.sku_view_tag).toString());
        }
        return -100;
    }

    /**
     * 删除某一条购物车中商品
     *
     * @param view
     * @param preCart
     */
    private void removeProductItemController(View view, PreCart preCart, SKU sku) {
        deleteProductData(preCart, sku);
        int preCartTag = preCart.getProductId();
        removeProductSubView(view);

        if (cartModel.getById(preCart.getProductId()) == null){
            View view1 = getViewByTag(preCartTag);
            removeProductSubView(view1);
        }
        if (cartModel.isEmpty()){
            finish();
        }
    }


    /**
     * 删除某一条购物车中商品的view
     *
     * @param view
     */
    private void removeProductSubView(View view) {
        productContainerSection.removeView(view);
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
     * @param preCart
     */

    private void addProductSubView(final PreCart preCart, final SKU sku) {
        final View child = getLayoutInflater().inflate(R.layout.item_pre_cart, null);

        setViewTag(child, preCart, sku);

        productContainerSection.addView(child);

        final EditText countEditText = (EditText) child.findViewById(R.id.pre_cart_product_no_edittext);
        final BaseTextView mProductNameTextView = (BaseTextView) child.findViewById(R.id.pre_cart_product_sku);
        final BaseTextView mProductPriceTextView = (BaseTextView) child.findViewById(R.id.pre_cart_product_unit_price);
        final BaseTextView mProductStockQuantityTextView = (BaseTextView) child.findViewById(R.id.pre_cart_product_stock_quantity);
        final View minus = child.findViewById(R.id.pre_cart_product_no_minus);
        final View add = child.findViewById(R.id.pre_cart_product_no_add);

        mProductNameTextView.setText(sku.getSkuName());
        mProductStockQuantityTextView.setText("库存：" + sku.getQuantity());
        BigDecimal big1 = new BigDecimal(sku.getStandardPrice());
        DecimalFormat format = new DecimalFormat("##0.00");
        String value = format.format(big1);
        mProductPriceTextView.setText("¥  " + value);

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
                BigDecimal countNumberDecimal = new BigDecimal("0");
                if (countEditText.getText().toString().equals("") || (len == 1 && countEditText.getText().toString().equals("."))) {
                    countNumberDecimal = new BigDecimal("0");
                } else {
                    countNumberDecimal = new BigDecimal(countEditText.getText().toString());
                }
                if (countNumberDecimal.compareTo(new BigDecimal("1")) == -1) {
                    minus.setEnabled(false);
                } else {
                    minus.setEnabled(true);
                }

                if (countNumberDecimal.compareTo(new BigDecimal(sku.getQuantity())) == 1) {
                    countEditText.removeTextChangedListener(this);//先移除监听，再设置文本，否则会无限死循环
                    countEditText.setText(sku.getQuantity());//对输入框文本进行分段显示
                    countEditText.addTextChangedListener(this);
//                    s.clear();
//                    s.append(String.valueOf(Integer.valueOf(preCart.getStockQuantity())));
                    countNumberDecimal = new BigDecimal(sku.getQuantity());
//                    countNumber = Integer.valueOf(sku.getQuantity());
                    Toast.makeText(getApplicationContext(), R.string.pre_order_sale_number, Toast.LENGTH_SHORT).show();
                } else {
                    add.setEnabled(true);
                }

                modifyProductData(sku, countNumberDecimal.toPlainString());

                countEditText.setSelection(countEditText.getText().length());

                if (countNumberDecimal.compareTo(new BigDecimal("0")) == 0){
                    removeProductItemController(child, preCart, sku);
                }
            }
        });

        minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BigDecimal countNumberDecimal = new BigDecimal("0");
//                int count = 0;
                if (!countEditText.getText().toString().equals("")) {
                    countNumberDecimal = new BigDecimal(countEditText.getText().toString());
//                    count = Integer.valueOf(countEditText.getText().toString());
                } else {
                    countNumberDecimal = new BigDecimal("0");
//                    count = 0;
                }

                try {
                    countNumberDecimal = countNumberDecimal.subtract(new BigDecimal("1"));
//                    count = count - 1;
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (countNumberDecimal.compareTo(new BigDecimal("0")) == -1){
                    countNumberDecimal = new BigDecimal("0");
                }

                countEditText.setText(countNumberDecimal.toPlainString());
            }
        });

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BigDecimal countNumberDecimal = new BigDecimal("0");
//                int count = 0;
                if (!countEditText.getText().toString().equals("")) {
                    countNumberDecimal = new BigDecimal(countEditText.getText().toString());
                } else {
                    countNumberDecimal = new BigDecimal("0");
                }

                try {
                    countNumberDecimal = countNumberDecimal.add(new BigDecimal("1"));
                } catch (Exception e) {
                    e.printStackTrace();
                }

                countEditText.setText(countNumberDecimal.toPlainString());
            }
        });
    }

    private void addProductTitleView(final PreCart preCart) {
        final View child = getLayoutInflater().inflate(R.layout.item_pre_cart_title, null);

        setViewTag(child, preCart);

        productContainerSection.addView(child);

        final BaseTextView mProductNameTextView = (BaseTextView) child.findViewById(R.id.sku_product_title_text);

        mProductNameTextView.setText(preCart.getProductName() + "    ");
    }

    /**
     * 修改购物车商品的数据
     *
     * @param preCart
     * @param price
     * @param judge
     */
    private void modifyProductData(PreCart preCart, String price, int judge) {
        for (int i = 0; i < cartModel.size(); i++) {
            if (cartModel.get(i).getProductId() == preCart.getProductId()) {
                cartModel.get(i).setUnitPrice(price);
            }
        }
    }

    /**
     * 修改购物车商品的数据
     *
     * @param preCart
     * @param number
     */
    private void modifyProductData(PreCart preCart, String number) {
        for (int i = 0; i < cartModel.size(); i++) {
            if (cartModel.get(i).getProductId() == preCart.getProductId()) {
                cartModel.get(i).setTotalNumber(number);
            }
        }
    }

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

    public void setAddOrderButtonUnEnable() {
        drawable1.setShape(GradientDrawable.RECTANGLE); // 画框
        drawable1.setStroke(1, getResources().getColor(R.color.transaction_tab_selected_bg_color)); // 边框粗细及颜色
        drawable1.setColor(getResources().getColor(R.color.white)); // 边框内部颜色
        //preContinueButton.setEnabled(false);
        preContinueButton.setBackground(drawable1);
        preContinueButton.setTextColor(getResources().getColor(R.color.transaction_tab_selected_bg_color));
    }

    public void setAddOrderButtonEnable() {
        drawable.setShape(GradientDrawable.RECTANGLE); // 画框
        drawable.setStroke(1, getResources().getColor(R.color.transaction_tab_selected_bg_color)); // 边框粗细及颜色
        drawable.setColor(getResources().getColor(R.color.transaction_tab_selected_bg_color)); // 边框内部颜色
        addOrderButton.setEnabled(true);
        addOrderButton.setBackground(drawable);
        addOrderButton.setTextColor(getResources().getColor(R.color.white));
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
