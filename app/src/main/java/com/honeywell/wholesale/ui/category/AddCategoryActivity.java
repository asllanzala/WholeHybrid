package com.honeywell.wholesale.ui.category;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import com.honeywell.wholesale.R;
import com.honeywell.wholesale.framework.application.AppInitManager;
import com.honeywell.wholesale.framework.http.NativeJsonResponseListener;
import com.honeywell.wholesale.framework.model.CategoriesManager;
import com.honeywell.wholesale.framework.model.Category;
import com.honeywell.wholesale.ui.base.BaseTextView;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by xiaofei on 12/26/16.
 *
 */

public class AddCategoryActivity extends Activity{

    private static final String TAG = AddCategoryActivity.class.getSimpleName();
    private BaseTextView addCategoryTextView;
    private ListView categoryListView;
    private ImageView backImageView;

    private ArrayList<Category> arrayList = new ArrayList<>();
    private CategoryListAdapter listAdapter;

    private static final String CATEGORY = "CATEGORY";
    private static final String MODIFY_CATEGORY = "MODIFY";
    private static final String CREATE_CATEGORY = "CREATE";

    private static final String CATEGORY_ID = "CATEGORY_ID";
    private static final String CATEGORY_NAME = "CATEGORY_NAME";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_add);
        addCategoryTextView = (BaseTextView)findViewById(R.id.add_category_textView);
        categoryListView = (ListView)findViewById(R.id.category_list);
        backImageView = (ImageView)findViewById(R.id.icon_back);
        addCategoryTextView.setOnClickListener(onClickListener);
        backImageView.setOnClickListener(onClickListener);
        categoryListView.setOnItemClickListener(onItemClickListener);


        arrayList = CategoriesManager.getInstance().getFullCategories();
        listAdapter = new CategoryListAdapter(this, arrayList);
        categoryListView.setAdapter(listAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        AppInitManager.getInstance().getFullCategory(new NativeJsonResponseListener<JSONObject>() {
            @Override
            public void listener(JSONObject jsonObject) {
                Log.e(TAG, "update category list");
                listAdapter.setData(CategoriesManager.getInstance().getFullCategories());
            }

            @Override
            public void errorListener(String s) {

            }
        });
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.add_category_textView){
                Intent intent = new Intent(AddCategoryActivity.this, ModifyCategoryActivity.class);
                intent.putExtra(CATEGORY, CREATE_CATEGORY);
                startActivity(intent);
            }

            if (v.getId() == R.id.icon_back){
                finish();
            }
        }
    };

    private AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Category category = CategoriesManager.getInstance().getFullCategories().get(position);
            Intent intent = new Intent(AddCategoryActivity.this, ModifyCategoryActivity.class);
            intent.putExtra(CATEGORY, MODIFY_CATEGORY);
            int categoryId = Integer.valueOf(category.getCategoryId());
            intent.putExtra(CATEGORY_ID, categoryId);
            intent.putExtra(CATEGORY_NAME, category.getName());
            startActivity(intent);
        }
    };
}
