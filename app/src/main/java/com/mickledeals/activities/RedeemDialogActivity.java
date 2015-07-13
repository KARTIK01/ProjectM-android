package com.mickledeals.activities;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.text.format.DateUtils;
import android.view.View;
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
        mExpiredTime = (TextView) findViewById(R.id.expireTime);

        mRedeemTime = getIntent().getLongExtra("redeemTime", 0);

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

    public void markUsedClick(View v) {
        AlertDialog dialog = new AlertDialog.Builder(RedeemDialogActivity.this, R.style.AppCompatAlertDialogStyle)
                .setMessage(R.string.mark_as_used_confirm)
                .setPositiveButton(R.string.mark_as_used_yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        //send request then finish activity
                        setResult(Activity.RESULT_OK);
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

    @Override
    protected int getLayoutResource() {
        return R.layout.redeem_dialog;
    }
}
