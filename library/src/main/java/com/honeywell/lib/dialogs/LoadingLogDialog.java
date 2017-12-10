package com.honeywell.lib.dialogs;

/**
 * Created by milton on 16/5/24.
 */


import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.honeywell.lib.R;
import com.honeywell.lib.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;


public class LoadingLogDialog extends Dialog {
    final static int MAX_LOGS = 15;
    private String content;
    protected TextView mTipText;
    protected ListView mListLog;
    List<String> mLogList = new ArrayList<>();
    LogAdapter mAdapter;

    public LoadingLogDialog(Context context, String title) {
        super(context, R.style.loadingDialogStyle);
        content = title;
    }

    public LoadingLogDialog(Context context) {
        this(context, "");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_loading_log);
        mTipText = (TextView) this.findViewById(R.id.dialog_tv_msg);
        if (!TextUtils.isEmpty(content)) {
            mTipText.setVisibility(View.VISIBLE);
            mTipText.setText(content);
        }
        mListLog = (ListView) findViewById(R.id.dialog_lv_log);
        for (int i = 0; i < MAX_LOGS; i++) {
            mLogList.add("");
        }
        mAdapter = new LogAdapter(mLogList);


        mListLog.setAdapter(mAdapter);
        View llBg = this.findViewById(R.id.dialogui_ll_bg);
        ProgressBar pbBg = (ProgressBar) this.findViewById(R.id.pb_bg);
//        llBg.setBackgroundResource(R.drawable.dialogui_shape_wihte_round_corner);
        pbBg.setIndeterminateDrawable(this.getContext().getResources().getDrawable(R.drawable.dialogui_rotate_mum));
    }

    public void setTip(String tip) {
        if (mTipText != null) {
            mTipText.setText(tip);
        }
    }

    public void clearLogs() {
        if (mAdapter != null) {
            mLogList.clear();
            for (int i = 0; i < MAX_LOGS; i++) {
                mLogList.add("");
            }
            mAdapter.notifyDataSetChanged();
        }
    }

    public void updateLog(String log) {
        if (mAdapter != null) {
            mLogList.add(log);
            if (mLogList.size() > MAX_LOGS) {
                mLogList.remove(0);
            }
            mAdapter.notifyDataSetChanged();
        }
    }

    static class LogAdapter extends BaseAdapter {
        private LayoutInflater mInflater;
        List<String> mDataList = new ArrayList<>();

        public LogAdapter(List<String> dataList) {
            this.mDataList = dataList;
        }

        @Override
        public int getCount() {
            return mDataList == null ? 0 : mDataList.size();
        }

        @Override
        public Object getItem(int position) {
            return mDataList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ItemHolder holder;
            if (convertView == null) {
                if (mInflater == null) {
                    mInflater = LayoutInflater.from(parent.getContext());
                }
                holder = new ItemHolder();
                convertView = mInflater.inflate(R.layout.dialog_list_one_line_log, null);

                holder.text = (TextView) convertView.findViewById(R.id.tv_log);
                convertView.setTag(holder);
            } else {
                holder = (ItemHolder) convertView.getTag();
            }
            holder.text.setText(mDataList.get(position));
            return convertView;
        }

    }

    protected static class ItemHolder {
        public TextView text;
    }
}