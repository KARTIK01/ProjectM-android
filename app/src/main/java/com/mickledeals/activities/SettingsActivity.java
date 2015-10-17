package com.mickledeals.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.TextView;

import com.mickledeals.R;
import com.mickledeals.utils.Constants;
import com.mickledeals.utils.MDLoginManager;
import com.mickledeals.utils.PreferenceHelper;
import com.mickledeals.utils.Utils;

/**
 * Created by Nicky on 11/28/2014.
 */
public class SettingsActivity extends SwipeDismissActivity {

    private TextView mLanguageText;
    private TextView mLogin;
    private TextView mLogout;
    private TextView mChangePwd;
    private int language = Constants.LANG_ENG;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null && savedInstanceState.getBoolean("isKilled")) return;

        mLogin = (TextView) findViewById(R.id.login);
        mLogout = (TextView) findViewById(R.id.logOut);
        mChangePwd = (TextView) findViewById(R.id.changePassword);

        setLoginBtnState();

        mLanguageText = (TextView) findViewById(R.id.languageText);
        if (Utils.isChineseLocale()) language = Constants.LANG_CHT;
        mLanguageText.setText(getResources().getStringArray(R.array.language_list)[language]);
    }

    private void setLoginBtnState() {
        if (MDLoginManager.isLogin()) {
            mLogin.setVisibility(View.GONE);
            mLogout.setVisibility(View.VISIBLE);
            mChangePwd.setVisibility(MDLoginManager.isFbLogin() ? View.GONE : View.VISIBLE);
        } else {
            mLogin.setVisibility(View.VISIBLE);
            mLogout.setVisibility(View.GONE);
            mChangePwd.setVisibility(View.GONE);
        }
    }

    public void languageRowClick(View v) {
        AlertDialog dialog = new AlertDialog.Builder(SettingsActivity.this, R.style.AppCompatAlertDialogStyle)
                .setSingleChoiceItems(getResources().getStringArray(R.array.language_list), language, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == Constants.LANG_ENG) {
//                            Locale.setDefault(Locale.ENGLISH);
                            PreferenceHelper.savePreferencesInt(SettingsActivity.this, "language", Constants.LANG_ENG);
                        } else if (which == Constants.LANG_CHT) {
//                            Locale.setDefault(Locale.CHINESE);
                            PreferenceHelper.savePreferencesInt(SettingsActivity.this, "language", Constants.LANG_CHT);
                        }
                        mLanguageText.setText(getResources().getStringArray(R.array.language_list)[which]);
                        language = which;
                        MDApplication.initLocale();
                        dialog.dismiss();
                        Utils.restartApp(SettingsActivity.this);
//                        android.os.Process.killProcess(android.os.Process.myPid());
                    }
                })
                .create();
        dialog.show();
    }

    public void loginClick(View v) {
        MDLoginManager.loginIfNecessary(this, new MDLoginManager.LoginCallback() {
            @Override
            public void onLoginSuccess() {
                setLoginBtnState();
            }
        });
    }

    public void signoutClick(View v) {
        String msg = getString(R.string.log_out_confirm, MDLoginManager.getNameOrEmail());

        AlertDialog dialog = new AlertDialog.Builder(SettingsActivity.this, R.style.AppCompatAlertDialogStyle)
                .setMessage(msg)
                .setPositiveButton(R.string.dialog_yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        MDLoginManager.logout(SettingsActivity.this);
                        Utils.restartApp(SettingsActivity.this);
//                        android.os.Process.killProcess(android.os.Process.myPid());

                    }
                })
                .setNegativeButton(R.string.dialog_no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();
        dialog.show();
    }

    public void changePasswordClick(View v) {
        Intent i = new Intent(this, ChangePasswordActivity.class);
        startActivity(i);
    }

    public void clearSearchClick(View v) {
        AlertDialog dialog = new AlertDialog.Builder(SettingsActivity.this, R.style.AppCompatAlertDialogStyle)
                .setMessage(R.string.clear_search_history_confirm)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setNegativeButton(R.string.dialog_no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();
        dialog.show();
    }

    public void clearUsedCouponClick(View v) {
        AlertDialog dialog = new AlertDialog.Builder(SettingsActivity.this, R.style.AppCompatAlertDialogStyle)
                .setMessage(R.string.clear_used_coupon_history_confirm)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setNegativeButton(R.string.dialog_no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();
        dialog.show();
    }


    @Override
    protected int getLayoutResource() {
        return R.layout.activity_settings;
    }
}
