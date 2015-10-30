package com.mickledeals.adapters;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.mickledeals.R;
import com.mickledeals.activities.BusinessPhotosActivity;
import com.mickledeals.datamodel.BusinessPhoto;
import com.mickledeals.utils.DLog;
import com.mickledeals.utils.MDApiManager;
import com.mickledeals.views.PagerIndicator;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nicky on 12/26/2014.
 */


public class BusinessPhotoSliderAdapter extends PagerAdapter implements
        ViewPager.OnPageChangeListener {

    private List<BusinessPhoto> mList;
    private PagerIndicator mIndicator;
    private Activity mActivity;

    public BusinessPhotoSliderAdapter(Activity activity, PagerIndicator indicator, List<BusinessPhoto> photoList) {
        mActivity = activity;
        mIndicator = indicator;
        mList = photoList;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((View) object);
    }


    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        DLog.d(this, "onCreateView");
        ViewGroup rootView = (ViewGroup) mActivity.getLayoutInflater().inflate(
                R.layout.business_photo_slide_page, null);
        final NetworkImageView imageView = (NetworkImageView) rootView.findViewById(R.id.slider_image);
        final TextView description = (TextView) rootView.findViewById(R.id.slider_text);
        imageView.setImageUrl(mList.get(position).mUrl, MDApiManager.sImageLoader);
        description.setText(mList.get(position).mPhotoDescription);
        rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(v.getContext(), BusinessPhotosActivity.class);
                i.putExtra("photoList", (ArrayList) mList);
                i.putExtra("position", position);
                //transition does not look good because different scale!!!!, dont bother to try
                mActivity.startActivity(i); //use default animation
            }
        });

        ((ViewPager) container).addView(rootView, 0);
        return rootView;
    }


    @Override
    public int getCount() {

        if (mIndicator != null) mIndicator.setSize(mList.size());
        return mList.size();
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ((ViewPager) container).removeView((View) object);
    }

    @Override
    public void onPageSelected(int position) {
        if (mIndicator != null) {
            mIndicator.setPosition(position);
            mIndicator.invalidate();
        }
    }
}

