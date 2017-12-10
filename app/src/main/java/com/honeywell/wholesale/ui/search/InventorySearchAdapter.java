package com.honeywell.wholesale.ui.search;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.InputFilter;
import android.text.method.DigitsKeyListener;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.honeywell.wholesale.R;
import com.honeywell.wholesale.framework.model.Inventory;
import com.honeywell.wholesale.framework.model.PreCart;
import com.honeywell.wholesale.ui.base.BaseTextView;
import com.honeywell.wholesale.ui.transaction.preorders.CartModel;
import com.honeywell.wholesale.ui.transaction.preorders.ObserverCartData;
import com.honeywell.wholesale.ui.transaction.preorders.search.PreTransactionSkuActivity;

import java.util.ArrayList;

/**
 * Created by H154326 on 17/5/3.
 * Email: yang.liu6@honeywell.com
 */

public class InventorySearchAdapter extends BaseAdapter {

    private ArrayList arrayList;
    private Context context;

    public InventorySearchAdapter() {
    }

    public InventorySearchAdapter(Context context) {
        this.context = context;
    }

    public InventorySearchAdapter(ArrayList arrayList, Context context) {
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
            convertView = LayoutInflater.from(context).inflate(R.layout.item_inventory_search, parent, false);
            holder.iconImage = (ImageView) convertView.findViewById(R.id.pre_search_result_icon);
            holder.titleTextView = (BaseTextView) convertView.findViewById(R.id.pre_search_title_textView);
            holder.priceInfoTextView = (BaseTextView) convertView.findViewById(R.id.pre_search_price_info_textView);
            holder.stockTextView = (BaseTextView) convertView.findViewById(R.id.pre_search_stock_info_textView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Object obj = arrayList.get(position);

        if (obj instanceof Inventory) {
            final ViewHolder holder1 = holder;
            final Inventory inventory = (Inventory) obj;

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
            holder.stockTextView.setVisibility(View.VISIBLE);
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
    }

    public void setDataSource(ArrayList arrayList) {
        this.arrayList = arrayList;
    }
}
