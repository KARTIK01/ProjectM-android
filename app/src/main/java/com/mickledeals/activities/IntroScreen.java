package com.mickledeals.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
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
import com.mickledeals.utils.PreferenceHelper;
import com.mickledeals.views.PagerIndicator;

/**
 * Created by Nicky on 9/26/2015.
 */
public class IntroScreen extends Activity {

    private static final int PAGE_SIZE = 5;
    private ViewPager mViewPager;
    private PagerIndicator mIndicator;
    private String[] mIntroMessages;
    private int[] mIntroContentRes = new int[PAGE_SIZE - 1];

    private Typeface mTf;
    private View[] mImageViews = new View[PAGE_SIZE];


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
        mViewPager.setOffscreenPageLimit(PAGE_SIZE);

        mIndicator = (PagerIndicator) findViewById(R.id.pagerIndicator);
        mIndicator.setSize(PAGE_SIZE);

        mIntroMessages = getResources().getStringArray(R.array.intro_message_list);

        mImageViews[0] = findViewById(R.id.introBg1);
        mImageViews[1] = findViewById(R.id.introBg2);
        mImageViews[2] = findViewById(R.id.introBg3);
        mImageViews[3] = findViewById(R.id.introBg4);
        mImageViews[4] = findViewById(R.id.introBg5);
    }

    @Override
    protected void onStop() {
        super.onStop();
        finish();
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
            ViewGroup rootView = null;
            if (position == getCount() - 1) {
                rootView = (ViewGroup) IntroScreen.this.getLayoutInflater().inflate(
                        R.layout.intro_last_layout, null);
                TextView signupText = (TextView) rootView.findViewById(R.id.signupText);
                signupText.setTypeface(mTf);
                TextView discoverText = (TextView) rootView.findViewById(R.id.discoverText);
                discoverText.setTypeface(mTf);
                View loginBtn = rootView.findViewById(R.id.loginButton);
                loginBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(IntroScreen.this, LoginDialogActivity.class);
                        i.putExtra("fromIntroScreen", true);
                        startActivity(i);
                    }
                });
                TextView skipBtn = (TextView) rootView.findViewById(R.id.skip);
                skipBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        PreferenceHelper.savePreferencesBoolean(IntroScreen.this, "firstLaunch", false);
                        Intent i = new Intent(IntroScreen.this, HomeActivity.class);
                        i.putExtra("fromIntroScreen", true);
                        startActivity(i);
                        finish();
                    }
                });
                skipBtn.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Montserrat-UltraLight.otf"));
            } else {
                rootView = (ViewGroup) IntroScreen.this.getLayoutInflater().inflate(
                        R.layout.intro_layout, null);
                TextView introMessage = (TextView) rootView.findViewById(R.id.introMessage);
                introMessage.setText(mIntroMessages[position]);
                introMessage.setTypeface(mTf);
                ImageView introImage = (ImageView) rootView.findViewById(R.id.introImage);
            }

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
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

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


            int index = (Integer) page.getTag();

            View bgImage = mImageViews[index];

            if(position <= -1 || position >= 1) {
                bgImage.setAlpha(0);
                bgImage.setVisibility(View.GONE);
            } else if( position == 0 ) {
                bgImage.setAlpha(1);
                bgImage.setVisibility(View.VISIBLE);
            } else {
                bgImage.setAlpha(1 - Math.abs(position));
                bgImage.setVisibility(View.VISIBLE);
            }


        }
    }



}


