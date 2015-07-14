package com.mickledeals.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.mickledeals.tests.TestDataHolder;
import com.mickledeals.utils.Constants;
import com.mickledeals.utils.DLog;
import com.mickledeals.utils.LocationManager;
import com.mickledeals.utils.Utils;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Nicky on 11/28/2014.
 */
public abstract class ListResultBaseFragment extends BaseFragment implements AdapterView.OnItemSelectedListener,
        LocationManager.LocationConnectionCallback {

    private static final double LAT_DEFAULT = 37.752814;
    private static final double LONG_DEFAULT = -122.440690;
    private Spinner mCategorySpinner;
    protected Spinner mLocationSpinner;
    private Spinner mSortSpinner;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    protected RecyclerView mListResultRecyclerView;
    private ImageView mMapToggleView;
    private FrameLayout mMapContainer;
    private MapView mMapView;
    private GoogleMap mMap;
    private HashMap<Marker, Integer> mMarkersHashMap = new HashMap<Marker, Integer>();
    private BitmapDescriptor mPinBitmap;
    protected List<TestDataHolder> mDataList;
    private View mNoResultLayout;
    private boolean mNeedPopularMapOverlays;

    protected LocationManager mLocationManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        setHasOptionsMenu(true);
        mLocationManager = LocationManager.getInstance(mContext);
        mLocationManager.registerCallback(this);
        mDataList = getDataList();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        DLog.d(this, "onCreateView");
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                getFragmentLayoutRes(), container, false);

        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mCategorySpinner = (Spinner) view.findViewById(R.id.categorySpinner);
        mLocationSpinner = (Spinner) view.findViewById(R.id.locationSpinner);
        mSortSpinner = (Spinner) view.findViewById(R.id.sortSpinner);

        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);
        mSwipeRefreshLayout.requestDisallowInterceptTouchEvent(false);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary1, R.color.colorPrimary4);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                initiateRefresh();
            }
        });

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
                    mCategorySpinner.setOnItemSelectedListener(ListResultBaseFragment.this);
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
                mLocationSpinner.setOnItemSelectedListener(ListResultBaseFragment.this);
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
                mSortSpinner.setOnItemSelectedListener(ListResultBaseFragment.this);
            }
        });

        mListResultRecyclerView = (RecyclerView) view.findViewById(R.id.listResultRecyclerView);
        setRecyclerView();
        mNoResultLayout = view.findViewById(R.id.noResultLayout);
        mMapContainer = (FrameLayout) view.findViewById(R.id.mapContainer);
        mMapToggleView = (ImageView) view.findViewById(R.id.mapToggleView);
        mMapToggleView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListResultRecyclerView.getVisibility() == View.VISIBLE) {
                    if (mMapView == null) initMapView();
                    showMap();
                } else {
                    hideMap();
                }
            }
        });
    }

    @Override
    public boolean handleBackPressed() { //only get called when its current active fragment
        if (mListResultRecyclerView.getVisibility() != View.VISIBLE) {
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
        mListResultRecyclerView.setVisibility(View.VISIBLE);
        mMapContainer.setVisibility(View.GONE);
        mMapContainer.removeView(mMapView);
        if (mDataList.size() == 0) {
            mNoResultLayout.setVisibility(View.VISIBLE);
        }
        if (mMapView != null) mMapView.onPause();
    }

    private void showMap() {
        mMapToggleView.setImageResource(R.drawable.ic_list);
        mListResultRecyclerView.setVisibility(View.GONE);
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

            TestDataHolder dataHolder = mDataList.get(i);
            LatLng ll = Utils.getLatLngFromDataHolder(dataHolder);

            Marker marker = mMap.addMarker(new MarkerOptions()
                    .position(ll)
                    .title(dataHolder.getStoreName())
                    .snippet(dataHolder.getDescription())
                    .icon(mPinBitmap));

            mMarkersHashMap.put(marker, i);

            boundsBuilder.include(ll);
        }


        if (mDataList.size() == 0) {
            if (anim)
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(LAT_DEFAULT, LONG_DEFAULT), 12));
            else
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(LAT_DEFAULT, LONG_DEFAULT), 12));
        } else if (mDataList.size() == 1) {
            if (anim) {
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(Utils.getLatLngFromDataHolder(mDataList.get(0)), 15));
            } else {
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(Utils.getLatLngFromDataHolder(mDataList.get(0)), 15));
            }
        } else {
            LatLngBounds bounds = boundsBuilder.build();
            if (anim)
                mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, Utils.getPixelsFromDip(50f, getResources())));
            else
                mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, Utils.getDeviceWidth(mContext),
                        Utils.getDeviceHeight(mContext) - Utils.getPixelsFromDip(56 + 48, mContext.getResources()), Utils.getPixelsFromDip(50f, getResources())));
        }
    }

    protected int getListType() {
        return Constants.TYPE_NEARBY_LIST;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        DLog.d(this, "onItemSelected");
        sendUpdateRequest();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }


    public void sendUpdateRequest() {
        mNeedPopularMapOverlays = true;
        List<TestDataHolder> temporaryDataList = getTemporaryDataList();
        mDataList.clear();

        //call api
        //temporrary
        for (int i = 0; i < temporaryDataList.size(); i++) {
            boolean matchCategory = false;
            boolean matchLocation = false;
            TestDataHolder holder = temporaryDataList.get(i);
            int categoryPos = 0;
            if (mCategorySpinner != null) categoryPos = mCategorySpinner.getSelectedItemPosition();
            if (categoryPos == 0 || holder.mCategoryId == categoryPos) {
                matchCategory = true;
            }
            int locationPos = mLocationSpinner.getSelectedItemPosition();
            String targetCityName = getActivity().getResources().getStringArray(R.array.city_name)[locationPos];
            String[] addrToken = holder.mAddress.split(",");
            String cityName = addrToken[addrToken.length - 1].trim();
            if (locationPos == 0 || cityName.equals(targetCityName)) {
                matchLocation = true;
            }
            if (matchCategory && matchLocation) {
                mDataList.add(holder);
            }
        }
        mListResultRecyclerView.getAdapter().notifyDataSetChanged();
        if (mDataList.size() == 0) {
            if (mMapContainer.getVisibility() == View.VISIBLE) {
                Toast.makeText(mContext, getNoResultToastMessage(), Toast.LENGTH_LONG).show();
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
        mListResultRecyclerView.getAdapter().notifyDataSetChanged();
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
    public void onConnected() {
        sendUpdateRequest();
    }

    public void onConnectionFailed() {
    }

    @Override
    public void onStart() { // this is actually the same lifecycle as its activity
        super.onStart();
        DLog.d(this, "onStart");
        //no need get location here, going back to activity(onstart) should not request another update
//        mLocationManager.connect();
    }

    @Override
    public void onStop() { // this is actually the same lifecycle as its activity
        super.onStop();
        DLog.d(this, "onStop");
        mLocationManager.disconnect();
    }

    public abstract List<TestDataHolder> getDataList();

    public abstract List<TestDataHolder> getTemporaryDataList();

    public abstract int getFragmentLayoutRes();

    public abstract void setRecyclerView();

    public abstract String getNoResultToastMessage();



    private void initiateRefresh() {
        new DummyBackgroundTask().execute();
    }

    private void onRefreshComplete() {
//        Log.i(LOG_TAG, "onRefreshComplete");
//
//        // Remove all items from the ListAdapter, and then replace them with the new items
//        mListAdapter.clear();
//        for (String cheese : result) {
//            mListAdapter.add(cheese);
//        }

        // Stop the refreshing indicator
        mSwipeRefreshLayout.setRefreshing(false);
    }

    private class DummyBackgroundTask extends AsyncTask<Void, Void, Void> {

        static final int TASK_DURATION = 3 * 1000; // 3 seconds

        @Override
        protected Void doInBackground(Void... params) {
            // Sleep for a small amount of time to simulate a background-task
            try {
                Thread.sleep(TASK_DURATION);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // Return a new random list of cheeses
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            // Tell the Fragment that the refresh has completed
            onRefreshComplete();
        }

    }
}
