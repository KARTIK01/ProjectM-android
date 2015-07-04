package com.mickledeals.activities;

import android.os.Bundle;
import android.view.View;

import com.mickledeals.R;

/**
 * Created by Nicky on 5/17/2015.
 */
public class SuccessDialogActivity extends DialogSwipeDismissActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null && savedInstanceState.getBoolean("isKilled")) return;

        if (getIntent().getBooleanExtra("pay", false)) {
            findViewById(R.id.sharePurchaseCouponMsg).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.shareFreeCouponMsg).setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_success_purchase;
    }
}
