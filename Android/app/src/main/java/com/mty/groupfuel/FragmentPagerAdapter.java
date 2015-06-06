package com.mty.groupfuel;


import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.SparseArray;
import android.view.ViewGroup;

import com.mty.groupfuel.datamodel.Car;

public class FragmentPagerAdapter extends android.support.v4.app.FragmentPagerAdapter{
    final int PAGE_COUNT = 3;
    private String tabTitles[];
    private Context context;
    SparseArray<Fragment> registeredFragments = new SparseArray<>(3);

    public FragmentPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
        tabTitles = new String[PAGE_COUNT];
        tabTitles[0] = context.getString(R.string.usage_title);
        tabTitles[1] = context.getString(R.string.fueling_title);
        tabTitles[2] = context.getString(R.string.settings_title);
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return UsageFragment.newInstance();
            case 1:
                return FuelingFragment.newInstance();
            case 2:
                return SettingsFragment.newInstance();
            default:
                return MainFragment.newInstance(position + 1);
        }
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitles[position];
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Fragment fragment = (Fragment) super.instantiateItem(container, position);
        registeredFragments.put(position, fragment);
        return fragment;
    }
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        registeredFragments.remove(position);
        super.destroyItem(container, position, object);
    }

    public Fragment getRegisteredFragment(int position) {
        return registeredFragments.get(position);
    }
}
