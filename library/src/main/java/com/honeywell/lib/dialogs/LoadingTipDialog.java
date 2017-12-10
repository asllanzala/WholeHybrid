package com.honeywell.lib.dialogs;

/**
 * Created by milton on 16/5/24.
 */


import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.honeywell.lib.R;


public class LoadingTipDialog extends Dialog {
    private String content;
    protected TextView mTipText;

    public LoadingTipDialog(Context context, String title) {
        super(context, R.style.loadingDialogStyle);
        content = title;
    }

    public LoadingTipDialog(Context context) {
        this(context, "");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialogui_loading_vertical);
        if (!TextUtils.isEmpty(content)) {
            mTipText = (TextView) this.findViewById(R.id.dialogui_tv_msg);
            mTipText.setVisibility(View.VISIBLE);
            mTipText.setText(content);
        }
        View llBg = this.findViewById(R.id.dialogui_ll_bg);
        ProgressBar pbBg = (ProgressBar) this.findViewById(R.id.pb_bg);
        llBg.setBackgroundResource(R.drawable.dialogui_shape_wihte_round_corner);
        pbBg.setIndeterminateDrawable(this.getContext().getResources().getDrawable(R.drawable.dialogui_rotate_mum));
    }

    public void setTip(String tip) {
        if (mTipText != null) {
            mTipText.setText(tip);
        }
    }
}