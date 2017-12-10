package com.honeywell.wholesale.ui.purchase.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;

import com.honeywell.wholesale.R;
import com.honeywell.wholesale.framework.model.ExtraCost;
import com.honeywell.wholesale.ui.base.BaseTextView;
import com.honeywell.wholesale.ui.transaction.preorders.ObserverCartData;

import java.util.ArrayList;

/**
 * Created by liuyang on 2017/7/5.
 */

public class ExtraCostAdapter extends BaseAdapter {

    private ArrayList<ExtraCost> arrayList;

    private ObserverCartData observerCartData;

    private Context context;

    public ExtraCostAdapter() {
    }

    public ExtraCostAdapter(Context context) {
        this.context = context;
    }

    public ExtraCostAdapter(ArrayList<ExtraCost> arrayList) {
        this.arrayList = arrayList;
    }

    public ExtraCostAdapter(ArrayList<ExtraCost> arrayList, Context context) {
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

        holder.titleTextView.setText(arrayList.get(position).getExtraCostName());

        return convertView;

    }

    private static class ViewHolder {
        BaseTextView titleTextView;
        EditText contentEditText;
    }

    public void setObserverCartData(ObserverCartData observerCartData) {
        this.observerCartData = observerCartData;
    }

    public void setDataSource(ArrayList arrayList) {
        this.arrayList = arrayList;
    }
}
