package com.honeywell.wholesale.ui.transaction.preorders.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.honeywell.wholesale.R;
import com.honeywell.wholesale.ui.base.BaseTextView;

import java.util.List;

/**
 * Created by xiaofei on 12/1/16.
 */

public class IndividualGuestAdapter extends BaseAdapter {
    private List<String> newGuestList;
    private Context context;

    public IndividualGuestAdapter(List<String> newGuestList, Context context) {
        this.newGuestList = newGuestList;
        this.context = context;
    }

    @Override
    public int getCount() {
        return newGuestList.size();
    }

    @Override
    public Object getItem(int position) {
        return newGuestList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null){
            convertView = LayoutInflater.from(context).inflate(R.layout.item_customer_individual_guest, parent, false);
        }

        BaseTextView textView = (BaseTextView)convertView.findViewById(R.id.new_guest_textView);
        String section = newGuestList.get(position);
        textView.setText(section);
        return convertView;
    }
}
