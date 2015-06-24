package com.mickledeals.activities;

import android.os.Bundle;

import com.mickledeals.R;
import com.mickledeals.adapters.VerticalPagerAdapter;
import com.mickledeals.views.VerticalViewPager;

/**
 * Created by Nicky on 5/17/2015.
 */
public abstract class SwipeDismissActivity extends BaseActivity {
    protected VerticalViewPager mViewPager;
    protected VerticalPagerAdapter mAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null && savedInstanceState.getBoolean("isKilled")) return;

        mViewPager = (VerticalViewPager) findViewById(R.id.verticalViewPager);
        mAdapter = new VerticalPagerAdapter(this, mViewPager);
        mViewPager.setPageTransformer(true, mAdapter);
        mViewPager.setAdapter(mAdapter);
        mViewPager.setCurrentItem(1, false);
        mViewPager.setOffscreenPageLimit(3);
    }

    //cannot override finish here because details

//    @Override
//    public void finish() {
//        super.finish();
//        overridePendingTransition(0, 0);
//    }
}
