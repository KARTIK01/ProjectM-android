package com.mickledeals.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.mickledeals.R;
import com.mickledeals.utils.Constants;
import com.mickledeals.utils.PreferenceHelper;
import com.mickledeals.utils.Utils;

import java.util.Locale;

/**
 * Created by Nicky on 11/28/2014.
 */
public class SettingsActivity extends BaseActivity {

    private TextView mLanguageText;
    private int language = Constants.LANG_ENG;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mLanguageText = (TextView) findViewById(R.id.languageText);
        if (Utils.mCurrentLocale.getLanguage().equals("zh")) language = Constants.LANG_CHT;
        mLanguageText.setText(getResources().getStringArray(R.array.language_list)[language]);
        findViewById(R.id.languageRow).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                            }
                        })
                        .create()
                        .show();
            }
        });
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_settings;
    }
}
