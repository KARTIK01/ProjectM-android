package com.mickledeals.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Nicky on 3/1/2015.
 */
public class PreferenceHelper {

    private static final String PREF_NAME = "mickledeals.preferences";

    public static void savePreferencesInt(Context context, String key, int value) {
        SharedPreferences prefs = context.getSharedPreferences(
                PREF_NAME, 0);
        SharedPreferences.Editor prefsPrivateEditor = prefs.edit();
        prefsPrivateEditor.putInt(key, value);
        prefsPrivateEditor.commit();
    }

    public static int getPreferenceValueInt(Context context, String key, int defaultValue) {
        SharedPreferences prefs = context.getSharedPreferences(
                PREF_NAME, 0);
        return prefs.getInt(key, defaultValue);
    }

    public static void savePreferencesStr(Context context, String key, String value) {
        SharedPreferences prefs = context.getSharedPreferences(
                PREF_NAME, 0);
        SharedPreferences.Editor prefsPrivateEditor = prefs.edit();
        prefsPrivateEditor.putString(key, value);
        prefsPrivateEditor.commit();
    }

    public static String getPreferenceValueStr(Context context, String key, String defaultValue) {
        SharedPreferences prefs = context.getSharedPreferences(
                PREF_NAME, 0);
        return prefs.getString(key, defaultValue);
    }

    public static void savePreferencesBoolean(Context context, String key, boolean value) {
        SharedPreferences prefs = context.getSharedPreferences(
                PREF_NAME, 0);
        SharedPreferences.Editor prefsPrivateEditor = prefs.edit();
        prefsPrivateEditor.putBoolean(key, value);
        prefsPrivateEditor.commit();
    }

    public static boolean getPreferenceValueBoolean(Context context, String key, boolean defaultValue) {
        SharedPreferences prefs = context.getSharedPreferences(
                PREF_NAME, 0);
        return prefs.getBoolean(key, defaultValue);
    }
}