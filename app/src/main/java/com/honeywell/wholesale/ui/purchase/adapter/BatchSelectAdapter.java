package com.honeywell.wholesale.ui.purchase.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;

import com.honeywell.wholesale.R;
import com.honeywell.wholesale.framework.model.BatchModel;
import com.honeywell.wholesale.framework.model.ExtraCost;
import com.honeywell.wholesale.ui.base.BaseTextView;

import java.util.ArrayList;

/**
 * Created by liuyang on 2017/7/5.
 */

public class BatchSelectAdapter extends BaseAdapter {
    private ArrayList<BatchModel> arrayList;

    private Context context;

    public BatchSelectAdapter() {
    }

    public BatchSelectAdapter(Context context) {
        this.context = context;
    }

    public BatchSelectAdapter(ArrayList<BatchModel> arrayList) {
        this.arrayList = arrayList;
    }

    public BatchSelectAdapter(ArrayList<BatchModel> arrayList, Context context) {
        this.arrayList = arrayList;
        this.context = context;
    }

    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = new ViewHolder();
        if (convertView == null) {

            convertView = LayoutInflater.from(context).inflate(R.layout.item_purchase_result_list, parent, false);

            holder.titleTextView = (BaseTextView) convertView.findViewById(R.id.pre_search_title_textView);
            holder.contentEditText = (EditText) convertView.findViewById(R.id.content_edit_text);

            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }

//        holder.titleTextView.setText(arrayList.get(position).get());

        return convertView;

    }

    private static class ViewHolder {
        BaseTextView titleTextView;
        EditText contentEditText;
    }

    public void setDataSource(ArrayList arrayList) {
        this.arrayList = arrayList;
    }
}
