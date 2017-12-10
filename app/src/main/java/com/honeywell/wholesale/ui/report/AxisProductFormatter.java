package com.honeywell.wholesale.ui.report;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.AxisValueFormatter;
import com.honeywell.wholesale.lib.util.StringUtil;

import java.util.ArrayList;

/**
 * Created by xiaofei on 9/27/16.
 *
 */

public class AxisProductFormatter implements AxisValueFormatter {

    protected ArrayList<String> productList;

    public AxisProductFormatter(ArrayList<String> productList) {
        this.productList = productList;
    }

    @Override
    public String getFormattedValue(float value, AxisBase axis) {
        int position = (int)value;
        if (position >= productList.size() || position < 0){
            return "-";
        }
        String s = productList.get(position);

        if (s.getBytes().length > 16 && StringUtil.isChinese(s)){
            s = s.substring(0, 4);
            return s;
        }

        if (s.length() > 8){
            s = s.substring(0, 8);
            return s;
        }

        return s;
    }

    @Override
    public int getDecimalDigits() {
        return 0;
    }
}
