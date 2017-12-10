package com.honeywell.wholesale.ui.purchase.view;

import com.honeywell.wholesale.framework.model.ExtraCost;
import com.honeywell.wholesale.ui.base.WholesaleBaseView;

import java.util.ArrayList;

/**
 * Created by liuyang on 2017/7/5.
 */

public interface ExtraCostView extends WholesaleBaseView {
    public void setListData(ArrayList<ExtraCost> arrayList);
}
