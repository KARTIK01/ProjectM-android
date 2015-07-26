package com.mickledeals.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;

import com.mickledeals.activities.BaseActivity;

/**
 * Created by Nicky on 11/28/2014.
 */
public abstract class BaseFragment extends Fragment{

    protected Context mContext;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
    }


    public Toolbar getToolBar() {
        return ((BaseActivity) getActivity()).getToolBar();
    }

    public boolean handleBackPressed() {
        return false;
    }

    public void onFragmentPause() {

    }

    public void onFragmentResume() {

    }



}
