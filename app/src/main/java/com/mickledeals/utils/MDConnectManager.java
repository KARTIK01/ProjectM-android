package com.mickledeals.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by Nicky on 10/12/2015.
 */
public class MDConnectManager {

    private static MDConnectManager sInstance;

    private Context mContext;
    private ConnectivityManager mConnectivityManager;

    public static MDConnectManager getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new MDConnectManager(context);
        }
        return sInstance;
    }

    private MDConnectManager(Context context) {
        mContext = context;

        mConnectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    public boolean isNetworkAvailable() {

        NetworkInfo activeNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

}
