package com.mickledeals.activities;

import android.os.Build;
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
import android.widget.ScrollView;
import android.widget.TextView;

import com.mickledeals.R;
import com.mickledeals.datamodel.BusinessPhoto;
import com.mickledeals.fragments.DetailsFragment;
import com.mickledeals.utils.DLog;
import com.mickledeals.utils.Utils;
import com.mickledeals.views.NotifyingScrollView;
import com.mickledeals.views.PagerIndicator;

import java.util.List;

/**
 * Created by Nicky on 12/27/2014.
 */
public class BusinessPhotosActivity extends SwipeDismissActivity  {

    private ViewPager mViewPager;
    private int mInitialIndex;
    private List<BusinessPhoto> mList;
    private PagerIndicator mIndicator;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null && savedInstanceState.getBoolean("isKilled")) return;

        if (Build.VERSION.SDK_INT >= 21) {
            postponeEnterTransition();
        }

        mInitialIndex = getIntent().getIntExtra("position", 0);
        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        DetailsPagerAdapter adapter = new DetailsPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(adapter);
        mViewPager.setOnPageChangeListener(adapter);
        mViewPager.setCurrentItem(mInitialIndex);

        mIndicator = (PagerIndicator) findViewById(R.id.pagerIndicator);
        mIndicator.setSize(mList.size());

    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_business_photos;
    }


    public class DetailsPagerAdapter extends FragmentPagerAdapter implements ViewPager.OnPageChangeListener {

        public DetailsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return mList.size();
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
            bundle.putInt("position", position);
            bundle.putInt("listType", mListType);
            int stringRes = Utils.getStringResFromType(mListType);
            if (stringRes != 0) bundle.putString("navMidText", getString(stringRes) + " " + (position + 1) + "/" + mList.size());
            if (position > 0) bundle.putString("navLeftText", position + " " + getString(R.string.nav_text_more));
            if (position < mList.size() - 1) bundle.putString("navRightText", (mList.size() - position - 1) + " " + getString(R.string.nav_text_more));
            fragment.setArguments(bundle);
            return fragment;
        }

        @Override
        public void onPageSelected(int position) {
            if (mIndicator != null) {
                mIndicator.setPosition(position);
                mIndicator.invalidate();
            }
        }

        @Override
        public void onPageScrollStateChanged(int i) {
        }

        @Override
        public void onPageScrolled(int i, float v, int i2) {
        }
    }



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
