package com.honeywell.wholesale.ui.dashboard.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.honeywell.wholesale.R;
import com.honeywell.wholesale.framework.model.Inventory;
import com.honeywell.wholesale.ui.base.BaseTextView;

import java.util.ArrayList;

/**
 * Created by xiaofei on 12/29/16.
 *
 */

public class InventoryAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<Inventory> arrayList;

    public InventoryAdapter(ArrayList<Inventory> arrayList, Context context) {
        this.context = context;
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
        ViewHolder holder;
        if (convertView == null){
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.item_inventory_list, parent, false);
            holder.inventoryImageView = (ImageView)convertView.findViewById(R.id.inventory_imageView);
            holder.inventoryTitle = (BaseTextView) convertView.findViewById(R.id.inventory_title_textView);
            holder.inventoryStock = (BaseTextView) convertView.findViewById(R.id.stock_value);
            holder.inventoryStockValue = (BaseTextView) convertView.findViewById(R.id.value_value);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        Inventory inventory = arrayList.get(position);
        String url = inventory.getPicSrc();

        if (url.equals("")){
            url = String.valueOf(R.drawable.inventory_default_icon);
        }

        holder.inventoryTitle.setText(inventory.getProductName());

        Glide
                .with( context )
                .load(url)
                .placeholder(R.drawable.inventory_default_icon)
                .error(R.drawable.inventory_default_icon)
                .priority(Priority.LOW)
                .into(holder.inventoryImageView);
        Log.e("chenjun", url);

        holder.inventoryStock.setText(inventory.getQuantity());

//        Float fPrice = (Float.parseFloat(inventory.getStandardPrice()));
//        String price = String.format("￥%s", String.valueOf(fPrice));

        String price = String.format("￥%s", inventory.getStandardPrice());

        holder.inventoryStockValue.setText(price);
        return convertView;
    }

    public void setDataSource(ArrayList<Inventory> arrayList){
        this.arrayList = arrayList;
    }

    static class ViewHolder {
        ImageView inventoryImageView;
        BaseTextView inventoryTitle;
        BaseTextView inventoryStock;
        BaseTextView inventoryStockValue;
    }

}

