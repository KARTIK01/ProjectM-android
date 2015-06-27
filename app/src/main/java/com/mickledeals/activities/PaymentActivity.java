package com.mickledeals.activities;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mickledeals.R;
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

    private LinearLayout mSavedMethodLayout;
    private List<String> mPayments = new ArrayList<String>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null && savedInstanceState.getBoolean("isKilled")) return;

//        Intent intent = new Intent(this, PayPalService.class);
//        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
//        startService(intent);

        mSavedMethodLayout = (LinearLayout) findViewById(R.id.savedMethodLayout);
        mPayments.add("VISA-1234");
//        mPayments.add("jackywong@gmail.com");

        addSavedMethodsToView();
    }

    public void addPayPalClick(View v) {
        Intent intent = new Intent(this, PayPalProfileSharingActivity.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
        intent.putExtra(PayPalProfileSharingActivity.EXTRA_REQUESTED_SCOPES, getOauthScopes());
        startActivityForResult(intent, REQUEST_CODE_PROFILE_SHARING);
    }

    public void addCardClick(View v) {

    }

    public void getMoreCreditClick(View v) {

    }

    private void addSavedMethodsToView() {
        for (String str : mPayments) {
            View paymentRow = getLayoutInflater().inflate(R.layout.saved_payment_methods_row, null);
            ImageView paymentImage = (ImageView) paymentRow.findViewById(R.id.payment_image);
            if (str.contains("VISA")) paymentImage.setImageResource(R.drawable.ic_visa);
            else paymentImage.setImageResource(R.drawable.ic_paypal_card);
            TextView paymentText = (TextView) paymentRow.findViewById(R.id.payment_text);
            paymentText.setText(str);
            mSavedMethodLayout.addView(paymentRow);
        }
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
