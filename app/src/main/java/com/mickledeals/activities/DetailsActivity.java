package com.mickledeals.activities;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ScrollView;

import com.mickledeals.R;
import com.mickledeals.fragments.DetailsFragment;
import com.mickledeals.tests.TestDataHolder;
import com.mickledeals.utils.Utils;
import com.mickledeals.views.NotifyingScrollView;

import java.util.List;

/**
 * Created by Nicky on 12/27/2014.
 */
public class DetailsActivity extends SwipeDismissActivity  {

    private ViewPager mDetailsViewPager;
    private int mListType;
    private int mInitialIndex;
    private List<TestDataHolder> mList;
    private View mShadow;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null && savedInstanceState.getBoolean("isKilled")) return;

        if (Build.VERSION.SDK_INT >= 21) {
            postponeEnterTransition();
        }

        mInitialIndex = getIntent().getIntExtra("listIndex", 0);
        mListType = getIntent().getIntExtra("listType", 0);
        mList = Utils.getListFromType(mListType);
        mDetailsViewPager = (ViewPager) findViewById(R.id.viewpager);
        mShadow = findViewById(R.id.toolbarShadow);
        DetailsPagerAdapter adapter = new DetailsPagerAdapter(getSupportFragmentManager());
        mDetailsViewPager.setAdapter(adapter);
        mDetailsViewPager.setOnPageChangeListener(adapter);
        mDetailsViewPager.setCurrentItem(mInitialIndex);
        mDetailsViewPager.setPageMargin(Utils.getPixelsFromDip(30f, getResources()));
        mDetailsViewPager.setPageTransformer(false, adapter);

//        mDetailsViewPager.setPageMarginDrawable(R.drawable.water_mark_bg);
//        mDetailsViewPager.setPageMarginDrawable(R.color.transparentViewPagerDivider);
        getSupportActionBar().setTitle(mList.get(mInitialIndex).getStoreName());
        setToolBarTransparency(0);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.details, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_search) {
//            return true;
//        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_details;
    }




    public class DetailsPagerAdapter extends FragmentStatePagerAdapter implements ViewPager.OnPageChangeListener, ViewPager.PageTransformer {

        int prevPosition = -1;

        public DetailsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return mList.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mList.get(position).getStoreName();
        }

        @Override
        public Fragment getItem(int position) {
            DetailsFragment fragment = new DetailsFragment();
            if (position == 0) fragment.showNavPanelHint();
            fragment.setOnScrollChangeListener(new NotifyingScrollView.OnScrollChangedListener() {
                @Override
                public void onScrollChanged(ScrollView who, int l, int t, int oldl, int oldt) {
                    setToolBarTransparency(t);
                }
            });
            Bundle bundle = new Bundle();
            bundle.putInt("storeId", mList.get(position).mId);
            bundle.putInt("listType", mListType);
            int stringRes = Utils.getStringResFromType(mListType);
            if (stringRes != 0) bundle.putString("navMidText", getString(stringRes) + " " + (position + 1) + "/" + mList.size());
            if (position > 0) bundle.putString("navLeftText", (position) + "/" + mList.size());
            if (position < mList.size() - 1) bundle.putString("navRightText", (position + 2) + "/" + mList.size());
            fragment.setArguments(bundle);
            return fragment;
        }

        @Override
        public void onPageSelected(int position) {
            //this wont get called when position is 0
            if (prevPosition != -1) {
                DetailsFragment oldFragment = (DetailsFragment) instantiateItem(mDetailsViewPager, prevPosition);
                if (oldFragment != null) {
                    oldFragment.resetNavPanelHint();
                }
            }
            DetailsFragment fragment = (DetailsFragment) instantiateItem(mDetailsViewPager, position);
            int scrollPos = 0;
            if (fragment != null) {
                fragment.showNavPanelHint();
                scrollPos = fragment.getScrollYPosition();
            }
            setToolBarTransparency(scrollPos);

            getSupportActionBar().setTitle(mList.get(position).getStoreName());
            prevPosition = position;
        }

        @Override
        public void onPageScrollStateChanged(int i) {
        }

        @Override
        public void onPageScrolled(int i, float v, int i2) {
        }

        public void transformPage(View view, float position) {

            if (position >= 0 && position <= 1) view.setAlpha(2 - position * 2);
            else if (position <= 0 && position >= -1) view.setAlpha(2 + position * 2);
        }
    }

    private void setToolBarTransparency(int scrollPos) {
        final int headerHeight = Utils.getDeviceWidth(DetailsActivity.this) * 9 / 16;
        final float ratio = (float) Math.min(Math.max(scrollPos, 0), headerHeight) / headerHeight;
        final int newAlpha = (int) (ratio * 255);
        mToolBar.getBackground().mutate().setAlpha(newAlpha);
        mToolBar.setTitleTextColor(Color.argb(newAlpha, 255, 255, 255));
        mShadow.setAlpha(ratio);
    }

    @Override
    public void onBackPressed() {
        //super method will apply shared element transition
        if (Build.VERSION.SDK_INT < 21 || mDetailsViewPager.getCurrentItem() == mInitialIndex) super.onBackPressed();
        else finish();
    }
}
