package com.honeywell.wholesale.ui.dashboard.adapter;

import android.content.Context;
import android.nfc.Tag;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.honeywell.hybridapp.framework.log.LogHelper;
import com.honeywell.wholesale.R;
import com.honeywell.wholesale.framework.model.Order;
import com.honeywell.wholesale.framework.utils.WSTimeStamp;
import com.honeywell.wholesale.ui.base.BaseTextView;

import java.math.BigDecimal;
import java.util.ArrayList;

/**
 * Created by xiaofei on 10/13/16.
 *
 *  已完成订单(order_status==1)
 *  已取消订单(order_status==100)
 *  未支付(payment=0 and order_status=0)
 *  赊账中(payment=2 and order_status=0)
 *
 */

public class TransactionOrderListAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<Order> arrayList;

    public TransactionOrderListAdapter(ArrayList<Order> arrayList, Context context) {
        this.context = context;
        this.arrayList = arrayList;
    }

    public void setListData(ArrayList<Order> arrayList) {
        this.arrayList = arrayList;
    }

    public void clearListData(){
        this.arrayList.clear();
        notifyDataSetChanged();
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

        OrderViewHolder holder = new OrderViewHolder();
        if (convertView == null){
            convertView = LayoutInflater.from(context).inflate(R.layout.fragment_transaction_order_listview_item, parent, false);
            holder.customerName = (BaseTextView) convertView.findViewById(R.id.order_listview_item_customer_name_textView);
            holder.totalMoney = (BaseTextView) convertView.findViewById(R.id.order_listview_item_total_money_textView);
            holder.products = (BaseTextView) convertView.findViewById(R.id.order_listview_item_products_textView);
            holder.orderTime = (BaseTextView) convertView.findViewById(R.id.order_listview_item_order_time_textView);
            holder.employeeName = (BaseTextView) convertView.findViewById(R.id.order_listview_item_employee_name_textView);
            holder.saleAmount = (BaseTextView) convertView.findViewById(R.id.order_listview_item_sale_amount_textView);
            holder.orderStatus = (BaseTextView) convertView.findViewById(R.id.order_listview_item_status_tag_textView);
            holder.orderDeadLine = (BaseTextView) convertView.findViewById(R.id.order_listview_item_deadline_textView);
            holder.signText = (BaseTextView) convertView.findViewById(R.id.order_listview_item_sign_textView);
            holder.orderNumber = (BaseTextView) convertView.findViewById(R.id.order_listview_item_order_number_textView);
            convertView.setTag(holder);
        }else{
            holder = (OrderViewHolder) convertView.getTag();
        }

        holder.saleAmount.setVisibility(View.GONE);
        holder.totalMoney.setVisibility(View.VISIBLE);
        holder.employeeName.setVisibility(View.VISIBLE);
        holder.orderTime.setVisibility(View.VISIBLE);
        holder.orderDeadLine.setVisibility(View.VISIBLE);
        holder.orderStatus.setVisibility(View.VISIBLE);
        holder.signText.setVisibility(View.VISIBLE);
        holder.orderNumber.setVisibility(View.VISIBLE);

        Order order = arrayList.get(position);

        holder.customerName.setText(order.getCustomerName());
        holder.totalMoney.setText(order.getTotalPrice());
        holder.orderNumber.setText(order.getOrderNumber());

        String productsName = "";
        Order.Product[] products = order.getSaleList();
        for(int i = 0; i < products.length; i++) {
            productsName += products[i].mName;
            if (i != products.length - 1) {
                productsName += ",";
            }
        }
        holder.products.setText(productsName);

        if (order.getOrderStatus() == null){
            return convertView;
        }

        //已完成订单(order_status==1)
        if (order.getOrderStatus().equals("1")){
            holder.orderStatus.setText("已结清");
            holder.orderStatus.setTextColor(context.getResources().getColor(R.color.transaction_paid_text_color));
            holder.orderDeadLine.setVisibility(View.GONE);
//            String mFinishTime = WSTimeStamp.getFullTimeString(Long.parseLong(order.getmFinishDt()));
//            holder.orderTime.setText(mFinishTime);
            holder.orderTime.setText(order.getFinishDtFormat());
            holder.employeeName.setText(order.getmFinishEmployeeName()+"结清账单处理");
        }

        //已取消订单(order_status==100)
        if (order.getOrderStatus().equals("100")){
            holder.orderStatus.setText("已取消");
            holder.orderStatus.setTextColor(context.getResources().getColor(R.color.transaction_cancel_text_color));
            holder.orderDeadLine.setVisibility(View.GONE);
            holder.orderTime.setText(order.getCancelDtFormat());
            holder.employeeName.setText(order.getmCancelEmployeeName()+"取消订单处理");
        }

        //未支付(payment=0 and order_status=0)
        if (order.getOrderStatus().equals("0") && order.getPayment().equals("0")){
            holder.orderStatus.setText("待付款");
            holder.employeeName.setText(order.getEmployeeName()+"开单处理");
            holder.orderStatus.setTextColor(context.getResources().getColor(R.color.light_blue));
            holder.orderDeadLine.setVisibility(View.GONE);
            holder.orderTime.setText(order.getSaleTime());

            long millsSeconds = 0l;
            try{
                //将科学计数法的字符串转换成long型
                millsSeconds = new BigDecimal(order.getmOrderDeadLine()).longValue();

            }catch (Exception e){
                e.printStackTrace();
                LogHelper.e("Error", "时间切换 string 转换为long 异常");
            }

            String simpleDateFormat = "dd/MM/yyyy hh:mm";
            String deadLineString = WSTimeStamp.getDate(millsSeconds, simpleDateFormat);
            holder.orderDeadLine.setText(deadLineString);
        }

        //赊账中(payment=2 and order_status=0)
        if (order.getOrderStatus().equals("0") && order.getPayment().equals("2")){
            holder.orderStatus.setText("赊账中");
            holder.orderStatus.setTextColor(context.getResources().getColor(R.color.red));
            holder.orderDeadLine.setVisibility(View.GONE);
            holder.orderTime.setText(order.getSetPayDtFormat());
            holder.employeeName.setText(order.getmSetPayEmpolyeeName()+"赊账处理");
        }

        return convertView;
    }

    public static class OrderViewHolder {
        BaseTextView customerName;
        BaseTextView totalMoney;
        BaseTextView products;
        BaseTextView orderTime;
        BaseTextView employeeName;
        BaseTextView signText;
        BaseTextView orderNumber;

        BaseTextView saleAmount;
        // 订单状态, 未支付截止日期
        BaseTextView orderStatus;
        BaseTextView orderDeadLine;
    }

}