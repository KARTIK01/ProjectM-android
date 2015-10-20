package com.mickledeals.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphRequestAsyncTask;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.mickledeals.R;
import com.mickledeals.utils.DLog;
import com.mickledeals.utils.MDApiManager;

import java.util.Arrays;

/**
 * Created by Nicky on 5/17/2015.
 */
public class SuccessDialogActivity extends DialogSwipeDismissActivity {
    CallbackManager mCallbackManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null && savedInstanceState.getBoolean("isKilled")) return;

        if (getIntent().getBooleanExtra("pay", false)) {
            findViewById(R.id.sharePurchaseCouponMsg).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.shareFreeCouponMsg).setVisibility(View.VISIBLE);
        }

        mCallbackManager = CallbackManager.Factory.create();
        mProgressBar.getIndeterminateDrawable().setColorFilter(
                getResources().getColor(R.color.white),
                android.graphics.PorterDuff.Mode.SRC_IN);
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_success_purchase;
    }

    public void shareBtnClick(View v) {
        mProgressBar.setVisibility(View.VISIBLE);
        if (AccessToken.getCurrentAccessToken() != null && !AccessToken.getCurrentAccessToken().isExpired()
                && AccessToken.getCurrentAccessToken().getPermissions().contains("publish_actions")) {
            share();
        } else {

            LoginManager.getInstance().registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
                @Override
                public void onSuccess(LoginResult loginResult) {
                    DLog.d(this, "facebook login success");
                    share();
                }

                @Override
                public void onCancel() {
                    mProgressBar.setVisibility(View.GONE);
                    DLog.d(this, "facebook login cancel");
                }

                @Override
                public void onError(FacebookException exception) {
                    DLog.d(this, "facebook login error");
                    mProgressBar.setVisibility(View.GONE);
                    exception.printStackTrace();
                }
            });
            LoginManager.getInstance().logInWithPublishPermissions(this, Arrays.asList("publish_actions"));
        }
    }

    public void share() {
//        Set<String> permissions = AccessToken.getCurrentAccessToken().getPermissions();
        AccessToken accessToken = AccessToken.getCurrentAccessToken();

        String description = getIntent().getStringExtra("coupon_description");
        String storeName = getIntent().getStringExtra("store_name");
        double price = getIntent().getDoubleExtra("price", 0);
        String message = getString(R.string.share_fb_message) + "\n\n" + description + "\n"
                + storeName + "\n\n" + getString(R.string.share_msg);

        Bundle postParams = new Bundle();
        postParams.putString("message", message);
        postParams.putString("link", "https://play.google.com/store/apps/details?id=com.cycon.macaufood");

        GraphRequest request = new GraphRequest(accessToken, "me/feed", postParams, HttpMethod.POST, new GraphRequest.Callback() {
            @Override
            public void onCompleted(GraphResponse graphResponse) {
                mProgressBar.setVisibility(View.GONE);
                if (graphResponse.getError() != null && graphResponse.getError().getErrorMessage() != null) {
                    Toast.makeText(SuccessDialogActivity.this, graphResponse.getError().getErrorMessage(), Toast.LENGTH_LONG).show();
                } else {
                    boolean isPay =getIntent().getBooleanExtra("pay", false);

                    Toast.makeText(SuccessDialogActivity.this, isPay ? R.string.share_to_fb_purchase_succeed
                            : R.string.share_to_fb_free_succeed, Toast.LENGTH_LONG).show();
                    finish();
                    MDApiManager.redeemPromotion(isPay ? "k7sq1m" : "7ck2a9");
                }

            }
        });

        new GraphRequestAsyncTask(request).execute();
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

}
