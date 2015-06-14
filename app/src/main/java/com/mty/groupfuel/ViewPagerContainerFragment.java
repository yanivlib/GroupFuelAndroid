package com.mty.groupfuel;


import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ViewPagerContainerFragment extends Fragment {

    private ViewPager pager;
    private SlidingTabLayout tabs;
    private FragmentPagerAdapter adapter;
    private AppCompatActivity context;

    public ViewPagerContainerFragment() {   }

    @Override
    public void onAttach(Activity activity) {
        context=(AppCompatActivity) activity;
        super.onAttach(activity);
    }

    private void getViewsById(View view) {
        pager = (ViewPager) view.findViewById(R.id.viewpager);
        tabs = (SlidingTabLayout) view.findViewById(R.id.sliding_tabs);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_view_pager_container, container, false);

        getViewsById(root);

        final int tabCount = 2;
        String[] tabTitles = new String[]{ getString(R.string.usage_title), "Fuel Log"};
        adapter = new FragmentPagerAdapter(context.getSupportFragmentManager(), tabCount, tabTitles);

        pager.setAdapter(adapter);

        tabs.setDistributeEvenly(true);
        tabs.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return getResources().getColor(R.color.accent);
            }
        });
        tabs.setViewPager(pager);

       /* String action = getIntent().getAction();
        if (action != null) {
            if (action.equals(Consts.OPEN_TAB_SETTINGS)) {
                pager.setCurrentItem(2);
            } else if (action.equals(Consts.OPEN_TAB_USAGE)) {
                pager.setCurrentItem(0);
            }
        }*/
        return root;
    }
    private class FragmentPagerAdapter extends android.support.v4.app.FragmentPagerAdapter{
        private int tabCount;
        private SparseArray<Fragment> registeredFragments;
        private String tabTitles[];

        public FragmentPagerAdapter(FragmentManager fm, int tabCount, String[] tabTitles) {
            super(fm);
            this.tabCount = tabCount;
            this.tabTitles = tabTitles;
            this.registeredFragments  = new SparseArray<>(tabCount);
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
                    return FuelLogFragment.newInstance();
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

}