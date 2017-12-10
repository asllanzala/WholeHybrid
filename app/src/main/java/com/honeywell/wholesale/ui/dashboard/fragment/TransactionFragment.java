package com.honeywell.wholesale.ui.dashboard.fragment;

import com.google.gson.Gson;

import com.google.gson.reflect.TypeToken;
import com.honeywell.hybridapp.framework.log.LogHelper;
import com.honeywell.wholesale.R;
import com.honeywell.wholesale.framework.application.WholesaleApplication;
import com.honeywell.wholesale.framework.database.CartDAO;
import com.honeywell.wholesale.framework.database.CartRefCustomerDao;
import com.honeywell.wholesale.framework.database.CartRefSKUDao;
import com.honeywell.wholesale.framework.database.IncrementalDAO;
import com.honeywell.wholesale.framework.database.OrderDAO;
import com.honeywell.wholesale.framework.database.StockDAO;
import com.honeywell.wholesale.framework.http.NativeJsonResponseListener;
import com.honeywell.wholesale.framework.http.WebClient;
import com.honeywell.wholesale.framework.model.Account;
import com.honeywell.wholesale.framework.model.AccountManager;
import com.honeywell.wholesale.framework.model.Cart;
import com.honeywell.wholesale.framework.model.CartItem;
import com.honeywell.wholesale.framework.model.CartRefCustomer;
import com.honeywell.wholesale.framework.model.CartRefSKU;
import com.honeywell.wholesale.framework.utils.WSComparator;
import com.honeywell.wholesale.lib.util.StringUtil;
import com.honeywell.wholesale.ui.base.BaseRootFragment;
import com.honeywell.wholesale.ui.base.BaseTextView;
import com.honeywell.wholesale.ui.inventory.AddProductActivity;
import com.honeywell.wholesale.ui.search.BaseSearchActivity;
import com.honeywell.wholesale.ui.search.InventorySearchActivity;
import com.honeywell.wholesale.ui.search.TransactionSearchActivity;
import com.honeywell.wholesale.ui.transaction.cancel.CancelOrderActivity;
import com.honeywell.wholesale.ui.transaction.cart.CartManagementActivity;
import com.honeywell.wholesale.ui.dashboard.adapter.TransactionModelSelecteAdapter;
import com.honeywell.wholesale.ui.dashboard.adapter.TransactionOrderListAdapter;
import com.honeywell.wholesale.ui.dashboard.adapter.TransactionCartListAdapter;
import com.honeywell.wholesale.ui.inventory.ProductStockInActivity;
import com.honeywell.wholesale.ui.login.module.TransactionOrderRequest;
import com.honeywell.wholesale.ui.login.module.TransactionStockRequest;
import com.honeywell.wholesale.ui.login.module.TransactionStockUnconfirmRequest;
import com.honeywell.wholesale.ui.order.OrderDetailActivity;
import com.honeywell.wholesale.framework.model.Order;
import com.honeywell.wholesale.framework.model.StockItem;
import com.honeywell.wholesale.framework.model.UnStockItem;
import com.honeywell.wholesale.ui.transaction.preorders.PreOrdersCustomerActivity;
import com.honeywell.wholesale.ui.search.LoadMoreScrollListener;
import com.honeywell.wholesale.ui.transaction.TransactionCountRequest;
import com.honeywell.wholesale.ui.transaction.TransactionDetailRequest;
import com.honeywell.wholesale.ui.transaction.TransactionQueryRequest;
import com.honeywell.wholesale.ui.transaction.preorders.PreTransactionActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

/**
 * Created by e887272 on 7/12/16.
 * 销售查询：/transaction/sale/search, 增加过滤条件分别搜索：
 * 已完成(order_status==1)
 * 已取消(order_status==100)
 * 未支付（payment=0 and order_status=0）
 * 赊账中(payment=2 and order_status=0)
 */
public class TransactionFragment extends BaseRootFragment {
    public static final String TAG = TransactionFragment.class.getSimpleName();

    private BaseTextView mCartTextView;
    private BaseTextView mOrderBillTextView;
    private BaseTextView mOrderCreditTextView;
    private BaseTextView mOrderPaidTextView;

    private View mCartSelectedView;
    private View mOrderBillSelectedView;
    private View mOrderCreditSelectedView;
    private View mOrderPaidSelectedView;

    private TextView mStockAllTabTextView;
    private TextView mStockUnconfirmedTabTextView;
    private TextView mStockAdjustmentTabTextView;

    private LinearLayout mStokeInLinearLayout;
    private LinearLayout mOrderInLinearLayout;

//    private RelativeLayout subTabCreditLayout;
//    private RelativeLayout subTabSquareLayout;

    private LinearLayout subTabCreditFormatLayout;
    private LinearLayout subTabPaidFormatLayout;
//    private LinearLayout creditOrderView;
//    private LinearLayout squareOrderTimeView;
//    private LinearLayout squareOrderView;

    //    private BaseTextView creditFormatTextView;
//    private BaseTextView timeFormatTextView;
//    private BaseTextView paidOrderFormatTextView;
    private BaseTextView creditFormatTimeTextView;
    private BaseTextView creditFormatMoneyTextView;
    private BaseTextView creditFormatCustomerTextView;
    private BaseTextView paidFormatTimeTextView;
    private BaseTextView paidFormatMoneyTextView;
    private BaseTextView paidFormatCustomerTextView;


    private ArrayList<String> creditFormatModelList;
    private ArrayList<String> timeFormatModelList;
    private ArrayList<String> paidOrderFormatList;


    private LinearLayout mOrderListViewLayout;
    private LinearLayout mStockListViewLayout;

    private ListView mOrdersListView;
    private ListView mStockListView;

    private View headerView;

    private TransactionOrderListAdapter mOrdersAdapter;
    private StockAdapter mStockAdapter;
    private TransactionModelSelecteAdapter transactionModelSelecteAdapter;
    private TransactionCartListAdapter mCartAdapter;

    //开单未付, 赊账中, 已结清的数据
    private ArrayList<Order> mOrderDataList;
    //购物篮的数据
    private ArrayList<Cart> mCartGroupDataList = new ArrayList<>();
    private ArrayList<CartItem> mCartDataList = new ArrayList<>();

    private ArrayList<StockItem> mStockDataList;
    private ArrayList<StockItem> mUnconfirmStockDataList;
    private ArrayList<StockItem> mUpdateStockDataList;

    private PopupWindow creditModelSelectPopup;
    private PopupWindow paidModelTimeSelectPopup;
    private PopupWindow paidSortConditionSelectPopup;

    private WebClient webClient;
    private String shopId;

    // 网络访问参数
    private TransactionQueryRequest transactionQueryRequest;
    private TransactionCountRequest transactionCountRequest;
    private TransactionDetailRequest transactionDetailRequest;
    private static String isLoadMore = "false";


    private String pageNumber = "0";
    private LoadMoreScrollListener loadMoreScrollListener;
    private String startTime = "";

    private String creditOrderBy = "finish_time";
    ;
    private String paidOrderBy = "finish_time";
    private String descOrder = "false";

    private String orderStatus;
    private String payMent;

    private void setmTransactionSearchStatus(String orderStatus, String payMent) {
        this.orderStatus = orderStatus;
        this.payMent = payMent;
    }

    @Override
    public int getLayout() {
        return R.layout.fragment_transaction_native;
    }

    @Override
    public int getIndex() {
        return 1;
    }

    @Override
    public void initImageMore(ImageView view) {
        super.initImageMore(view);
        view.setImageResource(R.drawable.dashboard_title_more);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), CancelOrderActivity.class));
            }
        });
    }
    @Override
    public void initLayoutSearch(View view) {
        super.initLayoutSearch(view);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), TransactionSearchActivity.class);
                intent.putExtra("orderstatus", orderStatus);
                intent.putExtra("payment", payMent);
                startActivity(intent);
            }
        });
    }
    @Override
    public void initView(View view) {
        super.initView(view);
        Account account = AccountManager.getInstance().getCurrentAccount();
        shopId = account.getCurrentShopId();
        webClient = new WebClient();

        transactionQueryRequest = new TransactionQueryRequest();
        transactionCountRequest = new TransactionCountRequest();
        transactionDetailRequest = new TransactionDetailRequest();

        setmTransactionSearchStatus("0", "0");

        creditFormatModelList = new ArrayList<>(Arrays.asList(getContext().getResources().getStringArray(R.array.transaction_unpaied_select_credit)));
        timeFormatModelList = new ArrayList<>(Arrays.asList(getContext().getResources().getStringArray(R.array.transaction_paid_select_time)));
        paidOrderFormatList = new ArrayList<>(Arrays.asList(getContext().getResources().getStringArray(R.array.transaction_paid_select_paidOrder)));

        transactionModelSelecteAdapter = new TransactionModelSelecteAdapter(creditFormatModelList, getContext());

        mStokeInLinearLayout = (LinearLayout) view.findViewById(R.id.transaction_fragment_stokein_sub_tab_layout);
        mOrderInLinearLayout = (LinearLayout) view.findViewById(R.id.transaction_fragment_order_sub_tab_layout);

        headerView = view.findViewById(R.id.trasnsaction_cart_headerview);
        headerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO 新建预售单的手续操作流程
                Intent intent = new Intent(getActivity(), PreTransactionActivity.class);
                intent.putExtra(PreTransactionActivity.INTENT_TYPE, PreTransactionActivity.INTENT_ADD_TO_TRANSACTION);
                startActivity(intent);
            }
        });

//        searchLayout = (LinearLayout) view.findViewById(R.id.transaction_search_layout);
//        searchLayout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(getActivity(), TransactionSearchActivity.class);
//                intent.putExtra("orderstatus", "");
//                intent.putExtra("payment", "");
//                startActivity(intent);
//            }
//        });

        mCartTextView = (BaseTextView) view.findViewById(R.id.tab_order_cart);
        mOrderBillTextView = (BaseTextView) view.findViewById(R.id.tab_order_bill_no_payment);
        mOrderCreditTextView = (BaseTextView) view.findViewById(R.id.tab_order_credit);
        mOrderPaidTextView = (BaseTextView) view.findViewById(R.id.tab_order_paid);

        mCartSelectedView = view.findViewById(R.id.tab_order_cart_selected_sign);
        mOrderBillSelectedView = view.findViewById(R.id.tab_order_bill_no_payment_selected_sign);
        mOrderCreditSelectedView = view.findViewById(R.id.tab_order_credit_selected_sign);
        mOrderPaidSelectedView = view.findViewById(R.id.tab_order_paid_selected_sign);

        mCartTextView.setSelected(true);

//        subTabCreditLayout = (RelativeLayout) view.findViewById(R.id.sub_tab_credit_layout);
//        subTabSquareLayout = (RelativeLayout) view.findViewById(R.id.sub_tab_square_layout);

        subTabCreditFormatLayout = (LinearLayout) view.findViewById(R.id.sub_tab_credit_format_layout);
        subTabPaidFormatLayout = (LinearLayout) view.findViewById(R.id.sub_tab_paid_format_layout);

//        creditOrderView = (LinearLayout) view.findViewById(R.id.credit_order_view);
//        squareOrderTimeView = (LinearLayout) view.findViewById(R.id.square_order_time_view);
//        squareOrderView = (LinearLayout) view.findViewById(R.id.square_order_view);
//
//        creditFormatTextView = (BaseTextView) view.findViewById(R.id.credit_format_textView);
//        timeFormatTextView = (BaseTextView) view.findViewById(R.id.order_time_format_textView);
//        paidOrderFormatTextView = (BaseTextView) view.findViewById(R.id.square_order_format_textView);
        creditFormatTimeTextView = (BaseTextView) view.findViewById(R.id.sub_tab_credit_format_time_textview);
        creditFormatTimeTextView.setSelected(true);
        creditFormatTimeTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WholesaleApplication.cancelAllRequest();
                creditFormatTimeTextView.setSelected(true);
                creditFormatMoneyTextView.setSelected(false);
                creditFormatCustomerTextView.setSelected(false);
                queryByCreditFormat();
            }
        });

        creditFormatMoneyTextView = (BaseTextView) view.findViewById(R.id.sub_tab_credit_format_money_textview);
        creditFormatMoneyTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WholesaleApplication.cancelAllRequest();
                creditFormatTimeTextView.setSelected(false);
                creditFormatMoneyTextView.setSelected(true);
                creditFormatCustomerTextView.setSelected(false);
                queryByCreditFormat();
            }
        });
        creditFormatCustomerTextView = (BaseTextView) view.findViewById(R.id.sub_tab_credit_format_customer_textview);
        creditFormatCustomerTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WholesaleApplication.cancelAllRequest();
                creditFormatTimeTextView.setSelected(false);
                creditFormatMoneyTextView.setSelected(false);
                creditFormatCustomerTextView.setSelected(true);
                queryByCreditFormat();
            }
        });
        paidFormatTimeTextView = (BaseTextView) view.findViewById(R.id.sub_tab_paid_format_time_textview);
        paidFormatTimeTextView.setSelected(true);
        paidFormatTimeTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WholesaleApplication.cancelAllRequest();
                paidFormatTimeTextView.setSelected(true);
                paidFormatMoneyTextView.setSelected(false);
                paidFormatCustomerTextView.setSelected(false);
                queryByPaidFormat();
            }
        });
        paidFormatMoneyTextView = (BaseTextView) view.findViewById(R.id.sub_tab_paid_format_money_textview);
        paidFormatMoneyTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WholesaleApplication.cancelAllRequest();
                paidFormatTimeTextView.setSelected(false);
                paidFormatMoneyTextView.setSelected(true);
                paidFormatCustomerTextView.setSelected(false);
                queryByPaidFormat();
            }
        });
        paidFormatCustomerTextView = (BaseTextView) view.findViewById(R.id.sub_tab_paid_format_customer_textview);
        paidFormatCustomerTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WholesaleApplication.cancelAllRequest();
                paidFormatTimeTextView.setSelected(false);
                paidFormatMoneyTextView.setSelected(false);
                paidFormatCustomerTextView.setSelected(true);
                queryByPaidFormat();
            }
        });

//        // 初始化子tab(账期,金额,客户)
//        creditFormatTextView.setText(creditFormatModelList.get(0));
//        timeFormatTextView.setText(timeFormatModelList.get(0));
//        paidOrderFormatTextView.setText(paidOrderFormatList.get(0));

        mStockAllTabTextView = (TextView) view.findViewById(R.id.transaction_fragment_tab_stock_all);
        mStockUnconfirmedTabTextView = (TextView) view.findViewById(R.id.transaction_fragment_tab_stock_unconfirmed);
        mStockAdjustmentTabTextView = (TextView) view.findViewById(R.id.transaction_fragment_tab_stock_adjustment);

        mOrderListViewLayout = (LinearLayout) view.findViewById(R.id.orders_listView_layout);
        mStockListViewLayout = (LinearLayout) view.findViewById(R.id.stock_history_listView_layout);

        mOrdersListView = (ListView) view.findViewById(R.id.orders_listView);
        mStockListView = (ListView) view.findViewById(R.id.stock_history_listView);

        mOrdersListView.setOnItemClickListener(onItemClickListener);
        mStockListView.setOnItemClickListener(onItemClickListener);

        loadMoreScrollListener = new LoadMoreScrollListener();

        mCartTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.isSelected()) {
                    return;
                }
                WholesaleApplication.cancelAllRequest();

                // todo 更新购物篮数据

                switchToCart();
                queryCartGroup();
            }
        });

        mOrderBillTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.isSelected()) {
                    return;
                }

                WholesaleApplication.cancelAllRequest();
                // todo 更新开单未付数据
                switchToBill();
                queryUnpaidOrder("0", "0", false);
            }
        });

        mOrderCreditTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.isSelected()) {
                    return;
                }

                WholesaleApplication.cancelAllRequest();
                // todo 更新赊账中数据
                switchToCredit();
                queryOnCreditOrder(creditOrderBy, "2", "0", false);
            }
        });

        mOrderPaidTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.isSelected()) {
                    return;
                }

                WholesaleApplication.cancelAllRequest();
                // todo 更新已结清数据
                switchToPaid();
                queryPaidOrder(paidOrderBy, "1", startTime, descOrder, false);

            }
        });

        mStockAllTabTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // set selected logic
                v.setSelected(true);
                mStockUnconfirmedTabTextView.setSelected(false);
                mStockAdjustmentTabTextView.setSelected(false);

                mStockAllTabTextView.setTextColor(getContext().getResources().getColor(R.color.light_blue));
                mStockUnconfirmedTabTextView.setTextColor(getContext().getResources().getColor(R.color.black));
                mStockAdjustmentTabTextView.setTextColor(getContext().getResources().getColor(R.color.black));

                mStockAdapter.setStatus(StockDAO.STOCK_IN_STATUS);
                mStockAdapter.setListData(mStockDataList);
                mStockAdapter.notifyDataSetChanged();

                refreshStockListView();
            }
        });

        mStockUnconfirmedTabTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setSelected(true);
                mStockAllTabTextView.setSelected(false);
                mStockAdjustmentTabTextView.setSelected(false);

                mStockAllTabTextView.setTextColor(getContext().getResources().getColor(R.color.black));
                mStockUnconfirmedTabTextView.setTextColor(getContext().getResources().getColor(R.color.light_blue));
                mStockAdjustmentTabTextView.setTextColor(getContext().getResources().getColor(R.color.black));

                mStockAdapter.setStatus(StockDAO.STOCK_UNCONFIRM_STATUS);
                mStockAdapter.setListData(mUnconfirmStockDataList);
                mStockAdapter.notifyDataSetChanged();

                refreshUnStockListView();
            }
        });

        mStockAdjustmentTabTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setSelected(true);
                mStockAllTabTextView.setSelected(false);
                mStockUnconfirmedTabTextView.setSelected(false);

                mStockAllTabTextView.setTextColor(getContext().getResources().getColor(R.color.black));
                mStockUnconfirmedTabTextView.setTextColor(getContext().getResources().getColor(R.color.black));
                mStockAdjustmentTabTextView.setTextColor(getContext().getResources().getColor(R.color.light_blue));
                mStockAdapter.setStatus(StockDAO.STOCK_UPDATE_STATUS);
                refreshUpdateStockListView();
            }
        });


//        creditOrderView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                LogHelper.e("Credit", "赊账中排序选择");
//                initCreditPopupWindow();
//                int left = Math.round(v.getX()) + v.getWidth() / 2 - 160 - 115;
//                int top = v.getTop();
//                creditModelSelectPopup.showAsDropDown(v, left, top);
//            }
//        });
//
//        squareOrderTimeView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                LogHelper.e("Paid", "已结清");
//                initPaidTimePopupWindow();
//                int left = Math.round(v.getX()) + v.getWidth() / 2 - 160 - 115;
//                int top = v.getTop();
//                paidModelTimeSelectPopup.showAsDropDown(v, left, top);
//            }
//        });
//
//        squareOrderView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                LogHelper.e("paid", "按客户");
//                initPaidRefPopupWindow();
//                int left = Math.round(v.getX()) + v.getWidth() / 2 - 160 - 115;
//                int top = v.getTop();
//                paidSortConditionSelectPopup.showAsDropDown(v, left, top);
//            }
//        });

        mOrdersListView.setOnScrollListener(loadMoreScrollListener);
        loadMoreScrollListener.setLoadMore(new LoadMoreScrollListener.LoadMore() {
            @Override
            public void loadMoreData() {
                if (mOrderBillTextView.isSelected() && isLoadMore.equals("true")) {
                    // 开单未付（payment=0 and order_status=0）
                    queryUnpaidOrder("0", "0", true);
                }

                if (mOrderCreditTextView.isSelected() && isLoadMore.equals("true")) {
                    queryOnCreditOrder(creditOrderBy, "2", "0", true);
                }

                if (mOrderPaidTextView.isSelected() && isLoadMore.equals("true")) {
                    queryPaidOrder(paidOrderBy, "1", startTime, descOrder, true);
                }
            }
        });

        //startTime = String.valueOf(WSTimeStamp.getSpecialDayStartTime(-7));
    }

    @Override
    public void onResume() {
        super.onResume();
        // to sync data between db and memory
        initData();
        initOrderListView();
        //initStockListView();
        //getDataFromServer();
    }


    @Override
    public void reLoadData() {
    }

    /**
     * 交易管理下的购物篮
     */
    private void queryCartGroup() {
        initCartGroupData();
        //if (mCartGroupDataList != null) {
        mOrdersListView.setAdapter(mCartAdapter);
        mCartAdapter.setArrayList(mCartGroupDataList);
        mCartAdapter.notifyDataSetChanged();
        //}
    }

    /**
     * 对购物篮的数据进行初始化
     */
    private void initCartGroupData() {

        String shopId = AccountManager.getInstance().getCurrentAccount().getCurrentShopId();
        String employeeId = AccountManager.getInstance().getCurrentAccount().getEmployeeId();
        mCartGroupDataList.clear();
        ArrayList<CartRefCustomer> cartRefCustomerArrayList = new ArrayList<>();
        cartRefCustomerArrayList = CartRefCustomerDao.getAllCartRefCustomerItems(shopId);
        for (int i = 0; i < cartRefCustomerArrayList.size(); i++) {
            String uuid = cartRefCustomerArrayList.get(i).getUuid();
            ArrayList<CartRefSKU> cartRefSKUArrayList = CartRefSKUDao.queryCartRefSKU(uuid);
            int amount = cartRefSKUArrayList.size();
            mCartGroupDataList.add(new Cart(cartRefCustomerArrayList.get(i), String.valueOf(amount)));
        }
    }

    private void switchToCart() {
        subTabCreditFormatLayout.setVisibility(View.GONE);
        subTabPaidFormatLayout.setVisibility(View.GONE);
        headerView.setVisibility(View.VISIBLE);

        mCartTextView.setSelected(true);
        mOrderBillTextView.setSelected(false);
        mOrderCreditTextView.setSelected(false);
        mOrderPaidTextView.setSelected(false);

        mCartTextView.setBackgroundColor(getContext().getResources().getColor(R.color.transaction_tab_selected_bg_color));
        mOrderBillTextView.setBackgroundColor(getContext().getResources().getColor(R.color.transaction_tab_bg_color));
        mOrderCreditTextView.setBackgroundColor(getContext().getResources().getColor(R.color.transaction_tab_bg_color));
        mOrderPaidTextView.setBackgroundColor(getContext().getResources().getColor(R.color.transaction_tab_bg_color));

        mCartTextView.setTextColor(getContext().getResources().getColor(R.color.transaction_tab_selected_text_color));
        mOrderBillTextView.setTextColor(getContext().getResources().getColor(R.color.transaction_tab_text_color));
        mOrderCreditTextView.setTextColor(getContext().getResources().getColor(R.color.transaction_tab_text_color));
        mOrderPaidTextView.setTextColor(getContext().getResources().getColor(R.color.transaction_tab_text_color));
//        mOrderCancelTextview.setTextColor(getContext().getResources().getColor(R.color.black));

        mCartSelectedView.setBackgroundColor(getContext().getResources().getColor(R.color.transaction_tab_bg_color));
        mOrderBillSelectedView.setBackgroundColor(getContext().getResources().getColor(R.color.transaction_border_selected_color));
        mOrderCreditSelectedView.setBackgroundColor(getContext().getResources().getColor(R.color.transaction_border_selected_color));
        mOrderPaidSelectedView.setBackgroundColor(getContext().getResources().getColor(R.color.transaction_border_selected_color));

        mOrdersAdapter.clearListData();
        mCartAdapter.clearListData();

    }

    private void switchToBill() {
//        subTabCreditLayout.setVisibility(View.GONE);
//        subTabSquareLayout.setVisibility(View.GONE);
        mOrdersListView.setAdapter(mOrdersAdapter);
        subTabCreditFormatLayout.setVisibility(View.GONE);
        subTabPaidFormatLayout.setVisibility(View.GONE);
        headerView.setVisibility(View.GONE);

        mCartTextView.setSelected(false);
        mOrderBillTextView.setSelected(true);
        mOrderCreditTextView.setSelected(false);
        mOrderPaidTextView.setSelected(false);

        mCartTextView.setBackgroundColor(getContext().getResources().getColor(R.color.transaction_tab_bg_color));
        mOrderBillTextView.setBackgroundColor(getContext().getResources().getColor(R.color.transaction_tab_selected_bg_color));
        mOrderCreditTextView.setBackgroundColor(getContext().getResources().getColor(R.color.transaction_tab_bg_color));
        mOrderPaidTextView.setBackgroundColor(getContext().getResources().getColor(R.color.transaction_tab_bg_color));

        mCartTextView.setTextColor(getContext().getResources().getColor(R.color.transaction_tab_text_color));
        mOrderBillTextView.setTextColor(getContext().getResources().getColor(R.color.transaction_tab_selected_text_color));
        mOrderCreditTextView.setTextColor(getContext().getResources().getColor(R.color.transaction_tab_text_color));
        mOrderPaidTextView.setTextColor(getContext().getResources().getColor(R.color.transaction_tab_text_color));
//        mOrderCancelTextview.setTextColor(getContext().getResources().getColor(R.color.black));

        mCartSelectedView.setBackgroundColor(getContext().getResources().getColor(R.color.transaction_tab_bg_color));
        mOrderBillSelectedView.setBackgroundColor(getContext().getResources().getColor(R.color.transaction_tab_bg_color));
        mOrderCreditSelectedView.setBackgroundColor(getContext().getResources().getColor(R.color.transaction_border_selected_color));
        mOrderPaidSelectedView.setBackgroundColor(getContext().getResources().getColor(R.color.transaction_border_selected_color));

        pageNumber = "0";

        setmTransactionSearchStatus("0", "0");
        mOrdersAdapter.clearListData();
        mCartAdapter.clearListData();

    }

    private void switchToCredit() {
        mOrdersListView.setAdapter(mOrdersAdapter);
        subTabCreditFormatLayout.setVisibility(View.VISIBLE);
        subTabPaidFormatLayout.setVisibility(View.GONE);
//        subTabCreditLayout.setVisibility(View.VISIBLE);
//        subTabSquareLayout.setVisibility(View.GONE);
        headerView.setVisibility(View.GONE);


        mCartTextView.setSelected(false);
        mOrderBillTextView.setSelected(false);
        mOrderCreditTextView.setSelected(true);
        mOrderPaidTextView.setSelected(false);

        mCartTextView.setBackgroundColor(getContext().getResources().getColor(R.color.transaction_tab_bg_color));
        mOrderBillTextView.setBackgroundColor(getContext().getResources().getColor(R.color.transaction_tab_bg_color));
        mOrderCreditTextView.setBackgroundColor(getContext().getResources().getColor(R.color.transaction_tab_selected_bg_color));
        mOrderPaidTextView.setBackgroundColor(getContext().getResources().getColor(R.color.transaction_tab_bg_color));

        mCartTextView.setTextColor(getContext().getResources().getColor(R.color.transaction_tab_text_color));
        mOrderBillTextView.setTextColor(getContext().getResources().getColor(R.color.transaction_tab_text_color));
        mOrderCreditTextView.setTextColor(getContext().getResources().getColor(R.color.transaction_tab_selected_text_color));
        mOrderPaidTextView.setTextColor(getContext().getResources().getColor(R.color.transaction_tab_text_color));
//        mOrderCancelTextview.setTextColor(getContext().getResources().getColor(R.color.black));

        mCartSelectedView.setBackgroundColor(getContext().getResources().getColor(R.color.transaction_border_selected_color));
        mOrderBillSelectedView.setBackgroundColor(getContext().getResources().getColor(R.color.transaction_tab_bg_color));
        mOrderCreditSelectedView.setBackgroundColor(getContext().getResources().getColor(R.color.transaction_tab_bg_color));
        mOrderPaidSelectedView.setBackgroundColor(getContext().getResources().getColor(R.color.transaction_border_selected_color));

        pageNumber = "0";

        setmTransactionSearchStatus("0", "2");
        mOrdersAdapter.clearListData();
        mCartAdapter.clearListData();

    }

    private void switchToPaid() {
        mOrdersListView.setAdapter(mOrdersAdapter);
        subTabCreditFormatLayout.setVisibility(View.GONE);
        subTabPaidFormatLayout.setVisibility(View.VISIBLE);
//        subTabCreditLayout.setVisibility(View.GONE);
//        subTabSquareLayout.setVisibility(View.VISIBLE);
        headerView.setVisibility(View.GONE);

        mCartTextView.setSelected(false);
        mOrderBillTextView.setSelected(false);
        mOrderCreditTextView.setSelected(false);
        mOrderPaidTextView.setSelected(true);

        mCartTextView.setBackgroundColor(getContext().getResources().getColor(R.color.transaction_tab_bg_color));
        mOrderBillTextView.setBackgroundColor(getContext().getResources().getColor(R.color.transaction_tab_bg_color));
        mOrderCreditTextView.setBackgroundColor(getContext().getResources().getColor(R.color.transaction_tab_bg_color));
        mOrderPaidTextView.setBackgroundColor(getContext().getResources().getColor(R.color.transaction_tab_selected_bg_color));

        mCartTextView.setTextColor(getContext().getResources().getColor(R.color.transaction_tab_text_color));
        mOrderBillTextView.setTextColor(getContext().getResources().getColor(R.color.transaction_tab_text_color));
        mOrderCreditTextView.setTextColor(getContext().getResources().getColor(R.color.transaction_tab_text_color));
        mOrderPaidTextView.setTextColor(getContext().getResources().getColor(R.color.transaction_tab_selected_text_color));
//        mOrderCancelTextview.setTextColor(getContext().getResources().getColor(R.color.black));

        mCartSelectedView.setBackgroundColor(getContext().getResources().getColor(R.color.transaction_border_selected_color));
        mOrderBillSelectedView.setBackgroundColor(getContext().getResources().getColor(R.color.transaction_border_selected_color));
        mOrderCreditSelectedView.setBackgroundColor(getContext().getResources().getColor(R.color.transaction_tab_bg_color));
        mOrderPaidSelectedView.setBackgroundColor(getContext().getResources().getColor(R.color.transaction_tab_bg_color));

        pageNumber = "0";
        setmTransactionSearchStatus("1", "");
        mOrdersAdapter.clearListData();
        mCartAdapter.clearListData();

    }

//    /**
//     * 交易数量统计
//     */
//
//    private void queryCountOrder() {
//        String shopId = AccountManager.getInstance().getCurrentAccount().getCurrentShopId();
//        // 按照销售时间排序,可以下面任意值："sale_time"/"customer_name_pinyin"/"total_price"
//        transactionCountRequest = new TransactionCountRequest(shopId);
//
//        webClient.httpQueryTransactionCountOrders(transactionCountRequest, new NativeJsonResponseListener<JSONObject>() {
//            @Override
//            public void listener(JSONObject jsonObject) {
//                String finishedNumber = jsonObject.optString("finished_number");
//                String initedNumber = jsonObject.optString("inited_number");
//                String canceledNumber = jsonObject.optString("canceled_number");
//                String debtingNumber = jsonObject.optString("debting_number");
//
//                // 如果快速切换,第一次的网络访问还没有完成,切换到另外一个页面,会产生getContext is null
//                if (getContext() == null) {
//                    return;
//                }
//                String paidTitleText = getContext().getString(R.string.transaction_fragment_tab_order_paid)
//                        + "\n" + "(" + finishedNumber + ")";
//                String creditTitleText = getContext().getString(R.string.transaction_fragment_tab_order_unpaid)
//                        + "\n" + "(" + debtingNumber + ")";
//                String unpaidTitleText = getContext().getString(R.string.transaction_fragment_tab_order_billing)
//                        + "\n" + "(" + initedNumber + ")";
//                String cancelTitleText = getContext().getString(R.string.transaction_fragment_tab_order_cancel)
//                        + "\n" + "(" + canceledNumber + ")";
//
//                mOrderPaidTextView.setText(paidTitleText);
//                mOrderCreditTextView.setText(creditTitleText);
//                mOrderBillTextView.setText(unpaidTitleText);
//            }
//
//            @Override
//            public void errorListener(String s) {
//                // 错误的情况下 页面展现
//                LogHelper.getInstance().e(TAG, "获取交易数量统计失败：");
//            }
//        });
//    }

    /**
     * 查询销售订单下的未付款
     *
     * @param
     * @param payment
     * @param orderStatus
     */

    private void queryUnpaidOrder(String payment, String orderStatus, final boolean isCallFormLoadMore) {
        String shopId = AccountManager.getInstance().getCurrentAccount().getCurrentShopId();
        // 按照销售时间排序,可以下面任意值："sale_time"/"customer_name_pinyin"/"total_price"
        transactionQueryRequest = new TransactionQueryRequest(shopId, pageNumber, "", payment, orderStatus);
        String s = transactionQueryRequest.getJsonString();
        webClient.httpQueryTransactionOrders(transactionQueryRequest, new NativeJsonResponseListener<JSONObject>() {
            @Override
            public void listener(JSONObject jsonObject) {
                // 成功的情况下页面展示
                String orderCount = jsonObject.optString("count");
                JSONArray jsonArray = jsonObject.optJSONArray("total_order_list");
                String nextPage = jsonObject.optString("next_page");
                Log.e(TAG, "开单未付" + jsonArray.toString());
                isLoadMore = nextPage;
                if (isLoadMore.equals("true")) {
                    pageNumber = String.valueOf(Integer.valueOf(pageNumber) + 1);
                }
                // 解析order数据
                Gson gson = new Gson();
                String ordersJson = jsonArray.toString();

                Type collectionType = new TypeToken<Collection<Order>>() {
                }.getType();
                ArrayList<Order> orders = gson.fromJson(ordersJson, collectionType);

                if (isCallFormLoadMore) {
                    mOrderDataList.addAll(orders);
                } else {
                    mOrderDataList = orders;
                }


//                mOrdersListView.setAdapter(mOrdersAdapter);
                mOrdersAdapter.setListData(mOrderDataList);
                mOrdersAdapter.notifyDataSetChanged();

                LoadMoreScrollListener.setIsLoading(false);

            }

            @Override
            public void errorListener(String s) {
                // 错误的情况下 页面展现
                LogHelper.getInstance().e(TAG, "查询销售订单下的开单未付失败：");
                Toast.makeText(getContext(), "网络连接超时", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * 查询销售订单下的赊账中
     *
     * @param orderBy
     * @param payment
     * @param orderStatus
     */

    private void queryOnCreditOrder(String orderBy, String payment, String orderStatus, final boolean isCallFromLoadMore) {
        String shopId = AccountManager.getInstance().getCurrentAccount().getCurrentShopId();
        // 按照销售时间排序,可以下面任意值："sale_time"/"customer_name_pinyin"/"total_price"
        transactionQueryRequest = new TransactionQueryRequest(shopId, pageNumber, orderBy, payment, orderStatus);

        webClient.httpQueryTransactionOrders(transactionQueryRequest, new NativeJsonResponseListener<JSONObject>() {
            @Override
            public void listener(JSONObject jsonObject) {
                // 成功的情况下页面展示
                String orderCount = jsonObject.optString("count");
                JSONArray jsonArray = jsonObject.optJSONArray("total_order_list");
                String nextPage = jsonObject.optString("next_page");
                Log.e(TAG, "赊账中" + jsonArray.toString());
                isLoadMore = nextPage;
                if (isLoadMore.equals("true")) {
                    pageNumber = String.valueOf(Integer.valueOf(pageNumber) + 1);
                }
                // 解析order数据
                Gson gson = new Gson();
                String ordersJson = jsonArray.toString();

                Type collectionType = new TypeToken<Collection<Order>>() {
                }.getType();
                ArrayList<Order> orders = gson.fromJson(ordersJson, collectionType);

                if (isCallFromLoadMore) {
                    mOrderDataList.addAll(orders);
                } else {
                    mOrderDataList = orders;
                }

//                mOrdersListView.setAdapter(mOrdersAdapter);
                mOrdersAdapter.setListData(mOrderDataList);
                mOrdersAdapter.notifyDataSetChanged();

                LoadMoreScrollListener.setIsLoading(false);
            }

            @Override
            public void errorListener(String s) {
                // 错误的情况下 页面展现
                LogHelper.getInstance().e(TAG, "查询销售订单下的赊账中失败：");
                Toast.makeText(getContext(), "网络连接超时", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * 查询销售订单下的已结清
     *
     * @param orderBy
     * @param orderStatus
     */

    private void queryPaidOrder(String orderBy, String orderStatus, String startTime, String descOrder, final boolean isCallFromLoadMore) {
        String shopId = AccountManager.getInstance().getCurrentAccount().getCurrentShopId();
        // 按照销售时间排序,可以下面任意值："sale_time"/"customer_name_pinyin"/"total_price"

        transactionQueryRequest = new TransactionQueryRequest(orderStatus,
                shopId, pageNumber, orderBy, startTime, descOrder);

        webClient.httpQueryTransactionOrders(transactionQueryRequest, new NativeJsonResponseListener<JSONObject>() {
            @Override
            public void listener(JSONObject jsonObject) {
                // 成功的情况下页面展示
                String orderCount = jsonObject.optString("count");
                JSONArray jsonArray = jsonObject.optJSONArray("total_order_list");
                String nextPage = jsonObject.optString("next_page");
                Log.e(TAG, "已结清" + jsonArray.toString());
                isLoadMore = nextPage;
                if (isLoadMore.equals("true")) {
                    pageNumber = String.valueOf(Integer.valueOf(pageNumber) + 1);
                }
                // 解析order数据
                Gson gson = new Gson();
                String ordersJson = jsonArray.toString();

                Type collectionType = new TypeToken<Collection<Order>>() {
                }.getType();
                ArrayList<Order> orders = gson.fromJson(ordersJson, collectionType);

                if (isCallFromLoadMore) {
                    mOrderDataList.addAll(orders);
                } else {
                    mOrderDataList = orders;
                }

//                mOrdersListView.setAdapter(mOrdersAdapter);
                mOrdersAdapter.setListData(mOrderDataList);
                mOrdersAdapter.notifyDataSetChanged();

                LoadMoreScrollListener.setIsLoading(false);
            }

            @Override
            public void errorListener(String s) {
                // 错误的情况下 页面展现
                LogHelper.getInstance().e(TAG, "查询销售订单下的已结清失败：");
                Toast.makeText(getContext(), "网络连接超时", Toast.LENGTH_SHORT).show();
            }
        });

    }

//    /**
//     * 查询销售订单下的已取消
//     * @param orderStatus
//     * @param isCallFromLoadMore
//     */
//    private void queryCancelOrder(String orderStatus, final boolean isCallFromLoadMore) {
//        String shopId = AccountManager.getInstance().getCurrentAccount().getCurrentShopId();
//
//        transactionQueryRequest = new TransactionQueryRequest(shopId, pageNumber, "finish_time",
//                "", orderStatus);
//
//        webClient.httpQueryTransactionOrders(transactionQueryRequest, new NativeJsonResponseListener<JSONObject>() {
//            @Override
//            public void listener(JSONObject jsonObject) {
//                String orderCount = jsonObject.optString("count");
//                JSONArray jsonArray = jsonObject.optJSONArray("total_order_list");
//                String nextPage = jsonObject.optString("next_page");
//                Log.e(TAG, "已取消" + jsonArray.toString());
//                isLoadMore = nextPage;
//                if (isLoadMore.equals("true")) {
//                    pageNumber = String.valueOf(Integer.valueOf(pageNumber) + 1);
//                }
//
//                // 解析order数据
//                Gson gson = new Gson();
//                String ordersJson = jsonArray.toString();
//
//                Type collectionType = new TypeToken<Collection<Order>>() {
//                }.getType();
//                ArrayList<Order> orders = gson.fromJson(ordersJson, collectionType);
//
//                if (isCallFromLoadMore) {
//                    mOrderDataList.addAll(orders);
//                } else {
//                    mOrderDataList = orders;
//                }
//
//                mOrdersListView.setAdapter(mOrdersAdapter);
//                mOrdersAdapter.setListData(mOrderDataList);
//                mOrdersAdapter.notifyDataSetChanged();
//
//                LoadMoreScrollListener.setIsLoading(false);
//
//            }
//
//            @Override
//            public void errorListener(String s) {
//                //错误的情况下,页面展现
//                LogHelper.getInstance().e(TAG, "查询销售订单下的已取消失败:");
//
//            }
//        });
//    }

    /**
     * 赊账中的搜索条件
     */
    private void queryByCreditFormat() {
        if (creditFormatTimeTextView.isSelected()) {
            creditOrderBy = "finish_time";
        } else if (creditFormatMoneyTextView.isSelected()) {
            creditOrderBy = "total_price";
        } else if (creditFormatCustomerTextView.isSelected()) {
            creditOrderBy = "customer_name_pinyin";
        }

        pageNumber = "0";

        queryOnCreditOrder(creditOrderBy, "2", "0", false);
    }

    /**
     * 已结清的搜索条件
     */
    private void queryByPaidFormat() {
        if (paidFormatMoneyTextView.isSelected()) {
            paidOrderBy = "total_price";
        } else if (paidFormatCustomerTextView.isSelected()) {
            paidOrderBy = "customer_name_pinyin";
        } else if (paidFormatTimeTextView.isSelected()) {
            paidOrderBy = "finish_time";
        }

        pageNumber = "0";

        startTime = "";
        queryPaidOrder(paidOrderBy, "1", startTime, descOrder, false);
    }

//    /**
//     * 赊账中的搜索条件-老版
//     */
//    private void initCreditPopupWindow() {
//        final View popupModelWindowView = LayoutInflater.from(getContext()).inflate(R.layout.popup_transaction_unpaid_model_select, null, false);
//
//        final ListView popupModelListView = (ListView) popupModelWindowView.findViewById(R.id.model_listView);
//
//        popupModelListView.setAdapter(transactionModelSelecteAdapter);
//        transactionModelSelecteAdapter.setTransactionModelArraylist(creditFormatModelList);
//
//        creditModelSelectPopup = new PopupWindow(popupModelWindowView, ViewGroup.LayoutParams.WRAP_CONTENT,
//                ViewGroup.LayoutParams.WRAP_CONTENT, true);
//
//        popupModelListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                String condition = creditFormatModelList.get(position);
//
//                if (condition.equals(creditFormatTextView.getText().toString())) {
//                    return;
//                }
//
//                creditFormatTextView.setText(condition);
//
//                creditModelSelectPopup.dismiss();
//                if (position == 0) {
//                    creditOrderBy = "sale_time";
//                } else if (position == 1) {
//                    creditOrderBy = "total_price";
//                } else if (position == 2) {
//                    creditOrderBy = "customer_name_pinyin";
//                }
//
//                queryOnCreditOrder(creditOrderBy, "2", "0", false);
//            }
//        });
//
//        creditModelSelectPopup.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.white)));
//        popupModelWindowView.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                if (popupModelWindowView != null && popupModelWindowView.isShown()) {
//                    creditModelSelectPopup.dismiss();
//                    creditModelSelectPopup = null;
//                }
//                return false;
//            }
//        });
//    }

//    /**
//     * 已结清的按时间搜索-老版
//     */
//    private void initPaidTimePopupWindow() {
//        final View popupModelWindowView = LayoutInflater.from(getContext()).
//                inflate(R.layout.popup_transaction_paid_time_model_select, null, false);
//
//        final ListView popupModelListView = (ListView) popupModelWindowView.findViewById(R.id.model_listView);
//
//        popupModelListView.setAdapter(transactionModelSelecteAdapter);
//        transactionModelSelecteAdapter.setTransactionModelArraylist(timeFormatModelList);
//
//        paidModelTimeSelectPopup = new PopupWindow(popupModelWindowView, ViewGroup.LayoutParams.WRAP_CONTENT,
//                ViewGroup.LayoutParams.WRAP_CONTENT, true);
//
//        popupModelListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                String condition = timeFormatModelList.get(position);
//
//                if (condition.equals(timeFormatTextView.getText().toString())) {
//                    return;
//                }
//
//                if (position == 0) {
//                    startTime = String.valueOf(WSTimeStamp.getSpecialDayStartTime(-7));
//                } else if (position == 1) {
//                    startTime = String.valueOf(WSTimeStamp.getSpecialDayStartTime(-30));
//                } else if (position == 2) {
//                    startTime = String.valueOf(WSTimeStamp.getSpecialDayStartTime(-90));
//                } else if (position == 3) {
//                    startTime = WSTimeStamp.getSpecialYearsStartTime(-1);
//                }
//
//                timeFormatTextView.setText(condition);
//                paidModelTimeSelectPopup.dismiss();
//
//                queryPaidOrder(paidOrderBy, "1", startTime, descOrder, false);
//            }
//        });
//
//        paidModelTimeSelectPopup.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.white)));
//        popupModelWindowView.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                if (popupModelWindowView != null && popupModelWindowView.isShown()) {
//                    paidModelTimeSelectPopup.dismiss();
//                    paidModelTimeSelectPopup = null;
//                }
//                return false;
//            }
//        });
//    }

//    /**
//     * 已结清的按条件搜索-老版
//     */
//    private void initPaidRefPopupWindow() {
//        final View popupModelWindowView = LayoutInflater.from(getContext()).
//                inflate(R.layout.popup_transaction_sort_condition_select, null, false);
//
//        final ListView popupModelListView = (ListView) popupModelWindowView.findViewById(R.id.model_listView);
//
//        popupModelListView.setAdapter(transactionModelSelecteAdapter);
//        transactionModelSelecteAdapter.setTransactionModelArraylist(paidOrderFormatList);
//
//        paidSortConditionSelectPopup = new PopupWindow(popupModelWindowView, ViewGroup.LayoutParams.WRAP_CONTENT,
//                ViewGroup.LayoutParams.WRAP_CONTENT, true);
//
//        popupModelListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                String condition = paidOrderFormatList.get(position);
//                if (condition.equals(paidOrderFormatTextView.getText().toString())) {
//                    return;
//                }
//
//                paidOrderFormatTextView.setText(condition);
//                paidSortConditionSelectPopup.dismiss();
//
//                if (position == 0) {
//                    paidOrderBy = "total_price";
//                } else if (position == 1) {
//                    paidOrderBy = "customer_name_pinyin";
//                } else if (position == 2) {
//                    paidOrderBy = "finish_time";
//                }
//                queryPaidOrder(paidOrderBy, "1", startTime, descOrder, false);
//            }
//        });
//
//        paidSortConditionSelectPopup.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.white)));
//        popupModelWindowView.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                if (popupModelWindowView != null && popupModelWindowView.isShown()) {
//                    paidSortConditionSelectPopup.dismiss();
//                    paidSortConditionSelectPopup = null;
//                }
//                return false;
//            }
//        });
//    }

    /**
     * 初始化数据
     */
    private void initData() {
        shopId = AccountManager.getInstance().getCurrentAccount().getCurrentShopId();
        // init order data list
//        try {
//            mOrderDataList = OrderDAO.getAllOrderItems(shopId);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }

        if (mOrderDataList == null) {
            mOrderDataList = new ArrayList<>();
        }

        if (mCartDataList == null) {
            mCartDataList = new ArrayList<>();
        }

        if (mCartGroupDataList == null) {
            mCartGroupDataList = new ArrayList<>();
        }

//        if (!mOrderDataList.isEmpty()){
//            Collections.sort(mOrderDataList, new WSComparator("mSaleTime", false));
//        }

        if (mOrdersAdapter != null) {
            mOrdersAdapter.setListData(mOrderDataList);
            mOrdersAdapter.notifyDataSetChanged();
        }

        if (mCartAdapter != null) {
            //mOrdersListView.addHeaderView(headerView);
            mCartAdapter.setArrayList(mCartGroupDataList);
            mCartAdapter.notifyDataSetChanged();
        }

        // init stock list data
        mStockDataList = StockDAO.getStockByShopId(shopId, StockDAO.STOCK_IN_STATUS);
        mUnconfirmStockDataList = StockDAO.getStockByShopId(shopId, StockDAO.STOCK_UNCONFIRM_STATUS);
        mUpdateStockDataList = new ArrayList<>();

        if (!mStockDataList.isEmpty()) {
            Collections.sort(mStockDataList, new WSComparator("mStockInTime", false));
        }

        if (mStockAdapter != null) {
            mStockAdapter.setListData(mStockDataList);
            mStockAdapter.notifyDataSetChanged();
        }
    }

    /**
     * 初始化ListView
     */
    public void initOrderListView() {
        mCartAdapter = new TransactionCartListAdapter(mCartGroupDataList, getActivity());
        mOrdersAdapter = new TransactionOrderListAdapter(mOrderDataList, getActivity());

        if (mCartTextView.isSelected()) {
            //mOrdersListView.addHeaderView(headerView);
            //mOrdersListView.setAdapter(mCartAdapter);
            switchToCart();
            queryCartGroup();
        } else {
            //mOrdersListView.setAdapter(mOrdersAdapter);
            if (mOrderBillTextView.isSelected()) {
                switchToBill();
                queryUnpaidOrder("0", "0", false);
            } else if (mOrderCreditTextView.isSelected()) {
                switchToCredit();
                queryOnCreditOrder(creditOrderBy, "2", "0", false);
            } else if (mOrderPaidTextView.isSelected()) {
                switchToPaid();
                queryPaidOrder(paidOrderBy, "1", startTime, descOrder, false);
            }
        }
    }

    public void refreshOrderListView() {
        // 去网络获取数据
//        refreshOrderData(shopId);
    }

    public void initStockListView() {
        mStockAdapter = new StockAdapter(mStockDataList, getActivity());
        mStockAdapter.setStatus(StockDAO.STOCK_IN_STATUS);

        if (mStockUnconfirmedTabTextView.isSelected()) {
            mStockAdapter.setListData(mUnconfirmStockDataList);
            mStockAdapter.setStatus(StockDAO.STOCK_UNCONFIRM_STATUS);
        }

        if (mStockAdjustmentTabTextView.isSelected()) {
            mStockAdapter.setListData(mUpdateStockDataList);
            mStockAdapter.setStatus(StockDAO.STOCK_UPDATE_STATUS);
        }
        mStockListView.setAdapter(mStockAdapter);
    }

    public void refreshStockListView() {
        // 去网络获取数据
        refreshStockInData(shopId);
    }

    public void refreshUnStockListView() {
        // 去网络获取数据
        refreshUnStockInData(shopId);
    }

    public void refreshUpdateStockListView() {
        // 7.30 this version not support
        mStockAdapter.setListData(mUpdateStockDataList);
        mStockAdapter.notifyDataSetChanged();
    }

    @Override
    public String getTitle() {
        return StringUtil.getString(R.string.dashboard_fragment_title_tab_transaction);
    }

    public static class StockViewHolder {
        BaseTextView mProductNameTextView;
        BaseTextView mNumberTextView;
        BaseTextView mUnitPriceTextView;
        BaseTextView mStockTimeTextView;
        BaseTextView mStockFinishTimeTextView;
//        BaseTextView mRetroTextView;
    }

    public static class StockAdapter extends BaseAdapter {
        private Context context;
        private ArrayList<StockItem> arrayList;

        private String status;

        public StockAdapter(ArrayList<StockItem> arrayList, Context context) {
            this.context = context;
            this.arrayList = arrayList;
        }

        public void setListData(ArrayList<StockItem> arrayList) {
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
            StockViewHolder holder = new StockViewHolder();
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.fragment_transaction_stock_listview_item, parent, false);
                holder.mProductNameTextView = (BaseTextView) convertView.findViewById(R.id.stock_listview_item_product_name_textView);
                holder.mNumberTextView = (BaseTextView) convertView.findViewById(R.id.stock_listview_item_product_number_textView);
                holder.mUnitPriceTextView = (BaseTextView) convertView.findViewById(R.id.stock_listview_item_unit_money_textView);
                holder.mStockTimeTextView = (BaseTextView) convertView.findViewById(R.id.stock_listview_item_stock_time_textView);
                holder.mStockFinishTimeTextView = (BaseTextView) convertView.findViewById(R.id.stock_listview_item_stock_finish_time_textView);
//                holder.mRetroTextView =  (BaseTextView) convertView.findViewById(R.id.stock_listview_retro);
                convertView.setTag(holder);
            } else {
                holder = (StockViewHolder) convertView.getTag();
            }

            StockItem stockItem = arrayList.get(position);
            holder.mProductNameTextView.setText(stockItem.mName);

            String numberUnit = stockItem.mCount + stockItem.getmUnit();
            holder.mNumberTextView.setText(numberUnit);

            String stockInTime = stockItem.mStockInTime + " " + stockItem.mStockInEmployeeName +
                    " " + context.getString(R.string.transaction_in_order);
            ;
            holder.mStockTimeTextView.setText(stockInTime);

            String unitPrice = "￥" + stockItem.mUnitPrice + "/" + stockItem.getmUnit();
            if (status.equals(StockDAO.STOCK_IN_STATUS)) {
                String stockFinishTime = stockItem.mStockFinishTime + " " + stockItem.mStockFinishEmployeeName;
                holder.mStockFinishTimeTextView.setVisibility(View.VISIBLE);
                String doneString = stockFinishTime + " " + context.getString(R.string.transaction_done);
                holder.mStockFinishTimeTextView.setText(doneString);
                holder.mUnitPriceTextView.setText(unitPrice);
                holder.mUnitPriceTextView.setTextColor(context.getResources().getColor(R.color.black));
            }

            if (status.equals(StockDAO.STOCK_UNCONFIRM_STATUS)) {
                holder.mStockFinishTimeTextView.setVisibility(View.GONE);
                holder.mUnitPriceTextView.setText(
                        context.getResources().getString(R.string.transaction_fragment_tab_stoke_retro));
                holder.mUnitPriceTextView.setTextColor(context.getResources().getColor(R.color.dark_red));
            }


            return convertView;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getStatus() {
            return status;
        }
    }

    private void refreshOrderData(final String shopId) {
// get last request time\
        String lastRequestTime = "";
        if (IncrementalDAO.isExistItem(Order.class, shopId)) {
            lastRequestTime = IncrementalDAO.queryIncrementalItem(Order.class, shopId);
        } else {
            IncrementalDAO.addIncrementalItem(Order.class, lastRequestTime, shopId);
        }

        // get transaction orders
        TransactionOrderRequest orderRequest = new TransactionOrderRequest(shopId, lastRequestTime);

        webClient.httpGetTransactionOrders(orderRequest, new NativeJsonResponseListener<JSONObject>() {
            @Override
            public void listener(JSONObject retJsonObject) {
                JSONArray jsonArray = retJsonObject.optJSONArray("total_order_list");

                if (jsonArray == null) {

                }
                Gson gson = new Gson();

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.optJSONObject(i);
                    if (jsonObject != null) {
                        String orderJson = jsonObject.toString();
                        Order order = gson.fromJson(orderJson, Order.class);
                        //TODO wait when API done
//                        String productJsonString = jsonObject.optString("sale_list");
//                        JSONArray productJsonArray = null;
//                        Order.Product[] products = null;
//
//                        try {
//                            productJsonArray = new JSONArray(productJsonString);
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//
//                        if (productJsonArray != null && productJsonArray.length() > 0){
//                            products = new Order.Product[productJsonArray.length()];
//
//                            for (int j = 0 ; j < productJsonArray.length(); j++){
//                                String jsonString = productJsonArray.optString(j);
//                                JsonElement mJson =  new JsonParser().parse(jsonString);
//                                Order.Product product = gson.fromJson(mJson, Order.Product.class);
//                                products[j] = product;
//                            }
//                        }

                        Order orderIndb = null;
                        try {
                            orderIndb = OrderDAO.queryOrderItem(shopId, order.getOrderNumber());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        if (orderIndb != null) {
                            OrderDAO.updateOrder(order);
                            mOrderDataList.remove(orderIndb);
                            mOrderDataList.add(order);
                        }

                        if (!mOrderDataList.contains(order)) {
                            mOrderDataList.add(order);
                            OrderDAO.addOrder(order);
                        }
                    }
                }

                IncrementalDAO.updateIncrementalItem(Order.class,
                        retJsonObject.optString("last_update_time", ""), shopId);
                if (retJsonObject.optBoolean("next_page")) {
                    refreshOrderData(shopId);
                } else {
                    Collections.sort(mOrderDataList, new WSComparator("mSaleTime", false));
                    mOrdersAdapter.setListData(mOrderDataList);
                    mOrdersAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void errorListener(String s) {
                LogHelper.getInstance().e(TAG, s);
            }
        });
    }


    // 更新入库记录
    private void refreshStockInData(final String shopId) {
        String lastRequestTime = "";
        if (IncrementalDAO.isExistItem(StockItem.class, shopId)) {
            lastRequestTime = IncrementalDAO.queryIncrementalItem(StockItem.class, shopId);
        } else {
            IncrementalDAO.addIncrementalItem(StockItem.class, lastRequestTime, shopId);
        }

        TransactionStockRequest stockRequest = new TransactionStockRequest(shopId, lastRequestTime);

        webClient.httpGetTransactionStock(stockRequest, new NativeJsonResponseListener<JSONObject>() {
            @Override
            public void listener(JSONObject retJsonObject) {
                JSONArray jsonArray = retJsonObject.optJSONArray("total_stokc_in_list");
                Gson gson = new Gson();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.optJSONObject(i);
                    if (jsonObject != null) {
                        String stockItemJson = jsonObject.toString();

                        StockItem stockItem = gson.fromJson(stockItemJson, StockItem.class);

                        String recordId = stockItem.mStockRecordId;
                        String productCode = stockItem.getmProductCode();

                        // remove and add
                        ArrayList<StockItem> arrayList = null;
                        try {
                            arrayList = StockDAO.queryStockItem(recordId, StockDAO.STOCK_IN_STATUS);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if (arrayList != null && arrayList.size() > 0) {
                            StockDAO.removeStockItem(recordId);
                            StockDAO.addStock(stockItem, StockDAO.STOCK_IN_STATUS);
                        } else {
                            StockDAO.addStock(stockItem, StockDAO.STOCK_IN_STATUS);
                        }

                        // 重新从数据库获取datalist for stock
                        mStockDataList = StockDAO.getStockByShopId(shopId, StockDAO.STOCK_IN_STATUS);
                    }
                }

                IncrementalDAO.updateIncrementalItem(StockItem.class,
                        retJsonObject.optString("last_update_time", ""), shopId);

                if (jsonArray.length() == 0) {
                    return;
                }

                if (retJsonObject.optBoolean("next_page")) {
                    refreshStockInData(shopId);
                } else {
                    Collections.sort(mStockDataList, new WSComparator("mStockInTime", false));
                    mStockAdapter.setStatus(StockDAO.STOCK_IN_STATUS);
                    mStockAdapter.setListData(mStockDataList);
                    mStockAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void errorListener(String s) {
                LogHelper.getInstance().e(TAG, s);
            }
        });
    }

    // 更新待确认入库记录
    private void refreshUnStockInData(final String shopId) {

        String lastRequestTime = "";
        if (IncrementalDAO.isExistItem(UnStockItem.class, shopId)) {
            lastRequestTime = IncrementalDAO.queryIncrementalItem(UnStockItem.class, shopId);
        } else {
            IncrementalDAO.addIncrementalItem(UnStockItem.class, lastRequestTime, shopId);
        }

        TransactionStockUnconfirmRequest unconfirmRequest = new TransactionStockUnconfirmRequest(shopId, lastRequestTime);

        webClient.httpGetTransactionUnconfirmStock(unconfirmRequest, new NativeJsonResponseListener<JSONObject>() {
            @Override
            public void listener(JSONObject retJsonObject) {
                JSONArray jsonArray = retJsonObject.optJSONArray("total_stokc_in_list");
                Gson gson = new Gson();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.optJSONObject(i);
                    if (jsonObject != null) {
                        String stockItemJson = jsonObject.toString();
                        StockItem stockItem = gson.fromJson(stockItemJson, StockItem.class);
                        stockItem.setStatus(StockDAO.STOCK_UNCONFIRM_STATUS);
                        String recordId = stockItem.mStockRecordId;

                        // remove and add
                        ArrayList<StockItem> arrayList = null;
                        try {
                            arrayList = StockDAO.queryStockItem(recordId, StockDAO.STOCK_UNCONFIRM_STATUS);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        if (arrayList != null && arrayList.size() > 0) {
                            StockDAO.removeStockItem(recordId);
                            StockDAO.addStock(stockItem, StockDAO.STOCK_UNCONFIRM_STATUS);
                        }

                        if (!mUnconfirmStockDataList.contains(stockItem)) {
                            StockDAO.addStock(stockItem, StockDAO.STOCK_UNCONFIRM_STATUS);
                            mUnconfirmStockDataList.add(stockItem);
                        }
                    }
                }

                IncrementalDAO.updateIncrementalItem(UnStockItem.class,
                        retJsonObject.optString("last_update_time", ""), shopId);

                if (jsonArray.length() == 0) {
                    return;
                }

                if (retJsonObject.optBoolean("next_page")) {
                    refreshUnStockInData(shopId);
                } else {
                    Collections.sort(mUnconfirmStockDataList, new WSComparator("mStockInTime", false));
                    mStockAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void errorListener(String s) {
                LogHelper.getInstance().e(TAG, s);
            }
        });
    }

    private void getDataFromServer() {

        // TODO 检查是不是以结清
//        refreshOrderData(shopId);
        // get transaction stock
        refreshStockInData(shopId);

        // to refresh in when view is shown
        // get unconfirm transaction stock
//        refreshUnStockInData(shopId);
        // 刷新订单数量
        //queryCountOrder();
    }


    private AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            if (parent.getId() == R.id.orders_listView) {
                LogHelper.getInstance().e(TAG, "orders_listView click");

                if (mCartTextView.isSelected()) {

                    Cart cart = mCartGroupDataList.get(position);
                    Intent intent1 = new Intent(getActivity(), PreTransactionActivity.class);
                    intent1.putExtra("cartUuid", cart.getCartUuid());
                    intent1.putExtra(PreTransactionActivity.INTENT_VALUE_CART_CUSTOMER_ID, cart.getCustomerId());
                    intent1.putExtra(PreTransactionActivity.INTENT_TYPE, PreTransactionActivity.INTENT_CART_TO_TRANSACTION);
                    startActivity(intent1);
                } else {
                    Order order = mOrderDataList.get(position);
                    //H5 页面需要展现Order的shopName,所以把shopName
                    // TODO 服务器没有返回,客户端 HACK
                    order.setShopName(AccountManager.getInstance().getCurrentAccount().getShopName());
                    // 打开订单详情

                    String str = order.getJsonString();
                    Intent intent = new Intent(getActivity(), OrderDetailActivity.class);
                    intent.putExtra(OrderDetailActivity.INTENT_VALUE_ORDER_DETAIL_JSON, order.getJsonString());
                    startActivity(intent);
                }
            } else if (parent.getId() == R.id.stock_history_listView) {
                LogHelper.getInstance().e(TAG, "stock_history_listView click");
                String status = mStockAdapter.getStatus();

                //入库列表点击事件
                if (status.equals(StockDAO.STOCK_IN_STATUS)) {
                    LogHelper.getInstance().e(TAG, "confirm click");
                    StockItem stockItem = mStockDataList.get(position);
                    LogHelper.getInstance().e(TAG, "confirm click");
                }

                //补登列表点击事件
                if (status.equals(StockDAO.STOCK_UNCONFIRM_STATUS)) {
                    LogHelper.getInstance().e(TAG, "unconfirm click");
                    StockItem stockItem = mUnconfirmStockDataList.get(position);
                    // 打开补登详情
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put(ProductStockInActivity.INTENT_VALUE_STOCK_IN_RECORD_ID, stockItem.getStockRecordId());
                        jsonObject.put(ProductStockInActivity.INTENT_VALUE_VENDOR, stockItem.getmName());
                        jsonObject.put(ProductStockInActivity.INTENT_VALUE_STANDARD_PRICE, stockItem.getmUnitPrice());
                        jsonObject.put(ProductStockInActivity.INTENT_VALUE_PRODUCT_NAME, stockItem.getmName());
                        jsonObject.put(ProductStockInActivity.INTENT_VALUE_PRODUCT_CODE, stockItem.getmProductCode());
                        jsonObject.put(ProductStockInActivity.INTENT_VALUE_NUMBER, stockItem.getmCount());

                        Intent intent = new Intent(getActivity(), ProductStockInActivity.class);
                        intent.putExtra(ProductStockInActivity.INTENT_KEY_JSON_DATA, jsonObject.toString());
                        startActivity(intent);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    };
}
