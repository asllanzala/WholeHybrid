package com.honeywell.wholesale.ui.dashboard.view;

import android.support.v4.app.Fragment;

/**
 * Created by xiaofei on 12/28/16.
 *
 */

public class TabItem {

    public int imageResId;
    public int lableResId;

    public Class<? extends Fragment> tagFragmentClz;

    public TabItem(int imageResId, int lableResId) {
        this.imageResId = imageResId;
        this.lableResId = lableResId;
    }

    public TabItem(int imageResId, int lableResId, Class<? extends Fragment> tagFragmentClz) {
        this.imageResId = imageResId;
        this.lableResId = lableResId;
        this.tagFragmentClz = tagFragmentClz;
    }
}
