package com.honeywell.wholesale.ui.report;


import android.support.v4.app.Fragment;

/**
 * Created by xiaofei on 9/22/16.
 *
 */
public abstract class BaseFragment extends Fragment {
    public abstract void setDays(int days);
    public abstract void setMonths(int months);
    public abstract void setYears(int years);

    public abstract void setCondition(String condition);

    // set chart show or list show?
    public abstract void toggle();

    // check for toggle button
    public abstract boolean isShownChart();
}
