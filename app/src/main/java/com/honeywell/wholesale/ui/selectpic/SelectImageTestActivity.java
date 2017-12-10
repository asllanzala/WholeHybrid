package com.honeywell.wholesale.ui.selectpic;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.honeywell.wholesale.R;
import com.honeywell.wholesale.ui.selectpic.config.SelectorSetting;

import java.util.ArrayList;

/**
 * Created by zhujunyu on 16/6/27.
 */
public class SelectImageTestActivity extends Activity implements View.OnClickListener {

    private Button mButtonSelect;
    private TextView mTextResult;
    private ArrayList<String> mResults = new ArrayList<>();
    private static final int REQUEST_CODE = 732;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_main);
        initView();
        initEvent();
    }

    private void initView() {
        mButtonSelect = (Button) findViewById(R.id.btn_select);
        mTextResult = (TextView) findViewById(R.id.result);

    }


    private void initEvent() {
        mButtonSelect.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        Intent intent = new Intent(this, ImageSelectorActivity.class);
        intent.putExtra(SelectorSetting.SELECTOR_PRODUCT_ID, "123");
        intent.putExtra(SelectorSetting.SELECTOR_SHOP_ID, "6676");
        startActivityForResult(intent, REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK) {
//            mResults = data.getStringArrayListExtra(SelectorSetting.SELECTOR_RESULTS);
            String results = data.getStringExtra(SelectorSetting.SELECTOR_RESULTS);
            assert results != null;
//            StringBuilder sb = new StringBuilder();
//            sb.append(String.format("Totally %d images selected:", mResults.size())).append("\n");
//            for (String result : mResults) {
//                sb.append(result).append("\n");
//            }
            mTextResult.setText(results);

        }
        super.onActivityResult(requestCode, resultCode, data);
    }

}
