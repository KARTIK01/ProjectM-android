package com.mickledeals.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.mickledeals.R;

/**
 * Created by Nicky on 5/17/2015.
 */
public class ConfirmRedeemDialogActivity extends DialogSwipeDismissActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null && savedInstanceState.getBoolean("isKilled")) return;
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_confirm_redeem;
    }

    public void redeemNowClick(View v) {

        Intent i = new Intent(ConfirmRedeemDialogActivity.this, RedeemDialogActivity.class);
        i.putExtra("storeName", getIntent().getStringExtra("storeName"));
        i.putExtra("couponDesc", getIntent().getStringExtra("couponDesc"));
        i.putExtra("id", getIntent().getIntExtra("id", 0));

        setResult(Activity.RESULT_OK, i);
        finish();
    }

    @Override
    protected boolean useTranslateAnim() {
        return false;
    }

    protected int getDimmingDuration() {
        return 300;
    }
}
