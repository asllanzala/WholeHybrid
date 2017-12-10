package com.honeywell.wholesale.ui.purchase.adapter;

import android.content.Context;
import android.content.Intent;
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
import com.honeywell.wholesale.ui.purchase.activity.PurchaseSkuSelectActicity;
import com.honeywell.wholesale.ui.saleReturn.PreSaleReturnSkuSelectActicity;
import com.honeywell.wholesale.ui.transaction.preorders.CartModel;

import java.util.ArrayList;

/**
 * Created by liuyang on 2017/5/26.
 */

public class PurchaseSearchAdapter extends BaseAdapter {

    private ArrayList arrayList;
    private Context context;

    private CartModel cartModel = CartModel.getInstance();

    public PurchaseSearchAdapter(Context context) {
        this.context = context;
    }

    public PurchaseSearchAdapter(ArrayList arrayList, Context context) {
        this.arrayList = arrayList;
        this.context = context;
    }


    @Override
    public int getCount() {
        if (arrayList != null) {
            return arrayList.size();
        }else {
            return 0;
        }
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
        ViewHolder holder = new ViewHolder();
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_purchase_result_list, parent, false);
            holder.iconImage = (ImageView) convertView.findViewById(R.id.pre_search_result_icon);
            holder.titleTextView = (BaseTextView) convertView.findViewById(R.id.pre_search_title_textView);
            holder.priceInfoTextView = (BaseTextView) convertView.findViewById(R.id.pre_search_price_info_textView);
            holder.stockTextView = (BaseTextView) convertView.findViewById(R.id.pre_search_stock_info_textView);
            holder.countTextView = (BaseTextView) convertView.findViewById(R.id.pre_search_product_no_text_view);

            holder.minus = convertView.findViewById(R.id.pre_search_product_no_minus);
            holder.add = convertView.findViewById(R.id.pre_search_product_no_add);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }


        holder.stockTextView.setVisibility(View.GONE);
        Object obj = arrayList.get(position);

        if (obj instanceof Inventory) {
            final ViewHolder holder1 = holder;
            final Inventory inventory = (Inventory) obj;
            if (cartModel.checkProductExist(inventory.getProductId())){
                holder.countTextView.setText(cartModel.getById(inventory.getProductId()).getTotalNumber());
                Log.e("sada", "11" + cartModel.getById(inventory.getProductId()).getTotalNumber());
                holder.minus.setVisibility(View.VISIBLE);
            }else {
                holder.countTextView.setText("");
                holder.minus.setVisibility(View.GONE);
            }
            holder.titleTextView.setText(inventory.getProductName());
            final String info;
            info = "¥  " + inventory.getStockPrice();
            String url = inventory.getPicSrc();
            if (url == null) {
                url = "";
            }
            if (url.equals("")) {
                url = String.valueOf(R.drawable.inventory_default_icon);
            }
            holder.priceInfoTextView.setText(info);

            if (inventory.getUnit() == null) {
                inventory.setUnit("");
            }
            String stock = context.getString(R.string.search_product_stock) + "  " + inventory.getQuantity() + "  " + inventory.getUnit();
            holder.stockTextView.setText(stock);
            Glide
                    .with(context)
                    .load(url)
                    .placeholder(R.drawable.inventory_default_icon)
                    .error(R.drawable.inventory_default_icon)
                    .priority(Priority.LOW)
                    .into(holder.iconImage);
            if (holder.countTextView.getText().toString().equals("")) {
                holder.minus.setVisibility(View.GONE);
            } else {
                holder.minus.setVisibility(View.VISIBLE);
            }
        }
        return convertView;
    }

    private static class ViewHolder {
        ImageView iconImage;
        BaseTextView titleTextView;
        BaseTextView priceInfoTextView;
        BaseTextView stockTextView;
        BaseTextView countTextView;
        View minus;
        View add;
    }

    public void setDataSource(ArrayList arrayList) {
        this.arrayList = arrayList;
    }

}
