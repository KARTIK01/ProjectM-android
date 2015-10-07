package com.mickledeals.utils;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.mickledeals.datamodel.CouponInfo;

/**
 * Created by Nicky on 5/18/2015.
 */
public class MDLocationManager implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    public static interface LocationConnectionCallback {
        void onUpdateLocation(Location lastLocation);

        void onConnectionFailed();
    }

    private Context mContext;
    private LocationConnectionCallback mCallback;
    private static MDLocationManager sManager;
    private Location mLastLocation;

    private GoogleApiClient mGoogleApiClient;

    private MDLocationManager(Context context) {
        mContext = context;
        buildGoogleApiClient();
    }

    public static MDLocationManager getInstance(Context context) {
        if (sManager == null) {
            sManager = new MDLocationManager(context);
        }
        return sManager;
    }

    public void registerCallback(LocationConnectionCallback callback) {
        mCallback = callback;
    }

    public Location getLastLocation() {
        return mLastLocation;
    }

    public void requestUpdateLocation(LocationConnectionCallback callback) {
        mCallback = callback;
        updateLocationAndSendCallback();
    }

    private void updateLocationAndSendCallback() {
        Location location = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (location != null) {
            mLastLocation = location;
            if (mCallback != null) mCallback.onUpdateLocation(mLastLocation);
            mCallback = null; //need to set it to null otherwise it will keep calling prepareSendRequest
        } else if (!mGoogleApiClient.isConnected()) {
            connect();
        } else {
            if (mCallback != null) mCallback.onConnectionFailed();
            mCallback = null;
        }
    }

    public float getDistanceFromCurLocation(CouponInfo holder) {
        if (mLastLocation == null || holder.mBusinessInfo.mLat == 0 || holder.mBusinessInfo.mLng == 0) return -1;
        Location dataLocation = new Location("");
        dataLocation.setLatitude(holder.mBusinessInfo.mLat);
        dataLocation.setLongitude(holder.mBusinessInfo.mLng);
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
        updateLocationAndSendCallback();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        DLog.d(this, "onConnectionFailed");
        if (mCallback != null) mCallback.onConnectionFailed();
    }

    @Override
    public void onConnectionSuspended(int i) {
        DLog.d(this, "onConnectionSuspended");
        if (mCallback != null) mCallback.onConnectionFailed();
    }

    public void connect() {
        DLog.d(this, "connect");
        if (!mGoogleApiClient.isConnected() && !mGoogleApiClient.isConnecting()) {

            DLog.d(this, "real connect");
            mGoogleApiClient.connect();
        }
    }

    public void disconnect() {
        mGoogleApiClient.disconnect();
    }

}
