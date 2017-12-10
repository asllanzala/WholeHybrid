package com.honeywell.wholesale.ui.saleReturn;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.honeywell.wholesale.R;
import com.honeywell.wholesale.framework.event.PayRIghtNowEvent;
import com.honeywell.wholesale.framework.model.AccountManager;
import com.honeywell.wholesale.framework.model.PreCart;
import com.honeywell.wholesale.framework.model.SKU;
import com.honeywell.wholesale.framework.scanner.ScanerRespManager;
import com.honeywell.wholesale.ui.base.BaseActivity;
import com.honeywell.wholesale.ui.base.BaseTextView;
import com.honeywell.wholesale.ui.saleReturn.presenter.SaleReturnPresenter;
import com.honeywell.wholesale.ui.transaction.preorders.CartModel;

import java.math.BigDecimal;
import java.text.DecimalFormat;

import de.greenrobot.event.EventBus;

public class SaleReturnOrderConfirmActivity extends BaseActivity {

    private static final Integer TITLE_VIEW_TAG_SKU_KEY = -1;

    private Button saveButton;

    private ImageView backImageView;

    private BaseTextView totalMoneyContentTextView;
    private BaseTextView adjustContentTextView;
    private BaseTextView customerContentTextView;
    private BaseTextView shopContentTextView;
    private BaseTextView employeeContentTextView;
    private BaseTextView warehouseContentTextView;
    private BaseTextView titleTextView;


    private EditText actualMoneyContentEditText;
    private EditText remarkEditText;

    private LinearLayout productContainerSection;

    private ScanerRespManager scanerRespManager;

    private CartModel cartModel = CartModel.getInstance();

    public String currentShopId = AccountManager.getInstance().getCurrentAccount().getCurrentShopId();
    public String currentShopName = AccountManager.getInstance().getCurrentAccount().getShopName();
    public String currentEmployeeId = AccountManager.getInstance().getCurrentAccount().getEmployeeId();
    public String currentEmployeeName = AccountManager.getInstance().getCurrentAccount().getUserName();

    public static SaleReturnOrderConfirmActivity saleReturnOrderConfirmActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sale_return_order_confirm);
        EventBus.getDefault().register(this);
        saleReturnOrderConfirmActivity = this;
        initData();
        initView();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        scanerRespManager.setType(ScanerRespManager.ScanerRespType.SCANNER_RESP_DEFAULT);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        reloadData();
    }

    public void onEventMainThread(PayRIghtNowEvent event) {
        if (event.getmMsg().equals(SaleReturnPresenter.SALE_RETURN_SUCCEED)){
            finish();
        }
    }

    private void initData() {
        scanerRespManager = ScanerRespManager.getInstance();
        scanerRespManager.setType(ScanerRespManager.ScanerRespType.SCANER_RESP_SEARCH);
        cartModel.setCurrentEmployeeId(currentEmployeeId);
        cartModel.setCurrentEmployeeName(currentEmployeeName);
        cartModel.setCurrentShopId(currentShopId);
        cartModel.setCurrentShopName(currentShopName);
    }

    private void initView() {
        productContainerSection = (LinearLayout) findViewById(R.id.product_containner);
        backImageView = (ImageView) findViewById(R.id.cart_management_back_imageView);
        backImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        saveButton = (Button) findViewById(R.id.save_button);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (actualMoneyContentEditText.getText().toString().equals("")){
                    Toast.makeText(getApplicationContext(), "请输入正确金额", Toast.LENGTH_SHORT).show();
                }else {
                    saveButton.setEnabled(false);
                    cartModel.setTotalPrice(actualMoneyContentEditText.getText().toString());
                    Intent intent = new Intent(SaleReturnOrderConfirmActivity.this, SaleReturnPaySelectActivity.class);
                    startActivity(intent);
                }
            }
        });
        totalMoneyContentTextView = (BaseTextView) findViewById(R.id.total_money_content_text_view);
        actualMoneyContentEditText = (EditText) findViewById(R.id.actual_money_content_text_view);
        actualMoneyContentEditText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        actualMoneyContentEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                int len = s.toString().length();
                if (len == 1 && s.toString().equals(".")) {
                    s.clear();
                    s.append("0.");
                }
                String temp = s.toString();
                int d = temp.indexOf(".");
                if (d >= 0) {
                    if (temp.length() - d - 1 > 2) {
                        s.delete(d + 3, d + 4);
                    } else if (d == 0) {
                        s.delete(d, d + 1);
                    }
                }

                String inputString = s.toString();
                if (inputString.equals("")){
                    inputString = "0";
                }
                BigDecimal totalMoney = new BigDecimal(cartModel.getTotalMoney());
                BigDecimal actualMoney = new BigDecimal(inputString);
                BigDecimal adjustMoney = totalMoney.subtract(actualMoney);
                BigDecimal zero = new BigDecimal("0");
                int comResult = adjustMoney.compareTo(zero);
                String resultString = "";
                if (comResult == 1) {
                    resultString = "减去";
                } else if (comResult == -1) {
                    resultString = "增加";
                    adjustMoney = adjustMoney.abs();
                } else if (comResult == 0) {
                    resultString = "";
                }
                DecimalFormat format = new DecimalFormat("##0.00");
                String value = format.format(adjustMoney);
                adjustContentTextView.setText(resultString + value);
            }
        });
        adjustContentTextView = (BaseTextView) findViewById(R.id.adjust_content_text_view);

        customerContentTextView = (BaseTextView) findViewById(R.id.customer_content_text_view);
        shopContentTextView = (BaseTextView) findViewById(R.id.shop_content_text_view);
        employeeContentTextView = (BaseTextView) findViewById(R.id.employee_content_text_view);
        warehouseContentTextView = (BaseTextView) findViewById(R.id.warehouse_content_text_view);
        titleTextView = (BaseTextView) findViewById(R.id.cart_management_title_textview);

        remarkEditText = (EditText) findViewById(R.id.remark_edit_text);
        remarkEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                cartModel.setRemark(s.toString());
            }
        });

        titleTextView.setText("退货单确认");
        customerContentTextView.setText(cartModel.getCustomerName());
        shopContentTextView.setText(cartModel.getCurrentShopName());
        employeeContentTextView.setText(cartModel.getCurrentEmployeeName());
        warehouseContentTextView.setText(cartModel.getWarehouseName());

    }

    private void reloadData() {
        saveButton.setEnabled(true);
        String totalValue = "0";
        try {
            BigDecimal big1 = new BigDecimal(cartModel.getTotalMoney());
            DecimalFormat format = new DecimalFormat("##0.00");
            totalValue = format.format(big1);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        totalMoneyContentTextView.setText(totalValue);
        String actualValue = "0";
        try {
            BigDecimal big1 = new BigDecimal(cartModel.getTotalMoney());
            DecimalFormat format = new DecimalFormat("##0.00");
            actualValue = format.format(big1);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        actualMoneyContentEditText.setText(actualValue);
        adjustContentTextView.setText("");
        productContainerSection.removeAllViews();
        for (int i = 0; i < cartModel.size(); i++) {
            addProductTitleView(cartModel.get(i));
            for (int j = 0; j < cartModel.get(i).getSkuList().size(); j++) {
                addProductSubView(cartModel.get(i), cartModel.get(i).getSkuList().get(j));
            }
        }
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

    private void modifyProductData(SKU sku, String price, int judge) {
        for (int i = 0; i < cartModel.size(); i++) {
            for (int j = 0; j < cartModel.get(i).getSkuList().size(); j++) {
                if (cartModel.get(i).getSkuList().get(j).getSkuId() == sku.getSkuId()) {
                    cartModel.get(i).getSkuList().get(j).setStandardPrice(price);
                }
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

    private String doubleTrans(float d) {
        if (Math.round(d) - d == 0) {
            return String.valueOf((long) d);
        }
        return String.valueOf(d);
    }

    private void addProductTitleView(final PreCart preCart) {
        final View child = getLayoutInflater().inflate(R.layout.item_sale_return_title, null);
        setViewTag(child, preCart);
        productContainerSection.addView(child);
        final BaseTextView mProductNameTextView = (BaseTextView) child.findViewById(R.id.product_name);
        mProductNameTextView.setText(preCart.getProductName() + "    ");
    }

    /**
     * 新增某一条购物车中商品的view
     *
     * @param preCart
     */
    private void addProductSubView(final PreCart preCart, final SKU sku) {
        final View child = getLayoutInflater().inflate(R.layout.item_sale_return, null);
        setViewTag(child, preCart, sku);
        productContainerSection.addView(child);
        final BaseTextView countEditText = (BaseTextView) child.findViewById(R.id.product_no_text_view);
        final BaseTextView mProductNameTextView = (BaseTextView) child.findViewById(R.id.product_name);
        final BaseTextView mProductUnitPriceEditView = (BaseTextView) child.findViewById(R.id.product_unit_price);
        String value = "0";
        try {
            BigDecimal big1 = new BigDecimal(sku.getStandardPrice());
            DecimalFormat format = new DecimalFormat("##0.00");
            value = format.format(big1);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        countEditText.setText(sku.getSaleAmount() + preCart.getUnit());

        mProductUnitPriceEditView.setText(value);
        mProductNameTextView.setText(sku.getSkuName() + "    ");
        int cartItemNo = 1;
        try {
            cartItemNo = Integer.valueOf(sku.getSaleAmount());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
