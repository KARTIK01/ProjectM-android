package com.mickledeals.activities;


import android.app.Activity;
import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.mickledeals.R;
import com.mickledeals.bean.NavMenuItem;
import com.mickledeals.datamodel.DataListModel;
import com.mickledeals.fragments.NavigationDrawerFragment;
import com.mickledeals.tests.TestDataHolder;
import com.mickledeals.utils.DLog;
import com.mickledeals.utils.PreferenceHelper;
import com.mickledeals.utils.Utils;


public class HomeActivity extends BaseActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    private CharSequence mTitle;

    private int mCurrentPosition = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int language = PreferenceHelper.getPreferenceValueInt(this, "language", -1);
        Utils.setLocaleWithLang(language, getBaseContext());

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout), mToolBar);

        getSupportFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                boolean isHome = false;
                int count = getSupportFragmentManager().getBackStackEntryCount();
                for (int i = 0 ; i < count; i++) {
                    String name = getSupportFragmentManager().getBackStackEntryAt(i).getName();
                    Log.e("ZZZ", "name = " + name);
                }
                if (count == 0) isHome = true;

                if (!isHome) {
                    String name = getSupportFragmentManager().getBackStackEntryAt(count - 1).getName();
                    if (name.equals("map")) { //if fragment is map, look for previous stack title
                        if (count == 1) { //if map is the only fragment, we know it is home
                            isHome = true;
                        } else {
                            name = getSupportFragmentManager().getBackStackEntryAt(count - 2).getName();
                        }
                    }
                    if (!isHome) { //if it is home, we dont even have backstack, so there is no name
                        String[] tokens = name.split("\\|");
                        mCurrentPosition = Integer.parseInt(tokens[1]);
                        mTitle = tokens[2];
                    }
                }

                if (isHome) {
                    mCurrentPosition = 0;
                    mTitle = getString(R.string.menu_home);
                }
                resetTitle();
                if (mNavigationDrawerFragment != null) mNavigationDrawerFragment.resetMenuRowBg(mCurrentPosition);
            }
        });


        String saveListStr = PreferenceHelper.getPreferenceValueStr(this, "saveList", "");
        if (!saveListStr.equals("")) {
            String[] tokens = saveListStr.split("\\|");
            for (String token : tokens) {
                if (token.length() != 0) {
                    int id = Integer.parseInt(token);
                    TestDataHolder data = DataListModel.getInstance().getDataList().get(id);
                    data.mSaved = true;
                }
            }
        }
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_main;
    }

    @Override
    public void onDrawerClosed() {
        resetTitle();
    }

    @Override
    public void onDrawerOpen() {
        if (mToolBar != null) {
            mToolBar.findViewById(R.id.sliding_tabs).setVisibility(View.GONE);
            mToolBar.setTitle(getString(R.string.app_name));
        }
    }

    private void resetTitle() {
        if (mToolBar != null) {
            if (mCurrentPosition == 0) {
                mToolBar.findViewById(R.id.sliding_tabs).setVisibility(View.VISIBLE);
            } else {
                mToolBar.setTitle(mTitle);
            }
        }
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {

        DLog.d(this, "onNavigationDrawerItemSelected pos = " + position);
        NavMenuItem item = Utils.sNavMenuList.get(position);


        Class navClass = item.getNavClass();
        if (navClass == null) return;
        if (Activity.class.isAssignableFrom(navClass)) {
            Intent i = new Intent(this, navClass);
            if (mNavigationDrawerFragment != null) {
                mNavigationDrawerFragment.setPendingActivityIntent(i);
            }
        } else if (Fragment.class.isAssignableFrom(navClass)) {
            if (mCurrentPosition == position) return;
            mTitle = getString(item.getTitleRes());
            //onBack stack changed should already handled
//            if (mNavigationDrawerFragment != null) mNavigationDrawerFragment.resetMenuRowBg(position);

            for (int i = 0; i < getSupportFragmentManager().getBackStackEntryCount() ; i++) {
                String  name = getSupportFragmentManager().getBackStackEntryAt(i).getName();
                if (name != null && name.split("\\|")[0].equals(position + "")) {
                    int id = getSupportFragmentManager().getBackStackEntryAt(i).getId();
                    getSupportFragmentManager().popBackStack(id, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    mCurrentPosition = position;
                    resetTitle();
                    return;
                }
            }

            try {
                Fragment fragment = (Fragment) item.getNavClass().newInstance();
                if (position == 0) {
                    if (mCurrentPosition == -1) {
                        // cannot add home fragment to backstack, otherwise hitting back would display an empty screen
                        getSupportFragmentManager().beginTransaction()
                                .add(R.id.container, fragment, mTitle.toString())
                                .commit();
                    }
                } else {
                    getSupportFragmentManager().beginTransaction()
                            .add(R.id.container, fragment, mTitle.toString())
                            .addToBackStack(mCurrentPosition + "|" + position + "|" + mTitle) //format is oldPosition|newPosition|newTitle
                            .commit();
                }

            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }

            mCurrentPosition = position;
            resetTitle();
        }


    }

    public void restoreActionBar() {
//        ActionBar actionBar = getSupportActionBar();
//        actionBar.setDisplayShowTitleEnabled(true);
//        actionBar.setTitle(mTitle);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        DLog.d(this, "onCreateOptionsMenu");
//        if (!mNavigationDrawerFragment.isDrawerOpen()) {
        // Only show items in the action bar relevant to this screen
        // if the drawer is not showing. Otherwise, let the drawer
        // decide what to show in the action bar.
        getMenuInflater().inflate(R.menu.main, menu);

        // Get the SearchView and set the searchable configuration
        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        // Assumes current activity is the searchable activity
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(true);

        restoreActionBar();
        return true;
//        }
//        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_search) {
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }
}
