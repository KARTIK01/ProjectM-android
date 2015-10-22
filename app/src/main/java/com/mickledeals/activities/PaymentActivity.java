package com.mickledeals.activities;

import android.app.Activity;
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
import android.widget.Toast;

import com.mickledeals.R;
import com.mickledeals.datamodel.DataListModel;
import com.mickledeals.datamodel.PaymentInfo;
import com.mickledeals.utils.MDApiManager;
import com.mickledeals.utils.PaymentHelper;
import com.paypal.android.sdk.payments.PayPalAuthorization;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalFuturePaymentActivity;
import com.paypal.android.sdk.payments.PayPalOAuthScopes;
import com.paypal.android.sdk.payments.PayPalProfileSharingActivity;
import com.paypal.android.sdk.payments.PayPalService;

import org.json.JSONException;

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
            .merchantPrivacyPolicyUri(Uri.parse("http://www.mickledeals.com/business"))
                    .merchantUserAgreementUri(Uri.parse("http://www.mickledeals.com/business"));
    private static final int REQUEST_CODE_PROFILE_SHARING = 1;
    private static final int REQUEST_CODE_ADD_CARD = 10;

    private View mNoPaymentMessage;
    private TextView mCredit;
    private LinearLayout mSavedMethodLayout;
    private List<PaymentInfo> mPayments = DataListModel.getInstance().getPaymentList();
    private int mUsingMethodId = 1;
    private int mSelectedPaymentRowIndex = -1;

    private class PaymentMethod {
        public int methodId;
        public boolean isUsing; //may not need to use, propose api: return a id to indicate is using
        public String displayString;

        PaymentMethod(int methodId, boolean isUsing, String displayString) {
            this.methodId = methodId;
            this.isUsing = isUsing;
            this.displayString = displayString;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null && savedInstanceState.getBoolean("isKilled")) return;

//        Intent intent = new Intent(this, PayPalService.class);
//        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
//        startService(intent);
        mNoPaymentMessage = findViewById(R.id.noPaymentMessage);
        mCredit = (TextView) findViewById(R.id.mickleCredit);
        mSavedMethodLayout = (LinearLayout) findViewById(R.id.savedMethodLayout);

        if (DataListModel.getInstance().mUpdatedPayment) {
            addSavedMethodsToView();
            mCredit.setText(getString(R.string.your_mickle_credit, DataListModel.getInstance().getMickleCredits()));
        } else {
            MDApiManager.getPayments(new MDReponseListenerImpl<Void>() {
                @Override
                public void onMDSuccessResponse(Void object) {
                    super.onMDSuccessResponse(object);
                    addSavedMethodsToView();
                    mCredit.setText(getString(R.string.your_mickle_credit, DataListModel.getInstance().getMickleCredits()));
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
            mSavedMethodLayout.addView(inflateSaveMethodRow(paymentMethod));
        }
        if (mPayments.size() == 0) {
            mNoPaymentMessage.setVisibility(View.VISIBLE);
        } else {
            mNoPaymentMessage.setVisibility(View.GONE);
        }
    }

    private View inflateSaveMethodRow(PaymentInfo paymentMethod) {
        final ViewGroup paymentRow = (ViewGroup) getLayoutInflater().inflate(R.layout.saved_payment_methods_row, null);
        ImageView paymentImage = (ImageView) paymentRow.findViewById(R.id.payment_image);
        TextView paymentText = (TextView) paymentRow.findViewById(R.id.payment_text);
        if (!paymentMethod.mPaypalAccount.equals("")) {
            paymentImage.setImageResource(R.drawable.ic_paypal_card);
            paymentText.setText(paymentMethod.mPaypalAccount);
        } else {
            PaymentHelper.setCardIcon(paymentImage, paymentMethod.mCardType);
            paymentText.setText(paymentMethod.mCardType.name + "-" + paymentMethod.mLastFourDigits);
        }
        paymentRow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectPaymentMethod(mSavedMethodLayout.indexOfChild(v), true);
            }
        });
        return paymentRow;
    }

    private void selectPaymentMethod(int index, boolean anim) {
        if (mSelectedPaymentRowIndex == index) return;

        //get payment id and send to server

        if (mSelectedPaymentRowIndex != -1) {
            View v = mSavedMethodLayout.getChildAt(mSelectedPaymentRowIndex);
            View check = v.findViewById(R.id.payment_check);
            check.setVisibility(View.GONE);
        }

        View v = mSavedMethodLayout.getChildAt(index);
        View check = v.findViewById(R.id.payment_check);
        check.setVisibility(View.VISIBLE);
        if (anim) {
            ScaleAnimation animation = new ScaleAnimation(0, 1, 0, 1, check.getWidth() / 2, check.getHeight() / 2);
            animation.setDuration(200);
            check.startAnimation(animation);
        }
        mSelectedPaymentRowIndex = index;
    }

    private PayPalOAuthScopes getOauthScopes() {
        /* create the set of required scopes
         * Note: see https://developer.paypal.com/docs/integration/direct/identity/attributes/ for mapping between the
         * attributes you select for this app in the PayPal developer portal and the scopes required here.
         */
        Set<String> scopes = new HashSet<String>(
                Arrays.asList(PayPalOAuthScopes.PAYPAL_SCOPE_EMAIL) );
        return new PayPalOAuthScopes(scopes);
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_PROFILE_SHARING) {
            if (resultCode == Activity.RESULT_OK) {
                PayPalAuthorization auth =
                        data.getParcelableExtra(PayPalProfileSharingActivity.EXTRA_RESULT_AUTHORIZATION);
                PayPalOAuthScopes scopes =
                        data.getParcelableExtra(PayPalProfileSharingActivity.EXTRA_REQUESTED_SCOPES);

                if (auth != null) {
                    try {
                        Log.i("PaymentActivity", auth.toJSONObject().toString(4));

                        String authorization_code = auth.getAuthorizationCode();
                        Log.i("PaymentActivity", authorization_code);

                        sendAuthorizationToServer(auth);
                        Toast.makeText(
                                getApplicationContext(),
                                "Profile Sharing code received from PayPal", Toast.LENGTH_LONG)
                                .show();

                    } catch (JSONException e) {
                        Log.e("PaymentActivity", "an extremely unlikely failure occurred: ", e);
                    }
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
                addSavedMethodsToView();
            }
        }
    }

    private void sendAuthorizationToServer(PayPalAuthorization authorization) {

        //send auto code and return email address
    }

    @Override
    public void onDestroy() {
//        stopService(new Intent(this, PayPalService.class));
        super.onDestroy();
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_payment;
    }
}
