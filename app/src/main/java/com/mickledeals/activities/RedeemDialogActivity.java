package com.mickledeals.activities;

import android.os.Bundle;
import android.os.Handler;
import android.text.format.DateUtils;
import android.widget.TextView;

import com.mickledeals.R;

/**
 * Created by Nicky on 5/17/2015.
 */
public class RedeemDialogActivity extends DialogSwipeDismissActivity {


    private static final long INTIIAL_REMAINING_TIME = 2 * 60 * 60 * 1000;

    private TextView mExpiredTime;
    private long mRedeemTime;
    private Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null && savedInstanceState.getBoolean("isKilled")) return;

        final TextView storeName = (TextView) findViewById(R.id.storeName);
        storeName.setText(getIntent().getStringExtra("storeName"));
        final TextView discLong = (TextView) findViewById(R.id.discLong);
        discLong.setText(getIntent().getStringExtra("couponDesc"));
        discLong.setSelected(true);
        mExpiredTime = (TextView) findViewById(R.id.expireTime);

        mRedeemTime = System.currentTimeMillis();

        mHandler = new Handler();
        mHandler.removeCallbacks(mUpdatetimerThread);
        mHandler.postDelayed(mUpdatetimerThread, 0);
    }

    private String getExpiredTimerValue() {

        long timeDiff = System.currentTimeMillis() - mRedeemTime;
        long timeRemainingInSecs = (INTIIAL_REMAINING_TIME - timeDiff) / 1000;
        return DateUtils.formatElapsedTime(timeRemainingInSecs);

    }

    private Runnable mUpdatetimerThread = new Runnable() {
        @Override
        public void run() {
            mExpiredTime.setText(getExpiredTimerValue());
            mHandler.postDelayed(this, 0);
        }
    };

    @Override
    protected int getLayoutResource() {
        return R.layout.redeem_dialog;
    }
}
