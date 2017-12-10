package com.honeywell.wholesale.ui.transaction.cart.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.honeywell.wholesale.R;
import com.honeywell.wholesale.framework.model.CartItem;
import com.honeywell.wholesale.ui.base.BaseTextView;

import java.util.ArrayList;

/**
 * Created by H154326 on 16/11/30.
 * Email: yang.liu6@honeywell.com
 */

public class CartManagementAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<CartItem> arrayList;
    private ImageLoader imageLoader;

    public CartManagementAdapter(Context context, ArrayList<CartItem> arrayList, ImageLoader imageLoader) {
        this.context = context;
        this.arrayList = arrayList;
        this.imageLoader = imageLoader;
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

        CartItemViewHolder holder = new CartItemViewHolder();

        if (convertView == null){
            convertView = LayoutInflater.from(context).inflate(R.layout.item_cart_list, parent,false);

            holder.selectImageView = (ImageView) convertView.findViewById(R.id.cart_management_select_imageView);
            holder.productImageView = (ImageView) convertView.findViewById(R.id.cart_management_product_imagmeView);
            holder.productNameTextView = (BaseTextView) convertView.findViewById(R.id.cart_management_product_name_textView);
            holder.priceTextView = (BaseTextView) convertView.findViewById(R.id.cart_management_product_price_textView);
            holder.amountTextView = (BaseTextView) convertView.findViewById(R.id.cart_management_amount_textview);
            convertView.setTag(holder);
        } else {
            holder = (CartItemViewHolder) convertView.getTag();
        }

        CartItem cartItem = arrayList.get(position);
        String url;
        url = cartItem.getImageUrl();

        if (url == null){
            url = "";
        }else {
            url = cartItem.getFirstImageUrl();
        }

        if (url.equals("")){
            url = String.valueOf(R.drawable.inventory_default_icon);
        }

        Glide
                .with( context )
                .load(url)
                .placeholder(R.drawable.inventory_default_icon)
                .error(R.drawable.inventory_default_icon)
                .priority(Priority.LOW)
                .into(holder.productImageView);


        holder.productNameTextView.setText(cartItem.getProductName());
        holder.priceTextView.setText(cartItem.getUnitPrice() + "/" + cartItem.getUnit());
        holder.amountTextView.setText("X " + cartItem.getTotalNumber());

        updateBackground(position, convertView, parent, holder);

        return convertView;
    }

    public void updateBackground(int position, View view, ViewGroup parent, CartItemViewHolder holder) {
        int backgroundId;
        ListView listView = (ListView) parent;
        if (listView.isItemChecked(position)) {
            backgroundId = R.drawable.shop_management_selected;
        } else {
            backgroundId = R.drawable.shop_management_unselected;
        }
        Drawable background = context.getResources().getDrawable(backgroundId);
        holder.selectImageView.setBackground(background);
    }

    public static class CartItemViewHolder{
        ImageView selectImageView;
        ImageView productImageView;
        BaseTextView productNameTextView;
        BaseTextView priceTextView;
        BaseTextView amountTextView;
    }
}
