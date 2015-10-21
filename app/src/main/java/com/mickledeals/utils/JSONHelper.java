package com.mickledeals.utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Nicky on 10/14/2015.
 */
public class JSONHelper {

    public static String getString(JSONObject object, String key) {

        if (!object.has(key)) {
            DLog.e(JSONHelper.class, "no such key exists, key = " + key);
            return "";
        }
        if (object.isNull(key)) {
            return "";
        }
        try {
            return object.getString(key);
        } catch (JSONException e) {
            DLog.e(JSONHelper.class, "key = " + key + " " + e.getMessage());
        }
        return "";
    }

    public static boolean getBoolean(JSONObject object, String key) {

        if (!object.has(key)) {
            DLog.e(JSONHelper.class, "no such key exists, key = " + key);
            return false;
        }
        try {
            return object.getBoolean(key);
        } catch (JSONException e) {
            DLog.e(JSONHelper.class, "key = " + key + " " + e.getMessage());
        }
        return false;
    }

    public static int getInteger(JSONObject object, String key) {

        if (!object.has(key)) {
            DLog.e(JSONHelper.class, "no such key exists, key = " + key);
            return 0;
        }
        try {
            return object.getInt(key);
        } catch (JSONException e) {
            DLog.e(JSONHelper.class, "key = " + key + " " + e.getMessage());
        }
        return 0;
    }

    public static long getLong(JSONObject object, String key) {

        if (!object.has(key)) {
            DLog.e(JSONHelper.class, "no such key exists, key = " + key);
            return 0;
        }
        try {
            return object.getLong(key);
        } catch (JSONException e) {
            DLog.e(JSONHelper.class, "key = " + key + " " + e.getMessage());
        }
        return 0;
    }

    public static double getDouble(JSONObject object, String key) {

        if (!object.has(key)) {
            DLog.e(JSONHelper.class, "no such key exists, key = " + key);
            return 0;
        }
        try {
            return object.getDouble(key);
        } catch (JSONException e) {
            DLog.e(JSONHelper.class, "key = " + key + " " + e.getMessage());
        }
        return 0;
    }

    public static JSONObject getJSONObject(JSONObject object, String key) {

        if (!object.has(key)) {
            DLog.e(JSONHelper.class, "no such key exists, key = " + key);
            return null;
        }
        try {
            return object.getJSONObject(key);
        } catch (JSONException e) {
            DLog.e(JSONHelper.class, "key = " + key + " " + e.getMessage());
        }
        return null;
    }

    public static JSONArray getJSONArray(JSONObject object, String key) {

        if (!object.has(key)) {
            DLog.e(JSONHelper.class, "no such key exists, key = " + key);
            return null;
        }
        try {
            return object.getJSONArray(key);
        } catch (JSONException e) {
            DLog.e(JSONHelper.class, "key = " + key + " " + e.getMessage());
        }
        return null;
    }


}
