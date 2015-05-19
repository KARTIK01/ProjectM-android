package com.mickledeals.activities;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.os.Bundle;
import android.view.View;

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

        findViewById(R.id.dialogContainer).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //to dismiss dialog when tap on empty space
                finish();
            }
        });
        findViewById(R.id.dialogContent).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //to prevent dialog dismiss when tapping content
            }
        });

        ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), getResources().getColor(android.R.color.transparent), getResources().getColor(R.color.transparentViewPagerBg));
        colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                mViewPager.setBackgroundColor((Integer)animator.getAnimatedValue());
            }

        });
        colorAnimation.setDuration(500);
        colorAnimation.start();
    }
}
