package com.honeywell.wholesale.ui.base;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.honeywell.wholesale.R;

/**
 * Created by allanhwmac on 16/5/9.
 *
 */
public class BaseActivity extends Activity {
    private final static String TAG = "BaseActivity";
    private LinearLayout contentContainer = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
    }

    @Override
    public void setContentView(int layoutResID) {
        if (layoutResID == R.layout.activity_base) {
            super.setContentView(R.layout.activity_base);
            contentContainer = (LinearLayout) findViewById(R.id.content_container);
            contentContainer.removeAllViews();
        } else {
            View content = LayoutInflater.from(this).inflate(layoutResID, null);

        }
        super.setContentView(layoutResID);
    }
}
