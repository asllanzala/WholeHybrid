package com.honeywell.wholesale.ui.dashboard.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.honeywell.wholesale.R;
import com.honeywell.wholesale.ui.base.BaseTextView;

/**
 * Created by xiaofei on 12/28/16.
 *
 */

public class TabView extends LinearLayout implements View.OnClickListener{

    private ImageView mTabImage;
    private BaseTextView mTabLable;

    public TabView(Context context) {
        super(context);
        initView(context);
    }

    public TabView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public TabView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView(context);
    }

    private void initView(Context context){
        setOrientation(VERTICAL);
        setGravity(Gravity.CENTER);
        View view = LayoutInflater.from(context).inflate(R.layout.dash_board_tabview, this, true);

        mTabImage = (ImageView)view.findViewById(R.id.tab_image);
        mTabLable = (BaseTextView)view.findViewById(R.id.tab_lable);
    }

    public void initData(TabItem tabItem){
        mTabImage.setImageResource(tabItem.imageResId);
        mTabLable.setText(tabItem.lableResId);
    }


    @Override
    public void onClick(View v) {

    }

    public void onDataChanged(int badgeCount) {
        //  TODO notify new message, change the badgeView
    }
}
