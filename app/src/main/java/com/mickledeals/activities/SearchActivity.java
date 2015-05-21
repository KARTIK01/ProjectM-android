package com.mickledeals.activities;

import android.app.SearchManager;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.widget.AutoCompleteTextView;

import com.mickledeals.R;

/**
 * Created by Nicky on 5/20/2015.
 */
public class SearchActivity extends SwipeDismissActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.e("ZZZ", "onCreate Search activity");
        super.onCreate(savedInstanceState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search, menu);
        // Get the SearchView and set the searchable configuration
        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        // Assumes current activity is the searchable activity
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false);
        searchView.setQueryRefinementEnabled(true);
        searchView.setQueryHint(getString(R.string.search_hint));
        searchView.requestFocus();
        AutoCompleteTextView search_text = (AutoCompleteTextView) searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
        search_text.setTextSize(16);
        return true;
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_search;
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, 0);
    }
}
