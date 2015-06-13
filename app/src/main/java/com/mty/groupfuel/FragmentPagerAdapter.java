package com.mty.groupfuel;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.SparseArray;
import android.view.ViewGroup;

public class FragmentPagerAdapter extends android.support.v4.app.FragmentPagerAdapter{
    SparseArray<Fragment> registeredFragments = new SparseArray<>(3);
    private int tabCount;
    private String tabTitles[];

    public FragmentPagerAdapter(FragmentManager fm, int tabCount, String[] tabTitles) {
        super(fm);
        this.tabCount = tabCount;
        this.tabTitles = tabTitles;
    }

    @Override
    public int getCount() {
        return tabCount;
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
