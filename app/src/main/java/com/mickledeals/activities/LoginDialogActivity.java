package com.mickledeals.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.mickledeals.R;
import com.mickledeals.utils.DLog;
import com.mickledeals.utils.MDApiManager;
import com.mickledeals.utils.MDLoginManager;
import com.mickledeals.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

/**
 * Created by Nicky on 5/17/2015.
 */
public class LoginDialogActivity extends DialogSwipeDismissActivity {

    private LoginButton mLoginButton;
//    private ProfileTracker mProfileTracker;
//    private AccessTokenTracker mAccessTokenTracker;
    CallbackManager mCallbackManager;
    private EditText mEmail;
    private EditText mPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        if (savedInstanceState != null && savedInstanceState.getBoolean("isKilled")) return;

        mEmail = (EditText) findViewById(R.id.email);
        mPassword = (EditText) findViewById(R.id.password);
        mCallbackManager = CallbackManager.Factory.create();
        mLoginButton = (LoginButton) findViewById(R.id.login_button);
        mLoginButton.setText(R.string.login_with_fb);
        mLoginButton.setReadPermissions(Arrays.asList("public_profile", "email", "user_birthday"));
        mLoginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                finish();
//                overridePendingTransition(0, R.anim.translate_anim_exit); //cannot use because bg not fade in
                MDLoginManager.getInstance().setAccessToken(loginResult.getAccessToken());
                String fbAuthToken = loginResult.getAccessToken().getToken();
                DLog.d(LoginDialogActivity.this, "fb login onSuccess token = " + fbAuthToken);

                GraphRequest.newMeRequest(
                        loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject me, GraphResponse response) {
                                if (response.getError() != null) {
                                    DLog.e(LoginDialogActivity.this, response.getError().toString());
                                } else {
                                    String email = me.optString("email");
                                    String id = me.optString("id");
                                    String name = me.optString("name");
                                    String birthday = me.optString("birthday");
                                    String gender = me.optString("gender");
//                                    MDLoginManager.setUserInfo(LoginDialogActivity.this, id, email, name);
                                    DLog.d(LoginDialogActivity.this, "email = " + email + " id = " + id + "name = "
                                    + name + "birthday = " + birthday + "gender = " + gender);
                                    // send email and id to your web server


                                    //wait for server response to ensure server received user id
                                    //server needs error handling when user id does not exist in database
                                    //while the app is using the user id as params for other request
                                    MDLoginManager.onLoginSuccess();

                                }
                            }
                        }).executeAsync();

            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException e) {
                DLog.d(LoginDialogActivity.this, "on Error, exception = " + e.toString());
            }
        });

//        mProfileTracker = new ProfileTracker() {
//            @Override
//            protected void onCurrentProfileChanged(
//                    Profile oldProfile,
//                    Profile currentProfile) {
//                DLog.d(LoginDialogActivity.this, "onCurrentProfileChanged" + currentProfile.getName() + " " + currentProfile.getId());
//                currentProfile.getName();
//                currentProfile.getId();
//            }
//        };

//        mAccessTokenTracker = new AccessTokenTracker() {
//            @Override
//            protected void onCurrentAccessTokenChanged(
//                    AccessToken oldAccessToken,
//                    AccessToken currentAccessToken) {
//
//                MDLoginManager.getInstance().setAccessToken(currentAccessToken);
//                if (currentAccessToken != null) {
//                    String fbAuthToken = currentAccessToken.getToken();
//                    String fbUserID = currentAccessToken.getUserId();
//                    DLog.d(LoginDialogActivity.this, "User id: " + fbUserID);
//                    DLog.d(LoginDialogActivity.this, "Access token is: " + fbAuthToken);
//                }
//
//            }
//        };


        float fbIconScale = 1.8F;
        Drawable drawable = getResources().getDrawable(
                com.facebook.R.drawable.com_facebook_button_icon);
        drawable.setBounds(0, 0, (int) (drawable.getIntrinsicWidth() * fbIconScale),
                (int) (drawable.getIntrinsicHeight() * fbIconScale));
        mLoginButton.setCompoundDrawables(drawable, null, null, null);
        int paddingInPixels = Utils.getPixelsFromDip(14f, getResources());
        int paddingLeftRightInPixels = Utils.getPixelsFromDip(12f, getResources());
        mLoginButton.setPadding(paddingLeftRightInPixels, paddingInPixels, paddingLeftRightInPixels, paddingInPixels);
        mLoginButton.setCompoundDrawablePadding(paddingInPixels);
    }

    public void forgotPassword(View v) {
        Intent i = new Intent(this, ForgotPasswordActivity.class);
        String emailStr = mEmail.getText().toString().trim();
        if (validateEmail(emailStr)) {
            i.putExtra("email", emailStr);
        }
        startActivity(i);
    }

    public void signupBtnClick(View v) {
        validateAndSubmit(true);
    }

    public void loginBtnClick(View v) {
        validateAndSubmit(false);
    }

    private void validateAndSubmit(boolean signup) {
        final String emailStr = mEmail.getText().toString().trim();
        String passwordStr = mPassword.getText().toString().trim();
        if (!validateEmail(emailStr)) {
            Toast.makeText(this, getString(R.string.invalid_email), Toast.LENGTH_SHORT).show();
            return;
        } else if (passwordStr.length() < 6) {
            Toast.makeText(this, getString(R.string.invalid_password), Toast.LENGTH_SHORT).show();
            return;
        } else {
            final ProgressDialog pDialog = ProgressDialog.show(this, null, getString(R.string.loading_login));
            if (signup) {
                MDApiManager.registerUserWithEmail(emailStr, passwordStr, new MDApiManager.MDResponseListener<JSONObject>() {
                    @Override
                    public void onMDSuccessResponse(JSONObject object) {
                        int id = 0;
                        try {
                            id = object.getInt("id");
                            // get promotion object from json
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        MDLoginManager.setUserInfo(LoginDialogActivity.this, id, emailStr, null);
                        MDLoginManager.onLoginSuccess();
                        finish();
                        pDialog.dismiss();
                    }

                    @Override
                    public void onMDNetworkErrorResponse(String errorMessage) {
                        Utils.showNetworkErrorDialog(LoginDialogActivity.this);
                        pDialog.dismiss();
                    }

                    @Override
                    public void onMDErrorResponse(String errorMessage) {
                        int errorMessageRes = R.string.response_error_message_unknown;
                        if (errorMessage != null) {
                            if (errorMessage.equals("DUPLICATE_USER")) {
                                errorMessageRes = R.string.user_already_registered;
                            }
                        }
                        Utils.showAlertDialog(LoginDialogActivity.this, R.string.response_error_title, errorMessageRes);
                        pDialog.dismiss();
                    }
                });
            } else {
                MDApiManager.loginUserWithEmail(emailStr, passwordStr, new MDApiManager.MDResponseListener<Integer>() {
                    @Override
                    public void onMDSuccessResponse(Integer id) {

                        MDLoginManager.setUserInfo(LoginDialogActivity.this, id, emailStr, null);
                        MDLoginManager.onLoginSuccess();
                        finish();
                        pDialog.dismiss();
                    }

                    @Override
                    public void onMDNetworkErrorResponse(String errorMessage) {
                        Utils.showNetworkErrorDialog(LoginDialogActivity.this);
                        pDialog.dismiss();
                    }

                    @Override
                    public void onMDErrorResponse(String errorMessage) {
                        int errorMessageRes = R.string.response_error_message_unknown;
                        if (errorMessage != null) {
                            if (errorMessage.equals("INVLIDA_LOGIN")) {
                                errorMessageRes = R.string.user_invalid_login;
                            }
                        }
                        Utils.showAlertDialog(LoginDialogActivity.this, R.string.response_error_title, errorMessageRes);
                        pDialog.dismiss();
                    }
                });
            }

        }
    }

    private static boolean validateEmail(String emailText) {
        String emailRegex = "[A-Z0-9a-z._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}";
        return emailText.matches(emailRegex);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        mProfileTracker.stopTracking();
//        if (mAccessTokenTracker != null) mAccessTokenTracker.stopTracking();
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.login_dialog;
    }


}
