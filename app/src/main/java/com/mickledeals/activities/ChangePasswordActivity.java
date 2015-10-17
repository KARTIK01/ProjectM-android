package com.mickledeals.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.mickledeals.R;
import com.mickledeals.utils.MDApiManager;
import com.mickledeals.utils.MDLoginManager;

/**
 * Created by Nicky on 5/17/2015.
 */
public class ChangePasswordActivity extends DialogSwipeDismissActivity {

    private EditText mOldPassword;
    private EditText mNewPassword;
    private EditText mConfirmNewPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        if (savedInstanceState != null && savedInstanceState.getBoolean("isKilled")) return;

        mOldPassword = (EditText) findViewById(R.id.oldPassword);
        mNewPassword = (EditText) findViewById(R.id.newPassword);
        mConfirmNewPassword = (EditText) findViewById(R.id.confirmNewPassword);

        mProgressBar.getIndeterminateDrawable().setColorFilter(
                getResources().getColor(R.color.white),
                android.graphics.PorterDuff.Mode.SRC_IN);
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.change_password_dialog;
    }

    public void cancelBtnClick(View v) {
        finish();
    }

    public void submitBtnClick(View v) {
        validateAndSubmit();
    }

    private void validateAndSubmit() {
        if (mNewPassword.length() < 6) {
            Toast.makeText(this, getString(R.string.invalid_password), Toast.LENGTH_SHORT).show();
        } else if (!validatePassword()) {
            Toast.makeText(this, getString(R.string.new_password_not_match), Toast.LENGTH_SHORT).show();
            return;
        } else {
            mProgressBar.setVisibility(View.VISIBLE);
            MDApiManager.changePassword(MDLoginManager.mEmailAddr, mOldPassword.getText().toString().trim(),
                    mNewPassword.getText().toString().trim(), new MDReponseListenerImpl<Boolean>() {
                        @Override
                        public void onMDSuccessResponse(Boolean object) {
                            super.onMDSuccessResponse(object);
                            Toast.makeText(ChangePasswordActivity.this, R.string.update_password_succeed, Toast.LENGTH_LONG).show();
                            finish();
                        }

                        @Override
                        public void onMDErrorResponse(String errorMessage) {
                            if (errorMessage != null) {
                                if (errorMessage.equals("Password does not match")) {
                                    onMDErrorResponse(R.string.update_password_wrong_password);
                                    return;
                                }
                            }
                            super.onMDErrorResponse(errorMessage);
                        }

                    });
        }
    }

    private boolean validatePassword() {
        return mNewPassword.getText().toString().trim().equals(mConfirmNewPassword.getText().toString().trim());
    }
}
