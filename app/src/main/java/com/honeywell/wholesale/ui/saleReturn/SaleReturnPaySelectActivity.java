package com.honeywell.wholesale.ui.saleReturn;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.gson.Gson;
import com.honeywell.hybridapp.framework.event.Event;
import com.honeywell.wholesale.R;
import com.honeywell.wholesale.framework.event.PayRIghtNowEvent;
import com.honeywell.wholesale.framework.event.SaleReturnEvent;
import com.honeywell.wholesale.framework.http.NativeJsonResponseListener;
import com.honeywell.wholesale.framework.http.WebClient;
import com.honeywell.wholesale.framework.model.PayAccount;
import com.honeywell.wholesale.framework.utils.SwitchButton;
import com.honeywell.wholesale.ui.activity.MainActivity;
import com.honeywell.wholesale.ui.base.BaseActivity;
import com.honeywell.wholesale.ui.base.BaseTextView;
import com.honeywell.wholesale.ui.inventory.ProductShipmentActivity;
import com.honeywell.wholesale.ui.saleReturn.presenter.SaleReturnPresenter;
import com.honeywell.wholesale.ui.transaction.preorders.CartModel;

import org.json.JSONArray;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;

import de.greenrobot.event.EventBus;

import static com.honeywell.wholesale.ui.saleReturn.presenter.SaleReturnPresenter.SALE_RETURN_SUCCEED;

public class SaleReturnPaySelectActivity extends BaseActivity {

    private SwitchButton switchButton;

    private BaseTextView debtTitleTextView;
    private BaseTextView debtContentTextView;
    private BaseTextView cashTextView;
    private BaseTextView alipayTextView;
    private BaseTextView webchatTextView;
    private BaseTextView creditCardTextView;
    private BaseTextView debtTextView;

    private BaseTextView totalTitleTextView;
    private BaseTextView totalContentTextView;

    private ImageView imageView;

    private EditText cashEditText;
    private EditText alipayEditText;
    private EditText webchatEditText;
    private EditText creditCardEditText;
    private EditText debtEditText;


    private RadioGroup radioGroup;
    private RadioButton cashRadioButton;
    private RadioButton alipayRadioButton;
    private RadioButton webchatRadioButton;
    private RadioButton creditCardRadioButton;
    private RadioButton debtRadioButton;

    private Button saveButton;

    private LinearLayout singlePayLayout;
    private LinearLayout multiPayLayout;

    private View blankRelativeLayout;

    private CartModel cartModel = CartModel.getInstance();

    private ArrayList<PayAccount> payAccountList;
    private ArrayList<PayAccount> payAccountOrderList;

    private WebClient webClient;

    private Context context;

    private SaleReturnPresenter saleReturnPresenter;

    public static SaleReturnPaySelectActivity saleReturnPaySelectActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sale_return_pay_select);
        saleReturnPaySelectActivity = this;
        EventBus.getDefault().register(this);
        initView();
        initData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    public void onEventMainThread(PayRIghtNowEvent event) {
        if (event.getmMsg().equals(SaleReturnPresenter.SALE_RETURN_SUCCEED)) {
            finish();
        }
    }

    private void initData() {
        context = getApplicationContext();
        webClient = new WebClient();
        payAccountList = new ArrayList<PayAccount>();
        payAccountOrderList = new ArrayList<PayAccount>();
        saleReturnPresenter = new SaleReturnPresenter(context);
        getAccountPay();
    }

    private void initView() {
        cashEditText = (EditText) findViewById(R.id.cash_edit_text);
        cashEditText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        cashEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                s = limitInput(s);
            }
        });
        alipayEditText = (EditText) findViewById(R.id.alipay_edit_text);
        alipayEditText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        alipayEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                s = limitInput(s);
            }
        });
        webchatEditText = (EditText) findViewById(R.id.webchat_edit_text);
        webchatEditText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        webchatEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                s = limitInput(s);
            }
        });
        creditCardEditText = (EditText) findViewById(R.id.credit_card_edit_text);
        creditCardEditText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        creditCardEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                s = limitInput(s);
            }
        });
        debtEditText = (EditText) findViewById(R.id.debt_edit_text);
        debtEditText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        debtEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                s = limitInput(s);
            }
        });

        switchButton = (SwitchButton) findViewById(R.id.warehouse_switch_button);
        switchButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                initShow();
                if (isChecked) {
                    singlePayLayout.setVisibility(View.GONE);
                    multiPayLayout.setVisibility(View.VISIBLE);
                    debtTitleTextView.setVisibility(View.VISIBLE);
                    debtContentTextView.setVisibility(View.VISIBLE);
                    showChangeTotalMoney();
                } else {
                    singlePayLayout.setVisibility(View.VISIBLE);
                    multiPayLayout.setVisibility(View.GONE);
                    debtTitleTextView.setVisibility(View.GONE);
                    debtContentTextView.setVisibility(View.GONE);
                }
            }
        });
        singlePayLayout = (LinearLayout) findViewById(R.id.single_pay_layout);
        multiPayLayout = (LinearLayout) findViewById(R.id.multi_pay_layout);

        blankRelativeLayout = findViewById(R.id.sku_activity_blank_layout);
        blankRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SaleReturnPaySelectActivity.this.finish();
            }
        });

        debtTitleTextView = (BaseTextView) findViewById(R.id.debt_title_text_view);
        debtContentTextView = (BaseTextView) findViewById(R.id.debt_content_text_view);

        totalTitleTextView = (BaseTextView) findViewById(R.id.total_title_text_view);
        totalContentTextView = (BaseTextView) findViewById(R.id.total_content_text_view);
        totalContentTextView.setText(cartModel.getTotalPrice());

        imageView = (ImageView) findViewById(R.id.cancel_iamge_view);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SaleReturnPaySelectActivity.this.finish();
            }
        });

        radioGroup = (RadioGroup) findViewById(R.id.payment_select_radio_group);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {

            }
        });

        saveButton = (Button) findViewById(R.id.order_to_cart_button);
        saveButton.setEnabled(false);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveButton.setEnabled(false);
                if (isEmpty()){
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.sale_return_money_empty), Toast.LENGTH_SHORT).show();
                    saveButton.setEnabled(true);
                }else if (compareMoney() != 0) {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.sale_return_money_inequal), Toast.LENGTH_SHORT).show();
                    saveButton.setEnabled(true);
                } else {
                    getPayAccount();
                    saleReturnPresenter.traModel();
                    saleReturnPresenter.sareReturnOrder(saveButton);
                }
            }
        });

        cashRadioButton = (RadioButton) findViewById(R.id.cash_radio_button);
        alipayRadioButton = (RadioButton) findViewById(R.id.alipay_radio_button);
        webchatRadioButton = (RadioButton) findViewById(R.id.webchat_radio_button);
        creditCardRadioButton = (RadioButton) findViewById(R.id.credit_card_radio_button);
        debtRadioButton = (RadioButton) findViewById(R.id.debt_radio_button);

        cashTextView = (BaseTextView) findViewById(R.id.cash_text_view);
        alipayTextView = (BaseTextView) findViewById(R.id.alipay_text_view);
        webchatTextView = (BaseTextView) findViewById(R.id.webchat_text_view);
        creditCardTextView = (BaseTextView) findViewById(R.id.credit_card_text_view);
        debtTextView = (BaseTextView) findViewById(R.id.debt_text_view);

        radioGroup.check(R.id.cash_radio_button);
        switchButton.setChecked(false);
        debtTitleTextView.setVisibility(View.GONE);
        debtContentTextView.setVisibility(View.GONE);
    }

    private void initShow() {
        if (switchButton.isChecked()) {
            cashTextView.setText(payAccountList.get(0).getAccountName());
            alipayTextView.setText(payAccountList.get(1).getAccountName());
            webchatTextView.setText(payAccountList.get(2).getAccountName());
            creditCardTextView.setText(payAccountList.get(3).getAccountName());
            debtTextView.setText(payAccountList.get(4).getAccountName());
        } else {
            cashRadioButton.setText(payAccountList.get(0).getAccountName());
            alipayRadioButton.setText(payAccountList.get(1).getAccountName());
            webchatRadioButton.setText(payAccountList.get(2).getAccountName());
            creditCardRadioButton.setText(payAccountList.get(3).getAccountName());
            debtRadioButton.setText(payAccountList.get(4).getAccountName());
            radioGroup.check(R.id.cash_radio_button);
        }
        saveButton.setEnabled(true);
    }

    public void getAccountPay() {
        webClient.httpGetAccountPay(new NativeJsonResponseListener<JSONObject>() {
            @Override
            public void listener(JSONObject jsonObject) {
                JSONArray jsonArray = jsonObject.optJSONArray(WebClient.API_RESPONSE_RET_BODY);
                Gson gson = new Gson();
                payAccountList = new ArrayList<PayAccount>();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject1 = jsonArray.optJSONObject(i);
                    if (jsonObject1 != null) {
                        PayAccount payAccount = gson.fromJson(jsonObject1.toString(), PayAccount.class);
                        payAccountList.add(payAccount);
                    }
                }
                initShow();
            }

            @Override
            public void errorListener(String s) {

            }
        });
    }

    public void jumpToMain() {
        Intent intent = new Intent(SaleReturnPaySelectActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
//        PriceDiffOrderConfirmActivity.priceDiffOrderConfirmActivity.finish();
//        PriceDiffActivity.priceDiffActivity.finish();
    }

    public void getPayAccount() {
        String cashMoney;
        String alipayMoney;
        String webchatMoney;
        String creditCardMoney;
        String debtMoney;
        if (switchButton.isChecked()) {
            payAccountOrderList = new ArrayList<PayAccount>();
            int i = 0;
            if (!cashEditText.getText().toString().equals("")) {
                cashMoney = negativeQuantity(cashEditText.getText().toString());
                payAccountOrderList.add(payAccountList.get(0));
                payAccountOrderList.get(i).setIncome(cashMoney);
                i = i + 1;
            }
            if (!alipayEditText.getText().toString().equals("")) {
                alipayMoney = negativeQuantity(alipayEditText.getText().toString());
                payAccountOrderList.add(payAccountList.get(1));
                payAccountOrderList.get(i).setIncome(alipayMoney);
                i = i + 1;
            }
            if (!webchatEditText.getText().toString().equals("")) {
                webchatMoney = negativeQuantity(webchatEditText.getText().toString());
                payAccountOrderList.add(payAccountList.get(2));
                payAccountOrderList.get(i).setIncome(webchatMoney);
                i = i + 1;
            }
            if (!creditCardEditText.getText().toString().equals("")) {
                creditCardMoney = negativeQuantity(creditCardEditText.getText().toString());
                payAccountOrderList.add(payAccountList.get(3));
                payAccountOrderList.get(i).setIncome(creditCardMoney);
                i = i + 1;
            }
            if (!debtEditText.getText().toString().equals("")) {
                debtMoney = negativeQuantity(debtEditText.getText().toString());
                payAccountOrderList.add(payAccountList.get(4));
                payAccountOrderList.get(i).setIncome(debtMoney);
                i = i + 1;
            }
        } else {
            String money = negativeQuantity(totalContentTextView.getText().toString());
            payAccountOrderList = new ArrayList<PayAccount>();
            if (cashRadioButton.isChecked()) {
                payAccountOrderList.add(payAccountList.get(0));
                payAccountOrderList.get(0).setIncome(money);
            } else if (alipayRadioButton.isChecked()) {
                payAccountOrderList.add(payAccountList.get(1));
                payAccountOrderList.get(0).setIncome(money);
            } else if (webchatRadioButton.isChecked()) {
                payAccountOrderList.add(payAccountList.get(2));
                payAccountOrderList.get(0).setIncome(money);
            } else if (creditCardRadioButton.isChecked()) {
                payAccountOrderList.add(payAccountList.get(3));
                payAccountOrderList.get(0).setIncome(money);
            } else if (debtRadioButton.isChecked()) {
                payAccountOrderList.add(payAccountList.get(4));
                payAccountOrderList.get(0).setIncome(money);
            }
        }
        saleReturnPresenter.setPayAccountArrayList(payAccountOrderList);
    }

    private void showChangeTotalMoney() {
        BigDecimal cashMoney;
        BigDecimal alipayMoney;
        BigDecimal webchatMoney;
        BigDecimal creditCardMoney;
        BigDecimal debtMoney;
        if (cashEditText.getText().toString().equals("")) {
            cashMoney = new BigDecimal("0");
        } else {
            cashMoney = new BigDecimal(cashEditText.getText().toString());
        }
        if (alipayEditText.getText().toString().equals("")) {
            alipayMoney = new BigDecimal("0");
        } else {
            alipayMoney = new BigDecimal(alipayEditText.getText().toString());
        }
        if (webchatEditText.getText().toString().equals("")) {
            webchatMoney = new BigDecimal("0");
        } else {
            webchatMoney = new BigDecimal(webchatEditText.getText().toString());
        }
        if (creditCardEditText.getText().toString().equals("")) {
            creditCardMoney = new BigDecimal("0");
        } else {
            creditCardMoney = new BigDecimal(creditCardEditText.getText().toString());
        }
        if (debtEditText.getText().toString().equals("")) {
            debtMoney = new BigDecimal("0");
        } else {
            debtMoney = new BigDecimal(debtEditText.getText().toString());
        }
        BigDecimal totalHave = cashMoney.add(alipayMoney).add(webchatMoney).add(creditCardMoney).add(debtMoney);

        debtContentTextView.setText(totalHave.toString());
    }

    private boolean isEmpty() {
        if (switchButton.isChecked()){
            if (cashEditText.getText().toString().equals("") &&
                    alipayEditText.getText().toString().equals("") &&
                    webchatEditText.getText().toString().equals("") &&
                    creditCardEditText.getText().toString().equals("") &&
                    debtEditText.getText().toString().equals("")) {
                return true;
            } else {
                return false;
            }
        }else {
            return false;
        }

    }

    private int compareMoney() {
        if (switchButton.isChecked()) {
            BigDecimal totalHave;
            BigDecimal origalHave;
            if (totalContentTextView.getText().toString().equals("")) {
                totalHave = new BigDecimal("0");
            } else {
                totalHave = new BigDecimal(totalContentTextView.getText().toString());
            }
            if (debtContentTextView.getText().toString().equals("")) {
                origalHave = new BigDecimal("0");
            } else {
                origalHave = new BigDecimal(debtContentTextView.getText().toString());
            }

            int comResult = totalHave.compareTo(origalHave);
            return comResult;
        } else {
            return 0;
        }
    }

    public void closeAll() {
//        EventBus.getDefault().post(new SaleReturnEvent(SALE_RETURN_SUCCEED));
        SaleReturnPaySelectActivity.this.finish();
    }

    private Editable limitInput(Editable s) {
        int len = s.toString().length();
        if (len == 0) {
        }
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
        showChangeTotalMoney();
        return s;
    }

    private String negativeQuantity(String quantity) {
        if (quantity.length() == 0){
            return "0";
        }

        BigDecimal quantityBigDecimal = new BigDecimal(quantity);
        BigDecimal zeroBigDecimal = new BigDecimal("0");
        BigDecimal negativeBigDecimal = zeroBigDecimal.subtract(quantityBigDecimal);
        return negativeBigDecimal.toString();
    }
}
