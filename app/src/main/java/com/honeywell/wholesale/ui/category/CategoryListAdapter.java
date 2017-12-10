package com.honeywell.wholesale.ui.category;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.honeywell.wholesale.R;
import com.honeywell.wholesale.framework.model.Category;
import com.honeywell.wholesale.ui.base.BaseTextView;

import java.util.ArrayList;

/**
 * Created by xiaofei on 12/26/16.
 *
 */

public class CategoryListAdapter extends BaseAdapter{

    private Context context;
    private ArrayList<Category> arrayList;

    public CategoryListAdapter(Context context, ArrayList<Category> arrayList) {
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
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null){
            convertView = LayoutInflater.from(context).inflate(R.layout.item_modify_category_list, parent, false);
            holder = new ViewHolder();
            holder.categoryName = (BaseTextView)convertView.findViewById(R.id.category_modify_textView);
            convertView.setTag(holder);
        }else {
            holder = (ViewHolder) convertView.getTag();
        }

        Category category = arrayList.get(position);
        String name  = category.getName();
        holder.categoryName.setText(name);

        return convertView;
    }

    private static class ViewHolder {
        BaseTextView categoryName;
    }

    public void setData(ArrayList<Category> arrayList){
        this.arrayList = arrayList;
        notifyDataSetChanged();
    }
}
