package com.mickledeals.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.widget.CardView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.mickledeals.R;
import com.mickledeals.activities.AccountActivity;
import com.mickledeals.activities.DetailsActivity;
import com.mickledeals.activities.NotificationActivity;
import com.mickledeals.activities.RedeemActivity;
import com.mickledeals.activities.SettingsActivity;
import com.mickledeals.bean.NavMenuItem;
import com.mickledeals.datamodel.DataListModel;
import com.mickledeals.fragments.HomeFragment;
import com.mickledeals.fragments.MyCouponsFragment;
import com.mickledeals.fragments.SavedCouponsFragment;
import com.mickledeals.tests.TestDataHolder;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by Nicky on 11/28/2014.
 */
public class Utils {

    public static ArrayList<NavMenuItem> sNavMenuList = new ArrayList<NavMenuItem>();
    private static int sDeviceWidth;
    private static int sDeviceHeight;
    public static Locale mCurrentLocale = Locale.ENGLISH;

    public static void loadMenuItems() {
        sNavMenuList.clear();
        sNavMenuList.add(new NavMenuItem(HomeFragment.class, R.string.menu_home, R.drawable.ic_home));
        sNavMenuList.add(new NavMenuItem(MyCouponsFragment.class, R.string.menu_my_deals, R.drawable.ic_coupons));
        sNavMenuList.add(new NavMenuItem(SavedCouponsFragment.class, R.string.menu_saved_deals, R.drawable.ic_save));
//        sNavMenuList.add(new NavMenuItem(RandomCouponsFragment.class, R.string.menu_random, R.drawable.ic_random));
        sNavMenuList.add(new NavMenuItem(null, 0, 0)); //divider
        sNavMenuList.add(new NavMenuItem(NotificationActivity.class, R.string.menu_notification, R.drawable.ic_bell));
        sNavMenuList.add(new NavMenuItem(AccountActivity.class, R.string.menu_account, R.drawable.ic_payment));
        sNavMenuList.add(new NavMenuItem(RedeemActivity.class, R.string.menu_redeem, R.drawable.ic_gift));
        sNavMenuList.add(new NavMenuItem(SettingsActivity.class, R.string.menu_settings, R.drawable.ic_setting));
        sNavMenuList.add(new NavMenuItem(null, 0, 0)); //divider
        sNavMenuList.add(new NavMenuItem(null, R.string.menu_faq, 0));
        sNavMenuList.add(new NavMenuItem(null, R.string.menu_promote, 0));
        sNavMenuList.add(new NavMenuItem(null, R.string.menu_feedback, 0));
        sNavMenuList.add(new NavMenuItem(null, R.string.menu_rate, 0));
        sNavMenuList.add(new NavMenuItem(null, R.string.menu_about, 0));
        sNavMenuList.add(new NavMenuItem(null, R.string.menu_terms, 0));
    }

    public static int getDeviceWidth(Context context) {
        if (sDeviceWidth == 0) {
            DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
            sDeviceWidth = displayMetrics.widthPixels;
        }
        return sDeviceWidth;
    }

    public static int getDeviceHeight(Context context) {
        if (sDeviceHeight == 0) {
            DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
            sDeviceHeight = displayMetrics.heightPixels;
        }
        return sDeviceHeight;
    }

    public static int getPixelsFromDip(float dips, Resources res) {
        return Math.round(dips * res.getDisplayMetrics().density);
    }

    public static float getDipFromPixels(int pixel, Resources res) {
        return pixel / res.getDisplayMetrics().density;
    }

    public static boolean isChineseLocale() {
        return Utils.mCurrentLocale.getLanguage().equals("zh");
    }

    public static void setLocaleWithLang(int language, Context context) {

        if (language == Constants.LANG_ENG) {
            mCurrentLocale = Locale.ENGLISH;
        } else if (language == Constants.LANG_CHT) {
            mCurrentLocale = Locale.CHINESE;
        } else { //use default locale
            mCurrentLocale = Locale.getDefault();
            return;
        }
        Configuration config = new Configuration();
        config.locale = mCurrentLocale;
        context.getResources().updateConfiguration(config,
                context.getResources().getDisplayMetrics());
    }

    public static List<TestDataHolder> getListFromType(int listType) {
        DataListModel dataModel = DataListModel.getInstance();
        switch (listType) {
            case Constants.TYPE_NEARBY_LIST:
                return dataModel.getNearbyList();
            case Constants.TYPE_FEATURE_SLIDER_LIST:
                return dataModel.getFeatureSliderCouponList();
            case Constants.TYPE_NEW_ADDED_LIST:
                return dataModel.getNewAddedCouponList();
            case Constants.TYPE_BEST_LIST:
                return dataModel.getBestCouponList();
            case Constants.TYPE_SAVED_LIST:
                return dataModel.getSavedList();
            case Constants.TYPE_BOUGHT_LIST:
                return dataModel.getBoughtList();
            case Constants.TYPE_MORE_COUPONS_LIST:
                return dataModel.getMoreCouponsList();
            case Constants.TYPE_SEARCH_RESULT_LIST:
                return dataModel.getSearchResultList();
            default:
                return null;
        }
    }

    public static int getStringResFromType(int listType) {
        switch (listType) {
            case Constants.TYPE_NEARBY_LIST:
                return R.string.nearby_tab_text;
            case Constants.TYPE_FEATURE_SLIDER_LIST:
                return R.string.feature_slider_text;
            case Constants.TYPE_NEW_ADDED_LIST:
                return R.string.new_added_coupons;
            case Constants.TYPE_BEST_LIST:
                return R.string.this_week_best_deals;
            case Constants.TYPE_SAVED_LIST:
                return R.string.menu_saved_deals;
            case Constants.TYPE_BOUGHT_LIST:
                return R.string.menu_my_deals;
//            case Constants.TYPE_MORE_COUPONS_LIST:
//                return 0;
            case Constants.TYPE_SEARCH_RESULT_LIST:
                return R.string.search_results;
            default:
                return 0;
        }
    }

    public static void transitDetailsActivity(Activity activity, int index, int listType, View v, String transition) {
        Intent i = new Intent(activity, DetailsActivity.class);
        i.putExtra("listIndex", index);
        i.putExtra("listType", listType);

        if (Build.VERSION.SDK_INT < 16 || v == null) {
            activity.startActivity(i); //use default animation
        } else if (Build.VERSION.SDK_INT >= 21) { //use tranistion animation
            Bundle scaledBundle = ActivityOptionsCompat.makeSceneTransitionAnimation(activity, v, transition).toBundle();
            activity.startActivity(i, scaledBundle);
        } else { //use scale up animation
            Bundle scaledBundle = ActivityOptionsCompat.makeScaleUpAnimation(v, 0, 0, v.getWidth(), v.getHeight()).toBundle();
            activity.startActivity(i, scaledBundle);
        }
    }

    public static void transitDetailsActivityFromViewPager(Activity activity, int index, int listType, View v) {
        Intent i = new Intent(activity, DetailsActivity.class);
        i.putExtra("listIndex", index);
        i.putExtra("listType", listType);
//        if (Build.VERSION.SDK_INT < 16 || v == null) {
            activity.startActivity(i);
//        } else {
//            Bundle scaledBundle = ActivityOptionsCompat.makeScaleUpAnimation(v, 0, 0, v.getWidth(), v.getHeight()).toBundle();
//            activity.startActivity(i, scaledBundle);
//        }
    }

    /**
     * Create a File for saving an image
     */
    public static File getImageFileLocation() {
        if (!Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED))
            return null;

        File mediaStorageDir = new File(
                Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                "MickleDeals");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("MickleDeals", "failed to create directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
                .format(new Date());
        File mediaFile = new File(mediaStorageDir.getPath() + File.separator
                + "IMG_" + timeStamp + ".jpg");

        return mediaFile;
    }

    public static int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    public static LatLng getLatLngFromDataHolder(TestDataHolder holder) {
        String[] tokens = holder.mLatLng.split(",");
        return new LatLng(Double.parseDouble(tokens[0].trim()), Double.parseDouble(tokens[1].trim()));
    }

    public static void wrapStringsIntoLinearLayout(String[] strings, LinearLayout rootLayout, int layoutRes) {

        Context context = rootLayout.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        int maxWidth = Utils.getDeviceWidth(context) - context.getResources().getDimensionPixelSize(R.dimen.suggestion_card_left_padding);
        int widthSoFar = 0;
        boolean isFirstTime = true;
        LinearLayout subLL = null;

        for (String string : strings) {

            CardView cardView = (CardView) inflater.inflate(layoutRes, null);
            TextView tv = (TextView) cardView.findViewById(R.id.tv);
            tv.setText(string);
            int rightMargin = context.getResources().getDimensionPixelSize(R.dimen.card_margin);
            cardView.measure(0, 0);
            int measuredWidth = cardView.getMeasuredWidth();
            if (measuredWidth >= maxWidth - rightMargin) continue;
            widthSoFar += cardView.getMeasuredWidth() + rightMargin;
            if (widthSoFar >= maxWidth || isFirstTime) {
                isFirstTime = false;
                subLL = new LinearLayout(context);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
                        android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
                params.bottomMargin = context.getResources().getDimensionPixelSize(R.dimen.suggestion_card_margin_bottom);
                subLL.setLayoutParams(params);
                subLL.setOrientation(LinearLayout.HORIZONTAL);
                rootLayout.addView(subLL);
                widthSoFar = measuredWidth + rightMargin;
            }
            subLL.addView(cardView);

        }

    }

    public static void toggleKeyboard(boolean show, EditText editText, Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);

        if (imm != null) {
            if (show) imm.showSoftInput(editText, 0);
            else imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
        }
    }


}
