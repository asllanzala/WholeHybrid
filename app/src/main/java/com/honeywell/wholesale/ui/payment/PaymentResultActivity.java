package com.honeywell.wholesale.ui.payment;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.honeywell.wholesale.R;
import com.honeywell.wholesale.framework.model.CartManager;

/**
 * Created by H155935 on 16/6/24.
 * Email: xiaofei.he@honeywell.com
 */
public class PaymentResultActivity extends Activity {
    private TextView paymentTotalMoneyTextView;
    private Button receiptButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_result);

        paymentTotalMoneyTextView = (TextView)findViewById(R.id.payment_money_amount);
        receiptButton = (Button) findViewById(R.id.payment_print_receipt);

        receiptButton.setOnClickListener(onClickListener);

        CartManager.getInstance().removeAllCarts();
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            finish();
        }
    };

}
