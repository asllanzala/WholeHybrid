package com.honeywell.wholesale.ui.transaction.preorders.Adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.InputFilter;
import android.text.method.DigitsKeyListener;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.honeywell.wholesale.R;
import com.honeywell.wholesale.framework.model.Inventory;
import com.honeywell.wholesale.framework.model.PreCart;
import com.honeywell.wholesale.ui.base.BaseTextView;
import com.honeywell.wholesale.ui.transaction.preorders.CartModel;
import com.honeywell.wholesale.ui.transaction.preorders.ObserverCartData;
import com.honeywell.wholesale.ui.transaction.preorders.PreTransactionCartActivity;
import com.honeywell.wholesale.ui.transaction.preorders.search.PreTransactionSearchActivity;
import com.honeywell.wholesale.ui.transaction.preorders.search.PreTransactionSkuActivity;

import java.util.ArrayList;

/**
 * Created by H154326 on 17/3/13.
 * Email: yang.liu6@honeywell.com
 */

public class PreSearchListAdapter extends BaseAdapter {
    private ArrayList arrayList;
    private Context context;

    private int wareHouseId;

    private ObserverCartData observerCartData;
    private CartModel cartModel = CartModel.getInstance();

    public PreSearchListAdapter(Context context) {
        this.context = context;
    }

    public PreSearchListAdapter(ArrayList arrayList, Context context) {
        this.arrayList = arrayList;
        this.context = context;
    }


    @Override
    public int getCount() {
        if (arrayList != null) {
            return arrayList.size();
        }else {
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
            convertView = LayoutInflater.from(context).inflate(R.layout.item_pre_search_result_list, parent, false);
            holder.iconImage = (ImageView) convertView.findViewById(R.id.pre_search_result_icon);
            holder.titleTextView = (BaseTextView) convertView.findViewById(R.id.pre_search_title_textView);
            holder.priceInfoTextView = (BaseTextView) convertView.findViewById(R.id.pre_search_price_info_textView);
            holder.stockTextView = (BaseTextView) convertView.findViewById(R.id.pre_search_stock_info_textView);
            holder.countTextView = (BaseTextView) convertView.findViewById(R.id.pre_search_product_no_text_view);

            holder.minus = convertView.findViewById(R.id.pre_search_product_no_minus);
            holder.add = convertView.findViewById(R.id.pre_search_product_no_add);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Object obj = arrayList.get(position);

        if (obj instanceof Inventory) {
            final ViewHolder holder1 = holder;
            final Inventory inventory = (Inventory) obj;
            if (cartModel.checkProductExist(inventory.getProductId())){
                holder.countTextView.setText(cartModel.getById(inventory.getProductId()).getTotalNumber());
                Log.e("sada", "11" + cartModel.getById(inventory.getProductId()).getTotalNumber());
                holder.minus.setVisibility(View.VISIBLE);
            }else {
                holder.countTextView.setText("");
                holder.minus.setVisibility(View.GONE);
            }
            holder.titleTextView.setText(inventory.getProductName());
            holder.countTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, PreTransactionSkuActivity.class);
                    intent.putExtra("wareHouseId", wareHouseId);
                    intent.putExtra("productId", inventory.getProductId());
                    intent.putExtra("productName", inventory.getProductName());
                    intent.putExtra("productPic", inventory.getPicSrc());
                    intent.putExtra("productStock", inventory.getQuantity());
                    intent.putExtra("productPrice", inventory.getStandardPrice());
                    intent.putExtra("productUnit", inventory.getUnit());
                    context.startActivity(intent);
//                    showInputDialog(inventory.getProductName(), holder1.countTextView, inventory);
                }
            });
            final String info;
            info = "¥  " + inventory.getStandardPrice();
            String url = inventory.getPicSrc();
            if (url == null) {
                url = "";
            }
            if (url.equals("")) {
                url = String.valueOf(R.drawable.inventory_default_icon);
            }
            holder.priceInfoTextView.setText(info);

            if (inventory.getUnit() == null) {
                inventory.setUnit("");
            }
            String stock = context.getString(R.string.search_product_stock) + "  " + inventory.getQuantity() + "  " + inventory.getUnit();
            holder.stockTextView.setVisibility(View.VISIBLE);
            holder.stockTextView.setText(stock);
            Glide
                    .with(context)
                    .load(url)
                    .placeholder(R.drawable.inventory_default_icon)
                    .error(R.drawable.inventory_default_icon)
                    .priority(Priority.LOW)
                    .into(holder.iconImage);
        if (holder.countTextView.getText().toString().equals("")) {
            holder.minus.setVisibility(View.GONE);
        } else {
            holder.minus.setVisibility(View.VISIBLE);
        }

        holder.minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, PreTransactionSkuActivity.class);
                intent.putExtra("wareHouseId", wareHouseId);
                intent.putExtra("productId", inventory.getProductId());
                intent.putExtra("productName", inventory.getProductName());
                intent.putExtra("productPic", inventory.getPicSrc());
                intent.putExtra("productStock", inventory.getQuantity());
                intent.putExtra("productPrice", inventory.getStandardPrice());
                intent.putExtra("productUnit", inventory.getUnit());
                context.startActivity(intent);
            }
        });

        holder.add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, PreTransactionSkuActivity.class);
                intent.putExtra("wareHouseId", wareHouseId);
                intent.putExtra("productId", inventory.getProductId());
                intent.putExtra("productName", inventory.getProductName());
                intent.putExtra("productPic", inventory.getPicSrc());
                intent.putExtra("productStock", inventory.getQuantity());
                intent.putExtra("productPrice", inventory.getStandardPrice());
                intent.putExtra("productUnit", inventory.getUnit());
                context.startActivity(intent);
            }
        });
        }
        return convertView;
    }

    private static class ViewHolder {
        ImageView iconImage;
        BaseTextView titleTextView;
        BaseTextView priceInfoTextView;
        BaseTextView stockTextView;
        BaseTextView countTextView;
        View minus;
        View add;
    }

    public void setDataSource(ArrayList arrayList, int wareHouseId) {
        this.arrayList = arrayList;
        this.wareHouseId = wareHouseId;
    }

    private void showInputDialog(String productName, final BaseTextView baseTextView,final Inventory inventory) {
    /*@setView 装入一个EditView
     */
//        final EditText editText = new EditText(context);

        TextView title = new TextView(context);
        title.setText(productName);
        title.setPadding(10, 10, 10, 10);
        title.setGravity(Gravity.CENTER);
        title.setTextColor(Color.parseColor("#000000"));
        title.setBackgroundColor(Color.parseColor("#FFFFFF"));
        title.setTextSize(23);

        AlertDialog.Builder inputDialog =
                new AlertDialog.Builder(context, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
        final View dialogView = LayoutInflater.from(context)
                .inflate(R.layout.dialog_product_amount,null);
        inputDialog.setCustomTitle(title).setView(dialogView);
        final EditText editText = (EditText) dialogView.findViewById(R.id.edit_text);
        final Button confirmBtn = (Button)dialogView.findViewById(R.id.dialog_confirm);

        try {
            editText.setText(String.valueOf(Integer.valueOf(baseTextView.getText().toString())));
        }catch (NumberFormatException e){
            e.printStackTrace();
        }
        editText.setInputType(EditorInfo.TYPE_CLASS_PHONE);
        editText.setKeyListener(DigitsKeyListener.getInstance("0123456789"));
        editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(8)});
        editText.setFocusableInTouchMode(true);
        editText.setSelectAllOnFocus(true);
        editText.setFocusable(true);
        editText.requestFocus();

        final AlertDialog dialog = inputDialog.show();

        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int count = 0;
                if (!editText.getText().toString().equals("")) {
                    count = Integer.valueOf(editText.getText().toString());
                } else {
                    count = 0;
                }

                if (count >= Integer.valueOf(inventory.getQuantity()) + 1) {
                    count = Integer.valueOf(inventory.getQuantity());
                    Toast.makeText(context, R.string.pre_order_sale_number, Toast.LENGTH_SHORT).show();
                }

                if (cartModel.checkProductExist(inventory.getProductId())) {
                    if (count > 0 ) {
                        cartModel.modifyProductData(inventory.getProductId(), String.valueOf(count));
                    } else {
                        cartModel.remove(inventory.getProductId());
                    }
                }else {
                    if (count > 0) {
                        cartModel.add(new PreCart(inventory.getProductId(),inventory.getProductCode(),inventory.getProductName(),
                                inventory.getStandardPrice(),inventory.getPicSrc(),inventory.getQuantity(),String.valueOf(count),
                                inventory.getUnit()));
                    }
                }
                if (observerCartData != null){
                    observerCartData.dataChanged();
                }
                notifyDataSetChanged();
                dialog.dismiss();
            }
        });

//        InputMethodManager imm = (InputMethodManager) editText.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
//        imm.showSoftInput(editText, InputMethodManager.SHOW_FORCED);

//        AlertDialog dialog = inputDialog.setPositiveButton("提交",
//                new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//
//                        int count = 0;
//                        if (!editText.getText().toString().equals("")) {
//                            count = Integer.valueOf(editText.getText().toString());
//                        } else {
//                            count = 0;
//                        }
//
//                        if (count >= Integer.valueOf(inventory.getQuantity()) + 1) {
//                            count = Integer.valueOf(inventory.getQuantity());
//                            Toast.makeText(context, R.string.pre_order_sale_number, Toast.LENGTH_SHORT).show();
//                        }
//
//                        if (cartModel.checkProductExist(inventory.getProductId())) {
//                            if (count > 0 ) {
//                                cartModel.modifyProductData(inventory.getProductId(), String.valueOf(count));
//                            } else {
//                                cartModel.remove(inventory.getProductId());
//                            }
//                        }else {
//                            if (count > 0) {
//                                cartModel.add(new PreCart(inventory.getProductId(),inventory.getProductCode(),inventory.getProductName(),
//                                        inventory.getStandardPrice(),inventory.getPicSrc(),inventory.getQuantity(),String.valueOf(count),
//                                        inventory.getUnit()));
//                            }
//                        }
//                        if (observerCartData != null){
//                            observerCartData.dataChanged();
//                        }
//                        notifyDataSetChanged();
//                    }
//                }).show();


//        Button btnPositive = dialog.getButton(android.app.AlertDialog.BUTTON_POSITIVE);
//        btnPositive.setTextColor(Color.parseColor("#000000"));
//        btnPositive.setBackgroundColor(Color.parseColor("#FFFFFF"));
//        btnPositive.setTextSize(18);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);

        WindowManager m = ((Activity)context).getWindowManager();
        Display d = m.getDefaultDisplay();
        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        params.width = (int) ((d.getWidth()) * 0.9);
        params.height = (int) ((d.getHeight()) * 0.9);
        dialog.getWindow().setAttributes(params);
    }

    public void setObserverCartData(ObserverCartData observerCartData) {
        this.observerCartData = observerCartData;
    }
}
