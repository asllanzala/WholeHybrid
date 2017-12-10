package com.honeywell.wholesale.ui.purchase.view;

import com.honeywell.wholesale.framework.model.PreCart;
import com.honeywell.wholesale.framework.model.SKU;
import com.honeywell.wholesale.framework.model.WareHouse;
import com.honeywell.wholesale.ui.base.WholesaleBaseView;

import java.util.ArrayList;

/**
 * Created by liuyang on 2017/7/5.
 */

public interface OrderView extends WholesaleBaseView {

    public void addProductItemController(final PreCart preCart, final SKU sku);

    public void judgeButtonEnable();

    public void setWarehouseText(String warehouseName);

    public void setAddProductLayoutEnabled(boolean flag);

    public void setWarehouseListData(ArrayList<WareHouse> wareHouseArrayList);

    public void setCurrentWarehouse(WareHouse currentWarehouse);
}
