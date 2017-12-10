package com.honeywell.wholesale.ui.transaction.cart.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.honeywell.wholesale.R;
import com.honeywell.wholesale.ui.base.BaseTextView;

import java.util.ArrayList;

/**
 * Created by H154326 on 16/12/5.
 * Email: yang.liu6@honeywell.com
 */

public class CartMoreAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<String> arrayList;

    public CartMoreAdapter(Context context, ArrayList<String> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public ArrayList<String> getArrayList() {
        return arrayList;
    }

    public void setArrayList(ArrayList<String> arrayList) {
        this.arrayList = arrayList;
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
            convertView = LayoutInflater.from(context).inflate(R.layout.item_popup_cart_more,parent,false);
        }
        BaseTextView baseTextView = (BaseTextView)convertView.findViewById(R.id.item_cart_more_textview);
        baseTextView.setText(arrayList.get(position));
        return convertView;
    }
}
