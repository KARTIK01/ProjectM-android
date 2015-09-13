package com.mickledeals.activities;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.mickledeals.R;

/**
 * Created by Nicky on 5/17/2015.
 */
public abstract class DialogSwipeDismissActivity extends SwipeDismissActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null && savedInstanceState.getBoolean("isKilled")) return;

        getWindow().setWindowAnimations(0);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //delay 1 second to prevent accidentally dismiss
                findViewById(R.id.dialogContainer).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //to dismiss dialog when tap on empty space
                        finish();
                    }
                });
            }
        }, 1000);

        View content = findViewById(R.id.dialogContent);
        if (content != null) {
            content.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //to prevent dialog dismiss when tapping content
                }
            });
        }

        ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), getResources().getColor(android.R.color.transparent), getResources().getColor(R.color.transparentViewPagerBg));
        colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                mViewPager.setBackgroundColor((Integer)animator.getAnimatedValue());
            }

        });
        colorAnimation.setDuration(getDimmingDuration());
        colorAnimation.start();
        if (useTranslateAnim()) {
            final Animation transAnim = AnimationUtils.loadAnimation(this,
                    R.anim.dialog_enter_anim);
            findViewById(R.id.dialogContainer).startAnimation(transAnim);
        }
    }

    protected boolean useTranslateAnim() {
        return true;
    }

    protected int getDimmingDuration() {
        return 500;
    }


    @Override
    protected int getLayoutType() {
        return LAYOUT_TYPE_DIALOG_SWIPE;
    }
}
