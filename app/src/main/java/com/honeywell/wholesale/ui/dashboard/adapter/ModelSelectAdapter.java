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
 * Created by xiaofei on 9/22/16.
 *
 */

public class ModelSelectAdapter extends BaseAdapter {

    private ArrayList<String> modelArrayList;
    private Context context;

    public ModelSelectAdapter(ArrayList<String> modelArrayList, Context context) {
        this.modelArrayList = modelArrayList;
        this.context = context;
    }

    @Override
    public int getCount() {
        return modelArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return modelArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null){
            convertView = LayoutInflater.from(context).inflate(R.layout.item_popup_report_model, parent, false);
        }

        BaseTextView textView = (BaseTextView)convertView.findViewById(R.id.item_report_model_textView);
        textView.setText(modelArrayList.get(position));
        return convertView;
    }

    public void setModelArrayList(ArrayList<String> modelArrayList) {
        this.modelArrayList = modelArrayList;
        notifyDataSetChanged();
    }
}
