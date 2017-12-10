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
 * Created by H154326 on 16/10/16.
 * Email: yang.liu6@honeywell.com
 */

public class TransactionOrderCountAdapter  extends BaseAdapter{
    private Context context;
    private ArrayList<String> arrayList;

    public void setListDate(ArrayList<String> arrayList) {
        this.arrayList = arrayList;
    }

    public TransactionOrderCountAdapter(ArrayList<String> arrayList, Context context) {
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

        OrderCountViewHolder orderCountViewHolder = new OrderCountViewHolder();
        if (convertView == null){
            convertView = LayoutInflater.from(context).inflate(R.layout.fragment_transaction_native,parent,false);
            orderCountViewHolder.mOrderBillTextView = (BaseTextView) convertView.findViewById(R.id.tab_order_bill_no_payment);
            orderCountViewHolder.mOrderUnpaidTextView = (BaseTextView) convertView.findViewById(R.id.tab_order_credit);
            orderCountViewHolder.mOrderPaidTextView = (BaseTextView) convertView.findViewById(R.id.tab_order_paid);
            convertView.setTag(orderCountViewHolder);
        }
        else {
            orderCountViewHolder = (OrderCountViewHolder) convertView.getTag();
        }

        orderCountViewHolder.mOrderBillTextView.setText("1开单未付"+arrayList.get(0));
        orderCountViewHolder.mOrderUnpaidTextView.setText("1赊账中"+arrayList.get(1));
        orderCountViewHolder.mOrderPaidTextView.setText("1已结清"+arrayList.get(2));

        return convertView;
    }

    public void setTransactionOrderCountArraylist(ArrayList<String> transactionOrderCountArraylist){
        this.arrayList = transactionOrderCountArraylist;
        notifyDataSetChanged();
    }

    public static class OrderCountViewHolder{
        BaseTextView mOrderBillTextView ;//开单未付
        BaseTextView mOrderUnpaidTextView;//赊账中
        BaseTextView mOrderPaidTextView ;//已结清
    }


}
