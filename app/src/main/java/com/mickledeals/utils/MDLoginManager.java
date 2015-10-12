package com.mickledeals.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.facebook.AccessToken;
import com.facebook.login.LoginManager;
import com.mickledeals.activities.LoginDialogActivity;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Nicky on 6/13/2015.
 */
public class MDLoginManager {

    public static interface LoginCallback {
        void onLoginSuccess();
    }

    public static String mEmailAddr;
    public static String mUserName;
    public static int mUserId;

    private static Set<LoginCallback> mCallbackSet = new HashSet<LoginCallback>();
    private static LoginCallback mPendingCallback;
    private AccessToken mAccessToken;
    private static boolean mIsFbLogin = true;
    private Activity mActivity;
    private static MDLoginManager sManager;

    public static MDLoginManager getInstance() {
        if (sManager == null) {
            sManager = new MDLoginManager();
        }
        return sManager;
    }

    public static String getNameOrEmail() {
        if (mUserName != null) return mUserName;
        else return mEmailAddr;
    }

    public static boolean isLogin() {
        return mUserId != 0;
    }

    public static void initFromPreference(Context context) {
        mUserId = PreferenceHelper.getPreferenceValueInt(context, "user_id", 0);
        mEmailAddr = PreferenceHelper.getPreferenceValueStr(context, "user_email", null);
        mUserName = PreferenceHelper.getPreferenceValueStr(context, "user_name", null);
    }

    public static void setUserInfo(Context context, int userId, String email, String userName) {
        mUserId = userId;
        mEmailAddr = email;
        mUserName = userName;
        PreferenceHelper.savePreferencesInt(context, "user_id", userId);
        PreferenceHelper.savePreferencesStr(context, "user_email", email);
        PreferenceHelper.savePreferencesStr(context, "user_name", userName);
    }

    public static void logout(Context context) {
        //check id with f prefix
        if (mIsFbLogin) {
            LoginManager.getInstance().logOut();
        }
        setUserInfo(context, 0, null, null);
    }

    //this should call after user email and name received
    public static void onLoginSuccess() {

        for (LoginCallback callback : mCallbackSet) {
            callback.onLoginSuccess();
        }
        if (mPendingCallback != null) mPendingCallback.onLoginSuccess();
    }

    public static void onLoginCancelOrFail() {
        mPendingCallback = null;
    }

    public static void loginIfNecessary(Activity activity, LoginCallback callback) {
        if (isLogin()) {
            callback.onLoginSuccess();
        } else {
            Intent i = new Intent(activity, LoginDialogActivity.class);
            activity.startActivity(i);
            mPendingCallback = callback;
        }
    }

    public void setAccessToken(AccessToken token) {
        mAccessToken = token;
    }


    public static void registerCallback(LoginCallback callback) {
        mCallbackSet.add(callback);
    }

    public static void unregisterCallback(LoginCallback callback) {
        mCallbackSet.remove(callback);
    }




}
