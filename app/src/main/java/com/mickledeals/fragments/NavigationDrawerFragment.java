package com.mickledeals.fragments;


import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mickledeals.R;
import com.mickledeals.bean.NavMenuItem;
import com.mickledeals.utils.DLog;
import com.mickledeals.utils.Utils;

/**
 * Fragment used for managing interactions for and presentation of a navigation drawer.
 * See the <a href="https://developer.android.com/design/patterns/navigation-drawer.html#Interaction">
 * design guidelines</a> for a complete explanation of the behaviors implemented here.
 */
public class NavigationDrawerFragment extends BaseFragment {

    /**
     * Remember the position of the selected item.
     */
    private static final String STATE_SELECTED_POSITION = "selected_navigation_drawer_position";

    /**
     * Per the design guidelines, you should show the drawer on launch until the user manually
     * expands it. This shared preference tracks this.
     */
    private static final String PREF_USER_LEARNED_DRAWER = "navigation_drawer_learned";

    /**
     * A pointer to the current callbacks instance (the Activity).
     */
    private NavigationDrawerCallbacks mCallbacks;

    /**
     * Helper component that ties the action bar to the navigation drawer.
     */
    private ActionBarDrawerToggle mDrawerToggle;

    private DrawerLayout mDrawerLayout;
    private View mFragmentContainerView;
    private Toolbar mToolbar;

    private boolean mDrawerOpened;
    private Intent mPendingActivityIntent;

    //    private int mCurrentSelectedPosition = -1;
    private boolean mFromSavedInstanceState;


    private LinearLayout mMenuContainer;
    private TextView mLoginOutBtn;

    public NavigationDrawerFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DLog.d(this, "onCreate");

        if (savedInstanceState != null) {
//            mCurrentSelectedPosition = savedInstanceState.getInt(STATE_SELECTED_POSITION);
            mFromSavedInstanceState = true;
        }

        // Select either the default item (0) or the last selected item.
        selectItem(0);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Indicate that this fragment would like to influence the set of actions in the action bar.
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View scrollView = inflater.inflate(
                R.layout.fragment_navigation_drawer, container, false);

        return scrollView;
    }

    private void inflateMenuViews(LayoutInflater inflater) {
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mMenuContainer = (LinearLayout) view.findViewById(R.id.menuContainer);
        for (int i = 0; i < Utils.sNavMenuList.size(); i++) {
            final int pos = i;
            NavMenuItem item = Utils.sNavMenuList.get(i);
            if (item.getTitleRes() == 0) {
                View divider = new View(mContext);
                divider.setBackgroundColor(getResources().getColor(R.color.light_divider));
                mMenuContainer.addView(divider,
                        new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, Utils.getPixelsFromDip(8, getResources())));
                continue;
            }
            View menuRow = LayoutInflater.from(mContext).inflate(R.layout.nav_menu_row, mMenuContainer, false);
            if (i == 0) menuRow.setSelected(true);
            ImageView menuIcon = (ImageView) menuRow.findViewById(R.id.navMenuIcon);
            TextView menuText = (TextView) menuRow.findViewById(R.id.navMenuText);
            int iconRes = item.getIconRes();
            if (iconRes != 0) {
                menuIcon.setImageResource(item.getIconRes());
                menuIcon.setVisibility(View.VISIBLE);
            }
            menuText.setText(item.getTitleRes());
            menuRow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectItem(pos);
                }
            });
            mMenuContainer.addView(menuRow);
        }
    }

    public boolean isDrawerOpen() {
        return mDrawerLayout != null && mDrawerLayout.isDrawerOpen(mFragmentContainerView);
    }

    public void resetMenuRowBg(int selectedPosition) {
        if (mMenuContainer != null) {
            for (int i = 0; i < mMenuContainer.getChildCount(); i++) {
                View view = mMenuContainer.getChildAt(i);
                if (i == selectedPosition) view.setSelected(true);
                else view.setSelected(false);
            }
        }
    }

    /**
     * Users of this fragment must call this method to set up the navigation drawer interactions.
     *
     * @param fragmentId   The android:id of this fragment in its activity's layout.
     * @param drawerLayout The DrawerLayout containing this fragment's UI.
     */
    public void setUp(int fragmentId, DrawerLayout drawerLayout, Toolbar toolbar) {
        mFragmentContainerView = getActivity().findViewById(fragmentId);
        mDrawerLayout = drawerLayout;
        mToolbar = toolbar;

        // set a custom shadow that overlays the main content when the drawer opens
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        // set up the drawer's list view with items and click listener

//        ActionBar actionBar = getSupportActionBar();
//        actionBar.setDisplayHomeAsUpEnabled(true);
//        actionBar.setHomeButtonEnabled(true);

        // ActionBarDrawerToggle ties together the the proper interactions
        // between the navigation drawer and the action bar app icon.
        mDrawerToggle = new ActionBarDrawerToggle(
                getActivity(),                    /* host Activity */
                mDrawerLayout,                    /* DrawerLayout object */
                mToolbar,             /* nav drawer image to replace 'Up' caret */
                R.string.navigation_drawer_open,  /* "open drawer" description for accessibility */
                R.string.navigation_drawer_close  /* "close drawer" description for accessibility */
        ) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
//                mDisableAnim = false;
//                Log.e("ZZZ", "onDrawerClosed");
//                if (!isAdded()) {
//                    return;
//                }
//                if (mCallbacks != null) {
//                    mCallbacks.onDrawerClosed();
//                }
//                if (mToolbar != null) {
//                    if (mToolbar.getTitle().equals(getString(R.string.app_name))) {
//                        mToolbar.setTitle(mPreviousTitle);
//                    mToolbar.findViewById(R.id.sliding_tabs).setVisibility(View.VISIBLE);
//                    }
//                }
//                getActivity().invalidateOptionsMenu(); // calls onPrepareOptionsMenu()
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
//                mDisableAnim = false;
//                Log.e("ZZZ", "onDrawerOpened");
//                if (!isAdded()) {
//                    return;
//                }
//                if (mCallbacks != null) {
//                    mCallbacks.onDrawerOpen();
//                }
//                if (mToolbar != null) {
//                    mPreviousTitle = mToolbar.getTitle();
//                    mToolbar.setTitle(R.string.app_name);
//                    mToolbar.findViewById(R.id.sliding_tabs).setVisibility(View.GONE);
//                }
//                getActivity().invalidateOptionsMenu(); // calls onPrepareOptionsMenu()
            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                if (slideOffset > 0.2 && !mDrawerOpened) {
                    mDrawerOpened = true;
                    if (mCallbacks != null) {
                        mCallbacks.onDrawerOpen();
                    }
                } else if (slideOffset < 0.2 && mDrawerOpened) {
                    mDrawerOpened = false;
                    if (mCallbacks != null) {
                        mCallbacks.onDrawerClosed();
                    }
                    if (mPendingActivityIntent != null) {
                        startActivity(mPendingActivityIntent);
                        mPendingActivityIntent = null;
                    }
                }

                super.onDrawerSlide(drawerView, slideOffset);
            }

            @Override
            public void onDrawerStateChanged(int newState) {
                super.onDrawerStateChanged(newState);
            }
        };

        // Defer code dependent on restoration of previous instance state.
        mDrawerLayout.post(new Runnable() {
               @Override
               public void run() {
                   mDrawerToggle.syncState();
               }
           }
        );

        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerToggle.setToolbarNavigationClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //so far do nothing
            }
        });
    }

    public void setPendingActivityIntent(Intent intent) {
        mPendingActivityIntent = intent;
    }

    private void selectItem(int position) {
        if (mDrawerLayout != null) {
            mDrawerLayout.closeDrawer(mFragmentContainerView);
        }
        if (mCallbacks != null) {
            mCallbacks.onNavigationDrawerItemSelected(position);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallbacks = (NavigationDrawerCallbacks) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement NavigationDrawerCallbacks.");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
//        outState.putInt(STATE_SELECTED_POSITION, mCurrentSelectedPosition);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Forward the new configuration the drawer toggle component.
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // If the drawer is open, show the global app actions in the action bar. See also
        // showGlobalContextActionBar, which controls the top-left area of the action bar.
        if (mDrawerLayout != null && isDrawerOpen()) {
            inflater.inflate(R.menu.global, menu);
            showGlobalContextActionBar();
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Per the navigation drawer design guidelines, updates the action bar to show the global app
     * 'context', rather than just what's in the current screen.
     */
    private void showGlobalContextActionBar() {
//        ActionBar actionBar = getSupportActionBar();
//        actionBar.setDisplayShowTitleEnabled(true);
//        actionBar.setTitle(R.string.app_name);
        if (mToolbar != null) {
//            mToolbar.setTitle(R.string.app_name);
        }
    }

    private ActionBar getSupportActionBar() {
        return ((ActionBarActivity) getActivity()).getSupportActionBar();
    }

    /**
     * Callbacks interface that all activities using this fragment must implement.
     */
    public static interface NavigationDrawerCallbacks {
        /**
         * Called when an item in the navigation drawer is selected.
         */
        void onNavigationDrawerItemSelected(int position);

        void onDrawerClosed();

        void onDrawerOpen();
    }
}
