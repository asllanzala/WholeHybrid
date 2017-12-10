package com.honeywell.wholesale.framework.utils;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.honeywell.wholesale.R;
import com.honeywell.wholesale.framework.model.Image;
import com.honeywell.wholesale.framework.model.WareHouse;
import com.honeywell.wholesale.framework.scanner.ScanerRespManager;
import com.honeywell.wholesale.ui.base.BaseTextView;
import com.honeywell.wholesale.ui.transaction.cart.adapter.CartMoreAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by H154326 on 17/5/2.
 * Email: yang.liu6@honeywell.com
 */

public class PopupWindowUtil {
    private ListView listView;
    private PopupWindow window;
    private MyAdapter myAdapter;
    //窗口在x轴偏移量
    private int xOff = 0;
    //窗口在y轴的偏移量
    private int yOff = 0;

    public PopupWindowUtil(Context context, ArrayList<WareHouse> datas) {

//        window = new PopupWindow(context);
//        //ViewGroup.LayoutParams.WRAP_CONTENT，自动包裹所有的内容
//        window.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
//        window.setFocusable(true);
//        //点击 back 键的时候，窗口会自动消失
//        window.setBackgroundDrawable(new BitmapDrawable());
//
//        View localView = LayoutInflater.from(context).inflate(R.layout.menu_popup_window, null);
//        listView = (ListView) localView.findViewById(R.id.lv_pop_list);
//
//        myAdapter = new MyAdapter(context, datas);
//        listView.setAdapter(myAdapter);
//        myAdapter.notifyDataSetChanged();
//        listView.setTag(window);
//        //设置显示的视图
//        window.setContentView(localView);

        final View localView = LayoutInflater.from(context).inflate(R.layout.menu_popup_window, null, false);
        listView = (ListView) localView.findViewById(R.id.lv_pop_list);

        myAdapter = new MyAdapter(context, datas);
        listView.setAdapter(myAdapter);
        myAdapter.notifyDataSetChanged();

        window = new PopupWindow(context);
        //ViewGroup.LayoutParams.WRAP_CONTENT，自动包裹所有的内容
        window.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        window.setFocusable(true);
        //点击 back 键的时候，窗口会自动消失
        window.setBackgroundDrawable(new ColorDrawable(context.getResources().getColor(R.color.transaction_shop_headview_divider_line_bg_color)));
        localView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (localView != null && localView.isShown()) {
                    window.dismiss();
                    window = null;
                }
                return false;
            }
        });
        //listView.setTag(window);
        //设置显示的视图
        window.setContentView(localView);
    }

    public void setItemClickListener(AdapterView.OnItemClickListener listener) {
        listView.setOnItemClickListener(listener);
    }

    public void dismiss() {
        window.dismiss();
    }

    /**
     * @param xOff x轴（左右）偏移
     * @param yOff y轴（上下）偏移
     */
    public void setOff(int xOff, int yOff) {
        this.xOff = xOff;
        this.yOff = yOff;
    }

    /**
     * @param paramView 点击的按钮
     */
    public void show(View paramView, int count) {
        //该count 是手动调整窗口的宽度
        window.setWidth(paramView.getWidth() * count);
        //设置窗口显示位置, 后面两个0 是表示偏移量，可以自由设置
        window.showAsDropDown(paramView, xOff, yOff);
        //更新窗口状态
        window.update();
    }

    public void setListViewLocation(int position){
        listView.setSelection(position);
    }

    public void notifyData(){
        myAdapter.notifyDataSetChanged();
    }

    public void setData(ArrayList<WareHouse> wareHouseArrayList){
        myAdapter.setmDatas(wareHouseArrayList);
        listView.setAdapter(myAdapter);
    }

    public void clearAllDeafault(){
        for (WareHouse wareHouse : myAdapter.getmDatas()){
            wareHouse.setDefault(false);
        }
    }

    public void clearAllSelected(){
        for (WareHouse wareHouse : myAdapter.getmDatas()){
            wareHouse.setSelected(false);
        }
    }

    class MyAdapter extends BaseAdapter {

        private Context context;
        private ArrayList<WareHouse> mDatas;

        public MyAdapter(Context context, ArrayList<WareHouse> datas) {
            this.context = context;
            if (datas == null) {
                datas = new ArrayList<>();
            }
            mDatas = datas;
        }

        public ArrayList<WareHouse> getmDatas() {
            return mDatas;
        }

        public void setmDatas(ArrayList<WareHouse> arrayList){
            mDatas = arrayList;
        }

        @Override
        public int getCount() {
            return mDatas.size();
        }

        @Override
        public WareHouse getItem(int position) {
            return mDatas.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.item_menu_popup_window, null);
                holder = new ViewHolder();
                holder.baseTextView = (BaseTextView) convertView.findViewById(R.id.tv_item_pw_menu);
                holder.imageView = (ImageView) convertView.findViewById(R.id.warehouse_select_image_view);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            if (getItem(position).isSelected()){
                holder.baseTextView.setTextColor(context.getResources().getColor(R.color.warehouse_selected_color));
                holder.imageView.setVisibility(View.VISIBLE);
                listView.setSelection(position);
            }else {
                holder.baseTextView.setTextColor(context.getResources().getColor(R.color.warehouse_enabled_text_color));
                holder.imageView.setVisibility(View.GONE);
            }
            holder.baseTextView.setText(getItem(position).getWareHouseName() + "");
            return convertView;
        }
        private class ViewHolder {
            BaseTextView baseTextView;
            ImageView imageView;
        }
    }
}
