package com.mickledeals.fragments;

/**
 * Created by Nicky on 11/28/2014.
 */
public class NearbyFragment extends ListResultBaseFragment {

//    private static final double LAT_DEFAULT = 37.752814;
//    private static final double LONG_DEFAULT = -122.440690;
//    private Spinner mCategorySpinner;
//    private Spinner mLocationSpinner;
//    private Spinner mSortSpinner;
//    private RecyclerView mNearbyRecyclerView;
//    private ImageView mMapToggleView;
//    private FrameLayout mMapContainer;
//    private MapView mMapView;
//    private GoogleMap mMap;
//    private HashMap<Marker, Integer> mMarkersHashMap = new HashMap<Marker, Integer>();
//    private BitmapDescriptor mPinBitmap;
//    private List<TestDataHolder> mNearbyList;
//    private View mNoResultLayout;
//    private boolean mNeedPopularMapOverlays;

//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//        buildGoogleApiClient();
//        mNearbyList = DataListModel.getInstance().getNearbyList();
//        for (int i = 1; i <= DataListModel.getInstance().getDataList().size(); i++) {
//            mNearbyList.add(DataListModel.getInstance().getDataList().get(i));
//        }
//    }
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//
//        DLog.d(this, "onCreateView");
//        ViewGroup rootView = (ViewGroup) inflater.inflate(
//                R.layout.fragment_nearby, container, false);
//
//        return rootView;
//
//    }
//
//    @Override
//    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//        mCategorySpinner = (Spinner) view.findViewById(R.id.categorySpinner);
//        mLocationSpinner = (Spinner) view.findViewById(R.id.locationSpinner);
//        mSortSpinner = (Spinner) view.findViewById(R.id.sortSpinner);
//
//        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(mContext,
//                R.array.category_name, R.layout.spinner_textview);
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        mCategorySpinner.setAdapter(adapter);
//        mCategorySpinner.post(new Runnable() {
//            @Override
//            public void run() {
//                //prevent onitem listener get called
//                mCategorySpinner.setOnItemSelectedListener(NearbyFragment.this);
//            }
//        });
//
//        adapter = ArrayAdapter.createFromResource(mContext,
//                R.array.city_name, R.layout.spinner_textview);
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        mLocationSpinner.setAdapter(adapter);
//        mLocationSpinner.post(new Runnable() {
//            @Override
//            public void run() {
//                mLocationSpinner.setOnItemSelectedListener(NearbyFragment.this);
//            }
//        });
//        adapter = ArrayAdapter.createFromResource(mContext,
//                R.array.sort_options, R.layout.dummy_spinner_textview);
//        adapter.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);
//        mSortSpinner.setAdapter(adapter);
//        mSortSpinner.post(new Runnable() {
//            @Override
//            public void run() {
//                mSortSpinner.setOnItemSelectedListener(NearbyFragment.this);
//            }
//        });
//
//        final int margin = getResources().getDimensionPixelSize(R.dimen.card_margin);
//        final int bottomMargin = getResources().getDimensionPixelSize(R.dimen.card_margin_bottom);
//
//        mNearbyRecyclerView = (RecyclerView) view.findViewById(R.id.nearbyRecyclerView);
//        mNearbyRecyclerView.setLayoutManager(new GridLayoutManager(mContext, 2));
//        mNearbyRecyclerView.setAdapter(new CardAdapter(getActivity(), mNearbyList, Constants.TYPE_NEARBY_LIST, R.layout.card_layout));
//        mNearbyRecyclerView.setItemAnimator(new DefaultItemAnimator());
//        mNearbyRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
//            @Override
//            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
//                int pos = parent.getChildPosition(view);
//                boolean leftside = pos % 2 == 0;
//                outRect.left = leftside ? margin : margin / 2;
//                outRect.right = leftside ? margin / 2 : margin;
//                outRect.bottom = bottomMargin;
//            }
//        });
//        mNoResultLayout = view.findViewById(R.id.noResultLayout);
//        mMapContainer = (FrameLayout) view.findViewById(R.id.mapContainer);
//        mMapToggleView = (ImageView) view.findViewById(R.id.mapToggleView);
//        mMapToggleView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (mNearbyRecyclerView.getVisibility() == View.VISIBLE) {
//                    if (mMapView == null) initMapView();
//                    showMap();
//                } else {
//                    hideMap();
//                }
//            }
//        });
//    }
//
//    @Override
//    public boolean handleBackPressed() { //only get called when its current active fragment
//        if (mNearbyRecyclerView.getVisibility() != View.VISIBLE) {
//            hideMap();
//            return true;
//        }
//        return false;
//    }
//
//    @Override
//    public void onFragmentPause() {
//        super.onFragmentPause();
//        if (mMapView != null && mMapContainer.getVisibility() == View.VISIBLE) mMapView.onPause();
//    }
//
//    @Override
//    public void onFragmentResume() {
//        super.onFragmentResume();
//        if (mMapView != null && mMapContainer.getVisibility() == View.VISIBLE) mMapView.onResume();
//    }
//
//    private void hideMap() {
//        mMapToggleView.setImageResource(R.drawable.ic_map);
//        mNearbyRecyclerView.setVisibility(View.VISIBLE);
//        mMapContainer.setVisibility(View.GONE);
//        mMapContainer.removeView(mMapView);
//        if (mNearbyList.size() == 0) {
//            mNoResultLayout.setVisibility(View.VISIBLE);
//        }
//        if (mMapView != null) mMapView.onPause();
//    }
//
//    private void showMap() {
//        mMapToggleView.setImageResource(R.drawable.ic_list);
//        mNearbyRecyclerView.setVisibility(View.GONE);
//        mMapContainer.setVisibility(View.VISIBLE);
//        mMapContainer.addView(mMapView);
//        mMapView.onResume();
//        mNoResultLayout.setVisibility(View.GONE);
//        if (mNeedPopularMapOverlays && mMap != null) populateMapOverlays(false);
//    }
//
//    private void initMapView() {
//        mMapView = new MapView(mContext);
//        mMapView.onCreate(null);
//        mMapView.getMapAsync(new OnMapReadyCallback() {
//            @Override
//            public void onMapReady(GoogleMap googleMap) {
//                mMap = googleMap;
//                mMap.setMyLocationEnabled(true);
//                mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
//
//                    public View getInfoWindow(Marker marker) {
//                        View view = getActivity().getLayoutInflater().inflate(R.layout.pin_overlay, null);
//                        TextView title = (TextView) view.findViewById(R.id.pin_item_title);
//                        TextView snippet = (TextView) view.findViewById(R.id.pin_item_snippet);
//                        title.setText(marker.getTitle());
//                        snippet.setText(marker.getSnippet());
////                        if (marker.equals(mSelectedMarker)) {
////                            snippet.setVisibility(View.GONE);
////                            view.findViewById(R.id.arrow).setVisibility(View.GONE);
////                        } else {
////                            snippet.setText(marker.getSnippet());
////                        }
//                        return view;
//                    }
//
//                    public View getInfoContents(Marker marker) {
//                        // TODO Auto-generated method stub
//                        return null;
//                    }
//                });
//                mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
//                    @Override
//                    public void onInfoWindowClick(Marker marker) {
//                        int pos = mMarkersHashMap.get(marker);
//                        Utils.transitDetailsActivity(getActivity(), pos, getListType(), null, null);
//                    }
//                });
//                MapsInitializer.initialize(mContext.getApplicationContext());
//                mPinBitmap = BitmapDescriptorFactory.fromResource(R.drawable.md_pin);
//                populateMapOverlays(false);
//            }
//        });
//    }
//
//    private void populateMapOverlays(boolean anim) {
//        mNeedPopularMapOverlays = false;
//        mMap.clear();
//        mMarkersHashMap.clear();
//
//        LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();
//
//
//        for (int i = 0; i < mNearbyList.size(); i++) {
//
//            TestDataHolder dataHolder = mNearbyList.get(i);
//            LatLng ll = Utils.getLatLngFromDataHolder(dataHolder);
//
//            Marker marker = mMap.addMarker(new MarkerOptions()
//                    .position(ll)
//                    .title(dataHolder.getStoreName())
//                    .snippet(dataHolder.getDescription())
//                    .icon(mPinBitmap));
//
//            mMarkersHashMap.put(marker, i);
//
//            boundsBuilder.include(ll);
//        }
//
//
//        if (mNearbyList.size() == 0) {
//            if (anim)
//                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(LAT_DEFAULT, LONG_DEFAULT), 12));
//            else
//                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(LAT_DEFAULT, LONG_DEFAULT), 12));
//        } else if (mNearbyList.size() == 1) {
//            if (anim) {
//                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(Utils.getLatLngFromDataHolder(mNearbyList.get(0)), 15));
//            } else {
//                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(Utils.getLatLngFromDataHolder(mNearbyList.get(0)), 15));
//            }
//        } else {
//            LatLngBounds bounds = boundsBuilder.build();
//            if (anim)
//                mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, Utils.getPixelsFromDip(50f, getResources())));
//            else
//                mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, Utils.getDeviceWidth(mContext),
//                        Utils.getDeviceHeight(mContext) - Utils.getPixelsFromDip(56 + 48, mContext.getResources()), Utils.getPixelsFromDip(50f, getResources())));
//        }
//    }
//
//    protected int getListType() {
//        return Constants.TYPE_NEARBY_LIST;
//    }
//
//    @Override
//    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//        DLog.d(this, "onItemSelected");
//        updateSearchFromSpinner();
//    }
//
//    @Override
//    public void onNothingSelected(AdapterView<?> parent) {
//
//    }
//
//
//    //Temporary for demo
//    public void updateSearchFromSpinner() {
//        mNeedPopularMapOverlays = true;
//        mNearbyList.clear();
//        for (int i = 1; i <= DataListModel.getInstance().getDataList().size(); i++) {
//            boolean matchCategory = false;
//            boolean matchLocation = false;
//            TestDataHolder holder = DataListModel.getInstance().getDataList().get(i);
//            int categoryPos = mCategorySpinner.getSelectedItemPosition();
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
//                mNearbyList.add(DataListModel.getInstance().getDataList().get(i));
//            }
//        }
//        mNearbyRecyclerView.getAdapter().notifyDataSetChanged();
//        if (mNearbyList.size() == 0) {
//            if (mMapContainer.getVisibility() == View.VISIBLE) {
//                Toast.makeText(mContext, getString(R.string.no_results_found) + " " + getString(R.string.try_different_filter), Toast.LENGTH_LONG).show();
//            } else {
//                mNoResultLayout.setVisibility(View.VISIBLE);
//            }
//        } else {
//            mNoResultLayout.setVisibility(View.GONE);
//        }
//        if (mMapContainer.getVisibility() == View.VISIBLE) populateMapOverlays(true);
//    }
//
//    @Override
//    public void onResume() {
//        super.onResume();
//        mNearbyRecyclerView.getAdapter().notifyDataSetChanged();
//        if (mMapView != null && mMapContainer.getVisibility() == View.VISIBLE) {
//            mMapView.onResume();
//        }
//    }
//
//    @Override
//    public void onPause() {
//        super.onPause();
//        if (mMapView != null && mMapContainer.getVisibility() == View.VISIBLE) mMapView.onPause();
//    }
//
//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        if (mMapView != null) mMapView.onDestroy();
//    }
//
//    @Override
//    public void onConnected(Bundle bundle) {
//        DLog.d(this, "onConnected");
//        Location lastLocation = LocationServices.FusedLocationApi.getLastLocation(
//                mGoogleApiClient);
//        Utils.setLastLocation(lastLocation);
//
//        mNearbyRecyclerView.getAdapter().notifyDataSetChanged();
//    }
//
//    @Override
//    public void onConnectionFailed(ConnectionResult connectionResult) {
//        DLog.d(this, "onConnectionFailed");
//    }
//
//    @Override
//    public void onConnectionSuspended(int i) {
//        DLog.d(this, "onConnectionSuspended");
//    }
//
//    @Override
//    public void onStart() { // this is actually the same lifecycle as its activity
//        super.onStart();
//        DLog.d(this, "onStart");
//        mGoogleApiClient.connect();
//    }
//
//    @Override
//    public void onStop() { // this is actually the same lifecycle as its activity
//        super.onStop();
//        DLog.d(this, "onStop");
//        mGoogleApiClient.disconnect();
//    }
}
