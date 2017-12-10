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
 * Created by H154326 on 16/12/7.
 * Email: yang.liu6@honeywell.com
 */

public class TransactionMoreAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<String> arrayList;

    public TransactionMoreAdapter(Context context, ArrayList<String> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    public void clearListData(){
        if (!this.arrayList.isEmpty()) {
            this.arrayList.clear();
            notifyDataSetChanged();
        }
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
            convertView = LayoutInflater.from(context).inflate(R.layout.item_popup_transaction_more,parent,false);
        }
        BaseTextView baseTextView = (BaseTextView)convertView.findViewById(R.id.item_transaction_more_textview);
        baseTextView.setText(arrayList.get(position));
        return convertView;
    }
}
