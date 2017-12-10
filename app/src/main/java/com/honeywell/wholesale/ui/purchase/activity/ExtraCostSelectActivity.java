package com.honeywell.wholesale.ui.purchase.activity;

import android.content.Context;
import android.content.Intent;
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
import android.widget.RelativeLayout;
import android.widget.Toast;
import com.honeywell.wholesale.R;
import com.honeywell.wholesale.framework.model.AccountManager;
import com.honeywell.wholesale.framework.model.ExtraCost;
import com.honeywell.wholesale.framework.model.PreCart;
import com.honeywell.wholesale.framework.model.SKU;
import com.honeywell.wholesale.ui.base.BaseActivity;
import com.honeywell.wholesale.ui.base.BaseTextView;
import com.honeywell.wholesale.ui.purchase.adapter.ExtraCostAdapter;
import com.honeywell.wholesale.ui.purchase.presenter.ExtraCostPresenter;
import com.honeywell.wholesale.ui.purchase.view.ExtraCostView;
import com.honeywell.wholesale.ui.transaction.preorders.CartModel;
import com.honeywell.wholesale.ui.transaction.preorders.ObserverCartData;

import java.math.BigDecimal;
import java.util.ArrayList;

/**
 * Created by liuyang on 2017/7/5.
 */

public class ExtraCostSelectActivity extends BaseActivity implements ExtraCostView {

    private static final String TAG = ExtraCostSelectActivity.class.getSimpleName();
    private static final String INTENT_VALUE_EXTRA_COST_ID = "INTENT_VALUE_EXTRA_COST_ID";
    public static final String SKU_ID = "SKU_ID";
    public static final String PRODUCT_ID = "PRODUCT_ID";

    private ImageView backImageView;

    private Button saveButton;

    private LinearLayout productContainerSection;

    private Context context;

    private ExtraCostPresenter extraCostPresenter;

    private ArrayList<ExtraCost> extraCostList;

    private ArrayList<ExtraCost> actualExtraCostList;

    private CartModel cartModel = CartModel.getInstance();

    private int productId;

    private int currentSkuId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_extra_cost);
        extraCostPresenter = new ExtraCostPresenter(this);
        initView();
        initData();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
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
    public Context getContext() {
        return null;
    }

    @Override
    public void setListData(ArrayList<ExtraCost> arrayList) {
        extraCostList =  arrayList;
        if (actualExtraCostList != null){
            for (int i = 0; i < actualExtraCostList.size(); i ++){
                modifyExtraCost(actualExtraCostList.get(i));
            }
        }
        initShow();
    }

    private void initView() {
        productContainerSection = (LinearLayout) findViewById(R.id.product_containner);
        backImageView = (ImageView) findViewById(R.id.icon_back);
        backImageView.setOnClickListener(onClickListener);
        saveButton = (Button) findViewById(R.id.save_button);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initActualExtraCostList(extraCostList);
                if (actualExtraCostList.size() == 0){
                    actualExtraCostList = null;
                }
                cartModel.modifyExtraCostById(productId, currentSkuId, actualExtraCostList);
                finish();
            }
        });
    }

    private void initData() {
        context = this;
        extraCostList = new ArrayList<>();
        Intent intent = getIntent();
        if (intent != null) {
            currentSkuId = intent.getIntExtra(SKU_ID, -1);
            productId = intent.getIntExtra(PRODUCT_ID, -1);
        }
        actualExtraCostList = cartModel.getExtraCostById(productId, currentSkuId);

        extraCostPresenter.getExtraCostDataFromServer();
    }

    private void initShow(){
        Log.e(TAG,String.valueOf(extraCostList.size()));
        for (int i = 0; i < extraCostList.size(); i++){
            Log.e(TAG,extraCostList.get(i).getExtraCostName());
            addExtraCostView(extraCostList.get(i));
        }
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.icon_back) {
                finish();
            }
        }
    };

    private ObserverCartData observerCartData = new ObserverCartData() {
        @Override
        public void dataChanged() {

        }
    };

    private void addExtraCostView(final ExtraCost extraCost) {
        final View child = getLayoutInflater().inflate(R.layout.item_extra_cost_list, null);
        setViewTag(child, extraCost);
        productContainerSection.addView(child);
        final BaseTextView baseTextView = (BaseTextView) child.findViewById(R.id.extra_cost_text_view);
        final EditText editText = (EditText) child.findViewById(R.id.content_edit_text);
        baseTextView.setText(extraCost.getExtraCostName());
        editText.setText(extraCost.getPremiumPrice());
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                decimalFilter(s, 2, 10, editText);
            }

            @Override
            public void afterTextChanged(Editable s) {
                extraCost.setPremiumPrice(editText.getText().toString());
                modifyExtraCost(extraCost);
            }
        });
    }

    private View getViewByTag(int tagKey) {
        for (int i = 0; i < productContainerSection.getChildCount(); i++) {
            if (tagKey == Integer.valueOf(productContainerSection.getChildAt(i).getTag(R.id.precart_view_tag).toString())) {
                return productContainerSection.getChildAt(i);
            }
        }
        return null;
    }

    public void initActualExtraCostList(ArrayList<ExtraCost> arrayList){
        actualExtraCostList = new ArrayList<ExtraCost>();
        for (int i = 0; i < arrayList.size(); i++){
            if (arrayList.get(i).getPremiumPrice() != null) {
                if (!arrayList.get(i).getPremiumPrice().equals("")) {
                    actualExtraCostList.add(arrayList.get(i));
                }
            }
        }
    }

    private void setViewTag(View view, ExtraCost extraCost) {
        view.setTag(R.id.extra_view_tag, extraCost.getExtraCostId());
    }

    private void modifyExtraCost(ExtraCost extraCost){
        for (int i = 0; i < extraCostList.size(); i++){
            if (extraCost.getExtraCostId().equals(extraCostList.get(i).getExtraCostId())){
                extraCostList.get(i).setPremiumPrice(extraCost.getPremiumPrice());
            }
        }
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
