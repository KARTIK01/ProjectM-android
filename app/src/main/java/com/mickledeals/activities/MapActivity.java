package com.mickledeals.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.mickledeals.R;
import com.mickledeals.tests.TestDataHolder;
import com.mickledeals.utils.Utils;

/**
 * Created by Nicky on 4/18/2015.
 */
public class MapActivity extends BaseActivity implements OnMapReadyCallback {

    private SupportMapFragment mMapFragment;
    private static final int DIRECTIONS_MENU_ID = 0;
    private static final int SHOW_GOOGLE_MAP_MENU_ID = 1;
    private TestDataHolder mData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null && savedInstanceState.getBoolean("isKilled")) return;

        mMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mMapFragment.getMapAsync(this);

        mData = (TestDataHolder) getIntent().getSerializableExtra("dataObject");
        setTitle(mData.getStoreName());
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_map;
    }


    @Override
    public void onMapReady(GoogleMap map) {
        LatLng latLng = Utils.getLatLngFromDataHolder(mData);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
        map.setMyLocationEnabled(true);
        map.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

            public View getInfoWindow(Marker marker) {
                View view = MapActivity.this.getLayoutInflater().inflate(R.layout.pin_overlay, null);
                TextView title = (TextView) view.findViewById(R.id.pin_item_title);
                TextView snippet = (TextView) view.findViewById(R.id.pin_item_snippet);
                title.setText(marker.getTitle());
                snippet.setText(marker.getSnippet());
                view.findViewById(R.id.arrow).setVisibility(View.GONE);
                return view;
            }

            public View getInfoContents(Marker marker) {
                // TODO Auto-generated method stub
                return null;
            }
        });

        final Marker marker = map.addMarker(new MarkerOptions()
                .position(latLng)
                .title(mData.getStoreName())
                .snippet(mData.mAddress)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.md_pin)));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, 1, 0, R.string.directions)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        menu.add(0, 2, 0, R.string.open_in_google_maps)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == 1) {
            Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                    Uri.parse("http://maps.google.com/maps?daddr=" + mData.mLatLng.replace(" ", "")));
            startActivity(intent);
        } else {
            Uri gmmIntentUri = Uri.parse("geo:" + mData.mLatLng.replace(" ", "") + "?q=" + mData.mAddress.replace(" ", "+") + "&z=16");
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");
            startActivity(mapIntent);
        }

        return super.onOptionsItemSelected(item);
    }
}
