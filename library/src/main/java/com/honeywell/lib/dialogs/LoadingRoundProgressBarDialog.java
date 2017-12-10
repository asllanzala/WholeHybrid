package com.honeywell.lib.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.honeywell.lib.R;
import com.honeywell.lib.widgets.RoundProgressBar;
import com.honeywell.lib.widgets.SmoothRoundProgressBar;

public class LoadingRoundProgressBarDialog extends Dialog {
    private String content;
    protected TextView mTipText;
    //    protected RoundProgressBar mRoundProgressBar;
    protected SmoothRoundProgressBar mSmoothRoundProgressBar;
    protected TextView mTextProgress;

    public LoadingRoundProgressBarDialog(Context context, String title) {
        super(context, R.style.loadingDialogStyle);
        content = title;
    }

    public LoadingRoundProgressBarDialog(Context context) {
        this(context, "");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_loading_round_progressbar);
        mTipText = (TextView) this.findViewById(R.id.dialog_tv_msg);
        if (!TextUtils.isEmpty(content)) {
            mTipText.setVisibility(View.VISIBLE);
            mTipText.setText(content);
        }
        mTextProgress = (TextView) this.findViewById(R.id.tv_progress);
//        mRoundProgressBar = (RoundProgressBar) this.findViewById(R.id.round_progress);
    }

    public void setTip(String tip) {
        if (mTipText != null) {
            mTipText.setText(tip);
        }
    }

    public void setProgress(int progress) {
//        if (mRoundProgressBar != null) {
//            mRoundProgressBar.setProgress(progress);
//        }
        if (mTextProgress != null) {
            mTextProgress.setText(progress + "%");
        }
    }

//    public RoundProgressBar getProgressBar() {
//        return mRoundProgressBar;
//    }

}