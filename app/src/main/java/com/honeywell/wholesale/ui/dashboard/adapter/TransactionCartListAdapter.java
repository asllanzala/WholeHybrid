package com.honeywell.wholesale.ui.dashboard.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.honeywell.wholesale.R;
import com.honeywell.wholesale.framework.model.Cart;
import com.honeywell.wholesale.framework.model.PreCartOrder;
import com.honeywell.wholesale.framework.model.PreOrder;
import com.honeywell.wholesale.ui.base.BaseTextView;

import java.util.ArrayList;

/**
 * Created by H154326 on 16/11/29.
 * Email: yang.liu6@honeywell.com
 */

public class TransactionCartListAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<Cart> arrayList;

    public TransactionCartListAdapter(ArrayList<Cart> arrayList, Context context) {
        this.context = context;
        this.arrayList = arrayList;
    }

    public ArrayList<Cart> getArrayList() {
        return arrayList;
    }

    public void setArrayList(ArrayList<Cart> arrayList) {
        this.arrayList = arrayList;
    }

    public void clearListData(){
        this.arrayList.clear();
        notifyDataSetChanged();
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
        CartViewHolder holder = new CartViewHolder();
        if (convertView == null){
            convertView = LayoutInflater.from(context).inflate(R.layout.fragment_transaction_order_listview_item, parent, false);
            holder.customerName = (BaseTextView) convertView.findViewById(R.id.order_listview_item_customer_name_textView);
            holder.saleAmount = (BaseTextView) convertView.findViewById(R.id.order_listview_item_sale_amount_textView);
            //holder.productsName = (BaseTextView) convertView.findViewById(R.id.order_listview_item_products_textView);
            holder.contactPhone = (BaseTextView) convertView.findViewById(R.id.order_listview_item_products_textView);
            holder.orderTime = (BaseTextView) convertView.findViewById(R.id.order_listview_item_order_time_textView);
            holder.employeeName = (BaseTextView) convertView.findViewById(R.id.order_listview_item_employee_name_textView);
            holder.totalMoney = (BaseTextView) convertView.findViewById(R.id.order_listview_item_total_money_textView);
            holder.orderDeadLine = (BaseTextView) convertView.findViewById(R.id.order_listview_item_deadline_textView);
            holder.orderStatus = (BaseTextView) convertView.findViewById(R.id.order_listview_item_status_tag_textView);
            holder.signText = (BaseTextView) convertView.findViewById(R.id.order_listview_item_sign_textView);
            holder.orderNumber = (BaseTextView) convertView.findViewById(R.id.order_listview_item_order_number_textView);

            convertView.setTag(holder);
        }else{
            holder = (CartViewHolder) convertView.getTag();
        }

        holder.saleAmount.setVisibility(View.VISIBLE);
        holder.totalMoney.setVisibility(View.GONE);
        holder.employeeName.setVisibility(View.GONE);
        holder.orderTime.setVisibility(View.GONE);
        holder.orderDeadLine.setVisibility(View.GONE);
        holder.orderStatus.setVisibility(View.GONE);
        holder.signText.setVisibility(View.GONE);
        holder.orderNumber.setVisibility(View.GONE);

        Cart cart = arrayList.get(position);

        holder.customerName.setText(cart.getCustomerName());
        holder.saleAmount.setText("共" + cart.getCartNumber() + "种");
        holder.contactPhone.setText(cart.getContactPhone());


        return convertView;
    }

    private static class CartViewHolder {

        BaseTextView customerName;
        BaseTextView saleAmount;
        BaseTextView productsName;
        BaseTextView contactPhone;
        BaseTextView orderTime;
        BaseTextView employeeName;
        BaseTextView totalMoney;
        BaseTextView orderDeadLine;
        BaseTextView orderStatus;
        BaseTextView signText;
        BaseTextView orderNumber;

    }
}
