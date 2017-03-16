package com.netsun.labuy.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/3/8.
 */

public class MyFragmentPageAdapter extends FragmentPagerAdapter {
    ArrayList<Fragment> fragments;
    private String[] titles = {"商品", "详情"};
    public MyFragmentPageAdapter(FragmentManager manager, ArrayList<Fragment> list) {
        super(manager);
        fragments = list;
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titles[position];
    }
}
