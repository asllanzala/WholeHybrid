package com.honeywell.wholesale.ui.report.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.honeywell.wholesale.R;
import com.honeywell.wholesale.framework.http.WebClient;
import com.honeywell.wholesale.ui.base.BaseTextView;
import com.honeywell.wholesale.ui.report.product.ProductChartEntity;

import java.util.ArrayList;

/**
 * Created by xiaofei on 9/26/16.
 *
 */
public class ReportProductListAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<ProductChartEntity> arrayList;
    private ImageLoader imageLoader = WebClient.getImageLoader();

    public ReportProductListAdapter( ArrayList<ProductChartEntity> arrayList, Context context) {
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
        if (convertView == null){
            convertView = LayoutInflater.from(context).inflate(R.layout.item_product_report_list, parent, false);
        }

        NetworkImageView productImageView = (NetworkImageView)convertView.findViewById(R.id.report_product_imageView);
        BaseTextView productTextView = (BaseTextView)convertView.findViewById(R.id.report_product_name);
        BaseTextView productSaleAmount = (BaseTextView)convertView.findViewById(R.id.report_sales_amount);
        BaseTextView productSaleNumber = (BaseTextView)convertView.findViewById(R.id.report_sales_number);
        BaseTextView productProfit = (BaseTextView)convertView.findViewById(R.id.report_product_profit);
        BaseTextView productStock = (BaseTextView)convertView.findViewById(R.id.report_product_stock);

        ProductChartEntity item = arrayList.get(position);
        String url = item.getPicSrc();
        imageLoader.get(url,
                ImageLoader.getImageListener(productImageView,
                        android.R.drawable.ic_dialog_alert,
                        android.R.drawable.ic_dialog_alert));

        productImageView.setImageUrl(url, imageLoader);


        productTextView.setText(item.getProductName());
        productSaleAmount.setText(String.format(context.getResources().getString(R.string.report_product_sales),
                item.getIncome()));

        productSaleNumber.setText(String.format(context.getResources().getString(R.string.report_product_sales_number),
                item.getSaleQuantity()));

        productProfit.setText(String.format(context.getResources().getString(R.string.report_product_profit),
                item.getProfit()));
        productStock.setText(String.format(context.getResources().getString(R.string.report_product_inventory),
                item.getInventoryQuantity()));

        return convertView;
    }

    public void setDataSource(ArrayList<ProductChartEntity> arrayList){
        this.arrayList = arrayList;
    }
}
