package com.honeywell.wholesale.ui.search;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.honeywell.wholesale.R;
import com.honeywell.wholesale.framework.model.Customer;
import com.honeywell.wholesale.framework.model.Inventory;
import com.honeywell.wholesale.framework.model.Order;
import com.honeywell.wholesale.framework.model.Supplier;
import com.honeywell.wholesale.framework.search.SearchResultItem;
import com.honeywell.wholesale.framework.search.SearchResultItem.ResultType;
import com.honeywell.wholesale.ui.base.BaseTextView;
import com.honeywell.wholesale.ui.selectpic.Adapter.FolderRecyclerViewAdapter;

import java.util.ArrayList;

import static android.R.attr.order;
import static android.media.CamcorderProfile.get;
import static com.honeywell.wholesale.ui.search.SearchKey.customer;
import static com.honeywell.wholesale.ui.search.SearchKey.inventory;

/**
 * Created by xiaofei on 9/5/16.
 *
 */

public class SearchListAdapter extends BaseAdapter {
    private ArrayList arrayList;
    private Context context;
    private ResultType type;

    public SearchListAdapter(ArrayList arrayList, Context context, ResultType type) {
        this.arrayList = arrayList;
        this.context = context;
        this.type = type;
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
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.item_search_result_list, parent, false);
            holder.iconImage = (ImageView)convertView.findViewById(R.id.result_icon);
            holder.titleTextView = (BaseTextView) convertView.findViewById(R.id.title_textView);
            holder.infoTextView = (BaseTextView)convertView.findViewById(R.id.info_textView);
            holder.stockTextView = (BaseTextView)convertView.findViewById(R.id.stock_info_textView);
            convertView.setTag(holder);
        }else {
            holder = (ViewHolder) convertView.getTag();
        }

        if (type == ResultType.CUSTOMER){
            Object obj = arrayList.get(position);

            if (obj instanceof Customer){
                Customer customer = (Customer)obj;
                holder.titleTextView.setText(customer.getContactName());
                String info = customer.getCustomerName() + " " + customer.getContactPhone();
                holder.infoTextView.setText(info);
                holder.stockTextView.setVisibility(View.GONE);
                holder.iconImage.setBackgroundResource(R.drawable.icon_search_customer);
            }
        }

        if (type == ResultType.INVENTORY){
            Object obj = arrayList.get(position);

            if (obj instanceof Inventory){
                Inventory inventory = (Inventory)obj;
                holder.titleTextView.setText(inventory.getProductName());

                String info;
                if (inventory.getProductNumber() == null){
                    info = context.getString(R.string.search_product_number);
                }else if(inventory.getProductNumber().equals("null")){
                    info = context.getString(R.string.search_product_number);
                }else {
                    info = context.getString(R.string.search_product_number) + inventory.getProductNumber();
                }

                String url = inventory.getPicSrc();
                if (url == null){
                    url = "";
                }
                if (url.equals("")){
                    url = String.valueOf(R.drawable.inventory_default_icon);
                }
                holder.infoTextView.setText(info);
                //iconImage.setBackgroundResource(R.drawable.icon_search_inventory);

                String stock = context.getString(R.string.search_product_stock) + inventory.getQuantity();
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
        }

        if (type == ResultType.VENDOR){
            Object obj = arrayList.get(position);

            if (obj instanceof Supplier){
                Supplier supplier = (Supplier)obj;
                holder.titleTextView.setText(supplier.getContactName());
                String info = supplier.getSupplierName() + " " + supplier.getContactPhone();
                holder.infoTextView.setText(info);
                holder.iconImage.setBackgroundResource(R.drawable.icon_search_vendor);
                holder.stockTextView.setVisibility(View.GONE);
            }
        }

        if (type == ResultType.TRANSACTION){
            Object obj = arrayList.get(position);

            if (obj instanceof  Order){
                Order order = (Order)obj;
                Order.Product[] products = order.getSaleList();

                String title = order.getSaleTime();
                for (Order.Product product: products) {
                    title = title + product.mName;
                }

                holder.stockTextView.setVisibility(View.GONE);
                holder.titleTextView.setText(title);
                String info = order.getCustomerName();
                holder.infoTextView.setText(info);
                holder.iconImage.setBackgroundResource(R.drawable.icon_search_vendor);
            }
        }

        return convertView;
    }

    private class ViewHolder{
        private ImageView iconImage;
        private BaseTextView titleTextView;
        private BaseTextView infoTextView;
        private BaseTextView stockTextView;
    }

    public void setDataSource(ArrayList arrayList, SearchResultItem.ResultType type){
        this.type = type;
        this.arrayList = arrayList;
    }
}
