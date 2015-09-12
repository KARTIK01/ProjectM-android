package com.mickledeals.activities;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.TextView;

import com.mickledeals.R;

/**
 * Created by Nicky on 5/17/2015.
 */
public class RedeemDialogActivity extends BaseActivity {

    //742  bg orange part height
    //1006 bg width
    //1234 bg height

    private View mBaseLayout;
    private TextView mMarkAsUsed;
    private TextView mCouponNumber;
    private View mBaseContent;
    private View mOrangeContent;

//    private static final long INTIIAL_REMAINING_TIME = 2 * 60 * 60 * 1000;

//    private TextView mExpiredTime;
//    private long mRedeemTime;
//    private Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null && savedInstanceState.getBoolean("isKilled")) return;

        getWindow().setWindowAnimations(0);

        final TextView storeName = (TextView) findViewById(R.id.storeName);
        storeName.setText(getIntent().getStringExtra("storeName"));
        final TextView discLong = (TextView) findViewById(R.id.discLong);
        discLong.setText(getIntent().getStringExtra("couponDesc"));

        mBaseLayout = findViewById(R.id.baseLayout);
        mBaseContent = findViewById(R.id.baseContent);
        ViewGroup.LayoutParams params = mBaseContent.getLayoutParams();
        params.width = MDApplication.sDeviceWidth;
        params.height = params.width * 1234 / 1006;
        mBaseContent.setLayoutParams(params);

        mOrangeContent = findViewById(R.id.orangeContent);
        params = mOrangeContent.getLayoutParams();
        params.width = MDApplication.sDeviceWidth;
        params.height = params.width * 742 / 1006;
        mOrangeContent.setLayoutParams(params);

        mMarkAsUsed = (TextView) findViewById(R.id.markAsUsed);
        Typeface tf = Typeface.createFromAsset(getAssets(), "fonts/Montserrat-SemiBold.otf");
        mMarkAsUsed.setTypeface(tf);

        mCouponNumber = (TextView) findViewById(R.id.couponNumber);
        //getIntent().getStringExtra("couponNo/purchaseno")
        mCouponNumber.setText("Coupon #100007198");


        ScaleAnimation anim = new ScaleAnimation(0, 1, 0, 1, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        anim.setDuration(600);
        mBaseContent.setAnimation(anim);

        ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), getResources().getColor(android.R.color.transparent), getResources().getColor(R.color.redeemDialogBg));
        colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                mBaseLayout.setBackgroundColor((Integer)animator.getAnimatedValue());
            }

        });
        colorAnimation.setDuration(600);
        colorAnimation.start();

//        mExpiredTime = (TextView) findViewById(R.id.expireTime);

//        mRedeemTime = getIntent().getLongExtra("redeemTime", 0);

//        mHandler = new Handler();
//        mHandler.removeCallbacks(mUpdatetimerThread);
//        mHandler.postDelayed(mUpdatetimerThread, 0);
    }

//    private String getExpiredTimerValue() {
//
//        long timeDiff = System.currentTimeMillis() - mRedeemTime;
//        long timeRemainingInSecs = (INTIIAL_REMAINING_TIME - timeDiff) / 1000;
//        return DateUtils.formatElapsedTime(timeRemainingInSecs);
//
//    }
//
//    private Runnable mUpdatetimerThread = new Runnable() {
//        @Override
//        public void run() {
//            mExpiredTime.setText(getExpiredTimerValue());
//            mHandler.postDelayed(this, 0);
//        }
//    };

    public void markUsedClick(View v) {
        AlertDialog dialog = new AlertDialog.Builder(RedeemDialogActivity.this, R.style.AppCompatAlertDialogStyle)
                .setTitle(R.string.mark_as_used_confirm)
                .setMessage(R.string.mark_as_used_confirm_msg)
                .setPositiveButton(R.string.mark_as_used_yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        Intent i = new Intent();
                        i.putExtra("id", getIntent().getIntExtra("id", 0));
                        //send request then finish activity
                        setResult(Activity.RESULT_OK, i);
                        finish();
                    }
                })
                .setNegativeButton(R.string.mark_as_used_no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();
        dialog.show();
    }

    public void fullDetailsClick(View v) {
        Intent i = new Intent(this, RedeemDetailsDialogActivity.class);
        i.putExtras(getIntent().getExtras());
        startActivity(i);
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.redeem_dialog;
    }

    @Override
    public void onBackPressed() {
        markUsedClick(null);
    }
}
