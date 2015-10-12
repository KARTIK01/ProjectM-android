package com.mickledeals.activities;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mickledeals.R;
import com.mickledeals.utils.DLog;
import com.mickledeals.views.PagerIndicator;

/**
 * Created by Nicky on 9/26/2015.
 */
public class IntroScreen extends Activity {

    private static final int PAGE_SIZE = 4;
    private ViewPager mViewPager;
    private PagerIndicator mIndicator;
    private String[] mIntroMessages;
    private int[] mIntroContentRes = new int[PAGE_SIZE];

    private Typeface mTf;
    private LayerDrawable mBgLayer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro_screen);

        mTf = Typeface.createFromAsset(getAssets(), "fonts/Montserrat-Light.otf");

        View decorView = getWindow().getDecorView();
        if (Build.VERSION.SDK_INT >= 16) {
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }

        if (Build.VERSION.SDK_INT >= 21) getWindow().setStatusBarColor(Color.TRANSPARENT);

        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        IntroAdapter adapter = new IntroAdapter();
        mViewPager.setAdapter(adapter);
        mViewPager.setPageTransformer(true, adapter);
        mViewPager.addOnPageChangeListener(adapter);

        mIndicator = (PagerIndicator) findViewById(R.id.pagerIndicator);
        mIndicator.setSize(PAGE_SIZE);

        mIntroMessages = getResources().getStringArray(R.array.intro_message_list);

        mBgLayer = (LayerDrawable) mViewPager.getBackground();
        mBgLayer.getDrawable(0).setAlpha(255);
        mBgLayer.getDrawable(1).setAlpha(0);
        mBgLayer.getDrawable(2).setAlpha(0);
        mBgLayer.getDrawable(3).setAlpha(0);
//        mBgLayer.getDrawable(4).setAlpha(0);
    }


    public class IntroAdapter extends PagerAdapter implements
            ViewPager.OnPageChangeListener, ViewPager.PageTransformer {

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == ((View) object);
        }


        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            DLog.d(this, "onCreateView");
            ViewGroup rootView = (ViewGroup) IntroScreen.this.getLayoutInflater().inflate(
                    R.layout.intro_layout, null);
            TextView introMessage = (TextView) rootView.findViewById(R.id.introMessage);
            introMessage.setText(mIntroMessages[position]);
            introMessage.setTypeface(mTf);
            ImageView introImage = (ImageView) rootView.findViewById(R.id.introImage);
            ((ViewPager) container).addView(rootView, 0);
            rootView.setTag(position);
            return rootView;
        }


        @Override
        public int getCount() {
            return PAGE_SIZE;
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

        @Override
        public void transformPage(View page, float position) {

//            int currentPos = mViewPager.getCurrentItem();
//
//            Log.e("ZZZ", "pos = " + currentPos);
//
//            if (position > 0 && position <= 1) {
//                mImageViews[currentPos].setAlpha(position);
//            }
//            if (position < 0 && position >= -1) {
//                mImageViews[currentPos + 1].setAlpha(-position);
//            }


            int index = (Integer) page.getTag();
            Drawable currentDrawableInLayerDrawable = mBgLayer.getDrawable(index);

            if(position <= -1 || position >= 1) {
                currentDrawableInLayerDrawable.setAlpha(0);
            } else if( position == 0 ) {
                currentDrawableInLayerDrawable.setAlpha(255);
            } else {
                currentDrawableInLayerDrawable.setAlpha((int)(255 - Math.abs(position*255)));
            }


        }
    }



}


