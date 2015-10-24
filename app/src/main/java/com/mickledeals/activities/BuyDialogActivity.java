package com.mickledeals.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.mickledeals.R;
import com.mickledeals.datamodel.CouponInfo;
import com.mickledeals.datamodel.DataListModel;
import com.mickledeals.datamodel.PaymentInfo;
import com.mickledeals.utils.DLog;
import com.mickledeals.utils.JSONHelper;
import com.mickledeals.utils.MDApiManager;
import com.mickledeals.utils.PaymentHelper;
import com.mickledeals.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

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
    private TextView mPaymentDisplay;
    private TextView mAdd;
    private View mPaymentRow;
    private View mTotalPriceProgress;
    private View mMickleCreditProgress;
    private View mPaymentProgress;
    private TextView mNoChargeMsg;

    private CouponInfo mCouponInfo;
    private PaymentInfo mPaymentInfo;

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
        mPaymentDisplay = (TextView) findViewById(R.id.paymentMethod);
        mAdd = (TextView) findViewById(R.id.add);
        mTotalPriceProgress = findViewById(R.id.totalPriceProgress);
        mMickleCreditProgress = findViewById(R.id.mickleCreditProgress);
        mPaymentProgress = findViewById(R.id.paymentProgress);
        mPaymentRow = findViewById(R.id.paymentRow);

        //just to optimize UI when loading
        mPaymentRow.setVisibility(DataListModel.getInstance().getMickleCredits() == 0 ? View.VISIBLE : View.GONE);


        mCouponInfo = DataListModel.getInstance().getCouponMap().get(getIntent().getIntExtra("couponId", 0));
        mStoreName.setText(mCouponInfo.mBusinessInfo.getStoreName());
        mCouponDescription.setText(mCouponInfo.getDescription());
        mCouponPrice.setText(Utils.formatPrice(mCouponInfo.mPrice));

        MDApiManager.reviewOrder(mCouponInfo.mId, new MDReponseListenerImpl<JSONObject>() {

            @Override
            public void onMDSuccessResponse(JSONObject object) {
                super.onMDSuccessResponse(object);
                mTotalPriceProgress.setVisibility(View.GONE);
                mMickleCreditProgress.setVisibility(View.GONE);
                mPaymentProgress.setVisibility(View.GONE);

                double currentCredit = JSONHelper.getDouble(object, "currentCredit");
                DataListModel.getInstance().setMickleCredit(currentCredit);
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

                JSONArray paymentArray = JSONHelper.getJSONArray(object, "payment");
                List<PaymentInfo> list = DataListModel.getInstance().getPaymentList();
                list.clear();
                for (int i = 0; i < paymentArray.length(); i++) {
                    try {
                        JSONObject jsonobject = paymentArray.getJSONObject(i);
                        PaymentInfo info = new PaymentInfo(jsonobject);
                        list.add(info);
                    } catch (JSONException e) {
                        DLog.e(MDApiManager.class, e.toString());
                    }
                }
                DataListModel.getInstance().mUpdatedPayment = true;
                updatePaymentInfo();
            }
        });
    }

    private void updatePaymentInfo() {

        List<PaymentInfo> list = DataListModel.getInstance().getPaymentList();
        boolean noPayment = list.size() == 0;
        if (noPayment) {
            mAdd.setVisibility(View.VISIBLE);
        } else {
            for (PaymentInfo info: list) {
                if (info.mPrimary) {
                    mPaymentInfo = info;
                    break;
                }
            }
            if (!mPaymentInfo.mPaypalAccount.equals("")) {
                mCreditCardIcon.setImageResource(R.drawable.ic_paypal_card);
//                mPaymentDisplay.setText(mPaymentInfo.mPaypalAccount);
            } else {
                PaymentHelper.setCardIcon(mCreditCardIcon, mPaymentInfo.mCardType);
                mPaymentDisplay.setText(mPaymentInfo.mLastFourDigits);
            }
            mCreditCardIcon.setVisibility(View.VISIBLE);
            mPaymentDisplay.setVisibility(View.VISIBLE);
        }
    }

    public void paymentMethodClick(View v) {
        Intent i = new Intent(this, PaymentActivity.class);
        startActivityForResult(i, REQUEST_PAYMENT_CODE);
    }

    public void confirmClick(View v) {
        if (mPaymentRow.isShown() && mPaymentInfo.mPaymentId == 0) {
            Toast.makeText(this, R.string.no_payment_message, Toast.LENGTH_SHORT).show();
            return;
        }

        mProgressBar.setVisibility(View.VISIBLE);
        MDApiManager.purchaseCoupon(mCouponInfo.mId, mPaymentInfo.mPaymentId, new MDReponseListenerImpl<JSONObject>() {

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_PAYMENT_CODE) {
            updatePaymentInfo();
        }
    }
}
