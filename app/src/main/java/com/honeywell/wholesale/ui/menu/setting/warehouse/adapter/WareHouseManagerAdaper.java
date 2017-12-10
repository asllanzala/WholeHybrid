package com.honeywell.wholesale.ui.menu.setting.warehouse.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.honeywell.wholesale.R;
import com.honeywell.wholesale.framework.model.Customer;
import com.honeywell.wholesale.framework.model.WareHouse;
import com.honeywell.wholesale.ui.base.BaseTextView;
import com.honeywell.wholesale.ui.menu.setting.warehouse.ObserverDefaultWareHouse;
import com.honeywell.wholesale.ui.menu.setting.warehouse.WareHouseUpdateActivity;
import com.honeywell.wholesale.ui.transaction.preorders.ObserverCartData;

import java.util.ArrayList;

/**
 * Created by H154326 on 17/4/26.
 * Email: yang.liu6@honeywell.com
 */

public class WareHouseManagerAdaper extends BaseAdapter {
    private ArrayList<WareHouse> arrayList;
    private Context context;

    private ObserverDefaultWareHouse observerDefaultWareHouse;

    public WareHouseManagerAdaper() {
    }

    public WareHouseManagerAdaper(Context context) {
        this.context = context;
    }

    public WareHouseManagerAdaper(ArrayList<WareHouse> arrayList, Context context) {
        this.arrayList = arrayList;
        this.context = context;
    }

    public ArrayList<WareHouse> getArrayList() {
        return arrayList;
    }

    public void setArrayList(ArrayList<WareHouse> arrayList) {
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
            convertView = LayoutInflater.from(context).inflate(R.layout.item_warehouse_manager, parent, false);
            holder.nameTextView = (BaseTextView) convertView.findViewById(R.id.item_warehouse_name_textview);
            holder.settingTextView = (BaseTextView) convertView.findViewById(R.id.item_warehouse_setting_textview);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final WareHouse currentWareHouse = arrayList.get(position);
        holder.nameTextView.setText(arrayList.get(position).getWareHouseName());
        holder.settingTextView.setText(R.string.warehouse_setting);
        holder.settingTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                observerDefaultWareHouse.defaultWareHouse(currentWareHouse.getWareHouseId(), currentWareHouse.getWareHouseName());
            }
        });
        return convertView;
    }

    public void setObserverCartData(ObserverDefaultWareHouse observerDefaultWareHouse) {
        this.observerDefaultWareHouse = observerDefaultWareHouse;
    }

    private static class ViewHolder {
        BaseTextView nameTextView;
        BaseTextView settingTextView;
    }
}
