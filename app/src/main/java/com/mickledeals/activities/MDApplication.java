package com.mickledeals.activities;

import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;
import android.util.DisplayMetrics;

import com.facebook.FacebookSdk;
import com.mickledeals.R;
import com.mickledeals.bean.NavMenuItem;
import com.mickledeals.fragments.HomeFragment;
import com.mickledeals.fragments.MyCouponsFragment;
import com.mickledeals.fragments.SavedCouponsFragment;
import com.mickledeals.utils.Constants;
import com.mickledeals.utils.MDApiManager;
import com.mickledeals.utils.MDLoginManager;
import com.mickledeals.utils.PreferenceHelper;

import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by Nicky on 5/23/2015.
 */
public class MDApplication extends Application {

    public static Context sAppContext;
    public static ArrayList<NavMenuItem> sNavMenuList = new ArrayList<NavMenuItem>();
    public static int sDeviceWidth;
    public static int sDeviceHeight;
    public static float sDeviceDensity;
    public static Locale sCurrentLocale = Locale.ENGLISH;

    @Override
    public void onCreate() {
        super.onCreate();
        sAppContext = this;
        initLocale();
        initDeviceInfo();
        initMenuItems();
        MDApiManager.initVolley();
        FacebookSdk.sdkInitialize(getApplicationContext());
        MDLoginManager.initFromPreference(getApplicationContext());
    }

    public static void initDeviceInfo() {
        DisplayMetrics displayMetrics = sAppContext.getResources().getDisplayMetrics();
        sDeviceWidth = displayMetrics.widthPixels;
        sDeviceHeight = displayMetrics.heightPixels;
        sDeviceDensity = displayMetrics.density;

    }

    public static void initLocale() {
        int language = PreferenceHelper.getPreferenceValueInt(sAppContext, "language", -1);
        if (language == Constants.LANG_ENG) {
            sCurrentLocale = Locale.ENGLISH;
        } else if (language == Constants.LANG_CHT) {
            sCurrentLocale = Locale.CHINESE;
        } else { //use default locale
            sCurrentLocale = Locale.getDefault();
            return;
        }
        Configuration config = new Configuration();
        config.locale = sCurrentLocale;
        sAppContext.getResources().updateConfiguration(config,
                sAppContext.getResources().getDisplayMetrics());
    }

    public static void initMenuItems() {
        sNavMenuList.clear();
        sNavMenuList.add(new NavMenuItem(HomeFragment.class, R.string.menu_home, R.drawable.ic_home));
        sNavMenuList.add(new NavMenuItem(MyCouponsFragment.class, R.string.menu_my_deals, R.drawable.ic_coupons));
        sNavMenuList.add(new NavMenuItem(SavedCouponsFragment.class, R.string.menu_saved_deals, R.drawable.ic_save));
//        sNavMenuList.add(new NavMenuItem(RandomCouponsFragment.class, R.string.menu_random, R.drawable.ic_random));
        sNavMenuList.add(new NavMenuItem(null, 0, 0)); //divider
        sNavMenuList.add(new NavMenuItem(NotificationActivity.class, R.string.menu_notification, R.drawable.ic_bell));
        sNavMenuList.add(new NavMenuItem(PaymentActivity.class, R.string.menu_account, R.drawable.ic_payment));
//        sNavMenuList.add(new NavMenuItem(RedeemActivity.class, R.string.menu_redeem, R.drawable.ic_gift));
        sNavMenuList.add(new NavMenuItem(SettingsActivity.class, R.string.menu_settings, R.drawable.ic_setting));
        sNavMenuList.add(new NavMenuItem(null, 0, 0)); //divider
        sNavMenuList.add(new NavMenuItem(null, R.string.menu_faq, 0));
        sNavMenuList.add(new NavMenuItem(null, R.string.menu_promote, 0));
        sNavMenuList.add(new NavMenuItem(null, R.string.menu_feedback, 0));
        sNavMenuList.add(new NavMenuItem(null, R.string.menu_rate, 0));
        sNavMenuList.add(new NavMenuItem(null, R.string.menu_about, 0));
        sNavMenuList.add(new NavMenuItem(null, R.string.menu_terms, 0));
    }
}
