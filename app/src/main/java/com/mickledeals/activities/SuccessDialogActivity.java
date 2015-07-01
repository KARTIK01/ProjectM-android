package com.mickledeals.activities;

import android.os.Bundle;

import com.mickledeals.R;

/**
 * Created by Nicky on 5/17/2015.
 */
public class SuccessDialogActivity extends DialogSwipeDismissActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null && savedInstanceState.getBoolean("isKilled")) return;
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_success_purchase;
    }
}
