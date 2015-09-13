package com.mickledeals.adapters;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityOptionsCompat;
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
import com.mickledeals.activities.BusinessPhotosActivity;
import com.mickledeals.datamodel.BusinessPhoto;
import com.mickledeals.utils.DLog;
import com.mickledeals.views.PagerIndicator;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nicky on 12/26/2014.
 */


public class BusinessPhotoSliderAdapter extends FragmentPagerAdapter implements
        ViewPager.OnPageChangeListener {

    private ArrayList<BusinessPhoto> mList;
    private PagerIndicator mIndicator;

    public BusinessPhotoSliderAdapter(FragmentManager fm, PagerIndicator indicator, ArrayList<BusinessPhoto> photoList) {
        super(fm);
        mIndicator = indicator;
        mIndicator.setSize(mList.size());
        mList = photoList;
    }

    @Override
    public Fragment getItem(int position) {

        DLog.d(this, "Featured Fragment getItem()");
        Fragment fragment = new SlidePageFragment();
        Bundle args = new Bundle();
        args.putSerializable("photoObject", mList.get(position));
        args.putInt("position", position);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public void onPageScrollStateChanged(int state) {
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

    public static class SlidePageFragment extends Fragment {

        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

            BusinessPhoto photo = (BusinessPhoto) getArguments().getSerializable("photoObject");
            final int pos = getArguments().getInt("position");

            DLog.d(this, "onCreateView");
            ViewGroup rootView = (ViewGroup) inflater.inflate(
                    R.layout.business_photo_slide_page, container, false);
            ImageView imageView = (ImageView) rootView.findViewById(R.id.slider_image);
            TextView description = (TextView) rootView.findViewById(R.id.slider_text);
            imageView.setImageResource(photo.mResId);
            description.setText(photo.mPhotoDescription);

            rootView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String transition = "cardImage" + pos;

                    Intent i = new Intent(v.getContext(), BusinessPhotosActivity.class);
                    i.putExtra("photoList", mList);
                    i.putStringArrayListExtra("photoList", mList);
                    i.putExtra("position", pos);

                    if (Build.VERSION.SDK_INT < 16 || v == null) {
                        v.getContext().startActivity(i); //use default animation
                    } else if (Build.VERSION.SDK_INT >= 21) { //use tranistion animation
                        Bundle scaledBundle = ActivityOptionsCompat.makeSceneTransitionAnimation(SlidePageFragment.this.getActivity(), v, transition).toBundle();
                        v.getContext().startActivity(i, scaledBundle);
                    } else { //use scale up animation
                        Bundle scaledBundle = ActivityOptionsCompat.makeScaleUpAnimation(v, 0, 0, v.getWidth(), v.getHeight()).toBundle();
                        v.getContext().startActivity(i, scaledBundle);
                    }
                }
            });

            return rootView;

        }
    }
}

