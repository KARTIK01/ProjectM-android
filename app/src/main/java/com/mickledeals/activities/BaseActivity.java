package com.mickledeals.activities;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import com.mickledeals.R;

/**
 * Created by Nicky on 11/23/2014.
 */
public abstract class BaseActivity extends ActionBarActivity{

    protected Toolbar mToolBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            boolean isKilled = savedInstanceState.getBoolean("isKilled");
            if (isKilled && !(this instanceof HomeActivity)) {
                Intent i = new Intent(this, HomeActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
//                android.os.Process.killProcess(android.os.Process.myPid());
//                System.exit(10);
//                finish();
                return;
            }
        }


        setContentView(getLayoutResource());
        mToolBar = (Toolbar) findViewById(R.id.toolbar);
        if (mToolBar != null) {
            setSupportActionBar(mToolBar);
//            mToolBar.setNavigationOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    finish();
//                }
//            });
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    protected abstract int getLayoutResource();

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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e("ZZZ", "destroy" + this.getClass().getSimpleName());
    }
}
