package com.mickledeals.adapters;

import android.app.Activity;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.mickledeals.R;
import com.mickledeals.datamodel.CouponInfo;
import com.mickledeals.datamodel.DataListModel;
import com.mickledeals.utils.Constants;
import com.mickledeals.utils.DLog;
import com.mickledeals.utils.MDApiManager;
import com.mickledeals.utils.Utils;
import com.mickledeals.views.PagerIndicator;

import java.util.List;

/**
 * Created by Nicky on 12/26/2014.
 */


public class FeatureSliderAdapter extends PagerAdapter implements
        ViewPager.OnPageChangeListener {

    private static final long REFRESH_PERIOD = 5000;
    private List<Integer> mList;
    private PagerIndicator mIndicator;
    private ViewPager mViewPager;
    private Handler mHandler;
    private Activity mActivity;

    public FeatureSliderAdapter(Activity activity, PagerIndicator indicator, ViewPager viewPager) {
        mActivity = activity;
        mList = DataListModel.getInstance().getFeatureSliderCouponList();
        mIndicator = indicator;
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

        CouponInfo info = DataListModel.getInstance().getCouponInfoFromList(mList, position);

        ViewGroup rootView = (ViewGroup) mActivity.getLayoutInflater().inflate(
                R.layout.fragment_feature_slide_page, null);
        final NetworkImageView imageView = (NetworkImageView) rootView.findViewById(R.id.slider_image);
        imageView.setNoAnimation();
        imageView.setImageUrl(info.mCoverPhotoUrl, MDApiManager.sImageLoader);
        TextView description = (TextView) rootView.findViewById(R.id.slider_text);
        description.setText(info.mBusinessInfo.getStoreName() + " - " + info.getDescription());
        rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String transition = "cardImage" + mList.get(position);
                DataListModel.sTransitBitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
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
        if (mIndicator != null) {
            mIndicator.setSize(mList.size());
            mIndicator.setVisibility(View.GONE);
            mIndicator.setVisibility(View.VISIBLE);
        }
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

