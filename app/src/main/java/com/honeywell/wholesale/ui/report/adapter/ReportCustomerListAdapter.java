package com.honeywell.wholesale.ui.report.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.honeywell.wholesale.R;
import com.honeywell.wholesale.ui.base.BaseTextView;
import com.honeywell.wholesale.ui.report.customer.CustomerChartEntity;
import com.honeywell.wholesale.ui.report.product.ProductChartEntity;

import java.util.ArrayList;

/**
 * Created by xiaofei on 9/26/16.
 *
 */
public class ReportCustomerListAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<CustomerChartEntity> arrayList;

    public ReportCustomerListAdapter( ArrayList<CustomerChartEntity> arrayList, Context context) {
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
            convertView = LayoutInflater.from(context).inflate(R.layout.item_customer_report_list, parent, false);
        }

        ImageView customerImageView = (ImageView)convertView.findViewById(R.id.report_customer_imageView);
        BaseTextView customerNameTextView = (BaseTextView)convertView.findViewById(R.id.report_customer_name);
        BaseTextView customerSaleAmount = (BaseTextView)convertView.findViewById(R.id.report_sales_amount);
        BaseTextView customerProfit = (BaseTextView)convertView.findViewById(R.id.report_customer_profit);
        BaseTextView customerReceive = (BaseTextView)convertView.findViewById(R.id.report_customer_receive);

        customerImageView.setImageResource(R.drawable.icon_search_customer);

        CustomerChartEntity item = arrayList.get(position);
        String contact = item.getCustomerName() + " | " + item.getContactName();
        customerNameTextView.setText(contact);
        customerSaleAmount.setText(String.format(context.getResources().getString(R.string.report_customer_sales),
                item.getIncomeAmount()));

        customerProfit.setText(String.format(context.getResources().getString(R.string.report_customer_profit),
                item.getProfitAmount()));
        customerReceive.setText(String.format(context.getResources().getString(R.string.report_customer_receive),
                item.getReceivable()));

        return convertView;
    }

    public void setDataSource(ArrayList<CustomerChartEntity> arrayList){
        this.arrayList = arrayList;
    }
}
