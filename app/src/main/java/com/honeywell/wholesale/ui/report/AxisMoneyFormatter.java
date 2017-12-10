package com.honeywell.wholesale.ui.report;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.AxisValueFormatter;

import java.text.DecimalFormat;

/**
 * Created by xiaofei on 9/27/16.
 *
 */

public class AxisMoneyFormatter implements AxisValueFormatter {

    private DecimalFormat mFormat;

    public AxisMoneyFormatter() {
        mFormat = new DecimalFormat("###,###,###,##0");
    }
    @Override
    public String getFormattedValue(float value, AxisBase axis) {
        return "￥" + mFormat.format(value);
    }

    @Override
    public int getDecimalDigits() {
        return 1;
    }
}
