package com.mickledeals.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.TextView;

import com.mickledeals.R;
import com.mickledeals.utils.Constants;
import com.mickledeals.utils.PreferenceHelper;
import com.mickledeals.utils.Utils;

import java.util.Locale;

/**
 * Created by Nicky on 9/26/2015.
 */
public class LaunchScreen extends Activity {

    private ViewGroup mContentLayout;
    private View mFullLogo;
    private View mContinueButton;
    private TextView mContinueText;
    private TextView mLanguageText;
    private TextView mLanguage1;
    private TextView mLanguage2;
    private View mLanguageList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch_screen);

        mContinueButton = findViewById(R.id.continueButton);
        mContinueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i =new Intent(LaunchScreen.this, IntroScreen.class);
                startActivity(i);
                overridePendingTransition(R.anim.fade_in_screen_enter, 0);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        finish();
                    }
                }, 2000);
            }
        });
        mContinueText = (TextView) findViewById(R.id.continueText);
        mLanguageText = (TextView) findViewById(R.id.languageText);
        mLanguage1 = (TextView) findViewById(R.id.language1);
        mLanguage2 = (TextView) findViewById(R.id.language2);
        mLanguageText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleLanguageList();
            }
        });
        mLanguageList = findViewById(R.id.languageList);
        mContentLayout = (ViewGroup) findViewById(R.id.contentLayout);
        mFullLogo = findViewById(R.id.fullLogo);

        AlphaAnimation anim = new AlphaAnimation(0, 1);
        anim.setDuration(1000);
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                startContentAppearAnim();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        mFullLogo.startAnimation(anim);



        mLanguage1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectLanguage(true);
            }
        });
        mLanguage2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectLanguage(false);
            }
        });

        setLanguage();

        //tap outside to dismiss the list
        findViewById(R.id.launchBg).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mLanguageList.getVisibility() == View.VISIBLE) {
                    mLanguageList.setVisibility(View.INVISIBLE);
                }
            }
        });

    }

    private void setLanguage() {
        int language = 0;
        if (Utils.isChineseLocale()) language = Constants.LANG_CHT;
        String displayText = getString(R.string.language) + ":  " + getResources().getStringArray(R.array.language_list)[language];

        int index = displayText.indexOf(":");
        SpannableString spannable = new SpannableString(displayText);
        spannable.setSpan(new StyleSpan(Typeface.BOLD), index + 1, displayText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannable.setSpan(new RelativeSizeSpan(0.85f), 0, index, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        mLanguageText.setText(spannable);

        mLanguage1.setText(getResources().getStringArray(R.array.language_list)[language]);
        mLanguage2.setText(getResources().getStringArray(R.array.language_list)[1 - language]);
        mContinueText.setText(R.string.continue_in_launch);
    }

    private void selectLanguage(boolean first) {

        mLanguageList.setVisibility(View.INVISIBLE);
        if (!first) {
            if (Utils.isChineseLocale()) {
                MDApplication.sCurrentLocale = Locale.ENGLISH;
                Locale.setDefault(Locale.ENGLISH);
                PreferenceHelper.savePreferencesInt(this, "language", Constants.LANG_ENG);
            } else {
                MDApplication.sCurrentLocale = Locale.CHINESE;
                Locale.setDefault(Locale.CHINESE);
                PreferenceHelper.savePreferencesInt(this, "language", Constants.LANG_CHT);
            }

            Configuration config = new Configuration();
            config.locale = MDApplication.sCurrentLocale;
            getResources().updateConfiguration(config, getResources().getDisplayMetrics());
            setLanguage();
        }
    }

    private void startContentAppearAnim() {
        mContentLayout.setVisibility(View.VISIBLE);

        for (int i = 0; i < mContentLayout.getChildCount(); i++) {
            View v = mContentLayout.getChildAt(i);
            if (v.getVisibility() != View.VISIBLE) continue;
            AlphaAnimation anim = new AlphaAnimation(0, 1);
//            anim.setFillBefore(true);
            anim.setDuration(500);
            anim.setStartOffset(i * 150);
            v.startAnimation(anim);
        }
    }

    private void toggleLanguageList() {
        if (mLanguageList.getVisibility() == View.VISIBLE) {
            mLanguageList.setVisibility(View.INVISIBLE);
            return;
        }

        mLanguageList.setVisibility(View.VISIBLE);
        AlphaAnimation anim = new AlphaAnimation(0, 1);
        anim.setDuration(500);
        mLanguageList.startAnimation(anim);
    }


}
