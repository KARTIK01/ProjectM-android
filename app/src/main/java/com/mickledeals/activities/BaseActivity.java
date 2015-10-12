package com.mickledeals.activities;

import android.app.ProgressDialog;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.mickledeals.R;
import com.mickledeals.utils.MDApiManager;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        boolean isFirstLaunch = PreferenceHelper.getPreferenceValueBoolean(this, "firstLaunch", true);
        if (isFirstLaunch) {
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
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
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
        }

        @Override
        public void onMDNetworkErrorResponse(String errorMessage) {
            Utils.showNetworkErrorDialog(BaseActivity.this);
            if (mProgressDialog != null) mProgressDialog.dismiss();
        }

        @Override
        public void onMDErrorResponse(String errorMessage) {
            int errorMessageRes = R.string.response_error_message_unknown;
            Utils.showAlertDialog(BaseActivity.this, R.string.response_error_title, errorMessageRes);
            if (mProgressDialog != null) mProgressDialog.dismiss();
        }

        public void onMDErrorResponse(int errorMessageRes) {
            Utils.showAlertDialog(BaseActivity.this, R.string.response_error_title, errorMessageRes);
            if (mProgressDialog != null) mProgressDialog.dismiss();
        }
    }
}
