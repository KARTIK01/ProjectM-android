package com.mickledeals.adapters;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.mickledeals.views.VerticalViewPager;

/**
 * Created by Nicky on 5/14/2015.
 */
public class VerticalPagerAdapter extends PagerAdapter implements
        VerticalViewPager.PageTransformer {
    private Activity mActivity;
    private VerticalViewPager mPager;
    private Drawable mBg;

    public float getPageHeight(int position) {
        return 1f;
    }
    public VerticalPagerAdapter(Activity a, VerticalViewPager pager) {
        mActivity = a;
        mPager = pager;
        mBg = mPager.getBackground().mutate();
    }
    public int getCount() {
        return 3;
    }
    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        return container.getChildAt(position);
    }
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ((VerticalViewPager) container).removeView((View) object);
    }
    @Override
    public void transformPage(View view, float position) {
        if (position >= 2 || position <= -2) {
            mActivity.finish();
            mActivity.overridePendingTransition(0, 0);
            return;
        }
        if (view == mPager.getChildAt(1)) return;

//        if (position >= 0 && position <= 1) view.setAlpha(position);
//        else if (position <= 0 && position >= -1) view.setAlpha(-position);
        if (position >= 0 && position <= 1) {
            mBg.setAlpha((int)(position * 255));
            mPager.getChildAt(1).setAlpha(position * 2);
        }
        else if (position <= 0 && position >= -1) {
            mBg.setAlpha(-(int)(position * 255));
            mPager.getChildAt(1).setAlpha(-position * 2);
        }
    }
}