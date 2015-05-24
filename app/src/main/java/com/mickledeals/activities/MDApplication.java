package com.mickledeals.activities;

import android.app.Application;

import com.mickledeals.utils.PreferenceHelper;
import com.mickledeals.utils.Utils;

/**
 * Created by Nicky on 5/23/2015.
 */
public class MDApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        int language = PreferenceHelper.getPreferenceValueInt(this, "language", -1);
        Utils.setLocaleWithLang(language, getBaseContext());
    }
}
