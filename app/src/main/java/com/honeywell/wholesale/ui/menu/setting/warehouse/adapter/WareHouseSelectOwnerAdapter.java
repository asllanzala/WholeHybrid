package com.honeywell.wholesale.ui.menu.setting.warehouse.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.honeywell.wholesale.R;
import com.honeywell.wholesale.framework.model.WareHouseOwnerWithFlag;
import com.honeywell.wholesale.ui.base.BaseTextView;

import java.util.ArrayList;

/**
 * Created by H154326 on 17/4/27.
 * Email: yang.liu6@honeywell.com
 */

public class WareHouseSelectOwnerAdapter extends BaseAdapter {
    private ArrayList<WareHouseOwnerWithFlag> arrayList;
    private Context context;

    public WareHouseSelectOwnerAdapter() {
    }

    public WareHouseSelectOwnerAdapter(Context context) {
        this.context = context;
    }

    public WareHouseSelectOwnerAdapter(ArrayList<WareHouseOwnerWithFlag> arrayList, Context context) {
        this.arrayList = arrayList;
        this.context = context;
    }

    public ArrayList<WareHouseOwnerWithFlag> getArrayList() {
        return arrayList;
    }

    public void setArrayList(ArrayList<WareHouseOwnerWithFlag> arrayList) {
        this.arrayList = arrayList;
    }

    @Override
    public int getCount() {
        if (arrayList != null) {
            return arrayList.size();
        } else {
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
            convertView = LayoutInflater.from(context).inflate(R.layout.item_warehouse_select_owner, parent, false);
            holder.nameTextView = (BaseTextView) convertView.findViewById(R.id.item_warehouse_select_owner_name_textview);
            holder.settingImageView = (ImageView) convertView.findViewById(R.id.item_warehouse_select_owner_setting_imageview);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final WareHouseOwnerWithFlag wareHouseOwnerWithFlag = arrayList.get(position);
        holder.nameTextView.setText(arrayList.get(position).getEmployeeName());
        if (wareHouseOwnerWithFlag.isOwner()){
            holder.nameTextView.setTextColor(context.getResources().getColor(R.color.warehouse_selected_color));
            holder.settingImageView.setVisibility(View.VISIBLE);
        }else {
            holder.nameTextView.setTextColor(context.getResources().getColor(R.color.black));
            holder.settingImageView.setVisibility(View.GONE);
        }
        return convertView;
    }

    private static class ViewHolder {
        BaseTextView nameTextView;
        ImageView settingImageView;
    }
}
