package com.honeywell.wholesale.framework.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by liuyang on 2017/7/6.
 */

public class ExtraCost {

    @SerializedName("flow_type_id")
    private String extraCostId;

    @SerializedName("remark")
    private String extraCostName;

    @SerializedName("premium_price")
    private String premiumPrice;

    public ExtraCost() {
    }

    public ExtraCost(String extraCostId, String extraCostName) {
        this.extraCostId = extraCostId;
        this.extraCostName = extraCostName;
    }

    public String getPremiumPrice() {
        return premiumPrice;
    }

    public void setPremiumPrice(String premiumPrice) {
        this.premiumPrice = premiumPrice;
    }

    public String getExtraCostId() {
        return extraCostId;
    }

    public void setExtraCostId(String extraCostId) {
        this.extraCostId = extraCostId;
    }

    public String getExtraCostName() {
        return extraCostName;
    }

    public void setExtraCostName(String extraCostName) {
        this.extraCostName = extraCostName;
    }
}
