package com.mickledeals.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.mickledeals.R;
import com.mickledeals.utils.MDApiManager;

import org.json.JSONObject;

/**
 * Created by Nicky on 5/17/2015.
 */
public class ForgotPasswordActivity extends DialogSwipeDismissActivity {

    private EditText mEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        if (savedInstanceState != null && savedInstanceState.getBoolean("isKilled")) return;

        mEmail = (EditText) findViewById(R.id.email);
        mEmail.setText(getIntent().getStringExtra("email"));
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.forgot_password_dialog;
    }

    public void cancelBtnClick(View v) {
        finish();
    }

    public void submitBtnClick(View v) {
        validateAndSubmit();
    }

    private void validateAndSubmit() {
        String emailStr = mEmail.getText().toString().trim();
        if (!validateEmail(emailStr)) {
            Toast.makeText(this, getString(R.string.invalid_email), Toast.LENGTH_SHORT).show();
            return;
        } else {
            mProgressBar.setVisibility(View.VISIBLE);
            MDApiManager.forgotPassword(emailStr, new MDReponseListenerImpl<JSONObject>() {
                @Override
                public void onMDSuccessResponse(JSONObject object) {
                    super.onMDSuccessResponse(object);
                    Toast.makeText(ForgotPasswordActivity.this, R.string.reset_password_succeed, Toast.LENGTH_LONG).show();
                    finish();
                }
            });
        }
    }

    private static boolean validateEmail(String emailText) {
        String emailRegex = "[A-Z0-9a-z._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}";
        return emailText.matches(emailRegex);
    }
}
