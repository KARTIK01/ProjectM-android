package com.mickledeals.adapters;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mickledeals.R;
import com.mickledeals.utils.DLog;
import com.mickledeals.views.PagerIndicator;

/**
 * Created by Nicky on 12/26/2014.
 */


public class BusinessPhotoSliderAdapter extends FragmentPagerAdapter implements
        ViewPager.OnPageChangeListener {

    private static final long REFRESH_PERIOD = 5000;
//    private List<TestDataHolder> mList;
    private PagerIndicator mIndicator;
    private ViewPager mViewPager;
//    private Handler mHandler;

    public BusinessPhotoSliderAdapter(FragmentManager fm, PagerIndicator indicator, ViewPager viewPager) {
        super(fm);
//        mList = DataListModel.getInstance().getFeatureSliderCouponList();
        mIndicator = indicator;
        mIndicator.setSize(4);
        mViewPager = viewPager;
//        mHandler = new Handler();
//        mHandler.removeCallbacks(mSlidePageThread);
//        mHandler.postDelayed(mSlidePageThread, REFRESH_PERIOD);
    }

    @Override
    public Fragment getItem(int position) {

        DLog.d(this, "Featured Fragment getItem()");
        Fragment fragment = new SlidePageFragment();
        Bundle args = new Bundle();
        int imageResId = 0;
        int photoStringResId = 0;
        if (position == 0) {
            imageResId = R.drawable.pic_business_1;
            photoStringResId = R.string.business_photo1;
        } else if (position == 1) {
            imageResId = R.drawable.pic_business_2;
            photoStringResId = R.string.business_photo2;
        } else if (position == 2) {
            imageResId = R.drawable.pic_business_3;
            photoStringResId = R.string.business_photo3;
        } else if (position == 3) {
            imageResId = R.drawable.pic_business_4;
            photoStringResId = R.string.business_photo4;
        }

        args.putInt("imageResId", imageResId);
        args.putInt("photoDescription", photoStringResId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public int getCount() {
        return 4;
    }

    @Override
    public void onPageScrollStateChanged(int state) {
//        if (state == ViewPager.SCROLL_STATE_IDLE) {
//            mHandler.removeCallbacks(mSlidePageThread);
//            mHandler.postDelayed(mSlidePageThread, REFRESH_PERIOD);
//        } else if (state == ViewPager.SCROLL_STATE_DRAGGING) {
//            mHandler.removeCallbacks(mSlidePageThread);
//        }
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

//    private Runnable mSlidePageThread = new Runnable() {
//        @Override
//        public void run() {
//            int cur = mViewPager.getCurrentItem();
//            mViewPager.setCurrentItem(cur == getCount() - 1 ? 0 : cur + 1, true);
//            mHandler.postDelayed(this, REFRESH_PERIOD);
//        }
//    };

    public static class SlidePageFragment extends Fragment {

        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

            DLog.d(this, "onCreateView");
            ViewGroup rootView = (ViewGroup) inflater.inflate(
                    R.layout.business_photo_slide_page, container, false);
            ImageView imageView = (ImageView) rootView.findViewById(R.id.slider_image);
            imageView.setImageResource(getArguments().getInt("imageResId"));
            TextView description = (TextView) rootView.findViewById(R.id.slider_text);
            description.setText(getString(getArguments().getInt("photoDescription")));
            return rootView;

        }
    }
}

