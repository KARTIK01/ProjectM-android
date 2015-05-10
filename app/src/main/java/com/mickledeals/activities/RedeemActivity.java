package com.mickledeals.activities;

import android.os.Bundle;

import com.mickledeals.R;

/**
 * Created by Nicky on 11/28/2014.
 */
public class RedeemActivity extends BaseActivity {


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null && savedInstanceState.getBoolean("isKilled")) return;
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_redeem;
    }
}
