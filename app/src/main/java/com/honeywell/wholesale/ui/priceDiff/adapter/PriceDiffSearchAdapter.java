package com.honeywell.wholesale.ui.priceDiff.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.honeywell.wholesale.R;
import com.honeywell.wholesale.framework.model.Inventory;
import com.honeywell.wholesale.ui.base.BaseTextView;
import com.honeywell.wholesale.ui.saleReturn.PreSaleReturnSkuSelectActicity;
import com.honeywell.wholesale.ui.transaction.preorders.CartModel;

import java.util.ArrayList;

/**
 * Created by liuyang on 2017/5/26.
 */

public class PriceDiffSearchAdapter extends BaseAdapter {

    private ArrayList arrayList;
    private Context context;

    private int wareHouseId;

    private CartModel cartModel = CartModel.getInstance();

    public PriceDiffSearchAdapter(Context context) {
        this.context = context;
    }

    public PriceDiffSearchAdapter(ArrayList arrayList, Context context) {
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
            convertView = LayoutInflater.from(context).inflate(R.layout.item_price_diff_result_list, parent, false);

            holder.iconImage = (ImageView) convertView.findViewById(R.id.pre_search_result_icon);
            holder.titleTextView = (BaseTextView) convertView.findViewById(R.id.pre_search_title_textView);
            holder.priceInfoTextView = (BaseTextView) convertView.findViewById(R.id.pre_search_price_info_textView);
            holder.stockTextView = (BaseTextView) convertView.findViewById(R.id.pre_search_stock_info_textView);
            holder.relativeLayout = (RelativeLayout) convertView.findViewById(R.id.pre_search_text_layout);
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
                holder.relativeLayout.setBackground(context.getResources().getDrawable(R.color.wholesale_base_skin_color));
            }else {
                holder.relativeLayout.setBackground(context.getResources().getDrawable(R.color.white));
            }
            holder.titleTextView.setText(inventory.getProductName());

            final String info;
            info = "¥  " + inventory.getStandardPrice();
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
        }
        return convertView;
    }

    private static class ViewHolder {
        ImageView iconImage;
        BaseTextView titleTextView;
        BaseTextView priceInfoTextView;
        BaseTextView stockTextView;
        RelativeLayout relativeLayout;
    }

    public void setDataSource(ArrayList arrayList, int wareHouseId) {
        this.arrayList = arrayList;
        this.wareHouseId = wareHouseId;
    }

}
