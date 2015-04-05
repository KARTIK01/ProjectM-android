package com.mickledeals.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.mickledeals.R;
import com.mickledeals.utils.DLog;
import com.mickledeals.views.SlidingTabLayout;

/**
 * Created by Nicky on 11/28/2014.
 */
public class HomeFragment extends BaseFragment {

    private static final int NUM_TABS = 2;
    private SlidingTabLayout mSlidingTabLayout;
    private ViewPager mViewPager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        DLog.d(this, "onCreateView");
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.fragment_home, container, false);

        return rootView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        DLog.d(this, "onDestroy");
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mViewPager = (ViewPager) view.findViewById(R.id.viewpager);
        mViewPager.setOffscreenPageLimit(NUM_TABS);
        mViewPager.setAdapter(new HomePagerAdapter(getActivity().getSupportFragmentManager()));
        mViewPager.setCurrentItem(1);
        mSlidingTabLayout = (SlidingTabLayout) getToolBar().findViewById(R.id.sliding_tabs);
        mSlidingTabLayout.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return Color.WHITE;
            }

            @Override
            public int getDividerColor(int position) {
                return Color.parseColor("#99FFFFFF");
            }
        });
        mSlidingTabLayout.setViewPager(mViewPager);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Indicate that this fragment would like to influence the set of actions in the action bar.
        setHasOptionsMenu(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

//        if (item.getItemId() == R.id.action_example) {
//            Toast.makeText(mContext, "Example action.", Toast.LENGTH_SHORT).show();
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }

    private void showGlobalContextActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(R.string.app_name);
    }

    private ActionBar getSupportActionBar() {
        return ((ActionBarActivity) getActivity()).getSupportActionBar();
    }

    public class HomePagerAdapter extends FragmentStatePagerAdapter {
        public HomePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return NUM_TABS;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            if (position == 0) return HomeFragment.this.getString(R.string.nearby_tab_text);
            else if (position == 1) return HomeFragment.this.getString(R.string.featured_tab_text);
            else throw new IllegalArgumentException("Invalid position");
        }

        @Override
        public Fragment getItem(int position) {
            DLog.d(this, "Home Pager getItem()");
            if (position == 0) return new NearbyFragment();
            else if (position == 1) return new FeaturedFragment();
            else throw new IllegalArgumentException("Invalid position");
        }
    }

}
