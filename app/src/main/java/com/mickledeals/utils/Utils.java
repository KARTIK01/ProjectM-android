package com.mickledeals.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.mickledeals.R;
import com.mickledeals.activities.DetailsActivity;
import com.mickledeals.activities.MDApplication;
import com.mickledeals.datamodel.DataListModel;
import com.mickledeals.tests.TestDataHolder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by Nicky on 11/28/2014.
 */
public class Utils {

    public static int getPixelsFromDip(float dips, Resources res) {
        return Math.round(dips * MDApplication.sDeviceDensity);
    }

    public static float getDipFromPixels(int pixel, Resources res) {
        return pixel / MDApplication.sDeviceDensity;
    }

    public static boolean isChineseLocale() {
        return MDApplication.sCurrentLocale.getLanguage().equals("zh");
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
        int maxWidth = MDApplication.sDeviceWidth - context.getResources().getDimensionPixelSize(R.dimen.suggestion_card_left_padding);
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

    public static void shareScreenShot(Activity activity, ScrollView scrollView, String subject, String content) {

        View v = scrollView.getChildAt(0);

        scrollView.scrollTo(0, 0);

        v.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));

        Bitmap bitmap = Bitmap.createBitmap(MDApplication.sDeviceWidth, v.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bitmap);
        v.draw(c);
        File file = Utils.getImageFileLocation();
        if (file == null) {
            Toast.makeText(activity, R.string.share_failed,
                    Toast.LENGTH_LONG).show();
            return;
        }
        FileOutputStream fout;
        try {
            fout = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, fout);
            fout.flush();
            fout.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_TEXT, content);
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        shareIntent.putExtra(Intent.EXTRA_STREAM,
                Uri.fromFile(file));
        shareIntent.setType("image/*");
        activity.startActivity(Intent.createChooser(shareIntent, activity.getResources().getText(R.string.share_to)));
    }


}
