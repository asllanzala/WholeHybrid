package com.honeywell.wholesale.ui.category;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.honeywell.wholesale.R;
import com.honeywell.wholesale.framework.http.NativeJsonResponseListener;
import com.honeywell.wholesale.framework.http.WebClient;
import com.honeywell.wholesale.ui.base.BaseTextView;
import com.honeywell.wholesale.ui.category.model.CategoryAddRequest;
import com.honeywell.wholesale.ui.category.model.CategoryDeleteRequest;
import com.honeywell.wholesale.ui.category.model.CategoryUpdateRequest;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import static android.R.attr.category;

/**
 * Created by xiaofei on 12/26/16.
 */

public class ModifyCategoryActivity extends Activity {
    private final String TAG = ModifyCategoryActivity.class.getSimpleName();

    private EditText categoryEditText;
    private Button confirmButton;
    private ImageView backImageView;
    private BaseTextView titleTextView;
    private BaseTextView categoryRemoveView;
    private WebClient webClient;

    private static boolean IS_MODIFY = true;

    private static final String CATEGORY = "CATEGORY";
    private static final String MODIFY_CATEGORY = "MODIFY";
    private static final String CREATE_CATEGORY = "CREATE";

    private static final String CATEGORY_ID = "CATEGORY_ID";
    private static final String CATEGORY_NAME = "CATEGORY_NAME";

    private Intent intent;

    private int inCount;

    private GradientDrawable drawable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_category);

        webClient = new WebClient();
        drawable = new GradientDrawable();

        categoryEditText = (EditText) findViewById(R.id.category_editText);
        categoryEditText.addTextChangedListener(new TextWatcher() {
            private CharSequence temp;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String lawInput = categoryEditText.getText().toString();
                String str = stringFilter(lawInput.toString());
                if ("".equals(lawInput.trim())) {
                    categoryEditText.setBackground(getResources().getDrawable(R.drawable.category_edit_text));
                    setAddOrderButtonUnEnable();
                } else if (!lawInput.equals(str)) {
                    categoryEditText.setBackground(getResources().getDrawable(R.drawable.category_error_edit_text));
                    setAddOrderButtonUnEnable();
                } else {
                    categoryEditText.setBackground(getResources().getDrawable(R.drawable.category_edit_text));
                    setAddOrderButtonEnable();
                }
            }
        });
        confirmButton = (Button) findViewById(R.id.category_confirm);
        backImageView = (ImageView) findViewById(R.id.icon_back);
        titleTextView = (BaseTextView) findViewById(R.id.category_list_header_title);
        categoryRemoveView = (BaseTextView) findViewById(R.id.category_list_remove);

        backImageView.setOnClickListener(onClickListener);
        intent = getIntent();
        String tag = intent.getStringExtra(CATEGORY);

        confirmButton.setOnClickListener(onClickListener);

        if (tag.equals(MODIFY_CATEGORY)) {
            categoryRemoveView.setVisibility(View.VISIBLE);
            categoryRemoveView.setOnClickListener(onClickListener);
            IS_MODIFY = true;
            String categoryName = intent.getStringExtra(CATEGORY_NAME);
            categoryEditText.setText(categoryName);
            categoryEditText.setSelection(categoryName.length());
        }

        if (tag.equals(CREATE_CATEGORY)) {
            categoryRemoveView.setVisibility(View.GONE);
            titleTextView.setText(getResources().getString(R.string.category_add_title));
            IS_MODIFY = false;
        }
    }

    public static String stringFilter(String str) throws PatternSyntaxException {
        // 只允许字母、数字和汉字
        String regEx = "[^a-zA-Z0-9\u4E00-\u9FA5]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        return m.replaceAll("").trim();
    }

    public void setAddOrderButtonEnable() {
        drawable.setShape(GradientDrawable.RECTANGLE); // 画框
        drawable.setStroke(1, Color.BLACK); // 边框粗细及颜色
        drawable.setColor(getResources().getColor(R.color.white)); // 边框内部颜色
        confirmButton.setEnabled(true);
        confirmButton.setBackground(drawable);
        confirmButton.setTextColor(getResources().getColor(R.color.cart_management_add_order_button_selected_text_color));
    }

    public void setAddOrderButtonUnEnable() {
        drawable.setShape(GradientDrawable.RECTANGLE); // 画框
        drawable.setStroke(1, getResources().getColor(R.color.cart_management_add_order_button_normal_bg_color)); // 边框粗细及颜色
        drawable.setColor(getResources().getColor(R.color.cart_management_add_order_button_normal_bg_color)); // 边框内部颜色
        confirmButton.setEnabled(false);
        confirmButton.setBackground(drawable);
        confirmButton.setTextColor(getResources().getColor(R.color.cart_management_add_order_button_normal_text_color));
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.icon_back) {
                finish();
            }

            if (v.getId() == R.id.category_list_remove) {
                int categoryId = intent.getIntExtra(CATEGORY_ID, 0);
                CategoryDeleteRequest request = new CategoryDeleteRequest(categoryId);
                webClient.httpCategoryDelete(request, new NativeJsonResponseListener() {
                    @Override
                    public void listener(Object o) {
                        Log.e(TAG, "category remove");
                        finish();
                    }

                    @Override
                    public void errorListener(String s) {
                        Toast.makeText(getApplicationContext(), "不支持删除已有货品的类别", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "category remove error");
                    }
                });
            }

            if (v.getId() == R.id.category_confirm) {
                String lawInput = categoryEditText.getText().toString();
                String str = stringFilter(lawInput.toString());
                if (!lawInput.equals(str)) {
                } else {
                    if ("".equals(lawInput.trim())) {
                    } else {
                        if (IS_MODIFY) {
                            int categoryId = intent.getIntExtra(CATEGORY_ID, 0);
                            String categoryName = categoryEditText.getEditableText().toString();
                            CategoryUpdateRequest request = new CategoryUpdateRequest(categoryId, categoryName);
                            webClient.httpCategoryUpdate(request, new NativeJsonResponseListener() {
                                @Override
                                public void listener(Object o) {
                                    Log.e(TAG, "modify category");
                                    finish();
                                }

                                @Override
                                public void errorListener(String s) {
                                    Log.e(TAG, "modify category error");
                                    Toast.makeText(getApplicationContext(), "该类别名已被占用，无法修改", Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {
                            String categoryName = categoryEditText.getEditableText().toString();
                            CategoryAddRequest request = new CategoryAddRequest(categoryName);

                            webClient.httpCategoryAdd(request, new NativeJsonResponseListener() {
                                @Override
                                public void listener(Object o) {
                                    Log.e(TAG, "create category");
                                    finish();
                                }

                                @Override
                                public void errorListener(String s) {
                                    Log.e(TAG, "create category error");
                                    Toast.makeText(getApplicationContext(), "该类别已存在，请不要重复添加", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                }
            }
        }
    };
}
