package com.honeywell.wholesale.ui.menu.shop.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RadioButton;

import com.honeywell.wholesale.R;
import com.honeywell.wholesale.framework.model.Account;
import com.honeywell.wholesale.framework.model.Shop;
import com.honeywell.wholesale.ui.base.BaseTextView;
import com.honeywell.wholesale.ui.dashboard.CircleImageView;
import com.honeywell.wholesale.ui.login.module.LoginWebServiceResponse;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by H154326 on 16/11/25.
 * Email: yang.liu6@honeywell.com
 */

public class ShopAdapter extends BaseAdapter {

    private int position;
    private Context context;
    private List<LoginWebServiceResponse.LoginShopResponse> shopArrayList;
    private String currentShopName;


    public ShopAdapter(Context context, List<LoginWebServiceResponse.LoginShopResponse> shopArrayList) {
        this.context = context;
        this.shopArrayList = shopArrayList;
    }

    public ShopAdapter(Context context, List<LoginWebServiceResponse.LoginShopResponse> shopArrayList, String currentShopName) {
        this.context = context;
        this.shopArrayList = shopArrayList;
        this.currentShopName = currentShopName;
    }

    public String getCurrentShopName() {
        return currentShopName;
    }

    public void setCurrentShopName(String currentShopName) {
        this.currentShopName = currentShopName;
    }

    @Override
    public int getCount() {
        return shopArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return shopArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ShopViewHolder shopViewHolder = new ShopViewHolder();

        if(convertView == null){
            convertView = LayoutInflater.from(context).inflate(R.layout.item_shop_list,parent,false);
            shopViewHolder.shopImageView = (ImageView) convertView.findViewById(R.id.shop_simagmeView);
            shopViewHolder.baseTextView = (BaseTextView) convertView.findViewById(R.id.shop_name_textView);
            shopViewHolder.selectImageView = (ImageView) convertView.findViewById(R.id.shop_select_imageView);

            convertView.setTag(shopViewHolder);
        } else {
            shopViewHolder = (ShopViewHolder) convertView.getTag();
        }

        if (this.position == position){
            shopViewHolder.selectImageView.setImageResource(R.drawable.shop_management_selected);
        }else {
            shopViewHolder.selectImageView.setImageResource(R.drawable.shop_management_unselected);
        }

        LoginWebServiceResponse.LoginShopResponse shop = shopArrayList.get(position);
        shopViewHolder.baseTextView.setText(shop.getmShopName());

        return convertView;
    }

    public static class ShopViewHolder {
        ImageView shopImageView;
        BaseTextView baseTextView;
        ImageView selectImageView;
    }


    public void setPositionChecked(int position){
        this.position = position;
    }
}
