package com.mickledeals.fragments;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewStub;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.mickledeals.R;
import com.mickledeals.activities.MDApplication;
import com.mickledeals.datamodel.CouponInfo;
import com.mickledeals.utils.Constants;
import com.mickledeals.utils.DLog;
import com.mickledeals.utils.MDApiManager;
import com.mickledeals.utils.MDLocationManager;
import com.mickledeals.utils.Utils;

import java.util.HashMap;

/**
 * Created by Nicky on 11/28/2014.
 */
public abstract class ListMapBaseFragment extends SwipeRefreshBaseFragment implements AdapterView.OnItemSelectedListener {

    private static final double LAT_DEFAULT = 37.752814;
    private static final double LONG_DEFAULT = -122.440690;
    private Spinner mCategorySpinner;
    protected Spinner mLocationSpinner;
    protected Spinner mSortSpinner;
    private ImageView mMapToggleView;
    private FrameLayout mMapContainer;
    private MapView mMapView;
    private GoogleMap mMap;
    private HashMap<Marker, Integer> mMarkersHashMap = new HashMap<Marker, Integer>();
    private BitmapDescriptor mPinBitmap;
    private boolean mNeedPopularMapOverlays;

    protected View mNoLocationLayout;
    protected ViewStub mNoLocationStub;
    protected MDLocationManager mLocationManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        setHasOptionsMenu(true);
        mLocationManager = MDLocationManager.getInstance(mContext);
        mLocationManager.connect();
//        prepareSendRequest(); //cannot send request here, otherwise no refresh dialog
    }

    protected boolean isSortByLocation() {
        if (mSortSpinner == null) return true;
        return mSortSpinner.getSelectedItemPosition() == 0;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mCategorySpinner = (Spinner) view.findViewById(R.id.categorySpinner);
        mLocationSpinner = (Spinner) view.findViewById(R.id.locationSpinner);
        mSortSpinner = (Spinner) view.findViewById(R.id.sortSpinner);

        ArrayAdapter<CharSequence> adapter;
        if (mCategorySpinner != null) {
            adapter = ArrayAdapter.createFromResource(mContext,
                    R.array.category_name, R.layout.spinner_textview);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            mCategorySpinner.setAdapter(adapter);
            mCategorySpinner.post(new Runnable() {
                @Override
                public void run() {
                    //prevent onitem listener get called
                    mCategorySpinner.setOnItemSelectedListener(ListMapBaseFragment.this);
                }
            });
        }

        adapter = ArrayAdapter.createFromResource(mContext,
                R.array.city_name, R.layout.spinner_textview);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mLocationSpinner.setAdapter(adapter);
        mLocationSpinner.post(new Runnable() {
            @Override
            public void run() {
                mLocationSpinner.setOnItemSelectedListener(ListMapBaseFragment.this);
            }
        });
        if (mSortSpinner != null) {
            adapter = ArrayAdapter.createFromResource(mContext,
                    R.array.sort_options, R.layout.dummy_spinner_textview);
        } else {
            mSortSpinner = (Spinner) view.findViewById(R.id.sortSpinnerInSearch);
            adapter = ArrayAdapter.createFromResource(mContext,
                    R.array.sort_options, R.layout.spinner_textview);
        }
        adapter.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);
        mSortSpinner.setAdapter(adapter);
        mSortSpinner.post(new Runnable() {
            @Override
            public void run() {
                mSortSpinner.setOnItemSelectedListener(ListMapBaseFragment.this);
            }
        });

        mMapContainer = (FrameLayout) view.findViewById(R.id.mapContainer);
        mMapToggleView = (ImageView) view.findViewById(R.id.mapToggleView);
        mMapToggleView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mSwipeRefreshLayout.getVisibility() == View.VISIBLE) {
                    if (mMapView == null) initMapView();
                    showMap();
                } else {
                    hideMap();
                }
            }
        });

        mNoLocationStub = (ViewStub) view.findViewById(R.id.noLocationStub);
    }

    @Override
    public boolean handleBackPressed() { //only get called when its current active fragment
        if (mSwipeRefreshLayout.getVisibility() != View.VISIBLE) {
            hideMap();
            return true;
        }
        return false;
    }

    @Override
    public void onFragmentPause() {
        super.onFragmentPause();
        if (mMapView != null && mMapContainer.getVisibility() == View.VISIBLE) mMapView.onPause();
    }

    @Override
    public void onFragmentResume() {
        super.onFragmentResume();
        if (mMapView != null && mMapContainer.getVisibility() == View.VISIBLE) mMapView.onResume();
    }

    private void hideMap() {
        mMapToggleView.setImageResource(R.drawable.ic_map);
        mSwipeRefreshLayout.setVisibility(View.VISIBLE);
        mMapContainer.setVisibility(View.GONE);
        mMapContainer.removeView(mMapView);
        if (mDataList.size() == 0 && !(mNoNetworkLayout != null && mNoNetworkLayout.isShown())) {
            mNoResultLayout.setVisibility(View.VISIBLE);
        }
        if (mMapView != null) mMapView.onPause();
    }

    private void showMap() {
        mMapToggleView.setImageResource(R.drawable.ic_list);
        mSwipeRefreshLayout.setVisibility(View.GONE);
        mMapContainer.setVisibility(View.VISIBLE);
        mMapContainer.addView(mMapView);
        mMapView.onResume();
        mNoResultLayout.setVisibility(View.GONE);
        if (mNeedPopularMapOverlays && mMap != null) populateMapOverlays(false);
    }

    private void initMapView() {
        mMapView = new MapView(mContext);
        mMapView.onCreate(null);
        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mMap = googleMap;
                mMap.setMyLocationEnabled(true);
                mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

                    public View getInfoWindow(Marker marker) {
                        View view = getActivity().getLayoutInflater().inflate(R.layout.pin_overlay, null);
                        TextView title = (TextView) view.findViewById(R.id.pin_item_title);
                        TextView snippet = (TextView) view.findViewById(R.id.pin_item_snippet);
                        title.setText(marker.getTitle());
                        snippet.setText(marker.getSnippet());
//                        if (marker.equals(mSelectedMarker)) {
//                            snippet.setVisibility(View.GONE);
//                            view.findViewById(R.id.arrow).setVisibility(View.GONE);
//                        } else {
//                            snippet.setText(marker.getSnippet());
//                        }
                        return view;
                    }

                    public View getInfoContents(Marker marker) {
                        // TODO Auto-generated method stub
                        return null;
                    }
                });
                mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                    @Override
                    public void onInfoWindowClick(Marker marker) {
                        int pos = mMarkersHashMap.get(marker);
                        Utils.transitDetailsActivity(getActivity(), pos, getListType(), null, null);
                    }
                });
                MapsInitializer.initialize(mContext.getApplicationContext());
                mPinBitmap = BitmapDescriptorFactory.fromResource(R.drawable.md_pin);
                populateMapOverlays(false);
            }
        });
    }

    private void populateMapOverlays(boolean anim) {
        mNeedPopularMapOverlays = false;
        mMap.clear();
        mMarkersHashMap.clear();

        LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();


        for (int i = 0; i < mDataList.size(); i++) {

            CouponInfo dataHolder = mDataList.get(i);

            LatLng latLng = new LatLng(dataHolder.mBusinessInfo.mLat, dataHolder.mBusinessInfo.mLng);

            Marker marker = mMap.addMarker(new MarkerOptions()
                    .position(latLng)
                    .title(dataHolder.mBusinessInfo.getStoreName())
                    .snippet(dataHolder.getDescription())
                    .icon(mPinBitmap));

            mMarkersHashMap.put(marker, i);

            boundsBuilder.include(latLng);
        }


        if (mDataList.size() == 0) {
            if (anim)
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(LAT_DEFAULT, LONG_DEFAULT), 12));
            else
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(LAT_DEFAULT, LONG_DEFAULT), 12));
        } else if (mDataList.size() == 1) {

            LatLng latLng = new LatLng(mDataList.get(0).mBusinessInfo.mLat, mDataList.get(0).mBusinessInfo.mLng);
            if (anim) {
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
            } else {
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
            }
        } else {
            LatLngBounds bounds = boundsBuilder.build();
            if (anim)
                mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, Utils.getPixelsFromDip(50f, getResources())));
            else
                mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, MDApplication.sDeviceWidth,
                        MDApplication.sDeviceHeight - Utils.getPixelsFromDip(56 + 48, mContext.getResources()), Utils.getPixelsFromDip(50f, getResources())));
            //action bar height + map panel height
        }
    }

    protected int getListType() {
        return Constants.TYPE_NEARBY_LIST;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        DLog.d(this, "onItemSelected");
        prepareSendRequest();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void sendRequest(boolean loadMore) {
        int categoryId = 0;
        if (mCategorySpinner != null) {
            categoryId = getResources().getIntArray(R.array.category_id)[mCategorySpinner.getSelectedItemPosition()];
        }
        String city = mLocationSpinner.getSelectedItemPosition() == 0 ? null : (String) mLocationSpinner.getSelectedItem();
        Location location = mSortSpinner.getSelectedItemPosition() == 0 ? MDLocationManager.getInstance(mContext).getLastLocation() : null;
        String searchText = getSearchText();
        int currentSize = loadMore ? mDataList.size() : 0;
        MDApiManager.fetchSearchCouponList(categoryId, city, location, searchText, currentSize, this);
    }

    protected String getSearchText() {
        return null;
    }

    @Override
    public void prepareSendRequest() {
        DLog.d(this, "prepareSendRequest");
        if (mNoLocationLayout != null) mNoLocationLayout.setVisibility(View.GONE);
        if (isSortByLocation()) {

            mLocationManager.requestUpdateLocation(new MDLocationManager.LocationConnectionCallback() {
                @Override
                public void onUpdateLocation(Location lastLocation) {
                    DLog.d(this, "onUpdateLocation");
                    ListMapBaseFragment.super.prepareSendRequest();
                }

                @Override
                public void onConnectionFailed() {
                    DLog.d(this, "onConnectionFailed");
                    inflateNoLocationLayout();
                    mSwipeRefreshLayout.setRefreshing(false);
                }
            });

        } else {
            super.prepareSendRequest();
        }
    }

    private void inflateNoLocationLayout() {

        if (mNoLocationLayout == null) mNoLocationLayout = mNoLocationStub.inflate();
        mNoLocationLayout.setVisibility(View.VISIBLE);

        mNoLocationLayout.findViewById(R.id.locationErrorRetry).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prepareSendRequest();
            }
        });
        mNoLocationLayout.findViewById(R.id.locationErrorOpenSettings).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent locationIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(locationIntent);
            }
        });
        mNoLocationLayout.findViewById(R.id.locationErrorSortDate).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSortSpinner.setSelection(1);
//                prepareSendRequest();
            }
        });
        mNoLocationLayout.findViewById(R.id.locationErrorSortRandom).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSortSpinner.setSelection(2);
//                prepareSendRequest();
            }
        });
    }


    @Override
    public void onSuccessResponse() {
        super.onSuccessResponse();

        mNeedPopularMapOverlays = true;
//        List<CouponInfo> temporaryDataList = getTemporaryDataList();

//        //temporrary
//        for (int i = 0; i < temporaryDataList.size(); i++) {
//            boolean matchCategory = false;
//            boolean matchLocation = false;
//            CouponInfo holder = temporaryDataList.get(i);
//            int categoryPos = 0;
//            if (mCategorySpinner != null) categoryPos = mCategorySpinner.getSelectedItemPosition();
//            if (categoryPos == 0 || holder.mCategoryId == categoryPos) {
//                matchCategory = true;
//            }
//            int locationPos = mLocationSpinner.getSelectedItemPosition();
//            String targetCityName = getActivity().getResources().getStringArray(R.array.city_name)[locationPos];
//            String[] addrToken = holder.mAddress.split(",");
//            String cityName = addrToken[addrToken.length - 1].trim();
//            if (locationPos == 0 || cityName.equals(targetCityName)) {
//                matchLocation = true;
//            }
//            if (matchCategory && matchLocation) {
//                mDataList.add(holder);
//            }
//        }
        if (mDataList.size() == 0) {
            if (mMapContainer.getVisibility() == View.VISIBLE) {
                Toast.makeText(mContext, getNoResultMessage(), Toast.LENGTH_LONG).show();
            } else {
                mNoResultLayout.setVisibility(View.VISIBLE);
            }
        } else {
            mNoResultLayout.setVisibility(View.GONE);
        }
        if (mMapContainer.getVisibility() == View.VISIBLE) populateMapOverlays(true);

    }

    @Override
    public void onResume() {
        super.onResume();
        if (mMapView != null && mMapContainer.getVisibility() == View.VISIBLE) {
            mMapView.onResume();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mMapView != null && mMapContainer.getVisibility() == View.VISIBLE) mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mMapView != null) mMapView.onDestroy();
    }

    @Override
    public void onStart() { // this is actually the same lifecycle as its activity
        super.onStart();
        DLog.d(this, "onStart");
        //no need get location here, going back to activity(onstart) should not request another update
        mLocationManager.connect();
    }

    @Override
    public void onStop() { // this is actually the same lifecycle as its activity
        super.onStop();
        DLog.d(this, "onStop");
        mLocationManager.disconnect();
    }

    public abstract String getNoResultMessage();


}
