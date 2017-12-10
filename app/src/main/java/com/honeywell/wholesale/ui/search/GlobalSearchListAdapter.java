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
import com.honeywell.wholesale.framework.model.Supplier;
import com.honeywell.wholesale.ui.base.BaseTextView;

import java.util.ArrayList;

/**
 * Created by xiaofei on 1/11/17.
 *
 */

public class GlobalSearchListAdapter extends BaseAdapter{
    private Context context;
    private ArrayList<GlobalSearchResultItem> arrayList;

    public GlobalSearchListAdapter(Context context, ArrayList<GlobalSearchResultItem> arrayList) {
        this.arrayList = arrayList;
        this.context = context;
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
        GlobalSearchResultItem item = arrayList.get(position);
        GlobalSearchResultItem.GlobalSearchResultType type = item.getType();

        if (type == GlobalSearchResultItem.GlobalSearchResultType.SEARCH_RESULT_SECTION){
            convertView = LayoutInflater.from(context).inflate(R.layout.item_global_search_section, parent, false);

            String section = item.getSection();
            BaseTextView baseTextView = (BaseTextView) convertView.findViewById(R.id.search_result_section);
            if (section != null){
                baseTextView.setText(section);
            }
        }

        if (type == GlobalSearchResultItem.GlobalSearchResultType.SEARCH_RESULT_INVENTORY){
            convertView = LayoutInflater.from(context).inflate(R.layout.item_global_search_inventory, parent, false);

            Inventory inventory = item.getInventory();
            ImageView productImage = (ImageView)convertView.findViewById(R.id.search_result_inventory_imageView);
            BaseTextView productNameTextView = (BaseTextView) convertView.findViewById(R.id.search_result_productName_textView);
            BaseTextView productPriceTextView = (BaseTextView) convertView.findViewById(R.id.search_result_productPrice_textView);
            BaseTextView productCodeTextView = (BaseTextView) convertView.findViewById(R.id.search_result_productCode_textView);
            BaseTextView productStockTextView = (BaseTextView) convertView.findViewById(R.id.search_result_productStock_textView);

            productNameTextView.setText(inventory.getProductName());

            String price = context.getString(R.string.global_search_product_price) + inventory.getStandardPrice();
            productPriceTextView.setText(price);

            String productCode = context.getString(R.string.global_search_product_code)+ " " + inventory.getProductCode();

            productCodeTextView.setText(productCode);

            String stockNumber = context.getString(R.string.global_search_product_stock) + inventory.getQuantity();
            productStockTextView.setText(stockNumber);

            Glide
                    .with(context)
                    .load(inventory.getPicSrc())
                    .placeholder(R.drawable.icon_search_inventory)
                    .error(R.drawable.icon_search_inventory)
                    .priority(Priority.LOW)
                    .into(productImage);
        }

        if (type == GlobalSearchResultItem.GlobalSearchResultType.SEARCH_RESULT_CUSTOMER){
            convertView = LayoutInflater.from(context).inflate(R.layout.item_global_search_customer, parent, false);

            Customer customer = item.getCustomer();

            ImageView imageView = (ImageView)convertView.findViewById(R.id.search_result_customer_imageView);
            BaseTextView customerName = (BaseTextView)convertView.findViewById(R.id.search_result_customerName_imageView);
            BaseTextView customerTele = (BaseTextView)convertView.findViewById(R.id.search_result_tele_textView);

            Glide
                    .with(context)
                    .load("")
                    .placeholder(R.drawable.left_menu_icon_customers)
                    .error(R.drawable.left_menu_icon_customers)
                    .priority(Priority.LOW)
                    .into(imageView);

            customerName.setText(customer.getCustomerName());
            customerTele.setText(customer.getContactPhone());
        }

        if (type == GlobalSearchResultItem.GlobalSearchResultType.SEARCH_RESULT_VENDOR){
            convertView = LayoutInflater.from(context).inflate(R.layout.item_global_search_supplier, parent, false);

            Supplier supplier = item.getSupplier();

            ImageView imageView = (ImageView)convertView.findViewById(R.id.search_result_supplier_imageView);
            BaseTextView supplierName = (BaseTextView)convertView.findViewById(R.id.search_result_supplierName_textView);
            BaseTextView supplierTele = (BaseTextView)convertView.findViewById(R.id.search_result_supplier_tele_textView);

            supplierName.setText(supplier.getSupplierName());
            supplierTele.setText(supplier.getContactPhone());

            Glide
                    .with(context)
                    .load("")
                    .placeholder(R.drawable.left_menu_icon_outlets)
                    .error(R.drawable.left_menu_icon_outlets)
                    .priority(Priority.LOW)
                    .into(imageView);
        }

        if (type == GlobalSearchResultItem.GlobalSearchResultType.SEARCH_RESULT_MORE){
            convertView = LayoutInflater.from(context).inflate(R.layout.item_global_search_more, parent, false);

            BaseTextView moreTextView = (BaseTextView) convertView.findViewById(R.id.search_result_more);
            String section = item.getSection();
            moreTextView.setText(section);
        }

        return convertView;
    }

    public void setAdapteData(ArrayList<GlobalSearchResultItem> arrayList){
        this.arrayList = arrayList;
        notifyDataSetChanged();
    }
}
