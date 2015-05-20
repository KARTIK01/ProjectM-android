package com.mickledeals.utils;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.mickledeals.tests.TestDataHolder;

/**
 * Created by Nicky on 5/18/2015.
 */
public class LocationManager implements  GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{

    public static interface LocationConnectionCallback {
        void onConnected();
        void onConnectionFailed();
    }

    private Context mContext;
    private LocationConnectionCallback mCallback;
    private static LocationManager sManager;
    private Location mLastLocation;
//    private boolean isConnecting;

    private GoogleApiClient mGoogleApiClient;

    private LocationManager(Context context) {
        mContext = context;
        buildGoogleApiClient();
    }

    public static LocationManager getInstance(Context context) {
        if (sManager == null) {
            sManager = new LocationManager(context);
        }
        return sManager;
    }

    public void registerCallback(LocationConnectionCallback callback) {
        mCallback = callback;
    }

    public Location getLastLocation() {
        //adding this logic may not be needed and could be error prone
//        if (mLastLocation == null && !isConnecting) {
//            isConnecting = true;
//            connect();
//        }
        return mLastLocation;
    }

    public float getDistanceFromCurLocation(TestDataHolder holder) {
        if (mLastLocation == null) return -1;
        Location dataLocation = new Location("");
        String[] tokens = holder.mLatLng.split(",");
        dataLocation.setLatitude(Double.parseDouble(tokens[0].trim()));
        dataLocation.setLongitude(Double.parseDouble(tokens[1].trim()));
        float meters = mLastLocation.distanceTo(dataLocation);
        float miles = Math.round(meters / 1609 * 10) / 10f;
        return miles;
    }

    private synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(mContext)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    public void onConnected(Bundle bundle) {
        DLog.d(this, "onConnected");
//        isConnecting = false;
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (mCallback != null) mCallback.onConnected();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        DLog.d(this, "onConnectionFailed");
//        isConnecting = false;
        if (mCallback != null) mCallback.onConnectionFailed();
    }

    @Override
    public void onConnectionSuspended(int i) {
        DLog.d(this, "onConnectionSuspended");
//        isConnecting = false;
        if (mCallback != null) mCallback.onConnectionFailed();
    }

    public void connect() {
        mGoogleApiClient.connect();
    }

    public void disconnect() {
        mGoogleApiClient.disconnect();
    }

}
