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
 * Created by xiaofei on 10/12/16.
 */

public class TransactionModelSelecteAdapter extends BaseAdapter {

    private ArrayList<String> transactionModeArraylist;
    private Context context;

    public TransactionModelSelecteAdapter(ArrayList<String> transactionModeArraylist, Context context){
        this.transactionModeArraylist = transactionModeArraylist;
        this.context = context;
    }

    @Override
    public int getCount() {
        return transactionModeArraylist.size();
    }

    @Override
    public Object getItem(int position) {
        return transactionModeArraylist.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null){
            convertView = LayoutInflater.from(context).inflate(R.layout.item_popup_transaction_model,parent,false);
        }

        BaseTextView baseTextView = (BaseTextView)convertView.findViewById(R.id.item_transaction_model_textview);
        baseTextView.setText(transactionModeArraylist.get(position));
        return convertView;
    }

    public void setTransactionModelArraylist(ArrayList<String> transactionModeArraylist){
        this.transactionModeArraylist = transactionModeArraylist;
        notifyDataSetChanged();
    }
}
