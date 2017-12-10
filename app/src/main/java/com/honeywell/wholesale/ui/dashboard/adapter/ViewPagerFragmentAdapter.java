package com.honeywell.wholesale.ui.dashboard.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.honeywell.wholesale.ui.dashboard.view.TabItem;

import java.util.ArrayList;

/**
 * Created by xiaofei on 12/28/16.
 *
 */

public class ViewPagerFragmentAdapter extends FragmentPagerAdapter {
    private ArrayList<TabItem> tabs;
    private ArrayList<Fragment> fragments;

    public ViewPagerFragmentAdapter(FragmentManager fm, ArrayList<TabItem> arrayList) {
        super(fm);
        tabs = arrayList;
        fragments = new ArrayList<>();

        for (TabItem item: tabs){
            try{
                Fragment fragment = item.tagFragmentClz.newInstance();
                fragments.add(fragment);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return tabs.size();
    }
}
