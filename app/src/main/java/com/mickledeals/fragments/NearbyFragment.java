package com.mickledeals.fragments;

import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
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

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.mickledeals.R;
import com.mickledeals.adapters.CardAdapter;
import com.mickledeals.datamodel.DataListModel;
import com.mickledeals.tests.TestDataHolder;
import com.mickledeals.utils.Constants;
import com.mickledeals.utils.DLog;
import com.mickledeals.utils.Utils;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Nicky on 11/28/2014.
 */
public class NearbyFragment extends BaseFragment implements AdapterView.OnItemSelectedListener {

    private Spinner mCategorySpinner;
    private Spinner mLocationSpinner;
    private RecyclerView mNearbyRecyclerView;
    private ImageView mMapToggleView;
    private FrameLayout mMapContainer;
    private SupportMapFragment mMapFragment;
    private GoogleMap mMap;
    private HashMap<Marker, Integer> mMarkersHashMap = new HashMap<Marker, Integer>();
    private BitmapDescriptor mPinBitmap;
    private List<TestDataHolder> mNearbyList;
    private boolean mNeedPopularMapOverlays;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mNearbyList = DataListModel.getInstance().getNearbyList();
        for (TestDataHolder holder : DataListModel.getInstance().getDataList().values()) {
            mNearbyList.add(holder);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        DLog.d(this, "onCreateView");
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.fragment_nearby, container, false);

        return rootView;

    }

    @Override
    public void onStart() {
        super.onStart();
        mNearbyRecyclerView.getAdapter().notifyDataSetChanged();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mCategorySpinner = (Spinner) view.findViewById(R.id.categorySpinner);
        mLocationSpinner = (Spinner) view.findViewById(R.id.locationSpinner);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(mContext,
                R.array.category_name, R.layout.spinner_textview);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mCategorySpinner.setAdapter(adapter);
        mCategorySpinner.setOnItemSelectedListener(this);

        adapter = ArrayAdapter.createFromResource(mContext,
                R.array.city_name, R.layout.spinner_textview);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mLocationSpinner.setAdapter(adapter);
        mLocationSpinner.setOnItemSelectedListener(this);

        final int margin = getResources().getDimensionPixelSize(R.dimen.card_margin);
        final int bottomMargin = getResources().getDimensionPixelSize(R.dimen.card_margin_bottom);

        mNearbyRecyclerView = (RecyclerView) view.findViewById(R.id.nearbyRecyclerView);
        mNearbyRecyclerView.setLayoutManager(new GridLayoutManager(mContext, 2));
        mNearbyRecyclerView.setAdapter(new CardAdapter(getActivity(), mNearbyList, Constants.TYPE_NEARBY_LIST, R.layout.card_layout));
        mNearbyRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mNearbyRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                int pos = parent.getChildPosition(view);
                boolean leftside = pos % 2 == 0;
                outRect.left = leftside ? margin : margin / 2;
                outRect.right = leftside ? margin / 2 : margin;
                outRect.bottom = bottomMargin;
            }
        });

        mMapContainer = (FrameLayout) view.findViewById(R.id.mapContainer);
        mMapToggleView = (ImageView) view.findViewById(R.id.mapToggleView);
        mMapToggleView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mNearbyRecyclerView.isShown()) {
                    if (mMapFragment == null) initMapView();
                    showMap();
                } else {
                    hideMap();
                }
            }
        });
    }

    @Override
    public boolean handleBackPressed() { //only get called when its current active fragment
        if (!mNearbyRecyclerView.isShown()) {
            hideMap();
            return true;
        }
        return false;
    }

    @Override
    public void onFragmentPause() {
        super.onFragmentPause();
        if (mMapFragment != null && mMapFragment.isAdded() && mMapContainer.isShown()) mMapFragment.onPause();
    }

    @Override
    public void onFragmentResume() {
        super.onFragmentResume();
        if (mMapFragment != null && mMapFragment.isAdded() && mMapContainer.isShown()) mMapFragment.onResume();
    }

    private void hideMap() {
        mMapToggleView.setImageResource(R.drawable.ic_map);
        mNearbyRecyclerView.setVisibility(View.VISIBLE);
        mMapContainer.setVisibility(View.GONE);
        if (mMapFragment != null && mMapFragment.isAdded()) mMapFragment.onPause();
    }

    private void showMap() {
        mMapToggleView.setImageResource(R.drawable.ic_list);
        mNearbyRecyclerView.setVisibility(View.GONE);
        mMapContainer.setVisibility(View.VISIBLE);
        if (mMapFragment != null && mMapFragment.isAdded()) mMapFragment.onResume();
        if (mNeedPopularMapOverlays && mMap != null) popularMapOverlays(false);
    }

    private void addMapFragment() {
        android.support.v4.app.FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.mapContainer, mMapFragment);
        fragmentTransaction.commit();
    }

    private void initMapView() {
        mMapFragment = SupportMapFragment.newInstance();
        addMapFragment();
        mMapFragment.getMapAsync(new OnMapReadyCallback() {
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
                        Utils.transitDetailsActivity(getActivity(), pos, getListType(), null);
                    }
                });
//                MapsInitializer.initialize(mContext.getApplicationContext());
                mPinBitmap = BitmapDescriptorFactory.fromResource(R.drawable.pin);
                popularMapOverlays(false);
            }
        });
    }

    private void popularMapOverlays(boolean anim) {
        mNeedPopularMapOverlays = false;
        mMap.clear();
        mMarkersHashMap.clear();

        LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();

        for (int i = 0; i < mNearbyList.size(); i++) {

            TestDataHolder dataHolder = mNearbyList.get(i);
            LatLng ll = Utils.getLatLngFromDataHolder(dataHolder);

            Marker marker = mMap.addMarker(new MarkerOptions()
                    .position(ll)
                    .title(dataHolder.getStoreName())
                    .snippet(dataHolder.getDescription())
                    .icon(mPinBitmap));

            mMarkersHashMap.put(marker, i);

            boundsBuilder.include(ll);
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
        mNeedPopularMapOverlays = true;
        if (parent == mCategorySpinner) {
            mNearbyList.clear();
            for (TestDataHolder holder : DataListModel.getInstance().getDataList().values()) {
                if (position == 0 || holder.mCategoryId == position) {
                    mNearbyList.add(holder);
                }
            }
            mNearbyRecyclerView.getAdapter().notifyDataSetChanged();
        }
        if (mMapContainer.isShown()) popularMapOverlays(true);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
