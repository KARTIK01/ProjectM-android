package com.mickledeals.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mickledeals.activities.BaseActivity;

/**
 * Created by Nicky on 11/28/2014.
 */
public abstract class BaseFragment extends Fragment{

    protected Context mContext;
    private int mTitleRes;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    public Toolbar getToolBar() {
        return ((BaseActivity)getActivity()).getToolBar();
    }

    public void setTitleRes(int titleRes) {
        mTitleRes = titleRes;
    }

    @Override
    public void onStart() {
        super.onStart();
//        if (mTitleRes != 0) {
//            Toolbar toolbar = getToolBar();
//            toolbar.setTitle(mTitleRes);
//            toolbar.findViewById(R.id.sliding_tabs).setVisibility(View.GONE);
//        }
    }
}
