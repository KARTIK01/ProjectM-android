package com.mickledeals.activities;


import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;

import com.google.android.gms.common.api.GoogleApiClient;
import com.mickledeals.R;
import com.mickledeals.bean.NavMenuItem;
import com.mickledeals.fragments.BaseFragment;
import com.mickledeals.fragments.NavigationDrawerFragment;
import com.mickledeals.utils.DLog;
import com.mickledeals.utils.MDLoginManager;
import com.mickledeals.utils.PreferenceHelper;


public class HomeActivity extends BaseActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;
    private View mToolBarLogo;
    private View mSlidingTab;
    private View mToolBarExtraLayout;
    private CharSequence mTitle;

    private int mCurrentPosition = -1;

    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        boolean isFirstLaunch = PreferenceHelper.getPreferenceValueBoolean(this, "firstLaunch", true);
        if (isFirstLaunch) {
            Intent i = new Intent(this, LaunchScreen.class);
            startActivity(i);
            finish();
            return;
        }

        final View launchBg = findViewById(R.id.launchBg);
        if (getIntent().getBooleanExtra("fromIntroScreen", false)) {
            launchBg.setVisibility(View.GONE);
        } else {
            final View fullLogo = findViewById(R.id.fullLogoHome);
            fullLogo.setVisibility(View.VISIBLE);
            AlphaAnimation anim = new AlphaAnimation(1, 0);
            anim.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    launchBg.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            });
            anim.setDuration(500);
            anim.setStartOffset(1000);
            launchBg.startAnimation(anim);
        }

        mToolBarLogo = mToolBar.findViewById(R.id.toolbar_logo);
        mSlidingTab = mToolBar.findViewById(R.id.sliding_tabs);
        mToolBarExtraLayout = mToolBar.findViewById(R.id.toolbar_extra_layout);

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
                for (int i = 0; i < count; i++) {
                    String name = getSupportFragmentManager().getBackStackEntryAt(i).getName();
//                    Log.e("ZZZ", "name = " + name);
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
                if (mNavigationDrawerFragment != null)
                    mNavigationDrawerFragment.resetMenuRowBg(mCurrentPosition);

                Fragment newFragment = getSupportFragmentManager().findFragmentByTag(mCurrentPosition + "");
                ((BaseFragment) newFragment).onFragmentResume();
            }
        });


//        String saveListStr = PreferenceHelper.getPreferenceValueStr(this, "saveList", "");
//        if (!saveListStr.equals("")) {
//            String[] tokens = saveListStr.split("\\|");
//            for (String token : tokens) {
//                if (token.length() != 0) {
//                    int id = Integer.parseInt(token);
//                    CouponInfo data = DataListModel.getInstance().getDataList().get(id);
//                    data.mSaved = true;
//                }
//            }
//        }
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_main;
    }

    @Override
    public void onDrawerSliding(float slideOffSet) {
        if (mCurrentPosition == 0) {
            mToolBarLogo.setAlpha(slideOffSet);
            mSlidingTab.setAlpha((1 - slideOffSet * 1.5f));
        }
    }

    @Override
    public void onDrawerClosed() {
        resetTitle();
    }

    @Override
    public void onDrawerOpen() {
        if (mToolBar != null) {
            if (mCurrentPosition != 0) {
                mSlidingTab.setVisibility(View.GONE);
            }
            mToolBarExtraLayout.setVisibility(View.VISIBLE);
        }
    }

    private void resetTitle() {
        if (mToolBar != null) {
            mSlidingTab.setVisibility(View.VISIBLE);
            if (mCurrentPosition == 0) {
                mToolBarExtraLayout.setVisibility(View.VISIBLE);
                mSlidingTab.setVisibility(View.VISIBLE);
                mToolBarLogo.setAlpha(0f);
                mSlidingTab.setAlpha(1f);
            } else {
                mToolBarExtraLayout.setVisibility(View.GONE);
                mToolBar.setTitle(mTitle);
            }
        }
    }

    @Override
    public void onNavigationDrawerItemSelected(final int position) {

        DLog.d(this, "onNavigationDrawerItemSelected pos = " + position);
        final NavMenuItem item = MDApplication.sNavMenuList.get(position);

        if (item.needsLogin()) {

            MDLoginManager.loginIfNecessary(this, new MDLoginManager.LoginCallback() {
                @Override
                public void onLoginSuccess() {
                    launchFragmentOrActivity(item, position);
                }
            });
        } else {
            launchFragmentOrActivity(item, position);
        }


    }

    private void launchFragmentOrActivity(NavMenuItem item, int position) {
        Class navClass = item.getNavClass();
        if (navClass == null) return;
        if (Activity.class.isAssignableFrom(navClass)) {
            Intent i = new Intent(this, navClass);
            if (mNavigationDrawerFragment != null) {
                mNavigationDrawerFragment.setPendingActivityIntent(i);
            }
        } else if (Fragment.class.isAssignableFrom(navClass)) {
            if (mCurrentPosition == position) return;
            if (mCurrentPosition != -1) {
                Fragment oldFragment = getSupportFragmentManager().findFragmentByTag(mCurrentPosition + "");
                ((BaseFragment) oldFragment).onFragmentPause();
            }
            mTitle = getString(item.getTitleRes());

            for (int i = 0; i < getSupportFragmentManager().getBackStackEntryCount(); i++) {
                String name = getSupportFragmentManager().getBackStackEntryAt(i).getName();
                if (name != null && name.split("\\|")[0].equals(position + "")) {
                    int id = getSupportFragmentManager().getBackStackEntryAt(i).getId();
                    getSupportFragmentManager().popBackStack(id, FragmentManager.POP_BACK_STACK_INCLUSIVE);
//                    mCurrentPosition = position;
//                    resetTitle();
                    return;
                }
            }

            try {
                Fragment fragment = (Fragment) item.getNavClass().newInstance();

                if (position == 0) {
                    if (mCurrentPosition == -1) {
                        // cannot add home fragment to backstack, otherwise hitting back would display an empty screen
                        getSupportFragmentManager().beginTransaction()
                                .add(R.id.container, fragment, position + "")
                                .commit();
                    }
                } else {
                    getSupportFragmentManager().beginTransaction()
                            .add(R.id.container, fragment, position + "")
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

        restoreActionBar();
        return true;
//        }
//        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_search) {
            Intent i = new Intent(this, SearchActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(i);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (mNavigationDrawerFragment.isDrawerOpen()) {
            mNavigationDrawerFragment.closeDrawer();
            return;
        }

        //this is for handling back button to hide map
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(mCurrentPosition + "");
        if (!((BaseFragment) fragment).handleBackPressed()) { //if handleBackPressed not consume
            super.onBackPressed();
        }
    }
}
