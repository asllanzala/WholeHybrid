package com.honeywell.wholesale.framework.model;

/**
 * Created by zhujunyu on 2017/4/24.
 */

public class ListItem {
    private String key;
    private String value;
    private boolean flag;



    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public boolean isFlag() {
        return flag;
    }
}
