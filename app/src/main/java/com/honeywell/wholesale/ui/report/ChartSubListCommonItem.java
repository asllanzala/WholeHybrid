package com.honeywell.wholesale.ui.report;

/**
 * Created by xiaofei on 9/23/16.
 *
 */
public class ChartSubListCommonItem {
    String title;
    String value;

    public ChartSubListCommonItem(String title, String value) {
        this.title = title;
        this.value = value;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
