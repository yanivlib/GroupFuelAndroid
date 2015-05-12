package com.mty.groupfuel;


import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.mty.groupfuel.datamodel.Car;

public class FragmentPagerAdapter extends android.support.v4.app.FragmentPagerAdapter{
    final int PAGE_COUNT = 3;
    private String tabTitles[] = new String[] { "My Usage", "Fuel Now", "My Account" };
    private Context context;

    public FragmentPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 1:
                return FuelingFragment.newInstance();
            default:
                return MainFragment.newInstance(position + 1);
        }
    }

    @Override
    public CharSequence getPageTitle(int position) {
        // Generate title based on item position
        return tabTitles[position];
    }
}
