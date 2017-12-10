package com.honeywell.wholesale.ui.dashboard.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.honeywell.wholesale.R;
import com.honeywell.wholesale.ui.base.BaseTextView;

import java.util.ArrayList;

/**
 * Created by xiaofei on 9/23/16.
 *
 */
public class TimeSelectAdapter extends BaseAdapter{
    private ArrayList<String> timeArrayList;
    private Context context;

    public TimeSelectAdapter(ArrayList<String> timeArrayList, Context context) {
        this.timeArrayList = timeArrayList;
        this.context = context;
    }

    @Override
    public int getCount() {
        return timeArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return timeArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null){
            convertView = LayoutInflater.from(context).inflate(R.layout.item_popup_report_time, parent, false);
        }

        BaseTextView textView = (BaseTextView)convertView.findViewById(R.id.item_report_time_textView);
        textView.setText(timeArrayList.get(position));
        return convertView;
    }

    public void setModelArrayList(ArrayList<String> timeArrayList) {
        this.timeArrayList = timeArrayList;
        notifyDataSetChanged();
    }
}
