package com.honeywell.wholesale.ui.dashboard.view;

import com.honeywell.wholesale.R;
import com.honeywell.wholesale.lib.util.DensityUtil;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.annotation.Dimension;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.UUID;

/**
 * Created by e887272 on 6/20/16.
 */
public class TabButton extends LinearLayout {

    private static final int DEFAULT_BORDER_COLOR = Color.BLACK;

    private static final int DEFAULT_ICON = R.drawable.main_page_home_bg;

    private int mTextSelectedColor = DEFAULT_BORDER_COLOR;

    private int mTextNormalColor = DEFAULT_BORDER_COLOR;

    // this is the title of the fragment, it will be used as fragment UUID
    private String mTitleText = UUID.randomUUID().toString();

    private int mIconSelected;

    private int mIconNormal;
    private int mTabAmount = 5;

    private TextView mTextView;

    private ImageView mImageView;

    private ImageView mImageNotificationView;

    private LinearLayout mTabButtonLayout;

    private int mTabButtonTextSize = 0;

    private boolean mIsSelected = false;

    public TabButton(Context context) {
        super(context);
        initView(context);
    }

    public TabButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TabButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        TypedArray typedArray = context.obtainStyledAttributes(attrs,
                R.styleable.TabButton, defStyle, 0);

        mIconSelected = typedArray.getResourceId(
                R.styleable.TabButton_button_icon_selected, DEFAULT_ICON);
        mIconNormal = typedArray.getResourceId(
                R.styleable.TabButton_button_icon_normal, DEFAULT_ICON);

        mTitleText = typedArray.getString(R.styleable.TabButton_button_text);

        mTextSelectedColor = typedArray
                .getColor(R.styleable.TabButton_button_text_color_selected, DEFAULT_BORDER_COLOR);
        mTextNormalColor = typedArray
                .getColor(R.styleable.TabButton_button_text_color_normal, DEFAULT_BORDER_COLOR);
        mIsSelected = typedArray
                .getBoolean(R.styleable.TabButton_button_isSelected, false);
        mTabAmount= typedArray
                .getInt(R.styleable.TabButton_tab_amount, 5);
        mTabButtonTextSize = typedArray
                .getDimensionPixelSize(R.styleable.TabButton_button_text_size, 1);
        typedArray.recycle();

        initView(context);

        setSelected(mIsSelected);
    }

    public void initView(Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.layout_mian_tab_btn, null);

        addView(view);

        mTextView = (TextView) view.findViewById(R.id.tab_button_text);
        mImageView = (ImageView) view.findViewById(R.id.tab_button_img);
        mTabButtonLayout = (LinearLayout) view.findViewById(R.id.tab_button_layout);
        mImageNotificationView = (ImageView) view.findViewById(R.id.tab_button_img_notification);

        mImageView.setImageResource(mIconNormal);
        mTextView.setText(mTitleText);

        int screenWidth = DensityUtil.getScreenWidth();
        LayoutParams layoutParams = (LayoutParams) mTabButtonLayout.getLayoutParams();
        layoutParams.width = screenWidth / mTabAmount;
        layoutParams.height = LayoutParams.MATCH_PARENT;
        mTabButtonLayout.setLayoutParams(layoutParams);
    }

    public void showNotification(boolean isShow) {
        if(isShow) {
            mImageNotificationView.setVisibility(VISIBLE);
        } else {
            mImageNotificationView.setVisibility(GONE);
        }
    }

    public void setSelected(boolean isSelected) {
        mIsSelected = isSelected;
        if (isSelected) {
            mImageView.setImageResource(mIconSelected);
            mTextView.setTextColor(mTextSelectedColor);
        } else {
            mImageView.setImageResource(mIconNormal);
            mTextView.setTextColor(mTextNormalColor);
        }
    }

    public boolean isSelected() {
        return mIsSelected;
    }

}