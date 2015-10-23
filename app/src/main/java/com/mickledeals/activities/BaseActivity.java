package com.mickledeals.activities;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.mickledeals.R;
import com.mickledeals.utils.DLog;
import com.mickledeals.utils.MDApiManager;
import com.mickledeals.utils.MDConnectManager;
import com.mickledeals.utils.PreferenceHelper;
import com.mickledeals.utils.Utils;

/**
 * Created by Nicky on 11/23/2014.
 */
public abstract class BaseActivity extends ActionBarActivity {

    protected static final int LAYOUT_TYPE_NORMAL = 1;
    protected static final int LAYOUT_TYPE_FULLSCREEN_SWIPE = 2;
    protected static final int LAYOUT_TYPE_DIALOG_SWIPE = 3;

    protected Toolbar mToolBar;
    protected ProgressDialog mProgressDialog;
    protected ProgressBar mProgressBar;

    private static boolean mHasCheckVersioned;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        boolean isFirstLaunch = PreferenceHelper.getPreferenceValueBoolean(this, "firstLaunch", true);
        if (isFirstLaunch && this instanceof HomeActivity) {
            //to skip home activity and do launch screen
            return;
        }

        if (savedInstanceState != null) {
            boolean isKilled = savedInstanceState.getBoolean("isKilled");
            if (isKilled && !(this instanceof HomeActivity)) {
                Utils.restartApp(this);
                //need following line, otherwise there is illegalstateexception for nearbyfragment not attached
                android.os.Process.killProcess(android.os.Process.myPid());
//                System.exit(10);
//                finish();
                return;
            }
        }

        int layoutType = getLayoutType();

        if (layoutType == LAYOUT_TYPE_NORMAL) {
            setContentView(getLayoutResource());
        } else if (layoutType == LAYOUT_TYPE_FULLSCREEN_SWIPE) {
            //this is for a simple screen with a scroll view
            setContentView(R.layout.activity_swipe_fullscreen_dismiss_base);
            getLayoutInflater().inflate(getLayoutResource(), (ViewGroup) findViewById(R.id.detailsScrollView), true);
        } else if (layoutType == LAYOUT_TYPE_DIALOG_SWIPE) {
            setContentView(R.layout.activity_swipe_dismiss_base);
            getLayoutInflater().inflate(getLayoutResource(), (ViewGroup) findViewById(R.id.baseLayout), true);
        }

        mToolBar = (Toolbar) findViewById(R.id.toolbar);
        if (mToolBar != null) {
            setSupportActionBar(mToolBar);
            mToolBar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //to enforce activity return transition
                    onBackPressed();
                }
            });
        }
        mProgressBar = (ProgressBar) findViewById(R.id.progressBarLoading);
        if (mProgressBar != null) {
            mProgressBar.getIndeterminateDrawable().setColorFilter(
                    getResources().getColor(R.color.white),
                    android.graphics.PorterDuff.Mode.SRC_IN);
            mProgressBar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //block touch event to disable button click from behind
                }
            });
        }
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

    }

    private BroadcastReceiver mNetworkReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            DLog.d(this, "receive network change");
            if (!mHasCheckVersioned && MDConnectManager.getInstance(BaseActivity.this).isNetworkAvailable()) {
                sendCheckVersionRequest();
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        if (!mHasCheckVersioned) {
            if (MDConnectManager.getInstance(this).isNetworkAvailable()) {
                sendCheckVersionRequest();
            } else {
                DLog.d(this, "register network receiver");
                registerReceiver(mNetworkReceiver, new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        //throws exception if not registered
        try {
            unregisterReceiver(mNetworkReceiver);
        } catch (Exception e) {
        }
    }

    private void sendCheckVersionRequest() {
        DLog.d(this, "sendCheckVersionRequest");
        MDApiManager.checkVersion(getString(R.string.versionNo), new MDApiManager.MDResponseListener<Boolean>() {
            @Override
            public void onMDSuccessResponse(Boolean object) {
                mHasCheckVersioned = true;
                DLog.d(this, "need update = " + object);
                if (object) {
                    AlertDialog dialog = new AlertDialog.Builder(BaseActivity.this, R.style.AppCompatAlertDialogStyle)
                            .setMessage(R.string.force_upgrade)
                            .setCancelable(false)
                            .setPositiveButton(R.string.play_store_update, null)
                            .create();
                    dialog.show();
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            final String appPackageName = getPackageName();
                            DLog.d(this, "appPackageName = " + appPackageName);
                            try {
                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                            } catch (android.content.ActivityNotFoundException anfe) {
                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                            }
                        }
                    });
                }
            }

            @Override
            public void onMDNetworkErrorResponse(String errorMessage) {
            }

            @Override
            public void onMDErrorResponse(String errorMessage) {
            }
        });
    }

    protected abstract int getLayoutResource();

    protected int getLayoutType() {
        return LAYOUT_TYPE_NORMAL;
    }

    public Toolbar getToolBar() {
        return mToolBar;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //KEY THING TO SOLVE WEIRD ISSUE AFTER APP KILLED BY SYSTEM
        //Seems like fragment by default use this method to retain some state that causes weird behavior
//        super.onSaveInstanceState(outState);
        outState.putBoolean("isKilled", true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    protected class MDReponseListenerImpl<T> implements MDApiManager.MDResponseListener<T> {
        @Override
        public void onMDSuccessResponse(T object) {
            if (mProgressDialog != null) mProgressDialog.dismiss();
            if (mProgressBar != null) mProgressBar.setVisibility(View.GONE);
        }

        @Override
        public void onMDNetworkErrorResponse(String errorMessage) {
            Utils.showNetworkErrorDialog(BaseActivity.this);
            if (mProgressDialog != null) mProgressDialog.dismiss();
            if (mProgressBar != null) mProgressBar.setVisibility(View.GONE);
        }

        @Override
        public void onMDErrorResponse(String errorMessage) {
            int errorMessageRes = R.string.response_error_message_unknown;
            Utils.showAlertDialog(BaseActivity.this, R.string.response_error_title, errorMessageRes);
            if (mProgressDialog != null) mProgressDialog.dismiss();
            if (mProgressBar != null) mProgressBar.setVisibility(View.GONE);
        }

        public void onMDErrorResponse(int errorMessageRes) {
            Utils.showAlertDialog(BaseActivity.this, R.string.response_error_title, errorMessageRes);
            if (mProgressDialog != null) mProgressDialog.dismiss();
            if (mProgressBar != null) mProgressBar.setVisibility(View.GONE);
        }
    }
}
