package com.mickledeals.utils;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Toast;

import com.mickledeals.R;
import com.mickledeals.activities.DetailsActivity;
import com.mickledeals.activities.HomeActivity;
import com.mickledeals.activities.MDApplication;
import com.mickledeals.datamodel.DataListModel;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

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

    public static List<Integer> getListFromType(int listType) {
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

//        v.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
//                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));

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

    public static void showRetryDialog(Context context) {
        new AlertDialog.Builder(context, R.style.AppCompatAlertDialogStyle)
                .setTitle(R.string.network_error_title)
                .setMessage(R.string.network_error_msg)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create()
                .show();
    }

    public static void replaceFont(String staticTypefaceFieldName,
                                      final Typeface newTypeface) {
        if (Build.VERSION.SDK_INT >= 21) {
            Map<String, Typeface> newMap = new HashMap<String, Typeface>();
            newMap.put("sans-serif", newTypeface);
            try {
                final Field staticField = Typeface.class
                        .getDeclaredField("sSystemFontMap");
                staticField.setAccessible(true);
                staticField.set(null, newMap);
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        } else {
            try {
                final Field staticField = Typeface.class
                        .getDeclaredField(staticTypefaceFieldName);
                staticField.setAccessible(true);
                staticField.set(null, newTypeface);
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    //this only get called above api level 17
    public static Bitmap blur(Context context, Bitmap image) {
        //weird holo effect after blur, dont use for now


        final float BITMAP_SCALE = 0.4f;
        final float BLUR_RADIUS = 25f;
        int width = Math.round(image.getWidth() * BITMAP_SCALE);
        int height = Math.round(image.getHeight() * BITMAP_SCALE);

        Bitmap inputBitmap = Bitmap.createScaledBitmap(image, width, height, false);
        Bitmap outputBitmap = Bitmap.createBitmap(inputBitmap);

        RenderScript rs = RenderScript.create(context);
        ScriptIntrinsicBlur theIntrinsic = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
        Allocation tmpIn = Allocation.createFromBitmap(rs, inputBitmap);
        Allocation tmpOut = Allocation.createFromBitmap(rs, outputBitmap);
        theIntrinsic.setRadius(BLUR_RADIUS);
        theIntrinsic.setInput(tmpIn);
        theIntrinsic.forEach(tmpOut);
        tmpOut.copyTo(outputBitmap);

        return outputBitmap;
    }

    public static void showAlertDialog(Context context, int titleRes, int messageRes) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.AppCompatAlertDialogStyle)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        if (titleRes != 0) builder.setTitle(titleRes);
        if (messageRes != 0) builder.setMessage(messageRes);
        builder.create().show();
    }

    public static void showNetworkErrorDialog(Context context) {
        AlertDialog dialog = new AlertDialog.Builder(context, R.style.AppCompatAlertDialogStyle)
                .setTitle(R.string.network_error_title)
                .setMessage(R.string.network_error_msg)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();
        dialog.show();
    }

    public static void showNetworkTimeOutDialog(Context context) {
        AlertDialog dialog = new AlertDialog.Builder(context, R.style.AppCompatAlertDialogStyle)
                .setTitle(R.string.network_error_title)
                .setMessage(R.string.response_error_timeout)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();
        dialog.show();
    }

    public static void restartApp(Context context) {
        DataListModel.getInstance().clear();
        Intent i = new Intent(context, HomeActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
    }

    public static String formatPrice(double price) {
        DecimalFormat df = new DecimalFormat("0.00");
        return "$" + df.format(price);
    }

    public static String formatDate(long timeStamp) {
        SimpleDateFormat format = null;
        if (isChineseLocale()) {
            format = new SimpleDateFormat("MM/dd/yyyy");
        } else {
            format = new SimpleDateFormat("MMMM dd, yyyy", Locale.ENGLISH);
        }

        return format.format(new Date(timeStamp));
    }

    public static String formatShortDate(long timeStamp) {
        SimpleDateFormat format = new SimpleDateFormat("MM/dd/yy");
        return format.format(new Date(timeStamp));
    }


    public static boolean containsChinese(String str) {
        return !str.matches("^[\u0000-\u0080]+$");
    }

}
