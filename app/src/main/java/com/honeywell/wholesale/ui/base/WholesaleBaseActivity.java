package com.honeywell.wholesale.ui.base;

import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.honeywell.wholesale.R;
import com.honeywell.wholesale.ui.util.Constants;


public class WholesaleBaseActivity extends AppCompatActivity {
    protected int activityCloseEnterAnimation;

    protected int activityCloseExitAnimation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme();
        initAnimation();
    }

    protected void setTheme() {
        setTheme(R.style.WholesaleTheme);
    }

    public void initAnimation() {
        TypedArray activityStyle = getTheme().obtainStyledAttributes(new int[]{
                android.R.attr.windowAnimationStyle
        });

        int windowAnimationStyleResId = activityStyle.getResourceId(0, 0);

        activityStyle.recycle();

        activityStyle = getTheme().obtainStyledAttributes(windowAnimationStyleResId, new int[]{
                android.R.attr.activityCloseEnterAnimation, android.R.attr.activityCloseExitAnimation
        });

        activityCloseEnterAnimation = activityStyle.getResourceId(0, 0);

        activityCloseExitAnimation = activityStyle.getResourceId(1, 0);

        activityStyle.recycle();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(activityCloseEnterAnimation, activityCloseExitAnimation);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data != null && data.getBooleanExtra(Constants.FINISH_ALL, false)) {
            finishAll();
        }
    }

    public void finishAll() {
        Intent intent = new Intent();
        intent.putExtra(Constants.FINISH_ALL, true);
        setResult(RESULT_OK, intent);
        finish();
    }

    protected <T extends View> T findView(int id) {
        return (T) findViewById(id);
    }
}
