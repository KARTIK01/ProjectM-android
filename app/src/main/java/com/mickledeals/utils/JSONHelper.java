package com.mickledeals.utils;

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
            object.getString(key);
        } catch (JSONException e) {
            DLog.e(JSONHelper.class, "key = " + key + " " + e.getMessage());
        }
        return "";
    }


}
