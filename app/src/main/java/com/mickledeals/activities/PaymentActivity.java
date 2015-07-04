package com.mickledeals.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mickledeals.R;
import com.mickledeals.utils.PaymentHelper;
import com.paypal.android.sdk.payments.PayPalAuthorization;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalFuturePaymentActivity;
import com.paypal.android.sdk.payments.PayPalOAuthScopes;
import com.paypal.android.sdk.payments.PayPalProfileSharingActivity;
import com.paypal.android.sdk.payments.PayPalService;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import io.card.payment.CardType;

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

    private LinearLayout mSavedMethodLayout;
    private List<PaymentMethod> mPayments = new ArrayList<PaymentMethod>();
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

        mSavedMethodLayout = (LinearLayout) findViewById(R.id.savedMethodLayout);
        mPayments.add(new PaymentMethod(1, false, "Visa-1234"));
        mPayments.add(new PaymentMethod(2, false, "MasterCard-1234"));
        mPayments.add(new PaymentMethod(3, false, "AmEx-1234"));
        mPayments.add(new PaymentMethod(4, false, "Discover-1234"));
        mPayments.add(new PaymentMethod(5, false, "jackywong@gmail.com"));

        addSavedMethodsToView();
        selectPaymentMethod(0, false);
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

        AlertDialog dialog = new AlertDialog.Builder(PaymentActivity.this)
                .setTitle(R.string.get_more_credit_dialog_title)
                .setMessage(R.string.get_more_credit_dialog_msg)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();
        WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
        lp.dimAmount = 0.6F;
        dialog.getWindow().setAttributes(lp);
        dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        dialog.show();
    }

    private void addSavedMethodsToView() {
        mSavedMethodLayout.removeAllViews();
        for (int i = 0; i < mPayments.size(); i++) {
            PaymentMethod paymentMethod = mPayments.get(i);
            mSavedMethodLayout.addView(inflateSaveMethodRow(paymentMethod));
        }
    }

    private View inflateSaveMethodRow(PaymentMethod paymentMethod) {
        final ViewGroup paymentRow = (ViewGroup) getLayoutInflater().inflate(R.layout.saved_payment_methods_row, null);
        ImageView paymentImage = (ImageView) paymentRow.findViewById(R.id.payment_image);
        String str = paymentMethod.displayString;
        if (str.contains("@")) paymentImage.setImageResource(R.drawable.ic_paypal_card);
        else {
            String cardTypeStr = str.substring(0, str.indexOf('-'));
            PaymentHelper.setCardIcon(paymentImage, CardType.fromString(cardTypeStr));
        }
        TextView paymentText = (TextView) paymentRow.findViewById(R.id.payment_text);
        paymentText.setText(str);
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
                PaymentMethod method = new PaymentMethod(12, false, data.getStringExtra("cardDisplayString"));
                mPayments.add(0, method);
//                addSavedMethodsToView();
                mSavedMethodLayout.addView(inflateSaveMethodRow(method), 0);
                mSelectedPaymentRowIndex++; //add one so that it will find the correct row to remove check mark
                selectPaymentMethod(0, false);
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
