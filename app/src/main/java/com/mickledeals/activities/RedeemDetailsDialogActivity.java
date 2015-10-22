package com.mickledeals.activities;

import android.os.Bundle;
import android.widget.TextView;

import com.mickledeals.R;

/**
 * Created by Nicky on 5/17/2015.
 */
public class RedeemDetailsDialogActivity extends DialogSwipeDismissActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null && savedInstanceState.getBoolean("isKilled")) return;


        final TextView storeName = (TextView) findViewById(R.id.storeName);
        storeName.setText(getIntent().getStringExtra("storeName"));
        final TextView discLong = (TextView) findViewById(R.id.discLong);
        discLong.setText(getIntent().getStringExtra("couponDesc"));
        final TextView finePrint = (TextView) findViewById(R.id.finePrint);
        finePrint.setText(getIntent().getStringExtra("finePrint"));

    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_redeem_full_details;
    }

    @Override
    protected boolean useTranslateAnim() {
        return false;
    }

    protected int getDimmingDuration() {
        return 300;
    }
}
