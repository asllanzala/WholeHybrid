package com.honeywell.wholesale.ui.base;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by xiaofei on 7/13/16.
 *
 */
public class BaseTextView extends TextView {
    private static Typeface honeyWellBoldTypeface;
    private static Typeface honeyWellItalicTypeface;
    private static Typeface honeyWellNormalTypeface;

    public BaseTextView(Context context) {
        super(context);
        initTypeface();
    }

    public BaseTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initTypeface();
    }

    public BaseTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initTypeface();
    }

    private void initTypeface(){
        if (honeyWellBoldTypeface == null){
            honeyWellBoldTypeface = Typeface.createFromAsset(getContext().getAssets(), "fonts/HoneywellSans-Bold.otf");
        }

        if (honeyWellNormalTypeface == null){
            honeyWellNormalTypeface = Typeface.createFromAsset(getContext().getAssets(), "fonts/HoneywellSans-Medium.otf");
        }
    }

    @Override
    public void setTypeface(Typeface tf, int style) {
        if (style == Typeface.BOLD) {
            super.setTypeface(honeyWellBoldTypeface);
            return;
        }
        super.setTypeface(honeyWellNormalTypeface);
    }

}
