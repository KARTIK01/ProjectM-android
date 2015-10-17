package com.mickledeals.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.mickledeals.R;
import com.mickledeals.utils.JSONHelper;
import com.mickledeals.utils.MDApiManager;

import org.json.JSONObject;

import java.text.DecimalFormat;

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
    private TextView mCurrentCredit;
    private TextView mTotalPrice;
    private ImageView mCreditCardIcon;
    private View mTotalPriceProgress;
    private View mMickleCreditProgress;
    private TextView mNoChargeMsg;
    private DecimalFormat mDf = new DecimalFormat("0.00");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null && savedInstanceState.getBoolean("isKilled")) return;

        mStoreName = (TextView) findViewById(R.id.storeName);
        mCouponDescription = (TextView) findViewById(R.id.couponDescription);
        mCouponPrice = (TextView) findViewById(R.id.couponPrice);
        mMickleCredit = (TextView) findViewById(R.id.mickleCredit);
        mCurrentCredit = (TextView) findViewById(R.id.currentCredit);
        mTotalPrice = (TextView) findViewById(R.id.totalPrice);
        mNoChargeMsg = (TextView) findViewById(R.id.noChargeMsg);
        mCreditCardIcon = (ImageView) findViewById(R.id.creditCardIcon);
        mTotalPriceProgress = findViewById(R.id.totalPriceProgress);
        mMickleCreditProgress = findViewById(R.id.mickleCreditProgress);

        mCreditCardIcon.setImageBitmap(CardType.VISA.imageBitmap(this));

        mStoreName.setText(getIntent().getStringExtra("store_name"));
        mCouponDescription.setText(getIntent().getStringExtra("coupon_description"));
        mCouponPrice.setText("$" + mDf.format(getIntent().getDoubleExtra("price", 0)));

        MDApiManager.reviewOrder(getIntent().getIntExtra("couponId", 0), new MDReponseListenerImpl<JSONObject>() {

            @Override
            public void onMDSuccessResponse(JSONObject object) {
                super.onMDSuccessResponse(object);
                mTotalPriceProgress.setVisibility(View.GONE);
                mMickleCreditProgress.setVisibility(View.GONE);

                double currentCredit = JSONHelper.getDouble(object, "currentCredit");
                double totalPrice = JSONHelper.getDouble(object, "totalPrice");
                double useCredit = JSONHelper.getDouble(object, "useCredit");
                mMickleCredit.setText("- $" + mDf.format(useCredit));
                mTotalPrice.setText("$" + mDf.format(totalPrice));
                mCurrentCredit.setText(getString(R.string.current_credit, "$" + mDf.format(currentCredit)));
            }
        });
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

    public void termsClick(View v) {
        Intent i = new Intent(this, WebPageActivity.class);
        i.putExtra("webContent", "terms");
        startActivity(i);
    }

    public void privacyClick(View v) {
        Intent i = new Intent(this, WebPageActivity.class);
        i.putExtra("webContent", "privacy");
        startActivity(i);
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.buy_dialog;
    }
}
