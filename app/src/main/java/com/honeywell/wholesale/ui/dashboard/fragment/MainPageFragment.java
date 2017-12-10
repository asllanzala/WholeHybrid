package com.honeywell.wholesale.ui.dashboard.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.honeywell.wholesale.R;
import com.honeywell.wholesale.framework.application.AppInitManager;
import com.honeywell.wholesale.ui.base.BaseRootFragment;
import com.honeywell.wholesale.ui.base.BaseTextView;
import com.honeywell.wholesale.ui.inventory.AddProductActivity;
import com.honeywell.wholesale.ui.purchase.activity.PurchaseActivity;
import com.honeywell.wholesale.ui.saleReturn.PreSaleReturnActivity;
import com.honeywell.wholesale.ui.search.BaseSearchActivity;
import com.honeywell.wholesale.ui.search.GlobalBaseSearchActivity;
import com.honeywell.wholesale.ui.search.InventorySearchActivity;
import com.honeywell.wholesale.ui.transaction.preorders.PreTransactionActivity;


/**
 * Created by xiaofei on 1/5/17.
 *
 */

public class MainPageFragment extends BaseRootFragment {

    public static final String TAG = MainPageFragment.class.getSimpleName();
    private ImageView mainPageSearch;
    private ImageView mainPageCreateOrder;
    private ImageView mainPageInStock;
    private ImageView mainPageNewProduct;
    private ImageView mainPageSaleReturn;
//    private ImageView circleImageView;

    private BaseTextView baseTextView;

    private static final String SHARE_PREFERENCE_APP_USER_PICS = "userPics";
    private static final String SHARE_PREFERENCE_APP_USER_PICS_URL = "userPicsUrl";
    private static final String SHARE_PREFERENCE_APP_USER_PICS_HD_URL = "userPicsHdUrl";

    public MainPageFragment() {
    }

    @Override
    public int getLayout() {
        return R.layout.fragment_mainpage_native;
    }

    @Override
    public int getIndex() {
        return 2;
    }

    @Override
    public void initView(View view) {
        super.initView(view);
        mainPageSearch = (ImageView)view.findViewById(R.id.main_page_search);
        mainPageCreateOrder = (ImageView)view.findViewById(R.id.main_page_create_order);
        mainPageInStock = (ImageView)view.findViewById(R.id.main_page_in_stock);
        mainPageNewProduct = (ImageView)view.findViewById(R.id.main_page_create_new_product);
        mainPageSaleReturn = (ImageView)view.findViewById(R.id.main_page_home_sale_return);
//        circleImageView = (ImageView)view.findViewById(R.id.main_page_avatar);

//        baseTextView = (BaseTextView) view.findViewById(R.id.pppp);
//        baseTextView.setText(WholesaleApplication.getInstance().getPn());

        mainPageSearch.setOnClickListener(onClickListener);
        mainPageCreateOrder.setOnClickListener(onClickListener);
        mainPageInStock.setOnClickListener(onClickListener);
        mainPageNewProduct.setOnClickListener(onClickListener);
        mainPageSaleReturn.setOnClickListener(onClickListener);

        AppInitManager.getInstance().startSyncData();
    }

    @Override
    public void reLoadData() {
        Log.e(TAG,"reLoadData");
    }

    @Override
    public String getTitle() {
        return null;
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.main_page_search){
                Intent intent = new Intent(getActivity(), GlobalBaseSearchActivity.class);
                startActivity(intent);
            }

            if (v.getId() == R.id.main_page_create_order){
                Intent intent = new Intent(getActivity(), PreTransactionActivity.class);
                intent.putExtra(PreTransactionActivity.INTENT_TYPE, PreTransactionActivity.INTENT_ADD_TO_TRANSACTION);
                startActivity(intent);
            }

            if (v.getId() == R.id.main_page_in_stock){
                Intent intent = new Intent(getActivity(), PurchaseActivity.class);
                startActivity(intent);

//                Intent intent = new Intent(getActivity(), InventorySearchActivity.class);
//                Bundle mBundle = new Bundle();
//                mBundle.putString(BaseSearchActivity.SEARCH_MODE, InventorySearchActivity.SEARCH_SELECT);
//                intent.putExtras(mBundle);
//                startActivity(intent);




//                Intent intent = new Intent(getActivity(), BaseSearchActivity.class);
//                Bundle mBundle = new Bundle();
//                mBundle.putString(BaseSearchActivity.SEARCH_MODE, BaseSearchActivity.SEARCH_SELECT);
//                intent.putExtra(PreTransactionActivity.SEARCH_FOR_TRANSACTION_ORDER, "");
//                mBundle.putSerializable(BaseSearchActivity.SEARCH_KEY, SearchResultItem.ResultType.INVENTORY);
//                intent.putExtras(mBundle);
//                startActivity(intent);
            }

            if (v.getId() == R.id.main_page_create_new_product){
                if (getActivity() != null){
                    Intent intent = new Intent(getActivity(), AddProductActivity.class);
                    startActivity(intent);
                }
            }
            if (v.getId() == R.id.main_page_home_sale_return){
                Intent intentSaleReturn = new Intent(getActivity(), PreSaleReturnActivity.class);
                startActivity(intentSaleReturn);
            }
        }
    };
}
