package com.honeywell.wholesale.ui.purchase.view;

import com.honeywell.wholesale.framework.model.Inventory;
import com.honeywell.wholesale.ui.base.WholesaleBaseView;

import java.util.ArrayList;

/**
 * Created by liuyang on 2017/7/5.
 */

public interface ProductSelectView extends WholesaleBaseView {
    public void setAdapterDataSource(ArrayList<Inventory> arrayList);

    public void setResultCountText(String value);

    public void setLoadMore(String loadMore);

    public String getLoadMore();

    public void setArrayListData(ArrayList<Inventory> arrayList);
}
