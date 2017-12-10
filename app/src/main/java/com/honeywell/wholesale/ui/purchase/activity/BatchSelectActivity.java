package com.honeywell.wholesale.ui.purchase.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.honeywell.wholesale.R;
import com.honeywell.wholesale.framework.model.AccountManager;
import com.honeywell.wholesale.framework.model.BatchModel;
import com.honeywell.wholesale.framework.model.ExtraCost;
import com.honeywell.wholesale.framework.model.PreCart;
import com.honeywell.wholesale.ui.base.BaseActivity;
import com.honeywell.wholesale.ui.base.BaseTextView;
import com.honeywell.wholesale.ui.purchase.adapter.BatchSelectAdapter;
import com.honeywell.wholesale.ui.purchase.presenter.BatchSelectPresenter;
import com.honeywell.wholesale.ui.purchase.view.BatchSelectView;
import com.honeywell.wholesale.ui.transaction.preorders.CartModel;

import java.util.ArrayList;

/**
 * Created by liuyang on 2017/7/5.
 */

public class BatchSelectActivity extends BaseActivity implements BatchSelectView{

    private static final String TAG = BatchSelectActivity.class.getSimpleName();
    private static final String INTENT_VALUE_EXTRA_COST_ID = "INTENT_VALUE_EXTRA_COST_ID";
    public static final String SKU_ID = "SKU_ID";
    public static final String PRODUCT_ID = "PRODUCT_ID";
    private ImageView backImageView;
    private ImageView addImageView;

//    private ListView batchListView;

    private boolean isHistoryVisible = false;

    private LinearLayout productContainerSection;

    private Context context;

//    private BatchSelectAdapter listAdapter;

    private ArrayList<BatchModel> batchList;

//    private String currentDay = "20170712";
    private String currentDay = "";

    private BatchSelectPresenter batchSelectPresenter;

    private CartModel cartModel = CartModel.getInstance();

    private int productId;

    private int currentSkuId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_batch);
        batchSelectPresenter = new BatchSelectPresenter(this);
        initView();
        initData();
//        initShow();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onShowLoading() {

    }

    @Override
    public void onHideLoading() {

    }

    @Override
    public void onToast(String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onIntent() {

    }

    @Override
    public Context getContext() {
        return null;
    }

    private void initView() {
        productContainerSection = (LinearLayout) findViewById(R.id.product_containner);
        backImageView = (ImageView) findViewById(R.id.icon_back);
        addImageView = (ImageView) findViewById(R.id.add_image_view);
        addImageView.setOnClickListener(onClickListener);
        backImageView.setOnClickListener(onClickListener);
    }

    @Override
    public void setListData(String currentDay, ArrayList<BatchModel> arrayList) {
        this.currentDay = currentDay;
        batchList =  arrayList;
        initShow();
    }

    private void initData() {
        context = this;
        batchList = new ArrayList<BatchModel>();
        setHistoryVisible(false);
        Intent intent = getIntent();
        if (intent != null) {
            currentSkuId = intent.getIntExtra(SKU_ID, -1);
            productId = intent.getIntExtra(PRODUCT_ID, -1);
        }

        batchSelectPresenter.getBatchDataFromServer();
    }


    private void initShow(){
        Log.e(TAG,String.valueOf(batchList.size()));
        productContainerSection.removeAllViews();
        if (batchList != null){
            if (batchList.size() != 0) {
                if (!currentDay.equals(batchList.get(0).getBatchDay())) {
                    addNullView();
                    addHistoryBatchView();
                    if (isHistoryVisible()) {
                        for (int i = 0; i < batchList.size(); i++) {
                            addBatchTitleView(batchList.get(i));
                            for (int j = 0; j < batchList.get(i).getBatchDayList().size(); j++) {
                                addBatchView(batchList.get(i), batchList.get(i).getBatchDayList().get(j));
                            }
                        }
                    }
                } else {
                    for (int i = 0; i < batchList.size(); i++) {
                        addBatchTitleView(batchList.get(i));
                        for (int j = 0; j < batchList.get(i).getBatchDayList().size(); j++) {
                            addBatchView(batchList.get(i), batchList.get(i).getBatchDayList().get(j));
                        }
                        if (i == 0) {
                            addHistoryBatchView();
                        }
                        if (!isHistoryVisible()) {
                            break;
                        }
                    }
                }
            }
        }
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.icon_back) {
                finish();
            }else if (v.getId() == R.id.add_image_view) {
                showNormalDialog();
            }
        }
    };


    private void addBatchTitleView(final BatchModel batchModel) {
        final View child = getLayoutInflater().inflate(R.layout.item_batch_title, null);
        setViewTag(child, batchModel);
        productContainerSection.addView(child);
        final BaseTextView mBatchTitleTextView = (BaseTextView) child.findViewById(R.id.batch_title_text_view);
        mBatchTitleTextView.setText(batchModel.getBatchDay());
    }

    private void addBatchView(final BatchModel batchModel, final BatchModel.BatchDay batchDay) {
        final View child = getLayoutInflater().inflate(R.layout.item_batch_content, null);
        setViewTag(child, batchModel, batchDay);
        productContainerSection.addView(child);
        child.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cartModel.modifyBatchById(productId, currentSkuId, batchDay.getBatchId(), batchDay.getBatchName());
                finish();
            }
        });
        final BaseTextView mBatchContentTextView = (BaseTextView) child.findViewById(R.id.batch_content_text_view);
        mBatchContentTextView.setText(batchDay.getBatchName());
    }


    private void addHistoryBatchView() {
        final View child = getLayoutInflater().inflate(R.layout.item_history_batch, null);
//        setViewTag(child, batchModel, batchDay);
        productContainerSection.addView(child);
        final BaseTextView mBatchHistoryTextView = (BaseTextView) child.findViewById(R.id.batch_history_text_view);
        final ImageView mHistoryBatchImageView = (ImageView) child.findViewById(R.id.batch_history_image_view);
        if (isHistoryVisible()){
            mHistoryBatchImageView.setImageResource(R.drawable.arrow_up);
        }else {
            mHistoryBatchImageView.setImageResource(R.drawable.arrow_down);
        }
        child.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setHistoryVisible(!isHistoryVisible());
                initShow();
            }
        });

        mBatchHistoryTextView.setText("历史批次");
    }


    private void addNullView() {
        final View child = getLayoutInflater().inflate(R.layout.item_null_batch, null);
        productContainerSection.addView(child);
        final BaseTextView mNullBatchTitleTextView = (BaseTextView) child.findViewById(R.id.null_batch_title_text_view);
        final BaseTextView mNullBatchContentTextView = (BaseTextView) child.findViewById(R.id.null_batch_content_text_view);
        mNullBatchTitleTextView.setText(currentDay);
        mNullBatchContentTextView.setText("今日暂无批次");
    }

    private View getViewByTag(int tagKey) {
        for (int i = 0; i < productContainerSection.getChildCount(); i++) {
            if (tagKey == Integer.valueOf(productContainerSection.getChildAt(i).getTag(R.id.precart_view_tag).toString())) {
                return productContainerSection.getChildAt(i);
            }
        }
        return null;
    }

    private void setViewTag(View view, BatchModel batchModel) {
        view.setTag(R.id.batch_title_view_tag, batchModel.getBatchDay());
    }

    private void setViewTag(View view, BatchModel batchModel, BatchModel.BatchDay batchDay) {

        view.setTag(R.id.batch_title_view_tag, batchModel.getBatchDay());
        if (batchDay == null) {
            view.setTag(R.id.batch_content_view_tag, batchModel.getBatchDay());
        } else {
            view.setTag(R.id.batch_content_view_tag, batchDay.getBatchId());
        }
    }

    public boolean isHistoryVisible() {
        return isHistoryVisible;
    }

    public void setHistoryVisible(boolean historyVisible) {
        isHistoryVisible = historyVisible;
    }

    /**
     * 弹出是否退出当前页面的对话框
     */
    private void showNormalDialog() {
        /* @setIcon 设置对话框图标
         * @setTitle 设置对话框标题
         * @setMessage 设置对话框消息提示
         * setXXX方法返回Dialog对象，因此可以链式设置属性
         */
        final AlertDialog.Builder normalDialog =
                new AlertDialog.Builder(BatchSelectActivity.this);

        final EditText editText = new EditText(this);
        editText.setFilters(new InputFilter[]{ new  InputFilter.LengthFilter(50)});

        normalDialog.setMessage("请输入批次号");
        normalDialog.setView(editText);
        normalDialog.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (editText.getText().toString().equals("")){
                            onToast("请输入批次号");
                        }else {
                            batchSelectPresenter.addBatchDataFromServer(editText.getText().toString());
                        }
                    }
                });
        normalDialog.setNegativeButton("取消",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        // 显示
        normalDialog.show();
    }
}
