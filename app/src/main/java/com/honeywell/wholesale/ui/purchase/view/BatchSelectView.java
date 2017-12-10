package com.honeywell.wholesale.ui.purchase.view;

import com.google.android.gms.common.api.Batch;
import com.honeywell.wholesale.framework.model.BatchModel;
import com.honeywell.wholesale.framework.model.ExtraCost;
import com.honeywell.wholesale.ui.base.WholesaleBaseView;

import java.util.ArrayList;

/**
 * Created by liuyang on 2017/7/5.
 */

public interface BatchSelectView extends WholesaleBaseView {
    public void setListData(String currentDay, ArrayList<BatchModel> arrayList);
}
