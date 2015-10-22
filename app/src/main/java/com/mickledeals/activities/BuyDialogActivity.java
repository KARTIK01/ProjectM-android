package com.mickledeals.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.mickledeals.R;
import com.mickledeals.datamodel.CouponInfo;
import com.mickledeals.datamodel.DataListModel;
import com.mickledeals.utils.JSONHelper;
import com.mickledeals.utils.MDApiManager;
import com.mickledeals.utils.Utils;

import org.json.JSONObject;

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
    private View mPaymentRow;
    private View mTotalPriceProgress;
    private View mMickleCreditProgress;
    private TextView mNoChargeMsg;

    private CouponInfo mCouponInfo;

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
        mPaymentRow = findViewById(R.id.paymentRow);

        //just to optimize UI when loading
        mPaymentRow.setVisibility(DataListModel.getInstance().getMickleCredits() == 0? View.VISIBLE : View.GONE);

        mCreditCardIcon.setImageBitmap(CardType.VISA.imageBitmap(this));

        mCouponInfo = DataListModel.getInstance().getCouponMap().get(getIntent().getIntExtra("couponId", 0));
        mStoreName.setText(mCouponInfo.mBusinessInfo.getStoreName());
        mCouponDescription.setText(mCouponInfo.getDescription());
        mCouponPrice.setText(Utils.formatPrice(mCouponInfo.mPrice));

        mProgressBar.getIndeterminateDrawable().setColorFilter(
                getResources().getColor(R.color.white),
                android.graphics.PorterDuff.Mode.SRC_IN);

        MDApiManager.reviewOrder(mCouponInfo.mId, new MDReponseListenerImpl<JSONObject>() {

            @Override
            public void onMDSuccessResponse(JSONObject object) {
                super.onMDSuccessResponse(object);
                mTotalPriceProgress.setVisibility(View.GONE);
                mMickleCreditProgress.setVisibility(View.GONE);

                double currentCredit = JSONHelper.getDouble(object, "currentCredit");
                double totalPrice = JSONHelper.getDouble(object, "totalPrice");
                double useCredit = JSONHelper.getDouble(object, "useCredit");
                if (totalPrice > 0) {
                    mPaymentRow.setVisibility(View.VISIBLE);
                } else {
                    mPaymentRow.setVisibility(View.GONE);
                }
                mMickleCredit.setText("- " + Utils.formatPrice(useCredit));
                mTotalPrice.setText(Utils.formatPrice(totalPrice));
                mCurrentCredit.setText(getString(R.string.current_credit, Utils.formatPrice(currentCredit)));
            }
        });
    }

    public void paymentMethodClick(View v) {
        Intent i = new Intent(this, PaymentActivity.class);
        startActivityForResult(i, REQUEST_PAYMENT_CODE);
    }

    public void confirmClick(View v) {
        mProgressBar.setVisibility(View.VISIBLE);
        int paymentId = 0;
        MDApiManager.purchaseCoupon(mCouponInfo.mId, paymentId, new MDReponseListenerImpl<JSONObject>() {

            @Override
            public void onMDSuccessResponse(JSONObject object) {
                super.onMDSuccessResponse(object);

                mCouponInfo.setPurhcaseInfo(object);

                MDApiManager.getPayments(null); //to update micklecredits in slider drawer

                Intent i = new Intent();
                i.putExtra("pay", mPaymentRow.isShown());
//                i.putExtra("remainingTime", "");
                setResult(RESULT_OK, i);
                finish();
            }

            @Override
            public void onMDErrorResponse(String errorMessage) {


//                //temp
//                Intent i = new Intent();
//                i.putExtra("pay", true);
////                i.putExtra("remainingTime", "");
//                setResult(RESULT_OK, i);
//                finish();

                if (errorMessage != null) {
                    if (errorMessage.equals("COUPON_NOT_AVAILABLE_FOR_USER")) {
                        onMDErrorResponse(R.string.purhcase_error_not_available);
                        return;
                    }
                }
                super.onMDErrorResponse(errorMessage);
            }
        });



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
