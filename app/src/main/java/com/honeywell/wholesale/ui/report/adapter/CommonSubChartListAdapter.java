package com.honeywell.wholesale.ui.report.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.honeywell.wholesale.R;
import com.honeywell.wholesale.ui.base.BaseTextView;
import com.honeywell.wholesale.ui.report.ChartSubListCommonItem;

import java.util.ArrayList;

/**
 * Created by xiaofei on 9/23/16.
 *
 */
public class CommonSubChartListAdapter extends BaseAdapter{

    private ArrayList<ChartSubListCommonItem> arrayList;
    private Context context;

    public CommonSubChartListAdapter(ArrayList<ChartSubListCommonItem> arrayList, Context context) {
        this.arrayList = arrayList;
        this.context = context;
    }

    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return arrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null){
            convertView = LayoutInflater.from(context).inflate(R.layout.item_sub_chart_list, parent, false);
        }

        BaseTextView titleTextView = (BaseTextView)convertView.findViewById(R.id.title_textView);
        BaseTextView valueTextView = (BaseTextView)convertView.findViewById(R.id.value_textView);

        ChartSubListCommonItem item = arrayList.get(position);
        titleTextView.setText(item.getTitle());
        valueTextView.setText(item.getValue());
        return convertView;
    }

    public void setDataSource(ArrayList<ChartSubListCommonItem> arrayList){
        this.arrayList = arrayList;
    }
}
