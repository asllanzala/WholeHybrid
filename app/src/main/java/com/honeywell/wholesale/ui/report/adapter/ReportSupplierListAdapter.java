package com.honeywell.wholesale.ui.report.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.honeywell.wholesale.R;
import com.honeywell.wholesale.ui.base.BaseTextView;
import com.honeywell.wholesale.ui.report.supplier.SupplierChartEntity;

import java.util.ArrayList;

/**
 * Created by xiaofei on 9/26/16.
 *
 */
public class ReportSupplierListAdapter extends BaseAdapter{
    private Context context;
    private ArrayList<SupplierChartEntity> arrayList;

    public ReportSupplierListAdapter(Context context, ArrayList<SupplierChartEntity> arrayList) {
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
            convertView = LayoutInflater.from(context).inflate(R.layout.item_supplier_report_list, parent, false);
        }

        ImageView supplierImageView = (ImageView)convertView.findViewById(R.id.report_supplier_imageView);
        BaseTextView supplierNameTextView = (BaseTextView)convertView.findViewById(R.id.report_supplier_name);
        BaseTextView supplierStockTextView = (BaseTextView)convertView.findViewById(R.id.report_stock_amount);
        BaseTextView supplierStockRadioTextView = (BaseTextView)convertView.findViewById(R.id.report_stock_radio);

        BaseTextView supplierPayableTextView = (BaseTextView)convertView.findViewById(R.id.report_supplier_payable);
        BaseTextView supplierPayableRadioTextView = (BaseTextView)convertView.findViewById(R.id.report_supplier_payable_radio);

        supplierImageView.setImageResource(R.drawable.icon_search_vendor);

        SupplierChartEntity item = arrayList.get(position);
        String supplierInfo = item.getSupplierName() + "|" + item.getContactName();
        supplierNameTextView.setText(supplierInfo);

        supplierStockTextView.setText(String.format(context.getResources().getString(R.string.report_supplier_stock),
                item.getBuyAmount()));

        String radio = String.valueOf(Float.valueOf(item.getBuyRatio()) * 100) + "%";
        supplierStockRadioTextView.setText(radio);

        supplierPayableTextView.setText("");
        supplierPayableRadioTextView.setText("");

        return convertView;
    }

    public void setDataSource(ArrayList<SupplierChartEntity> arrayList){
        this.arrayList = arrayList;
    }
}
