package com.mickledeals.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.mickledeals.R;

import io.card.payment.CardType;

/**
 * Created by Nicky on 5/17/2015.
 */
public class BuyDialogActivity extends DialogSwipeDismissActivity {

    private static final int REQUEST_PAYMENT_CODE = 1;

    private TextView mStoreName;
    private TextView mCouponDescription;
    private TextView mCouponPrice;
    private TextView mMickleCredit;
    private TextView mTotalPrice;
    private ImageView mCreditCardIcon;
    private TextView mNoChargeMsg;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null && savedInstanceState.getBoolean("isKilled")) return;

        mStoreName = (TextView) findViewById(R.id.storeName);
        mCouponDescription = (TextView) findViewById(R.id.couponDescription);
        mCouponPrice = (TextView) findViewById(R.id.couponPrice);
        mMickleCredit = (TextView) findViewById(R.id.mickleCredit);
        mTotalPrice = (TextView) findViewById(R.id.totalPrice);
        mNoChargeMsg = (TextView) findViewById(R.id.noChargeMsg);
        mCreditCardIcon = (ImageView) findViewById(R.id.creditCardIcon);

        mCreditCardIcon.setImageBitmap(CardType.VISA.imageBitmap(this));

        mStoreName.setText(getIntent().getStringExtra("store_name"));
        mCouponDescription.setText(getIntent().getStringExtra("coupon_description"));
        mCouponPrice.setText("$" + (int)getIntent().getFloatExtra("price", 0));
        mMickleCredit.setText("-$" + (int)getIntent().getFloatExtra("price", 0));
        mTotalPrice.setText(getResources().getString(R.string.free));
    }

    public void paymentMethodClick(View v) {
        Intent i = new Intent(this, PaymentActivity.class);
        startActivityForResult(i, REQUEST_PAYMENT_CODE);
    }

    public void confirmClick(View v) {
        Intent i = new Intent();
        i.putExtra("remainingTime", "");
        setResult(RESULT_OK, i);
        finish();
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.buy_dialog;
    }
}
