package com.honeywell.wholesale.ui.purchase.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import com.honeywell.wholesale.R;
import com.honeywell.wholesale.framework.model.Supplier;
import com.honeywell.wholesale.ui.base.BaseTextView;
import java.util.ArrayList;

/**
 * Created by liuyang on 2017/7/5.
 */

public class SupplierSelectAdapter extends BaseAdapter {
    private static final int TYPE_ITEM = 0;
    private static final int TYPE_SECTION = 1;

    private ArrayList<Object> arrayList;

    private Context context;


    public SupplierSelectAdapter(Context context, ArrayList<Object> arrayList) {
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
    public int getItemViewType(int position) {
        return (arrayList.get(position) instanceof String)? TYPE_SECTION : TYPE_ITEM;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
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
            }else {
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
                holder.csupplierContact = (BaseTextView)convertView.findViewById(R.id.supplier_contact_name_textView);
                holder.supplierTele = (BaseTextView)convertView.findViewById(R.id.supplier_tele_textView);
                convertView.setTag(holder);
            }else {
                holder = (ViewHolder) convertView.getTag();
            }
            Supplier supplier = (Supplier)arrayList.get(position);
            holder.supplierName.setText(supplier.getSupplierName());
            holder.csupplierContact.setText(supplier.getContactName() + ",");
            holder.supplierTele.setText(supplier.getContactPhone());

        }
        return convertView;
    }

    private static class ViewHolder {
        BaseTextView supplierName;
        BaseTextView csupplierContact;
        BaseTextView supplierTele;
        BaseTextView supplierSection;
    }
}
