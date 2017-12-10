package com.honeywell.wholesale.ui.payment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.honeywell.wholesale.R;
import com.honeywell.wholesale.framework.utils.SignatureView;

/**
 * Created by H155935 on 16/6/24.
 * Email: xiaofei.he@honeywell.com
 */
public class SignatureActivity extends Activity {

    private ImageView backActionImageView;

    private TextView totalPriceTextView;

    private SignatureView signatureView;

    private Button clearButton, confirmButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signature);

        backActionImageView = (ImageView) findViewById(R.id.back_arrow);
        totalPriceTextView = (TextView) findViewById(R.id.amount_price);
        signatureView = (SignatureView) findViewById(R.id.signature_view);

        clearButton = (Button) findViewById(R.id.clear_button);
        confirmButton = (Button) findViewById(R.id.confirm_button);

        backActionImageView.setOnClickListener(onClickListener);
        clearButton.setOnClickListener(onClickListener);
        confirmButton.setOnClickListener(onClickListener);
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.back_arrow) {
                SignatureActivity.this.finish();
            }

            if (v.getId() == R.id.clear_button) {
                signatureView.clear();
            }

            if (v.getId() == R.id.confirm_button) {
                Intent intent = new Intent(SignatureActivity.this, PaymentResultActivity.class);
                startActivity(intent);
                setResult(RESULT_OK);
                finish();
            }

        }
    };

}
