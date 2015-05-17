package com.mickledeals.adapters;

import android.app.Activity;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mickledeals.R;
import com.mickledeals.datamodel.DataListModel;
import com.mickledeals.tests.TestDataHolder;
import com.mickledeals.utils.Constants;
import com.mickledeals.utils.DLog;
import com.mickledeals.utils.Utils;
import com.mickledeals.views.PagerIndicator;

import java.util.List;

/**
 * Created by Nicky on 12/26/2014.
 */


public class FeatureSliderAdapter extends PagerAdapter implements
        ViewPager.OnPageChangeListener {

    private static final long REFRESH_PERIOD = 5000;
    private List<TestDataHolder> mList;
    private PagerIndicator mIndicator;
    private ViewPager mViewPager;
    private Handler mHandler;
    private Activity mActivity;

    public FeatureSliderAdapter(Activity activity, PagerIndicator indicator, ViewPager viewPager) {
        mActivity = activity;
        mList = DataListModel.getInstance().getFeatureSliderCouponList();
        mIndicator = indicator;
        mIndicator.setSize(mList.size());
        mViewPager = viewPager;
        mHandler = new Handler();
        mHandler.removeCallbacks(mSlidePageThread);
        mHandler.postDelayed(mSlidePageThread, REFRESH_PERIOD);
    }

    public void stopAutoSliding() {
        mHandler.removeCallbacks(mSlidePageThread);
    }

    public void startAutoSliding() {
        mHandler.removeCallbacks(mSlidePageThread);
        mHandler.postDelayed(mSlidePageThread, REFRESH_PERIOD);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((View) object);
    }


    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        DLog.d(this, "onCreateView");
        ViewGroup rootView = (ViewGroup) mActivity.getLayoutInflater().inflate(
                R.layout.fragment_feature_slide_page, null);
        final ImageView imageView = (ImageView) rootView.findViewById(R.id.slider_image);
        imageView.setImageResource(mList.get(position).mImageResId);
        TextView description = (TextView) rootView.findViewById(R.id.slider_text);
        description.setText(mList.get(position).getStoreName() + " - " + mList.get(position).getDescription());
        rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String transition = "cardImage" + mList.get(position).mId;
                Utils.transitDetailsActivity(mActivity, position, Constants.TYPE_FEATURE_SLIDER_LIST, null, transition);
            }
        });
        ((ViewPager) container).addView(rootView, 0);
        return rootView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ((ViewPager) container).removeView((View) object);
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        if (state == ViewPager.SCROLL_STATE_IDLE) {
            mHandler.removeCallbacks(mSlidePageThread);
            mHandler.postDelayed(mSlidePageThread, REFRESH_PERIOD);
        } else if (state == ViewPager.SCROLL_STATE_DRAGGING) {
            mHandler.removeCallbacks(mSlidePageThread);
        }
    }

    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {
    }

    @Override
    public void onPageSelected(int position) {
        if (mIndicator != null) {
            mIndicator.setPosition(position);
            mIndicator.invalidate();
        }
    }

    private Runnable mSlidePageThread = new Runnable() {
        @Override
        public void run() {
            int cur = mViewPager.getCurrentItem();
            mViewPager.setCurrentItem(cur == getCount() - 1 ? 0 : cur + 1, true);
            mHandler.postDelayed(this, REFRESH_PERIOD);
        }
    };
}

