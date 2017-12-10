package com.honeywell.wholesale.framework.search;

import java.util.ArrayList;

/**
 * Created by xiaofei on 9/1/16.
 *
 */
public interface ApiSearch<T> {
    //    void beforeSearch();
    void onSearchResultListener(ArrayList arrayList);
    ArrayList<T> queryFromLocal(String s);
}
