package com.mickledeals.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mickledeals.R;
import com.mickledeals.datamodel.DataListModel;
import com.mickledeals.datamodel.PaymentInfo;
import com.mickledeals.utils.MDApiManager;
import com.mickledeals.utils.PaymentHelper;
import com.mickledeals.utils.Utils;
import com.paypal.android.sdk.payments.PayPalAuthorization;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalFuturePaymentActivity;
import com.paypal.android.sdk.payments.PayPalOAuthScopes;
import com.paypal.android.sdk.payments.PayPalProfileSharingActivity;
import com.paypal.android.sdk.payments.PayPalService;

import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Nicky on 11/28/2014.
 */
public class PaymentActivity extends DialogSwipeDismissActivity {

    private static final String CONFIG_ENVIRONMENT = PayPalConfiguration.ENVIRONMENT_SANDBOX;
    private static final String CONFIG_CLIENT_ID = "AU6GrNZx32VuyV1xOoHaJZ9uhyqbujT7icRU3IVMWLVcFydRYrUZ2Xewine1B6eBHaQ-c4H9ORLsORZJ";
    private static PayPalConfiguration config = new PayPalConfiguration()
            .environment(CONFIG_ENVIRONMENT)
            .clientId(CONFIG_CLIENT_ID)
                    // The following are only used in PayPalFuturePaymentActivity.
            .merchantName("MickleDeals")
            .merchantPrivacyPolicyUri(Uri.parse("http://www.mickledeals.com/privacy"))
            .merchantUserAgreementUri(Uri.parse("http://www.mickledeals.com/terms"));
    private static final int REQUEST_CODE_PROFILE_SHARING = 1;
    private static final int REQUEST_CODE_FUTURE_PAYMENT = 2;
    private static final int REQUEST_CODE_ADD_CARD = 10;

    private View mNoPaymentMessage;
    private TextView mCredit;
    private LinearLayout mSavedMethodLayout;
    private List<PaymentInfo> mPayments = DataListModel.getInstance().getPaymentList();
    private int mSelectedPaymentPos = -1;
    private PaymentInfo mSelectedPaymentInfo;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null && savedInstanceState.getBoolean("isKilled")) return;

        Intent intent = new Intent(this, PayPalService.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
        startService(intent);
        mNoPaymentMessage = findViewById(R.id.noPaymentMessage);
        mCredit = (TextView) findViewById(R.id.mickleCredit);
        mSavedMethodLayout = (LinearLayout) findViewById(R.id.savedMethodLayout);

        if (DataListModel.getInstance().mUpdatedPayment) {
            addSavedMethodsToView();
            mCredit.setText(getString(R.string.your_mickle_credit, Utils.formatPrice(DataListModel.getInstance().getMickleCredits())));
        } else {
            MDApiManager.getPayments(new MDReponseListenerImpl<Void>() {
                @Override
                public void onMDSuccessResponse(Void object) {
                    super.onMDSuccessResponse(object);
                    addSavedMethodsToView();
                    mCredit.setText(getString(R.string.your_mickle_credit, Utils.formatPrice(DataListModel.getInstance().getMickleCredits())));
                }
            });
        }
    }

    public void addPayPalClick(View v) {
        Intent intent = new Intent(this, PayPalProfileSharingActivity.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
        intent.putExtra(PayPalProfileSharingActivity.EXTRA_REQUESTED_SCOPES, getOauthScopes());
        startActivityForResult(intent, REQUEST_CODE_PROFILE_SHARING);
    }

    public void addCardClick(View v) {
        Intent i = new Intent(this, EnterCardDetailsActivity.class);
        startActivityForResult(i, REQUEST_CODE_ADD_CARD);
    }


    public void getMoreCreditClick(View v) {

        AlertDialog dialog = new AlertDialog.Builder(PaymentActivity.this, R.style.AppCompatAlertDialogStyle)
                .setTitle(R.string.get_more_credit_dialog_title)
                .setMessage(R.string.get_more_credit_dialog_msg)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();
        dialog.show();
    }

    private void addSavedMethodsToView() {
        mSavedMethodLayout.removeAllViews();
        for (int i = 0; i < mPayments.size(); i++) {
            PaymentInfo paymentMethod = mPayments.get(i);
            mSavedMethodLayout.addView(inflateSaveMethodRow(i, paymentMethod));
        }
        if (mPayments.size() == 0) {
            mNoPaymentMessage.setVisibility(View.VISIBLE);
        } else {
            mNoPaymentMessage.setVisibility(View.GONE);
        }
    }

    private View inflateSaveMethodRow(final int pos, PaymentInfo paymentMethod) {
        final ViewGroup paymentRow = (ViewGroup) getLayoutInflater().inflate(R.layout.saved_payment_methods_row, null);
        ImageView paymentImage = (ImageView) paymentRow.findViewById(R.id.payment_image);
        TextView paymentText = (TextView) paymentRow.findViewById(R.id.payment_text);
        View check = paymentRow.findViewById(R.id.payment_check);
        if (!paymentMethod.mPaypalAccount.equals("")) {
            paymentImage.setImageResource(R.drawable.ic_paypal_card);
            paymentText.setText(paymentMethod.mPaypalAccount);
        } else {
            PaymentHelper.setCardIcon(paymentImage, paymentMethod.mCardType);
            paymentText.setText(paymentMethod.mCardType.name + "-" + paymentMethod.mLastFourDigits);
        }
        if (paymentMethod.mPrimary) {
            mSelectedPaymentPos = pos;
            mSelectedPaymentInfo = paymentMethod;
            check.setVisibility(View.VISIBLE);
        } else {
            check.setVisibility(View.GONE);
        }
        paymentRow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectPaymentMethod(pos, true);
            }
        });
        return paymentRow;
    }

    private void selectPaymentMethod(int pos, boolean anim) {
        if (mSelectedPaymentPos == pos) return;

        //get payment id and send to server
        MDApiManager.setPrimaryPayment(mPayments.get(pos).mPaymentId);

        if (mSelectedPaymentPos != -1) {
            View v = mSavedMethodLayout.getChildAt(mSelectedPaymentPos);
            View check = v.findViewById(R.id.payment_check);
            check.setVisibility(View.GONE);
            if (mSelectedPaymentInfo != null) mSelectedPaymentInfo.mPrimary = false;
        }

        View v = mSavedMethodLayout.getChildAt(pos);
        View check = v.findViewById(R.id.payment_check);
        check.setVisibility(View.VISIBLE);
        mSelectedPaymentInfo = mPayments.get(pos);
        mSelectedPaymentInfo.mPrimary = true;
        if (anim) {
            ScaleAnimation animation = new ScaleAnimation(0, 1, 0, 1, check.getWidth() / 2, check.getHeight() / 2);
            animation.setDuration(200);
            check.startAnimation(animation);
        }
        mSelectedPaymentPos = pos;
    }

    private PayPalOAuthScopes getOauthScopes() {
        /* create the set of required scopes
         * Note: see https://developer.paypal.com/docs/integration/direct/identity/attributes/ for mapping between the
         * attributes you select for this app in the PayPal developer portal and the scopes required here.
         */
        Set<String> scopes = new HashSet<String>(
                Arrays.asList(PayPalOAuthScopes.PAYPAL_SCOPE_EMAIL));
        return new PayPalOAuthScopes(scopes);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_PROFILE_SHARING) {
            if (resultCode == Activity.RESULT_OK) {
                PayPalAuthorization auth =
                        data.getParcelableExtra(PayPalProfileSharingActivity.EXTRA_RESULT_AUTHORIZATION);
//                PayPalOAuthScopes scopes =
//                        data.getParcelableExtra(PayPalProfileSharingActivity.EXTRA_REQUESTED_SCOPES);

                if (auth != null) {
                    String authorization_code = auth.getAuthorizationCode();
                    sendAuthorizationToServer(authorization_code);
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Log.i("PaymentActivity", "The user canceled.");
            } else if (resultCode == PayPalFuturePaymentActivity.RESULT_EXTRAS_INVALID) {
                Log.i(
                        "PaymentActivity",
                        "Probably the attempt to previously start the PayPalService had an invalid PayPalConfiguration. Please see the docs.");
            }
        } else if (requestCode == REQUEST_CODE_ADD_CARD) {
            if (resultCode == Activity.RESULT_OK) {
                if (mSelectedPaymentInfo != null) mSelectedPaymentInfo.mPrimary = false;
                addSavedMethodsToView();
            }
        }
    }

    private void sendAuthorizationToServer(String authorizationCode) {

        mProgressDialog = ProgressDialog.show(PaymentActivity.this, null, getString(R.string.loading_adding_payment));
        MDApiManager.addPayPalPayments(PayPalConfiguration.getApplicationCorrelationId(this), authorizationCode, new MDReponseListenerImpl<JSONObject>() {

            @Override
            public void onMDSuccessResponse(JSONObject object) {
                super.onMDSuccessResponse(object);
                if (mSelectedPaymentInfo != null) mSelectedPaymentInfo.mPrimary = false;
                addSavedMethodsToView();
            }
        });
    }

    @Override
    public void onDestroy() {
        stopService(new Intent(this, PayPalService.class));
        super.onDestroy();
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_payment;
    }
}
