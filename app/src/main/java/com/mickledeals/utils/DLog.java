package com.mickledeals.utils;

import android.util.Log;

/**
 * Created by Nicky on 11/28/2014.
 */
public class DLog {

    public static void d(Object object, String str) {
        Log.d(object.getClass().getSimpleName(), str);
    }

    public static void e(Object object, String str) {
        Log.e(object.getClass().getSimpleName(), str);
    }
}
