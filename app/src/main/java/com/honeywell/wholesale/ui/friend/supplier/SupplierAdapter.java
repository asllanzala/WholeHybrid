package com.honeywell.wholesale.ui.friend.supplier;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.honeywell.wholesale.R;
import com.honeywell.wholesale.framework.model.Supplier;
import com.honeywell.wholesale.ui.base.BaseTextView;
import com.honeywell.wholesale.ui.dashboard.adapter.InventoryAdapter;

import java.util.ArrayList;


/**
 * Created by H154326 on 16/12/21.
 * Email: yang.liu6@honeywell.com
 */

public class SupplierAdapter extends BaseAdapter {
    private static final int TYPE_ITEM = 0;
    private static final int TYPE_SECTION = 1;
    private Context context;
    private ArrayList<Object> arrayList;


    public SupplierAdapter(Context context, ArrayList<Object> arrayList) {
        this.context = context;
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
    public int getItemViewType(int position) {
        return (arrayList.get(position) instanceof String)? TYPE_SECTION : TYPE_ITEM;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    public void setListData(ArrayList<Object> arrayList){
        this.arrayList = arrayList;
    }

    public void clearListData(){
        this.arrayList.clear();
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (arrayList.get(position) instanceof String){
            if (convertView == null){
                holder = new ViewHolder();
                convertView = LayoutInflater.from(context).inflate(R.layout.item_supplier_list_section, parent, false);
                holder.supplierSection = (BaseTextView)convertView.findViewById(R.id.supplier_list_section);
                convertView.setTag(holder);
            }else{
                holder = (ViewHolder) convertView.getTag();
            }
            String section = (String) arrayList.get(position);
            holder.supplierSection.setText(section);
        }

        if (arrayList.get(position) instanceof Supplier){
            if (convertView == null){
                holder = new ViewHolder();
                convertView = LayoutInflater.from(context).inflate(R.layout.item_supplier_list_view, parent, false);
                holder.supplierName = (BaseTextView)convertView.findViewById(R.id.supplier_name_textView);
                holder.supplierContact = (BaseTextView)convertView.findViewById(R.id.supplier_contact_name_textView);
                holder.supplierTele = (BaseTextView)convertView.findViewById(R.id.supplier_tele_textView);
                convertView.setTag(holder);

            }else{
                holder = (ViewHolder) convertView.getTag();
            }
            Supplier supplier = (Supplier) arrayList.get(position);
            holder.supplierName.setText(supplier.getSupplierName());
            holder.supplierContact.setText(supplier.getContactName() + ",");
            holder.supplierTele.setText(supplier.getContactPhone());
        }

        return convertView;
    }

    private static class ViewHolder {
        BaseTextView supplierName;
        BaseTextView supplierContact;
        BaseTextView supplierTele;
        BaseTextView supplierSection;
    }
}
