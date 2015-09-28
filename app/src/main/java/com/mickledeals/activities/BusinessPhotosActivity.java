package com.mickledeals.activities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.mickledeals.R;
import com.mickledeals.datamodel.BusinessPhoto;
import com.mickledeals.utils.DLog;
import com.mickledeals.utils.Utils;
import com.mickledeals.views.PagerIndicator;

import java.util.ArrayList;

import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * Created by Nicky on 12/27/2014.
 */
public class BusinessPhotosActivity extends BaseActivity {

    private ViewPager mViewPager;
    private int mInitialIndex;
    private ArrayList<BusinessPhoto> mList;
    private PagerIndicator mIndicator;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null && savedInstanceState.getBoolean("isKilled")) return;

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        mInitialIndex = getIntent().getIntExtra("position", 0);
        mList = (ArrayList) getIntent().getSerializableExtra("photoList");
        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        DetailsPagerAdapter adapter = new DetailsPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(adapter);
        mViewPager.addOnPageChangeListener(adapter);
        mViewPager.setCurrentItem(mInitialIndex);

        mIndicator = (PagerIndicator) findViewById(R.id.pagerIndicator);


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
            if (mIndicator != null) mIndicator.setSize(mList.size());
            return mList.size();
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

            BusinessPhoto photo = (BusinessPhoto) getArguments().getSerializable("photoObject");

            DLog.d(this, "onCreateView");
            ViewGroup rootView = (ViewGroup) inflater.inflate(
                    R.layout.business_photo_fullscreen_slide_page, container, false);
            ImageView imageView = (ImageView) rootView.findViewById(R.id.slider_image);
            TextView description = (TextView) rootView.findViewById(R.id.slider_text);

            PhotoViewAttacher attacher = new PhotoViewAttacher(imageView);

            //temp
            Bitmap bm = BitmapFactory.decodeResource(getResources(), photo.mResId);
            //

            if (Build.VERSION.SDK_INT >= 17) {
                ImageView imageViewBg = (ImageView) rootView.findViewById(R.id.slider_blurry_image);
                imageViewBg.setImageBitmap(Utils.blur(rootView.getContext(), bm));
            }
            imageView.setImageResource(photo.mResId);
            attacher.update();
            description.setText(photo.mPhotoDescription);


            return rootView;

        }
    }
}
