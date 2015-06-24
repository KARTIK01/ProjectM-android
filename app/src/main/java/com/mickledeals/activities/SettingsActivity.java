package com.mickledeals.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.mickledeals.R;
import com.mickledeals.utils.Constants;
import com.mickledeals.utils.MDLoginManager;
import com.mickledeals.utils.PreferenceHelper;
import com.mickledeals.utils.Utils;

import java.util.Locale;

/**
 * Created by Nicky on 11/28/2014.
 */
public class SettingsActivity extends BaseActivity {

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
        if (Utils.mCurrentLocale.getLanguage().equals("zh")) language = Constants.LANG_CHT;
        mLanguageText.setText(getResources().getStringArray(R.array.language_list)[language]);
    }

    private void setLoginBtnState() {
        if (MDLoginManager.isLogin()) {
            mLogin.setVisibility(View.GONE);
            mLogout.setVisibility(View.VISIBLE);
            mChangePwd.setVisibility(View.VISIBLE);
        } else {
            mLogin.setVisibility(View.VISIBLE);
            mLogout.setVisibility(View.GONE);
            mChangePwd.setVisibility(View.GONE);
        }
    }

    public void languageRowClick(View v) {
        new AlertDialog.Builder(SettingsActivity.this)
                .setSingleChoiceItems(getResources().getStringArray(R.array.language_list), language, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == Constants.LANG_ENG) {
                            Locale.setDefault(Locale.ENGLISH);
                            PreferenceHelper.savePreferencesInt(SettingsActivity.this, "language", Constants.LANG_ENG);
                        } else if (which == Constants.LANG_CHT) {
                            Locale.setDefault(Locale.SIMPLIFIED_CHINESE);
                            PreferenceHelper.savePreferencesInt(SettingsActivity.this, "language", Constants.LANG_CHT);
                        }
                        mLanguageText.setText(getResources().getStringArray(R.array.language_list)[which]);
                        language = which;
                        Utils.setLocaleWithLang(which, SettingsActivity.this.getBaseContext());
                        dialog.dismiss();
                        Intent i = new Intent(SettingsActivity.this, HomeActivity.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(i);
                        android.os.Process.killProcess(android.os.Process.myPid());
                    }
                })
                .create()
                .show();
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
        String msg = getString(R.string.log_out_confirm, MDLoginManager.mEmailAddr);

        new AlertDialog.Builder(SettingsActivity.this)
                .setMessage(msg)
                .setPositiveButton(R.string.log_out_yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        MDLoginManager.logout(SettingsActivity.this);
                        Intent i = new Intent(SettingsActivity.this, HomeActivity.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(i);
                        android.os.Process.killProcess(android.os.Process.myPid());

                    }
                })
                .setNegativeButton(R.string.log_out_no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create()
                .show();
    }

    public void changePasswordClick(View v) {

    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_settings;
    }
}
