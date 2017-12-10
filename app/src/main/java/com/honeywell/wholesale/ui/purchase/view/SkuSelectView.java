package com.honeywell.wholesale.ui.purchase.view;

import com.honeywell.wholesale.framework.model.SKU;
import com.honeywell.wholesale.ui.base.WholesaleBaseView;

import java.util.ArrayList;

/**
 * Created by liuyang on 2017/7/5.
 */

public interface SkuSelectView extends WholesaleBaseView{

    public void addSkuView();

    public void setStockText(String totalQuantity);

    public void setSkuListData(ArrayList<SKU> skuList);
}
