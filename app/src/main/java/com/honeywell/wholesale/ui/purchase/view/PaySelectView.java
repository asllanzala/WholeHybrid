package com.honeywell.wholesale.ui.purchase.view;

import com.honeywell.wholesale.ui.base.WholesaleBaseView;

/**
 * Created by liuyang on 2017/7/3.
 */

public interface PaySelectView extends WholesaleBaseView {
    public void onSetButtonEnabled(boolean flag);
    public void onShowPayment();

}
